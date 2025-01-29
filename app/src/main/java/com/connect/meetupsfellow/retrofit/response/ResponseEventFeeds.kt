package com.connect.meetupsfellow.retrofit.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ResponseEventFeeds : Serializable {

    @SerializedName("id")
    internal val id = -1

    @SerializedName("title")
    internal val title = ""

    @SerializedName("image")
    internal val image = ""

    @SerializedName("startDate")
    internal val startDate = ""

    @SerializedName("endDate")
    internal val endDate = ""

    @SerializedName("location")
    internal val location = ""

    @SerializedName("locationLat")
    internal val locationLat = ""

    @SerializedName("locationLong")
    internal val locationLong = ""

    @SerializedName("startTime")
    internal val startTime = ""

    @SerializedName("endTime")
    internal val endTime = ""

    @SerializedName("week_time")
    internal val week_time = ""

    @SerializedName("description")
    internal val description = ""

    @SerializedName("createdBy")
    internal val createdBy = ""

    @SerializedName("status")
    internal val status = ""

    @SerializedName("visibility")
    internal val visibility = ""

    @SerializedName("websiteUrl")
    internal val websiteUrl = ""

    @SerializedName("buyTicketUrl")
    internal val buyTicketUrl = ""

    @SerializedName("created_at")
    internal val created_at = ""

    @SerializedName("distance")
    internal val distance = ""

    @SerializedName("mile")
    internal var mile = ""

    @SerializedName("km")
    internal var km = ""

    @SerializedName("interestedUserCount")
    internal val interestedUserCount = -1

    @SerializedName("meInterested")
    internal var meInterested = false

    @SerializedName("meliked")
    internal var meliked = false

    @SerializedName("interestedUsers")
    internal val interestedUsers = arrayListOf<ResponseEventInterestedPeople>()

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
        val obj = other as ResponseEventFeeds
        if (id == -1) {
            if (obj.id != -1)
                return false
        } else if (id != obj.id)
            return false
        return true
    }
}