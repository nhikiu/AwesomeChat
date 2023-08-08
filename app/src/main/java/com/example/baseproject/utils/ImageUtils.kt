package com.example.baseproject.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.io.OutputStream

object ImageUtils {
    fun saveBitmapToGallery(context: Context, bitmap: Bitmap): Uri? {
        val contentResolver = context.contentResolver
        val imageUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues())

        imageUri?.let { uri ->
            try {
                val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
                outputStream?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
                }
                outputStream?.close()
                return uri
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }
}
