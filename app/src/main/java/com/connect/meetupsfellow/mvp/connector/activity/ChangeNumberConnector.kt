package com.connect.meetupsfellow.mvp.connector.activity

import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.mvp.connector.reusable.ReusableRetrofitConnector
import com.connect.meetupsfellow.retrofit.request.RequestUpdateNumber
import com.connect.meetupsfellow.retrofit.response.CommonResponse

interface ChangeNumberConnector {

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
        fun addChangeNumberObject(requestUpdateNumber: RequestUpdateNumber)
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
        fun saveUserProfile(profile: String)
    }
}