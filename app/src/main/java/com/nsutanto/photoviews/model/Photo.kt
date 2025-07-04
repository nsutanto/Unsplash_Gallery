package com.nsutanto.photoviews.model

import com.google.gson.annotations.SerializedName

data class Photo(
    @SerializedName("id") val id: String?,
    @SerializedName("urls") val urls: PhotoUrls?,
)