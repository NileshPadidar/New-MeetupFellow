package com.connect.meetupsfellow.global.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log

fun getFileDetails(context: Context, uri: Uri): FileDetails {
    var name = "unknown"
    var size: Long = 0

    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)

        if (cursor.moveToFirst()) {
            if (nameIndex != -1) name = cursor.getString(nameIndex)
            if (sizeIndex != -1) size = cursor.getLong(sizeIndex)
        }
    }
    return FileDetails(name, size)
}
