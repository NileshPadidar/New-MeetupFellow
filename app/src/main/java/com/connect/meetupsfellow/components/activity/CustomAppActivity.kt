package com.connect.meetupsfellow.components.activity

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.global.utils.GlobalUtils
import com.connect.meetupsfellow.global.utils.InterstitialAdManager
import com.connect.meetupsfellow.mvp.view.model.SkuDetail

/**
 * Created by xeemu on 07-10-2017.
 */
@SuppressLint("Registered")
@Suppress("DEPRECATION")
open class CustomAppActivity : AppCompatActivity() {
    private var instance: Context? = null
    protected var postId = ""
    protected var roomId = ""

    protected var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        initCustomAppActivityImpl()
        SkuDetail()
    }

    private fun initCustomAppActivityImpl() {
//        context = this
    }

    protected fun setInstance(context: Context?) {
        instance = context
    }

    protected fun getInstance() = instance

    /*  internal fun switchActivity(activity: String, finish: Boolean, extras: Bundle?) {
          startActivity(Intent(activity).putExtras(extras!!))
          when (finish) {
              true -> {
                  finish()
              }
              false -> { startActivity(
                  Intent(Constants.Intent.LoginWithEmailActivity)
                      .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
              )}
          }
      }*/
    internal fun switchActivity(actionOrClass: Any, finish: Boolean, extras: Bundle?) {
        val intent = when (actionOrClass) {
            is String -> Intent(actionOrClass) // Action-based intent
            is Class<*> -> Intent(this, actionOrClass) // Explicit intent
            else -> throw IllegalArgumentException("Invalid parameter for switchActivity")
        }

        extras?.let { intent.putExtras(it) }

        Log.d("IntentDebug", "Intent: $intent") // Log for debugging

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
            if (finish) finish()
        } else {
            Log.e("Error", "No activity found for intent: $intent")
        }
    }


    internal fun switchActivityOnly(actionOrClass: Any, finish: Boolean) {
        val intent = when (actionOrClass) {
            is String -> Intent(actionOrClass) // Action-based intent
            is Class<*> -> Intent(this, actionOrClass) // Explicit intent
            else -> throw IllegalArgumentException("Invalid parameter for switchActivity")
        }

        Log.d("IntentDebug", "Intent: $intent") // Log for debugging

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
            if (finish) finish()
        } else {
            Log.e("Error", "No activity found for intent: $intent")
        }

      /*  startActivity(Intent(activity))
        when (finish) {
            true -> {
                finish()
            }

            false -> {
                startActivity(
                    Intent(Constants.Intent.LoginWithEmailActivity).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
            }
        }*/
    }


    protected fun logout() {
        // Once the user is successfully logged in
        val interstitialAdManager = InterstitialAdManager.getInstance(this)
        interstitialAdManager.setUserLoggedIn(false)

        GlobalUtils.isFirstTime = "yes"
        getSharedPreferences("UserId", MODE_PRIVATE).edit().clear().apply()
        getSharedPreferences("isUserLogin", MODE_PRIVATE).edit().putBoolean("isUserLogin", false)
            .apply()
        startActivity(
            Intent(Constants.Intent.LoginWithEmailActivity).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
        /* val intent = Intent(context, LoginWithEmailActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent,null)*/

    }

    internal fun showProgressView(layout: View) {
        layout.visibility = View.VISIBLE
    }

    internal fun hideProgressView(layout: View) {
        layout.visibility = View.GONE
    }

    fun indexExists(list: ArrayList<*>, index: Int): Boolean = index >= 0 && index < list.size

    private fun setColor(value: String, color: Int): String {
        return "<font color_preference_selector=" + color + ">" + value + "</font>"
    }

    internal fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString().trim())
            }
        })
    }

    internal fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningServices(Integer.MAX_VALUE)
            .any { serviceClass.name == it.service.className }
    }

    internal fun updateConversationList() {
//      updateDetails(Constants.BroadcastReceiver.UPDATE_CONVERSATION)
    }

    private fun updateDetails() {
        val intent = Intent()
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
        intent.action = "com.oit.datingondl.BroadcastReceiver"
//        intent.putExtra(Constants.BroadcastReceiver.FLAG, flag)
        sendBroadcast(intent)
    }

    @SuppressLint("NewApi")
    protected fun isActivityRunning(activityClass: Class<*>): Boolean {
        val activityManager =
            baseContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks = activityManager.getRunningTasks(Integer.MAX_VALUE)

        for (task in tasks) {
            if (activityClass.canonicalName!!.equals(
                    task.baseActivity!!.className, ignoreCase = true
                )
            ) return true
        }

        return false
    }

    protected fun clearNotifications() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

}