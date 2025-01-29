package com.connect.meetupsfellow.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ResponseFlagReason : Serializable {

    @SerializedName("id")
    @Expose
    internal var id = 0

    @SerializedName("reason_flag")
    @Expose
    internal var reason_flag = ""

    @SerializedName("current_position")
    @Expose
    internal var current_position = 0

    @SerializedName("created_at")
    @Expose
    internal var created_at = ""

    @SerializedName("updated_at")
    @Expose
    internal var updated_at = ""

    internal var selected = false

}