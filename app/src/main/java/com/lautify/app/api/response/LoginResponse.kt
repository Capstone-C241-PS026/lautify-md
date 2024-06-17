package com.lautify.app.api.response

import com.google.gson.annotations.SerializedName


data class LoginResponse(
    val email : String,
    val password : String,

)

data class SuccessResponses(
    val success: Boolean,
    val message: String?,
    val error: Boolean,
    val data :LoginData?
)

data class ErrorResponses(
    @SerializedName("message") val message: String,
    @SerializedName("errors") val errors: Any
)

data class LoginData(
    val username : String,
    val email: String,
    val uid : String,
    val token : String

)
