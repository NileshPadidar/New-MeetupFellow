package com.connect.meetupsfellow.global.sharedPreferences

import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp

object SharedPreferencesBean {

    private val APP_NAME = ArchitectureApp.instance!!.getString(R.string.app_name)

    internal val KEY_LOGIN_DETAILS = APP_NAME + "_login_credentials"
    internal val KEY_LOGIN_DETAILS_DEVICE_TOKEN = APP_NAME + "_login_credentials_device_token"

    /*
    * @param Firebase Device Token
    * */
    internal val KEY_FIREBASE_DEVICE_TOKEN = APP_NAME + "_device_token"

    /*
    * @param User ID
    * */
    internal val KEY_USER_ID = APP_NAME + "_user_id"

    /*
    * @params User current location
    * */
    internal val KEY_USER_LOCATION = APP_NAME + "_user_location"
    internal val KEY_USER_LOCATION_NAME = APP_NAME + "_user_location_name"

    /*
    * @param Login Token
    * */
    internal val KEY_LOGIN_TOKEN = APP_NAME + "_device_token"

    /*
    * @param User Details
    * */
    internal val KEY_USER_LOGGEDIN = APP_NAME + "_user_loggedIn"
    internal val KEY_USER_NUMBER = APP_NAME + "_phone_number"
    internal val KEY_USER_PROFILE = APP_NAME + "_user_full_profile"
    internal val KEY_SETTINGS = APP_NAME + "_user_settings"
    internal val KEY_FLAG_REASON = APP_NAME + "_flag_reason"
    internal val KEY_FLAG_CONVERSATION = APP_NAME + "_user_conversation"
    internal val KEY_FLAG_NOTIFICATION = APP_NAME + "_notification"

    internal val KEY_PURCHASE_NEARBY = APP_NAME + "_purchase_nearby"
    internal val KEY_PURCHASE_CHAT = APP_NAME + "_purchase_chat"

    internal val KEY_ADMIN_COUNT = "${APP_NAME}_ADMIN_COUNT"

    internal  val KEY_USERCHAT_POPUP_SHOW = APP_NAME + "_userchat_popup_show"
    internal  val KEY_CHAT_POPUP_DISMISS = APP_NAME + "_chat_pop_dismiss"
    internal  val KEY_ADMINCHAT_POPUP_SHOW = APP_NAME + "_adminchat_popup_show"

    internal  val KEY_ADMIN_ADS = APP_NAME + "_admin_ads"
    internal  val KEY_HASPRIVATE_ALBUM = APP_NAME + "privates_album"

    internal  val KEY_SCREEN_STATE = APP_NAME + "save_state_screen"

    internal  val KEY_TIMER_TIME = APP_NAME + "save_timmer_time"


}
