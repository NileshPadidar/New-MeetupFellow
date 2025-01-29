package com.connect.meetupsfellow.global.view;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.firebase.database.DatabaseReference;
import com.connect.meetupsfellow.constants.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

//import io.fabric.sdk.android.services.concurrency.AsyncTask;

/**
 * Created & Update by Nilu on 12-12-2024.
 */
public class DownloadsFiles extends AsyncTask<String, Void, Void> {

    Context context;
    String imgUriPath;
    DatabaseReference chatDb;
    String userId;

    public DownloadsFiles(Context context, DatabaseReference chatDb, String userId) {

        this.context = context;
        this.chatDb = chatDb;
        this.userId = userId;
        Log.d("DownloadId", userId);
    }

    @Override
    protected Void doInBackground(String... strings) {
        URL url = null;
        String ext = "";
        try {
            url = new URL(strings[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Bitmap bm = null;
        try {
            bm =    BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("decode*&Catch", "fileUrL: " + url);

        String[] parts = url.toString().split("\\?");
        String path1 = parts[0];

// Split the path by '/' to get the filename
        String[] pathSegments = path1.split("/");
        String filename = pathSegments[pathSegments.length - 1];
        Log.e("decode*&Catch", "filename: " + filename);
// Split the filename by '.' to get the extension
        String[] filenameParts = filename.split("\\.");
         ext = filenameParts[filenameParts.length - 1];
        Log.e("decode*&Catch", "extension: " + ext);
        ext = "."+ext;
        Log.e("decode*&Catch", "extension2: " + ext);
          File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS); //Creates app specific folder
            if (!path.exists()) {
                path.mkdirs();
            }
        long time = System.currentTimeMillis();
        File imageFile = null;

        imageFile = new File(path, time + ext);

            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uriM = Uri.parse(String.valueOf(url));

        String finalExt = ext;
        Log.e("decode*&Catch", "finalExt " + finalExt);
        MediaScannerConnection.scanFile(context, new String[]{imageFile.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                public void onScanCompleted(String path, Uri uri) {
                    Log.e("decode*&Catch", "Scanned " + path + ":");
                    Log.d("decode*ExternalStorage", "-> uri=" + uri);
                    imgUriPath = path;
                    chatDb.child("mediaDeviceUrlOf").child(userId).setValue(imgUriPath);
                    Constants.INSTANCE.setImgPath(path);
                    DownloadManager.Request request = new DownloadManager.Request(uriM);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, time + finalExt);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.allowScanningByMediaScanner();
                    long downloadId = downloadManager.enqueue(request);
                    // Register a receiver to listen for download completion
                    context.registerReceiver(new DownloadReceiver(downloadId), new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_NOT_EXPORTED);
                }
            });


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
       /// Toast.makeText(context,"File Saved!", Toast.LENGTH_SHORT).show();
    }
    private static class DownloadReceiver extends BroadcastReceiver {
        private final long downloadId;

        DownloadReceiver(long downloadId) {
            this.downloadId = downloadId;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            long receivedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if (downloadId == receivedDownloadId) {
                // Download completed
                Log.e("Downlod", "Video download completed");
                Toast.makeText(context, "File save", Toast.LENGTH_SHORT).show();
                // Perform any further processing here, e.g., open the downloaded video file
            }
        }
    }
}