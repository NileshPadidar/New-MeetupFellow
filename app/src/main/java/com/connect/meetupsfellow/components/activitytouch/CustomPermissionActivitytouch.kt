package com.connect.meetupsfellow.components.activitytouch

import android.annotation.SuppressLint
import android.content.Intent
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.global.interfaces.SinchServiceConnection
import com.connect.meetupsfellow.mvp.view.activities.ChatActivity
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


@SuppressLint("Registered")
/**
 * Created by Jammwal on 07-03-2018.
 * Update by Nilu on 12_Dec_24
 */
open class CustomPermissionActivitytouch() : CustomAppActivityImpltouch(), EasyPermissions.PermissionCallbacks {

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this@CustomPermissionActivitytouch, perms)) {
            AppSettingsDialog.Builder(this@CustomPermissionActivitytouch).build().show()
        }
//        LocationModel().apply {
//            setLatitude(Constants.Default.Latitude.toDouble())
//            setLongitude(Constants.Default.Longitude.toDouble())
//            setLocationName(Constants.Default.Location)
//        }.saveLocation()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    // Tracks the bound state of the service.
    private var mBoundSinch = false

    private var sinchConnection: SinchServiceConnection? = null

    internal fun checkRequiredPermissions() {
        checkLocation()
//        checkRecordAudioPermission()
    }

    private fun checkRecordAudioPermission() {
        if (!EasyPermissions.hasPermissions(
                this@CustomPermissionActivitytouch,
                *Constants.Permissions.CALLING
            )
        ) {
            EasyPermissions.requestPermissions(
                this@CustomPermissionActivitytouch,
                String.format(
                    getString(R.string.text_rational_audio),
                    getString(R.string.app_name)
                ),
                Constants.PermissionsCode.REQUEST_PERMISSION_RECORD_AUDIO,
                *Constants.Permissions.CALLING
            )
        }
    }

    protected fun onSinchConnection(sinchConnection: SinchServiceConnection) {
        this.sinchConnection = sinchConnection
    }

    protected fun onRemoveConnection() {
        sinchConnection!!.onServiceDisconnected()
        this.sinchConnection = null
    }

    @AfterPermissionGranted(Constants.PermissionsCode.REQUEST_PERMISSION_LOCATION)
    private fun checkLocation() {
        when (isLocationEnabled(this@CustomPermissionActivitytouch)) {
            true -> {
                hasLocationPermission()
            }

            false -> {
                AlertDialog.Builder(this@CustomPermissionActivitytouch)
                    .setTitle("Location Disabled")  // GPS not found
                    .setMessage("Location Disabled") // Want to enable?
                    .setPositiveButton(
                        R.string.text_yes
                    ) { _, _ ->
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                    .setNegativeButton(R.string.text_no, null)
                    .show()
//                LocationModel().apply {
//                    setLatitude(Constants.Default.Latitude.toDouble())
//                    setLongitude(Constants.Default.Longitude.toDouble())
//                    setLocationName(Constants.Default.Location)
//                }.saveLocation()
            }
        }
    }

    internal fun fetchLocation() {
        if (EasyPermissions.hasPermissions(
                this@CustomPermissionActivitytouch,
                *Constants.Permissions.LOCATION
            )
        ) {
            initializeApp()
        }
    }

    private fun hasLocationPermission() {
        if (EasyPermissions.hasPermissions(
                this@CustomPermissionActivitytouch,
                *Constants.Permissions.LOCATION
            )
        ) {
            initializeApp()
        } else {
            EasyPermissions.requestPermissions(
                this@CustomPermissionActivitytouch,
                String.format(
                    getString(R.string.text_rational_location),
                    getString(R.string.app_name)
                ),
                Constants.PermissionsCode.REQUEST_PERMISSION_LOCATION,
                *Constants.Permissions.LOCATION
            )
        }
    }

    protected fun cameraPermission(permission: Int): Boolean {
        return when {
            EasyPermissions.hasPermissions(
                this@CustomPermissionActivitytouch,
                *Constants.Permissions.CAMERA_STORAGE
            ) -> true
            else -> {
                EasyPermissions.requestPermissions(
                    this@CustomPermissionActivitytouch,
                    String.format(
                        getString(R.string.text_rational_camera),
                        getString(R.string.app_name)
                    ),
                    permission,
                    *Constants.Permissions.CAMERA_STORAGE
                )
                false
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
            requestCode, permissions, grantResults, this@CustomPermissionActivitytouch
        )
    }

}