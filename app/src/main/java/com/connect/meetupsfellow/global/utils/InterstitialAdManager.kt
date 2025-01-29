package com.connect.meetupsfellow.global.utils

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.util.Log
import com.google.android.gms.ads.*
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback

class InterstitialAdManager private constructor(context: Context) {
    private var mAdManagerInterstitialAd: AdManagerInterstitialAd? = null
    init {}
    private var isUserLoggedIn = false
    fun loadInterstitialAd(context : Context) {
        if (!isUserLoggedIn) return

        var adRequest = AdManagerAdRequest.Builder().build()
        AdManagerInterstitialAd.load(
            context,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : AdManagerInterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adError?.toString()?.let { Log.d("InterstitialAdManager", it) }
                    mAdManagerInterstitialAd = null
                }
                override fun onAdLoaded(interstitialAd: AdManagerInterstitialAd) {
                    Log.e("InterstitialAdManager", "Ad was loaded.")
                    mAdManagerInterstitialAd = interstitialAd
                    val runnable = Runnable {
                        showInterstitialAd(context)
                    }
                    val handler = Handler()
                    handler.postDelayed(runnable, 300000) // 10000 milliseconds = 10 seconds
                }
            })
    }

    fun showInterstitialAd(context: Context) {
        if (mAdManagerInterstitialAd != null) {
            mAdManagerInterstitialAd?.show(context as Activity)
        } else {
            Log.e("InterstitialAdManager", "The interstitial ad wasn't ready yet.")
        }

        mAdManagerInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.e("InterstitialAdManager2", "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.e("InterstitialAdManager2", "Ad dismissed fullscreen content.")
                mAdManagerInterstitialAd = null
                loadInterstitialAd(context)
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                // Called when ad fails to show.
                Log.e("InterstitialAdManager2", "Ad failed to show fullscreen content.")
                mAdManagerInterstitialAd = null
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.e("InterstitialAdManager2", "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.e("InterstitialAdManager2", "Ad showed fullscreen content.")
            }
        }
    }

    companion object {
        private var instance: InterstitialAdManager? = null

        fun getInstance(context: Context): InterstitialAdManager {
            if (instance == null) {
                instance = InterstitialAdManager(context)
            }
            return instance!!
        }
    }

    fun setUserLoggedIn(loggedIn: Boolean) {
        isUserLoggedIn = loggedIn
        if (!isUserLoggedIn) {
            mAdManagerInterstitialAd = null
        }
    }
}
