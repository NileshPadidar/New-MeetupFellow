package com.connect.meetupsfellow.mvp.view.model

import android.net.Uri
import java.io.File

/**
 * Created by Maheshwar on 19-11-2019.
 */
class GalleryImagesModel(
    val images: String,
    var selected: Boolean,
    var sized: Boolean,
    var uri: Uri?,
    var file: File
)