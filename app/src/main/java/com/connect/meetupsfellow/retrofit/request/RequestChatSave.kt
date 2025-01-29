package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.mvp.view.model.ChatModel
import java.io.Serializable

class RequestChatSave : Serializable {

    @SerializedName("conversationId")
    @Expose
    internal var conversationId = ""

    @SerializedName("chat")
    @Expose
    internal var chat = arrayListOf<ChatModel>()

    @SerializedName("type")
    @Expose
    internal var type = Constants.Device.TYPE
}