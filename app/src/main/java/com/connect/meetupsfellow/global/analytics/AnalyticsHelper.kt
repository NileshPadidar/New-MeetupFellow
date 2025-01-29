@file:Suppress("DEPRECATION", "unused")

package com.connect.meetupsfellow.global.analytics

//import com.flurry.android.FlurryAgent
//import com.oit.datingondl.application.ArchitectureApp

/**
 * Created by xeemu on 13-10-2017.
 */
object AnalyticsHelper {

    /**
     * Logs an event for analytics.
     *
     * @param eventName     name of the event
     * @param eventParams   event parameters (can be null)
     * @param timed         `true` if the event should be timed, false otherwise
     */
//    fun logEvent(eventName: String, eventParams: HashMap<String, String>, timed: Boolean) {
//        FlurryAgent.logEvent(eventName, eventParams, timed)
//    }
//
//    fun eventLogin(userId: String, userName: String) {
//        FlurryAgent.logEvent(
//            AnalyticsEvents.Event.Login,
//                HashMap<String, String>().apply {
//                    put(AnalyticsEvents.Key.USER_ID, userId)
//                    put(AnalyticsEvents.Key.USER_NAME, userName)
//                    put(AnalyticsEvents.Key.DEVICE_TYPE, ConstantsImage.Device.TYPE)
//                }, true)
//
//        FirebaseAnalytics.getInstance(ArchitectureApp.instance!!.applicationContext)
//                .logEvent(AnalyticsEvents.Event.Login, Bundle().apply {
//                    putString(AnalyticsEvents.Key.USER_ID, userId)
//                    putString(AnalyticsEvents.Key.USER_NAME, userName)
//                    putString(AnalyticsEvents.Key.DEVICE_TYPE, ConstantsImage.Device.TYPE)
//                })
//    }
//
//    fun eventBlock(userId: String, anotherId: String) {
//        FlurryAgent.logEvent(
//            AnalyticsEvents.Event.BLOCK,
//                HashMap<String, String>().apply {
//                    put(AnalyticsEvents.Key.USER_ID, userId)
//                    put(AnalyticsEvents.Key.ANOTHER_USER_ID, anotherId)
//                    put(AnalyticsEvents.Key.DEVICE_TYPE, ConstantsImage.Device.TYPE)
//                }, true)
//
//        FirebaseAnalytics.getInstance(ArchitectureApp.instance!!.applicationContext)
//                .logEvent(AnalyticsEvents.Event.BLOCK, Bundle().apply {
//                    putString(AnalyticsEvents.Key.USER_ID, userId)
//                    putString(AnalyticsEvents.Key.ANOTHER_USER_ID, anotherId)
//                    putString(AnalyticsEvents.Key.DEVICE_TYPE, ConstantsImage.Device.TYPE)
//                })
//    }
//
//    fun eventReport(userId: String, anotherId: String) {
//        FlurryAgent.logEvent(
//            AnalyticsEvents.Event.REPORT,
//                HashMap<String, String>().apply {
//                    put(AnalyticsEvents.Key.USER_ID, userId)
//                    put(AnalyticsEvents.Key.ANOTHER_USER_ID, anotherId)
//                    put(AnalyticsEvents.Key.DEVICE_TYPE, ConstantsImage.Device.TYPE)
//                }, true)
//
//        FirebaseAnalytics.getInstance(ArchitectureApp.instance!!.applicationContext)
//                .logEvent(AnalyticsEvents.Event.REPORT, Bundle().apply {
//                    putString(AnalyticsEvents.Key.USER_ID, userId)
//                    putString(AnalyticsEvents.Key.ANOTHER_USER_ID, anotherId)
//                    putString(AnalyticsEvents.Key.DEVICE_TYPE, ConstantsImage.Device.TYPE)
//                })
//    }
//
//    fun eventLogoutAnother(userId: String) {
//        FlurryAgent.logEvent(
//            AnalyticsEvents.Event.LOGOUT,
//                HashMap<String, String>().apply {
//                    put(AnalyticsEvents.Key.USER_ID, userId)
//                    put(AnalyticsEvents.Key.DEVICE_TYPE, ConstantsImage.Device.TYPE)
//                }, true)
//
//        FirebaseAnalytics.getInstance(ArchitectureApp.instance!!.applicationContext)
//                .logEvent(AnalyticsEvents.Event.LOGOUT, Bundle().apply {
//                    putString(AnalyticsEvents.Key.USER_ID, userId)
//                    putString(AnalyticsEvents.Key.DEVICE_TYPE, ConstantsImage.Device.TYPE)
//                })
//    }
//
//    fun eventLogout(userId: String, userName: String) {
//        FlurryAgent.logEvent(
//            AnalyticsEvents.Event.LOGOUT,
//                HashMap<String, String>().apply {
//                    put(AnalyticsEvents.Key.USER_ID, userId)
//                    put(AnalyticsEvents.Key.USER_NAME, userName)
//                    put(AnalyticsEvents.Key.DEVICE_TYPE, ConstantsImage.Device.TYPE)
//                }, true)
//
//        FirebaseAnalytics.getInstance(ArchitectureApp.instance!!.applicationContext)
//                .logEvent(AnalyticsEvents.Event.LOGOUT, Bundle().apply {
//                    putString(AnalyticsEvents.Key.USER_ID, userId)
//                    putString(AnalyticsEvents.Key.USER_NAME, userName)
//                    putString(AnalyticsEvents.Key.DEVICE_TYPE, ConstantsImage.Device.TYPE)
//                })
//    }
//
//    fun logEvent(eventName: String) {
//        FlurryAgent.logEvent(eventName)
//    }
//
//    fun userDetails(userId: String, userAge: Int, userGender: String) {
//        FlurryAgent.setUserId(userId)
//        FlurryAgent.setAge(userAge)
//        FlurryAgent.setGender(getGender(userGender))
//        FirebaseAnalytics.getInstance(ArchitectureApp.instance!!.applicationContext).setUserId(userId)
//    }
//
//    private fun getGender(gender: String): Byte {
//        return when (gender) {
//            ConstantsImage.UserGender.MALE -> {
//                com.flurry.android.ConstantsImage.MALE
//            }
//
//            ConstantsImage.UserGender.FEMALE -> {
//                com.flurry.android.ConstantsImage.FEMALE
//            }
//
//            else -> {
//                com.flurry.android.ConstantsImage.UNKNOWN
//            }
//        }
//    }
//
//    /**
//     * Ends a timed event that was previously started.
//     *
//     * @param eventName     name of the event
//     * @param eventParams   event parameters (can be null)
//     */
//    fun endTimedEvent(eventName: String, eventParams: Map<String, String>) {
//        FlurryAgent.endTimedEvent(eventName, eventParams)
//    }
//
//
//    /**
//     * Ends a timed event without event parameters.
//     *
//     * @param eventName     name of the event
//     */
//    fun endTimedEvent(eventName: String) {
//        FlurryAgent.endTimedEvent(eventName)
//    }
//
//    /**
//     * Logs an error.
//     *
//     * @param errorId           error ID
//     * @param errorDescription  error description
//     * @param throwable         a [Throwable] that describes the error
//     */
//    fun logError(errorId: String, errorDescription: String, throwable: Throwable?) {
//        FlurryAgent.onError(errorId, errorDescription, throwable)
//    }
//
//    /**
//     * Logs location.
//     *
//     * @param latitude           latitude of location
//     * @param longitude        longitude of location
//     */
//    fun logLocation(latitude: Double, longitude: Double) {
//        FlurryAgent.setLocation(latitude.toFloat(), longitude.toFloat())
//    }
//
//    /**
//     * Logs page view counts.
//     *
//     */
//    fun logPageViews() {
//        FlurryAgent.onPageView()
//    }

}