package com.lautify.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.lautify.app.api.ApiClient
import com.lautify.app.api.ApiService
import com.lautify.app.api.response.ErrorResponse
import com.lautify.app.api.response.ErrorResponses
import com.lautify.app.api.response.LoginData
import com.lautify.app.api.response.LoginResponse
import com.lautify.app.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var apiService: ApiService
    private lateinit var preferenceHelper: UserPreferece
    private lateinit var regis: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = ApiClient.getInstance()
        preferenceHelper = UserPreferece(this)
        regis = findViewById(R.id.to_register)

        showLoading(false)
        setupAction()

        regis.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val email = binding.loginEmailText.text.toString()
            val password = binding.loginPasswordCustom.text.toString()

            if (email.isEmpty()) {
                binding.loginEmailLayout.error = getString(R.string.fill_email)
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.loginPasswordLayout.error = getString(R.string.fill_password)
                return@setOnClickListener
            }

            login(email, password)
        }
    }

    private fun login(email: String, password: String) {
        val request = LoginResponse(email, password)
        showLoading(true)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.Login(request)
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    if (response.isSuccessful) {
                        val loginResponseBody = response.body()
                        if (loginResponseBody != null && !loginResponseBody.error) {
                            if (loginResponseBody.data != null) {
                                saveUserData(loginResponseBody.data)
                                preferenceHelper.saveLoginStatus(true)
                                showToast("Login successful.")
                                navigateToMainActivity()
                            } else {
                                showToast("Login failed: No data found")
                                Log.d("LoginActivity", "No data found in response")
                            }
                        } else {
                            showToast("Login failed: ${loginResponseBody?.message ?: "Unknown error"}")
                            Log.d("LoginActivity", "Response body is null or error: ${loginResponseBody?.message}")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorResponse = Gson().fromJson(errorBody, ErrorResponses::class.java)
                        showToast(errorResponse.message)
                        Log.d("LoginActivity", "Error response: $errorBody")
                    }
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    showToast(errorResponse.message)
                    Log.d("LoginActivity", "HTTP Exception: $errorBody")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    showToast("Error: ${e.message}")
                    Log.d("LoginActivity", "Exception: ${e.message}")
                }
            }
        }
    }

    private fun saveUserData(data: LoginData?) {
        if (data != null) {
            preferenceHelper.saveUserId(data.uid)
            preferenceHelper.saveUserToken(data.token)
            preferenceHelper.saveUsername(data.username)
            preferenceHelper.saveEmail(data.email)
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressLogin.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
