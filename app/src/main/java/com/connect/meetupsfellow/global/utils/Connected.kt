package com.connect.meetupsfellow.global.utils

/**
 * Created by Maheshwar on 09-08-2017.
 */
object Connected {

    private var connection = false
    fun connected(connection: Boolean) {
        Connected.connection = connection
    }

    fun isConnected(): Boolean = connection

}