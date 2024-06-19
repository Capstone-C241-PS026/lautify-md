package com.lautify.app.fragment

import PredictApiClient
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.lautify.app.R
import com.lautify.app.ResultActivity
import com.lautify.app.databinding.FragmentCameraBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONStringer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Permission request granted", Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Permission request denied", Toast.LENGTH_LONG)
                    .show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener {
            currentImageUri?.let {
                analyzeImage(it)
            } ?: run {
                showToast(getString(R.string.empty_image_warning))
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(requireContext())
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage(uri: Uri) {
        // Show the progress bar
        binding.progressIndicator.visibility = View.VISIBLE

        val file = uriToFile(uri, requireContext())
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("predict", file.name, requestFile)

        val call = PredictApiClient.instance.uploadImage(body)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                // Hide the progress bar
                binding.progressIndicator.visibility = View.GONE

                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (responseBody != null) {
                        try {
                            val json = JSONObject(responseBody)
                            val status = json.getString("status")
                            val data = json.optJSONObject("data") ?: JSONObject()
                            val imageUrl = data.optString("image", "")
                            val predictions = data.optJSONArray("predictions") ?: JSONArray()
                            val freshnessList = mutableListOf<String>()
                            val message = json.optJSONArray("message")?.optString(0, "Unknown error") ?: "Unknown error"

                            if (status == "error") {
                                Log.d("eye_detect", message)
                                Toast.makeText(requireContext(), "Mata ikan tidak terdeteksi", Toast.LENGTH_SHORT).show()
                            } else {
                                var freshCount = 0
                                var notFreshCount = 0

                                for (i in 0 until predictions.length()) {
                                    val freshness = predictions.optJSONObject(i)?.optString("freshness", "Unknown freshness")
                                    if (freshness == "not fresh") {
                                        notFreshCount++
                                    } else {
                                        freshCount++
                                    }
                                }

                                val freshnessDescription = "Dari hasil analisis gambar, terdapat $freshCount ikan yang segar dan $notFreshCount ikan yang tidak segar. Pastikan untuk memeriksa kondisi ikan secara fisik untuk memastikan kesegarannya sebelum dikonsumsi. 👍🏻"

                                // Start ResultActivity and pass the data
                                val intent = Intent(requireContext(), ResultActivity::class.java).apply {
                                    putExtra("imageUrl", imageUrl)
                                    putExtra("freshness", freshnessDescription)
                                }
                                startActivity(intent)
                            }
                        } catch (e: Exception) {
                            Log.e("JSON Parsing Error", e.message ?: "Unknown error")
                            Toast.makeText(requireContext(), "Error parsing response", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.d("Upload", "Empty response body")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.d("Upload", "Failed with response code: ${response.code()}, error body: $errorBody")
                    Toast.makeText(requireContext(), "Upload failed: $errorBody", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Hide the progress bar
                binding.progressIndicator.visibility = View.GONE

                Log.e("Upload error:", t.message ?: "Unknown error")
                Toast.makeText(requireContext(), "Upload failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun uriToFile(uri: Uri, context: Context): File {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp_image.jpg")
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val outputStream: FileOutputStream = FileOutputStream(file)
        inputStream?.use { input ->
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
        }
        return file
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
