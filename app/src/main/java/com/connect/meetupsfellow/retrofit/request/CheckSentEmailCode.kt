package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CheckSentEmailCode: Serializable {

    @SerializedName("email")
    internal var email = ""

    @SerializedName("code")
    internal var code = ""

}