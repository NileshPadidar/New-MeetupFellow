package com.connect.meetupsfellow.log

import android.util.Log
import com.connect.meetupsfellow.BuildConfig

/**
 * Helper class to redirect [LogManager.logger] to [Log]
 */
class LoggerDefault : Logger {

    override fun v(tag: String, msg: String) {
        when (BuildConfig.DEBUG) {
            true -> {
                Log.v(tag, msg)
            }

            false -> {}
        }
    }

    override fun v(tag: String, msg: String, tr: Throwable) {
        when (BuildConfig.DEBUG) {
            true -> {
                Log.v(tag, msg, tr)
            }

            false -> {}
        }
    }

    override fun d(tag: String, msg: String) {
        when (BuildConfig.DEBUG) {
            true -> {
                Log.d(tag, msg)
            }

            false -> {}
        }
    }

    override fun d(tag: String, msg: String, tr: Throwable) {
        when (BuildConfig.DEBUG) {
            true -> {
                Log.d(tag, msg, tr)
            }

            false -> {}
        }
    }

    override fun i(tag: String, msg: String) {
        when (BuildConfig.DEBUG) {
            true -> {
                Log.i(tag, msg)
            }

            false -> {}
        }
        Log.i(tag, msg)
    }

    override fun i(tag: String, msg: String, tr: Throwable) {
        when (BuildConfig.DEBUG) {
            true -> {
                Log.i(tag, msg, tr)
            }

            false -> {}
        }
    }

    override fun w(tag: String, msg: String) {
        when (BuildConfig.DEBUG) {
            true -> {
                Log.w(tag, msg)
            }

            false -> {}
        }
    }

    override fun w(tag: String, msg: String, tr: Throwable) {
        when (BuildConfig.DEBUG) {
            true -> {
                Log.w(tag, msg, tr)
            }

            false -> {}
        }
    }

    override fun e(tag: String, msg: String) {
        when (BuildConfig.DEBUG) {
            true -> {
                Log.e(tag, msg)
            }

            false -> { Log.d(tag, msg)}
        }
    }

    override fun e(tag: String, msg: String, tr: Throwable) {
        when (BuildConfig.DEBUG) {
            true -> {
                Log.e(tag, msg, tr)
            }

            false -> {}
        }
    }

}