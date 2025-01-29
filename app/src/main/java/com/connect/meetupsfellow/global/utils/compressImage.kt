package com.connect.meetupsfellow.global.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min

fun compressImage(file: File, maxWidth: Int, maxHeight: Int, quality: Int): File {
    // Decode the image with inJustDecodeBounds to get original dimensions
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    BitmapFactory.decodeFile(file.absolutePath, options)

    val originalWidth = options.outWidth
    val originalHeight = options.outHeight

    if (originalWidth <= 0 || originalHeight <= 0) {
        throw IllegalArgumentException("Invalid image file.")
    }

    // Calculate optimal inSampleSize to downscale while maintaining aspect ratio
    options.inSampleSize = calculateInSampleSize(originalWidth, originalHeight, maxWidth, maxHeight)
    options.inJustDecodeBounds = false

    // Decode the downscaled image
    val bitmap = BitmapFactory.decodeFile(file.absolutePath, options)

    // Calculate final scaled dimensions while maintaining aspect ratio
    val aspectRatio = originalWidth.toFloat() / originalHeight.toFloat()
    val finalWidth: Int
    val finalHeight: Int

    if (originalWidth > originalHeight) {
        finalWidth = min(maxWidth, originalWidth)
        finalHeight = (finalWidth / aspectRatio).toInt()
    } else {
        finalHeight = min(maxHeight, originalHeight)
        finalWidth = (finalHeight * aspectRatio).toInt()
    }

    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)

    // Save the compressed image to a new file
    val compressedFile = File(file.parent, "compressed_${file.name}")
    FileOutputStream(compressedFile).use { out ->
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
    }

    return compressedFile
}

// Function to calculate the optimal inSampleSize value
private fun calculateInSampleSize(
    origWidth: Int,
    origHeight: Int,
    reqWidth: Int,
    reqHeight: Int
): Int {
    var inSampleSize = 1

    if (origHeight > reqHeight || origWidth > reqWidth) {
        val halfHeight = origHeight / 2
        val halfWidth = origWidth / 2

        while (halfWidth / inSampleSize >= reqWidth && halfHeight / inSampleSize >= reqHeight) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
}
