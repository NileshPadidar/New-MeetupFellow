package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RequestResetPassword: Serializable {

    @SerializedName("email")
    internal var email = ""

    @SerializedName("code")
    internal var code = ""

    @SerializedName("password")
    internal var password = ""

    @SerializedName("confirm_password")
    internal var confirm_password = ""

}