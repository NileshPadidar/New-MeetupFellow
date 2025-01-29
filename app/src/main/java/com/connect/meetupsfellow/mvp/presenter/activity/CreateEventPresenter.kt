package com.connect.meetupsfellow.mvp.presenter.activity

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.gson.Gson
import com.connect.meetupsfellow.BuildConfig
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
import com.connect.meetupsfellow.global.utils.Multipart
import com.connect.meetupsfellow.global.utils.OnFileUploadedListener
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.CreateEventConnector
import com.connect.meetupsfellow.mvp.model.activity.CreateEventModel
import com.connect.meetupsfellow.retrofit.request.RequestCreateEvent
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ErrorResponse
import java.net.URL
import javax.inject.Inject

@Suppress("SENSELESS_COMPARISON")
class CreateEventPresenter(internal var mView: CreateEventConnector.RequiredViewOps) :
    CreateEventConnector.PresenterOps,
    CreateEventConnector.RequiredPresenterOps {

    @Inject
    lateinit var apiConnect: ApiConnect

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    internal var mModel: CreateEventConnector.ModelOps = CreateEventModel(this)

    init {
        ArchitectureApp.component!!.inject(this@CreateEventPresenter)
    }

    private lateinit var requestCreateEvent: RequestCreateEvent


    override fun addCreateEventObject(requestCreateEvent: RequestCreateEvent) {
        this.requestCreateEvent = requestCreateEvent
    }

    override fun callRetrofit(type: ConstantsApi) {

        when (Connected.isConnected()) {
            false -> {
                returnMessage(ArchitectureApp.instance!!.getString(R.string.internet_not_connected))
                Log.e("CreateEventPresenter","internet_not_connected")
                return
            }

            true -> {}
        }

        when (type) {
            ConstantsApi.CREATE_EVENT -> callForCreateEvent(type)
            else -> returnMessage(ArchitectureApp.instance!!.getString(R.string.invalid_request))
        }
    }

    private fun callForCreateEvent(type: ConstantsApi) {
        try {

            Thread {
                LogManager.logger.e(
                    ArchitectureApp.instance!!.tag,
                    "URL::: ${BuildConfig.URL_LIVE}event/create"
                )
                val service = Multipart(
                    URL("${BuildConfig.URL_LIVE}event/create"),
                    sharedPreferencesUtil.loginToken()
                )
                service.addFormField("title", requestCreateEvent.title)
                service.addFormField("startDate", requestCreateEvent.start)
                service.addFormField("endDate", requestCreateEvent.end)
                service.addFormField("startTime", requestCreateEvent.startTime)
                service.addFormField("endTime", requestCreateEvent.endTime)
                service.addFormField("location", requestCreateEvent.location)
                service.addFormField("locationLat", requestCreateEvent.locationLat)
                service.addFormField("locationLong", requestCreateEvent.locationLong)
                service.addFormField("description", requestCreateEvent.description)
                service.addFormField("websiteUrl", requestCreateEvent.websiteUrl)
                service.addFormField("buyTicketUrl", requestCreateEvent.buyTicketUrl)
                service.addFormField("timeZone", requestCreateEvent.timeZone)

                Log.e("Event_IMG","${requestCreateEvent.image}")
                Log.e("Event_IMG","Name: ${requestCreateEvent.image!!.name}")
                Log.e("Event_IMG","extension: ${requestCreateEvent.image!!.extension}")
                if (requestCreateEvent.image != null){
                    service.addFilePart(
                        "image",
                        requestCreateEvent.image!!,
                        requestCreateEvent.image!!.name,
                        requestCreateEvent.image!!.extension
                    )
                }

                service.upload(object : OnFileUploadedListener {
                    override fun onFileUploadingSuccess(response: String) {
                        Handler(Looper.getMainLooper()).post {
                            mView.showResponse(
                                Gson().fromJson<CommonResponse>(
                                    response,
                                    CommonResponse::class.java
                                ), type
                            )
                        }
                    }

                    override fun onFileUploadingFailed(error: String) {
                        Handler(Looper.getMainLooper()).post {
                            when (Constants.Json.validJson(error)) {
                                true -> returnError(error)
                                false -> returnMessage(error)
                            }
                        }
                    }

                })
            }.start()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun returnError(reader: String) {
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
