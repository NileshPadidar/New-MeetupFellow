package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RequestAdminAdsCountClick {
    @SerializedName("adId")
    @Expose
    internal var adId = ""
}