package com.connect.meetupsfellow.service

import android.annotation.SuppressLint
import android.app.IntentService
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
import com.connect.meetupsfellow.constants.Constants
import java.io.IOException
import java.util.*
import android.os.Build
import android.os.IBinder
import android.app.NotificationManager
import android.app.NotificationChannel
import android.content.Context
import android.content.ServiceConnection
import android.graphics.Color
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_LOW
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.log.LogManager

/**
 * Created by Jammwal on 26-03-2018.
 */
class FetchAddressIntentService : IntentService("FetchAddress") {

    private val TAG = "FetchAddressService"
    /**
     * The receiver where results are forwarded from this service.
     */
    private var receiver: ResultReceiver? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val startedFromNotification = intent!!.getBooleanExtra(
            EXTRA_STARTED_FROM_NOTIFICATION,
            false
        )

        // We got here because the user decided to remove location updates from the notification.
        if (startedFromNotification) {
//            removeLocationUpdates()
            stopSelf()
        }
        return Service.START_NOT_STICKY
    }

    override fun unbindService(conn: ServiceConnection) {
        super.unbindService(conn)
        stopForeground(true)
    }

    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground()
        else
            startForeground(1, Notification())


        // Do whatever you want to do here
        LogManager.logger.i(ArchitectureApp.instance!!.tag, "Fetch Address")
    }

    @SuppressLint("ForegroundServiceType")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startMyOwnForeground() {

        Log.d("Push Notification", "ChatNotify")

        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("my_service", "My Background Service")
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }

      val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.meetupsfellow_transpatent)
            .setPriority(PRIORITY_LOW)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .build()
       startForeground(101, notification)
    }
// count NotificationChannel  setShowBadge(false)
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        .apply { setShowBadge(true) }
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    /**
     * Tries to get the location address using a Geocoder. If successful, sends an address to a
     * result receiver. If unsuccessful, sends an error message instead.
     * Note: We define a [android.os.ResultReceiver] in * MainActivity to process content
     * sent from this service.
     *
     * This service calls this method from the default worker thread with the intent that started
     * the service. When this method returns, the service automatically stops.
     */
    override fun onHandleIntent(intent: Intent?) {
//        var errorMessage = ""

        receiver = intent?.getParcelableExtra(Constants.Location.RECEIVER)
        LogManager.logger.i(ArchitectureApp.instance!!.tag, "Fetch Address")
        // Check if receiver was properly registered.
        if (intent == null || receiver == null) {
            Log.wtf(TAG, "No receiver received. There is nowhere to send the results.")
            return
        }

        // Get the location passed to this service through an extra.
        val location = intent.getParcelableExtra<Location>(Constants.Location.LOCATION_DATA_EXTRA)

        // Make sure that the location data was really sent over through an extra. If it wasn't,
        // send an error error message and return.
//        if (location == null) {
//            errorMessage = getString(R.string.no_location_data_provided)
//            Log.wtf(TAG, errorMessage)
//            deliverResultToReceiver(Constants.Location.FAILURE_RESULT, errorMessage)
//            return
//        }

        // Errors could still arise from using the Geocoder (for example, if there is no
        // connectivity, or if the Geocoder is given illegal location data). Or, the Geocoder may
        // simply not have an address for a location. In all these cases, we communicate with the
        // receiver using a resultCode indicating failure. If an address is found, we use a
        // resultCode indicating success.

        // The Geocoder used in this sample. The Geocoder's responses are localized for the given
        // Locale, which represents a specific geographical or linguistic region. Locales are used
        // to alter the presentation of information such as numbers or dates to suit the conventions
        // in the region they describe.
        val geocoder = Geocoder(this, Locale.getDefault())

        // Address found using the Geocoder.
        var addresses: List<Address> = emptyList()

        try {
            // Using getFromLocation() returns an array of Addresses for the area immediately
            // surrounding the given latitude and longitude. The results are a best guess and are
            // not guaranteed to be accurate.
            addresses = geocoder.getFromLocation(
                location!!.latitude,
                location.longitude,
                // In this sample, we get just a single address.
                1
            )!!
        } catch (ioException: IOException) {
            // Catch network or other I/O problems.
//            errorMessage = getString(R.string.service_not_available)
//            Log.e(TAG, errorMessage, ioException)
        } catch (illegalArgumentException: IllegalArgumentException) {
            // Catch invalid latitude or longitude values.
//            errorMessage = getString(R.string.invalid_lat_long_used)
//            Log.e(TAG, "$errorMessage. Latitude = $location.latitude , " +
//                    "Longitude = $location.longitude", illegalArgumentException)
        }

        // Handle case where no address was found.
        if (addresses.isEmpty()) {
//            if (errorMessage.isEmpty()) {
//                errorMessage = getString(R.string.no_address_found)
//                Log.e(TAG, errorMessage)
//            }
//            deliverResultToReceiver(Constants.Location.FAILURE_RESULT, errorMessage)
        } else {
            val address = addresses[0]
            Log.i(TAG, "Address Found. : ${address.adminArea}")

            Log.i(TAG, "Address Found. : ${address.locality}")
            deliverResultToReceiver(
                Constants.Location.SUCCESS_RESULT,
                address.adminArea,
                address.locality,
                "${address.latitude}",
                "${address.longitude}"
            )
        }
    }

    /**
     * Sends a resultCode and message to the receiver.
     */
    private fun deliverResultToReceiver(
        resultCode: Int,
        adminArea: String,
        message: String,
        lat: String,
        lng: String
    ) {
        val bundle = Bundle().apply {
            putString(Constants.Location.RESULT_DATA_KEY, message)
            putString(Constants.Location.RESULT_ADMIN_AREA, adminArea)
            putString(Constants.Location.RESULT_DATA_KEY_LATITUDE, lat)
            putString(Constants.Location.RESULT_DATA_KEY_LONGITUDE, lng)
        }
        receiver?.send(resultCode, bundle)
        stopForeground(true)
    }

    companion object {

        private const val PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationupdatesforegroundservice"

        private val TAG = LocationUpdatesService::class.java.simpleName

        private const val EXTRA_STARTED_FROM_NOTIFICATION =
            "$PACKAGE_NAME.started_from_notification"

        /**
         * The desired interval for location updates. Inexact. Updates may be more or less frequent.
         */
        private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10 * 60 * 1000

        /**
         * The fastest rate for active location updates. Updates will never be more frequent
         * than this value.
         */
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2
    }

}