package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RequestReportEvent : Serializable {

    @SerializedName("eventId")
    @Expose
    internal var id = ""

    @SerializedName("reason")
    @Expose
    internal var reason = ""

    @SerializedName("description")
    @Expose
    internal var description = ""
}