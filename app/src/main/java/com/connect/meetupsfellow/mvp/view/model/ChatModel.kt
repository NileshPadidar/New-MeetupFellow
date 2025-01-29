package com.connect.meetupsfellow.mvp.view.model

import androidx.annotation.Keep
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@Keep
@IgnoreExtraProperties
data class ChatModel constructor(
    var chatId: String = "",
    var chatMessage: String = "",
    var chatSenderId: String = "",
    var chatSenderName: String = "",
    var chatStatus: Int = 0,
    var chatTimeStamp: Long = 0,
    var mediaLength: String = "",
    var mediaType: Int = 0,
    var mediaUrlOriginal: String = "",
    var mediaUrlThumb: String = ""
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "chatId" to chatId,
            "chatMessage" to chatMessage,
            "chatSenderId" to chatSenderId,
            "chatSenderName" to chatSenderName,
            "chatStatus" to chatStatus,
            "chatTimeStamp" to chatTimeStamp,
            "mediaLength" to mediaLength,
            "mediaType" to mediaType,
            "mediaUrlOriginal" to mediaUrlOriginal,
            "mediaUrlThumb" to mediaUrlThumb
        )
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + chatId.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (javaClass != other.javaClass)
            return false
        val obj = other as ChatModel
        if (chatId == "") {
            if (obj.chatId != "")
                return false
        } else if (chatId != obj.chatId)
            return false
        return true
    }
}