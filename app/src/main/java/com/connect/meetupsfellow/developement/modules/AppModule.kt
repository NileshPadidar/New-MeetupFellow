package com.connect.meetupsfellow.developement.modules

import android.app.Application
import com.connect.meetupsfellow.application.ArchitectureApp
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Jammwal on 08-02-2018.
 */
@Module
class AppModule(internal val app: ArchitectureApp) {
    @Provides
    @Singleton
    internal fun provideNowDoThisApp(): ArchitectureApp = app

    @Provides
    @Singleton
    internal fun provideApplication(app: ArchitectureApp): Application = app
}