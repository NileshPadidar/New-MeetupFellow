package com.connect.meetupsfellow.global.interfaces

import android.graphics.Bitmap
import com.connect.meetupsfellow.mvp.view.activities.ChatActivity

import java.io.File


interface UploadMediaInterface {

    abstract val lifecycleScope: Any

    fun onThumbImageUploaded(url: String)

    fun onOriginalImageUploaded(
        thumbUrl: String, originalUrl: String, fileLength: Long,
        thumbBitmap: Bitmap, originalBitmap: Bitmap, currentMiliSeconds: Long
    )

    fun onImageUploadFailure(error: String)

    fun onThumbVideoUploaded(url: String)

    fun onVideoUploaded(
        thumbUrl: String, videoUrl: String, videoSize: Long, currentMiliSeconds: Long,
        bitmapVideoThumb: Bitmap, uploadedFile: File
    )

    fun onVideoUploadFailure(error: String)
    abstract fun PdfiumCore(chatActivity: ChatActivity): Any

    //    void onAudioUploaded(String url, long audioSize, long currentMiliSeconds, File uploadedFile);
    //
    //    void onAudioUploadFailure(String error);
    //
    //    void onDocumentUploaded(String url, long docSize, String fileExtension, long currentMiliSeconds,
    //                            File docFile);
    //
    //    void onDocumentUploadFailure(String error);
    //
    //    void onMapsThumbUplaoded(String url, String lat, String lng, long currentMiliseconds);
    //
    //    void onMapslaodedFailure(String error);

}
