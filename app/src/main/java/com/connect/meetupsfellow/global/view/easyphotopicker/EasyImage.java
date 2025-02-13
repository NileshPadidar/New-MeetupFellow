package com.connect.meetupsfellow.global.view.easyphotopicker;

import android.app.Activity;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.connect.meetupsfellow.global.view.easyphotopicker.EasyImageFiles.singleFileList;

import com.connect.meetupsfellow.constants.Constants;

/**
 * Created by Jacek Kwiecień on 16.10.2015.
 */
public class EasyImage {

    private static final boolean SHOW_GALLERY_IN_CHOOSER = false;

    public enum ImageSource {
        GALLERY, DOCUMENTS, CAMERA_IMAGE, CAMERA_VIDEO
    }

    public interface Callbacks {
        void onImagePickerError(Exception e, ImageSource source, int type);

        void onImagesPicked(@NonNull List<File> imageFiles, ImageSource source, int type);

        void onCanceled(ImageSource source, int type);
    }

    private static final String KEY_PHOTO_URI = "pl.aprilapps.easyphotopicker.photo_uri";
    private static final String KEY_VIDEO_URI = "pl.aprilapps.easyphotopicker.video_uri";
    private static final String KEY_LAST_CAMERA_PHOTO = "pl.aprilapps.easyphotopicker.last_photo";
    private static final String KEY_LAST_CAMERA_VIDEO = "pl.aprilapps.easyphotopicker.last_video";
    private static final String KEY_TYPE = "pl.aprilapps.easyphotopicker.type";

    private static Uri createCameraPictureFile(@NonNull Context context) throws IOException {
        File imagePath = EasyImageFiles.getCameraPicturesLocation(context);
        Uri uri = EasyImageFiles.getUriToFile(context, imagePath);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(KEY_PHOTO_URI, uri.toString());
        editor.putString(KEY_LAST_CAMERA_PHOTO, imagePath.toString());
        editor.apply();
        return uri;
    }

    private static Uri createCameraVideoFile(@NonNull Context context) throws IOException {
        File imagePath = EasyImageFiles.getCameraVideoLocation(context);
        Uri uri = EasyImageFiles.getUriToFile(context, imagePath);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(KEY_VIDEO_URI, uri.toString());
        editor.putString(KEY_LAST_CAMERA_VIDEO, imagePath.toString());
        editor.apply();
        return uri;
    }

    private static Intent createDocumentsIntent(@NonNull Context context, int type) {
        storeType(context, type);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        return intent;
    }


