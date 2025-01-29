package com.connect.meetupsfellow.global.interfaces

import com.connect.meetupsfellow.constants.ProfileStatus

/**
 * Created by Jammwal on 28-02-2018.
 */
interface ProfileListener {
    fun onProfile(status: ProfileStatus, link: String)
}
