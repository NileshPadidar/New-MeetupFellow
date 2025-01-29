package com.connect.meetupsfellow.global.interfaces

import com.google.firebase.messaging.RemoteMessage

/**
 * Created by Maheshwar on 13-07-2017.
 */
interface OnNotificationReceived {
    fun onNotification(remoteMessage: RemoteMessage)

}