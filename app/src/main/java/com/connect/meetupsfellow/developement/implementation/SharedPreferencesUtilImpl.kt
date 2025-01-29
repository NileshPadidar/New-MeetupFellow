package com.connect.meetupsfellow.developement.implementation

import android.content.Context
import com.android.billingclient.api.SkuDetails
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.sharedPreferences.SharedPreferencesBean
import com.connect.meetupsfellow.global.sharedPreferences.SharedPreferencesCustom
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.view.model.ChatModel
import com.connect.meetupsfellow.mvp.view.model.ConversationModel
import com.connect.meetupsfellow.mvp.view.model.ProfileFirebase
import com.connect.meetupsfellow.retrofit.response.ResponseFlagReason
import com.connect.meetupsfellow.retrofit.response.ResponseSettings
import com.connect.meetupsfellow.retrofit.response.ResponseUserData

class SharedPreferencesUtilImpl(internal var context: Context) : SharedPreferencesUtil {


    override fun fetchAdminChatCount(): String {
        return fetchDetails(SharedPreferencesBean.KEY_ADMIN_COUNT)
    }

    override fun premiunNearby(value: String): Boolean {
        val objUser = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS)
        objUser.putString(SharedPreferencesBean.KEY_PURCHASE_NEARBY, value)
        return true
    }

    override fun fetchPremiunNearby(): Boolean {
        return fetchDetails(SharedPreferencesBean.KEY_PURCHASE_NEARBY).isNotEmpty()
    }

    override fun premiunChat(value: String): Boolean {
        val objUser = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS)
        objUser.putString(SharedPreferencesBean.KEY_PURCHASE_CHAT, value)
        return true
    }

    override fun fetchPremiunChat(): Boolean {
        return fetchDetails(SharedPreferencesBean.KEY_PURCHASE_CHAT).isNotEmpty()
    }

    override fun saveAdminChatCount(value: String): Boolean {
        val objUser = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS)
        objUser.putString(SharedPreferencesBean.KEY_ADMIN_COUNT, value)
        return true
    }

    override fun saveSkuDetails(key: String, value: String): Boolean {
        val objUser = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS)
        objUser.putString(key, value)
        return true
    }

    override fun fetchSkeDetails(key: String): HashMap<String, SkuDetails>? {
        val details = fetchDetails(key)
        return when (details.isNotEmpty()) {
            true -> {
                Gson().fromJson(details, object : TypeToken<HashMap<String, SkuDetails>>() {}.type)
            }

            false -> {
                null
            }
        }
    }

    override fun saveUserChatKeys(key: String, value: String): Boolean {
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag,
            "Testing is saveUserChatKeys : KEY   $key   Value    $value"
        )
        val objUser = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS)
        objUser.putString(key, value)
        return true
    }

    override fun fetchUserChatKeys(key: String): ArrayList<String> {
        if (key.isEmpty()) {
            return arrayListOf()
        }
        val keys = fetchDetails(key)
        return when (keys.isNotEmpty()) {
            true -> {
                Gson().fromJson<ArrayList<String>>(
                    keys,
                    object : TypeToken<ArrayList<String>>() {}.type
                )
            }
            false -> {
                arrayListOf()
            }
        }
    }

    override fun saveNotificationCount(count: String): Boolean {
        val objUser = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS)
        objUser.putString(SharedPreferencesBean.KEY_FLAG_NOTIFICATION, count)
        return true
    }

    override fun fetchNotificationCount(): String {
        return when (fetchDetails(SharedPreferencesBean.KEY_FLAG_NOTIFICATION).isEmpty()) {
            true -> "0"
            else -> fetchDetails(SharedPreferencesBean.KEY_FLAG_NOTIFICATION)
        }
    }

    override fun saveUserChat(key: String, value: String): Boolean {
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag,
            "Testing is saveUserChat : KEY   $key   Value    $value"
        )
        val objUser = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS)
        objUser.putString(key, value)
        return true
    }

    override fun fetchUserChat(key: String): ArrayList<ChatModel> {
        if (key.isEmpty()) {
            return arrayListOf()
        }
        val chat = fetchDetails(key)
        return when (chat.isNotEmpty()) {
            true -> {
                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag,
                    "Testing is Chat : $chat, Key is : $key"
                )
                Gson().fromJson<ArrayList<ChatModel>>(
                    chat,
                    object : TypeToken<ArrayList<ChatModel>>() {}.type
                )
            }
            false -> {
                arrayListOf()
            }
        }
    }

    override fun fetchOtherUserProfile(key: String): ProfileFirebase {
        if (key.isEmpty()) {
            return ProfileFirebase()
        }
        val profile = fetchDetails(key)
        return when (profile.isNotEmpty()) {
            true -> {
                LogManager.logger.i(ArchitectureApp.instance!!.tag, "Profile is : $profile")
                Gson().fromJson<ProfileFirebase>(profile, ProfileFirebase::class.java)
            }
            false -> {
                ProfileFirebase()
            }
        }
    }

    override fun saveOtherUserProfile(key: String, value: String): Boolean {
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag,
            "Testing is saveOtherUserProfile : KEY   $key   Value    $value"
        )
        val objUser = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS)
        objUser.putString(key, value)
        return true
    }

    override fun saveConversation(conversation: String): Boolean {
        val objUser = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS)
        objUser.putString(SharedPreferencesBean.KEY_FLAG_CONVERSATION, conversation)
        return true
    }



    override fun fetchConversation(): ArrayList<ConversationModel> {
        val conversation = fetchDetails(SharedPreferencesBean.KEY_FLAG_CONVERSATION)
        return when (conversation.isEmpty()) {
            true -> {
                arrayListOf()
            }

            false -> {
                Gson().fromJson<ArrayList<ConversationModel>>(
                    conversation,
                    object : TypeToken<ArrayList<ConversationModel>>() {}.type
                )
            }
        }
    }




    override fun saveReason(reason: String): Boolean {
        val objUser = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS)
        objUser.putString(SharedPreferencesBean.KEY_FLAG_REASON, reason)
        return true
    }

    override fun fetchReason(): ArrayList<ResponseFlagReason> {
        val reason = fetchDetails(SharedPreferencesBean.KEY_FLAG_REASON)
        return when (reason.isEmpty()) {
            true -> {
                arrayListOf()
            }

            false -> {
                Gson().fromJson<ArrayList<ResponseFlagReason>>(
                    reason,
                    object : TypeToken<ArrayList<ResponseFlagReason>>() {}.type
                )
            }
        }
    }

    override fun fetchLocationName(): String {
        return fetchDetails(SharedPreferencesBean.KEY_USER_LOCATION_NAME)
    }

    override fun saveLocationName(location: String): Boolean {
        val objUser = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS)
        objUser.putString(SharedPreferencesBean.KEY_USER_LOCATION_NAME, location)
        return true
    }

    override fun fetchCountryForNameCode(): String {
        return fetchUserProfile().isoCode
    }

    override fun isUserLogin(): Boolean {
        return fetchDetails(SharedPreferencesBean.KEY_USER_LOGGEDIN).isNotEmpty()
    }

    override fun saveUserLoggedIn(loggedIn: String): Boolean {
        val objUser = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS)
        objUser.putString(SharedPreferencesBean.KEY_USER_LOGGEDIN, loggedIn)
        return true
    }

    override fun userLoggedIn(): Boolean {
        return fetchDetails(SharedPreferencesBean.KEY_USER_LOGGEDIN).isNotEmpty()
    }

    override fun saveUserProfile(profile: String): Boolean {
        val objUser = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS)
        objUser.putString(SharedPreferencesBean.KEY_USER_PROFILE, profile)
        return true
    }

    override fun userId(): String {
        val profile = fetchDetails(SharedPreferencesBean.KEY_USER_PROFILE)
        return when (profile.isNotEmpty()) {
            true -> {
                Gson().fromJson<ResponseUserData>(profile, ResponseUserData::class.java).id
            }
            else -> {
                ""
            }
        }
    }

    override fun fetchUserProfile(): ResponseUserData {
        val profile = fetchDetails(SharedPreferencesBean.KEY_USER_PROFILE)
        return when (profile.isNotEmpty()) {
            true -> {
                Gson().fromJson<ResponseUserData>(profile, ResponseUserData::class.java)
            }
            else -> {
                ResponseUserData()
            }
        }
    }

    override fun fetchUserProfileImage(): String {
        val profile = fetchDetails(SharedPreferencesBean.KEY_USER_PROFILE)
        return when (profile.isNotEmpty()) {
            true -> {
                val user = Gson().fromJson<ResponseUserData>(profile, ResponseUserData::class.java)
                if (user.images.isEmpty()) {
                    ""
                } else {
                    user.images[0].imageThumb
                }
            }
            else -> {
                ""
            }
        }
    }

    override fun fetchUserPhoneNumber(): String {
        val profile = fetchUserProfile()
        return "${profile.countryCode} ${profile.number}"
    }

    override fun saveUserPhoneNumberPrivate(phoneNumbers : String):Boolean{
        val objUser = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS)
        objUser.putString(SharedPreferencesBean.KEY_USER_NUMBER, phoneNumbers)
        return true
    }

    override fun fetchUserPhoneNumberPrivate(): String {
        return  fetchDetails(SharedPreferencesBean.KEY_USER_NUMBER)
    }



    override fun saveLoginToken(loginToken: String): Boolean {
        val objUser = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS)
        objUser.putString(SharedPreferencesBean.KEY_LOGIN_TOKEN, loginToken)
        return true
    }


    override fun saveLocation(location: String): Boolean {
        val objUser =
            SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS_DEVICE_TOKEN)
        objUser.putString(SharedPreferencesBean.KEY_USER_LOCATION, location)
        return true
    }

    override fun fetchLocation(): LatLng {
        val objSPLocation =
            SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS_DEVICE_TOKEN)
        val location = "${objSPLocation.getString(SharedPreferencesBean.KEY_USER_LOCATION)}"
        return when (location.isNotEmpty()) {
            true -> {
                Gson().fromJson<LatLng>(location, LatLng::class.java)
            }

            else -> {
                LatLng(0.0, 0.0)
            }
        }

    }

    override fun saveDeviceToken(deviceToken: String): Boolean {
        val objSPDeviceToken =
            SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS_DEVICE_TOKEN)
        objSPDeviceToken.putString(SharedPreferencesBean.KEY_FIREBASE_DEVICE_TOKEN, deviceToken)
        return objSPDeviceToken.getString(SharedPreferencesBean.KEY_FIREBASE_DEVICE_TOKEN)!!.isNotEmpty()
    }

    override fun fetchDeviceToken(): String {
        val objSPDeviceToken =
            SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS_DEVICE_TOKEN)
        return "${objSPDeviceToken.getString(SharedPreferencesBean.KEY_FIREBASE_DEVICE_TOKEN)}"
    }


    override fun loginToken(): String {
        return fetchDetails(SharedPreferencesBean.KEY_LOGIN_TOKEN)
    }

    override fun saveSettings(settings: String): Boolean {
        val objUser = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS)
        objUser.putString(SharedPreferencesBean.KEY_SETTINGS, settings)
        return true
    }

    override fun fetchSettings(): ResponseSettings {
        val settings = fetchDetails(SharedPreferencesBean.KEY_SETTINGS)
        return when (settings.isNotEmpty()) {
            true -> {
                Gson().fromJson<ResponseSettings>(settings, ResponseSettings::class.java)
            }

            else -> {
                ResponseSettings()
            }
        }
    }

    override fun fetchDetails(key: String): String {
        val objUser = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS)
        return "${objUser.getString(key)}"
    }

    override fun remove(key: String) {
        val objUser = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS)
        objUser.removeString(key)
    }



    override fun clearAll() {
        val objUser = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS)
        objUser.clearSharedPreferences()
    }



    override fun savePopUpUserChatPush(conversation: String): Boolean {
        val objUser = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS)
        objUser.putString(SharedPreferencesBean.KEY_USERCHAT_POPUP_SHOW, conversation)
        return true
    }


    override fun fetchPopUpUserChatPush(): String {
        return  fetchDetails(SharedPreferencesBean.KEY_USERCHAT_POPUP_SHOW)

    }


    override fun saveAdminAdsCount(conversation: String): Boolean {
        val objUser = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS)
        objUser.putString(SharedPreferencesBean.KEY_ADMIN_ADS, conversation)
        return true
    }


    override fun fetchAdminAdsCount(): String {
        return  fetchDetails(SharedPreferencesBean.KEY_ADMIN_ADS)

    }


    override fun saveHasPrivateAlbum(hasPrivate: String): Boolean {
        val objUser = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS)
        objUser.putString(SharedPreferencesBean.KEY_HASPRIVATE_ALBUM, hasPrivate)
        return true
    }


    override fun fetchPrivateAlbum(): String {
        return  fetchDetails(SharedPreferencesBean.KEY_HASPRIVATE_ALBUM)

    }

    override fun saveCurrentStateScreen(screenName: String): Boolean {
        val objUser = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS)
        objUser.putString(SharedPreferencesBean.KEY_SCREEN_STATE, screenName)
        return true
    }

    override fun featchCurrentStateScreen(): String {
       return fetchDetails(SharedPreferencesBean.KEY_SCREEN_STATE)
    }

    override fun saveTimmerTime(systemTime: String): Boolean {

        val objUser = SharedPreferencesCustom(context, SharedPreferencesBean.KEY_LOGIN_DETAILS)
        objUser.putString (SharedPreferencesBean.KEY_TIMER_TIME, systemTime)
        return true

    }

    override fun featchTimmerTime(): String {
        return fetchDetails(SharedPreferencesBean.KEY_TIMER_TIME)
    }


}