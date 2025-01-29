package com.connect.meetupsfellow.mvp.view.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.fragment.CustomFragment
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.developement.interfaces.ApiConnect
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.fragment.FavoriteUserConnector
import com.connect.meetupsfellow.mvp.presenter.fragment.FavoriteUserPresenter
import com.connect.meetupsfellow.mvp.view.activities.SplashActivity
import com.connect.meetupsfellow.mvp.view.adapter.MyFollowingAdapter
import com.connect.meetupsfellow.retrofit.request.FollowUnfollowUserReq
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import javax.inject.Inject


class FollowingFragment : CustomFragment() {
    private val presenter = object : FavoriteUserConnector.RequiredViewOps {
        override fun showToast(error: String, logout: Boolean?) {
            when (logout) {
                true -> {
                    logout()
                }

                false -> {}
                null -> {}
            }
            universalToast(error)
            hideProgressView(rl_progress)
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {
            when (type) {
                ConstantsApi.MY_FOLLOWING_LIST -> {
                    Log.e("FollowerAPI", "List: ${response.myFollowingList!!.size}")
                    if (response.myFollowingList != null && response.total_following > 0) {
                        myFollowingAdapter.add(
                            requireContext(), this@FollowingFragment, response.myFollowingList!!
                        )
                        myFollowingAdapter.notifyDataSetChanged()
                    } else {
                        myFollowingAdapter.clearItems()
                    }
                    updateUi()
                }

                ConstantsApi.UNFOLLOW_USER -> {
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                    hideProgressView(rl_progress)
                    getMyFollowingList()
                }

                else -> {
                    Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            hideProgressView(rl_progress)
        }
    }

    private var mPresenter: FavoriteUserConnector.PresenterOps? = null

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var apiConnect: ApiConnect
    lateinit var mAdManagerAdView: AdManagerAdView
    private val myFollowingAdapter = MyFollowingAdapter()
    lateinit var rvMyFollowing: RecyclerView
    lateinit var tvNoUser_following: TextView
    lateinit var no_following_img: ImageView
    lateinit var rl_progress: LinearLayout
    lateinit var animLogo: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_following, container, false)
        ArchitectureApp.component!!.inject(this@FollowingFragment)
        rvMyFollowing = view.findViewById(R.id.rvMyFollowing)
        mAdManagerAdView = view.findViewById(R.id.adManagerAdView_Following)
        no_following_img = view.findViewById(R.id.no_following_iv)
        tvNoUser_following = view.findViewById(R.id.tvNoUser_following)
        rl_progress = view.findViewById(R.id.rl_progress)
        animLogo = view.findViewById(R.id.animLogo)
        startLoadingAnim()

        init()
        val profile = sharedPreferencesUtil.fetchUserProfile()
        if (!profile.isProMembership) {
            loadBannerAd()
        } else {
            mAdManagerAdView.visibility = View.GONE
        }
        return view

    }

    private fun init() {
        with(rvMyFollowing) {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                context, RecyclerView.VERTICAL, false
            )
            adapter = myFollowingAdapter

            addItemDecoration(
                androidx.recyclerview.widget.DividerItemDecoration(
                    context, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
                )
            )
            // Set the adapter to your RecyclerView
            rvMyFollowing.adapter = adapter
        }
    }

    private fun loadBannerAd() {
        MobileAds.initialize(requireContext()) {}

        val adRequest = AdManagerAdRequest.Builder().build()
        mAdManagerAdView.loadAd(adRequest)

        mAdManagerAdView.adListener = object : AdListener() {
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.e("Adds", "onAdClicked@#")
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.e("Adds", "onAdClicked@#")
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                // Code to be executed when an ad request fails.
                Log.e("Adds", "onAdFailedToLoad@#")
            }

            override fun onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
                Log.e("Adds", "onAdImpression@#")
            }

            override fun onAdLoaded() {
                Log.e("Adds", "onAdLoaded!@#")
                mAdManagerAdView.visibility = View.VISIBLE

            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.e("Adds", "onAdOpened**")
            }
        }

    }

    private fun startLoadingAnim() {

        val animation = android.view.animation.AnimationUtils.loadAnimation(
            requireContext(), R.anim.blink_anim
        )

        animLogo.startAnimation(animation)
    }

    private fun updateUi() {
        when (myFollowingAdapter.itemCount > 0) {
            true -> {
                rvMyFollowing.visibility = View.VISIBLE
                tvNoUser_following.visibility = View.GONE
                no_following_img.visibility = View.GONE
            }

            false -> {
                rvMyFollowing.visibility = View.GONE
                tvNoUser_following.visibility = View.VISIBLE
                no_following_img.visibility = View.VISIBLE
            }
        }
    }

    fun unFollowUser(userId: Int) {
        if (null == mPresenter) mPresenter = FavoriteUserPresenter(presenter)

        run {
            mPresenter!!.followUnfollowUser(FollowUnfollowUserReq().apply {
                follower_id = userId
            })
            showProgressView(rl_progress)
            mPresenter!!.callRetrofit(ConstantsApi.UNFOLLOW_USER)
        }
    }

    fun getMyFollowingList() {
        if (null == mPresenter) mPresenter = FavoriteUserPresenter(presenter)

        run {
            showProgressView(rl_progress)
            mPresenter!!.callRetrofit(ConstantsApi.MY_FOLLOWING_LIST)
        }
    }

    override fun onResume() {
        super.onResume()

        var lasttrackTime = sharedPreferencesUtil.featchTimmerTime()
        var currentbackTime: Long = System.currentTimeMillis()
        Log.e("lasttrackTime", lasttrackTime)
        LogManager.logger.i(ArchitectureApp.instance!!.tag, "onResume ::: " + lasttrackTime)
        getMyFollowingList()

        if (lasttrackTime != "") {
            val currentSeconds = ((currentbackTime - lasttrackTime.toLong()) / 1000).toInt()
            if (currentSeconds >= Constants.TimerRefresh.START_TIME_IN_SEC) {
                startActivity(Intent(context, SplashActivity::class.java))
                //finish()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sharedPreferencesUtil.saveTimmerTime(System.currentTimeMillis().toString())
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag, "onPause ::: " + System.currentTimeMillis().toString()
        )
    }
}