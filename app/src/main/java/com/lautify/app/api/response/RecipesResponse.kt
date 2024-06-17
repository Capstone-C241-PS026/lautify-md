package com.lautify.app.api.response

data class RecipesResponse(
	val rid:Int,
	val title:String,
	val readyInMinutes:Int,
	val types:Array<String>,
	val image:String
)


