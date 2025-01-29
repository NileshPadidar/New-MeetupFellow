package com.connect.meetupsfellow.log

import android.util.Log

/**
 * class that holds the [Logger] for this library, defaults to [LoggerDefault] to send logs to android [Log]
 */
object LogManager {

    var logger = LoggerDefault()

}
