package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.SerializedName

class EnableDisableNotificationReq {

    @SerializedName("notification_type_id")
    internal var notificationTypeId = 0

}