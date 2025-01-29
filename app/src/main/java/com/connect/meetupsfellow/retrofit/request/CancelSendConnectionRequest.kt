package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CancelSendConnectionRequest : Serializable {

    @SerializedName("receiver_id")
    @Expose
    internal var receiver_id = 0

}