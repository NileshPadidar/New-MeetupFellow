package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.SerializedName

class RequestSendNotification {

    @SerializedName("otherUserName")
    internal var otherUserName = ""

    @SerializedName("otherUserId")
    internal var otherUserId = ""

    @SerializedName("senderImageUrl")
    internal var senderImageUrl = ""

    @SerializedName("senderId")
    internal var senderId = ""

    @SerializedName("receiverId")
    internal var receiverId = ""

    @SerializedName("notificationModel")
    internal var notificationModel = NotificationModel()
}