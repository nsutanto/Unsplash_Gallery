package com.nsutanto.photoviews.util

import android.content.Context
import android.content.Intent

object SharePhotoLink {
    fun shareImageUrl(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, url)
        }
        context.startActivity(Intent.createChooser(intent, "Share Image URL via"))
    }
}