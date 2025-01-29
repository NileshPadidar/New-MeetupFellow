package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RequestReadNotification : Serializable {

    @SerializedName("notificationId")
    @Expose
    internal var notificationId = ""
}