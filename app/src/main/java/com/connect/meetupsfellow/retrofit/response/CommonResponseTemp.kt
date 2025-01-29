package com.connect.meetupsfellow.retrofit.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Jammwal on 07-03-2018.
 */
class CommonResponseTemp : Serializable {

    @SerializedName("userData")
    internal val nearbyuser = arrayListOf<ResponseUserData>()

    @SerializedName("haveNext")
    internal val haveNext = -1

    @SerializedName("nextPage")
    internal val nextPage = -1

}