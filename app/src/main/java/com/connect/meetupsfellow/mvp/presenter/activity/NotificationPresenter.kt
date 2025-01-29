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
import com.connect.meetupsfellow.mvp.connector.activity.NotificationConnector
import com.connect.meetupsfellow.retrofit.request.RequestReadNotification
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ErrorResponse
import retrofit2.Response
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.Reader
import javax.inject.Inject

@Suppress("SENSELESS_COMPARISON")
class NotificationPresenter(internal var mView: NotificationConnector.RequiredViewOps) :
    NotificationConnector.PresenterOps, NotificationConnector.RequiredPresenterOps {

    @Inject
    lateinit var apiConnect: ApiConnect

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    init {
        ArchitectureApp.component!!.inject(this@NotificationPresenter)
    }

    private lateinit var requestReadNotification: RequestReadNotification

    override fun readNotificationObject(requestReadNotification: RequestReadNotification) {
        this.requestReadNotification = requestReadNotification
    }

    override fun getVisitorListObject(requestReadNotification: RequestReadNotification) {
        this.requestReadNotification = requestReadNotification
    }

    override fun getFollowingListObject(requestReadNotification: RequestReadNotification) {
        this.requestReadNotification = requestReadNotification
    }

    override fun getEventInterestListObject(requestReadNotification: RequestReadNotification) {
        this.requestReadNotification = requestReadNotification
    }

    override fun getEventLikeListObject(requestReadNotification: RequestReadNotification) {
        this.requestReadNotification = requestReadNotification
    }

    override fun getFavouriteListObject(requestReadNotification: RequestReadNotification) {
        this.requestReadNotification = requestReadNotification
    }

    override fun getLikeUserListObject(requestReadNotification: RequestReadNotification) {
        this.requestReadNotification = requestReadNotification
    }

    override fun callRetrofit(type: ConstantsApi) {

        when (Connected.isConnected()) {
            false -> {
                returnMessage(ArchitectureApp.instance!!.getString(R.string.internet_not_connected))
                Log.e("NotificationPresenter","internet_not_connected")
                return
            }

            true -> {}
        }

        when (type) {
            ConstantsApi.NOTIFICATION -> callForNotifications(type)
            ConstantsApi.GET_PROFILE_VISITORS_LIST -> callProfileVisitorList(type)
            ConstantsApi.GET_FAVOURITE_USER_LIST -> callFavouriteUserList(type)
            ConstantsApi.GET_EVENT_INTEREST_USER_LIST -> callEventInterestUserList(type)
            ConstantsApi.GET_EVENT_LIKE_USER_LIST -> callEventLikeUserList(type)
            ConstantsApi.GET_LIKE_USER_LIST -> callLikesUserList(type)
            ConstantsApi.READ_NOTIFICATION -> callForReadNotifications(type)
            ConstantsApi.CLEAR_NOTIFICATION -> callForClearNotifications(type)
            ConstantsApi.DELETE_NOTIFICATION -> callForDeleteNotifications(type)
            ConstantsApi.GET_FOLLOWING_VISITORS_LIST -> callGetFollowingList(type)
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

    private fun callForNotifications(type: ConstantsApi) {
        try {
            val people = apiConnect.api.getNotifications(
                sharedPreferencesUtil.loginToken(),
                Constants.Random.random()
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
                                    LogManager.logger.i(
                                        ArchitectureApp.instance!!.tag,
                                        "Response is ::: ${response.body()}"
                                    )
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

    private fun callForReadNotifications(type: ConstantsApi) {
        try {
            val people = apiConnect.api.readNotification(
                sharedPreferencesUtil.loginToken(),
                requestReadNotification
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

    private fun callProfileVisitorList(type: ConstantsApi) {
        try {
            val people = apiConnect.api.getProfileVisitorList(
                sharedPreferencesUtil.loginToken(),
                requestReadNotification
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

    private fun callFavouriteUserList(type: ConstantsApi) {
        try {
            val people = apiConnect.api.getFavouriteList(
                sharedPreferencesUtil.loginToken(),
                requestReadNotification
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


   private fun callEventInterestUserList(type: ConstantsApi) {
        try {
            val people = apiConnect.api.getEventInterestUserList(
                sharedPreferencesUtil.loginToken(),
                requestReadNotification
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


   private fun callEventLikeUserList(type: ConstantsApi) {
        try {
            val people = apiConnect.api.getEventLikeUserList(
                sharedPreferencesUtil.loginToken(),
                requestReadNotification
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

    private fun callLikesUserList(type: ConstantsApi) {
        try {
            val people = apiConnect.api.getUserLikeList(
                sharedPreferencesUtil.loginToken(),
                requestReadNotification
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

    private fun callForDeleteNotifications(type: ConstantsApi) {
        try {
            val people = apiConnect.api.deleteNotification(
                sharedPreferencesUtil.loginToken(),
                requestReadNotification
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

    private fun callGetFollowingList(type: ConstantsApi) {
        try {
            val people = apiConnect.api.getFollowUserListNotification(
                sharedPreferencesUtil.loginToken(),
                requestReadNotification
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

    private fun callForClearNotifications(type: ConstantsApi) {
        try {
            val people = apiConnect.api.clearNotification(sharedPreferencesUtil.loginToken())

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