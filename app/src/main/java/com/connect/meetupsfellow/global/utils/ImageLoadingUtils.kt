package com.connect.meetupsfellow.global.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.TypedValue
import com.connect.meetupsfellow.R


@SuppressLint("StaticFieldLeak")
/**
 * Created by Jamwal on 11/23/2017.
 */
class ImageLoadingUtils(private var context: Context) {

    private var icon = BitmapFactory.decodeResource(context.resources, R.drawable.meetupsfellow_transpatent)

    //    fun ImageLoadingUtils(context: Context) {
//        this.context = context
//        icon = BitmapFactory.decodeResource(context.resources, R.drawable.meetupsfellow_transpatent)
//    }

    private fun convertDipToPixels(dips: Float): Int {
        val r = context.resources
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dips, r.displayMetrics).toInt()
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }

        return inSampleSize
    }

    @Suppress("VARIABLE_WITH_REDUNDANT_INITIALIZER", "UNUSED_VALUE", "DEPRECATION")
    fun decodeBitmapFromPath(filePath: String): Bitmap? {
        var scaledBitmap: Bitmap? = null

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        scaledBitmap = BitmapFactory.decodeFile(filePath, options)

        options.inSampleSize = calculateInSampleSize(options, convertDipToPixels(150f), convertDipToPixels(200f))
        options.inDither = false
        options.inPurgeable = true
        options.inInputShareable = true
        options.inJustDecodeBounds = false

        scaledBitmap = BitmapFactory.decodeFile(filePath, options)
        return scaledBitmap
    }
}