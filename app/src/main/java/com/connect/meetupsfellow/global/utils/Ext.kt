package com.connect.meetupsfellow.global.utils

import android.content.res.Resources

val Float.px: Float get() = (this * Resources.getSystem().displayMetrics.density)

val Int.px: Int get() = ((this * Resources.getSystem().displayMetrics.density).toInt())