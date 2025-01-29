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
import com.connect.meetupsfellow.mvp.connector.activity.PrivateAlbumConnector
import com.connect.meetupsfellow.mvp.model.activity.PrivateAlbumModel
import com.connect.meetupsfellow.retrofit.request.ReqPrivetUserProfile
import com.connect.meetupsfellow.retrofit.request.RequestUserBlock
import com.connect.meetupsfellow.retrofit.request.setAccessPrivateAlbumPermissionReq
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
class PrivateAlbumPresenter(internal var mView: PrivateAlbumConnector.RequiredViewOps) :
    PrivateAlbumConnector.PresenterOps, PrivateAlbumConnector.RequiredPresenterOps {

    @Inject
    lateinit var apiConnect: ApiConnect

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    private lateinit var requestUserBlock: RequestUserBlock
    private lateinit var reqPrivetUserProfile: ReqPrivetUserProfile
    private lateinit var setAccessPrivateAlbumPermissionReq: setAccessPrivateAlbumPermissionReq
    private lateinit var id: String
    private var imageId = -1
    private lateinit var privateAlbumModel: ArrayList<com.connect.meetupsfellow.mvp.view.model.PrivateAlbumModel>

    private val mModel: PrivateAlbumConnector.ModelOps = PrivateAlbumModel(this)

    init {
        ArchitectureApp.component!!.inject(this@PrivateAlbumPresenter)
    }

    override fun addObjectForPrivateAlbum(id: String) {
        this.id = id
    }

    override fun addObjectForDeleteImage(id: Int) {
        this.imageId = id
    }

    override fun addObjectForPrivetProfile(reqPrivetUserProfile: ReqPrivetUserProfile) {
        this.reqPrivetUserProfile = reqPrivetUserProfile
    }

    override fun addObjectPrivateAlbum(privateAlbumModel: ArrayList<com.connect.meetupsfellow.mvp.view.model.PrivateAlbumModel>) {
        this.privateAlbumModel = privateAlbumModel
    }

    override fun addObjectSetMyPrivateAlbum(setAccessPrivateAlbumPermissionReq: setAccessPrivateAlbumPermissionReq) {
        this.setAccessPrivateAlbumPermissionReq = setAccessPrivateAlbumPermissionReq
    }

    override fun callRetrofit(type: ConstantsApi) {

        when (Connected.isConnected()) {
            false -> {
                returnMessage(ArchitectureApp.instance!!.getString(R.string.internet_not_connected))
                Log.e("PrivateAlbumPresenter","internet_not_connected")
                return
            }

            true -> {}
        }

        when (type) {
            ConstantsApi.LOGOUT -> callForLogout(type)
            ConstantsApi.PRIVATE_ACCESS -> callForPrivateAccess(type)
            ConstantsApi.PRIVATE_ACCESS_OTHERS -> callForPrivateAccessOthers(type)
            ConstantsApi.UPLOAD_PRIVATE -> callForUploadPrivate(type)
            ConstantsApi.PRIVATE_ALBUM -> callForPrivateAlbum(type)
            ConstantsApi.OTHER_PRIVATE_ALBUM -> callForPrivateAlbumOthers(type)
            ConstantsApi.DELETE_PRIVATE -> callForDeletePrivateImage(type)
            ConstantsApi.SHARE_PRIVATE -> callForShareAccess(type)
            ConstantsApi.REMOVE_PRIVATE -> callForRemoveShareAccess(type)
            ConstantsApi.MACK_PROFILE_PRIVET -> callForMackPrivet(type)
            ConstantsApi.MY_PRIVET_ALBUM_PERMISSION -> callForMyPrivateAlbumPermission(type)
            ConstantsApi.SET_ACCESS_PRIVET_ALBUM_PERMISSION -> callForSetMyPrivateAlbumPermission(type)
            else -> returnMessage(ArchitectureApp.instance!!.getString(R.string.invalid_request))
        }
    }

    private fun callForUploadPrivate(type: ConstantsApi) {
        try {

            Thread {
                LogManager.logger.e(
                    ArchitectureApp.instance!!.tag,
                    "API_URL : ${BuildConfig.URL_LIVE}addPrivatePic"
                )
                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag,
                    "sharedPreferencesUtil.loginToken() : ${sharedPreferencesUtil.loginToken()}"
                )
                val service = Multipart(
                    URL("${BuildConfig.URL_LIVE}addPrivatePic"),
                    sharedPreferencesUtil.loginToken()
                )

                service.addFormField("visibility", "public")
                if (privateAlbumModel.isNotEmpty()) {
                    privateAlbumModel.asSequence().forEach {
                        service.addFilePart(
                            "image_${(privateAlbumModel.indexOf(it) + 1)}_file",
                            it.image!!,
                            it.image!!.name,
                            it.image!!.extension
                        )

                        Log.e("FiledName", "image_${(privateAlbumModel.indexOf(it) + 1)}_file")
                        Log.d("FileExt", it.image!!.extension)
                        Log.d("File", it.image!!.isFile.toString())
                        Log.d("File", it.image!!.absolutePath)
                    }
                }

                service.upload(object : OnFileUploadedListener {
                    override fun onFileUploadingSuccess(response: String) {
                        Handler(Looper.getMainLooper()).post {
                            val res = Gson().fromJson<CommonResponse>(
                                response,
                                CommonResponse::class.java
                            )
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

    private fun callForDeletePrivateImage(type: ConstantsApi) {
        try {
            val logout =
                apiConnect.api.deletePrivateAlbumById(
                    "$imageId",
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

    private fun callForPrivateAlbumOthers(type: ConstantsApi) {
        try {
            val logout =
                apiConnect.api.getPrivateAlbumById(
                    id,
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

    private fun callForPrivateAlbum(type: ConstantsApi) {
        try {
            val logout =
                apiConnect.api.getPrivateAlbum(
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

    private fun callForShareAccess(type: ConstantsApi) {
        try {
            val people = apiConnect.api.privateAccess(
                id, sharedPreferencesUtil.loginToken(), Constants.Random.random()
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

    private fun callForRemoveShareAccess(type: ConstantsApi) {
        try {
            val people = apiConnect.api.removeAccessList(
                sharedPreferencesUtil.loginToken(), Constants.Random.random()
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

    private fun callForMackPrivet(type: ConstantsApi) {
        try {
            val people = apiConnect.api.mackProfilePrivet(
                sharedPreferencesUtil.loginToken(),
                reqPrivetUserProfile
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

    private fun callForPrivateAccess(type: ConstantsApi) {
        try {
            val people = apiConnect.api.fetchAccessList(
                sharedPreferencesUtil.loginToken(), Constants.Random.random()
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

    private fun callForMyPrivateAlbumPermission(type: ConstantsApi) {
        try {
            val people = apiConnect.api.myPrivateAlbumPermission(
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

    private fun callForSetMyPrivateAlbumPermission(type: ConstantsApi) {
        try {
            val people = apiConnect.api.setAccessPermissionForPrivateAlbum(
                sharedPreferencesUtil.loginToken(),
                setAccessPrivateAlbumPermissionReq
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

    private fun callForPrivateAccessOthers(type: ConstantsApi) {
        try {
            val people = apiConnect.api.fetchAccessListOthers(
                sharedPreferencesUtil.loginToken(), Constants.Random.random()
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

    private fun callForBlockedUsers(type: ConstantsApi) {
        try {
            val people = apiConnect.api.getBlockedUsers(
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

    private fun callForUnBlock(type: ConstantsApi) {
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
//                                    mModel.logoutUser()
                                    mModel.unBlockUser(requestUserBlock.blockUserid)
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

                    false -> TODO()
                    null -> TODO()
                }
                mView.showToast(message, logout)
            }
        })
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
