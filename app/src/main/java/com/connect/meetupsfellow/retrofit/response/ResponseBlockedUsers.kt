package com.connect.meetupsfellow.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ResponseBlockedUsers : Serializable {

    @SerializedName("blocUserId")
    @Expose
    internal val blocUserId = 0

    @SerializedName("blockByUser")
    @Expose
    internal val blockByUser = -1

    @SerializedName("name")
    @Expose
    internal val name = ""

    @SerializedName("imagePath")
    @Expose
    internal val imagePath = ""

    @SerializedName("imageThumb")
    @Expose
    internal val imageThumb = ""

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + blocUserId.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (javaClass != other.javaClass)
            return false
        val obj = other as ResponseBlockedUsers
        if (blocUserId == -1) {
            if (obj.blocUserId != -1)
                return false
        } else if (blocUserId != obj.blocUserId)
            return false
        return true
    }
}