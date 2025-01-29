package com.connect.meetupsfellow.mvp.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.viewpager2.widget.ViewPager2
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.databinding.ActivityFollowFollowingBinding
import com.connect.meetupsfellow.developement.interfaces.ApiConnect
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.utils.RecyclerViewClick
import com.connect.meetupsfellow.global.utils.ToolbarUtils
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.view.adapter.FollowFollowingViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import javax.inject.Inject

class FollowFollowingActivity : CustomAppActivityCompatViewImpl() {
    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil
    private var binding: ActivityFollowFollowingBinding? = null

    @Inject
    lateinit var apiConnect: ApiConnect
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFollowFollowingBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        //  setContentView(R.layout.activity_follow_following)
        component.inject(this@FollowFollowingActivity)
       // setupActionBar(getString(R.string.follower_following), true)
        ToolbarUtils.setupActionBar(
            this,
            binding!!.header.toolbar,
            getString(R.string.follower_following),
            binding!!.header.tvTitle,
            showBackArrow = true,
            activity = this
        )
        startLoadingAnim()
        init()
    }

    private fun init() {
        val viewPager: ViewPager2 = findViewById(R.id.follow_viewPager)
        val tabLayout: TabLayout = findViewById(R.id.follow_tabLayout)

        val adapter = FollowFollowingViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Followers"
                1 -> "Following"
                2 -> "Likes"
                else -> ""
            }
        }.attach()
    }

    private fun startLoadingAnim() {

        val animation = android.view.animation.AnimationUtils.loadAnimation(
            applicationContext, R.anim.blink_anim
        )
        binding!!.includedLoading.animLogo.startAnimation(animation)
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