package com.connect.meetupsfellow.global.utils

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import com.android.billingclient.api.*
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.view.activities.HomeActivity
import com.connect.meetupsfellow.mvp.view.activities.VerifyPinActivity

object GlobalUtils {
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil
    private lateinit var billingClient: BillingClient
    var isFirstTime = "yes"

    fun create(context: Context, isNotification: Boolean, contentId: String?, intent: Intent) {
        val isAppPinEnabled = context.getSharedPreferences("appPinB", Context.MODE_PRIVATE)
            .getBoolean("appPinB", false)
        val isUserLogin =
            context.getSharedPreferences("isUserLogin", AppCompatActivity.MODE_PRIVATE)
                .getBoolean("isUserLogin", false)
        when (isUserLogin) {
            true -> {
                Log.e("GlobalUtils$", "UserLogin")

                if (context.getSharedPreferences("appPin", Context.MODE_PRIVATE)
                        .getString("appPin", "").toString().isNotEmpty() && isAppPinEnabled
                ) {

                    checkSubs(context)
                    if (isFirstTime.equals("yes")) {
                        switchActivity(context,
                            VerifyPinActivity::class.java,
                            true,
                            if (isNotification) intent.extras else Bundle().apply {
                                putString(Constants.IntentDataKeys.UserId, contentId?.trim())
                                putString(Constants.IntentDataKeys.IsFirstTime, isFirstTime)
                            })
                    } else {
                        switchActivity(context,
                            VerifyPinActivity::class.java,
                            false,
                            if (isNotification) intent.extras else Bundle().apply {
                                putString(Constants.IntentDataKeys.UserId, contentId?.trim())
                                putString(Constants.IntentDataKeys.IsFirstTime, isFirstTime)
                            })
                    }
                    isFirstTime = "no"
                } else if (context.getSharedPreferences("Finger", Context.MODE_PRIVATE)
                        .getBoolean("Finger", false)
                ) {
                    checkSubs(context)
                    Log.e("GlobalUtils$", "yes_FingerPrint_enable")
                    if (isFirstTime.equals("yes")) {
                        switchActivity(context,
                            VerifyPinActivity::class.java,
                            true,
                            if (isNotification) intent.extras else Bundle().apply {
                                putString(Constants.IntentDataKeys.UserId, contentId?.trim())
                                putString(Constants.IntentDataKeys.IsFirstTime, isFirstTime)
                            })
                    } else {
                        switchActivity(context,
                            VerifyPinActivity::class.java,
                            false,
                            if (isNotification) intent.extras else Bundle().apply {
                                putString(Constants.IntentDataKeys.UserId, contentId?.trim())
                                putString(Constants.IntentDataKeys.IsFirstTime, isFirstTime)
                            })
                    }
                    isFirstTime = "no"
                    // showFingerScanner(context, isNotification, contentId, intent)
                    // switchActivity(context, HomeActivity::class.java, true, if (isNotification) intent.extras else Bundle())
                } else {
                    Log.e("GlobalUtils$", "NotShowing_anything")
                    if (isFirstTime.equals("yes")) {
                        Handler().postDelayed({
                            switchActivity(
                                context,
                                HomeActivity::class.java,
                                true,
                                if (isNotification) intent.extras else Bundle()
                            )
                        }, 2000)
                    }
                    isFirstTime = "no"
                }
            }

            false -> {
                Log.e("GlobalUtils$", "userLogOut")
                return
            }
        }

        Log.e("GlobalUtils", "in")
    }

    private fun checkSubs(context: Context) {

        Log.d("subscription Cancelled", "function called")

        billingClient = BillingClient.newBuilder(context).setListener { billingResult, purchases ->
                when (billingResult.responseCode) {

                    BillingClient.BillingResponseCode.OK -> {

                        Log.d(
                            "subscription Cancelled", billingResult.responseCode.toString()
                        )

                        billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder()
                            .setProductType(BillingClient.ProductType.SUBS).build(),
                            PurchasesResponseListener { result, list ->

                                when (result.responseCode) {

                                    BillingClient.BillingResponseCode.USER_CANCELED -> {

                                        Log.d(
                                            "TAG",
                                            "subscription Cancelled" + result.responseCode.toString()
                                        )
                                    }

                                    BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {

                                        Log.d(
                                            "TAG",
                                            "subscription Cancelled" + result.responseCode.toString()
                                        )
                                    }

                                    BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> {

                                        Log.d(
                                            "TAG",
                                            "subscription Cancelled" + result.responseCode.toString()
                                        )
                                    }

                                    BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> {

                                        Log.d(
                                            "TAG",
                                            "subscription Cancelled" + result.responseCode.toString()
                                        )
                                    }

                                    BillingClient.BillingResponseCode.OK -> {

                                        Log.d(
                                            "TAG",
                                            "subscription Cancelled" + result.responseCode.toString()
                                        )
                                    }
                                }
                            })
                    }
                }
            }.enablePendingPurchases().build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                LogManager.logger.i("GlobalUtils", "Connection ended.")
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.ERROR) {
                    return
                }
            }
        })

        val params =
            QueryPurchaseHistoryParams.newBuilder().setProductType(BillingClient.ProductType.SUBS)

        billingClient.queryPurchaseHistoryAsync(params.build(),
            PurchaseHistoryResponseListener { result, list ->

                if (list != null) {
                    for (i in list) {
                        Log.d("list", i.toString())
                    }
                }
            })
    }

    private fun switchActivity(
        context: Context, targetActivity: Class<*>, finish: Boolean, extras: Bundle?
    ) {
        val intent = Intent(context, targetActivity).apply {
            extras?.let { putExtras(it) }
            if (finish) {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            if (context !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

        }
        context.startActivity(intent)

    }

    fun clearNotifications(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }
}
