package com.connect.meetupsfellow.mvp.connector.activity

import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.mvp.connector.reusable.ReusableRetrofitConnector
import com.connect.meetupsfellow.retrofit.request.RequestReportUser
import com.connect.meetupsfellow.retrofit.request.RequestUserBlock
import com.connect.meetupsfellow.retrofit.response.CommonResponse

interface UserProfileConnector {

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
        fun addReportUserObject(requestReportUser: RequestReportUser)
        fun addUserProfileObject(profileId: String)
        fun addBlockUserObject(requestUserBlock: RequestUserBlock)
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
        fun saveReason(reason: String)
    }
}