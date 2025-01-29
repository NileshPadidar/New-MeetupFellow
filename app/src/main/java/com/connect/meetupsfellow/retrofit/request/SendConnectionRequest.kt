package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SendConnectionRequest : Serializable {
    @SerializedName("receiver_id")
    @Expose
    internal var receiver_id = 0

    @SerializedName("is_direct_message")
    @Expose
    internal var is_direct_message = 0

    @SerializedName("direct_message")
    @Expose
    internal var direct_message = ""
}