package com.connect.meetupsfellow.mvp.model.activity

import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.developement.interfaces.FirebaseUtil
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.mvp.connector.activity.LoginConnector
import com.connect.meetupsfellow.mvp.view.model.ProfileFirebase
import com.connect.meetupsfellow.retrofit.response.ResponseUserData
import com.connect.meetupsfellow.retrofit.response.ResponseUserProfileImages
import com.google.gson.Gson
import javax.inject.Inject

class LoginModel(internal var mPresenter: LoginConnector.RequiredPresenterOps) :
    LoginConnector.ModelOps {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    init {
        ArchitectureApp.component!!.inject(this@LoginModel)
    }

    override fun saveLoginToken(token: String) {
        sharedPreferencesUtil.saveLoginToken(token)
    }

    override fun saveDeviceToken(token: String) {
        sharedPreferencesUtil.saveDeviceToken(token)
    }

    override fun saveUserProfile(profile: String) {
        sharedPreferencesUtil.saveUserProfile(profile)
        updateUserProfileToFirebase(profile)
    }

    override fun userLoggedIn() {
        sharedPreferencesUtil.saveUserLoggedIn("true")
    }

    override fun saveSettings(settings: String) {
        sharedPreferencesUtil.saveSettings(settings)
    }

    private fun updateUserProfileToFirebase(profile: String) {
        val user = Gson().fromJson<ResponseUserData>(profile, ResponseUserData::class.java)
        if (user.id.isNotEmpty()) {
            firebaseUtil.saveUserProfile(
                ProfileFirebase(
                    "",
                    getApprovedImage(user.images),
                    true,
                    user.id.toInt(),
                    user.name,
                    sharedPreferencesUtil.fetchDeviceToken(),
                    sharedPreferencesUtil.fetchSettings().allowPush != 0,
                    1,
                    "" + Constants.getUTCDateTime.getUTCTime()
                )
            )
        }

    }

    private fun getApprovedImage(images: ArrayList<ResponseUserProfileImages>): String {
        var image = ""
        images.asSequence().forEach {
            when (it.status) {
                "approved" -> {
                    image = it.imagePath
                }
            }
        }
        return image
    }

}