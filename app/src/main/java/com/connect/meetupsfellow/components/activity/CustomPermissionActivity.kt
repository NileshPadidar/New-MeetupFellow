package com.connect.meetupsfellow.components.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.global.interfaces.SinchServiceConnection
import com.connect.meetupsfellow.global.view.easyphotopicker.EasyImage

import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


@SuppressLint("Registered")
/**
 * Created by Jammwal on 07-03-2018.
 * Update by Nilu on 12_Dec_24
 */
open class CustomPermissionActivity : CustomAppActivityImpl(), EasyPermissions.PermissionCallbacks {

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this@CustomPermissionActivity, perms)) {
            AppSettingsDialog.Builder(this@CustomPermissionActivity).build().show()
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
                this@CustomPermissionActivity,
                *Constants.Permissions.CALLING
            )
        ) {
            EasyPermissions.requestPermissions(
                this@CustomPermissionActivity,
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
        when (isLocationEnabled(this@CustomPermissionActivity)) {
            true -> {
                hasLocationPermission()
            }

            false -> {

                val dialog = Dialog(this@CustomPermissionActivity)

                dialog.setCancelable(true)

                val view = layoutInflater.inflate(R.layout.custom_location_dialog, null)

                dialog.setContentView(view)

                dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation

                if (dialog.window != null) {
                    dialog.window!!.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
                dialog.window!!.setGravity(Gravity.CENTER)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                val noBtn = view.findViewById<Button>(R.id.noLocBtn)
                val yesBtn = view.findViewById<Button>(R.id.yesLocBtn)

                noBtn.setOnClickListener {

                    dialog.dismiss()
                }

                yesBtn.setOnClickListener {

                    dialog.dismiss()
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }

                dialog.show()

                /*AlertDialog.Builder(this@CustomPermissionActivity)
                    .setTitle("Location Disabled")  // GPS not found
                    .setMessage("Location Disabled") // Want to enable?
                    .setPositiveButton(
                        R.string.text_yes
                    ) { _, _ ->
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                    .setNegativeButton(R.string.text_no, null)
                    .show()*/
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
                this@CustomPermissionActivity,
                *Constants.Permissions.LOCATION
            )
        ) {
            initializeApp()
        }
    }

    private fun hasLocationPermission() {
        if (EasyPermissions.hasPermissions(
                this@CustomPermissionActivity,
                *Constants.Permissions.LOCATION
            )
        ) {
            initializeApp()
        } else {
            EasyPermissions.requestPermissions(
                this@CustomPermissionActivity,
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
                this@CustomPermissionActivity,
                *Constants.Permissions.CAMERA_STORAGE
            ) -> true
            else -> {
                EasyPermissions.requestPermissions(
                    this@CustomPermissionActivity,
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

  /*  override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
            requestCode, permissions, grantResults, this@CustomPermissionActivity
        )
    }*/


    protected fun hasCameraPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    protected fun requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                100
            )
        }
    }

    protected fun hasGalleryPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    protected fun requestGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /*ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                101
            )*/

            ActivityCompat.requestPermissions(
                this, arrayOf<String>(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 101
            )
        }else{
            Log.e("Click$%","Gallery___Request")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
            requestCode, permissions, grantResults, this@CustomPermissionActivity
        )
        when (requestCode) {
            100 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Camera permission granted
                } else {
                    // Camera permission denied
                }
            }

            101 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Gallery permission granted
                    Log.e("Click$%","Gallery___GRanted")
                } else {
                    // Gallery permission denied
                    Log.e("Click$%","Gallery___Denied")
                     EasyImage.openGallery(
                    this,
                    //ConstantsImage.RequestCodes.PICK_PICTURE_FROM_GALLERY
                    Constants.ImagePicker.Image
                )
                }
            }

            // Handle other permissions if needed
        }
    }
}



