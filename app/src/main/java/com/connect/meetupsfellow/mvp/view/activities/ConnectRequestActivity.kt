package com.connect.meetupsfellow.mvp.view.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.databinding.ActivityConnectRequestBinding
import com.connect.meetupsfellow.databinding.ActivityFlagBinding
import com.connect.meetupsfellow.developement.interfaces.ApiConnect
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.utils.RecyclerViewClick
import com.connect.meetupsfellow.global.utils.ToolbarUtils
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.view.adapter.ConnectionViewPagerAdapter
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import javax.inject.Inject

class ConnectRequestActivity : CustomAppActivityCompatViewImpl() {
    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil
    private var mAdManagerInterstitialAd: AdManagerInterstitialAd? = null
    private lateinit var binding: ActivityConnectRequestBinding
    @Inject
    lateinit var apiConnect: ApiConnect

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this@ConnectRequestActivity)
        binding = ActivityConnectRequestBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
     //   setContentView(R.layout.activity_connect_request)
        ToolbarUtils.setupActionBar(
            this,
            binding!!.header.toolbar,
            getString(R.string.connect_request),
            binding!!.header.tvTitle,
            showBackArrow = true,
            activity = this
        )
        startLoadingAnim()
        init()
        if (!sharedPreferencesUtil.fetchUserProfile().isProMembership){
            loadInterstitialAd(this)
        }
    }

    private fun init() {

        val viewPager: ViewPager2 = findViewById(R.id.connection_viewPager)
        val tabLayout: TabLayout = findViewById(R.id.connection_tabLayout)

        val adapter = ConnectionViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Connected"
                1 -> "Received"
                2 -> "Sent"
                else -> ""
            }
        }.attach()
    }


    private fun startLoadingAnim() {
        val animation = android.view.animation.AnimationUtils.loadAnimation(
            applicationContext, R.anim.blink_anim
        )
        binding.includedLoading.animLogo.startAnimation(animation)
    }

    fun loadInterstitialAd(context : Context) {
        var adRequest = AdManagerAdRequest.Builder().build()
        AdManagerInterstitialAd.load(
            context,
            getString(R.string.InterstitialAd_unit_id),
            adRequest,
            object : AdManagerInterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("InterstitialAdManager", adError?.toString().toString())
                    mAdManagerInterstitialAd = null
                }
                override fun onAdLoaded(interstitialAd: AdManagerInterstitialAd) {
                    Log.e("InterstitialAdManager", "ConnectRequestActivity Ad was loaded.")
                    mAdManagerInterstitialAd = interstitialAd
                    showInterstitialAd(context)
                }
            })
    }

    fun showInterstitialAd(context: Context) {
        if (mAdManagerInterstitialAd != null) {
            mAdManagerInterstitialAd?.show(context as Activity)
        } else {
            Log.e("InterstitialAdManager", "ConnectRequestActivity The interstitial ad wasn't ready yet.")
        }

        mAdManagerInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.e("InterstitialAdManager", "ConnectRequestActivity Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.e("InterstitialAdManager", "ConnectRequestActivity Ad dismissed fullscreen content.")
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
                Log.e("InterstitialAdManager2", "ConnectRequestActivity Ad showed fullscreen content.")
            }
        }
    }

    override fun onResume() {
        super.onResume()

        var lasttrackTime = sharedPreferencesUtil.featchTimmerTime()
        var currentbackTime: Long = System.currentTimeMillis()
        Log.e("lasttrackTime", lasttrackTime)
        LogManager.logger.i(ArchitectureApp.instance!!.tag, "onResume Connect::: " + lasttrackTime)

        if (lasttrackTime != "") {
            val currentSeconds = ((currentbackTime - lasttrackTime.toLong()) / 1000).toInt()
            if (currentSeconds >= Constants.TimerRefresh.START_TIME_IN_SEC) {
                startActivity(Intent(this, SplashActivity::class.java))
                finish()
            }
        }

    }

    override fun onPause() {
        super.onPause()
        sharedPreferencesUtil.saveTimmerTime(System.currentTimeMillis().toString())
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag,
            "onPause connect::: " + System.currentTimeMillis().toString()
        )

        RecyclerViewClick.disableClick()
    }
}