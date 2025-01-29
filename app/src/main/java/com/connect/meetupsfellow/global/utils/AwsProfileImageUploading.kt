//package com.oit.reegur.global.utils
//
//import android.annotation.SuppressLint
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.graphics.Matrix
//import android.net.Uri
//import android.os.StrictMode
//import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
//import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
//import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
//import com.oit.reegur.application.ArchitectureApp
//import com.oit.reegur.constants.Constants
//import com.oit.reegur.log.LogManager
//import com.oit.reegur.mvp.view.model.ProfileImageModel
//import org.apache.commons.io.FileUtils
//import java.io.File
//import java.io.FileOutputStream
//import java.io.IOException
//import java.net.HttpURLConnection
//import java.net.URL
//
//
///**
// * Created by Jammwal on 21-03-2018.
// */
//@SuppressLint("StaticFieldLeak")
//object AwsProfileImageUploading {
//
//    private lateinit var transferUtility: TransferUtility
//    private lateinit var observer: TransferObserver
//    private lateinit var transferListener: TransferListener
//
//    private var file: File? = null
//    private lateinit var image: ProfileImageModel
//
//    internal fun with(transferUtility: TransferUtility, transferListener: TransferListener): AwsProfileImageUploading {
//        AwsProfileImageUploading.transferUtility = transferUtility
//        AwsProfileImageUploading.transferListener = transferListener
//        return this
//    }
//
//    internal fun image(file: File): AwsProfileImageUploading {
//        AwsProfileImageUploading.file = file
//        return this
//    }
//
//    internal fun upload(): AwsProfileImageUploading {
//        beginUpload()
////        generateTempFile(image.url.replace(" ", ""))
//        return this
//    }
//
//    private fun beginUpload() {
//        if (file != null) {
//            observer = transferUtility.upload(
//                Constants.AWS.AwsProfileBucketName.BUCKET_NAME,
//                file!!.name,
//                file
//            )
//            LogManager.logger.i(ArchitectureApp.instance!!.tag, "Image Path is : File")
//            observer.setTransferListener(transferListener)
//        } else {
//            LogManager.logger.i(ArchitectureApp.instance!!.tag, "Image Path is : No File")
//        }
//    }
//
////    private fun getBucketName(): String {
////        return when (image.signature) {
////            "Selfie" -> {
////                Constants.AWS.AwsSelfieBucketName.BUCKET_NAME
////            }
////
////            else -> {
////                Constants.AWS.AwsProfileBucketName.BUCKET_NAME
////            }
////        }
////    }
//
//    private fun generateTempFile(url: String) {
//        try {
//            val fileName = Uri.parse(url).lastPathSegment
//            val filesDir = ArchitectureApp.instance!!.filesDir
//
//            file =
//                File(filesDir, fileName.replace(" ", "") + Constants.Device.TYPE + System.currentTimeMillis() + ".jpg")
//
//            /*
//             * To overcome android.os.NetworkOnMainThreadException
//            * */
//            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
//            StrictMode.setThreadPolicy(policy)
//
//            if (url.startsWith("file://")) {
//                FileUtils.copyURLToFile(URL(url), file)
//            } else {
//                val bitmap = getBitmapFromURL(url)
//                val os = FileOutputStream(file)
//                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, os)
//                os.flush()
//                os.close()
//            }
//
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        beginUpload()
//    }
//
//    private fun getBitmapFromURL(src: String): Bitmap? {
//        return try {
//            val url = URL(src)
//            val connection = url
//                .openConnection() as HttpURLConnection
//            connection.doInput = true
//            connection.connect()
//            val input = connection.inputStream
//            getResizedBitmap(
//                BitmapFactory.decodeStream(input),
//                400,
//                400
//            )
//
//        } catch (e: IOException) {
//            e.printStackTrace()
//            null
//        }
//    }
//
//    private fun getResizedBitmap(bm: Bitmap, newHeight: Int, newWidth: Int): Bitmap {
//        val width = bm.width
//        val height = bm.height
//        val scaleWidth = newWidth.toFloat() / width
//        val scaleHeight = newHeight.toFloat() / height
//        // Create A MATRIX FOR THE MANIPULATION
//        val matrix = Matrix()
//        // RESIZE THE BIT MAP
//        matrix.postScale(scaleWidth, scaleHeight)
//
//        // "RECREATE" THE NEW BITMAP
//
//        return Bitmap.createBitmap(
//            bm, 0, 0, width, height,
//            matrix, false
//        )
//    }
//
//    private fun getFacebookProfilePicture(url: String): Bitmap {
//        return BitmapFactory.decodeStream(URL(url).openConnection().getInputStream())
//    }
//
//}