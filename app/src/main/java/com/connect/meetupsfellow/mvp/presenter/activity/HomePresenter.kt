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
import com.connect.meetupsfellow.mvp.connector.activity.HomeConnector
import com.connect.meetupsfellow.mvp.model.activity.HomeModel
import com.connect.meetupsfellow.retrofit.request.RequestClearChat
import com.connect.meetupsfellow.retrofit.request.RequestFeed
import com.connect.meetupsfellow.retrofit.request.RequestReadNotification
import com.connect.meetupsfellow.retrofit.request.SaveContactDetailsReq
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ErrorResponse
import retrofit2.Response
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.Reader
import javax.inject.Inject

@Suppress("SENSELESS_COMPARISON")
class HomePresenter(internal var mView: HomeConnector.RequiredViewOps) : HomeConnector.PresenterOps,
    HomeConnector.RequiredPresenterOps {

    @Inject
    lateinit var apiConnect: ApiConnect

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    internal var mModel: HomeConnector.ModelOps = HomeModel(this)

    init {
        ArchitectureApp.component!!.inject(this@HomePresenter)
    }

    private lateinit var requestReadNotification: RequestReadNotification
    private lateinit var saveContactDetailsReq: SaveContactDetailsReq

    override fun readNotificationObject(requestReadNotification: RequestReadNotification) {
        this.requestReadNotification = requestReadNotification
    }

    override fun saveUserContactObject(saveContactDetailsReq: SaveContactDetailsReq) {
        this.saveContactDetailsReq = saveContactDetailsReq
    }


    private lateinit var requestFeed: RequestFeed
    private lateinit var requestClearChat: RequestClearChat
    private lateinit var eventId: String

    override fun addUserFeedObject(requestFeed: RequestFeed) {
        this.requestFeed = requestFeed
    }


    override fun addLikeEventObject(eventId: String) {
        this.eventId = eventId
    }

    override fun addClearChatObject(requestClearChat: RequestClearChat) {
        this.requestClearChat = requestClearChat
    }

    override fun callRetrofit(type: ConstantsApi) {
        when (Connected.isConnected()) {
            false -> {
                returnMessage(ArchitectureApp.instance!!.getString(R.string.internet_not_connected))
                Log.e("HomePresenter","internet_not_connected")
                return
            }

            true -> {}
        }

        when (type) {
            ConstantsApi.FETCH_FEEDS -> callForEventFeed(type)
            ConstantsApi.FETCH_NEARBY -> callForNearBy(type)
            ConstantsApi.EVENT_LIKE -> callForEventLike(type)
            ConstantsApi.FAVORITE_EVENT -> callForFavoriteEvent(type)
            ConstantsApi.FAVORITE_USER -> callForFavoriteUser(type)
            ConstantsApi.LOGOUT -> callForLogout(type)
            ConstantsApi.CLEAR_CHAT -> callForClearChat(type)
            ConstantsApi.SUPPORT_CLEARCHAT-> callForClearChat(type)
            ConstantsApi.FETCH_PROFILE ->  callForUserProfile(type)
            ConstantsApi.ADMIN_CONVERSATION -> callForAdminConversation(type)
            ConstantsApi.UPADTE_TIME_STAMP -> callForUpdateDateTime(type)
            ConstantsApi.REASON -> {
                mModel.fetchConversation(sharedPreferencesUtil.userId())
                callForFlagReason(type)
//                callForNotifications(ConstantsApi.NOTIFICATION)
            }
            ConstantsApi.ADMIN_ADS -> callForAdminAds(type)
            ConstantsApi.GET_MOST_RECENTLY_JOIND_USER -> callForMostRecentlyJoined(type)
            ConstantsApi.ADMIN_LAST_PUSH ->  callForAdminLastPush(type)
            ConstantsApi.READ_NOTIFICATION -> callForReadNotifications(type)
            ConstantsApi.SAVE_USER_CONTACTS -> callForSaveUserContact(type)

            else -> returnMessage(ArchitectureApp.instance!!.getString(R.string.invalid_request))
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
                                   // returnError(response.errorBody()!!.charStream())
                                }
                            }

                        } else {
                         //   returnMessage(ArchitectureApp.instance!!.getString(R.string.error_title))
                        }
                    }

                    override fun onCompleted() {

                    }

                })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun callForSaveUserContact(type: ConstantsApi) {
        try {
            val people = apiConnect.api.saveusercontactdetails(
                sharedPreferencesUtil.loginToken(),
                saveContactDetailsReq
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
                                   // returnError(response.errorBody()!!.charStream())
                                }
                            }

                        } else {
                         //   returnMessage(ArchitectureApp.instance!!.getString(R.string.error_title))
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
            val user = sharedPreferencesUtil.fetchUserProfile()

            val people =
                apiConnect.api.userProfile(
                    user.id,
                    sharedPreferencesUtil.loginToken(),
                    Constants.Random.random()
                )

            people.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response<CommonResponse>> {
                    override fun onError(e: Throwable?) {
                        e!!.printStackTrace()
                        //returnMessage("${e.message}")
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

    private fun callForFlagReason(type: ConstantsApi) {
        try {
            val reason = apiConnect.api.reasons(
                sharedPreferencesUtil.loginToken(),
                Constants.Random.random()
            )

            reason.subscribeOn(Schedulers.newThread())
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
                                    mModel.saveReason(Gson().toJson(response.body()!!.reason))
                                }

                                false -> {}
                            }
                        }
                    }

                    override fun onCompleted() {
                    }

                })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callForEventLike(type: ConstantsApi) {
        try {
            val event = apiConnect.api.likeEvent(
                eventId,
                sharedPreferencesUtil.loginToken(),
                Constants.Random.random()
            )

            event.subscribeOn(Schedulers.newThread())
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
                                    mModel.logoutUser()
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

    private fun callForAdminConversation(type: ConstantsApi) {
        try {
            val feed = apiConnect.api.fetchAdminConversation(
                sharedPreferencesUtil.loginToken(),
                Constants.Random.random()
            )

            feed.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response<CommonResponse>> {
                    override fun onError(e: Throwable?) {
                        e!!.printStackTrace()
                        returnMessage("${e.message}")
                    }

                    override fun onNext(response: Response<CommonResponse>?) {
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag,
                            "Admin Response is ::: $response"
                        )
                        if (response != null) {
                            when (response.isSuccessful) {
                                true -> {
                                    mModel.saveAdminChatCount("${response.body()!!.adminChatCount}")
                                    mView.showResponse(response.body()!!, type)
                                }

                                false -> {
                                    mModel.saveAdminChatCount("0")
                                    returnError(response.errorBody()!!.charStream())
                                }
                            }

                        } else {
                            mModel.saveAdminChatCount("0")
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

    private fun callForEventFeed(type: ConstantsApi) {
        try {
            val feed = apiConnect.api.eventFeeds(sharedPreferencesUtil.loginToken(), requestFeed)

            feed.subscribeOn(Schedulers.newThread())
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
                                    mModel.saveNotification("${response.body()!!.unReadMgsCount}")
                                    mView.showResponse(response.body()!!, type)
                                }

                                false -> {
                                    mModel.saveNotification("0")
                                    returnError(response.errorBody()!!.charStream())
                                }
                            }

                        } else {
                            mModel.saveNotification("0")
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

    private fun callForFavoriteEvent(type: ConstantsApi) {
        try {
            val feed = apiConnect.api.favoriteEvent(sharedPreferencesUtil.loginToken(), requestFeed)

            feed.subscribeOn(Schedulers.newThread())
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

    private fun callForFavoriteUser(type: ConstantsApi) {
        try {
            val feed = apiConnect.api.favoriteUser(sharedPreferencesUtil.loginToken(), requestFeed)

            feed.subscribeOn(Schedulers.newThread())
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


    private fun callForNearBy(type: ConstantsApi) {
        try {
            val nearby = apiConnect.api.nearbyUsers(sharedPreferencesUtil.loginToken(), requestFeed)
            nearby.subscribeOn(Schedulers.newThread())
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
                                    mModel.saveNotification("${response.body()!!.unReadMgsCount}")
                                    mView.showResponse(response.body()!!, type)
                                }

                                false -> {
                                    mModel.saveNotification("0")
                                    returnError(response.errorBody()!!.charStream())
                                }
                            }

                        } else {
                            mModel.saveNotification("0")
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



    private fun callForAdminAds(type: ConstantsApi) {
        try {
            val adminads = apiConnect.api.getAdminAds(sharedPreferencesUtil.loginToken() )
            adminads.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response<CommonResponse>> {
                    override fun onError(e: Throwable?) {
                        e!!.printStackTrace()
                       // returnMessage("${e.message}")
                    }

                    override fun onNext(response: Response<CommonResponse>?) {
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag,
                            "Response is ::: $response"
                        )
                        if (response != null) {
                            when (response.isSuccessful) {
                                true -> {
                                   // val loginResponse = response.body()
                                  //  mModel.saveNotification("${response.body()!!.unReadMgsCount}")
                                   mView.showResponse(response.body()!!, type)
                                }

                                false -> {
                                    mModel.saveNotification("0")
                                    returnError(response.errorBody()!!.charStream())
                                }
                            }

                        } else {
                            mModel.saveNotification("0")
                           // returnMessage(ArchitectureApp.instance!!.getString(R.string.error_title))
                        }
                    }

                    override fun onCompleted() {
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun callForAdminLastPush(type: ConstantsApi) {
        try {
            val adminads = apiConnect.api.getAdminLastPush(sharedPreferencesUtil.loginToken() )
            adminads.subscribeOn(Schedulers.newThread())
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
                                    // val loginResponse = response.body()
                                    //  mModel.saveNotification("${response.body()!!.unReadMgsCount}")
                                    mView.showResponse(response.body()!!, type)
                                }

                                false -> {
                                    mModel.saveNotification("0")
                                    returnError(response.errorBody()!!.charStream())
                                }
                            }

                        } else {
                            mModel.saveNotification("0")
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



    private fun callForMostRecentlyJoined(type: ConstantsApi) {
        try {

            val nearby = apiConnect.api.mostRecentUsers(sharedPreferencesUtil.loginToken(), requestFeed)
            nearby.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response<CommonResponse>> {
                    override fun onError(e: Throwable?) {
                        e!!.printStackTrace()
                        returnMessage("${e.message}")
                    }

                    override fun onNext(response: Response<CommonResponse>?) {
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag,
                            "Recent Response is ::: $response"
                        )
                        if (response != null) {
                            when (response.isSuccessful) {
                                true -> {
                                  //  mModel.saveNotification("${response.body()!!.unReadMgsCount}")
                                    mView.showResponse(response.body()!!, type)
                                }

                                false -> {
                                   // mModel.saveNotification("0")
                                    returnError(response.errorBody()!!.charStream())
                                }
                            }

                        } else {
                           // mModel.saveNotification("0")
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
                        mModel.saveNotification("0")
                        mView.showResponse(CommonResponse(), type)
                    }

                    override fun onNext(response: Response<CommonResponse>?) {
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag,
                            "Response is ::: $response"
                        )
                        if (response != null) {
                            when (response.isSuccessful) {
                                true -> {
                                    mModel.saveNotification(if (response.body()!!.notification.isEmpty()) "0" else "${response.body()!!.notification.size}")
                                    mView.showResponse(response.body()!!, type)
                                }

                                false -> {
                                    mModel.saveNotification("0")
                                    mView.showResponse(CommonResponse(), type)
                                }
                            }

                        } else {
                            mModel.saveNotification("0")
                            mView.showResponse(CommonResponse(), type)
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
                    null -> {}
                }
                mView.showToast(message, logout)
            }
        })
    }

    private fun returnMessage(error: String) {
        mView.showToast(error, false)
    }

    private fun callForUpdateDateTime(type: ConstantsApi) {
        try {
            val nearby = apiConnect.api.updateUserTimeStamp(sharedPreferencesUtil.loginToken(), requestFeed)
            Log.d("Login time",""+requestFeed.lastLoginTimeStamp)

            nearby.subscribeOn(Schedulers.newThread())
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
                                true -> {}
                                false -> {}
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