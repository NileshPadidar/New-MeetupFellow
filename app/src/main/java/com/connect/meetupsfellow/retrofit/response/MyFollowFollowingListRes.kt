package com.connect.meetupsfellow.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class MyFollowFollowingListRes: Serializable {

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("follower_id")
    @Expose
    var followerId: Int? = null

    @SerializedName("followed_by")
    @Expose
    var followedBy: Int? = null

    @SerializedName("follow_date_time")
    @Expose
    var followDateTime: String? = null

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
}