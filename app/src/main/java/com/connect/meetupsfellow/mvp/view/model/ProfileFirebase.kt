package com.connect.meetupsfellow.mvp.view.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class ProfileFirebase(
    var activeConvoId: String = "",
    var imgUrl: String = "",
    var onlineStatus: Boolean = false,
    var userId: Int = -1,
    var userName: String = "",
    var deviceToken: String = "",
    var allowPush: Boolean = true,
    var status: Int = 1,
    var lastLoginTimeStamp: String=""
   // var isPromember: Boolean = false    // schange

) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "activeConvoId" to activeConvoId,
            "imgUrl" to imgUrl,
            "onlineStatus" to onlineStatus,
            "userId" to userId,
            "userName" to userName,
            "deviceToken" to deviceToken,
            "allowPush" to allowPush,
            "status" to status,
            "lastLoginTimeStamp" to lastLoginTimeStamp
         //   "isPromember" to isPromember    //schange

        )
    }
}