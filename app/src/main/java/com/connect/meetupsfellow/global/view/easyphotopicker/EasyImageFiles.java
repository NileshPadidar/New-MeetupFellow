package com.connect.meetupsfellow.global.view.easyphotopicker;

import android.content.ContentResolver;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static com.connect.meetupsfellow.global.view.easyphotopicker.EasyImage.configuration;


/**
 * Created by Jacek Kwiecień on 14.12.15.
 */
class EasyImageFiles  {

    private static String getFolderName(@NonNull Context context) {
        return configuration(context).getFolderName();
    }

    private static File tempImageDirectory(@NonNull Context context) {
        File privateTempDir = new File(context.getCacheDir(), ConstantsImage.Companion.getDEFAULT_FOLDER_NAME());
        if (!privateTempDir.exists()) privateTempDir.mkdirs();
        return privateTempDir;
    }

    private static void writeToFile(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void copyFile(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        writeToFile(in, dst);
    }

    public static void copyFilesInSeparateThread(final Context context, final List<File> filesToCopy) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<File> copiedFiles = new ArrayList<>();
                int i = 1;
                for (File fileToCopy : filesToCopy) {
                    File dstDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getFolderName(context));
                    if (!dstDir.exists()) dstDir.mkdirs();

                    String[] filenameSplit = fileToCopy.getName().split("\\.");
                    String extension = "." + filenameSplit[filenameSplit.length - 1];
                    String filename = String.format("IMG_%s_%d.%s", new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()), i, extension);

                    File dstFile = new File(dstDir, filename);
                    try {
                        dstFile.createNewFile();
                        copyFile(fileToCopy, dstFile);
                        copiedFiles.add(dstFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    i++;
                }
                scanCopiedImages(context, copiedFiles);
            }
        }).run();
    }

    static List<File> singleFileList(File file) {
        List<File> list = new ArrayList<>();
        list.add(file);
        return list;
    }

    static void scanCopiedImages(Context context, List<File> copiedImages) {
        String[] paths = new String[copiedImages.size()];
        for (int i = 0; i < copiedImages.size(); i++) {
            paths[i] = copiedImages.get(i).toString();
        }

        MediaScannerConnection.scanFile(context,
                paths, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.d(getClass().getSimpleName(), "Scanned " + path + ":");
                        Log.d(getClass().getSimpleName(), "-> uri=" + uri);
                    }
                });
    }

    public static File pickedExistingPicture(@NonNull Context context, Uri photoUri) throws IOException {
        InputStream pictureInputStream = context.getContentResolver().openInputStream(photoUri);
        File directory = tempImageDirectory(context);
        File photoFile = new File(directory, UUID.randomUUID().toString() + "." + getMimeType(context, photoUri));
        photoFile.createNewFile();
        writeToFile(pictureInputStream, photoFile);
        return photoFile;
    }

    public static File getCameraPicturesLocation(@NonNull Context context) throws IOException {
        File dir = tempImageDirectory(context);
        return File.createTempFile(UUID.randomUUID().toString(), ".jpg", dir);
    }

    public static File getCameraVideoLocation(@NonNull Context context) throws IOException {
        File dir = tempImageDirectory(context);
        return File.createTempFile(UUID.randomUUID().toString(), ".mp4", dir);
    }

    /**
     * To find out the extension of required object in given uri
     * Solution by http://stackoverflow.com/a/36514823/1171484
     */
    private static String getMimeType(@NonNull Context context, @NonNull Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }

    public static Uri getUriToFile(@NonNull Context context, @NonNull File file) {
        String packageName = context.getApplicationContext().getPackageName();
        String authority = packageName + ".easyphotopicker.fileprovider";
        return FileProvider.getUriForFile(context, authority, file);
    }

}
