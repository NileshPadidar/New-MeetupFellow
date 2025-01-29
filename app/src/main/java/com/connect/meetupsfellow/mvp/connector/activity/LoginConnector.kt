package com.connect.meetupsfellow.mvp.connector.activity

import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.mvp.connector.reusable.ReusableRetrofitConnector
import com.connect.meetupsfellow.retrofit.request.*
import com.connect.meetupsfellow.retrofit.response.CommonResponse

interface LoginConnector {

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
        fun addUserLoginAuthObject(requestUserAuth: RequestUserAuth)
        fun addUserForgetPasswordObject(forgetPasswordReq: ForgetPasswordReq)
        fun addSendVerifyLinkObject(forgetPasswordReq: ForgetPasswordReq)
        fun addUserLoginWithEmailObject(loginWithEmailRequest: LoginWithEmailRequest)
        fun addCheckSentCodeObject(checkSentEmailCode: CheckSentEmailCode)
        fun addResetPasswordObject(requestResetPassword: RequestResetPassword)
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
        fun saveLoginToken(token: String)
        fun saveDeviceToken(token: String)
        fun saveUserProfile(profile: String)
        fun userLoggedIn()
        fun saveSettings(settings: String)
    }
}