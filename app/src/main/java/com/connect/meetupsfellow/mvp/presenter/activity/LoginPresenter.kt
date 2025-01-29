package com.connect.meetupsfellow.mvp.presenter.activity

import android.util.Log
import com.google.gson.Gson
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.developement.interfaces.ApiConnect
import com.connect.meetupsfellow.developement.interfaces.FirebaseUtil
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.interfaces.ErrorFlagStatusMessage
import com.connect.meetupsfellow.global.utils.Connected
import com.connect.meetupsfellow.global.utils.ErrorFlagStatus
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.LoginConnector
import com.connect.meetupsfellow.mvp.model.activity.LoginModel
import com.connect.meetupsfellow.retrofit.request.*
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ErrorResponse
import retrofit2.Response
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.Reader
import javax.inject.Inject

@Suppress("SENSELESS_COMPARISON")
class LoginPresenter(internal var mView: LoginConnector.RequiredViewOps) :
    LoginConnector.PresenterOps,
    LoginConnector.RequiredPresenterOps {

    @Inject
    lateinit var apiConnect: ApiConnect

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    internal var mModel: LoginConnector.ModelOps = LoginModel(this)

    init {
        ArchitectureApp.component!!.inject(this@LoginPresenter)
    }

    private lateinit var requestUserAuth: RequestUserAuth
    private lateinit var loginWithEmailRequest: LoginWithEmailRequest
    private lateinit var forgetPasswordReq: ForgetPasswordReq
    private lateinit var checkSentEmailCode: CheckSentEmailCode
    private lateinit var requestResetPassword: RequestResetPassword

    override fun addUserLoginAuthObject(requestUserAuth: RequestUserAuth) {
        this.requestUserAuth = requestUserAuth
    }

    override fun addUserForgetPasswordObject(forgetPasswordReq: ForgetPasswordReq) {
        this.forgetPasswordReq = forgetPasswordReq
    }

    override fun addSendVerifyLinkObject(forgetPasswordReq: ForgetPasswordReq) {
        this.forgetPasswordReq = forgetPasswordReq
    }

    override fun addUserLoginWithEmailObject(loginWithEmailRequest: LoginWithEmailRequest) {
        this.loginWithEmailRequest = loginWithEmailRequest
    }

    override fun addCheckSentCodeObject(checkSentEmailCode: CheckSentEmailCode) {
        this.checkSentEmailCode = checkSentEmailCode
    }
    override fun addResetPasswordObject(requestResetPassword: RequestResetPassword) {
        this.requestResetPassword = requestResetPassword
    }
    override fun callRetrofit(type: ConstantsApi) {

        when (Connected.isConnected()) {
            false -> {
                returnMessage(ArchitectureApp.instance!!.getString(R.string.internet_not_connected))
                Log.e("LoginPresenter","internet_not_connected")
                return
            }

            true -> {}
        }

        when (type) {
            ConstantsApi.LOGIN -> callForUserLogin(type)
            ConstantsApi.FORGET_PASSWORD -> callForForgetPassword(type)
            ConstantsApi.CHECK_SENT_EMAIL_CODE -> callForSentEmailCode(type)
            ConstantsApi.RESET_PASSWORD -> callForResetPassword(type)
            ConstantsApi.LOGIN_WITH_EMAIL -> callForUserLoginWithEmail(type)
            ConstantsApi.RESEND_EMAIL_VERIFICATION_LINK -> callForVerificationLink(type)
            ConstantsApi.FETCH_PROFILE -> callForUserProfile(type)
            else -> returnMessage(ArchitectureApp.instance!!.getString(R.string.invalid_request))
        }
    }

    private fun callForSettings(type: ConstantsApi) {
        try {
            val settings = apiConnect.api.settings(
                sharedPreferencesUtil.loginToken(),
                Constants.Random.random()
            )

            settings.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response<CommonResponse>> {
                    override fun onError(e: Throwable?) {
                        e!!.printStackTrace()
                        returnMessage("${e.message}")
                    }

                    override fun onNext(response: Response<CommonResponse>?) {
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag,
                            "Response is ::: $response"
                        )
                        if (response != null) {
                            when (response.isSuccessful) {
                                true -> {
                                    val loginResponse = response.body()!!
                                    mModel.saveSettings(Gson().toJson(loginResponse.settings))
                                    mView.showResponse(loginResponse, type)
                                }
                                false -> {
                                    returnError(response.errorBody()!!.charStream())
                                }
                            }
                        } else {
                            returnMessage(ArchitectureApp.instance!!.getString(R.string.error_title))
                        }
                    }

                    override fun onCompleted() {
                    }

                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callForUserLoginWithEmail(type: ConstantsApi) {
        try {
            val login = apiConnect.api.userLoginWithEmail(loginWithEmailRequest)

            login.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response<CommonResponse>> {
                    override fun onError(e: Throwable?) {
                        e!!.printStackTrace()
                        returnMessage("${e.message}")
                    }

                    override fun onNext(response: Response<CommonResponse>?) {
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag,
                            "Response is ::: $response"
                        )
                        if (response != null) {
                            when (response.isSuccessful) {
                                true -> {
                                    val loginResponse = response.body()
                                    mModel.saveLoginToken(loginResponse!!.token)
                                    when (loginResponse.status) {
                                        Constants.ProfileStatus.Pending -> {
                                            when (loginResponse.created) {
                                                0 -> {
                                                    mModel.saveUserProfile(
                                                        Gson().toJson(
                                                            loginResponse.userInfo
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    mView.showResponse(loginResponse, type)
                                }

                                false -> {

                                    returnError(response.errorBody()!!.charStream())
                                }
                            }

                        } else {
                            returnMessage(ArchitectureApp.instance!!.getString(R.string.error_title))
                        }
                    }

                    override fun onCompleted() {

                    }

                })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callForForgetPassword(type: ConstantsApi) {
        try {
            val login = apiConnect.api.userForgetPassword(forgetPasswordReq)

            login.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response<CommonResponse>> {
                    override fun onError(e: Throwable?) {
                        e!!.printStackTrace()
                        returnMessage("${e.message}")
                    }

                    override fun onNext(response: Response<CommonResponse>?) {
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag,
                            "Response is ::: $response"
                        )
                        if (response != null) {
                            when (response.isSuccessful) {
                                true -> {
                                    val loginResponse = response.body()
                                    if (loginResponse != null) {
                                        mView.showResponse(loginResponse, type)
                                    }
                                }

                                false -> {

                                    returnError(response.errorBody()!!.charStream())
                                }
                            }

                        } else {
                            returnMessage(ArchitectureApp.instance!!.getString(R.string.error_title))
                        }
                    }

                    override fun onCompleted() {

                    }

                })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callForVerificationLink(type: ConstantsApi) {
        try {
            val login = apiConnect.api.sendVerifyEmailLink(forgetPasswordReq)

            login.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response<CommonResponse>> {
                    override fun onError(e: Throwable?) {
                        e!!.printStackTrace()
                        returnMessage("${e.message}")
                    }

                    override fun onNext(response: Response<CommonResponse>?) {
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag,
                            "Response is ::: $response"
                        )
                        if (response != null) {
                            when (response.isSuccessful) {
                                true -> {
                                    val loginResponse = response.body()
                                    if (loginResponse != null) {
                                        mView.showResponse(loginResponse, type)
                                    }
                                }

                                false -> {

                                    returnError(response.errorBody()!!.charStream())
                                }
                            }

                        } else {
                            returnMessage(ArchitectureApp.instance!!.getString(R.string.error_title))
                        }
                    }

                    override fun onCompleted() {

                    }

                })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callForSentEmailCode(type: ConstantsApi) {
        try {
            val login = apiConnect.api.checkUserCode(checkSentEmailCode)

            login.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response<CommonResponse>> {
                    override fun onError(e: Throwable?) {
                        e!!.printStackTrace()
                        returnMessage("${e.message}")
                    }

                    override fun onNext(response: Response<CommonResponse>?) {
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag,
                            "Response is ::: $response"
                        )
                        if (response != null) {
                            when (response.isSuccessful) {
                                true -> {
                                    val loginResponse = response.body()
                                    if (loginResponse != null) {
                                        mView.showResponse(loginResponse, type)
                                    }
                                }

                                false -> {

                                    returnError(response.errorBody()!!.charStream())
                                }
                            }

                        } else {
                            returnMessage(ArchitectureApp.instance!!.getString(R.string.error_title))
                        }
                    }

                    override fun onCompleted() {

                    }

                })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callForResetPassword(type: ConstantsApi) {
        try {
            val login = apiConnect.api.userResetPassword(requestResetPassword)

            login.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response<CommonResponse>> {
                    override fun onError(e: Throwable?) {
                        e!!.printStackTrace()
                        returnMessage("${e.message}")
                    }

                    override fun onNext(response: Response<CommonResponse>?) {
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag,
                            "Response is ::: $response"
                        )
                        if (response != null) {
                            when (response.isSuccessful) {
                                true -> {
                                    val loginResponse = response.body()
                                    if (loginResponse != null) {
                                        mView.showResponse(loginResponse, type)
                                    }
                                }

                                false -> {

                                    returnError(response.errorBody()!!.charStream())
                                }
                            }

                        } else {
                            returnMessage(ArchitectureApp.instance!!.getString(R.string.error_title))
                        }
                    }

                    override fun onCompleted() {

                    }

                })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callForUserLogin(type: ConstantsApi) {
        try {
            val login = apiConnect.api.userLoginWithMobile(requestUserAuth)

            login.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response<CommonResponse>> {
                    override fun onError(e: Throwable?) {
                        e!!.printStackTrace()
                        returnMessage("${e.message}")
                    }

                    override fun onNext(response: Response<CommonResponse>?) {
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag,
                            "Response is ::: $response"
                        )
                        if (response != null) {
                            when (response.isSuccessful) {
                                true -> {
                                    val loginResponse = response.body()
                                    mModel.saveLoginToken(loginResponse!!.token)
                                    when (loginResponse.status) {
                                        Constants.ProfileStatus.Pending -> {
                                            when (loginResponse.created) {
                                                0 -> {
                                                    mModel.saveUserProfile(
                                                        Gson().toJson(
                                                            loginResponse.userInfo
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    mView.showResponse(loginResponse, type)
                                }

                                false -> {

                                    returnError(response.errorBody()!!.charStream())
                                }
                            }

                        } else {
                            returnMessage(ArchitectureApp.instance!!.getString(R.string.error_title))
                        }
                    }

                    override fun onCompleted() {

                    }

                })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callForUserProfile(type: ConstantsApi) {
        try {
            val login = apiConnect.api.getUserProfile(
                sharedPreferencesUtil.loginToken(),
                Constants.Random.random(),
                Constants.Random.random()
            )

            login.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response<CommonResponse>> {
                    override fun onError(e: Throwable?) {
                        e!!.printStackTrace()
                        returnMessage("${e.message}")
                    }

                    override fun onNext(response: Response<CommonResponse>?) {
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag,
                            "Response is ::: $response"
                        )
                        if (response != null) {
                            when (response.isSuccessful) {
                                true -> {
                                    val loginResponse = response.body()
                                    mModel.saveUserProfile(Gson().toJson(loginResponse?.userInfo))
                                    mModel.userLoggedIn()
                                    callForSettings(type)
                                    mView.showResponse(loginResponse!!, type)
                                }

                                false -> {
                                    returnError(response.errorBody()!!.charStream())
                                }
                            }

                        } else {
                            returnMessage(ArchitectureApp.instance!!.getString(R.string.error_title))
                        }
                    }

                    override fun onCompleted() {

                    }

                })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun returnError(reader: Reader) {
        val error = Gson().fromJson(reader, ErrorResponse::class.java)
        ErrorFlagStatus.getErrorMessage(error, object : ErrorFlagStatusMessage {
            override fun status(message: String, logout: Boolean?) {
                when (logout) {
                    true -> {
                        if (error.error != Constants.ServerError.SessionExpired) {
                            firebaseUtil.userLogout()
                        }
                        sharedPreferencesUtil.clearAll()
                    }

                    false -> TODO()
                    null -> TODO()
                }
                Log.d("error", message)
                mView.showToast(message, logout)
            }
        })
    }

    private fun returnMessage(error: String) {
        mView.showToast(error, false)
    }
}