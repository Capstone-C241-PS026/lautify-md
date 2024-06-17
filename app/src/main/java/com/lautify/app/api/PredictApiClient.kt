package com.lautify.app.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PredictApiClient {
    private const val PREDICT_BASE_URL ="https://lautify-predict-jgr52etzdq-et.a.run.app/"

    val predict: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(PREDICT_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }

    fun predict(): ApiService{
        return predict
    }
}