    private static Intent createGalleryIntent(@NonNull Context context, int type) {
        storeType(context, type);
        Intent intent = plainGalleryPickerIntent();
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, configuration(context).allowsMultiplePickingInGallery());
        return intent;
    }

    private static Intent createGalleryMultiplePic(@NonNull Context context, int type) {
        storeType(context, type);
        Intent intent = plainGalleryPickerIntent();
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        return intent;
    }


    private static Intent createPhotoIntent(@NonNull Context context, int type){
        storeType(context, type);
        Intent intent = plainPhotoPickerIntent();
        //intent.setType("image/*");
        //intent.putExtra("crop", "true");
        //intent.putExtra("scale", true);
        //intent.putExtra("outputX", 256);
        //intent.putExtra("outputY", 256);
        //intent.putExtra("aspectX", 1);
        //intent.putExtra("aspectY", 1);
        //intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, configuration(context).allowsMultiplePickingInGallery());
        return intent;
    }

    private static Intent createCameraForImageIntent(@NonNull Context context, int type) {
        storeType(context, type);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            Uri capturedImageUri = createCameraPictureFile(context);
            //We have to explicitly grant the write permission since Intent.setFlag works only on API Level >=20
            grantWritePermission(context, intent, capturedImageUri);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return intent;
    }

    private static Intent createCameraForVideoIntent(@NonNull Context context, int type) {
        storeType(context, type);

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        try {
            Uri capturedImageUri = createCameraVideoFile(context);
            //We have to explicitly grant the write permission since Intent.setFlag works only on API Level >=20
            grantWritePermission(context, intent, capturedImageUri);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return intent;
    }

    private static void revokeWritePermission(@NonNull Context context, Uri uri) {
        context.revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }

    private static void grantWritePermission(@NonNull Context context, Intent intent, Uri uri) {
        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    private static Intent createChooserIntent(@NonNull Context context, @Nullable String chooserTitle, int type) throws IOException {
        return createChooserIntent(context, chooserTitle, SHOW_GALLERY_IN_CHOOSER, type);
    }

    private static Intent createChooserIntent(@NonNull Context context, @Nullable String chooserTitle, boolean showGallery, int type) throws IOException {
        storeType(context, type);

        Uri outputFileUri = createCameraPictureFile(context);
        List<Intent> cameraIntents = new ArrayList<>();
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> camList = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : camList) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            grantWritePermission(context, intent, outputFileUri);
            cameraIntents.add(intent);
        }
        Intent galleryIntent;

        if (showGallery) {
            galleryIntent = createGalleryIntent(context, type);
        } else {
            galleryIntent = createDocumentsIntent(context, type);
        }

        Intent chooserIntent = Intent.createChooser(galleryIntent, chooserTitle);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        return chooserIntent;
    }

    private static void storeType(@NonNull Context context, int type) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(KEY_TYPE, type).commit();
    }

    private static int restoreType(@NonNull Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY_TYPE, 0);
    }

    public static void openChooserWithDocuments(Activity activity, @Nullable String chooserTitle, int type) {
        try {
            Intent intent = createChooserIntent(activity, chooserTitle, type);
            activity.startActivityForResult(intent, ConstantsImage.RequestCodes.Companion.getSOURCE_CHOOSER() | ConstantsImage.RequestCodes.Companion.getPICK_PICTURE_FROM_DOCUMENTS());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openChooserWithDocuments(Fragment fragment, @Nullable String chooserTitle, int type) {
        try {
            Intent intent = createChooserIntent(fragment.getActivity(), chooserTitle, type);
//            fragment.startActivityForResult(intent, ConstantsImage.RequestCodes.SOURCE_CHOOSER | ConstantsImage.RequestCodes.PICK_PICTURE_FROM_DOCUMENTS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openChooserWithDocuments(android.app.Fragment fragment, @Nullable String chooserTitle, int type) {
        try {
            Intent intent = createChooserIntent(fragment.getActivity(), chooserTitle, type);
//            fragment.startActivityForResult(intent, ConstantsImage.RequestCodes.SOURCE_CHOOSER | ConstantsImage.RequestCodes.PICK_PICTURE_FROM_DOCUMENTS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openChooserWithGallery(Activity activity, @Nullable String chooserTitle, int type) {
        try {
            Intent intent = createChooserIntent(activity, chooserTitle, true, type);
//            activity.startActivityForResult(intent, ConstantsImage.RequestCodes.SOURCE_CHOOSER | ConstantsImage.RequestCodes.PICK_PICTURE_FROM_GALLERY);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void openChooserWithGallery(Fragment fragment, @Nullable String chooserTitle, int type) {
        try {
            Intent intent = createChooserIntent(fragment.getActivity(), chooserTitle, true, type);
//            fragment.startActivityForResult(intent, ConstantsImage.RequestCodes.SOURCE_CHOOSER | ConstantsImage.RequestCodes.PICK_PICTURE_FROM_GALLERY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openChooserWithGallery(android.app.Fragment fragment, @Nullable String chooserTitle, int type) {
        try {
            Intent intent = createChooserIntent(fragment.getActivity(), chooserTitle, true, type);
//            fragment.startActivityForResult(intent, ConstantsImage.RequestCodes.SOURCE_CHOOSER | ConstantsImage.RequestCodes.PICK_PICTURE_FROM_GALLERY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openDocuments(Activity activity, int type) {
        Intent intent = createDocumentsIntent(activity, type);
//        activity.startActivityForResult(intent, ConstantsImage.RequestCodes.PICK_PICTURE_FROM_DOCUMENTS);
    }

    public static void openDocuments(Fragment fragment, int type) {
        Intent intent = createDocumentsIntent(fragment.getContext(), type);
//        fragment.startActivityForResult(intent, ConstantsImage.RequestCodes.PICK_PICTURE_FROM_DOCUMENTS);
    }

    public static void openDocuments(android.app.Fragment fragment, int type) {
        Intent intent = createDocumentsIntent(fragment.getActivity(), type);
//        fragment.startActivityForResult(intent, ConstantsImage.RequestCodes.PICK_PICTURE_FROM_DOCUMENTS);
    }

    /**
     * Opens default galery or a available galleries picker if there is no default
     *
     * @param type Custom type of your choice, which will be returned with the images
     */
    public static void openGallery(Activity activity, int type) {
        Intent intent = createGalleryIntent(activity, type);
        activity.startActivityForResult(intent, ConstantsImage.RequestCodes.Companion.getPICK_PICTURE_FROM_GALLERY());
    }

  public static void openGalleryMultiImage(Activity activity, int type) {
        Intent intent = createGalleryMultiplePic(activity, type);
        activity.startActivityForResult(intent, ConstantsImage.RequestCodes.Companion.getPICK_PICTURE_FROM_GALLERY());
    }

    public static void openPhoto(Activity activity, int type){
        Intent intent = createPhotoIntent(activity, type);
        activity.startActivityForResult(intent, ConstantsImage.RequestCodes.Companion.getPICK_PICTURE_FROM_GALLERY());
    }

    /**
     * Opens default galery or a available galleries picker if there is no default
     *
     * @param type Custom type of your choice, which will be returned with the images
     */
    public static void openGallery(Fragment fragment, int type) {
        Intent intent = createGalleryIntent(fragment.getContext(), type);
//        fragment.startActivityForResult(intent, ConstantsImage.RequestCodes.PICK_PICTURE_FROM_GALLERY);
    }

    /**
     * Opens default galery or a available galleries picker if there is no default
     *
     * @param type Custom type of your choice, which will be returned with the images
     */
    public static void openGallery(android.app.Fragment fragment, int type) {
        Intent intent = createGalleryIntent(fragment.getActivity(), type);
//        fragment.startActivityForResult(intent, ConstantsImage.RequestCodes.PICK_PICTURE_FROM_GALLERY);
    }

    public static void openCameraForImage(Activity activity, int type) {
        Intent intent = createCameraForImageIntent(activity, type);
        //activity.startActivityForResult(intent, ConstantsImage.RequestCodes.Companion.getTAKE_PICTURE());
        activity.startActivityForResult(intent, Constants.ImagePicker.CAMERA);
    }

    public static void openCameraForImage(Fragment fragment, int type) {
        Intent intent = createCameraForImageIntent(fragment.getActivity(), type);
//        fragment.startActivityForResult(intent, ConstantsImage.RequestCodes.TAKE_PICTURE);
    }

    public static void openCameraForImage(android.app.Fragment fragment, int type) {
        Intent intent = createCameraForImageIntent(fragment.getActivity(), type);
//        fragment.startActivityForResult(intent, ConstantsImage.RequestCodes.TAKE_PICTURE);
    }

    public static void openCameraForVideo(Activity activity, int type) {
        Intent intent = createCameraForVideoIntent(activity, type);
//        activity.startActivityForResult(intent, ConstantsImage.RequestCodes.CAPTURE_VIDEO);
    }

    public static void openCameraForVideo(Fragment fragment, int type) {
        Intent intent = createCameraForVideoIntent(fragment.getActivity(), type);
//        fragment.startActivityForResult(intent, ConstantsImage.RequestCodes.CAPTURE_VIDEO);
    }

    public static void openCameraForVideo(android.app.Fragment fragment, int type) {
        Intent intent = createCameraForVideoIntent(fragment.getActivity(), type);
//        fragment.startActivityForResult(intent, ConstantsImage.RequestCodes.CAPTURE_VIDEO);
    }

    @Nullable
    private static File takenCameraPicture(Context context) {
        String lastCameraPhoto = PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_LAST_CAMERA_PHOTO, null);
        if (lastCameraPhoto != null) {
            return new File(lastCameraPhoto);
        } else {
            return null;
        }
    }

    @Nullable
    private static File takenCameraVideo(Context context) {
        String lastCameraPhoto = PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_LAST_CAMERA_VIDEO, null);
        if (lastCameraPhoto != null) {
            return new File(lastCameraPhoto);
        } else {
            return null;
        }
    }

    public static void handleActivityResult(int requestCode, int resultCode, Intent data, Activity activity, @NonNull Callbacks callbacks) {
        boolean isEasyImage = (requestCode & ConstantsImage.RequestCodes.Companion.getEASYIMAGE_IDENTIFICATOR()) > 0;
        if (isEasyImage) {
            requestCode &= ~ConstantsImage.RequestCodes.Companion.getSOURCE_CHOOSER();
            if (requestCode == ConstantsImage.RequestCodes.Companion.getPICK_PICTURE_FROM_GALLERY() ||
                    requestCode == ConstantsImage.RequestCodes.Companion.getTAKE_PICTURE() ||
                    requestCode == ConstantsImage.RequestCodes.Companion.getCAPTURE_VIDEO() ||
                    requestCode == ConstantsImage.RequestCodes.Companion.getPICK_PICTURE_FROM_DOCUMENTS()) {
                if (resultCode == Activity.RESULT_OK) {
                    if (requestCode == ConstantsImage.RequestCodes.Companion.getPICK_PICTURE_FROM_DOCUMENTS() && !isPhoto(data)) {
                        onPictureReturnedFromDocuments(data, activity, callbacks);
                    } else if (requestCode == ConstantsImage.RequestCodes.Companion.getPICK_PICTURE_FROM_GALLERY() && !isPhoto(data)) {
                        onPictureReturnedFromGallery(data, activity, callbacks);
                    } else if (requestCode == ConstantsImage.RequestCodes.Companion.getTAKE_PICTURE()) {
                        onPictureReturnedFromCamera(activity, callbacks);
                    } else if (requestCode == ConstantsImage.RequestCodes.Companion.getCAPTURE_VIDEO()) {
                        onVideoReturnedFromCamera(activity, callbacks);
                    } else if (isPhoto(data)) {
                        onPictureReturnedFromCamera(activity, callbacks);
                    } else {
                        onPictureReturnedFromDocuments(data, activity, callbacks);
                    }
                } else {
                    if (requestCode == ConstantsImage.RequestCodes.Companion.getPICK_PICTURE_FROM_DOCUMENTS()) {
                        callbacks.onCanceled(ImageSource.DOCUMENTS, restoreType(activity));
                    } else if (requestCode == ConstantsImage.RequestCodes.Companion.getPICK_PICTURE_FROM_GALLERY()) {
                        callbacks.onCanceled(ImageSource.GALLERY, restoreType(activity));
                    } else {
                        callbacks.onCanceled(ImageSource.CAMERA_IMAGE, restoreType(activity));
                    }
                }
            }
        }
    }

    private static boolean isPhoto(Intent data) {
        return data == null || (data.getData() == null && data.getClipData() == null);
    }

    public static boolean willHandleActivityResult(int requestCode, int resultCode, Intent data) {
        return true;
//        return requestCode == ConstantsImage.RequestCodes.SOURCE_CHOOSER || requestCode == ConstantsImage.RequestCodes.PICK_PICTURE_FROM_GALLERY || requestCode == ConstantsImage.RequestCodes.TAKE_PICTURE || requestCode == ConstantsImage.RequestCodes.PICK_PICTURE_FROM_DOCUMENTS;
    }

    private static Intent plainGalleryPickerIntent() {
        return new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }

    private static Intent plainPhotoPickerIntent(){
        return new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }

    public static boolean canDeviceHandleGallery(@NonNull Context context) {
        return plainGalleryPickerIntent().resolveActivity(context.getPackageManager()) != null;
    }

    /**
     * @param context context
     * @return File containing lastly taken (using camera) photo. Returns null if there was no photo taken or it doesn't exist anymore.
     */
    public static File lastlyTakenButCanceledPhoto(@NonNull Context context) {
        String filePath = PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_LAST_CAMERA_PHOTO, null);
        if (filePath == null) return null;
        File file = new File(filePath);
        if (file.exists()) {
            return file;
        } else {
            return null;
        }
    }

    public static File lastlyTakenButCanceledVideo(@NonNull Context context) {
        String filePath = PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_LAST_CAMERA_VIDEO, null);
        if (filePath == null) return null;
        File file = new File(filePath);
        if (file.exists()) {
            return file;
        } else {
            return null;
        }
    }

    private static void onPictureReturnedFromDocuments(Intent data, Activity activity, @NonNull Callbacks callbacks) {
        try {
            Uri photoPath = data.getData();
            File photoFile = EasyImageFiles.pickedExistingPicture(activity, photoPath);
            callbacks.onImagesPicked(singleFileList(photoFile), ImageSource.DOCUMENTS, restoreType(activity));

            if (configuration(activity).shouldCopyPickedImagesToPublicGalleryAppFolder()) {
                EasyImageFiles.copyFilesInSeparateThread(activity, singleFileList(photoFile));
            }
        } catch (Exception e) {
            e.printStackTrace();
            callbacks.onImagePickerError(e, ImageSource.DOCUMENTS, restoreType(activity));
        }
    }

    private static void onPictureReturnedFromGallery(Intent data, Activity activity, @NonNull Callbacks callbacks) {
        try {
            ClipData clipData = data.getClipData();
            List<File> files = new ArrayList<>();
            if (clipData == null) {
                Uri uri = data.getData();
                File file = EasyImageFiles.pickedExistingPicture(activity, uri);
                files.add(file);
            } else {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri uri = clipData.getItemAt(i).getUri();
                    File file = EasyImageFiles.pickedExistingPicture(activity, uri);
                    files.add(file);
                }
            }

            if (configuration(activity).shouldCopyPickedImagesToPublicGalleryAppFolder()) {
                EasyImageFiles.copyFilesInSeparateThread(activity, files);
            }

            callbacks.onImagesPicked(files, ImageSource.GALLERY, restoreType(activity));
        } catch (Exception e) {
            e.printStackTrace();
            callbacks.onImagePickerError(e, ImageSource.GALLERY, restoreType(activity));
        }
    }

    private static void onPictureReturnedFromCamera(Activity activity, @NonNull Callbacks callbacks) {
        try {
            String lastImageUri = PreferenceManager.getDefaultSharedPreferences(activity).getString(KEY_PHOTO_URI, null);
            if (!TextUtils.isEmpty(lastImageUri)) {
                revokeWritePermission(activity, Uri.parse(lastImageUri));
            }

            File photoFile = EasyImage.takenCameraPicture(activity);
            List<File> files = new ArrayList<>();
            files.add(photoFile);

            if (photoFile == null) {
                Exception e = new IllegalStateException("Unable to get the picture returned from camera");
                callbacks.onImagePickerError(e, ImageSource.CAMERA_IMAGE, restoreType(activity));
            } else {
                if (configuration(activity).shouldCopyTakenPhotosToPublicGalleryAppFolder()) {
                    EasyImageFiles.copyFilesInSeparateThread(activity, singleFileList(photoFile));
                }

                callbacks.onImagesPicked(files, ImageSource.CAMERA_IMAGE, restoreType(activity));
            }

            PreferenceManager.getDefaultSharedPreferences(activity)
                    .edit()
                    .remove(KEY_LAST_CAMERA_PHOTO)
                    .remove(KEY_PHOTO_URI)
                    .apply();
        } catch (Exception e) {
            e.printStackTrace();
            callbacks.onImagePickerError(e, ImageSource.CAMERA_IMAGE, restoreType(activity));
        }
    }

    private static void onVideoReturnedFromCamera(Activity activity, @NonNull Callbacks callbacks) {
        try {
            String lastVideoUri = PreferenceManager.getDefaultSharedPreferences(activity).getString(KEY_VIDEO_URI, null);
            if (!TextUtils.isEmpty(lastVideoUri)) {
                revokeWritePermission(activity, Uri.parse(lastVideoUri));
            }

            File photoFile = EasyImage.takenCameraVideo(activity);
            List<File> files = new ArrayList<>();
            files.add(photoFile);

            if (photoFile == null) {
                Exception e = new IllegalStateException("Unable to get the video returned from camera");
                callbacks.onImagePickerError(e, ImageSource.CAMERA_VIDEO, restoreType(activity));
            } else {
                if (configuration(activity).shouldCopyTakenPhotosToPublicGalleryAppFolder()) {
                    EasyImageFiles.copyFilesInSeparateThread(activity, singleFileList(photoFile));
                }

                callbacks.onImagesPicked(files, ImageSource.CAMERA_VIDEO, restoreType(activity));
            }

            PreferenceManager.getDefaultSharedPreferences(activity)
                    .edit()
                    .remove(KEY_LAST_CAMERA_VIDEO)
                    .remove(KEY_VIDEO_URI)
                    .apply();
        } catch (Exception e) {
            e.printStackTrace();
            callbacks.onImagePickerError(e, ImageSource.CAMERA_VIDEO, restoreType(activity));
        }
    }


    /**
     * Method to clear configuration. Would likely be used in onDestroy(), onDestroyView()...
     *
     * @param context context
     */
    public static void clearConfiguration(@NonNull Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .remove(ConstantsImage.BundleKeys.Companion.getFOLDER_NAME())
                .remove(ConstantsImage.BundleKeys.Companion.getALLOW_MULTIPLE())
                .remove(ConstantsImage.BundleKeys.Companion.getCOPY_TAKEN_PHOTOS())
                .remove(ConstantsImage.BundleKeys.Companion.getCOPY_PICKED_IMAGES())
                .apply();
    }

    public static EasyImageConfiguration configuration(@NonNull Context context) {
        return new EasyImageConfiguration(context);
    }
}
