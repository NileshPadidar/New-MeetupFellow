package com.connect.meetupsfellow.mvp.view.activities

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.constants.ItemClickStatus
import com.connect.meetupsfellow.databinding.ActivityBlockedBinding
import com.connect.meetupsfellow.developement.interfaces.ApiConnect
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.interfaces.ItemClick
import com.connect.meetupsfellow.global.utils.RecyclerViewClick
import com.connect.meetupsfellow.global.utils.ToolbarUtils
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.BlockedUsersConnector
import com.connect.meetupsfellow.mvp.presenter.activity.BlockedUsersPresenter
import com.connect.meetupsfellow.mvp.view.adapter.BlockedUsersAdapter
import com.connect.meetupsfellow.retrofit.request.RequestUserBlock
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ResponseUserData
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import retrofit2.Response
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject


class BlockedUsersActivity : CustomAppActivityCompatViewImpl() {

    private val presenter = object : BlockedUsersConnector.RequiredViewOps {
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
            Log.e("blocked", Gson().toJson(response))

            when (type) {
                ConstantsApi.BLOCKED_USERS -> {
                    blockedUsersAdapter.add(response.blockedUsers)
                    updateUi()
                }

                ConstantsApi.UNBLOCK -> {
                    updateUi()
                }

                else -> {

                }
            }
            hideProgressView(progressBar)
        }
    }

    private val clickListener = object : ItemClick {
        override fun onItemClick(position: Int, status: ItemClickStatus) {
            //showAlertUnblock("Are you sure, you want to unblock?", position)
            showAlertUnblockDialog(
                "Unblock User",
                "Are you sure you want to unblock this user? They will be able to contact you and view your profile again.",
                position
            )
        }
    }

    private fun showAlertUnblockDialog(head: String, content: String, position: Int) {

        val dialog = Dialog(this)

        dialog.setCancelable(true)

        val view = layoutInflater.inflate(R.layout.custom_exit_dialog, null)

        dialog.setContentView(view)

        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation

        if (dialog.window != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val dialogHead = view.findViewById<TextView>(R.id.dialog_title)
        val dialogContent = view.findViewById<TextView>(R.id.dialog_content)
        val cancelBtn = view.findViewById<Button>(R.id.noExitBtn)
        val confirm = view.findViewById<Button>(R.id.yesExitBtn)

        // dialogHead.text = head
        dialogHead.visibility = View.GONE
        dialogContent.text = content

        cancelBtn.setOnClickListener {

            dialog.cancel()
        }

        confirm.setOnClickListener {

            dialog.dismiss()
            userId = blockedUsersAdapter.remove(position)
            when (userId.isEmpty()) {
                true -> {
                    universalToast("No user found")
                }

                false -> {
                    showProgressView(progressBar)
                    unBlockChat(userId)
                    unBlockUser(userId)
                }
            }
        }

        dialog.show()
    }

    private fun unBlockChat(fellowId: String) {

        val profile = sharedPreferencesUtil.fetchUserProfile()

        val chatDb =
            FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference("Live")
                .child("UserChat")

        chatDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {

                if (snap.exists()) {

                    for (chats in snap.children) {

                        if (chats.key.toString().contains(profile.userId) && chats.key.toString()
                                .contains(fellowId)
                        ) {

                            chatDb.child(chats.key.toString()).child("blockedBy").setValue("")
                        }
                    }

                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }

    private val blockedUsersAdapter = BlockedUsersAdapter()

    private var mPresenter: BlockedUsersConnector.PresenterOps? = null

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var apiConnect: ApiConnect
    private var userId = ""
    lateinit var mAdManagerAdView: AdManagerAdView
    private var mAdManagerInterstitialAd: AdManagerInterstitialAd? = null

    private var binding: ActivityBlockedBinding? = null
    private lateinit var progressBar: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlockedBinding.inflate(layoutInflater)
        component.inject(this@BlockedUsersActivity)
        setContentView(binding!!.root)
        progressBar = binding!!.includedLoading.rlProgress
        // setContentView(R.layout.activity_blocked)
        ToolbarUtils.setupActionBar(
            this,
            binding!!.header.toolbar,
            getString(R.string.label_block_text),
            binding!!.header.tvTitle,
            showBackArrow = true,
            activity = this
        )

        showProgressView(progressBar)
        startLoadingAnim()
        init()
        callForUserProfile()

        mAdManagerAdView = findViewById(R.id.adManagerAdView)
        val profile = sharedPreferencesUtil.fetchUserProfile()
        if (!profile.isProMembership) {
            loadInterstitialAd(this)
            loadBannerAd()
        } else {
            mAdManagerAdView.visibility = View.GONE
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
                    Log.e("InterstitialAdManager", "BlockedUsersActivity Ad was loaded.")
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
                "BlockedUsersActivity The interstitial ad wasn't ready yet."
            )
        }

        mAdManagerInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.e("InterstitialAdManager", "BlockedUsersActivity Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.e(
                    "InterstitialAdManager", "BlockedUsersActivity Ad dismissed fullscreen content."
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
                Log.e(
                    "InterstitialAdManager2", "BlockedUsersActivity Ad showed fullscreen content."
                )
            }
        }
    }

    private fun loadBannerAd() {
        MobileAds.initialize(this) {}

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
            applicationContext, R.anim.blink_anim
        )
        binding!!.includedLoading.animLogo.startAnimation(animation)
    }

    override fun onResume() {
        super.onResume()

        var lasttrackTime = sharedPreferencesUtil.featchTimmerTime()
        var currentbackTime: Long = System.currentTimeMillis()
        Log.e("lasttrackTime", lasttrackTime)
        LogManager.logger.i(ArchitectureApp.instance!!.tag, "onResume ::: " + lasttrackTime)

        if (lasttrackTime != "") {
            val currentSeconds = ((currentbackTime - lasttrackTime.toLong()) / 1000).toInt()
            if (currentSeconds >= Constants.TimerRefresh.START_TIME_IN_SEC) {
                startActivity(Intent(this, SplashActivity::class.java))
                finish()
            }
        }
        RecyclerViewClick.enableClick(clickListener)

    }

    override fun onPause() {
        super.onPause()
        sharedPreferencesUtil.saveTimmerTime(System.currentTimeMillis().toString())
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag, "onPause ::: " + System.currentTimeMillis().toString()
        )

        RecyclerViewClick.disableClick()
    }

    private fun init() {
        with(binding!!.rvBlockedUsers) {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                binding!!.rvBlockedUsers.context,
                androidx.recyclerview.widget.RecyclerView.VERTICAL,
                false
            )
            adapter = blockedUsersAdapter
            addItemDecoration(
                androidx.recyclerview.widget.DividerItemDecoration(
                    binding!!.rvBlockedUsers.context,
                    androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
                )
            )
        }

        getBlockedUsers()
    }

    private fun getBlockedUsers() {
        if (null == mPresenter) mPresenter = BlockedUsersPresenter(presenter)

        run {
            mPresenter!!.callRetrofit(ConstantsApi.BLOCKED_USERS)
        }
    }

    private fun unBlockUser(userId: String) {
        if (null == mPresenter) mPresenter = BlockedUsersPresenter(presenter)

        run {
            mPresenter!!.addBlockUserObject(RequestUserBlock().apply {
                blockUserid = userId
                status = "0"
            })
            mPresenter!!.callRetrofit(ConstantsApi.UNBLOCK)
        }
    }

    private fun updateUi() {
        when (blockedUsersAdapter.itemCount > 0) {
            true -> {
                binding!!.rvBlockedUsers.visibility = View.VISIBLE
                binding!!.tvNoUser.visibility = View.GONE
                binding!!.blockImg.visibility = View.GONE
            }

            false -> {
                binding!!.rvBlockedUsers.visibility = View.GONE
                binding!!.tvNoUser.visibility = View.VISIBLE
                binding!!.blockImg.visibility = View.VISIBLE
            }
        }
    }

    private fun showAlertUnblock(message: String, position: Int) {
        val alertDialog = AlertDialog.Builder(this@BlockedUsersActivity, R.style.MyDialogTheme2)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton(getString(R.string.text_unblock)) { dialog, _ ->
            dialog.dismiss()
            userId = blockedUsersAdapter.remove(position)
            when (userId.isEmpty()) {
                true -> {
                    universalToast("No user found")
                }

                false -> {
                    showProgressView(progressBar)
                    unBlockUser(userId)
                }
            }
        }
        alertDialog.setNegativeButton(getString(R.string.label_cancel_text)) { dialog, _ ->
            dialog.cancel()
        }
        alertDialog.setCancelable(false)
        // Showing Alert Message
        alertDialog.show()
    }

    private fun callForUserProfile() {
        try {
            val user = sharedPreferencesUtil.fetchUserProfile()

            val people = apiConnect.api.userProfile(
                user.id, sharedPreferencesUtil.loginToken(), Constants.Random.random()
            )

            people.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response<CommonResponse>> {
                    override fun onError(e: Throwable?) {
                        e!!.printStackTrace()
                        //returnMessage("${e.message}")
                    }

                    override fun onNext(response: Response<CommonResponse>?) {
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag, "Response is ::: $response"
                        )
                        if (response != null) {
                            when (response.isSuccessful) {
                                true -> {
                                    val response: CommonResponse? = response.body()
                                    val profile: ResponseUserData? = response?.userInfo
                                    var visible_pro_user: Boolean? = profile?.isProMembership

                                    /* if (visible_pro_user!!) {

                                         tv_pro_visibility.visibility = View.VISIBLE

                                     } else {

                                         tv_pro_visibility.visibility = View.INVISIBLE
                                     }*/

                                }

                                false -> {
                                    // returnError(response.errorBody()!!.charStream())
                                }
                            }

                        } else {
                            //  returnMessage(ArchitectureApp.instance!!.getString(R.string.error_title))
                        }
                    }

                    override fun onCompleted() {
                    }
                })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}