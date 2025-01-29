package com.connect.meetupsfellow.mvp.model.activity

import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.mvp.connector.activity.ProfileConnector
import javax.inject.Inject

class ProfileModel(internal var mPresenter: ProfileConnector.RequiredPresenterOps) :
    ProfileConnector.ModelOps {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.component!!.inject(this@ProfileModel)
    }

    override fun saveUserProfile(profile: String) {
        sharedPreferencesUtil.saveUserProfile(profile)
    }

}