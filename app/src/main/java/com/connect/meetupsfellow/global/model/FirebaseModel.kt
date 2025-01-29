package com.connect.meetupsfellow.global.model

import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.developement.interfaces.FirebaseUtil
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import javax.inject.Inject

class FirebaseModel : FirebaseUtil.ModelOps {

    init {
        ArchitectureApp.component!!.inject(this@FirebaseModel)
    }

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    override fun saveConversation(conversation: String) {
        sharedPreferencesUtil.saveConversation(conversation)
    }

    override fun saveUserProfiles(key: String, value: String) {
        sharedPreferencesUtil.saveOtherUserProfile(key, value)
    }
}