package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.File
import java.io.Serializable

class SignupRequest: Serializable {
    @SerializedName("name")
    @Expose
    internal var name = ""

    @SerializedName("email")
    @Expose
    internal var email = ""

    @SerializedName("age")
    @Expose
    internal var age = ""

    @SerializedName("dob")
    @Expose
    internal var dob = ""

    @SerializedName("phoneNumber")
    @Expose
    internal var phoneNumber = ""

    @SerializedName("countryCode")
    @Expose
    internal var countryCode = ""

    @SerializedName("isoCode")
    @Expose
    internal var isoCode = ""

    @SerializedName("deviceToken")
    @Expose
    internal var deviceToken = ""

    @SerializedName("deviceType")
    @Expose
    internal var deviceType = "android"

    @SerializedName("password")
    @Expose
    internal var password = ""

    @SerializedName("aboutMe")
    @Expose
    internal var aboutMe = ""

    @SerializedName("currentLocation")
    @Expose
    internal var currentLocation = ""

    @SerializedName("currentLat")
    @Expose
    internal var currentLat = ""

    @SerializedName("currentLong")
    @Expose
    internal var currentLong = ""

    @SerializedName("image_1_file")
    @Expose
    internal var image1: File? = null

    @SerializedName("image_1_id")
    @Expose
    internal var id1 = 0

    @SerializedName("imagePath1")
    @Expose
    internal var imagePath1 = ""

    @SerializedName("gender")
    @Expose
    internal var gender = ""

    @SerializedName("signup_type")
    @Expose
    internal var signup_type = ""
}