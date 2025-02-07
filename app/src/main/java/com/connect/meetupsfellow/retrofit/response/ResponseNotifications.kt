package com.connect.meetupsfellow.retrofit.response

import com.connect.meetupsfellow.mvp.view.model.ConversationModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ResponseNotifications : Serializable {


    @SerializedName("notificationId")
    @Expose
    internal val notificationId = -1

    @SerializedName("pushMgsId")
    @Expose
    internal val pushMgsId = -1

     @SerializedName("eventId")
    @Expose
    internal val eventId = -1

    @SerializedName("type")
    @Expose
    internal val type = ""

    @SerializedName("message")
    @Expose
    internal val message = ""

    @SerializedName("created_at")
    @Expose
    internal val created_at = ""

    @SerializedName("imagePath")
    @Expose
    internal val imagePath = ""

    @SerializedName("notification_web_url")
    @Expose
    internal val notification_web_url = ""

    @SerializedName("readMgs")
    @Expose
    internal val readMgs = -1

    @SerializedName("is_multiple_visitor")
    @Expose
    internal val is_multiple_visitor = -1

    @SerializedName("is_multiple_profile_favourite")
    @Expose
    internal val is_multiple_profile_favourite = -1

    @SerializedName("is_multiple_profile_like")
    @Expose
    internal val is_multiple_profile_like = -1

    @SerializedName("is_multiple_following")
    @Expose
    internal val is_multiple_following = -1

    @SerializedName("is_multiple_event_interest")
    @Expose
    internal val is_multiple_event_interest = -1

    @SerializedName("is_multiple_event_like")
    @Expose
    internal val is_multiple_event_like = -1

    @SerializedName("otherSide")
    @Expose
    internal val otherSide = ConversationModel()

    /*@SerializedName("unreadMgsCount")
    @Expose
    internal val unreadMgsCount = -1*/


    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + notificationId.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (javaClass != other.javaClass) return false
        val obj = other as ResponseNotifications
        if (notificationId == -1) {
            if (obj.notificationId != -1) return false
        } else if (notificationId != obj.notificationId) return false
        return true
    }

}