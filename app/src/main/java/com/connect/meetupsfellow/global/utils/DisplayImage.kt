package com.connect.meetupsfellow.global.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.bumptech.glide.request.RequestOptions.overrideOf
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.global.interfaces.OnImageDisplay
import jp.wasabeef.glide.transformations.*
import jp.wasabeef.glide.transformations.gpu.VignetteFilterTransformation


@SuppressLint("StaticFieldLeak")
/**
 * Created by Jamwal on 12/28/2017.
 * Update by Nilu on 12_Dec_24
 */
object DisplayImage {

    private lateinit var imageView: ImageView
    private lateinit var imageUrl: String
    private var imageUri: Uri? = null
    private var signature = ""
    private var display: OnImageDisplay? = null
    private var shape = Shape.DEFAULT
    private var context: Context? = null
    private var fragment: FragmentActivity? = null
    private var placeholder = R.drawable.meetupsfellow_transpatent
    private var placeholder_icon = R.drawable.meetupsfellow_transpatent
    private var memoryCache = true
    private var mask = R.drawable.mask_image

    internal fun with(context: Context): DisplayImage {
        DisplayImage.context = context
        return this
    }

    internal fun with(fragment: FragmentActivity): DisplayImage {
        DisplayImage.fragment = fragment
        return this
    }

    internal fun load(imageUrl: String): DisplayImage {
        DisplayImage.imageUrl = imageUrl
        return this
    }

    internal fun load(imageUri: Uri): DisplayImage {
        DisplayImage.imageUri = imageUri
        return this
    }

    internal fun placeholder(placeholder: Int): DisplayImage {
        DisplayImage.placeholder = placeholder
        return this
    }

    internal fun mask(mask: Int): DisplayImage {
        DisplayImage.mask = mask
        return this
    }

    internal fun shape(shape: Shape): DisplayImage {
        DisplayImage.shape = shape
        return this
    }

    internal fun into(imageView: ImageView): DisplayImage {
        DisplayImage.imageView = imageView
        return this
    }

    internal fun progress(display: OnImageDisplay): DisplayImage {
        DisplayImage.display = display
        return this
    }

    internal fun signature(signature: String): DisplayImage {
        DisplayImage.signature = signature
        return this
    }

    internal fun cache(memoryCache: Boolean): DisplayImage {
        DisplayImage.memoryCache = memoryCache
        return this
    }

    internal fun build(): DisplayImage {
        checkShape()
        return this
    }


    private fun checkShape() {
        when (shape) {
            Shape.GIF -> displayImageGif()
            Shape.CropCircle -> circleImage()
            Shape.RoundedCorners -> rectangleImage()
            Shape.DEFAULT -> displayImage()
            Shape.Mask -> TODO()
            Shape.NinePatchMask -> maskNinePatchImage()
            Shape.CropTop -> TODO()
            Shape.CropCenter -> TODO()
            Shape.CropBottom -> TODO()
            Shape.CropSquare -> squareImage()
            Shape.ColorFilter -> colorFilter()
            Shape.Grayscale -> TODO()
            Shape.Blur -> blurImage()
            Shape.Toon -> TODO()
            Shape.Sepia -> TODO()
            Shape.Contrast -> TODO()
            Shape.Invert -> TODO()
            Shape.Pixel -> TODO()
            Shape.Sketch -> TODO()
            Shape.Swirl -> TODO()
            Shape.Brightness -> TODO()
            Shape.Kuawahara -> TODO()
            Shape.Vignette -> vignetteImage()
            Shape.SHAPE_NEARBY -> displayImageNearBy()
        }
    }

    private fun squareImage() {

        Glide.with(ArchitectureApp.instance!!)
            .load(if (imageUrl.isEmpty()) placeholder else imageUrl)
            .apply(bitmapTransform(CropSquareTransformation()))
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .dontAnimate()
            .into(imageView)
    }

    private fun displayImageGif() {
        Glide.with(ArchitectureApp.instance!!)
            .asGif()
            .load(R.raw.final_splash)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(imageView)
    }

    private fun displayImage() {
        Glide.with(ArchitectureApp.instance!!)
            .load(if (imageUrl.isEmpty()) placeholder else imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .skipMemoryCache(false)
            .dontAnimate()
            .into(imageView)
    }

    private fun displayImageNearBy() {
        Glide.with(ArchitectureApp.instance!!)
            .load(if (imageUrl.isEmpty()) placeholder_icon else imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .skipMemoryCache(false)
            .dontAnimate()
            .into(imageView)
    }

    @Suppress("DEPRECATION")
    private fun circleImage() {

        Glide.with(ArchitectureApp.instance!!)
            .load(if (imageUrl.isEmpty()) placeholder else imageUrl)
            .apply(bitmapTransform(CropCircleTransformation()))
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .dontAnimate()
            .into(imageView)
    }

    private fun rectangleImage() {
        Glide.with(ArchitectureApp.instance!!)
            .load(if (imageUrl.isEmpty()) placeholder else imageUrl)
            .apply(
                bitmapTransform(
                    RoundedCornersTransformation(
                        20, 5,
                        RoundedCornersTransformation.CornerType.ALL
                    )
                )
            )
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(imageView)
    }

    private fun maskNinePatchImage() {

        Glide.with(ArchitectureApp.instance!!)
            .load(imageUrl)
            .apply(overrideOf(266.px, 252.px))
            .apply(
                bitmapTransform(
                    MultiTransformation<Bitmap>(
                        CenterCrop(),
                        MaskTransformation(mask)
                    )
                )
            )
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(imageView)

    }

    private fun blurImage() {
        Glide.with(ArchitectureApp.instance!!)
            .load(imageUrl)
            .apply(bitmapTransform(BlurTransformation(25)))
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(imageView)
    }

    private fun colorFilter() {
        Glide.with(ArchitectureApp.instance!!)
            .load(imageUrl)
            .apply(bitmapTransform(ColorFilterTransformation(Color.argb(80, 255, 0, 0))))
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(imageView)
    }

    private fun vignetteImage() {
        Glide.with(ArchitectureApp.instance!!)
            .load(imageUrl)
            .apply(
                bitmapTransform(
                    VignetteFilterTransformation(
                        PointF(0.5f, 0.5f),
                        floatArrayOf(0.0f, 0.0f, 0.0f), 0f, 0.75f
                    )
                )
            )
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .dontAnimate()
            .into(imageView)
    }

    private fun getImage(imageUrl: String):String{
        return when(BuildConfig.DEBUG) {
            true -> "http://107.23.204.210/assets/images/unnamed_tn.png"
            false -> imageUrl
        }
    }

    enum class Shape {
        DEFAULT,
        GIF,
        Mask,
        NinePatchMask,
        CropTop,
        CropCenter,
        CropBottom,
        CropSquare,
        CropCircle,
        ColorFilter,
        Grayscale,
        RoundedCorners,
        Blur,
        Toon,
        Sepia,
        Contrast,
        Invert,
        Pixel,
        Sketch,
        Swirl,
        Brightness,
        Kuawahara,
        Vignette,
        SHAPE_NEARBY
    }
}