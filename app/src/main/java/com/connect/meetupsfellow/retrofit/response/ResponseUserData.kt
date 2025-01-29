package com.connect.meetupsfellow.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ResponseUserData : Serializable {

    @SerializedName("message")
    internal var message = ""

    @SerializedName("phoneNumber")
    internal var number = ""

    @SerializedName("countryCode")
    internal var countryCode = ""

    @SerializedName("is_email_verified")
    internal var is_email_verified = ""

    @SerializedName("login_with_google_or_apple")
    internal var login_with_google_or_apple = ""

    @SerializedName("tempUserBlock")
    internal var tempUserBlock = ""

    @SerializedName("deviceToken")
    internal var deviceToken = ""

    @SerializedName("id")
    internal var id = ""

    @SerializedName("name")
    internal var name = ""

    @SerializedName("email")
    internal var email = ""

    @SerializedName("isoCode")
    internal var isoCode = ""

    @SerializedName("currentLocation")
    internal var currentLocation = ""

    @SerializedName("currentLat")
    internal var currentLat = ""

    @SerializedName("currentLong")
    internal var currentLong = ""

    @SerializedName("profileStatus")
    internal var profileStatus = ""

    @SerializedName("status")
    internal var status = 0

    @SerializedName("isBlocked")
    internal var isBlocked = ""

    @SerializedName("membersince")
    internal var membersince = ""

    @SerializedName("age")
    internal var age = ""

    @SerializedName("dob")
    internal var dob = ""

    @SerializedName("connection_status")
    internal var connection_status = ""

    @SerializedName("isprofileprivate")
    internal var isprofileprivate = 0

    @SerializedName("isprofilehidden")
    internal var isprofilehidden = 0

    @SerializedName("is_contact_list_added")
    internal var is_contact_list_added = 0

    @SerializedName("available_connection_counts")
    internal var available_connection_counts = 0

    @SerializedName("followers_count")
    internal var followers_count = 0

    @SerializedName("following_count")
    internal var following_count = 0

    @SerializedName("follower_status")
    internal var follower_status = ""

    @SerializedName("is_favourite")
    internal var is_favourite = 0

    @SerializedName("likedUserCount")
    internal var likedUserCount = 0

    @SerializedName("userId")
    internal var userId = ""

    @SerializedName("gender")
    internal var gender = ""

    @SerializedName("homeLocation")
    internal var homeLocation = ""

    @SerializedName("homeLat")
    internal var homeLat = ""

    @SerializedName("homeLong")
    internal var homeLong = ""

    @SerializedName("twitterLink")
    internal var twitterLink = ""

    @SerializedName("instagramLink")
    internal var instagramLink = ""

    @SerializedName("xtubeLink")
    internal var xtubeLink = ""

    @SerializedName("height")
    internal var height = ""

    @SerializedName("heightUnit")
    internal var heightUnit = ""

    @SerializedName("weight")
    internal var weight = ""

    @SerializedName("weightUnit")
    internal var weightUnit = ""

    @SerializedName("fistingId")
    internal var fistingId = ""

    @SerializedName("glovePreference")
    internal var glovePreference = ""

    @SerializedName("generalIntercourse")
    internal var generalIntercourse = ""

    @SerializedName("condomPreference")
    internal var condomPreference = ""

    @SerializedName("stiStatus")
    internal var stiStatus = ""

    @SerializedName("PrEP")
    internal var PrEP = ""

    @SerializedName("ethnicity")
    internal var ethnicity = ""

    @SerializedName("partnerStatus")
    internal var partnerStatus = ""

    @SerializedName("aboutMe")
    internal var aboutMe = ""

    @SerializedName("userImage")
    internal var images = arrayListOf<ResponseUserProfileImages>()

    @SerializedName("userPic")
    internal var userPic = ""

    @SerializedName("userThumb")
    internal var userThumb = ""

    @SerializedName("onlineStatus")
    internal var onlineStatus = 0

    @SerializedName("isImageUploaded")
    internal var isImageUploaded = 0

    @SerializedName("distance")
    internal var distance = ""

    @SerializedName("mile")
    internal var mile = ""

    @SerializedName("km")
    internal var km = ""

    @SerializedName("meLiked")
    internal var meLiked = false

    @SerializedName("intoIdentity")
    internal var intoIdentity = ""

    @SerializedName("myIdentity")
    internal var myIdentity = ""

    @SerializedName("ismyprivatealbumaccess")
    internal var ismyprivatealbumaccess = false

    @SerializedName("hasSharedPrivateAlbumWithMe")
    internal var hasSharedPrivateAlbumWithMe = false

    @SerializedName("isMyPrivateAlbumSharedWithUser")
    internal var isMyPrivateAlbumSharedWithUser = false

    @SerializedName("isProMembership")
    internal var isProMembership = false

    @SerializedName("hasPrivatePic")
    internal var hasPrivatePic = false

    @SerializedName("lastLoginTimeStamp")
    internal var lastLoginTimeStamp = ""

    internal var item = 1

    @SerializedName("sender_id")
    @Expose
    var senderId: Int? = null

    @SerializedName("receiver_id")
    @Expose
    var receiverId: Int? = null

    @SerializedName("directMessage")
    @Expose
    var directMessage: String? = null

    @SerializedName("badge_icons")
    @Expose
    var badgeIcons: String? = null

    @SerializedName("event_post_limit")
    @Expose
    var eventPostLimit: Int? = null

   @SerializedName("available_media_storage")
    @Expose
    var availableMediaStorage: Int? = null

    @SerializedName("current_plan_info")
    @Expose
    var currentPlanInfo: CurrentPlanInfo? = null

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + id.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (javaClass != other.javaClass) return false
        val obj = other as ResponseUserData
        if (id == "") {
            if (obj.id != "") return false
        } else if (id != obj.id) return false
        return true
    }


    @SerializedName("image")
    @Expose
    internal var image = ""


    @SerializedName("schedule")
    @Expose
    internal var schedule = ""


}