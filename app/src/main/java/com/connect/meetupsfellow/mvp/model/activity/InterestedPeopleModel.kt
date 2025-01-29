package com.connect.meetupsfellow.mvp.model.activity

import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.mvp.connector.activity.InterestedPeopleConnector
import javax.inject.Inject

class InterestedPeopleModel(internal var mPresenter: InterestedPeopleConnector.RequiredPresenterOps) :
    InterestedPeopleConnector.ModelOps {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.component!!.inject(this@InterestedPeopleModel)
    }

    override fun logoutUser() {
        sharedPreferencesUtil.clearAll()
    }
}