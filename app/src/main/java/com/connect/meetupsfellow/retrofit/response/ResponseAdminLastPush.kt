package com.connect.meetupsfellow.retrofit.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.connect.meetupsfellow.mvp.view.model.ConversationModel
import java.io.Serializable

class ResponseAdminLastPush : Serializable {


        @SerializedName("notificationId")
        @Expose
        internal val notificationId = -1

        @SerializedName("pushMgsId")
        @Expose
        internal val pushMgsId = -1

        @SerializedName("type")
        @Expose
        internal val type = ""

        @SerializedName("message")
        @Expose
        internal val message = ""

        @SerializedName("created_at")
        @Expose
        internal val created_at = ""

        @SerializedName("imagePath")
        @Expose
        internal val imagePath = ""

        @SerializedName("readMgs")
        @Expose
        internal val readMgs = -1

        @SerializedName("otherSide")
        @Expose
        internal val otherSide = ConversationModel()

        override fun hashCode(): Int {
                val prime = 31
                var result = 1
                result = prime * result + notificationId.hashCode()
                return result
        }

        override fun equals(other: Any?): Boolean {
                if (this === other)
                        return true
                if (other == null)
                        return false
                if (javaClass != other.javaClass)
                        return false
                val obj = other as ResponseAdminLastPush
                if (notificationId == -1) {
                        if (obj.notificationId != -1)
                                return false
                } else if (notificationId != obj.notificationId)
                        return false
                return true
        }
    }