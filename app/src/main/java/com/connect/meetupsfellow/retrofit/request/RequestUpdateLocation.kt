package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RequestUpdateLocation: Serializable {

    @SerializedName("currentLat")
    internal var latitude = 0.0

    @SerializedName("currentLong")
    internal var longitude = 0.0

    @SerializedName("currentLocation")
    internal var locationName = ""
}