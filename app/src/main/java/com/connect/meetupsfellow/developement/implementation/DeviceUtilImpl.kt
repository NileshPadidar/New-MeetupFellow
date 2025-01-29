package com.connect.meetupsfellow.developement.implementation

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import com.connect.meetupsfellow.developement.interfaces.DeviceUtil

/**
 * Created by Maheshwar on 04-09-2017.
 */
class DeviceUtilImpl(private val context: Context) : DeviceUtil {
    @SuppressLint("HardwareIds")
    override fun getDeviceId(): String {
        return Settings.Secure.getString(context.contentResolver,
                Settings.Secure.ANDROID_ID)
    }
}