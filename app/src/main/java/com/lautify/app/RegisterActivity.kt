package com.lautify.app

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.lautify.app.api.ApiClient
import com.lautify.app.api.ApiService
import com.lautify.app.api.response.ErrorResponse
import com.lautify.app.api.response.RegisterResponse
import com.lautify.app.databinding.ActivityRegisterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var registerButton: Button
    private lateinit var Login: TextView
    private lateinit var apiService: ApiService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            etName = nameEditText
            etEmail = registerEmailText
            etPassword = registerPasswordCustom
            registerButton = btnRegis
            Login = findViewById(R.id.to_login)

        }
        apiService = ApiClient.getInstance()

        registerButton.isEnabled = false

        etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                showButton()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                showButton()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                showButton()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        registerButton.setOnClickListener {
            showLoading(true)
            val username = etName.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            registerUser(username, email, password)
        }

        Login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isValidEmail(str: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(str).matches()
    }

    private fun isValidPassword(str: String): Boolean {
        return str.length >= 8
    }

    private fun showButton() {
        binding.apply {
            registerButton.isEnabled =
                etPassword.text != null && isValidPassword(etPassword.text.toString()) &&
                        etEmail.text != null && isValidEmail(etEmail.text.toString()) &&
                        etName.text.isNotEmpty()
        }
    }

    private fun registerUser(username: String, email: String, password: String) {
        val request = RegisterResponse(username, email, password)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.registerUser(request)
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    if (response.isSuccessful) {
                        val registerResponse = response.body()
                        if (registerResponse != null) {
                            Toast.makeText(this@RegisterActivity, "User Registered.", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            // Handle unexpected empty response body
                            Toast.makeText(this@RegisterActivity, "Empty response body.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errorResponse = response.errorBody()?.string()
                        val errorMessage = Gson().fromJson(errorResponse, ErrorResponse::class.java).message
                        Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    val errorResponse = e.response()?.errorBody()?.string()
                    val errorMessage = Gson().fromJson(errorResponse, ErrorResponse::class.java).message
                    Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.progressSignup.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}
