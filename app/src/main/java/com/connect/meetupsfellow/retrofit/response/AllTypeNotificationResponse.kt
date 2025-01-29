package com.connect.meetupsfellow.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class AllTypeNotificationResponse: Serializable {

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("value")
    @Expose
    var value: String? = null

    @SerializedName("is_check")
    @Expose
    var isCheck: Int? = null

}