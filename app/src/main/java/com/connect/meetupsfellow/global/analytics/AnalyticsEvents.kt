package com.connect.meetupsfellow.global.analytics

object AnalyticsEvents {

    object Event {
        const val REPORT = "user_report"
        const val BLOCK = "user_block"
        const val PAYMENT = "user_payment"
        const val LOGIN = "user_login"
        const val LOGOUT = "user_logout"
        const val DELETE = "user_delete"
    }

    object Key {
        const val USER_ID = "userId"
        const val USER_NAME = "userName"
        const val DEVICE_TYPE = "deviceType"
        const val ANOTHER_USER_ID = "anotherUserId"
    }
}