package com.connect.meetupsfellow.global.view.easyphotopicker;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Jacek Kwiecień on 22.12.2016.
 */

public class EasyImageConfiguration {

    private Context context;

    EasyImageConfiguration(Context context) {
        this.context = context;
    }

    public EasyImageConfiguration setImagesFolderName(String folderName) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putString(ConstantsImage.BundleKeys.Companion.getFOLDER_NAME(), folderName)
                .apply();
        return this;
    }

    public EasyImageConfiguration setAllowMultiplePickInGallery(boolean allowMultiple) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(ConstantsImage.BundleKeys.Companion.getALLOW_MULTIPLE(), allowMultiple)
                .apply();
        return this;
    }

    public EasyImageConfiguration setCopyTakenPhotosToPublicGalleryAppFolder(boolean copy) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(ConstantsImage.BundleKeys.Companion.getCOPY_TAKEN_PHOTOS(), copy)
                .apply();
        return this;
    }

    public EasyImageConfiguration setCopyPickedImagesToPublicGalleryAppFolder(boolean copy) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(ConstantsImage.BundleKeys.Companion.getCOPY_PICKED_IMAGES(), copy)
                .apply();
        return this;
    }

    public String getFolderName() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(ConstantsImage.BundleKeys.Companion.getFOLDER_NAME(), ConstantsImage.Companion.getDEFAULT_FOLDER_NAME());
    }

    public boolean allowsMultiplePickingInGallery() {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(ConstantsImage.BundleKeys.Companion.getALLOW_MULTIPLE(), false);
    }

    public boolean shouldCopyTakenPhotosToPublicGalleryAppFolder() {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(ConstantsImage.BundleKeys.Companion.getCOPY_TAKEN_PHOTOS(), false);
    }

    public boolean shouldCopyPickedImagesToPublicGalleryAppFolder() {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(ConstantsImage.BundleKeys.Companion.getCOPY_PICKED_IMAGES(), false);
    }

}
