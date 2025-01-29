package com.connect.meetupsfellow.mvp.view.activities

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.databinding.ActivityNotificationSettingBinding
import com.connect.meetupsfellow.developement.interfaces.FirebaseUtil
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.utils.ToolbarUtils
import com.connect.meetupsfellow.mvp.connector.activity.SettingsConnector
import com.connect.meetupsfellow.mvp.presenter.activity.SettingsPresenter
import com.connect.meetupsfellow.mvp.view.adapter.AllTypeNotificationsAdapter
import com.connect.meetupsfellow.retrofit.request.EnableDisableNotificationReq
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ResponseSettings
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import javax.inject.Inject

class NotificationSettingActivity : CustomAppActivityCompatViewImpl() {

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    private var setting: ResponseSettings? = null

    private val allTypeNotificationsAdapter = AllTypeNotificationsAdapter()
    private var mAdManagerInterstitialAd: AdManagerInterstitialAd? = null

    private var mPresenter: SettingsConnector.PresenterOps? = null
    private var binding: ActivityNotificationSettingBinding? = null
    private lateinit var progressBar: LinearLayout

    private val presenter = object : SettingsConnector.RequiredViewOps {
        override fun showToast(error: String, logout: Boolean?) {
            when (logout) {
                true -> {
                    logout()
                }

                false -> {}
                null -> {}
            }
            universalToast(error)
            hideProgressView(progressBar)
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {
            when (type) {
                //ConstantsApi.DEACTIVATE -> deactivateLogoutAccount()
                ConstantsApi.GET_ALL_TYPE_NOTIFICATIONS -> {
                    Log.e("ALL_NOtification", "List: ${response.notificationTypeList!!.size}")
                    // Toast.makeText(requireContext(), response.total_followers, Toast.LENGTH_SHORT).show()
                    if (response.notificationTypeList != null) {
                        allTypeNotificationsAdapter.add(
                            this@NotificationSettingActivity, response.notificationTypeList!!
                        )
                        allTypeNotificationsAdapter.notifyDataSetChanged()
                    }
                    hideProgressView(progressBar)
                }

                ConstantsApi.ENABLE_DISABLE_NOTIFICATION_TYPE -> {
                    universalToast(response.message)
                    ///  getAllNotificationsList()
                    hideProgressView(progressBar)
                }

                ConstantsApi.DELETE -> {
                    logout()
                    hideProgressView(progressBar)
                }

                ConstantsApi.DEACTIVATE_LOGOUT -> {
                    logout()
                    hideProgressView(progressBar)
                }

                ConstantsApi.UNIT_IMPERIAL -> {
                    setting = sharedPreferencesUtil.fetchSettings()
                    //adapter.getSettings(setting!!)
                    hideProgressView(progressBar)
                }

                ConstantsApi.UNIT_METRIC -> {
                    setting = sharedPreferencesUtil.fetchSettings()
                    //adapter.getSettings(setting!!)
                    hideProgressView(progressBar)
                }

                else -> {
                    universalToast(response.message)
                    setting = sharedPreferencesUtil.fetchSettings()
                    //adapter.getSettings(setting!!)
                    hideProgressView(progressBar)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this@NotificationSettingActivity)
        binding = ActivityNotificationSettingBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        progressBar = binding!!.includedLoading.rlProgress
        // setContentView(R.layout.activity_notification_setting)
      //  setupActionBar(getString(R.string.title_notifications_setting), true)
        ToolbarUtils.setupActionBar(
            this,
            binding!!.header.toolbar,
            getString(R.string.title_notifications_setting),
            binding!!.header.tvTitle,
            showBackArrow = true,
            activity = this
        )
        //showProgressView(progressBar)
        startLoadingAnim()
        init()
        if (!sharedPreferencesUtil.fetchUserProfile().isProMembership) {
            loadInterstitialAd(this)
        }
    }

    fun loadInterstitialAd(context: Context) {
        var adRequest = AdManagerAdRequest.Builder().build()
        AdManagerInterstitialAd.load(context,
            getString(R.string.InterstitialAd_unit_id),
            adRequest,
            object : AdManagerInterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("InterstitialAdManager", adError.toString())
                    mAdManagerInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: AdManagerInterstitialAd) {
                    Log.e("InterstitialAdManager", "NotificationSetting Ad was loaded.")
                    mAdManagerInterstitialAd = interstitialAd
                    showInterstitialAd(context)
                }
            })
    }

    fun showInterstitialAd(context: Context) {
        if (mAdManagerInterstitialAd != null) {
            mAdManagerInterstitialAd?.show(context as Activity)
        } else {
            Log.e(
                "InterstitialAdManager", "NotificationSetting The interstitial ad wasn't ready yet."
            )
        }

        mAdManagerInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.e("InterstitialAdManager", "NotificationSetting Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.e(
                    "InterstitialAdManager",
                    "NotificationSetting Ad dismissed fullscreen content."
                )
                mAdManagerInterstitialAd = null
                /// loadInterstitialAd(context)
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                // Called when ad fails to show.
                Log.e("InterstitialAdManager", "Ad failed to show fullscreen content.")
                mAdManagerInterstitialAd = null
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.e("InterstitialAdManager", "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.e("InterstitialAdManager2", "NotificationSetting Ad showed fullscreen content.")
            }
        }
    }

