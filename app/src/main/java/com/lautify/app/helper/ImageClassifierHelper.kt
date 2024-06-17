package com.lautify.app.helper

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.lautify.app.ml.ModelPrediction
import okio.IOException
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ImageClassifierHelper(
    private val context: Context,
    private val classifierListener: ClassifierListener
) {

    private lateinit var labels: List<String>
    private lateinit var model: ModelPrediction

    init {
        try {
            // Load the model
            model = loadModel()
            // Load the labels
            labels = loadLabels("labels.txt")
            Log.d("ImageClassifierHelper", "Labels loaded: $labels")
        } catch (e: IOException) {
            classifierListener.onError("Error initializing labels: ${e.message}")
        } catch (e: Exception) {
            classifierListener.onError("Error loading model: ${e.message}")
        }
    }

    @Throws(IOException::class)
    private fun loadLabels(labelPath: String): List<String> {
        return context.assets.open(labelPath).bufferedReader().useLines { it.toList() }
    }

    @Throws(Exception::class)
    private fun loadModel(): ModelPrediction {
        return ModelPrediction.newInstance(context)
    }

    fun classify(image: Bitmap) {
        try {
            // Preprocess the image
            val resizedImage = Bitmap.createScaledBitmap(image, INPUT_IMAGE_WIDTH, INPUT_IMAGE_HEIGHT, true)
            val byteBuffer = convertBitmapToByteBuffer(resizedImage)

            // Creates inputs for reference
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, INPUT_IMAGE_WIDTH, INPUT_IMAGE_HEIGHT, 3), DataType.FLOAT32)
            inputFeature0.loadBuffer(byteBuffer)

            // Runs model inference and gets result
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            // Postprocess the output
            val labeledProbability = TensorLabel(labels, outputFeature0).mapWithFloatValue
            val result = getTopResult(labeledProbability)

            // Notify listener with the results
            classifierListener.onResults(result)

        } catch (e: Exception) {
            classifierListener.onError("Error during classification: ${e.message}")
        }
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * INPUT_IMAGE_WIDTH * INPUT_IMAGE_HEIGHT * 3)
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(INPUT_IMAGE_WIDTH * INPUT_IMAGE_HEIGHT)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        var pixel = 0
        for (i in 0 until INPUT_IMAGE_WIDTH) {
            for (j in 0 until INPUT_IMAGE_HEIGHT) {
                val value = intValues[pixel++]
                byteBuffer.putFloat(((value shr 16 and 0xFF) - 128) / 128.0f)
                byteBuffer.putFloat(((value shr 8 and 0xFF) - 128) / 128.0f)
                byteBuffer.putFloat(((value and 0xFF) - 128) / 128.0f)
            }
        }
        return byteBuffer
    }

    private fun getTopResult(labelProb: Map<String, Float>): String {
        var maxLabel = ""
        var maxProb = 0.0f

        for ((key, value) in labelProb) {
            if (value > maxProb) {
                maxLabel = key
                maxProb = value
            }
        }

        return "$maxLabel: ${maxProb * 100}%"
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(result: String)
    }

    companion object {
        private const val INPUT_IMAGE_WIDTH = 128
        private const val INPUT_IMAGE_HEIGHT = 128
    }
}
