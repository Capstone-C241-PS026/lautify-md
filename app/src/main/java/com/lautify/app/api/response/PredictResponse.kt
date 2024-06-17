package com.lautify.app.api.response

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody

data class PredictResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	val predict: String? = null,

)

data class Data(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("predictions")
	val predictions: List<PredictionsItem?>? = null
)

data class PredictionsItem(

	@field:SerializedName("confidence")
	val confidence: Int? = null,

	@field:SerializedName("freshness")
	val freshness: String? = null
)
