package com.connect.meetupsfellow.retrofit.request

import com.google.gson.annotations.SerializedName
import com.connect.meetupsfellow.mvp.view.model.ChatModel
import com.connect.meetupsfellow.mvp.view.model.ConversationModel

class Data {

    @SerializedName("senderImageUrl")
    internal var senderImageUrl = ""

    @SerializedName("type")
    internal var type = "chat"

    @SerializedName("receiverId")
    internal var receiverId = ""

    @SerializedName("userSide")
    internal var userSide = ConversationModel()

    @SerializedName("chatInfo")
    internal var chatInfo = ChatModel()

    @SerializedName("otherSide")
    internal var otherSide = ConversationModel()
}