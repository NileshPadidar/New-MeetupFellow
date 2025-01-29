package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class getConectionRequest: Serializable {
    @SerializedName("sort_by")
    @Expose
    internal var sort_by = ""
}