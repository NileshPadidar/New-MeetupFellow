package com.connect.meetupsfellow.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ResponseConnectionRequest : Serializable {
    @SerializedName("connection_id")
    @Expose
    var connectionId: Int? = null

    @SerializedName("sender_id")
    @Expose
    var senderId: Int? = null

    @SerializedName("receiver_id")
    @Expose
    var receiverId: Int? = null

    @SerializedName("connection_status")
    @Expose
    var connectionStatus: String? = null

    @SerializedName("direct_message")
    @Expose
    var directMessage: String? = null

    @SerializedName("sent_time")
    @Expose
    var sentTime: String? = null

    @SerializedName("aboutMe")
    @Expose
    var aboutMe: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("userId")
    @Expose
    var userId: Int? = null

    @SerializedName("imagePath")
    @Expose
    var imagePath: String? = null

    @SerializedName("imageThumb")
    @Expose
    var imageThumb: String? = null

    @SerializedName("access_status")
    @Expose
    var access_status: String? = null

    @SerializedName("ismyprivatealbumaccess")
    @Expose
    var ismyprivatealbumaccess = false


    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (javaClass != other.javaClass)
            return false
        val obj = other as ResponseConnectionRequest
        if (userId == -1) {
            if (obj.userId != -1)
                return false
        } else if (userId != obj.userId)
            return false
        return true
    }
}