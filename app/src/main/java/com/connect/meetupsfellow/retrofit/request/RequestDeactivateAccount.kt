package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RequestDeactivateAccount : Serializable {

    @SerializedName("status")
    @Expose
    internal var status = "1"
}