package com.connect.meetupsfellow.global.firebase

import com.google.firebase.database.DataSnapshot
import com.connect.meetupsfellow.constants.FirebaseChat

interface FirebaseChatListener {
    fun onChatEventListener(snapshot: DataSnapshot, chat: FirebaseChat)
}