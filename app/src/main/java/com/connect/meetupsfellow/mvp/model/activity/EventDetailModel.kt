package com.connect.meetupsfellow.mvp.model.activity

import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.mvp.connector.activity.EventDetailsConnector
import javax.inject.Inject

class EventDetailModel(internal var mPresenter: EventDetailsConnector.RequiredPresenterOps) :
    EventDetailsConnector.ModelOps {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.component!!.inject(this@EventDetailModel)
    }

    override fun logoutUser() {
        sharedPreferencesUtil.clearAll()
    }

    override fun saveReason(reason: String) {
        sharedPreferencesUtil.saveReason(reason)
    }
}