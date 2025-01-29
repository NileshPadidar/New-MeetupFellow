package com.connect.meetupsfellow.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ResponsePrivatePics : Serializable {

    @SerializedName("id")
    @Expose
    internal val id = -1

    @SerializedName("userId")
    @Expose
    internal val userId = -1

    @SerializedName("path")
    @Expose
    internal var path = ""

    @SerializedName("video_thumbnail")
    @Expose
    internal val video_thumbnail = ""

    @SerializedName("type")
    @Expose
    internal var type = "add"

    @SerializedName("visibility")
    @Expose
    internal val visibility = ""

    @SerializedName("position")
    @Expose
    internal val position = -1

    @SerializedName("status")
    @Expose
    internal val status = ""

    internal var selected = false

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + path.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (javaClass != other.javaClass)
            return false
        val obj = other as ResponsePrivatePics
        if (path == "") {
            if (obj.path != "")
                return false
        } else if (path != obj.path)
            return false
        return true
    }
}