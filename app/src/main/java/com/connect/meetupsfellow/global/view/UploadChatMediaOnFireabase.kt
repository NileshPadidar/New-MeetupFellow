package com.connect.meetupsfellow.global.view

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.developement.interfaces.FirebaseUtil
import com.connect.meetupsfellow.global.interfaces.UploadMediaInterface
import com.connect.meetupsfellow.mvp.view.dialog.UploadDialogCustom
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.lang.Exception
import javax.inject.Inject

/**
 * Uploading Bitmap Image on Firebase Storage
 */

class UploadChatMediaOnFireabase(private val uploadMediaInterface: UploadMediaInterface) {

    private var thumbUrl: String? = null
    private var originalUrl: String? = null

    private var fileLength: Long = 0

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    init {
        ArchitectureApp.component!!.inject(this@UploadChatMediaOnFireabase)
    }

    /**
     * For Image
     */
    fun uploadThumbImage(imageNamePath: String, thumbBitmap: Bitmap, origianlBitmap: Bitmap, currentMiliseconds: Long) {

        originalUrl = ""
        thumbUrl = originalUrl

        val imageReference = firebaseUtil.fetchStorageReference().child("$imageNamePath.jpeg")

        val baos = ByteArrayOutputStream()
        thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = imageReference.putBytes(data)


        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) throw task.exception!!

            // Continue with the task to get the download URL
            imageReference.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result

