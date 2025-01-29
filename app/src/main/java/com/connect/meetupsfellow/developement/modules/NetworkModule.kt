package com.connect.meetupsfellow.developement.modules

import android.app.Application
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.connect.meetupsfellow.constants.Constants

import com.connect.meetupsfellow.developement.implementation.ApiConnectImpl
import com.connect.meetupsfellow.developement.implementation.CachingControlInterceptor
import com.connect.meetupsfellow.developement.implementation.ConnectApiEndPointImpl
import com.connect.meetupsfellow.developement.interfaces.ApiConnect
import com.connect.meetupsfellow.developement.interfaces.EndPoint
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.utils.Connected
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by Jammwal on 08-02-2018.
 */
@Suppress("unused")
@Module
class NetworkModule {
    @Provides
    @Singleton
    internal fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setLenient()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
        return gsonBuilder.create()
    }

    @Provides
    @Singleton
    internal fun provideOkHttpCache(application: Application): Cache {
        // Install an HTTP cache in the application cache directory.
        val cacheDir = File(application.cacheDir, "http")
        val sizeOfCache = (10 * 1024 * 1024).toLong() // 10 MB
        return Cache(cacheDir, sizeOfCache)
    }

    @Provides
    @Singleton
    internal fun provideOkHttpClient(
        cache: Cache,
        logging: HttpLoggingInterceptor,
        cachingControlInterceptor: CachingControlInterceptor,
        headerInterceptor: Interceptor
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.networkInterceptors().add(cachingControlInterceptor)

        return builder
            .addInterceptor(headerInterceptor)
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .cache(cache)
            .build()
    }

    @Provides
    @Singleton
    internal fun provideRetrofitHeader(sharedPreferencesUtil: SharedPreferencesUtil?): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()

            val builder = original.newBuilder()

            val request = builder.build()

            builder.addHeader("content-type", "application/json")
                .method(original.method, original.body)

            builder.addHeader("Authorization-Key", "9J2MYE9O9K20T99R8I23S344H898A876N007")
            if (sharedPreferencesUtil != null) {
                if (sharedPreferencesUtil.isUserLogin()) {
                    builder.header(
                        "Authorization",
                        "Token " + sharedPreferencesUtil.loginToken().trim()
                    )
                }
            }

            if (Connected.isConnected()) {
                val cacheControl = request.cacheControl.toString()
                builder.header("Cache-Control", cacheControl)
            } else {
                builder.header("Cache-Control", "no-cache")
            }

            chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    internal fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return logging
    }

    @Provides
    internal fun cachingControlInterceptor(): CachingControlInterceptor =
        CachingControlInterceptor()

    // Change URL in CreateEventPresenter, CreateProfilePresenter, EditProfilePresenter, PrivateAlbumPresenter, PrivateAlbumImagesPresenter
    @Provides
    internal fun provideEndPoint(@Named(Constants.Injection.API_LIVE_URL) URL: String): EndPoint =
        ConnectApiEndPointImpl().setEndPoint(URL)

    @Provides
    @Singleton
    internal fun provideRetrofit(
        gson: Gson,
        okHttpClient: OkHttpClient,
        endPoint: EndPoint
    ): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .baseUrl(endPoint.getUrl())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    internal fun provideRetrofitApi(retrofit: Retrofit): ApiConnect = ApiConnectImpl(retrofit)
}
