package com.connect.meetupsfellow.developement.interfaces

import com.google.firebase.storage.StorageReference
import com.connect.meetupsfellow.global.firebase.FirebaseChatListener
import com.connect.meetupsfellow.global.firebase.FirebaseListener
import com.connect.meetupsfellow.mvp.view.model.ChatModel
import com.connect.meetupsfellow.mvp.view.model.ConversationModel
import com.connect.meetupsfellow.mvp.view.model.ProfileFirebase

interface FirebaseUtil {

    fun syncFirebaseDataBase(): Boolean

    fun fetchStorageReference(): StorageReference
    fun userOffline(): Boolean
    fun userLogout(): Boolean
    fun deactivateAccount(): Boolean
    fun deleteAccount(): Boolean
    fun saveUserProfile(profile: ProfileFirebase): Boolean
    fun hasConversation(path: String, listener: FirebaseListener)
    fun createConversationAndChat(me: ConversationModel, other: ConversationModel, chatModel: ChatModel)
    fun isTyping(path: String, conversationModel: ConversationModel)
    fun fetchFullConversation(userId: String, listener: FirebaseListener)
    fun fetchConversation(userId: String, listener: FirebaseChatListener)
    fun fetchUserProfile()
    fun fetchUserProfile(listener: FirebaseChatListener)
    fun fetchUserProfileImage(userID: String, listener: FirebaseListener)
    fun fetchUserChat(convoId: String, listener: FirebaseChatListener?)
    fun fetchFullUserChat(convoId: String, listener: FirebaseListener)
    fun updateChatStatus(path: String, chatModel: ChatModel)
    fun updateActiveConvoId(convoId: String, userId: String): Boolean
    fun fetchActiveConvoId(userId: String, listener: FirebaseChatListener)
    fun updateUserStatus(userId: String, online: Boolean): Boolean
    fun updateUserBlocked(blockedId: String, otherId: String, userId: String)
    fun updateConversationDeleted(userId: String, convoId: String)

    fun universalChildListener(path: String, listener: FirebaseChatListener)

    fun deleteMessage(path: String, key: String)
    fun updatePushNotificationStatus(userId: String, allowPush: Boolean)
    fun fetchOtherUserPushNotificationStatus(userID: String, listener: FirebaseListener)

    interface ModelOps {
        fun saveConversation(conversation: String)
        fun saveUserProfiles(key: String, value: String)
    }
}