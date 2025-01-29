package com.connect.meetupsfellow.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.connect.meetupsfellow.mvp.view.model.ConversationModel
import java.io.Serializable

class ResponseSearch : Serializable {

    @SerializedName("search_id", alternate = ["id", "chatSenderId","eventId"])
    @Expose
    internal var search_id = 0

    @SerializedName("search", alternate = ["otherUserId"])
    @Expose
    internal var search = ""

    @SerializedName("userId", alternate = ["convoId"])
    @Expose
    internal var userId = ""

    @SerializedName("name", alternate = ["otherUserName"])
    @Expose
    internal var name = ""

    @SerializedName("type")
    @Expose
    internal var type = ""

    @SerializedName("isBlocked")
    @Expose
    internal var isBlocked = ""

    @SerializedName("profileStatus")
    @Expose
    internal var profileStatus = ""

    //    @SerializedName("userLocation", alternate = ["homeLocation"])
    @SerializedName("userLocation")
    @Expose
    internal var userLocation = ""

    @SerializedName("currentLat")
    @Expose
    internal var currentLat = ""

    @SerializedName("currentLong")
    @Expose
    internal var currentLong = ""

    //    @SerializedName("eventImage", alternate = ["image"])
    @SerializedName("eventImage")
    @Expose
    internal var eventImage = ""

    @SerializedName("title")
    @Expose
    internal var title = ""

    @SerializedName("startDate", alternate = ["chatMessage"])
    @Expose
    internal var startDate = ""

    @SerializedName("endDate")
    @Expose
    internal var endDate = ""

    //    @SerializedName("eventLocation", alternate = ["location"])
    @SerializedName("eventLocation")
    @Expose
    internal var eventLocation = ""

    @SerializedName("description")
    @Expose
    internal var description = ""

    @SerializedName("status")
    @Expose
    internal var status = ""

    //    @SerializedName("eventDistance", alternate = ["distance"])
    @SerializedName("eventDistance")
    @Expose
    internal var eventDistance = ""

    //    @SerializedName("userDistance", alternate = ["distance"])
    @SerializedName("userDistance", alternate = ["lastMessagetime"])
    @Expose
    internal var userDistance = ""

    @SerializedName("mile")
    @Expose
    internal var mile = ""

    @SerializedName("km")
    @Expose
    internal var km = ""

    //    @SerializedName("userImage", alternate = ["userPic"])
    @SerializedName("userImage", alternate = ["imageUrl"])
    @Expose
    internal var userImage = ""

    internal var conversation = ConversationModel()

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + search_id.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (javaClass != other.javaClass)
            return false
        val obj = other as ResponseSearch
        if (search_id == -1) {
            if (obj.search_id != -1)
                return false
        } else if (search_id != obj.search_id)
            return false
        return true
    }

}