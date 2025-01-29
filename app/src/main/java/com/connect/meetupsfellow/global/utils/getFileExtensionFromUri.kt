package com.connect.meetupsfellow.global.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap

fun getFileExtensionFromUri(context: Context, uri: Uri): String? {
    return try {
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(uri) // Get the MIME type
        if (mimeType != null) {
            MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) // Get extension from MIME type
        } else {
            // If MIME type is null, try to extract from file name
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst() && nameIndex != -1) {
                    val fileName = cursor.getString(nameIndex)
                    fileName.substringAfterLast('.', "") // Extract extension from file name
                } else {
                    null
                }
            }
        }
    } catch (e: Exception) {
        Log.e("FileExtensionError", "Error getting file extension: ${e.message}")
        null
    }
}
