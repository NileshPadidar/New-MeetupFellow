package com.connect.meetupsfellow.developement.modules

import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.BuildConfig
import dagger.Module
import dagger.Provides
import javax.inject.Named

/**
 * Created by Jammwal on 08-02-2018.
 * Update by Nilu on 12_Dec_24
 */
@Module
class PrimitivesModule {
    @Provides
    @Named(Constants.Injection.API_DEVELOPMENT_URL)
    fun provideDevelopmentURL(): String = BuildConfig.DEVELOPEMENT

    @Provides
    @Named(Constants.Injection.API_TESTING_URL)
    fun provideTestingURL(): String = BuildConfig.TESTING

    @Provides
    @Named(Constants.Injection.API_LIVE_URL)
    fun provideLiveURL(): String = BuildConfig.URL_LIVE


}