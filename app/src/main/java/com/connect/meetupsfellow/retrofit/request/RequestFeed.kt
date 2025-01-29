package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RequestFeed : Serializable {

    @SerializedName("search")
    @Expose
    internal var search = ""

    @SerializedName("page")
    @Expose
    internal var page = ""

    @SerializedName("limit")
    @Expose
    internal var limit = ""

    @SerializedName("currentLat")
    @Expose
    internal var currentLat = ""

    @SerializedName("currentLong")
    @Expose
    internal var currentLong = ""

    @SerializedName("intoIdentity")
    @Expose
    internal var intoIdentity = ""

    @SerializedName("myIdentity")
    @Expose
    internal var myIdentity = ""

    @SerializedName("partnerStatus")
    @Expose
    internal var partnerStatus = ""

    @SerializedName("gender")
    @Expose
    internal var gender = ""

    @SerializedName("generalIntercourse")
    @Expose
    internal var generalIntercourse = ""

    @SerializedName("fistingId")
    @Expose
    internal var fistingId = ""

    @SerializedName("status")
    @Expose
    internal var status = ""

    @SerializedName("searchPlace")
    @Expose
    internal var searchPlace = ""

    @SerializedName("lastLoginTimeStamp")
    @Expose
    internal var lastLoginTimeStamp = ""


    @SerializedName("id")
    @Expose
    internal var id = ""

}