package com.connect.meetupsfellow.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ResponseUserProfileImages : Serializable {

    @SerializedName("id")
    @Expose
    internal var id = 0

    @SerializedName("imagePath")
    @Expose
    internal var imagePath = ""

    @SerializedName("imageThumb")
    @Expose
    internal val imageThumb = ""

    @SerializedName("position")
    @Expose
    internal var position = 0

    @SerializedName("status")
    @Expose
    internal var status = ""



    internal var selected = false


}