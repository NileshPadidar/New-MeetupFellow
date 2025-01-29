package com.connect.meetupsfellow.global.utils

import com.connect.meetupsfellow.global.interfaces.ConnectionReceiverListener


/**
 * Created by Maheshwar on 09-08-2017.
 */
object Connections {

    private var connection: ConnectionReceiverListener? = null

    fun with(notification: ConnectionReceiverListener) {
        connection = notification
    }

    fun getConnection(): ConnectionReceiverListener? = connection
}