package com.connect.meetupsfellow.mvp.model.activity

import com.google.gson.Gson
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.developement.interfaces.FirebaseUtil
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.mvp.connector.activity.SettingsConnector
import javax.inject.Inject

class SettingsModel(internal var mPresenter: SettingsConnector.RequiredPresenterOps) :
    SettingsConnector.ModelOps {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    init {
        ArchitectureApp.component!!.inject(this@SettingsModel)
    }

    override fun logoutUser() {
        firebaseUtil.userLogout()
        sharedPreferencesUtil.clearAll()
    }

    override fun deactivateUser() {
        firebaseUtil.deactivateAccount()
        sharedPreferencesUtil.clearAll()
    }

    override fun deleteUser() {
        firebaseUtil.deleteAccount()
        sharedPreferencesUtil.clearAll()
    }

    override fun updateSettings(update: Int, type: ConstantsApi) {
        val setting = sharedPreferencesUtil.fetchSettings()
        when (type) {
            ConstantsApi.ALLOW_PUSH -> {
                setting.allowPush = update
            }

            ConstantsApi.ALLOW_EVENT -> {
                setting.allowEvent = update
            }

            ConstantsApi.ALLOW_EVENT_UPDATE -> {
                setting.allowEventUpdate = update
            }

            ConstantsApi.UNIT_IMPERIAL -> {
                setting.unit = update
            }

            ConstantsApi.UNIT_METRIC -> {
                setting.unit = update
            }

            ConstantsApi.ALLOW_NSFW_SETTING ->{
                setting.nsfw = update
            }



            else -> {
            }
        }
        sharedPreferencesUtil.saveSettings(Gson().toJson(setting))

    }
}