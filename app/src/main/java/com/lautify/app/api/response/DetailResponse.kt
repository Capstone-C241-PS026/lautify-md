package com.lautify.app.api.response

import com.google.gson.annotations.SerializedName

data class DetailResponse(

	@field:SerializedName("readyInMinutes")
	val readyInMinutes: Int? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("instructions")
	val instructions: ArrayList<String>,

	@field:SerializedName("types")
	val types: List<String?>? = null,

	@field:SerializedName("ingredients")
	val ingredients: ArrayList<String>,

	@field:SerializedName("rid")
	val rid: Int? = null,

	@field:SerializedName("title")
	val title: String? = null
)
