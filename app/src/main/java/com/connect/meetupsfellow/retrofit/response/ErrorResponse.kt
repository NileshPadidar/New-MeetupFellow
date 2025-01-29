package com.connect.meetupsfellow.retrofit.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ErrorResponse : Serializable {

    @SerializedName("error_description", alternate = ["message"])
    internal var error_description = ""

    @SerializedName("error")
    internal var error: String? = null

    @SerializedName("errors")
    internal val errors = Error()

}