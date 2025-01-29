package com.connect.meetupsfellow.mvp.presenter.model

import android.util.Log
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.developement.interfaces.ApiConnect
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.interfaces.ErrorFlagStatusMessage
import com.connect.meetupsfellow.global.utils.Connected
import com.connect.meetupsfellow.global.utils.ErrorFlagStatus
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.model.UserPaymentConnector
import com.connect.meetupsfellow.retrofit.request.RequestUserPayment
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ErrorResponse
import com.google.gson.Gson
import retrofit2.Response
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.Reader
import javax.inject.Inject

class UserPaymentPresenter(internal var mView: UserPaymentConnector.RequiredViewOps) :
    UserPaymentConnector.PresenterOps, UserPaymentConnector.RequiredPresenterOps {

    private lateinit var requestUserPayment: RequestUserPayment

    override fun addCurrentLocationObject(requestUpdateLocation: RequestUserPayment) {
        this.requestUserPayment = requestUpdateLocation
    }

    @Inject
    lateinit var apiConnect: ApiConnect

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.component!!.inject(this@UserPaymentPresenter)
    }

    override fun callRetrofit(type: ConstantsApi) {
        when (Connected.isConnected()) {
            false -> {
                return
            }

            true -> {}
        }

        when (type) {
            ConstantsApi.USER_PAYMENT -> {
                callForuserPayment(type)
            }
            ConstantsApi.GET_PLAN_LIST -> {
                callForGetPlanList(type)
            }
            ConstantsApi.GET_TRANSECTION_HISTORY -> {
                callForGetTransectionHistory(type)
            }

            else -> {

            }
        }
    }

    private fun callForuserPayment(type: ConstantsApi) {
        try {
            val login = apiConnect.api.addUserPayment(
                sharedPreferencesUtil.loginToken(),
                requestUserPayment
            )

            login.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response<CommonResponse>> {
                    override fun onError(e: Throwable?) {
                        e!!.printStackTrace()
                        returnMessage("${e.message}")
                    }

                    override fun onNext(response: Response<CommonResponse>?) {
                        LogManager.logger.e(
                            ArchitectureApp.instance!!.tag,
                            "Response is setting::: $response"
                        )
                        if (response != null) {
                            when (response.isSuccessful) {
                                true -> {
                                    val loginResponse = response.body()!!
                                  //  mModel.saveSettings(Gson().toJson(loginResponse.settings))
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

    private fun callForGetTransectionHistory(type: ConstantsApi) {
        try {
            val login = apiConnect.api.getTransectionHistory(
                sharedPreferencesUtil.loginToken(),
                )

            login.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response<CommonResponse>> {
                    override fun onError(e: Throwable?) {
                        e!!.printStackTrace()
                        returnMessage("${e.message}")
                    }

                    override fun onNext(response: Response<CommonResponse>?) {
                        LogManager.logger.e(
                            ArchitectureApp.instance!!.tag,
                            "Response is setting::: $response"
                        )
                        if (response != null) {
                            when (response.isSuccessful) {
                                true -> {
                                    val loginResponse = response.body()!!
                                  //  mModel.saveSettings(Gson().toJson(loginResponse.settings))
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

    private fun callForGetPlanList(type: ConstantsApi) {
        try {
            val login = apiConnect.api.getPlanList(
                sharedPreferencesUtil.loginToken(),
                )

            login.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response<CommonResponse>> {
                    override fun onError(e: Throwable?) {
                        e!!.printStackTrace()
                        returnMessage("${e.message}")
                    }

                    override fun onNext(response: Response<CommonResponse>?) {
                        LogManager.logger.e(
                            ArchitectureApp.instance!!.tag,
                            "Response is setting::: $response"
                        )
                        if (response != null) {
                            when (response.isSuccessful) {
                                true -> {
                                    val loginResponse = response.body()!!
                                  //  mModel.saveSettings(Gson().toJson(loginResponse.settings))
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

    private fun returnMessage(error: String) {
        Log.e("returnMessage#$: ", "returnMessage: "+ error)
        mView.showToast(error, false)
    }
    private fun returnError(reader: Reader) {
        val error = Gson().fromJson(reader, ErrorResponse::class.java)
        ErrorFlagStatus.getErrorMessage(error, object : ErrorFlagStatusMessage {
            override fun status(message: String, logout: Boolean?) {
                Log.e("#%_Error: ", "87t423: "+ message)
                mView.showToast(message, logout)
            }
        })
    }
}