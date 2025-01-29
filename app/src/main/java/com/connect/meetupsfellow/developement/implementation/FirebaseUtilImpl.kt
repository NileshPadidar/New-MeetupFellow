package com.connect.meetupsfellow.developement.implementation

import android.content.Context
import android.text.TextUtils
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.FirebaseChat
import com.connect.meetupsfellow.developement.interfaces.FirebaseUtil
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.firebase.FirebaseChatListener
import com.connect.meetupsfellow.global.firebase.FirebaseListener
import com.connect.meetupsfellow.global.model.FirebaseModel
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.view.model.ChatModel
import com.connect.meetupsfellow.mvp.view.model.ConversationModel
import com.connect.meetupsfellow.mvp.view.model.ProfileFirebase
import com.connect.meetupsfellow.retrofit.response.ResponseUserProfileImages
import javax.inject.Inject

class FirebaseUtilImpl(internal var context: Context) : FirebaseUtil {

    private companion object {
        var databaseReference: DatabaseReference? = null
            private set

        var storageReference: StorageReference? = null
            private set
    }

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.component!!.inject(this@FirebaseUtilImpl)
    }

    private val firebaseModel: FirebaseUtil.ModelOps = FirebaseModel()

    private fun getReference(): DatabaseReference {
        if (null == databaseReference) {
            LogManager.logger.i(
                ArchitectureApp.instance!!.tag,
                "getEnvironment() : ${getEnvironment()}"
            )
            databaseReference = FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl+"/").reference.child(Constants.Firebase.ChatDB)
        }
        return databaseReference!!
    }

    private fun getApprovedImage(images: ArrayList<ResponseUserProfileImages>): String {
        var image = ""
        images.asSequence().forEach {
            when (it.status) {
                "approved" -> {
                    image = it.imagePath
                }
            }
        }
        return image
    }

    override fun userOffline(): Boolean {
        if (sharedPreferencesUtil.userId().isNotEmpty()) {
            databaseReference!!.child("${Constants.Firebase.Profile}/${sharedPreferencesUtil.userId()}")
                .setValue(
                    ProfileFirebase(
                        "",
                        getApprovedImage(sharedPreferencesUtil.fetchUserProfile().images),
                        false,
                        sharedPreferencesUtil.userId().toInt(),
                        sharedPreferencesUtil.fetchUserProfile().name,
                        sharedPreferencesUtil.fetchDeviceToken(),
                        sharedPreferencesUtil.fetchSettings().allowPush != 0,
                        1,
                        ""+Constants.getUTCDateTime.getUTCTime()
                    )
                )
        }
        return true
    }

    override fun userLogout(): Boolean {
        if (sharedPreferencesUtil.userId().isNotEmpty()) {
            databaseReference!!.child("${Constants.Firebase.Profile}/${sharedPreferencesUtil.userId()}")
                .setValue(
                    ProfileFirebase(
                        "",
                        getApprovedImage(sharedPreferencesUtil.fetchUserProfile().images),
                        false,
                        sharedPreferencesUtil.userId().toInt(),
                        sharedPreferencesUtil.fetchUserProfile().name,
                        "",
                        sharedPreferencesUtil.fetchSettings().allowPush != 0,
                        1,
                        ""+Constants.getUTCDateTime.getUTCTime()
                    )
                )
        }
        return true
    }

    override fun deactivateAccount(): Boolean {
        if (sharedPreferencesUtil.userId().isNotEmpty()) {
            databaseReference!!.child("${Constants.Firebase.Profile}/${sharedPreferencesUtil.userId()}")
                .setValue(
                    ProfileFirebase(
                        "",
                        getApprovedImage(sharedPreferencesUtil.fetchUserProfile().images),
                        false,
                        sharedPreferencesUtil.userId().toInt(),
                        sharedPreferencesUtil.fetchUserProfile().name,
                        "",
                        sharedPreferencesUtil.fetchSettings().allowPush != 0,
                        0,
                        ""+Constants.getUTCDateTime.getUTCTime()
                    )
                )
        }
        return true
    }

    override fun deleteAccount(): Boolean {
        if (sharedPreferencesUtil.userId().isNotEmpty()) {
            getReference().child("${Constants.Firebase.Profile}/${sharedPreferencesUtil.userId()}")
                .removeValue()
            getReference().child("${Constants.Firebase.Conversation}/${sharedPreferencesUtil.userId()}")
                .removeValue()
        }
        return true
    }

    private fun getEnvironment(): String {
        return when (BuildConfig.DEBUG) {
            true -> com.connect.meetupsfellow.BuildConfig.TESTING
            else -> com.connect.meetupsfellow.BuildConfig.URL_LIVE
        }
    }

    override fun syncFirebaseDataBase(): Boolean {
//        getReference().addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onCancelled(error: DatabaseError) {
////                listener.onCancelled(error)
//            }
//
//            override fun onDataChange(snapShot: DataSnapshot) {
//                LogManager.logger.i(
//                    ArchitectureApp.instance!!.tag,
//                    "Token is Database : ${snapShot.value}"
//                )
//            }
//        })
        return true
    }

    override fun fetchStorageReference(): StorageReference {
        if (null == storageReference)
            storageReference =
                FirebaseStorage.getInstance().getReferenceFromUrl(Constants.Firebase.BucketUrl)

        return storageReference!!
    }

    override fun saveUserProfile(profile: ProfileFirebase): Boolean {
        getReference().child("${Constants.Firebase.Profile}/${profile.userId}").setValue(profile)
        return true
    }

    override fun updateActiveConvoId(convoId: String, userId: String): Boolean {
        val childUpdate = HashMap<String, Any>()
        childUpdate["${Constants.Firebase.Profile}/$userId/activeConvoId"] = convoId
        when (convoId.isNotEmpty()) {
            true -> childUpdate["${Constants.Firebase.Conversation}/$userId/$convoId/unReadCount"] =
                "0"

            false -> {}
        }
        getReference().updateChildren(childUpdate).addOnSuccessListener { }
            .addOnFailureListener { }
        return true
    }

    override fun updateUserStatus(userId: String, online: Boolean): Boolean {
        if (userId.isEmpty())
            return false
        getReference().child("${Constants.Firebase.Profile}/$userId")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapShot: DataSnapshot) {
                    if (snapShot.exists()) {
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag,
                            "Online Status is : $online"
                        )
                        if (userId.isNotEmpty()) {
                            val childUpdate = HashMap<String, Any>()
                            childUpdate["${Constants.Firebase.Profile}/$userId/onlineStatus"] =
                                online
                            getReference().updateChildren(childUpdate).addOnSuccessListener { }
                                .addOnFailureListener { }
                        }
                    } else {
                        val user = sharedPreferencesUtil.fetchUserProfile()
                        saveUserProfile(
                            ProfileFirebase(
                                "",
                                getApprovedImage(user.images),
                                true,
                                user.id.toInt(),
                                user.name,
                                sharedPreferencesUtil.fetchDeviceToken(),
                                true,
                                1,
                                ""+Constants.getUTCDateTime.getUTCTime()
                            )
                        )
                    }
                }
            })

        return true
    }

    override fun fetchActiveConvoId(userId: String, listener: FirebaseChatListener) {
        getReference().child("${Constants.Firebase.Profile}/$userId/activeConvoId")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    LogManager.logger.i(ArchitectureApp.instance!!.tag, "Token is error : $error")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    LogManager.logger.i(
                        ArchitectureApp.instance!!.tag,
                        "Token is fetchActiveConvoId onChildRemoved : ${snapshot.key}"
                    )
                    listener.onChatEventListener(snapshot, FirebaseChat.Changed)
                }

            })
    }

    override fun hasConversation(path: String, listener: FirebaseListener) {
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag,
            "Token is : ${getReference().child(path).push().key}"
        )
        getReference().child("${Constants.Firebase.Conversation}/$path")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    listener.onCancelled(error)
                }

                override fun onDataChange(snapShot: DataSnapshot) {
                    listener.onDataChange(snapShot)
                }
            })
    }

    override fun createConversationAndChat(
        me: ConversationModel,
        other: ConversationModel,
        chatModel: ChatModel
    ) {
        val childUpdate = HashMap<String, Any>()
        childUpdate["/${me.chatSenderId}/${me.convoId}"] = me
//        childUpdate["/${me.otherUserId}/${me.convoId}"] = other
        getReference().child(Constants.Firebase.Conversation).updateChildren(childUpdate)
            .addOnSuccessListener {
                createConversation(other, "/${me.otherUserId}/${me.convoId}")
                createChat(chatModel, me.convoId)
                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag,
                    "Token is : addOnSuccessListener"
                )
            }.addOnFailureListener {
                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag,
                    "Token is : addOnFailureListener"
                )
            }
    }

    private fun createConversation(other: ConversationModel, path: String) {
        val childUpdate = HashMap<String, Any>()
        childUpdate[path] = other
        getReference().child(Constants.Firebase.Conversation).updateChildren(childUpdate)
            .addOnSuccessListener {
                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag,
                    "Token is : addOnSuccessListener"
                )
            }.addOnFailureListener {
                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag,
                    "Token is : addOnFailureListener"
                )
            }
    }

    private fun createChat(chatModel: ChatModel, convoId: String) {
        val key = getReference().child("${Constants.Firebase.Chat}/${convoId}/Chat").push().key
        val childUpdate = HashMap<String, Any>()
        childUpdate["/${convoId}/Chat/$key"] = chatModel
        getReference().child(Constants.Firebase.Chat).updateChildren(childUpdate)
            .addOnSuccessListener {
                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag,
                    "Token is : addOnSuccessListener"
                )
            }.addOnFailureListener {
                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag,
                    "Token is : addOnFailureListener"
                )
            }
    }

    override fun fetchConversation(userId: String, listener: FirebaseChatListener) {

        getReference().child("${Constants.Firebase.Conversation}/$userId/")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildMoved(snapshot: DataSnapshot, p1: String?) {
                    LogManager.logger.i(
                        ArchitectureApp.instance!!.tag,
                        "Token is onChildMoved : $p1"
                    )
                }

                override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {
                    LogManager.logger.i(
                        ArchitectureApp.instance!!.tag,
                        "Token is onChildChanged fetchConversation : $snapshot"
                    )
                    listener.onChatEventListener(snapshot, FirebaseChat.Changed)
                }

                override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
                    LogManager.logger.i(
                        ArchitectureApp.instance!!.tag,
                        "Token is onChildAdded : $snapshot"
                    )
                    listener.onChatEventListener(snapshot, FirebaseChat.Added)
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    LogManager.logger.i(
                        ArchitectureApp.instance!!.tag,
                        "Token is onChildRemoved : ${snapshot.value}"
                    )
                    listener.onChatEventListener(snapshot, FirebaseChat.Removed)
                }

                override fun onCancelled(error: DatabaseError) {
                    LogManager.logger.i(ArchitectureApp.instance!!.tag, "Token is error : $error")
                }
            })
    }

    override fun fetchFullConversation(userId: String, listener: FirebaseListener) {
        getReference().child("${Constants.Firebase.Conversation}/$userId/")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    LogManager.logger.i(ArchitectureApp.instance!!.tag, "Token is error : $error")
                    listener.onCancelled(error)
                }

                override fun onDataChange(snapShot: DataSnapshot) {
                    listener.onDataChange(snapShot)
                }

            })
    }

    override fun fetchUserProfile() {
        getReference().child(Constants.Firebase.Profile)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    LogManager.logger.i(ArchitectureApp.instance!!.tag, "Token is error : $error")
                }

                override fun onDataChange(snapShot: DataSnapshot) {
                    when (snapShot.exists()) {
                        true -> {
                            LogManager.logger.i(
                                ArchitectureApp.instance!!.tag,
                                "Token is Profile : ${snapShot.value}"
                            )
                            LogManager.logger.i(
                                ArchitectureApp.instance!!.tag,
                                "Token is Profile : ${snapShot.value is ArrayList<*>}"
                            )

                            val profile = snapShot.value as Map<*, *>
                            for ((key, value) in profile) {
                                when (value) {
                                    is ProfileFirebase -> {
                                        firebaseModel.saveUserProfiles("$key", Gson().toJson(value))
                                    }
                                }
                            }
                        }

                        false -> TODO()
                    }
                }
            })
    }

    override fun fetchUserProfile(listener: FirebaseChatListener) {
        getReference().child(Constants.Firebase.Profile)
            .addChildEventListener(object : ChildEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onChildMoved(snapshot: DataSnapshot, p1: String?) {
                }

                override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {
                    when (TextUtils.isDigitsOnly(snapshot.key)) {
                        true -> {
                            sharedPreferencesUtil.saveOtherUserProfile(
                                "${snapshot.key}",
                                Gson().toJson(snapshot.value)
                            )
                            LogManager.logger.i(
                                ArchitectureApp.instance!!.tag,
                                "Profile is ::: Changed $snapshot"
                            )
                        }

                        false -> TODO()
                    }
                }

                override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
                    when (TextUtils.isDigitsOnly(snapshot.key)) {
                        true -> {
                            sharedPreferencesUtil.saveOtherUserProfile(
                                "${snapshot.key}",
                                Gson().toJson(snapshot.value)
                            )
                            LogManager.logger.i(
                                ArchitectureApp.instance!!.tag,
                                "Profile is ::: Added $snapshot"
                            )
                        }

                        false -> TODO()
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    when (TextUtils.isDigitsOnly(snapshot.key)) {
                        true -> {
                            val profile = snapshot.getValue(ProfileFirebase::class.java)
                            profile!!.status = 0
                            profile.imgUrl = ""
                            sharedPreferencesUtil.remove("${snapshot.key}")
                            LogManager.logger.i(
                                ArchitectureApp.instance!!.tag,
                                "Profile is ::: Changed $snapshot"
                            )
                        }

                        false -> TODO()
                    }

                }
            })
    }

    override fun fetchUserProfileImage(userID: String, listener: FirebaseListener) {
        getReference().child("${Constants.Firebase.Profile}/$userID")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    LogManager.logger.i(ArchitectureApp.instance!!.tag, "Token is error : $error")
                    listener.onCancelled(error)
                }

                override fun onDataChange(snapShot: DataSnapshot) {
                    listener.onDataChange(snapShot)
                }
            })
    }

    override fun fetchFullUserChat(convoId: String, listener: FirebaseListener) {
        getReference().child("${Constants.Firebase.Chat}/$convoId/Chat")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    listener.onCancelled(error)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    listener.onDataChange(snapshot)
                }
            })
    }

    override fun fetchUserChat(convoId: String, listener: FirebaseChatListener?) {
        getReference().child("${Constants.Firebase.Chat}/$convoId/Chat")
            .addChildEventListener(object : ChildEventListener {
                override fun onCancelled(error: DatabaseError) {
                    LogManager.logger.i(ArchitectureApp.instance!!.tag, "Token is error : $error")
                }

                override fun onChildMoved(snapshot: DataSnapshot, p1: String?) {
                    LogManager.logger.i(
                        ArchitectureApp.instance!!.tag,
                        "Token is onChildMoved : $p1"
                    )
                }

                override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {
                    LogManager.logger.i(
                        ArchitectureApp.instance!!.tag,
                        "Token is onChildChanged fetchUserChat : $snapshot"
                    )
                    listener?.onChatEventListener(snapshot, FirebaseChat.Changed)
                }

                override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
                    LogManager.logger.i(
                        ArchitectureApp.instance!!.tag,
                        "Token is onChildAdded : $snapshot"
                    )
                    listener?.onChatEventListener(snapshot, FirebaseChat.Added)
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    LogManager.logger.i(
                        ArchitectureApp.instance!!.tag,
                        "Token is onChildRemoved : ${snapshot.value}"
                    )
                    listener?.onChatEventListener(snapshot, FirebaseChat.Removed)
                }
            })
    }

    override fun updateChatStatus(path: String, chatModel: ChatModel) {
        val childUpdate = HashMap<String, Any>()
        childUpdate[path] = chatModel.chatStatus
        getReference().updateChildren(childUpdate)
            .addOnSuccessListener {
                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag,
                    "Token is : addOnSuccessListener : ${Gson().toJson(chatModel)}, Path is : $path"
                )
            }.addOnFailureListener {
                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag,
                    "Token is : addOnFailureListener"
                )
            }
    }

    override fun deleteMessage(path: String, key: String) {
        getReference().child(path).removeValue()
    }

    override fun isTyping(path: String, conversationModel: ConversationModel) {
        val childUpdate = HashMap<String, Any>()
        childUpdate[path] = conversationModel.userTyping
        getReference().updateChildren(childUpdate)
            .addOnSuccessListener {
                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag,
                    "Token is : addOnSuccessListener"
                )
            }.addOnFailureListener {
                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag,
                    "Token is : addOnFailureListener"
                )
            }
    }

    override fun updateUserBlocked(blockedId: String, otherId: String, userId: String) {
        val convoId = Constants.ConvoId.id(userId.toInt(), otherId.toInt())
        val childUpdate = HashMap<String, Any>()
        childUpdate["/$otherId/$convoId/blockedBY"] = blockedId
//        childUpdate["/$userId/$convoId/blockedBY"] = blockedId
        getReference().child(Constants.Firebase.Conversation).updateChildren(childUpdate)
            .addOnSuccessListener {
                blockBy(blockedId, "/$userId/$convoId/blockedBY")
                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag,
                    "Token is : addOnSuccessListener"
                )
            }.addOnFailureListener {
                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag,
                    "Token is : addOnFailureListener"
                )
            }
    }

    private fun blockBy(blockedId: String, path: String) {
        val childUpdate = HashMap<String, Any>()
        childUpdate[path] = blockedId
        getReference().child(Constants.Firebase.Conversation).updateChildren(childUpdate)
            .addOnSuccessListener {
                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag,
                    "Token is : addOnSuccessListener"
                )
            }.addOnFailureListener {
                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag,
                    "Token is : addOnFailureListener"
                )
            }
    }

    override fun updateConversationDeleted(userId: String, convoId: String) {
        val childUpdate = HashMap<String, Any>()
        childUpdate["/$userId/$convoId/meDeletedConvo"] = true
        getReference().child(Constants.Firebase.Conversation).updateChildren(childUpdate)
            .addOnSuccessListener {
                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag,
                    "Token is : addOnSuccessListener"
                )
            }.addOnFailureListener {
                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag,
                    "Token is : addOnFailureListener"
                )
            }
    }

    override fun universalChildListener(path: String, listener: FirebaseChatListener) {
        getReference().child(path).addChildEventListener(object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {
                LogManager.logger.i(ArchitectureApp.instance!!.tag, "Token is error : $error")
            }

            override fun onChildMoved(snapshot: DataSnapshot, p1: String?) {
                LogManager.logger.i(ArchitectureApp.instance!!.tag, "Token is onChildMoved : $p1")
            }

            override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {
                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag,
                    "Token is onChildChanged universalChildListener : $snapshot"
                )
                listener.onChatEventListener(snapshot, FirebaseChat.Changed)
            }

            override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag,
                    "Token is onChildAdded : $snapshot"
                )
                listener.onChatEventListener(snapshot, FirebaseChat.Added)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag,
                    "Token is onChildRemoved : ${snapshot.value}"
                )
                listener.onChatEventListener(snapshot, FirebaseChat.Removed)
            }
        })
    }

    override fun fetchOtherUserPushNotificationStatus(userID: String, listener: FirebaseListener) {

        getReference().child("${Constants.Firebase.Profile}/$userID/allowPush")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    LogManager.logger.i(ArchitectureApp.instance!!.tag, "Token is error : $error")
                    listener.onCancelled(error)
                }

                override fun onDataChange(snapShot: DataSnapshot) {
                    listener.onDataChange(snapShot)
                }
            })
    }

    override fun updatePushNotificationStatus(userId: String, allowPush: Boolean) {
        val childUpdate = HashMap<String, Any>()
        childUpdate["${Constants.Firebase.Profile}/$userId/allowPush"] = allowPush
        getReference().updateChildren(childUpdate)
    }

}