    private fun startLoadingAnim() {

        val animation = android.view.animation.AnimationUtils.loadAnimation(
            applicationContext, R.anim.blink_anim
        )

        binding!!.includedLoading.animLogo.startAnimation(animation)
    }

    private fun init() {

        getAllNotificationsList()
        Log.e("Init", "NotificationSettingActivity")
        //hideProgressView(progressBar)
        setting = sharedPreferencesUtil.fetchSettings()

        /*   if (getSharedPreferences("notyDisable", MODE_PRIVATE).getBoolean("notyDisable", false)){

               binding!!.notySwitch.checked = binding!!.IconSwitch.Checked.LEFT
           }
           else {

               binding!!.notySwitch.checked = binding!!.IconSwitch.Checked.RIGHT
           }

           if (getSharedPreferences("eventNotyDisable", MODE_PRIVATE).getBoolean("eventNotyDisable", false)){

               binding!!.eventNotySwitch.checked = binding!!.IconSwitch.Checked.LEFT
               binding!!.eventNotySwitch.isChecked = binding!!.eventNotySwitch.
           }
           else {

               binding!!.eventNotySwitch.checked = binding!!.IconSwitch.Checked.RIGHT
           }

           binding!!.notySwitch.setCheckedChangeListener(object : IconSwitch.CheckedChangeListener{
               override fun onCheckChanged(current: IconSwitch.Checked?) {

                   when (current) {

                       binding!!.IconSwitch.Checked.LEFT -> {
                           showProgressView(progressBar)
                           notificationUpdate(setting!!.allowPush != 0, ConstantsApi.ALLOW_PUSH)
                           getSharedPreferences("notyDisable", MODE_PRIVATE).edit().putBoolean("notyDisable", true).apply()
                           //Toast.makeText(this@NotificationSettingActivity, "Notification Disabled", Toast.LENGTH_SHORT).show()
                       }
                       binding!!.IconSwitch.Checked.RIGHT -> {

                           getSharedPreferences("notyDisable", MODE_PRIVATE).edit().putBoolean("notyDisable", false).apply()
                           Toast.makeText(this@NotificationSettingActivity, "Notification Enabled", Toast.LENGTH_SHORT).show()
                       }
                   }
               }
           })

           binding!!.eventNotySwitch.setCheckedChangeListener(object : IconSwitch.CheckedChangeListener{
               override fun onCheckChanged(current: IconSwitch.Checked?) {

                   when (current) {

                       binding!!.IconSwitch.Checked.LEFT -> {

                           showProgressView(progressBar)
                           notificationUpdate(setting!!.allowEvent != 0, ConstantsApi.ALLOW_EVENT)
                           notificationUpdate(
                               setting!!.allowEventUpdate != 0,
                               ConstantsApi.ALLOW_EVENT_UPDATE
                           )
                           getSharedPreferences("eventNotyDisable", MODE_PRIVATE).edit().putBoolean("eventNotyDisable", true).apply()
                           //Toast.makeText(this@NotificationSettingActivity, "Event Notification Disabled", Toast.LENGTH_SHORT).show()
                       }

                       binding!!.IconSwitch.Checked.RIGHT -> {

                           getSharedPreferences("eventNotyDisable", MODE_PRIVATE).edit().putBoolean("eventNotyDisable", false).apply()

                           Toast.makeText(this@NotificationSettingActivity, "Event Notification Enabled", Toast.LENGTH_SHORT).show()
                       }
                   }

               }
           })
   */
        with(binding!!.rvAllTypeNotification) {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                context, androidx.recyclerview.widget.RecyclerView.VERTICAL, false
            )
            adapter = allTypeNotificationsAdapter

            addItemDecoration(
                androidx.recyclerview.widget.DividerItemDecoration(
                    context, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
                )
            )
            // Set the adapter to your RecyclerView
            binding!!.rvAllTypeNotification.adapter = adapter
        }

    }

    private fun notificationUpdate(setting: Boolean, type: ConstantsApi) {
        if (null == mPresenter) mPresenter = SettingsPresenter(presenter)

        Log.e("API_Call", "notificationUpdate: $type")
        run {
            mPresenter!!.addSettingsObject(!setting)
            mPresenter!!.callRetrofit(type)
            firebaseUtil.updatePushNotificationStatus(sharedPreferencesUtil.userId(), !setting)
        }
    }

    fun enableDisableNotificationUpdate(id: Int) {
        if (null == mPresenter) mPresenter = SettingsPresenter(presenter)

        Log.e("API_Call", "notificationUpdate: $id")
        run {
            showProgressView(progressBar)
            mPresenter!!.addEnableDisableNotificationObject(EnableDisableNotificationReq().apply {
                notificationTypeId = id
            })
            mPresenter!!.callRetrofit(ConstantsApi.ENABLE_DISABLE_NOTIFICATION_TYPE)
            // firebaseUtil.updatePushNotificationStatus(sharedPreferencesUtil.userId(), !setting)
        }
    }

    internal fun getAllNotificationsList() {
        if (null == mPresenter) mPresenter = SettingsPresenter(presenter)

        run {
            showProgressView(progressBar)
            mPresenter!!.callRetrofit(ConstantsApi.GET_ALL_TYPE_NOTIFICATIONS)
        }
    }

}