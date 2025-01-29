package com.connect.meetupsfellow.mvp.view.model

import androidx.annotation.Keep
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@Keep
@IgnoreExtraProperties
data class ConversationModel(
    var chatMessage: String = "",
    var chatSenderId: String = "",
    var convoConstantId: String = "",
    var convoId: String = "0",
    var chatOpen: Boolean = false,
    var meDeletedConvo: Boolean = false,
    var otherUserOnline: Boolean = false,
    var userTyping: Boolean = false,
    var blockedBY: String = "",
    var lastMessagetime: Long = 0,
    var otherUserId: String = "0",
    var otherUserName: String = "",
    var unReadCount: String = "",
    var otherUserImageUrl: String = ""
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "chatMessage" to chatMessage,
            "chatSenderId" to chatSenderId,
            "convoConstantId" to convoConstantId,
            "convoId" to convoId,
            "chatOpen" to chatOpen,
            "meDeletedConvo" to meDeletedConvo,
            "otherUserOnline" to otherUserOnline,
            "userTyping" to userTyping,
            "blockedBY" to blockedBY,
            "lastMessagetime" to lastMessagetime,
            "otherUserId" to otherUserId,
            "otherUserName" to otherUserName,
            "unReadCount" to unReadCount,
            "otherUserImageUrl" to otherUserImageUrl
        )
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + convoId.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (javaClass != other.javaClass)
            return false
        val obj = other as ConversationModel
        if (convoId == "") {
            if (obj.convoId != "")
                return false
        } else if (convoId != obj.convoId)
            return false
        return true
    }
}