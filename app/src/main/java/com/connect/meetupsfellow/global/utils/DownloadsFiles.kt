package com.connect.meetupsfellow.global.utils

import android.app.DownloadManager
import android.content.Context
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import com.connect.meetupsfellow.constants.Constants

class DownloadsFiles(
    private val context: Context,
    private val chatDb: DatabaseReference,
    private val userId: String
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun downloadFile(fileUrl: String) {
        withContext(Dispatchers.IO) {
            try {
                val url = URL(fileUrl)
                val filename = fileUrl.substringAfterLast('/')
                val extension = filename.substringAfterLast('.', "")

                val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                if (!path.exists()) {
                    path.mkdirs()
                }

                val time = System.currentTimeMillis()
                val imageFile = File(path, "$time.$extension")

                val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val uriM = Uri.parse(fileUrl)

                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(imageFile.absolutePath),
                    null
                ) { scannedPath, uri ->
                    Log.e("decode*&Catch", "Scanned $scannedPath -> uri=$uri")
                    chatDb.child("mediaDeviceUrlOf").child(userId).setValue(scannedPath)
                    Constants.ImgPath = scannedPath

                    val request = DownloadManager.Request(uriM)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, "$time.$extension")
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

                    val downloadId = downloadManager.enqueue(request)

                    val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        context.registerReceiver(DownloadReceiver(downloadId), intentFilter, Context.RECEIVER_NOT_EXPORTED)
                    } else {
                        context.registerReceiver(DownloadReceiver(downloadId), intentFilter,
                            Context.RECEIVER_NOT_EXPORTED)
                    }
                }
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }
        }
    }



    private class DownloadReceiver(private val downloadId: Long) : android.content.BroadcastReceiver() {
        override fun onReceive(context: Context, intent: android.content.Intent) {
            val receivedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadId == receivedDownloadId) {
                Log.e("Download", "File download completed")
                Toast.makeText(context, "File saved", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
