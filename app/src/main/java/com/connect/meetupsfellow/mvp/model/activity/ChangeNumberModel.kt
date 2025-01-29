package com.connect.meetupsfellow.mvp.model.activity

import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.mvp.connector.activity.ChangeNumberConnector
import javax.inject.Inject

class ChangeNumberModel(internal var mPresenter: ChangeNumberConnector.RequiredPresenterOps) :
    ChangeNumberConnector.ModelOps {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.component!!.inject(this@ChangeNumberModel)
    }

    override fun saveUserProfile(profile: String) {
        sharedPreferencesUtil.saveUserProfile(profile)
    }

}