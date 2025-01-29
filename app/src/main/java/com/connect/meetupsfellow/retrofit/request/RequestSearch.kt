package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RequestSearch : Serializable {

    @SerializedName("search")
    @Expose
    internal var search = ""

    @SerializedName("page")
    @Expose
    internal var page = ""

    @SerializedName("limit")
    @Expose
    internal var limit = ""

    @SerializedName("currentLat")
    @Expose
    internal var currentLat = ""

    @SerializedName("currentLong")
    @Expose
    internal var currentLong = ""
}