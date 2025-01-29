package com.connect.meetupsfellow.mvp.view.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.databinding.LayoutFavoriteBinding
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.utils.ToolbarUtils
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.view.adapter.TabLayoutAdapter
import com.connect.meetupsfellow.mvp.view.fragment.FavoriteEventFragment
import com.connect.meetupsfellow.mvp.view.fragment.FavoriteUserFragment
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.google.android.material.tabs.TabLayout
import javax.inject.Inject


class FavoriteUserEvent : CustomAppActivityCompatViewImpl() {

    private var adapter: TabLayoutAdapter? = null
    private val favoriteUserFragment = FavoriteUserFragment()
    private val favoriteEventFragment = FavoriteEventFragment()
    private var indicatorWidth = 0
    lateinit var mAdViewFavFellow: AdManagerAdView

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil
    private var mAdManagerInterstitialAd: AdManagerInterstitialAd? = null
    private var binding: LayoutFavoriteBinding? = null
    private lateinit var progressBar: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this@FavoriteUserEvent)
        binding = LayoutFavoriteBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        progressBar = binding!!.includedLoading.rlProgress
        //setContentView(R.layout.layout_favorite)
       // setupActionBar(getString(R.string.label_favorite_fellows), true)
        ToolbarUtils.setupActionBar(
            this,
            binding!!.header.toolbar,
            getString(R.string.label_favorite_fellows),
            binding!!.header.tvTitle,
            showBackArrow = true,
            activity = this
        )
        startLoadingAnim()
        init()

        mAdViewFavFellow = findViewById(R.id.mAdViewFavFellow)
        val profile = sharedPreferencesUtil.fetchUserProfile()
        if (!profile.isProMembership) {
            loadInterstitialAd(this)
            loadBannerAd()
        } else {
            mAdViewFavFellow.visibility = View.GONE
        }

        //callForUserProfile()
    }

    private fun loadBannerAd() {
        MobileAds.initialize(this) {}

        val adRequest = AdManagerAdRequest.Builder().build()
        mAdViewFavFellow.loadAd(adRequest)

        mAdViewFavFellow.adListener = object : AdListener() {
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.e("mAdViewFavFellow", "onAdClicked@#")
            }

            override fun onAdClosed() {
                Log.e("mAdViewFavFellow", "onAdClicked@#")
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e("mAdViewFavFellow", "onAdFailedToLoad@#")
            }

            override fun onAdImpression() {
                Log.e("mAdViewFavFellow", "onAdImpression@#")
            }

            override fun onAdLoaded() {
                Log.e("mAdViewFavFellow", "onAdLoaded!@#")
                mAdViewFavFellow.visibility = View.VISIBLE

            }

            override fun onAdOpened() {
                Log.e("mAdViewFavFellow", "onAdOpened**")
            }
        }

    }

    fun loadInterstitialAd(context: Context) {
        var adRequest = AdManagerAdRequest.Builder().build()
        AdManagerInterstitialAd.load(context,
            getString(R.string.InterstitialAd_unit_id),
            adRequest,
            object : AdManagerInterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adError.toString()?.let { Log.d("InterstitialAdManager", it) }
                    mAdManagerInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: AdManagerInterstitialAd) {
                    Log.e("InterstitialAdManager", "FavoriteUserEvent Ad was loaded.")
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
                "InterstitialAdManager",
                "FavoriteUserEvent The interstitial ad wasn't ready yet."
            )
        }

        mAdManagerInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.e("InterstitialAdManager", "FavoriteUserEvent Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.e("InterstitialAdManager", "FavoriteUserEvent Ad dismissed fullscreen content.")
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
                Log.e("InterstitialAdManager2", "FavoriteUserEvent Ad showed fullscreen content.")
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

        adapter = TabLayoutAdapter(supportFragmentManager)
        adapter!!.addFragment(
            favoriteUserFragment, SpannableString(getString(R.string.label_users_text))
        )/*adapter!!.addFragment(
            favoriteEventFragment,
            SpannableString(getString(R.string.label_events_text))
        )*/
        binding!!.viewPagerFavorite.adapter = adapter
        binding!!.viewPagerFavorite.currentItem = 0
        binding!!.tabLayoutFavorite.setupWithViewPager(binding!!.viewPagerFavorite, true)
        binding!!.tabLayoutFavorite.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab!!.position) {
                    0 -> {
                        favoriteUserFragment.favoriteUser()
                    }

                    1 -> {
                        favoriteEventFragment.favoriteEvent()
                    }
                }
            }

        })

        binding!!.tabLayoutFavorite.post(Runnable {
            indicatorWidth =
                binding!!.tabLayoutFavorite.width / binding!!.tabLayoutFavorite.tabCount

            //Assign new width
            val indicatorParams = binding!!.indicator.layoutParams as FrameLayout.LayoutParams
            indicatorParams.width = indicatorWidth
            binding!!.indicator.layoutParams = indicatorParams
        })

        binding!!.viewPagerFavorite.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {

                val params = binding!!.indicator.layoutParams as FrameLayout.LayoutParams

                //Multiply positionOffset with indicatorWidth to get translation

                //Multiply positionOffset with indicatorWidth to get translation
                val translationOffset: Float = (positionOffset + position) * indicatorWidth
                params.leftMargin = translationOffset.toInt()
                binding!!.indicator.layoutParams = params

            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    override fun onPause() {
        super.onPause()
        sharedPreferencesUtil.saveTimmerTime(System.currentTimeMillis().toString())
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag,
            "onPause ::: " + System.currentTimeMillis().toString()
        )

    }

    override fun onResume() {
        super.onResume()

        var lasttrackTime = sharedPreferencesUtil.featchTimmerTime()
        var currentbackTime: Long = System.currentTimeMillis()
        Log.e("lasttrackTime: ", lasttrackTime)
        LogManager.logger.i(ArchitectureApp.instance!!.tag, "onResume ::: " + lasttrackTime)

        if (lasttrackTime != "") {
            val currentSeconds = ((currentbackTime - lasttrackTime.toLong()) / 1000).toInt()
            if (currentSeconds >= Constants.TimerRefresh.START_TIME_IN_SEC) {
                startActivity(Intent(this, SplashActivity::class.java))
                finish()
            }
        }

    }
}