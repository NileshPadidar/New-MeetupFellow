package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.File
import java.io.Serializable

class RequestCreateUser : Serializable {

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

    @SerializedName("gender")
    @Expose
    internal var gender = ""

    @SerializedName("ethnicity")
    @Expose
    internal var ethnicity = ""

    @SerializedName("partnerStatus")
    @Expose
    internal var partnerStatus = ""

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

    @SerializedName("homeLocation")
    @Expose
    internal var homeLocation = ""

    @SerializedName("homeLat")
    @Expose
    internal var homeLat = ""

    @SerializedName("homeLong")
    @Expose
    internal var homeLong = ""

    @SerializedName("distance")
    @Expose
    internal var distance = ""

    @SerializedName("mile")
    internal var mile = ""

    @SerializedName("km")
    internal var km = ""

    @SerializedName("twitterLink")
    @Expose
    internal var twitterLink = ""

    @SerializedName("instagramLink")
    @Expose
    internal var instagramLink = ""

    @SerializedName("xtubeLink")
    @Expose
    internal var xtubeLink = ""

    @SerializedName("height")
    @Expose
    internal var height = ""

    @SerializedName("heightUnit")
    @Expose
    internal var heightUnit = ""

    @SerializedName("weight")
    @Expose
    internal var weight = ""

    @SerializedName("weightUnit")
    @Expose
    internal var weightUnit = ""

    @SerializedName("fistingId")
    @Expose
    internal var fistingId = ""

    @SerializedName("glovePreference")
    @Expose
    internal var glovePreference = ""

    @SerializedName("generalIntercourse")
    @Expose
    internal var generalIntercourse = ""

    @SerializedName("condomPreference")
    @Expose
    internal var condomPreference = ""

    @SerializedName("stiStatus")
    @Expose
    internal var stiStatus = ""

    @SerializedName("PrEP")
    @Expose
    internal var PrEP = ""

    @SerializedName("image_1_file")
    @Expose
    internal var image1: File? = null

    @SerializedName("image_1_id")
    @Expose
    internal var id1 = 0

    @SerializedName("imagePath1")
    @Expose
    internal var imagePath1 = ""

    @SerializedName("image_2_file")
    @Expose
    internal var image2: File? = null

    @SerializedName("image_2_id")
    @Expose
    internal var id2 = 0

    @SerializedName("imagePath2")
    @Expose
    internal var imagePath2 = ""

    @SerializedName("image_3_file")
    @Expose
    internal var image3: File? = null

    @SerializedName("image_3_id")
    @Expose
    internal var id3 = 0

    @SerializedName("imagePath3")
    @Expose
    internal var imagePath3 = ""

    @SerializedName("image_4_file")
    @Expose
    internal var image4: File? = null

    @SerializedName("image_4_id")
    @Expose
    internal var id4 = 0

    @SerializedName("imagePath4")
    @Expose
    internal var imagePath4 = ""

    @SerializedName("image_5_file")
    @Expose
    internal var image5: File? = null

    @SerializedName("image_5_id")
    @Expose
    internal var id5 = 0

    @SerializedName("imagePath5")
    @Expose
    internal var imagePath5 = ""

    @SerializedName("intoIdentity")
    @Expose
    internal var intoIdentity = ""

    @SerializedName("myIdentity")
    @Expose
    internal var myIdentity = ""
}