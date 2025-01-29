package com.connect.meetupsfellow.mvp.model.fragment

import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.mvp.connector.fragment.FavoriteEventConnector
import javax.inject.Inject

class FavoriteEventModel(internal var mPresenter: FavoriteEventConnector.RequiredPresenterOps) :
    FavoriteEventConnector.ModelOps {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.component!!.inject(this@FavoriteEventModel)
    }
//
//    override fun saveUserProfile(userProfileResponse: UserProfileResponse) {
//        sharedPreferencesUtil.saveUserProfile(Gson().toJson(userProfileResponse))
//    }

}