                thumbUrl = downloadUri!!.toString()
                callInterface(currentMiliseconds, thumbBitmap, origianlBitmap)
                uploadMediaInterface.onThumbImageUploaded(thumbUrl!!)

            } else {
                // Handle failures
                // ...
            }
        }.addOnFailureListener { e -> uploadMediaInterface.onImageUploadFailure(e.message!!) }


        /*uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                Log.e("sss", "Exception in UploadImageOnFireabaseStorage - - - " + exception);

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                String downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                Log.e("sss", "downloadUrl UploadImageOnFireabaseStorage - - - " + downloadUrl);

                thumbUrl = downloadUrl;
                callInterface(currentMiliseconds, thumbBitmap, origianlBitmap);
                uploadMediaInterface.onThumbImageUploaded(thumbUrl);
            }
        });*/
    }

    fun uploadOriginalImage(
        imageNamePath: String, thumbBitmap: Bitmap, originalBitmap: Bitmap, uploadDialog: UploadDialogCustom,
        currentMiliseconds: Long
    ) {

        fileLength = originalBitmap.byteCount.toLong()

        val imageReference = firebaseUtil.fetchStorageReference().child("$imageNamePath.jpeg")

        val baos = ByteArrayOutputStream()
        originalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = imageReference.putBytes(data)


        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) throw task.exception!!

            // Continue with the task to get the download URL
            imageReference.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result

                originalUrl = downloadUri!!.toString()
                callInterface(currentMiliseconds, thumbBitmap, originalBitmap)

            } else
                uploadMediaInterface.onImageUploadFailure("Failed to upload")
        }.addOnFailureListener { exception ->
            Log.e("sss", "Exception in UploadImageOnFireabaseStorage - - - $exception")
            uploadMediaInterface.onImageUploadFailure(exception.message!!)
        }


        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
            println("Upload is $progress% done")

            Log.i("sss", "bitmap.getByteCount() - " + originalBitmap.byteCount)
            Log.i("sss", "taskSnapshot.getBytesTransferred() - " + taskSnapshot.bytesTransferred)

            uploadDialog.setProgress(progress.toInt())
        }

    }


    private fun callInterface(currentMiliseconds: Long, thumbBitmap: Bitmap, originalBitmap: Bitmap) {

        Log.d("sss", "In uploadChatMedia thumbUrl = $thumbUrl   ,, originalUrl = $originalUrl")

        if (!thumbUrl!!.trim { it <= ' ' }.isEmpty() && !originalUrl!!.trim { it <= ' ' }.isEmpty())
            uploadMediaInterface.onOriginalImageUploaded(
                thumbUrl!!.toString(), originalUrl!!.toString(),
                fileLength, thumbBitmap, originalBitmap, currentMiliseconds
            )
    }


    /**
     * For Video
     */


    fun uploadVideoThumb(imageNamePath: String, bitmap: Bitmap) {
        try {


            val imageReference = firebaseUtil.fetchStorageReference().child("$imageNamePath.jpeg")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

            val uploadTask = imageReference.putBytes(data)


            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }

                // Continue with the task to get the download URL
                imageReference.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result

                    thumbUrl = downloadUri!!.toString()
                    Log.d("sss", "Thumb Url for video storage = " + thumbUrl!!)
                    uploadMediaInterface.onThumbVideoUploaded(thumbUrl!!)

                } else
                    uploadMediaInterface.onImageUploadFailure("Failed to upload")
            }.addOnFailureListener { exception ->
                Log.e("sss", "Exception in UploadImageOnFireabaseStorage - - - $exception")
                uploadMediaInterface.onVideoUploadFailure(exception.message!!)
            }


            /*uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    System.out.println("Upload is " + progress + "% done");

                    Log.i("sss", "bitmap.getByteCount() - " + bitmap.getByteCount());
                    Log.i("sss", "taskSnapshot.getBytesTransferred() - " + taskSnapshot.getBytesTransferred());

                    uploadDialog.setProgress((int) progress);
                }
            });*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun uploadVideoFile(
        videoPath: String, stream: InputStream, uploadDialog: UploadDialogCustom,
        fileSize: Double, currentMilis: Long,
        bitmapVideoThumb: Bitmap, videoFile: File
    ) {

        Log.i("sss", "currentMilis 2 $currentMilis  ,, fileSize = $fileSize")

        val videoReference = firebaseUtil.fetchStorageReference().child("$videoPath.mp4")

        val uploadTask = videoReference.putStream(stream)


        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                throw task.exception!!
            }

            // Continue with the task to get the download URL
            videoReference.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result

                val downloadUrl = downloadUri!!.toString()
                uploadMediaInterface.onVideoUploaded(
                    thumbUrl!!, downloadUrl,
                    0, currentMilis, bitmapVideoThumb, videoFile
                )

            } else
                uploadMediaInterface.onVideoUploadFailure("Failed to upload")
        }.addOnFailureListener { exception ->
            Log.e("sss", "Exception in UploadImageOnFireabaseStorage - - - $exception")
            uploadMediaInterface.onVideoUploadFailure(exception.message!!)
        }

        uploadTask.addOnProgressListener { taskSnapshot ->
            Log.i(
                "sss", "TasksnapShot transfered bytes = " + taskSnapshot.bytesTransferred
                        + "  and total bytes = " + fileSize
            )

            val progress = 100.0 * taskSnapshot.bytesTransferred / fileSize
            uploadDialog.setProgress(progress.toInt())
        }
    }

    fun uploadVideoUri(
        videoPath: String, uri: Uri, uploadDialog: UploadDialogCustom,
        currentMilis: Long, bitmapVideoThumb: Bitmap?, galleryFile: File
    ) {

        val videoReference = firebaseUtil.fetchStorageReference().child("$videoPath.mp4")

        val uploadTask = videoReference.putFile(uri)


        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                throw task.exception!!
            }

            // Continue with the task to get the download URL
            videoReference.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result

                val downloadUrl = downloadUri!!.toString()
                uploadMediaInterface.onVideoUploaded(
                    thumbUrl!!, downloadUrl,
                    0, currentMilis, bitmapVideoThumb!!, galleryFile
                )

            } else
                uploadMediaInterface.onVideoUploadFailure("Failed to upload")
        }.addOnFailureListener { exception ->
            Log.e("sss", "Exception in UploadImageOnFireabaseStorage - - - $exception")
            uploadMediaInterface.onVideoUploadFailure(exception.message!!)
        }

        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
            uploadDialog.setProgress(progress.toInt())
        }
    }


    fun uploadAudioUri(
        audioPath: String, uri: Uri, uploadDialog: UploadDialogCustom,
        currentMiliseconds: Long, uploadFile: File
    ) {

        val audioStorageReference = firebaseUtil.fetchStorageReference().child("$audioPath.m4a")

        val uploadTask = audioStorageReference.putFile(uri)


        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                throw task.exception!!
            }

            // Continue with the task to get the download URL
            audioStorageReference.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result

                val downloadUrl = downloadUri!!.toString()
//                uploadMediaInterface.onAudioUploaded(downloadUrl, 0, currentMiliseconds, uploadFile)

            }
//            else
//                uploadMediaInterface.onAudioUploadFailure("Failed to upload")
        }.addOnFailureListener { exception ->
            Log.e("sss", "Exception in UploadImageOnFireabaseStorage - - - $exception")
//            uploadMediaInterface.onAudioUploadFailure(exception.message)
        }


        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
            uploadDialog.setProgress(progress.toInt())
        }
    }


    fun uploadDocumentUri(
        docPath: String, uriPath: Uri, uploadDialog: UploadDialogCustom, fileExtention: String,
        currentMiliseconds: Long, docFile: File
    ) {

        val docReference = firebaseUtil.fetchStorageReference().child("$docPath.$fileExtention")

        val uploadTask = docReference.putFile(uriPath)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                throw task.exception!!
            }

            // Continue with the task to get the download URL
            docReference.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val downloadUri = task.result
                val downloadUrl = downloadUri!!.toString()

//                uploadMediaInterface.onDocumentUploaded(downloadUrl, 0, fileExtention, currentMiliseconds, docFile)

            }
//            else
//                uploadMediaInterface.onDocumentUploadFailure("Failed to upload")
        }.addOnFailureListener { exception ->
            Log.e("sss", "Exception in UploadImageOnFireabaseStorage - - - $exception")
//            uploadMediaInterface.onDocumentUploadFailure(exception.message)
        }


        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
            uploadDialog.setProgress(progress.toInt())
        }

    }


    /**
     * For Location thumb
     */

    fun uploadMapThumbImage(
        uploadDialog: UploadDialogCustom, imageNamePath: String,
        thumbBitmap: Bitmap, currentMiliseconds: Long,
        lat: String, lng: String
    ) {

        originalUrl = ""
        thumbUrl = originalUrl

        val imageReference = firebaseUtil.fetchStorageReference().child("$imageNamePath.jpeg")

        val baos = ByteArrayOutputStream()
        thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = imageReference.putBytes(data)


        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                throw task.exception!!
            }

            // Continue with the task to get the download URL
            imageReference.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
//                uploadMediaInterface.onMapsThumbUplaoded(downloadUri!!.toString(), lat, lng, currentMiliseconds)
            }
        }.addOnFailureListener { e ->
            //            uploadMediaInterface.onMapslaodedFailure(e.message)
        }


        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
            uploadDialog.setProgress(progress.toInt())
        }

    }
}