package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.SerializedName

class setAccessPrivateAlbumPermissionReq {

    @SerializedName("access_status")
    internal var access_status = ""

     @SerializedName("users")
    internal var users = ArrayList<Int>()


}