package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class MyFavoriteUserReq : Serializable {

    @SerializedName("favourite_user_id")
    @Expose
    internal var favourite_user_id = 0
}