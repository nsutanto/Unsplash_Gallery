package com.nsutanto.photoviews.ui.screens

import android.net.Uri

sealed class Screen(val route: String) {
    object PhotoGallery : Screen("photo_gallery")
    object PhotoDetail : Screen("photo_detail/{photoId}") {
        fun createRoute(photoId: String): String {
            return "photo_detail/$photoId"
        }
    }
}