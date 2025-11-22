package com.nsutanto.photoviews.model

import com.google.gson.annotations.SerializedName

data class MainResponse(
    @SerializedName("products") val products: List<MainModel>
)

data class MainModel(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String?,
    @SerializedName("images") val image: List<String>
)

