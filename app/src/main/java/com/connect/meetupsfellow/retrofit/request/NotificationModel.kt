package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.SerializedName

class NotificationModel {

    @SerializedName("thread-id")
    internal var thread = "chat_reegür"

    @SerializedName("priority")
    internal var priority = "high"

    @SerializedName("tod")
    internal var to = ""

    @SerializedName("content_available")
    internal var content_available = true

    @SerializedName("notification")
    internal var notification = Notification()

    @SerializedName("data")
    internal var data = Data()

}