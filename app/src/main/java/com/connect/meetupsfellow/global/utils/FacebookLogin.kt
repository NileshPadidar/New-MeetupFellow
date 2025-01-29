//package com.oit.datingondl.global.utils
//
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.os.Bundle
//import com.facebook.*
//import com.facebook.login.LoginManager
//import com.facebook.login.LoginResult
//import com.google.gson.Gson
//import com.oit.datingondl.facebook.response.ResponseFacebookUserProfile
//import com.oit.reegur.global.interfaces.OnFacebookLogin
//import com.oit.reegur.log.LogManager
//import java.util.*
//
//
//@SuppressLint("StaticFieldLeak")
///**
// * Created by Maheshwar on 09-08-2017.
// */
//object FacebookLogin {
//
//    private var activity: Activity? = null
//    private var callbackManager: CallbackManager? = null
//    private var login: OnFacebookLogin? = null
//    private var accessToken: AccessToken? = null
//
//    fun login(callbackManager: CallbackManager?, activity: Activity, login: OnFacebookLogin) {
//        FacebookLogin.activity = activity
//        FacebookLogin.callbackManager = callbackManager
//        FacebookLogin.login = login
//        accessToken = AccessToken.getCurrentAccessToken()
//        onFbLogin()
//    }
//
//    private fun onFbLogin() {
//        if (accessToken != null && !accessToken!!.isExpired) {
//            getProfileFromFacebook(accessToken!!)
//        } else {
//            LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("email",
//                    "public_profile", "user_birthday", "user_friends", "user_gender"))
//            LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
//                override fun onSuccess(loginResult: LoginResult) {
//                    LogManager.logger.i("DatingOnDl", "Object is : $loginResult")
//                    getProfileFromFacebook(loginResult.accessToken)
//                }
//
//                override fun onCancel() {
//                    LoginManager.getInstance().logOut()
//                    login!!.onFailure("Facebook Login process cancelled by user")
//                }
//
//                override fun onError(error: FacebookException) {
//                    login!!.onFailure(error.message.toString())
//                    LogManager.logger.i("DatingOnDl", "Error : ${error.message}")
//                }
//            })
//        }
//    }
//
//    private fun getProfileFromFacebook(accessToken: AccessToken) {
//
//        val graphRequest = GraphRequest.newMeRequest(accessToken) { _, response ->
//
//            LogManager.logger.i("DatingOnDl", "Object is : ${response.rawResponse}")
//            if (response.error == null) {
//                login!!.onSuccess(Gson().fromJson(response.rawResponse, ResponseFacebookUserProfile::class.java))
//            } else {
//                login!!.onFailure(response.error.errorMessage)
//            }
//        }
//
//        val parameters = Bundle()
//        parameters.putString("fields", "id,name,birthday,email,gender,picture.height(400),friends.limit(999)")
//        graphRequest.parameters = parameters
//        graphRequest.executeAsync()
//    }
//
//
//}