package com.connect.meetupsfellow.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ResponsePrivateAccess: Serializable {

    @SerializedName("userId")
    @Expose
    internal val userId = -1

    @SerializedName("userName")
    @Expose
    internal val userName = ""

    @SerializedName("imagePath")
    @Expose
    internal val imagePath = ""

    @SerializedName("currentLocation")
    @Expose
    internal val location = ""

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + userId.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (javaClass != other.javaClass)
            return false
        val obj = other as ResponsePrivateAccess
        if (userId == -1) {
            if (obj.userId != -1)
                return false
        } else if (userId != obj.userId)
            return false
        return true
    }

}