package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.SerializedName

class FollowUnfollowUserReq {

    @SerializedName("follower_id")
    internal var follower_id = 0
}