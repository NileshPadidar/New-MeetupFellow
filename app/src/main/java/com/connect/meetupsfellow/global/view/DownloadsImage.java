package com.connect.meetupsfellow.global.view;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.connect.meetupsfellow.constants.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

//import io.fabric.sdk.android.services.concurrency.AsyncTask;

public class DownloadsImage extends AsyncTask<String, Void, Void> {

    Context context;
    String imgUriPath;
    DatabaseReference chatDb;
    String userId;
    int type;

    public DownloadsImage(Context context, DatabaseReference chatDb, String userId, int type) {

        this.context = context;
        this.chatDb = chatDb;
        this.userId = userId;
        this.type = type;
        Log.d("DownloadId", userId + " chatDb: " + chatDb);
    }

    @Override
    protected Void doInBackground(String... strings) {
        URL url = null;
        try {
            url = new URL(strings[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Bitmap bm = null;
        Log.e("decode*&", "URLImage- " + url);
        Log.e("decode*&", "URLstring- " + strings);
        try {
            assert url != null;
            bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        long time = System.currentTimeMillis();
        //Create Path to save Image
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/MeetUpsFellow Chat Images"); //Creates app specific folder
        if (!path.exists()) {
            path.mkdirs();
        }

        File imageFile = null;

        if (type == 2 || strings[0].contains(".png") || strings[0].contains(".jpg") || strings[0].contains(".jpeg")) {
            imageFile = new File(path, time + ".jpg");

        }
        else {
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES); //Creates app specific folder
            if (!path.exists()) {
                path.mkdirs();
            }
            imageFile = new File(path, time + ".mp4");

            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uriM = Uri.parse(String.valueOf(url));

            MediaScannerConnection.scanFile(context, new String[]{imageFile.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.e("decode*&Catch", "Scanned " + path + ":");
                    Log.d("ExternalStorageELSS", "-> uri=" + uri);
                    imgUriPath = path;
                    chatDb.child("mediaDeviceUrlOf").child(userId).setValue(imgUriPath);
                    Constants.INSTANCE.setImgPath(path);

                    DownloadManager.Request request = new DownloadManager.Request(uriM);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES, time + ".mp4");
                    /// request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES + "/MeetUpsFellow Chat Images", time + ".mp4");
                    ///  request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    //   request.setDestinationInExternalFilesDir(context, String.valueOf(context.getExternalFilesDir(null)), time + ".mp4");

                    // Allow the MediaScanner to scan the downloaded file
                    request.allowScanningByMediaScanner();

                    long downloadId = downloadManager.enqueue(request);


                    // Register a receiver to listen for download completion
                    ///     context.registerReceiver(new DownloadReceiver(downloadId), new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                }
            });
            return null;
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(imageFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Log.e("decode*&Up", "imageFile- " + imageFile);
        try {
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out); // Compress Image
            out.flush();
            out.close();
            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(context, new String[]{imageFile.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.e("decode*&Try", "Scanned " + path + ":");
                    Log.d("ExternalStorage", "-> uri=" + uri);
                    imgUriPath = path;
                    chatDb.child("mediaDeviceUrlOf").child(userId).setValue(imgUriPath);
                    Constants.INSTANCE.setImgPath(path);
                }
            });
        } catch (Exception e) {
            Log.e("decode*&", "catch-Faild");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context, "Video download!", Toast.LENGTH_SHORT).show();
                // Perform any further processing here, e.g., open the downloaded video file
            }
        }
    }


}