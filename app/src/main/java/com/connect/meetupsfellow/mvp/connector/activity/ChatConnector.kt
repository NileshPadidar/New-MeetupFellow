package com.connect.meetupsfellow.mvp.connector.activity

import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.mvp.connector.reusable.ReusableRetrofitConnector
import com.connect.meetupsfellow.retrofit.request.*
import com.connect.meetupsfellow.retrofit.response.CommonResponse

interface ChatConnector {

    /**
     * View mandatory methods. Available to Presenter
     * Presenter -> View
     */
    interface RequiredViewOps {
        fun showToast(error: String, logout: Boolean?)
        fun showResponse(response: CommonResponse, type: ConstantsApi)
    }

    /**
     * Operations offered from Presenter to View
     * View -> Presenter
     */
    interface PresenterOps : ReusableRetrofitConnector {
        fun addBlockUserObject(requestUserBlock: RequestUserBlock)
        fun addSendConnectionObject(sendConnectionRequest: SendConnectionRequest)
        fun addChatSaveObject(requestChatSave: RequestChatSave)
        fun addChatObject(convoId: String)
        fun addClearChatObject(requestClearChat: RequestClearChat)
        fun addSendNotificationObject(requestSendNotification: RequestSendNotification)
        fun addClearBadge(requestClearBadge: RequestClearBadge)
        fun addUserProfileObject(profileId: String)
    }

    /**
     * Operations offered from Presenter to Model
     * Model -> Presenter
     */
    interface RequiredPresenterOps

    /**
     * Model operations offered to Presenter
     * Presenter -> Model
     */
    interface ModelOps {
        fun logoutUser()
        fun blockUser(userId: String)
    }
}