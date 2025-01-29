package com.connect.meetupsfellow.retrofit.response

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class MyprivatealbumpermissionRes : Serializable {

    @SerializedName("access_status")
    @Expose
    var accessStatus: String? = null

    @SerializedName("userId")
    @Expose
    var userId: List<Int>? = null
}