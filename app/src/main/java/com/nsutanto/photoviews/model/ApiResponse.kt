package com.nsutanto.photoviews.model

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("data")
    val data: List<Data>
)

data class Data(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
)

