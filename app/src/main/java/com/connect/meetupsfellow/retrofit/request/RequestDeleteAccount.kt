package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RequestDeleteAccount : Serializable {

    @SerializedName("accountDelete")
    @Expose
    internal var accountDelete = "1"
}