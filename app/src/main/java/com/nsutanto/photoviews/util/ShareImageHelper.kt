package com.nsutanto.photoviews.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.core.content.FileProvider
import coil.imageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object ShareImageHelper {

    fun shareImage(context: Context, url: String) {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs() // create folder

        val fileName = "shared_image.jpg"
        val file = File(cachePath, fileName)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Load the image using Coil
                val request = ImageRequest.Builder(context)
                    .data(url)
                    .allowHardware(false) // Required to get a software Bitmap
                    .build()

                val drawable = context.imageLoader.execute(request).drawable
                val bitmap = (drawable as? BitmapDrawable)?.bitmap

                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))

                val contentUri: Uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )

                withContext(Dispatchers.Main) {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "image/jpeg"
                        putExtra(Intent.EXTRA_STREAM, contentUri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}