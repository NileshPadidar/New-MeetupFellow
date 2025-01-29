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
import com.connect.meetupsfellow.mvp.connector.activity.UserProfileConnector
import com.connect.meetupsfellow.mvp.model.activity.UserProfileModel
import com.connect.meetupsfellow.retrofit.request.RequestReportUser
import com.connect.meetupsfellow.retrofit.request.RequestUserBlock
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ErrorResponse
import retrofit2.Response
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.Reader
import javax.inject.Inject

@Suppress("SENSELESS_COMPARISON")
class UserProfilePresenter(internal var mView: UserProfileConnector.RequiredViewOps) :
    UserProfileConnector.PresenterOps,
    UserProfileConnector.RequiredPresenterOps {

    @Inject
    lateinit var apiConnect: ApiConnect

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    internal var mModel: UserProfileConnector.ModelOps = UserProfileModel(this)

    init {
        ArchitectureApp.component!!.inject(this@UserProfilePresenter)
    }

    private lateinit var profileId: String
    private lateinit var requestUserBlock: RequestUserBlock
    private lateinit var requestReportUser: RequestReportUser


    override fun addUserProfileObject(profileId: String) {
        this.profileId = profileId
    }

    override fun addBlockUserObject(requestUserBlock: RequestUserBlock) {
        this.requestUserBlock = requestUserBlock
    }

    override fun addReportUserObject(requestReportUser: RequestReportUser) {
        this.requestReportUser = requestReportUser
    }

    override fun callRetrofit(type: ConstantsApi) {
        when (Connected.isConnected()) {
            false -> {
               // returnMessage(ArchitectureApp.instance!!.getString(R.string.internet_not_connected))
                Log.e("UserProfilePresenter","internet_not_connected")
                return
            }

            true -> {}
        }

        when (type) {
            ConstantsApi.FETCH_PROFILE -> callForUserProfile(type)
            ConstantsApi.USER_LIKE -> callForUserLike(type)
            ConstantsApi.LOGOUT -> callForLogout(type)
            ConstantsApi.BLOCK -> callForBlock(type)
            ConstantsApi.REPORT_USER -> callForReportUser(type)
            ConstantsApi.SHARE_PRIVATE -> callForShareAccess(type)
            ConstantsApi.LIKE_USER_PROFILE_MULTIPLE_TIME -> callForUserProfileLikeMultipleTime(type)
            ConstantsApi.LIKE_USER_PROFILE_HEART -> callForUserProfileHeart(type)
            else -> returnMessage(ArchitectureApp.instance!!.getString(R.string.invalid_request))
        }
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

    private fun callForReportUser(type: ConstantsApi) {
        try {
            val logout =
                apiConnect.api.reportUser(sharedPreferencesUtil.loginToken(), requestReportUser)

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

    private fun callForUserLike(type: ConstantsApi) {
        try {
            val event =
                apiConnect.api.likeUser(
                    profileId,
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
    private fun callForUserProfileHeart(type: ConstantsApi) {
        try {
            val event =
                apiConnect.api.likeUserProfileHeart(
                    profileId,"2",
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


    private fun callForUserProfileLikeMultipleTime(type: ConstantsApi) {
        try {
            val event =
                apiConnect.api.likeUserProfileMultipleTime(
                    profileId,
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

    private fun callForUserProfile(type: ConstantsApi) {
        try {
            val people =
                apiConnect.api.userProfile(
                    profileId,
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
                                    mView.showResponse(response.body()!!, type)
                                    callForFlagReason(ConstantsApi.REASON)
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
           Log.e("API_Call","callForFlagReason")
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
}