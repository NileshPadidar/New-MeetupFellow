package com.connect.meetupsfellow.mvp.connector.activity

import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.mvp.connector.reusable.ReusableRetrofitConnector
import com.connect.meetupsfellow.retrofit.request.RequestReadNotification
import com.connect.meetupsfellow.retrofit.response.CommonResponse

interface NotificationConnector {

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
        fun readNotificationObject(requestReadNotification: RequestReadNotification)
        fun getVisitorListObject(requestReadNotification: RequestReadNotification)
        fun getFollowingListObject(requestReadNotification: RequestReadNotification)
        fun getEventInterestListObject(requestReadNotification: RequestReadNotification)
        fun getEventLikeListObject(requestReadNotification: RequestReadNotification)
        fun getFavouriteListObject(requestReadNotification: RequestReadNotification)
        fun getLikeUserListObject(requestReadNotification: RequestReadNotification)
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
    }
}