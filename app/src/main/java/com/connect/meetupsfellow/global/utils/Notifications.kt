package com.connect.meetupsfellow.global.utils

import com.connect.meetupsfellow.global.interfaces.OnNotificationReceived

/**
 * Created by xeemu on 13-07-2017.
 */
object Notifications {

    private var notification: OnNotificationReceived? = null

    fun with(notification: OnNotificationReceived) {
        Notifications.notification = notification
    }

    fun getNotification() = notification

}