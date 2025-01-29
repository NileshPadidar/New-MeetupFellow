package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.SerializedName
import java.io.File
import java.io.Serializable

class RequestCreateEvent : Serializable {

    @SerializedName("title")
    internal var title = ""

    @SerializedName("startDate")
    internal var start = ""

    @SerializedName("endDate")
    internal var end = ""

    @SerializedName("location")
    internal var location = ""

    @SerializedName("locationLat")
    internal var locationLat = ""

    @SerializedName("locationLong")
    internal var locationLong = ""

    @SerializedName("description")
    internal var description = ""

    @SerializedName("websiteUrl")
    internal var websiteUrl = ""

    @SerializedName("buyTicketUrl")
    internal var buyTicketUrl = ""

    @SerializedName("image")
    internal var image: File? = null

    @SerializedName("timeZone")
    internal var timeZone = ""

    @SerializedName("startTime")
    internal var startTime = ""

    @SerializedName("endTime")
    internal var endTime = ""


}