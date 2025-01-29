@file:Suppress("DEPRECATION")

package com.connect.meetupsfellow.global.utils

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsNotification
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.mvp.view.activities.SplashActivity

object GenerateNotification {

    @Suppress("SENSELESS_COMPARISON")
    @TargetApi(Build.VERSION_CODES.O)
    @SuppressLint("PrivateResource")
    internal fun sendNotification(name: String, senderId: String, callId: String, type: ConstantsNotification) {

        val pendingIntent = PendingIntent.getActivity(
            ArchitectureApp.instance, 0,
                getIntent(senderId, callId, type),
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = buildNotificationBuilder(ArchitectureApp.instance!!.applicationContext, name, type)

        notificationBuilder.setContentIntent(pendingIntent)
        notificationBuilder.priority = getPriority()
        notificationBuilder.setDefaults(Notification.DEFAULT_ALL)
        notificationBuilder.setAutoCancel(true)
        Log.d("push1", "sendNotification1: ")
        val notificationManager = ArchitectureApp.instance!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        LogManager.logger.d("DatingOnDl", "Message Notifications Manager : $notificationManager")
        if (notificationManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // channel id is required for oreo.
//                val channel = NotificationChannel(
//                    getString(R.string.default_notification_channel_id), getString(R.string.app_name),
//                        NotificationManager.IMPORTANCE_DEFAULT)
//                channel.description = getString(R.string.app_name)
//                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(0, notificationBuilder.build())
            Log.d("push1", "sendNotification1: ")
        }
    }

    private fun buildNotificationBuilder(context: Context, msg: String, type: ConstantsNotification): NotificationCompat.Builder {
        val color = ContextCompat.getColor(context, R.color.colorAccent)
        Log.d("push1", "sendNotification1: ")
        return NotificationCompat.Builder(context)
//                .setSmallIcon(R.drawable.ic_logo_white)
                .setContentTitle(getMessage(type))
                .setStyle(NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg)
//                .setChannelId(getString(R.string.default_notification_channel_id))
                .setColor(color).setAutoCancel(true)
    }

    private fun getMessage(type: ConstantsNotification): String {
        return when (type) {
            ConstantsNotification.SINCH_MESSAGE -> "New message from :"
            ConstantsNotification.SINCH_CALL -> "Call from :"
            ConstantsNotification.SINCH_CALL_MISSED -> "Missed call from :"
            else -> "Notification from :"
        }
    }

    private fun getPriority(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManager.IMPORTANCE_HIGH
        } else {
            android.app.Notification.PRIORITY_HIGH or android.app.Notification.PRIORITY_MAX
        }
    }

    private fun getString(id: Int): String {
        return ArchitectureApp.instance!!.getString(id)
    }

    private fun getIntent(userId: String, callId: String, type: ConstantsNotification): Intent {
        Log.d("push1", "sendNotification1: ")
        return Intent(ArchitectureApp.instance, SplashActivity::class.java)
                .putExtra(Constants.IntentData.NOTIFICATION, ConstantsNotification.SINCH)
                .putExtra(Constants.IntentData.NOTIFICATION_TYPE, type)
                .putExtra(Constants.IntentData.CALL_ID, callId)
                .putExtra(Constants.IntentData.ANOTHER_USER_ID, userId)
    }
}