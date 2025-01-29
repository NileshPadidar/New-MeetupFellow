package com.connect.meetupsfellow.retrofit.request

import com.connect.meetupsfellow.constants.Constants
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class LoginWithEmailRequest: Serializable {

    @SerializedName("email")
    internal var email = ""

    @SerializedName("password")
    internal var password = ""

    @SerializedName("deviceType")
    internal var deviceType = Constants.Device.TYPE

    @SerializedName("deviceToken")
    internal var deviceToken = ""

    @SerializedName("currentLocation")
    internal var currentLocation = ""

    @SerializedName("currentLat")
    internal var currentLat = ""

    @SerializedName("currentLong")
    internal var currentLong = ""
}