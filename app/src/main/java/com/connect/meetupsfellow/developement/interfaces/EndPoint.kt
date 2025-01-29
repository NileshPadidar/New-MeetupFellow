package com.connect.meetupsfellow.developement.interfaces

/**
 * Created by Maheshwar on 10/24/2016.
 */

interface EndPoint {

    /** The base API URL. */
    fun getUrl(): String

    /** A name for differentiating multiple API URLs */
    fun getName(): String
}
