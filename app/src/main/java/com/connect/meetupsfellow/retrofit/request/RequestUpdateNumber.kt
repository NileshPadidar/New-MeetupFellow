package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RequestUpdateNumber : Serializable {

    @SerializedName("phoneNumber")
    @Expose
    internal var number = ""

    @SerializedName("countryCode")
    @Expose
    internal var countryCode = ""

    @SerializedName("isoCode")
    @Expose
    internal var isoCode = ""

}