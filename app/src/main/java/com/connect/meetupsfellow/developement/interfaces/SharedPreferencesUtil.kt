package com.connect.meetupsfellow.developement.interfaces

import com.android.billingclient.api.SkuDetails
import com.google.android.gms.maps.model.LatLng
import com.connect.meetupsfellow.mvp.view.model.ChatModel
import com.connect.meetupsfellow.mvp.view.model.ConversationModel
import com.connect.meetupsfellow.mvp.view.model.ProfileFirebase
import com.connect.meetupsfellow.retrofit.response.LoginUserDataResponse
import com.connect.meetupsfellow.retrofit.response.ResponseFlagReason
import com.connect.meetupsfellow.retrofit.response.ResponseSettings
import com.connect.meetupsfellow.retrofit.response.ResponseUserData

interface SharedPreferencesUtil {

    fun saveLocation(location: String): Boolean
    fun fetchLocation(): LatLng
    fun saveLocationName(location: String): Boolean
    fun fetchLocationName(): String
    fun saveDeviceToken(deviceToken: String): Boolean
    fun fetchDeviceToken(): String

    fun saveLoginToken(loginToken: String): Boolean
    fun loginToken(): String

    fun saveNotificationCount(count: String): Boolean
    fun fetchNotificationCount(): String

    fun saveUserProfile(profile: String): Boolean
    fun fetchUserProfile(): ResponseUserData
   /// fun fetchLoginUserData(): LoginUserDataResponse
    fun fetchUserProfileImage(): String
    fun fetchUserPhoneNumber(): String
    fun saveUserPhoneNumberPrivate(phoneNumbeer: String): Boolean
    fun fetchUserPhoneNumberPrivate(): String
    fun userId(): String
    fun fetchCountryForNameCode(): String
    fun saveSettings(settings: String): Boolean
    fun fetchSettings(): ResponseSettings
    fun isUserLogin(): Boolean
    fun saveUserLoggedIn(loggedIn: String): Boolean
    fun userLoggedIn(): Boolean

    fun saveReason(reason: String): Boolean
    fun fetchReason(): ArrayList<ResponseFlagReason>

    fun fetchDetails(key: String): String

    fun saveConversation(conversation: String): Boolean
    fun fetchConversation(): ArrayList<ConversationModel>
    fun saveOtherUserProfile(key: String, value: String): Boolean
    fun fetchOtherUserProfile(key: String): ProfileFirebase
    fun saveUserChat(key: String, value: String): Boolean
    fun fetchUserChat(key: String): ArrayList<ChatModel>
    fun saveUserChatKeys(key: String, value: String): Boolean
    fun fetchUserChatKeys(key: String): ArrayList<String>

    fun saveSkuDetails(key: String, value: String): Boolean
    fun fetchSkeDetails(key: String): HashMap<String, SkuDetails>?

    fun premiunNearby(value: String): Boolean
    fun fetchPremiunNearby(): Boolean
    fun premiunChat(value: String): Boolean
    fun fetchPremiunChat(): Boolean

    fun saveAdminChatCount(value: String): Boolean
    fun fetchAdminChatCount(): String

    fun clearAll()
    fun remove(key: String)

    fun savePopUpUserChatPush(message: String): Boolean
    fun fetchPopUpUserChatPush() : String

    fun saveAdminAdsCount(message: String): Boolean
    fun fetchAdminAdsCount() : String

    fun saveHasPrivateAlbum(message: String): Boolean
    fun fetchPrivateAlbum() : String

    fun saveCurrentStateScreen(screenName: String): Boolean
    fun featchCurrentStateScreen() : String

    fun saveTimmerTime(systemTime: String  ): Boolean
    fun featchTimmerTime():String



    /* fun popUpAdminPush()
     */
}