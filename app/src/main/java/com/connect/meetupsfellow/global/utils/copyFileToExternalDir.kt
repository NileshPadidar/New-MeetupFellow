package com.connect.meetupsfellow.global.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
fun copyFileToExternalDir(context: Context, uri: Uri, fileName: String): File? {
    return try {
        val externalDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: return null
        if (!externalDir.exists()) {
            val created = externalDir.mkdirs()
            Log.d("DirectoryCreation", "Created Documents directory: $created")
        }

        val file = File(externalDir, fileName)
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        Log.d("FileCopy", "Copied file to path: ${file.absolutePath}")
        file
    } catch (e: Exception) {
        Log.e("FileCopyError", "Error copying file: ${e.message}", e)
        null
    }
}



/*fun copyFileToExternalDir(context: Context, uri: Uri, fileName: String): String? {
    try {
        val externalDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        if (externalDir != null && !externalDir.exists()) {
            externalDir.mkdirs() // Create the directory if it doesn't exist
        }

        val externalFile = File(externalDir, fileName)
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)

        inputStream?.use { input ->
            FileOutputStream(externalFile).use { output ->
                input.copyTo(output)
            }
        }

        return externalFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}*/
