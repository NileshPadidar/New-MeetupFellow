package com.connect.meetupsfellow.global.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.google.gson.Gson
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.view.model.GalleryImagesModel
import java.io.File

/**
 * Created by Maheshwar on 07-09-2017.
 */
class MediaStoreData {

    companion object {
        private val IMAGE_ID_COLUMN_NAME = MediaStore.Images.Media._ID
        private val CONTENT_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    fun loadPhotoUris(context: Context): List<GalleryImagesModel> {
        val uris = mutableListOf<GalleryImagesModel>()
        try {
            val columns = arrayOf(MediaStore.Images.Media.DATA, IMAGE_ID_COLUMN_NAME)
            val orderBy = MediaStore.Images.Media._ID

            val imagecursor = context.contentResolver.query(
                CONTENT_URI, columns, null, null, orderBy
            )

            if (imagecursor != null && imagecursor.count > 0) {

                while (imagecursor.moveToNext()) {
                    val dataColumnIndex = imagecursor
                        .getColumnIndex(MediaStore.Images.Media.DATA)

                    val file = File(imagecursor.getString(dataColumnIndex))
                    if (file.length() > 0) {
                        val imageUrl = imagecursor.getString(dataColumnIndex)
                        val images = GalleryImagesModel(
                            imageUrl,
                            selected = false,
                            sized = false,
                            uri = Uri.fromFile(file),
                            file = file
                        )

                        uris.add(images)
                    }
                }
            }

            imagecursor?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        uris.reverse()
        LogManager.logger.i(ArchitectureApp.instance!!.tag, "Photos is : ${Gson().toJson(uris)}")
        return uris

    }

}