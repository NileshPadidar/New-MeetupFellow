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
import com.connect.meetupsfellow.mvp.connector.activity.ConnectionRequestConnector
import com.connect.meetupsfellow.mvp.model.activity.ConnectionRequestModel
import com.connect.meetupsfellow.retrofit.request.CancelSendConnectionRequest
import com.connect.meetupsfellow.retrofit.request.RequestConnectionRequest
import com.connect.meetupsfellow.retrofit.request.UnfriendConnectionReq
import com.connect.meetupsfellow.retrofit.request.getConectionRequest
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ErrorResponse
import retrofit2.Response
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.Reader
import javax.inject.Inject

@Suppress("SENSELESS_COMPARISON")
class ConnectionRequestPresenter(internal var mView: ConnectionRequestConnector.RequiredViewOps) :
    ConnectionRequestConnector.PresenterOps,
    ConnectionRequestConnector.RequiredPresenterOps {

    @Inject
    lateinit var apiConnect: ApiConnect

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    private lateinit var requestConnectionRequest: RequestConnectionRequest
    private lateinit var cancelSendConnectionRequest: CancelSendConnectionRequest
    private lateinit var unfriendConnectionReq: UnfriendConnectionReq
    private lateinit var getConnectionRequestData : getConectionRequest

    private val mModel: ConnectionRequestConnector.ModelOps = ConnectionRequestModel(this)

    init {
        ArchitectureApp.component!!.inject(this@ConnectionRequestPresenter)
    }

    override fun addConnectionRequestObject(requestConnectionRequest: RequestConnectionRequest) {
        this.requestConnectionRequest = requestConnectionRequest
    }

    override fun cancelConnectionRequestObject(cancelSendConnectionRequest: CancelSendConnectionRequest) {
        this.cancelSendConnectionRequest = cancelSendConnectionRequest
    }
    override fun unfriendConnectionRequestObject(unfriendConnectionReq: UnfriendConnectionReq) {
        this.unfriendConnectionReq = unfriendConnectionReq
    }
  override fun getConnectionRequestObject(getConectionRequest: getConectionRequest) {
        this.getConnectionRequestData = getConectionRequest
    }

    override fun callRetrofit(type: ConstantsApi) {

        when (Connected.isConnected()) {
            false -> {
                returnMessage(ArchitectureApp.instance!!.getString(R.string.internet_not_connected))
                Log.e("ConnectionRequestPr","internet_not_connected")
                return
            }

            true -> {}
        }

        when (type) {
            ConstantsApi.ACCEPT_CONNECTION_REQUEST -> callForAcceptRequest(type)
            ConstantsApi.DECLINE_CONNECTION_REQUEST -> callForDeclineRequest(type)
            ConstantsApi.GET_CONNECTION_REQUEST -> callForGetConnectionRequest(type)
            ConstantsApi.CANCEL_SEND_CONNECTION_REQUEST -> callForCancelConnectionRequest(type)
            ConstantsApi.UNFRIEND_CONNECTION_REQUEST -> callForUnfriendConnectionRequest(type)
            ConstantsApi.GET_MY_CONNECTIONS -> callForGetMyConnection(type)
            ConstantsApi.LOGOUT -> callForLogout(type)
            else -> returnMessage(ArchitectureApp.instance!!.getString(R.string.invalid_request))
        }
    }

    private fun callForLogout(type: ConstantsApi) {
        try {
            val logout =
                apiConnect.api.logout(sharedPreferencesUtil.loginToken(), Constants.Random.random())

            logout.subscribeOn(Schedulers.newThread())
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
//                                    mModel.logoutUser()
                                    mView.showResponse(response.body()!!, type)
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

    private fun callForGetConnectionRequest(type: ConstantsApi) {
        try {
            val people = apiConnect.api.getConnectionRequest(
                sharedPreferencesUtil.loginToken(),
                getConnectionRequestData
            )

            people.subscribeOn(Schedulers.newThread())
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
                                    mView.showResponse(response.body()!!, type)
                                }

                                false -> {
                                    Log.e("Error","getconnectionrequestlistError: ${response.errorBody()
                                        ?.charStream()}")
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

    private fun callForGetMyConnection(type: ConstantsApi) {
        try {
            val people = apiConnect.api.getMyConnectionsRequest(
                sharedPreferencesUtil.loginToken()
            )

            people.subscribeOn(Schedulers.newThread())
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
                                    mView.showResponse(response.body()!!, type)
                                }

                                false -> {
                                    Log.e("Error","getconnectionrequestlistError: ${response.errorBody()
                                        ?.charStream()}")
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

    private fun callForAcceptRequest(type: ConstantsApi) {
        try {
            val logout =
                apiConnect.api.acceptConnectionRequest(sharedPreferencesUtil.loginToken(), requestConnectionRequest)

            logout.subscribeOn(Schedulers.newThread())
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
//                                    mModel.logoutUser()
                                   // mModel.acceptConnectionRequest(requestConnectionRequest.blockUserid)
                                    mView.showResponse(response.body()!!, type)
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

  private fun callForDeclineRequest(type: ConstantsApi) {
        try {
            val logout =
                apiConnect.api.declineConnectionRequest(sharedPreferencesUtil.loginToken(), requestConnectionRequest)

            logout.subscribeOn(Schedulers.newThread())
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
//                                    mModel.logoutUser()
                                 ///   mModel.declineConnectionRequest(requestConnectionRequest.blockUserid)
                                    mView.showResponse(response.body()!!, type)
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

 private fun callForCancelConnectionRequest(type: ConstantsApi) {
        try {
            val logout =
                apiConnect.api.cancelSendConnectionRequest(sharedPreferencesUtil.loginToken(), cancelSendConnectionRequest)

            logout.subscribeOn(Schedulers.newThread())
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
//                                    mModel.logoutUser()
                                 ///   mModel.declineConnectionRequest(requestConnectionRequest.blockUserid)
                                    mView.showResponse(response.body()!!, type)
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

private fun callForUnfriendConnectionRequest(type: ConstantsApi) {
        try {
            val logout =
                apiConnect.api.unfriendConnectionRequest(sharedPreferencesUtil.loginToken(), unfriendConnectionReq)

            logout.subscribeOn(Schedulers.newThread())
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
//                                    mModel.logoutUser()
                                 ///   mModel.declineConnectionRequest(requestConnectionRequest.blockUserid)
                                    mView.showResponse(response.body()!!, type)
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

                    false -> {}
                    null -> TODO()
                }
                mView.showToast(message, logout)
            }
        })
    }

    private fun returnMessage(error: String) {
        mView.showToast(error, false)
    }
}