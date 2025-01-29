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
import com.connect.meetupsfellow.mvp.connector.activity.ChatConnector
import com.connect.meetupsfellow.mvp.model.activity.ChatModel
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
class ChatPresenter(internal var mView: ChatConnector.RequiredViewOps) : ChatConnector.PresenterOps,
    ChatConnector.RequiredPresenterOps {

    @Inject
    lateinit var apiConnect: ApiConnect

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    internal var mModel: ChatConnector.ModelOps = ChatModel(this)

    private lateinit var profileId: String

    init {
        ArchitectureApp.component!!.inject(this@ChatPresenter)
    }

    private lateinit var requestUserBlock: RequestUserBlock
    private lateinit var sendConnectionRequest: SendConnectionRequest
    private lateinit var requestChatSave: RequestChatSave
    private lateinit var requestClearChat: RequestClearChat
    private lateinit var requestSendNotification: RequestSendNotification
    private lateinit var requestClearBadge: RequestClearBadge
    private lateinit var convoId: String

    override fun addBlockUserObject(requestUserBlock: RequestUserBlock) {
        this.requestUserBlock = requestUserBlock
    }

    override fun addSendConnectionObject(sendConnectionRequest: SendConnectionRequest) {
        this.sendConnectionRequest = sendConnectionRequest
    }

    override fun addChatSaveObject(requestChatSave: RequestChatSave) {
        this.requestChatSave = requestChatSave
    }

    override fun addChatObject(convoId: String) {
        this.convoId = convoId
    }

    override fun addSendNotificationObject(requestSendNotification: RequestSendNotification) {
        this.requestSendNotification = requestSendNotification
    }

    override fun addClearChatObject(requestClearChat: RequestClearChat) {
        this.requestClearChat = requestClearChat
    }

    override fun addClearBadge(requestClearBadge: RequestClearBadge) {
        this.requestClearBadge = requestClearBadge
    }

    override fun addUserProfileObject(profileId: String) {
        this.profileId = profileId
    }

    override fun callRetrofit(type: ConstantsApi) {

        when (Connected.isConnected()) {
            false -> {
                returnMessage(ArchitectureApp.instance!!.getString(R.string.internet_not_connected))
                Log.e("ChatPresenter","internet_not_connected")
                return
            }

            true -> {}
        }

        when (type) {
//            ConstantsApi.FETCH_PROFILE -> callForUserProfile(type)
            ConstantsApi.CLEAR_CHAT -> callForClearChat(type)
            ConstantsApi.LOGOUT -> callForLogout(type)
            ConstantsApi.BLOCK -> callForBlock(type)
            ConstantsApi.SEND_CONNECTION_REQUEST -> callForSendRequest(type)
            ConstantsApi.SAVE_CHAT -> callForSaveChat(type)
            ConstantsApi.FETCH_CHAT -> callForFetchChat(type)
            ConstantsApi.SEND_NOTIFICATION -> callForSendNotification(type)
            ConstantsApi.CLEAR_BADGE -> callForClearBadge(type)
            ConstantsApi.SHARE_PRIVATE -> callForShareAccess(type)
            ConstantsApi.REEGUR_UPDATENOTIFICATION -> callReegurUpdateNotification(type)


            else -> returnMessage(ArchitectureApp.instance!!.getString(R.string.invalid_request))
        }
    }

    private fun callForSendNotification(type: ConstantsApi) {
        try {
            val logout = apiConnect.api.sendNotification(
                sharedPreferencesUtil.loginToken(),
                requestSendNotification
            )
            logout.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response<CommonResponse>> {
                    override fun onError(e: Throwable?) {
                        e!!.printStackTrace()
                    }

                    override fun onNext(response: Response<CommonResponse>?) {
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag,
                            "Response is callForSendNotification ::: $response"
                        )
                    }

                    override fun onCompleted() {
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callForClearBadge(type: ConstantsApi) {
        try {
            val logout =
                apiConnect.api.clearBadge(sharedPreferencesUtil.loginToken(), requestClearBadge)
            logout.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response<CommonResponse>> {
                    override fun onError(e: Throwable?) {
                        e!!.printStackTrace()
                    }

                    override fun onNext(response: Response<CommonResponse>?) {
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag,
                            "Response is callForSendNotification ::: $response"
                        )
                    }

                    override fun onCompleted() {
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    //callReegurUpdateNotification

  private fun  callReegurUpdateNotification(type: ConstantsApi) {
        try {
            val logout =
                apiConnect.api.notificationReadAdminChat(sharedPreferencesUtil.loginToken())
            logout.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response<CommonResponse>> {
                    override fun onError(e: Throwable?) {
                        e!!.printStackTrace()
                    }

                    override fun onNext(response: Response<CommonResponse>?) {
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag,
                            "Response is callForSendNotification ::: $response"
                        )
                    }

                    override fun onCompleted() {
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    private fun callForBlock(type: ConstantsApi) {
        try {
            val logout =
                apiConnect.api.blockUser(sharedPreferencesUtil.loginToken(), requestUserBlock)

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
                                    mModel.blockUser(requestUserBlock.blockUserid)
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

    private fun callForSendRequest(type: ConstantsApi) {
        try {
            val logout =
                apiConnect.api.SendConnetionRequest(sharedPreferencesUtil.loginToken(), sendConnectionRequest)

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
                                   /// mModel.blockUser(requestUserBlock.blockUserid)
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

    private fun callForClearChat(type: ConstantsApi) {
        try {
            val logout =
                apiConnect.api.clearChat(sharedPreferencesUtil.loginToken(), requestClearChat)

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
//                                    mModel.blockUser(requestUserBlock.blockUserid)
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

    private fun callForFetchChat(type: ConstantsApi) {
        try {
            val logout =
                apiConnect.api.fetchChat(
                    convoId,
                    sharedPreferencesUtil.loginToken(),
                    Constants.Random.random()
                )

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

    private fun callForSaveChat(type: ConstantsApi) {
        try {
            val logout =
                apiConnect.api.saveChat(sharedPreferencesUtil.loginToken(), requestChatSave)
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
                                false -> {
                                    returnError(response.errorBody()!!.charStream())
                                }

                                true -> TODO()
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

//    private fun callForUserLike(type: ConstantsApi) {
//        try {
//            val event = apiConnect.api.likeUser(profileId, sharedPreferencesUtil.loginToken())
//
//            event.subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(object : Observer<Response<CommonResponse>> {
//                    override fun onError(e: Throwable?) {
//                        e!!.printStackTrace()
//                        returnMessage("${e.message}")
//                    }
//
//                    override fun onNext(response: Response<CommonResponse>?) {
//                        LogManager.logger.i(ArchitectureApp.instance!!.tag, "Response is ::: $response")
//                        if (response != null) {
//                            when (response.isSuccessful) {
//                                true -> {
//                                    mView.showResponse(response.body(), type)
//                                }
//
//                                false -> {
//                                    returnError(response.errorBody()!!.charStream())
//                                }
//                            }
//
//                        } else {
//                            returnMessage(ArchitectureApp.instance!!.getString(R.string.error_title))
//                        }
//                    }
//
//                    override fun onCompleted() {
//
//                    }
//
//                })
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }

//    private fun callForUserProfile(type: ConstantsApi) {
//        try {
//            val people = apiConnect.api.userProfile(profileId, sharedPreferencesUtil.loginToken())
//
//            people.subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(object : Observer<Response<CommonResponse>> {
//                    override fun onError(e: Throwable?) {
//                        e!!.printStackTrace()
//                        returnMessage("${e.message}")
//                    }
//
//                    override fun onNext(response: Response<CommonResponse>?) {
//                        LogManager.logger.i(ArchitectureApp.instance!!.tag, "Response is ::: $response")
//                        if (response != null) {
//                            when (response.isSuccessful) {
//                                true -> {
//                                    mView.showResponse(response.body(), type)
//                                }
//
//                                false -> {
//                                    returnError(response.errorBody()!!.charStream())
//                                }
//                            }
//
//                        } else {
//                            returnMessage(ArchitectureApp.instance!!.getString(R.string.error_title))
//                        }
//                    }
//
//                    override fun onCompleted() {
//
//                    }
//
//                })
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

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
                mView.showToast(message, logout)
            }
        })
    }

    private fun returnMessage(error: String) {
        mView.showToast(error, false)
    }

    private fun callForShareAccess(type: ConstantsApi) {
        try {
            val people = apiConnect.api.privateAccess(
                profileId, sharedPreferencesUtil.loginToken(), Constants.Random.random()
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
}