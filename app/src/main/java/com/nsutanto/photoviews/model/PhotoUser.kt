package com.nsutanto.photoviews.model

import com.google.gson.annotations.SerializedName

data class PhotoUser(
    @SerializedName("username") val username: String?
)
