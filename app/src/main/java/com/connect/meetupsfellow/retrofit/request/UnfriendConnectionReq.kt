package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UnfriendConnectionReq : Serializable {

    @SerializedName("user_id")
    @Expose
    internal var user_id = 0

}