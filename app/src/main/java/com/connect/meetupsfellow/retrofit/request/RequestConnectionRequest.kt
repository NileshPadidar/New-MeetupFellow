package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RequestConnectionRequest : Serializable {

    @SerializedName("sender_id")
    @Expose
    internal var sender_id = 0

}