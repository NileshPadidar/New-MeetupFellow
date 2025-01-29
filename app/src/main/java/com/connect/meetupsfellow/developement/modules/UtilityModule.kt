package com.connect.meetupsfellow.developement.modules

//import com.oit.reegur.developement.implementation.AwsS3UtilImpl
import android.app.Application
import com.connect.meetupsfellow.developement.implementation.DeviceUtilImpl
import com.connect.meetupsfellow.developement.implementation.FirebaseUtilImpl
import com.connect.meetupsfellow.developement.implementation.SharedPreferencesUtilImpl
import com.connect.meetupsfellow.developement.interfaces.DeviceUtil
import com.connect.meetupsfellow.developement.interfaces.FirebaseUtil
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.utils.UtilExceptionHandlerImpl
import com.connect.meetupsfellow.global.utils.UtilExceptionalHandler
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Jammwal on 08-02-2018.
 */
@Module
class UtilityModule {

    @Provides
    @Singleton
    internal fun provideException(application: Application): UtilExceptionalHandler =
        UtilExceptionHandlerImpl(application)

    @Provides
    @Singleton
    internal fun provideSharedPreference(application: Application): SharedPreferencesUtil =
        SharedPreferencesUtilImpl(application)

    @Provides
    @Singleton
    internal fun provideDeviceUtil(application: Application): DeviceUtil =
        DeviceUtilImpl(application)

    @Provides
    @Singleton
    internal fun provideFirebaseUtil(application: Application): FirebaseUtil =
        FirebaseUtilImpl(application)

//    @Provides
//    @Singleton
//    internal fun provideAwsS3Util(application: Application): AwsS3Util =
//        AwsS3UtilImpl(application)

}