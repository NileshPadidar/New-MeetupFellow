package com.connect.meetupsfellow.retrofit.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ResponseInterestedPeople : Serializable {

    @SerializedName("id")
    internal var id = -1

    @SerializedName("name")
    internal var name = ""

    @SerializedName("userPic")
    internal var userPic = ""

    @SerializedName("userThumb")
    internal var userThumb = ""

    @SerializedName("distance")
    internal var distance = ""

    @SerializedName("mile")
    internal var mile = ""

    @SerializedName("km")
    internal var km = ""

    @SerializedName("homeLocation")
    internal var homeLocation = ""

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + id.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (javaClass != other.javaClass)
            return false
        val obj = other as ResponseInterestedPeople
        if (id == -1) {
            if (obj.id != -1)
                return false
        } else if (id != obj.id)
            return false
        return true
    }
}