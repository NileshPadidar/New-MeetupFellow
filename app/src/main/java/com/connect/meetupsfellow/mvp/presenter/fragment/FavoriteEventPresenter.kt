package com.connect.meetupsfellow.mvp.presenter.fragment

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
import com.connect.meetupsfellow.mvp.connector.fragment.FavoriteEventConnector
import com.connect.meetupsfellow.mvp.model.fragment.FavoriteEventModel
import com.connect.meetupsfellow.retrofit.request.RequestFeed
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ErrorResponse
import retrofit2.Response
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.Reader
import javax.inject.Inject

@Suppress("SENSELESS_COMPARISON")
class FavoriteEventPresenter(internal var mView: FavoriteEventConnector.RequiredViewOps) :
    FavoriteEventConnector.PresenterOps, FavoriteEventConnector.RequiredPresenterOps {

    @Inject
    lateinit var apiConnect: ApiConnect

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    internal var mModel: FavoriteEventConnector.ModelOps = FavoriteEventModel(this)

    init {
        ArchitectureApp.component!!.inject(this@FavoriteEventPresenter)
    }

    private lateinit var requestFeed: RequestFeed
    private lateinit var eventId: String

    override fun addFavoriteEventObject(requestFeed: RequestFeed) {
        this.requestFeed = requestFeed
    }

    override fun addLikeEventObject(eventId: String) {
        this.eventId = eventId
    }

    override fun callRetrofit(type: ConstantsApi) {

        when (Connected.isConnected()) {
            false -> {
             //   returnMessage(ArchitectureApp.instance!!.getString(R.string.internet_not_connected))
                Log.e("FavoriteEventPresenter","internet_not_connected")
                return
            }
            true -> {}
        }

        when (type) {
            ConstantsApi.FAVORITE_EVENT -> callForFavoriteEvent(type)
            ConstantsApi.EVENT_LIKE -> callForEventLike(type)
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

    private fun callForFavoriteEvent(type: ConstantsApi) {
        try {
            val people =
                apiConnect.api.favoriteEvent(sharedPreferencesUtil.loginToken(), requestFeed)

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