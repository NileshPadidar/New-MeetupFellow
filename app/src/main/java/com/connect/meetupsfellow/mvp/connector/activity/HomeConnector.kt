package com.connect.meetupsfellow.mvp.connector.activity

import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.mvp.connector.reusable.ReusableRetrofitConnector
import com.connect.meetupsfellow.retrofit.request.RequestClearChat
import com.connect.meetupsfellow.retrofit.request.RequestFeed
import com.connect.meetupsfellow.retrofit.request.RequestReadNotification
import com.connect.meetupsfellow.retrofit.request.SaveContactDetailsReq
import com.connect.meetupsfellow.retrofit.response.CommonResponse

interface HomeConnector {

    /**
     * View mandatory methods. Available to Presenter
     * Presenter -> View
     */
    interface RequiredViewOps {
        fun showToast(error: String, logout: Boolean?)
        fun showResponse(response: CommonResponse, type: ConstantsApi)
      //  fun showProUser(response: CommonResponse, type: ConstantsApi)
    }

    /**
     * Operations offered from Presenter to View
     * View -> Presenter
     */
    interface PresenterOps : ReusableRetrofitConnector {
        fun addUserFeedObject(requestFeed: RequestFeed)
        fun addLikeEventObject(eventId: String)
        fun addClearChatObject(requestClearChat: RequestClearChat)
        fun readNotificationObject(requestReadNotification: RequestReadNotification)
        fun saveUserContactObject(saveContactDetailsReq: SaveContactDetailsReq)
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
        fun saveReason(reason: String)
        fun fetchConversation(userId: String)
        fun saveNotification(count: String)
        fun saveAdminChatCount(count: String)
    }
}