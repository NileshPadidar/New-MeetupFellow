package com.connect.meetupsfellow.mvp.model.activity

import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.mvp.connector.activity.CreateEventConnector
import javax.inject.Inject

class CreateEventModel(internal var mPresenter: CreateEventConnector.RequiredPresenterOps) :
    CreateEventConnector.ModelOps {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.component!!.inject(this@CreateEventModel)
    }
//
//    override fun saveUserProfile(userProfileResponse: UserProfileResponse) {
//        sharedPreferencesUtil.saveUserProfile(Gson().toJson(userProfileResponse))
//    }

}