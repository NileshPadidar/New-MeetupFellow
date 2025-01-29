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
import com.connect.meetupsfellow.mvp.connector.activity.EditProfileConnector
import com.connect.meetupsfellow.mvp.model.activity.EditProfileModel
import com.connect.meetupsfellow.retrofit.request.RequestCreateUser
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ErrorResponse
import retrofit2.Response
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.Reader
import java.net.URL
import javax.inject.Inject

@Suppress("SENSELESS_COMPARISON")
class EditProfilePresenter(internal var mView: EditProfileConnector.RequiredViewOps) :
    EditProfileConnector.PresenterOps, EditProfileConnector.RequiredPresenterOps {

    @Inject
    lateinit var apiConnect: ApiConnect

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    internal var mModel: EditProfileConnector.ModelOps = EditProfileModel(this)

    init {
        ArchitectureApp.component!!.inject(this@EditProfilePresenter)
    }

    private lateinit var requestCreateUser: RequestCreateUser
    private lateinit var profileId: String

    override fun addCreateUserObject(requestCreateUser: RequestCreateUser) {
        this.requestCreateUser = requestCreateUser
    }

    override fun addUserProfileObject(profileId: String) {
        this.profileId = profileId
    }

    override fun callRetrofit(type: ConstantsApi) {

        when (Connected.isConnected()) {
            false -> {
               // returnMessage(ArchitectureApp.instance!!.getString(R.string.internet_not_connected))
                Log.e("EditProfilePresenter","internet_not_connected")
                return
            }

            true -> {}
        }

        when (type) {
            ConstantsApi.CREATE_PROFILE -> callForCreateProfile(type)
            ConstantsApi.FETCH_PROFILE -> callForUserProfile(type)
            else -> returnMessage(ArchitectureApp.instance!!.getString(R.string.invalid_request))
        }
    }

    private fun callForCreateProfile(type: ConstantsApi) {
        try {

            Thread {
                LogManager.logger.e(
                    ArchitectureApp.instance!!.tag,
                    "sharedPreferencesUtil.loginToken() : ${sharedPreferencesUtil.loginToken()}"
                )
                val service = Multipart(
                    URL("${BuildConfig.URL_LIVE}updateProfile"),
                    sharedPreferencesUtil.loginToken()
                )
                Log.e("Token1: ","EditProfile: " +service.toString())
                service.addFormField("name", requestCreateUser.name)
                service.addFormField("email", requestCreateUser.email)
                service.addFormField("age", requestCreateUser.age)
                service.addFormField("phoneNumber", requestCreateUser.phoneNumber)
                service.addFormField("gender", requestCreateUser.gender)
                service.addFormField("ethnicity", requestCreateUser.ethnicity)
                service.addFormField("partnerStatus", requestCreateUser.partnerStatus)
                service.addFormField("aboutMe", requestCreateUser.aboutMe)
                service.addFormField("currentLocation", requestCreateUser.currentLocation)
                service.addFormField("currentLat", requestCreateUser.currentLat)
                service.addFormField("currentLong", requestCreateUser.currentLong)
                service.addFormField("homeLat", requestCreateUser.homeLat)
                service.addFormField("homeLocation", requestCreateUser.homeLocation)
                service.addFormField("homeLong", requestCreateUser.homeLong)
                service.addFormField("distance", requestCreateUser.distance)
                service.addFormField("twitterLink", requestCreateUser.twitterLink)
                service.addFormField("instagramLink", requestCreateUser.instagramLink)

                service.addFormField("xtubeLink", requestCreateUser.xtubeLink)

                service.addFormField("height", requestCreateUser.height)
                service.addFormField("weight", requestCreateUser.weight)

                service.addFormField("heightUnit", requestCreateUser.heightUnit)
                service.addFormField("weightUnit", requestCreateUser.weightUnit)

                service.addFormField("fistingId", requestCreateUser.fistingId)
                service.addFormField("glovePreference", requestCreateUser.glovePreference)
                service.addFormField("generalIntercourse", requestCreateUser.generalIntercourse)
                service.addFormField("condomPreference", requestCreateUser.condomPreference)
                service.addFormField("stiStatus", requestCreateUser.stiStatus)
                service.addFormField("PrEP", requestCreateUser.PrEP)
                service.addFormField("myIdentity", requestCreateUser.myIdentity)
                service.addFormField("intoIdentity", requestCreateUser.intoIdentity)

                if (null != requestCreateUser.image1) {
                    service.addFilePart(
                        "image_1_file",
                        requestCreateUser.image1!!,
                        requestCreateUser.image1!!.name,
                        requestCreateUser.image1!!.extension
                    )
                    service.addFormField("image_1_id", "${requestCreateUser.id1}")
                } else {
                    if (requestCreateUser.id1 > 0) {
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag,
                            "Image is : ${requestCreateUser.id1}"
                        )
                        service.addFormField("image_1_id", "${requestCreateUser.id1}")
                        service.addFormField("image_1_file", requestCreateUser.imagePath1)
                    }
                }

                if (null != requestCreateUser.image2) {
                    LogManager.logger.i(
                        ArchitectureApp.instance!!.tag,
                        "Uploaded image is : ${requestCreateUser.image2!!.name}"
                    )
                    LogManager.logger.i(
                        ArchitectureApp.instance!!.tag,
                        "Uploaded image is : ${requestCreateUser.image2!!.extension}"
                    )
                    service.addFilePart(
                        "image_2_file",
                        requestCreateUser.image2!!,
                        requestCreateUser.image2!!.name,
                        requestCreateUser.image2!!.extension
                    )
                    service.addFormField("image_2_id", "${requestCreateUser.id2}")
                } else {
                    if (requestCreateUser.id2 > 0) {
                        service.addFormField("image_2_id", "${requestCreateUser.id2}")
                        service.addFormField("image_2_file", requestCreateUser.imagePath2)

                    }
                }

                if (null != requestCreateUser.image3) {
                    service.addFilePart(
                        "image_3_file",
                        requestCreateUser.image3!!,
                        requestCreateUser.image3!!.name,
                        requestCreateUser.image3!!.extension
                    )
                    service.addFormField("image_3_id", "${requestCreateUser.id3}")
                } else {
                    if (requestCreateUser.id3 > 0) {
                        service.addFormField("image_3_id", "${requestCreateUser.id3}")
                        service.addFormField("image_3_file", requestCreateUser.imagePath3)
                    }
                }

                if (null != requestCreateUser.image4) {
                    service.addFilePart(
                        "image_4_file",
                        requestCreateUser.image4!!,
                        requestCreateUser.image4!!.name,
                        requestCreateUser.image4!!.extension
                    )
                    service.addFormField("image_4_id", "${requestCreateUser.id4}")
                } else {
                    if (requestCreateUser.id4 > 0) {
                        service.addFormField("image_4_id", "${requestCreateUser.id4}")
                        service.addFormField("image_4_file", requestCreateUser.imagePath4)
                    }
                }

                if (null != requestCreateUser.image5) {
                    service.addFilePart(
                        "image_5_file",
                        requestCreateUser.image5!!,
                        requestCreateUser.image5!!.name,
                        requestCreateUser.image5!!.extension
                    )
                    service.addFormField("image_5_id", "${requestCreateUser.id5}")
                } else {
                    if (requestCreateUser.id5 > 0) {
                        service.addFormField("image_5_id", "${requestCreateUser.id5}")
                        service.addFormField("image_5_file", requestCreateUser.imagePath5)
                    }
                }

                service.upload(object : OnFileUploadedListener {
                    override fun onFileUploadingSuccess(response: String) {
                        Handler(Looper.getMainLooper()).post {
                            val res = Gson().fromJson<CommonResponse>(
                                response,
                                CommonResponse::class.java
                            )
                            when (res.userInfo.profileStatus) {
                                Constants.ProfileStatus.Pending -> {
                                    sharedPreferencesUtil.clearAll()
                                }

                                Constants.ProfileStatus.Approved -> {
                                    mModel.saveUserProfile(Gson().toJson(res.userInfo))
                                }
                            }
                            mView.showResponse(res, type)
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
                        LogManager.logger.e(
                            ArchitectureApp.instance!!.tag,
                            "Response is EditProfile::: $response"
                        )
                        if (response != null) {
                            when (response.isSuccessful) {
                                true -> {
                                    val loginResponse = response.body()
//                                    mModel.saveUserProfile(Gson().toJson(loginResponse?.userInfo))
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
                            firebaseUtil.userOffline()
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
