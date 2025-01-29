package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RequestUserBlock : Serializable {

    @SerializedName("blockUserid")
    @Expose
    internal var blockUserid = ""

    @SerializedName("status")
    @Expose
    internal var status = ""

}