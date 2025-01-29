package com.connect.meetupsfellow.global.utils

import com.connect.meetupsfellow.global.interfaces.OnDatabaseUpdatedSuccess

object DataUpdation {

    private var updation: OnDatabaseUpdatedSuccess? = null

    internal fun enableUpdation(updation: OnDatabaseUpdatedSuccess) {
        DataUpdation.updation = updation
    }

    internal fun disableUpdation() {
        updation = null
    }

    internal val onUpdation: OnDatabaseUpdatedSuccess?
        get() = updation
}