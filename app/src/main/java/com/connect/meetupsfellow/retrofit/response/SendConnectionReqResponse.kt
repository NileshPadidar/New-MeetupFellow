package com.connect.meetupsfellow.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SendConnectionReqResponse : Serializable {

    @SerializedName("sender_id")
    @Expose
    internal val sender_id = 0

    @SerializedName("receiver_id")
    @Expose
    internal val receiver_id = 0

    @SerializedName("connection_status")
    @Expose
    internal val connection_status = ""

    @SerializedName("direct_message")
    @Expose
    internal val direct_message = ""


}