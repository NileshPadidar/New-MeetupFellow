package com.connect.meetupsfellow.retrofit.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ResponseEventInterestedPeople : Serializable {

    @SerializedName("uid")
    internal var uid = -1

    @SerializedName(value = "name", alternate = ["userName"])
    internal var name = ""

    @SerializedName("imagePath")
    internal var imagePath = ""

    @SerializedName("imageThumb")
    internal var imageThumb = ""

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + uid.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (javaClass != other.javaClass)
            return false
        val obj = other as ResponseEventInterestedPeople
        if (uid == -1) {
            if (obj.uid != -1)
                return false
        } else if (uid != obj.uid)
            return false
        return true
    }
}