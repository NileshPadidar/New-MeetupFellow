package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RequestClearChat : Serializable {

    @SerializedName("conversationId")
    @Expose
    internal var conversationId = ""

    @SerializedName("clearChatTime")
    @Expose
    internal var clearChatTime = ""

}