package com.connect.meetupsfellow.mvp.view.activities

import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.constants.ProfileStatus
import com.connect.meetupsfellow.databinding.ActivityProfileUserBinding
import com.connect.meetupsfellow.developement.interfaces.ApiConnect
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.interfaces.ProfileListener
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.UserProfileConnector
import com.connect.meetupsfellow.mvp.presenter.activity.UserProfilePresenter
import com.connect.meetupsfellow.mvp.view.adapter.TabLayoutAdapter
import com.connect.meetupsfellow.mvp.view.dialog.FlagReportingDialog
import com.connect.meetupsfellow.mvp.view.fragment.ProfileFragment
import com.connect.meetupsfellow.mvp.view.fragment.TwitterFragment
import com.connect.meetupsfellow.retrofit.request.RequestReportUser
import com.connect.meetupsfellow.retrofit.request.RequestUserBlock
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ResponseUserData
import com.google.android.material.tabs.TabLayout
import retrofit2.Response
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject


@Suppress("UNUSED_PARAMETER")
class UserProfileActivity : CustomAppActivityCompatViewImpl() {

    private val presenter = object : UserProfileConnector.RequiredViewOps {
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
            Log.e("Api_Response", "Msg: ${response.message} ")
            when (type) {
                ConstantsApi.FETCH_PROFILE -> {

                    when (response.message.isNotEmpty()) {
                        true -> {
                            universalToast(response.message)
                            finish()
                        }

                        false -> {
                            when (response.userInfo.message.isEmpty()) {
                                true -> {
                                    profile = response.userInfo
                                    profileFragment.setupUserProfileDetails(profile!!)
                                }

                                false -> {
                                    universalToast(response.userInfo.message)
                                    finish()
                                }
                            }
                        }
                    }
                }

                ConstantsApi.REPORT_USER -> {
                    universalToast(response.message)
                    //showAlertClearChat("Do you want to block ${profile!!.name}?", "block")
                    showAlertBlockDialog()
                }

                ConstantsApi.BLOCK -> {
                    universalToast(response.message)
                    Handler().postDelayed({
                        finish()
                    }, 500)
                }

                ConstantsApi.SHARE_PRIVATE -> {
                    profileFragment.shared(response.isShareStatus > 0)
                    Log.e("Api_Response", "Shar_Privet: ${response.message} ")
                    universalToast(response.message)/* when {
                        response.isShareStatus > 0 -> {
                            universalToast("Your media album is now shared with user")
                        }
                    }*/
                }

                ConstantsApi.LIKE_USER_PROFILE_HEART -> {
                    Log.e("Api_Response", "Heart: ${response.message} ")
                    universalToast(response.message)
                }

                ConstantsApi.LIKE_USER_PROFILE_MULTIPLE_TIME -> {
                    Log.e("Api_Response", "Multiple_Like: ${response.message} ")
                    universalToast(response.message)

                }

                else -> {
                    Log.e("Api_Response", "Elss: ${response.message} ")
                }
            }
            hideProgressView(progressBar)
        }

    }

    private val profileListener = object : ProfileListener {
        override fun onProfile(status: ProfileStatus, link: String) {
            when (status) {
                ProfileStatus.Favorite -> markFavorite()

                ProfileStatus.HeartLike -> markHeartLike()

                ProfileStatus.Liked -> markProfileLikeMultipleTime()

                ProfileStatus.Clicked -> {
//                    viewPager.currentItem = 1
                    if (null != profile || profile!!.twitterLink.isNotEmpty()) {
                        switchActivity(Constants.Intent.Webview, false, Bundle().apply {
                            putString(
                                Constants.IntentDataKeys.TITLE,
                                getString(R.string.label_twitter_text).replace(":", "")
                            )
                            putString(Constants.IntentDataKeys.LINK, profile!!.twitterLink)
                        })
//                        twitterFragment.loadTwitterProfile(profile!!.twitterLink)
                    }
                }

                ProfileStatus.Insta -> {

                    if (null != profile || profile!!.instagramLink.isNotEmpty()) {
                        switchActivity(Constants.Intent.Webview, false, Bundle().apply {
                            putString(
                                Constants.IntentDataKeys.TITLE,
                                getString(R.string.label_insta_text).replace(":", "")
                            )
                            putString(Constants.IntentDataKeys.LINK, profile!!.instagramLink)
                        })
//                        twitterFragment.loadTwitterProfile(profile!!.twitterLink)
                    }
                }

                ProfileStatus.Youtube -> {

                    Log.e("SocialLinks", profile!!.xtubeLink)

                    if (null != profile && profile!!.xtubeLink.isNotEmpty()) {
                        switchActivity(Constants.Intent.Webview, false, Bundle().apply {
                            putString(
                                Constants.IntentDataKeys.TITLE,
                                getString(R.string.label_youtube_text).replace(":", "")
                            )
                            putString(Constants.IntentDataKeys.LINK, profile!!.xtubeLink)
                        })
                    }


                }


                ProfileStatus.Shared -> sharePrivate()
            }
        }
    }

    private var userId = -1

    var linkIntentData = 0

    private var profile: ResponseUserData? = null

    private var mPresenter: UserProfileConnector.PresenterOps? = null

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var apiConnect: ApiConnect

    private val profileFragment = ProfileFragment()
    private val twitterFragment = TwitterFragment()
    private var adapter: TabLayoutAdapter? = null
    private var isProfilePrivate = false
    val TAG = "UserProfileTAG"
    private var binding: ActivityProfileUserBinding? = null
    private lateinit var progressBar: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIntentData()
        binding = ActivityProfileUserBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        // setContentView(R.layout.activity_profile_user)
        progressBar = binding!!.includedLoading.rlProgress
        showProgressView(progressBar)
        component.inject(this@UserProfileActivity)
        // setupActionBarNavigation("", false, R.drawable.back_arrow)
        setupActionBar("", true)
        if (userId == -1) {
            finish()
            return
        }
        Log.e("userProfileAc*", "getData_otherUser_Activity")

        val param = binding!!.header.ivLogo.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(10, 10, 10, 10)
        binding!!.header.ivLogo.layoutParams = param

        startLoadingAnim()
        init()
        callForUserProfile()

        /*  val  interstitialAdManager : InterstitialAdManager = InterstitialAdManager.getInstance(this)
          interstitialAdManager.loadInterstitialAd(this)*/
    }

    private fun startLoadingAnim() {

        val animation = android.view.animation.AnimationUtils.loadAnimation(
            applicationContext, R.anim.blink_anim
        )

        binding!!.includedLoading.animLogo.startAnimation(animation)
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
        Log.e("lasttrackTime", lasttrackTime)
        LogManager.logger.i(ArchitectureApp.instance!!.tag, "onResume ::: " + lasttrackTime)

        if (lasttrackTime != "") {
            val currentSeconds = ((currentbackTime - lasttrackTime.toLong()) / 1000).toInt()
            if (currentSeconds >= Constants.TimerRefresh.START_TIME_IN_SEC) {
                startActivity(Intent(this, SplashActivity::class.java))
                finish()
            }
        }
        fetchUserProfile()
    }

    override fun onBackPressed() {
        Log.e("userProfileAc*", "OnBack_Pressed")/*if (linkIntentData == 1){
            switchActivity(Constants.Intent.Home, true, Bundle())
            finish()
        }*/
        val mngr = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val taskList = mngr.getRunningTasks(10)

        if (taskList[0].numActivities == 1 && taskList[0].topActivity!!.className == this::class.java.name) {
            Log.e(TAG, "This is the last activity in the stack")
            val intent = Intent(
                this, HomeActivity::class.java
            )
            startActivity(intent)
            finish()
        } else {
            Log.e(TAG, "activity allready exist in the stack")
            finish()
        }

        super.onBackPressed()
    }

    private fun getIntentData() {
        if (intent.hasExtra(Constants.IntentDataKeys.UserId)) {
            userId = intent.getIntExtra(Constants.IntentDataKeys.UserId, -1)
            linkIntentData = intent.getIntExtra(Constants.IntentDataKeys.LINK, 0)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_user, menu)
        val item = menu!!.findItem(R.id.menu_lock)
        item.isVisible = false
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_more -> {
                optionMenu()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun init() {

        adapter = TabLayoutAdapter(supportFragmentManager)
        adapter!!.addFragment(profileFragment, getText(R.string.label_profile_text))
//        adapter!!.addFragment(twitterFragment, getText(R.string.text_twitter))
        binding!!.viewPager.adapter = adapter
        binding!!.tabLayout.setupWithViewPager(binding!!.viewPager, true)
        binding!!.viewPager.currentItem = 0


        profileFragment.profileStatus(profileListener)


        binding!!.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                when (tab!!.position) {
                    1 -> {
                        setTwitter(
                            R.drawable.twitter, ContextCompat.getColor(
                                this@UserProfileActivity, R.color.colorWhite
                            )
                        )
                    }
                }
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab!!.position) {
                    0 -> {
//                        profileFragment.userId(userId)
                    }

                    1 -> {
                        setTwitter(
                            R.drawable.twitter_select, ContextCompat.getColor(
                                this@UserProfileActivity, R.color.colorTwitter
                            )
                        )
                        if (null != profile) twitterFragment.loadTwitterProfile(profile!!.twitterLink)
                    }
                }
            }
        })

        binding!!.tabLayout.visibility = View.GONE

    }

    private fun setProfile() {
        val layout = binding!!.tabLayout.getChildAt(0) as LinearLayout
        val item = layout.getChildAt(0) as LinearLayout
        val text = item.getChildAt(1) as TextView
        text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.user_profile, 0, 0, 0)
        text.compoundDrawablePadding = 10
    }

    private fun setTwitter(drawable: Int, color: Int) {
        val layout = binding!!.tabLayout.getChildAt(0) as LinearLayout
        val item = layout.getChildAt(1) as LinearLayout
        val text = item.getChildAt(1) as TextView
        text.setTextColor(color)
        text.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0)
        text.compoundDrawablePadding = 10
    }

    private fun optionMenu() {
        val dialog = Dialog(this@UserProfileActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_chat)
        dialog.setCanceledOnTouchOutside(true)
        val rect = locateView(binding!!.header.toolbar)
        val wmlp = dialog.window!!.attributes
        wmlp.gravity = Gravity.TOP
        wmlp.width = (resources.displayMetrics.widthPixels / 2.2).toInt()
        assert(rect != null)
        wmlp.x = rect!!.right
        wmlp.y = rect.top
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val report = dialog.findViewById<TextView>(R.id.tvClearChat)
        dialog.findViewById<TextView>(R.id.tvBlock).setOnClickListener {
            // showAlertClearChat("Are you sure, you want to block ${profile!!.name}?", "block")
            showAlertBlockDialog()
            dialog.cancel()
        }

        report.text = getString(R.string.text_flag)
        report.setOnClickListener {
            dialog.cancel()
            reportUser()
        }
        dialog.show()
    }

    fun reportUser() {

        val name = profile!!.name

        FlagReportingDialog.Builder(this@UserProfileActivity, Constants.DialogTheme.NoTitleBar)
            .heading("Report $name").reasons(sharedPreferencesUtil.fetchReason())
            .setOnCompletionListener(object : FlagReportingDialog.OnCompleteListener {
                override fun onComplete(param: String, dialog: DialogInterface) {
                    when (param.isNotEmpty()) {
                        true -> {
                            showProgressView(progressBar)
                            report(param)
                        }

                        false -> {}
                    }
                    dialog.dismiss()
                }
            }).build()
    }

    private fun report(message: String) {
        if (null == mPresenter) mPresenter = UserProfilePresenter(presenter)

        run {
            mPresenter!!.addReportUserObject(RequestReportUser().apply {
                id = "$userId"
                reason = message
            })
            mPresenter!!.callRetrofit(ConstantsApi.REPORT_USER)
        }
    }

    fun fetchUserProfile() {
        showProgressView(progressBar)
        if (null == mPresenter) mPresenter = UserProfilePresenter(presenter)

        run {
            mPresenter!!.addUserProfileObject("$userId")
            mPresenter!!.callRetrofit(ConstantsApi.FETCH_PROFILE)
        }
    }

    private fun markFavorite() {
        if (null == mPresenter) mPresenter = UserProfilePresenter(presenter)

        run {
            mPresenter!!.addUserProfileObject("$userId")
            mPresenter!!.callRetrofit(ConstantsApi.USER_LIKE)
        }
    }

    private fun markProfileLikeMultipleTime() {
        if (null == mPresenter) mPresenter = UserProfilePresenter(presenter)

        run {
            mPresenter!!.addUserProfileObject("$userId")
            mPresenter!!.callRetrofit(ConstantsApi.LIKE_USER_PROFILE_MULTIPLE_TIME)
        }
    }


    private fun markHeartLike() {
        if (null == mPresenter) mPresenter = UserProfilePresenter(presenter)

        run {
            mPresenter!!.addUserProfileObject("$userId")
            mPresenter!!.callRetrofit(ConstantsApi.LIKE_USER_PROFILE_HEART)
        }
    }

    private fun sharePrivate() {
        if (null == mPresenter) mPresenter = UserProfilePresenter(presenter)

        run {
            mPresenter!!.addUserProfileObject("$userId")
            mPresenter!!.callRetrofit(ConstantsApi.SHARE_PRIVATE)
        }
    }

    fun blockUser() {
        if (null == mPresenter) mPresenter = UserProfilePresenter(presenter)

        Log.e("block_Methode", "Block_API_call")

        run {
            mPresenter!!.addBlockUserObject(RequestUserBlock().apply {
                blockUserid = "$userId"
                status = "1"

            })
            mPresenter!!.callRetrofit(ConstantsApi.BLOCK)
        }
    }

    private fun showAlertClearChat(message: String, type: String) {
        val alertDialog = AlertDialog.Builder(this@UserProfileActivity, R.style.MyDialogTheme2)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton(getString(R.string.text_block)) { dialog, _ ->
            dialog.dismiss()
            showProgressView(progressBar)
            blockUser()
        }
        alertDialog.setNegativeButton(getString(R.string.label_cancel_text)) { dialog, _ ->
            dialog.cancel()
        }
        alertDialog.setCancelable(false)
        // Showing Alert Message
        alertDialog.show()
    }

    private fun showAlertBlockDialog() {
        Log.e("chatRoomId*&", "chatRoomId:- ${profile!!.directMessage}")
        val dialog = Dialog(this@UserProfileActivity)

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
        Log.e("Chat_Clear", "all_chat_clear_dialog")
        dialogHead.visibility = View.GONE
        // dialogHead.text = "Disconnect"
        dialogContent.text =
            "Are you sure you want to block ${profile!!.name}? They will no longer be able to contact you or view your profile."

        cancelBtn.setOnClickListener {
            dialog.cancel()
        }

        confirm.setOnClickListener {
            showProgressView(progressBar)
            blockUser()
            dialog.dismiss()
        }

        dialog.show()

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

                                    if (visible_pro_user!!) {

                                        //  pro_user_images.visibility = View.VISIBLE

                                    } else {

                                        //    pro_user_images.visibility = View.GONE
                                    }

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

    fun kilActivity() {
        val mngr = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val taskList = mngr.getRunningTasks(10)
        if (taskList[0].numActivities == 1 && taskList[0].topActivity!!.className == this::class.java.name) {
            Log.e(TAG, "This is the last activity in the stack")
            val intent = Intent(
                this, HomeActivity::class.java
            )
            startActivity(intent)
            finish()
        } else {
            Log.e(TAG, "activity allready exist in the stack")
            finish()
        }
    }
}