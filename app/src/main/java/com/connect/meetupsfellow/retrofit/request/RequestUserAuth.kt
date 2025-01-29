package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.SerializedName
import com.connect.meetupsfellow.constants.Constants
import java.io.Serializable

/**
 * Created by Jammwal on 07-03-2018.
 */
class RequestUserAuth : Serializable {

    @SerializedName("phoneNumber")
    internal var phoneNumber = ""

    @SerializedName("countryCode")
    internal var countryCode = ""

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

    @SerializedName("isoCode")
    internal var isoCode = ""

}