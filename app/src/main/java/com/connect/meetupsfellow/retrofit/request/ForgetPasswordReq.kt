package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ForgetPasswordReq: Serializable {

    @SerializedName("email")
    internal var email = ""

}