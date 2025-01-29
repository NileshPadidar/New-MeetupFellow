package com.connect.meetupsfellow.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.google.firebase.database.DataSnapshot
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.constants.FirebaseChat
import com.connect.meetupsfellow.developement.interfaces.FirebaseUtil
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.firebase.FirebaseChatListener
import com.connect.meetupsfellow.log.LogManager
import javax.inject.Inject

class StickyService : Service() {

    private val mBinder = LocalBinder()

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    init {
        ArchitectureApp.component!!.inject(this@StickyService)
    }

    override fun onCreate() {
        super.onCreate()
//        fetchProfiles()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    inner class LocalBinder : Binder() {
        internal val service: StickyService
            get() = this@StickyService
    }

    internal fun fetchProfiles() {
        LogManager.logger.i(ArchitectureApp.instance!!.tag, "Profile is ::: Changed ")
        firebaseUtil.fetchUserProfile(object : FirebaseChatListener{
            override fun onChatEventListener(snapshot: DataSnapshot, chat: FirebaseChat) {

            }
        })
    }

}