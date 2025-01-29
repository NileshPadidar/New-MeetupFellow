package com.connect.meetupsfellow.mvp.model.activity

import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.mvp.connector.activity.EditProfileConnector
import javax.inject.Inject

class EditProfileModel(internal var mPresenter: EditProfileConnector.RequiredPresenterOps) :
    EditProfileConnector.ModelOps {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.component!!.inject(this@EditProfileModel)
    }

    override fun saveUserProfile(profile: String) {
        sharedPreferencesUtil.saveUserProfile(profile)
    }

}