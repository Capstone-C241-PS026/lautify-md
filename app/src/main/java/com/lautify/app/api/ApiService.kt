package com.lautify.app.api

import com.lautify.app.api.response.DetailResponse
import com.lautify.app.api.response.LoginResponse
import com.lautify.app.api.response.PredictResponse
import com.lautify.app.api.response.PredictionsItem
import com.lautify.app.api.response.RecipesResponse
import com.lautify.app.api.response.RegisterResponse
import com.lautify.app.api.response.SimpleResponse
import com.lautify.app.api.response.SuccessResponse
import com.lautify.app.api.response.SuccessResponses
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("recipes")
    fun getRecipes(): Call<List<RecipesResponse>>

    @GET("recipes/save/{uid}")
    fun getRecipesSaved(@Path("uid")uid: String): Call<List<RecipesResponse>>

    @GET("recipes/save/{uid}/{rid}")
    fun saveRecipe(
        @Path("uid") uid: String,
        @Path("rid") rid: String
    ): Call<SimpleResponse>

    @DELETE("recipes/save/{uid}/{rid}")
    fun deleteRecipe(
        @Path("uid") uid: String,
        @Path("rid") rid: String
    ): Call<SimpleResponse>

    @GET("recipes/{rid}")
    fun getRecipesDetails(
        @Path("rid")rid:String
    ):Call<DetailResponse>

    @GET("recipes/fish/search")
    fun getSearch(
        @Query("query") query: String
    ):Call<List<RecipesResponse>>

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
        @Part predict: MultipartBody.Part,
    ): Call<ResponseBody>
}

