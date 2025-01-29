package com.connect.meetupsfellow.retrofit.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ResponseSettings : Serializable {

    @SerializedName("allowPush")
    internal var allowPush = -1

    @SerializedName("allowEvent")
    internal var allowEvent = -1

    @SerializedName("allowEventUpdate")
    internal var allowEventUpdate = -1

    @SerializedName("imperial_unit")
    internal var unit = -1

    @SerializedName("nsfw")
    internal var nsfw = -1

}