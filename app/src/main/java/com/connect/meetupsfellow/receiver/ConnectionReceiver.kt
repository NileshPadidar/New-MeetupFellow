package com.connect.meetupsfellow.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
//import com.oit.datingondl.application.ArchitectureApp
import com.connect.meetupsfellow.global.utils.Connections
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.developement.interfaces.FirebaseUtil
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import javax.inject.Inject


@Suppress("DEPRECATED_IDENTITY_EQUALS")
/**
 * Created by Maheshwar on 08-08-2017.
 */
class ConnectionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    init {
        ArchitectureApp.component!!.inject(this@ConnectionReceiver)
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, arg1: Intent) {
        isNetworkAvailable()
    }

    private fun isNetworkAvailable() {
        if (isInternetConnected()) {
            if (Connections.getConnection() != null) {
                Connections.getConnection()!!.onNetworkConnectionChanged(true)
            }
            Handler(Looper.getMainLooper()).postDelayed({
                firebaseUtil.updateUserStatus(sharedPreferencesUtil.userId(), false)
                firebaseUtil.updateUserStatus(sharedPreferencesUtil.userId(), true)
            }, 100)
        } else {
            if (Connections.getConnection() != null) {
                Connections.getConnection()!!.onNetworkConnectionChanged(false)
            }
        }
    }

    private fun isInternetConnected(): Boolean {
        return if (isAirplaneModeOn()) {
            false
        } else {
            val connectivity =
                ArchitectureApp.instance!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = connectivity.activeNetworkInfo
            info != null && info.isConnected
        }
    }

    @Suppress("DEPRECATION")
    private fun isAirplaneModeOn(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Settings.System.getInt(
                ArchitectureApp.instance!!.contentResolver,
                Settings.Global.AIRPLANE_MODE_ON,
                0
            ) !== 0
        } else {
            Settings.System.getInt(
                ArchitectureApp.instance!!.contentResolver,
                Settings.System.AIRPLANE_MODE_ON,
                0
            ) !== 0
        }
    }

}