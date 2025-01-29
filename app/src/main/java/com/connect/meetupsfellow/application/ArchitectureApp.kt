package com.connect.meetupsfellow.application

import AppStatusTracker
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import cat.ereza.customactivityoncrash.CustomActivityOnCrash
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.developement.components.ApplicationComponent
import com.connect.meetupsfellow.developement.components.DaggerApplicationComponent
import com.connect.meetupsfellow.developement.modules.AppModule
import com.connect.meetupsfellow.developement.modules.NetworkModule
import com.connect.meetupsfellow.developement.modules.PrimitivesModule
import com.connect.meetupsfellow.developement.modules.UtilityModule
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.view.activities.CustomErrorActivity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber
import timber.log.Timber.DebugTree


class ArchitectureApp : MultiDexApplication() {

    var tag = "MEETUP"

    companion object {
        var instance: ArchitectureApp? = null
            private set
        var component: ApplicationComponent? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initializeDagger()
        initializeCustomCrash()
        initializeCrashlytics()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        Timber.plant(DebugTree())
        AppStatusTracker.init(this)
    }

    private fun initializeDagger() {
        component = DaggerApplicationComponent.builder()
            .appModule(AppModule(this)) // Pass the application context
            .networkModule(NetworkModule()) // Add other modules
            .primitivesModule(PrimitivesModule())
            .utilityModule(UtilityModule())
            .build()
    }

    private fun initializeCustomCrash() {
        CaocConfig.Builder.create()
            .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT)
            .errorActivity(CustomErrorActivity::class.java)
            .eventListener(CustomEventListener())
            .apply()
    }

  /*  private fun initializeCrashlytics() {
        when (Constants.EnableCrashlytics.ENABLE) {
            true -> {
                val fabric = Fabric.Builder(this)
                    .kits(Crashlytics())
                    .debuggable(true)
                    .build()
                Fabric.with(fabric)
            }
            false -> {}
        }
    }*/
  private fun initializeCrashlytics() {
      if (Constants.EnableCrashlytics.ENABLE) {
          // Enable Crashlytics debugging
          FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
          FirebaseCrashlytics.getInstance().log("Crashlytics initialized")
      } else {
          FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
      }
  }

    private class CustomEventListener : CustomActivityOnCrash.EventListener {
        override fun onLaunchErrorActivity() {
            LogManager.logger.i(ArchitectureApp().tag, "onLaunchErrorActivity()")
        }

        override fun onRestartAppFromErrorActivity() {
            LogManager.logger.i(ArchitectureApp().tag, "onRestartAppFromErrorActivity()")
        }

        override fun onCloseAppFromErrorActivity() {
            LogManager.logger.i(ArchitectureApp().tag, "onCloseAppFromErrorActivity()")
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        LogManager.logger.i(ArchitectureApp().tag, "onTerminate()")
    }

}