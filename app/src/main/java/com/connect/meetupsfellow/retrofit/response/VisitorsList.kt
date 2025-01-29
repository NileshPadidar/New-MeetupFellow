package com.connect.meetupsfellow.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class VisitorsList : Serializable {

    @SerializedName("visitor_id")
    @Expose
    var visitorId: String? = null

    @SerializedName("visit_date")
    @Expose
    var visitDate: String? = null

    @SerializedName("visitor_name")
    @Expose
    var visitorName: String? = null

    @SerializedName("imagePath")
    @Expose
    var imagePath: String? = null

    @SerializedName("imageThumb")
    @Expose
    var imageThumb: String? = null
}