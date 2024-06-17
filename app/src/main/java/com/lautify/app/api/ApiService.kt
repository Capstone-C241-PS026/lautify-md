package com.lautify.app.api

import com.lautify.app.api.response.DetailResponse
import com.lautify.app.api.response.LoginResponse
import com.lautify.app.api.response.PredictResponse
import com.lautify.app.api.response.PredictionsItem
import com.lautify.app.api.response.RecipesResponse
import com.lautify.app.api.response.RegisterResponse
import com.lautify.app.api.response.SuccessResponse
import com.lautify.app.api.response.SuccessResponses
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @GET("recipes")
    fun getRecipes(): Call<List<RecipesResponse>>

    @GET("recipes/{rid}")
    fun getRecipesDetails(
        @Path("rid")rid:String
    ):Call<DetailResponse>

    @POST("auth/login")
    suspend fun Login(
        @Body
        request: LoginResponse
    ): Response<SuccessResponses>


    @POST("auth/signup")
    suspend fun registerUser(
        @Body
        request: RegisterResponse
    ): Response<SuccessResponse>


    @Multipart
    @POST("predict")
    fun uploadImage(
        @Part predict : MultipartBody.Part
    ): Call<PredictResponse>
}

