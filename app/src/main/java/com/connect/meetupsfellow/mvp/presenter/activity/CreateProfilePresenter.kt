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
import com.connect.meetupsfellow.global.utils.*
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.CreateProfileConnector
import com.connect.meetupsfellow.mvp.model.activity.CreateProfileModel
import com.connect.meetupsfellow.retrofit.request.RequestCreateUser
import com.connect.meetupsfellow.retrofit.request.SignupRequest
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ErrorResponse
import com.google.gson.JsonSyntaxException
import retrofit2.Response
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.Reader
import java.net.URL
import javax.inject.Inject

@Suppress("SENSELESS_COMPARISON")
class CreateProfilePresenter(internal var mView: CreateProfileConnector.RequiredViewOps) :
    CreateProfileConnector.PresenterOps, CreateProfileConnector.RequiredPresenterOps {

    @Inject
    lateinit var apiConnect: ApiConnect

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    internal var mModel: CreateProfileConnector.ModelOps = CreateProfileModel(this)

    init {
        ArchitectureApp.component!!.inject(this@CreateProfilePresenter)
    }

    private lateinit var requestCreateUser: RequestCreateUser
    private lateinit var signupRequest: SignupRequest


    override fun addCreateUserObject(requestCreateUser: RequestCreateUser) {
        this.requestCreateUser = requestCreateUser
    }

    override fun addSignUpUserObject(signupRequest: SignupRequest) {
        this.signupRequest = signupRequest
    }

    override fun callRetrofit(type: ConstantsApi) {

        when (Connected.isConnected()) {
            false -> {
                returnMessage(ArchitectureApp.instance!!.getString(R.string.internet_not_connected))
                Log.e("CreateProfilePresenter","internet_not_connected")
                return
            }

            true -> {}
        }

        when (type) {
            ConstantsApi.CREATE_PROFILE -> callForCreateProfile(type)
            ConstantsApi.SIGN_UP -> callForSignUpUser(type)
            ConstantsApi.FETCH_PROFILE -> callForUserProfile(type)
            ConstantsApi.FETCH_SELF_PROFILE -> callForSelfUserProfile(type)
            else -> returnMessage(ArchitectureApp.instance!!.getString(R.string.invalid_request))
        }
    }
    private fun callForSignUpUser(type: ConstantsApi) {
        try {
            Thread {
                LogManager.logger.e(
                    ArchitectureApp.instance!!.tag,
                    "sharedPreferencesUtil.loginToken() : ${URL("${BuildConfig.URL_LIVE}auth/signup")},${sharedPreferencesUtil.fetchDeviceToken()}"
                )
                var service = SignupMultipart(
                    URL("${BuildConfig.URL_LIVE}auth/signup"))
                Log.e("Req_SignUp:  ",service.toString())
                service.addFormField("name", signupRequest.name)
                service.addFormField("age", signupRequest.age)
                service.addFormField("dob", signupRequest.dob)
                service.addFormField("aboutMe", signupRequest.aboutMe)
                service.addFormField("currentLocation", signupRequest.currentLocation)
                service.addFormField("currentLong", signupRequest.currentLong)
                service.addFormField("currentLat", signupRequest.currentLat)
                service.addFormField("password", signupRequest.password)
                service.addFormField("isoCode", signupRequest.isoCode)
                service.addFormField("countryCode", signupRequest.countryCode)
                service.addFormField("deviceToken", signupRequest.deviceToken)
                service.addFormField("deviceType", "android")
                service.addFormField("gender", signupRequest.gender)
                service.addFormField("signup_type", signupRequest.signup_type)
                service.addFormField("phoneNumber", signupRequest.phoneNumber)
                service.addFormField("email", signupRequest.email)

              /*  if (signupRequest.signup_type.equals("phone")){
                    service.addFormField("phoneNumber", signupRequest.phoneNumber)
                    Log.d("Req_SignUp", "phoneNumber_ADD")
                }else{
                    service.addFormField("email", signupRequest.email)
                    Log.d("Req_SignUp", "Email_add")
                }*/

                Log.e("Req_SignUp","image11-:  ${signupRequest.image1}")

                Log.e("Req_SignUp","signupRequest.id1-:  ${signupRequest.id1}")

                Log.e("Req_SignUp:  ", "Data: $service")
                // Reegur code
                if (null != signupRequest.image1) {
                    service.addFilePart(
                        "image_1_file",
                        signupRequest.image1!!,
                        signupRequest.image1!!.name,
                        signupRequest.image1!!.extension
                    )
                    service.addFormField("image_1_id", "${signupRequest.id1}")
                } else {
                    if (signupRequest.id1 > 0) {
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag,
                            "Image is : ${signupRequest.id1}"
                        )
                        service.addFormField("image_1_id", "${signupRequest.id1}")
                        service.addFormField("image_1_file", signupRequest.imagePath1)
                    }
                }

                Log.e("Req_SignUp:  ","Data: "+service)

                // Reegur Code
                service.upload(object : OnSignupFileUploadedListener {
                    override fun onFileUploadingSuccess(response: String) {
                        Handler(Looper.getMainLooper()).post {
                            Log.e("UpdateResponse_Signup: ", response)
                            val res: CommonResponse = Gson().fromJson(response, CommonResponse::class.java)
                            try {
                                Log.e("Parsed_Message", res.message)
                                Log.e("Parsed_Token", res.token)
                                if (res.is_verify == 1){
                                    mModel.saveLoginToken(res.token)
                                }
                            } catch (e: JsonSyntaxException) {
                                Log.e("Parsing_Error", e.message ?: "Unknown error")
                            }

                            when (res.userInfo.profileStatus) {
                                Constants.ProfileStatus.Pending -> {
                                    sharedPreferencesUtil.clearAll()
                                }
                                Constants.ProfileStatus.Approved -> {
                                    mModel.saveUserProfile(Gson().toJson(res.userInfo))
                                    mModel.saveLoginToken(res.token)
                                    mModel.saveDeviceToken(res.userInfo.deviceToken)
                                }
                            }
                            mView.showResponse(res, type)
                        }
                    }

                    override fun onFileUploadingFailed(error: String) {
                        Handler(Looper.getMainLooper()).post {
                            Log.e("UpdateResponse_Error: ", error)
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

    private fun callForCreateProfile(type: ConstantsApi) {
        try {

            Thread {
                LogManager.logger.e(
                    ArchitectureApp.instance!!.tag,
                    "sharedPreferencesUtil.loginToken() : ${ URL("${BuildConfig.URL_LIVE}updateProfile")} ${ sharedPreferencesUtil.loginToken()}"
                )
                val service = Multipart(
                    URL("${BuildConfig.URL_LIVE}updateProfile"),
                    sharedPreferencesUtil.loginToken()
                )
                Log.e("CreatProfileService:  ",service.toString())
                service.addFormField("name", requestCreateUser.name)
               /// service.addFormField("email", requestCreateUser.email)
                service.addFormField("age", requestCreateUser.age)
                service.addFormField("dob", requestCreateUser.dob)
              ///  service.addFormField("phoneNumber", requestCreateUser.phoneNumber)
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
                service.addFormField("xtubeLink", requestCreateUser.xtubeLink)
                service.addFormField("instagramLink", requestCreateUser.instagramLink)

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

                if (!requestCreateUser.email.equals("")){
                    Log.e("CreatProfileService: ", "Update_email")
                    service.addFormField("email", requestCreateUser.email)
                }else{
                    Log.e("CreatProfileService: ", "Update_email_blank")
                  //  service.addFormField("email", requestCreateUser.email)
                }

                if (!requestCreateUser.phoneNumber.equals("")){
                    Log.e("CreatProfileService: ", "Update_phone")
                    service.addFormField("phoneNumber", requestCreateUser.phoneNumber)
                }else{
                    Log.e("CreatProfileService: ", "Update_phone_blank")
                   // service.addFormField("email", requestCreateUser.email)
                }

    /*

                if (null != requestCreateUser.image1) {
                    service.addFilePart(
                        "image_1_file",
                        requestCreateUser.image1!!,
                        requestCreateUser.image1!!.name,
                        requestCreateUser.image1!!.extension
                    )
                    service.addFormField("image_1_id", "${requestCreateUser.id1}")
                }

                if (null != requestCreateUser.image2) {
                    LogManager.logger.i(
                        ArchitectureApp.instance!!.tag,
                        "Uploaded image is : ${requestCreateUser.image2!!.name}"
                    )
                    service.addFilePart(
                        "image_2_file",
                        requestCreateUser.image2!!,
                        requestCreateUser.image2!!.name,
                        requestCreateUser.image2!!.extension
                    )
                    service.addFormField("image_2_id", "${requestCreateUser.id2}")
                }

                if (null != requestCreateUser.image3) {
                    service.addFilePart(
                        "image_3_file",
                        requestCreateUser.image3!!,
                        requestCreateUser.image3!!.name,
                        requestCreateUser.image3!!.extension
                    )
                    service.addFormField("image_3_id", "${requestCreateUser.id3}")
                }

                if (null != requestCreateUser.image4) {
                    service.addFilePart(
                        "image_4_file",
                        requestCreateUser.image4!!,
                        requestCreateUser.image4!!.name,
                        requestCreateUser.image4!!.extension
                    )
                    service.addFormField("image_4_id", "${requestCreateUser.id4}")
                }

                if (null != requestCreateUser.image5) {
                    service.addFilePart(
                        "image_5_file",
                        requestCreateUser.image5!!,
                        requestCreateUser.image5!!.name,
                        requestCreateUser.image5!!.extension
                    )
                    service.addFormField("image_5_id", "${requestCreateUser.id5}")
                }
*/

                Log.e("Reqest_Persen","image11-:  ${requestCreateUser.image1}")
                Log.e("Reqest_per","image22-:  ${requestCreateUser.image2}")
                Log.e("Reqest_per","image33-:  ${requestCreateUser.image3}")
                Log.e("Reqest_per","image44-:  ${requestCreateUser.image4}")

                // Reegur code
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

                Log.e("Req CreatProfile:  ","Data: "+service)
                  /*  service.upload(object : OnFileUploadedListener {
                        override fun onFileUploadingSuccess(response: String) {
                            Handler(Looper.getMainLooper()).post {
                                mView.showResponse(
                                    Gson().fromJson<CommonResponse>(
                                        response,
                                        CommonResponse::class.java
                                    ), type
                                )
                                Log.e("UpdateResponse: ", response)
                            }
                        }

                        override fun onFileUploadingFailed(error: String) {
                            Handler(Looper.getMainLooper()).post {
                                when (Constants.Json.validJson(error)) {
                                    true -> returnError(error)
                                    false -> returnMessage(error)
                                }
                                Log.e("UpdateResponse_Error: ", error)
                            }
                        }
                    })*/

               // Reegur Code
               service.upload(object : OnFileUploadedListener {
                    override fun onFileUploadingSuccess(response: String) {
                        Handler(Looper.getMainLooper()).post {
                            Log.e("UpdateResponse: ", "onFileUploadingSuccess ${response.isNotEmpty()}")
                            val res = Gson().fromJson<CommonResponse>(
                                response,
                                CommonResponse::class.java
                            )
                            Log.e("UpdateResponse: ", response)

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

                            Log.e("UpdateResponse_Error: ", error)
                        }
                    }

                })


            }.start()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callForUserProfile(type: ConstantsApi) {
        Log.e("Token: CreatProfile:  ","callForUserProfile")
        try {

            val login = apiConnect.api.getUserProfile(
                sharedPreferencesUtil.loginToken(),
                Constants.Random.random(),
                Constants.Random.random()
            )
        Log.e("Token: ",login.toString())
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
                            "Response is userProfile::: $response"
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

    private fun callForSelfUserProfile(type: ConstantsApi) {
        try {
            val user = sharedPreferencesUtil.fetchUserProfile()

            LogManager.logger.e(
                ArchitectureApp.instance!!.tag,
                " FETCH_Self_PROFILE: ${URL("${BuildConfig.URL_LIVE}profile/${user.id}")}, ${sharedPreferencesUtil.fetchDeviceToken()}"
            )
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
                            "FETCH_PROFILE Response is ::: $response"
                        )
                        if (response != null) {
                            when (response.isSuccessful) {
                                true -> {
                                    val response: CommonResponse? = response.body()
                                    mModel.saveUserProfile(Gson().toJson(response?.userInfo))
                                    mView.showResponse(response!!, type)
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

    private fun callForSettings(type: ConstantsApi) {
        Log.e("Token: CreatProfile:  ","callForSettings")
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
                        LogManager.logger.e(
                            ArchitectureApp.instance!!.tag,
                            "Response is setting::: $response"
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
                Log.e("#%_Error: ", "87t423: "+ message)
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
                Log.e("returnError@$: ", message)
                mView.showToast(message, logout)
            }
        })
    }

    private fun returnMessage(error: String) {
        Log.e("returnMessage#$: ", "returnMessage: "+ error)
        mView.showToast(error, false)
    }
}
