package com.connect.meetupsfellow.global.view.easyphotopicker

/**
 * Created by Jacek Kwiecień on 03.11.2015.
 */
interface ConstantsImage {

    interface RequestCodes {
        companion object {
            val EASYIMAGE_IDENTIFICATOR = 876 //876
            val SOURCE_CHOOSER = 1 shl 15

            val PICK_PICTURE_FROM_DOCUMENTS = EASYIMAGE_IDENTIFICATOR + (1 shl 11)
            val PICK_PICTURE_FROM_GALLERY = EASYIMAGE_IDENTIFICATOR + (1 shl 12)
            val TAKE_PICTURE = EASYIMAGE_IDENTIFICATOR + (1 shl 13)
            val CAPTURE_VIDEO = EASYIMAGE_IDENTIFICATOR + (1 shl 14)
        }
    }

    interface BundleKeys {
        companion object {
            val FOLDER_NAME = "com.oit.reegur.folder_name"
            val ALLOW_MULTIPLE = "com.oit.reegur.allow_multiple"
            val COPY_TAKEN_PHOTOS = "com.oit.reegur.copy_taken_photos"
            val COPY_PICKED_IMAGES = "com.oit.reegur.copy_picked_images"
        }
    }

    companion object {

        val DEFAULT_FOLDER_NAME = "MeetUpsFellow"
    }
}
