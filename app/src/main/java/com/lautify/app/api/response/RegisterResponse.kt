package com.lautify.app.api.response

data class RegisterResponse(
    val username : String,
    val email : String,
    val password : String,
)

data class SuccessResponse(
    val success: Boolean,
    val message: String
)

data class ErrorResponse(
    val message: String
)
