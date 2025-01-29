package com.connect.meetupsfellow.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class ResponseFavoriteUser : Serializable {
/*
    @SerializedName("email")
    internal var email = ""

    @SerializedName("id")
    internal var id = -1

    @SerializedName("name")
    internal var name = ""

    @SerializedName("age")
    internal var age = ""

    @SerializedName("phoneNumber")
    internal var phoneNumber = ""

    @SerializedName("countryCode")
    internal var countryCode = ""

    @SerializedName("isoCode")
    internal var isoCode = ""

    @SerializedName("currentLocation")
    internal var currentLocation = ""

    @SerializedName("currentLat")
    internal var currentLat = ""

    @SerializedName("currentLong")
    internal var currentLong = ""

    @SerializedName("homeLat")
    internal var homeLat = ""

    @SerializedName("homeLong")
    internal var homeLong = ""

    @SerializedName("onlineStatus")
    internal var onlineStatus = -1

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
    internal var homeLocation = ""*/

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("favourite_by")
    @Expose
    var favouriteBy: Int? = null

    @SerializedName("favourite_user_id")
    @Expose
    var favouriteUserId: Int? = null

    @SerializedName("favourite_date_time")
    @Expose
    var favouriteDateTime: String? = null

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
        val obj = other as ResponseFavoriteUser
        if (id == -1) {
            if (obj.id != -1)
                return false
        } else if (id != obj.id)
            return false
        return true
    }
}