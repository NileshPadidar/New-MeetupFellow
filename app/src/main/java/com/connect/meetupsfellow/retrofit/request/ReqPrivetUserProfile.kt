package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ReqPrivetUserProfile : Serializable {
    @SerializedName("isprofilehidden")
    @Expose
    internal var isprofilehidden = 0

}