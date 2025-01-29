package com.connect.meetupsfellow.mvp.view.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.connect.meetupsfellow.BuildConfig
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.Constants.AWS.URL.AWS_URL_PROFILE
import com.connect.meetupsfellow.constants.Constants.getUTCDateTime.getUTCTime
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.constants.ItemClickStatus
import com.connect.meetupsfellow.databinding.ActivityProfileSelfBinding
import com.connect.meetupsfellow.developement.interfaces.ApiConnect
import com.connect.meetupsfellow.developement.interfaces.FirebaseUtil
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.interfaces.ItemClick
import com.connect.meetupsfellow.global.interfaces.ProfileListener
import com.connect.meetupsfellow.global.utils.DisplayImage
import com.connect.meetupsfellow.global.utils.RecyclerViewClick
import com.connect.meetupsfellow.global.utils.compressImage
import com.connect.meetupsfellow.global.view.easyphotopicker.ConstantsImage
import com.connect.meetupsfellow.global.view.easyphotopicker.DefaultCallback
import com.connect.meetupsfellow.global.view.easyphotopicker.EasyImage
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.CreateProfileConnector
import com.connect.meetupsfellow.mvp.connector.activity.PrivateAlbumConnector
import com.connect.meetupsfellow.mvp.presenter.activity.CreateProfilePresenter
import com.connect.meetupsfellow.mvp.presenter.activity.PrivateAlbumPresenter
import com.connect.meetupsfellow.mvp.view.adapter.*
import com.connect.meetupsfellow.mvp.view.dialog.AlertBuyPremium
import com.connect.meetupsfellow.mvp.view.model.*
import com.connect.meetupsfellow.retrofit.request.ReqPrivetUserProfile
import com.connect.meetupsfellow.retrofit.request.RequestCreateUser
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ResponsePrivatePics
import com.connect.meetupsfellow.retrofit.response.ResponseUserData
import com.connect.meetupsfellow.retrofit.response.ResponseUserProfileImages
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Response
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import javax.inject.Inject

class ProfileActivity : CustomAppActivityCompatViewImpl() {

    private val pageChangeListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
        }

        override fun onTabSelected(tab: TabLayout.Tab?) {
            when (tab!!.position) {
                0 -> setSelected(binding!!.stats.llMain)
                1 -> setSelected(binding!!.stats.llMain)
            }
        }
    }

    private val itemClick = object : ItemClick {
        override fun onItemClick(position: Int, status: ItemClickStatus) {
            this@ProfileActivity.position = position
            Log.d("position", this@ProfileActivity.position.toString())

            Log.d("status", status.toString())

            when (status) {

                ItemClickStatus.Add -> {

                    Log.d("position", this@ProfileActivity.position.toString())
                    getPics(position)

                }

                else -> {}
            }
        }
    }


    private val presenterP = object : PrivateAlbumConnector.RequiredViewOps {
        override fun showToast(error: String, logout: Boolean?) {
            when (logout) {
                true -> {
                    logout()
                }

                else -> {}
            }
            universalToast(error)
            privateAlbumAdapter.clearAll()

            hideProgressView(progressBar)

        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {
            when (type) {
                ConstantsApi.UPLOAD_PRIVATE -> {
                    universalToast(response.message)
                    showProgressView(progressBar)
                    privatePicsLocal.clear()
                    //showHideSave()
                    fetchPrivateAlbum()
                }

                ConstantsApi.PRIVATE_ALBUM, ConstantsApi.OTHER_PRIVATE_ALBUM -> {
                    userPicRv.layoutManager = LinearLayoutManager(
                        this@ProfileActivity, LinearLayoutManager.HORIZONTAL, false
                    )
                    userPicRv.hasFixedSize()
                    privateAlbumAdapter.setType(PrivateAlbumAdapter.Image)
                    privateAlbumAdapter.privateAlbum(response.privatePics)
                    showProgressView(progressBar)
                    if (response.privatePics.size <= 0) {

                        binding!!.stats.addPrivateImgIc.visibility = View.GONE
                        userPicRv.visibility = View.GONE
                        binding!!.stats.addImgBtn.visibility = View.VISIBLE
                        binding!!.stats.noPrivateImgLay.visibility = View.VISIBLE
                        binding!!.stats.tvMediaPublic.visibility = View.GONE
                        hideProgressView(progressBar)
                    } else {
                        setData(response.privatePics)
                    }
                    Log.d("responsePics", response.privatePics.size.toString())
                    userPicRv.visibility = View.VISIBLE
                    //tvNoUser.visibility = View.GONE
                }

                ConstantsApi.DELETE_PRIVATE -> {
                    universalToast(response.message)
                    //showHideSave()
                    showProgressView(progressBar)
                    fetchPrivateAlbum()
                }

                ConstantsApi.MACK_PROFILE_PRIVET -> {
                    Toast.makeText(this@ProfileActivity, response.message, Toast.LENGTH_SHORT)
                        .show()
                    if (response.isprofileprivate) {
                        binding!!.btnTxtPrivet.text = getString(R.string.make_public)
                        profile!!.isprofileprivate = 1
                    } else {
                        binding!!.btnTxtPrivet.text = getString(R.string.make_privet)
                        profile!!.isprofileprivate = 0
                    }
                    if (response.isprofilehidden) {
                        profile!!.isprofilehidden = 1
                    } else {
                        profile!!.isprofilehidden = 0
                    }
                }

                else -> {
                }
            }
            //hideProgressView(rl_progress)
        }
    }

    private fun setData(privatePics: ArrayList<ResponsePrivatePics>) {

        Log.d("called", "called")
        recycleViewModels.clear()
        userPicRv.itemAnimator = null

        if (privatePics.size >= 1) {

            binding!!.stats.addPrivateImgIc.visibility = View.VISIBLE
            userPicRv.visibility = View.VISIBLE
            binding!!.stats.noPrivateImgLay.visibility = View.GONE
            binding!!.stats.addImgBtn.visibility = View.GONE
            if (sharedPreferencesUtil.fetchUserProfile().currentPlanInfo!!.isMediaPrivate < 1) {
                binding!!.stats.tvMediaPublic.visibility = View.VISIBLE
            }

            var privatePicNew = ArrayList<ResponsePrivatePics>()

            Allimages = Gson().toJson(privatePics)

            if (privatePics.size > 10) {

                for (i in 0..10) {

                    privatePicNew.add(privatePics[i])
                }
                Allimages = Gson().toJson(privatePicNew)

                for (i in 0..10) {

                    recycleViewModels.add(
                        RecycleModel(
                            privatePics[i].path,
                            profile!!.userId,
                            Allimages,
                            privatePics.size,
                            privatePics[i].type
                        )
                    )
                    recycleViewUserPic = RecycleViewUserPicAdapter(this, recycleViewModels)
                    userPicRv.adapter = recycleViewUserPic
                    recycleViewUserPic.notifyItemRangeInserted(i, privatePics.lastIndex)

                }

                hideProgressView(progressBar)
            } else {

                for (i in privatePics.indices) {

                    recycleViewModels.add(
                        RecycleModel(
                            privatePics[i].path,
                            profile!!.userId,
                            Allimages,
                            privatePics.size,
                            privatePics[i].type
                        )
                    )
                    recycleViewUserPic = RecycleViewUserPicAdapter(this, recycleViewModels)
                    userPicRv.adapter = recycleViewUserPic
                    recycleViewUserPic.notifyItemRangeInserted(i, privatePics.lastIndex)

                }
                hideProgressView(progressBar)
            }


            //userPicRv.getRecycledViewPool().clear()

            recycleViewUserPic.notifyDataSetChanged()


        } else {

            userPicRv.visibility = View.GONE
            binding!!.stats.addImgBtn.visibility = View.VISIBLE
            binding!!.stats.noPrivateImgLay.visibility = View.VISIBLE
            binding!!.stats.tvMediaPublic.visibility = View.GONE

        }


    }

    private val presenter = object : CreateProfileConnector.RequiredViewOps {
        override fun showToast(error: String, logout: Boolean?) {
            when (logout) {
                true -> {
                    logout()
                }

                else -> {}
            }
            universalToast(error)
            hideProgressView(progressBar)
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {
            when (type) {
                ConstantsApi.CREATE_PROFILE -> {
                    Log.e("Your_Profile", "CREATE_PROFILE: ${response.message}")
                    ///  isUpdated = true
                    featchProfile()
                    if (response.message.isNotEmpty()) {
                        universalToast(response.message)
                    }
                    showSuccessDialog()
                    //setProfileDetails()

                }

                ConstantsApi.FETCH_PROFILE -> {
                    profile = response.userInfo
                    Log.e("Your_Profile", "proMamber: ${profile!!.isProMembership}")
                    Log.e("Your_Profile", "name: ${profile!!.name}")
                    Log.e("Your_Profile", "number: ${profile!!.number}")
                    this@ProfileActivity.onResume()
                }

                ConstantsApi.FETCH_SELF_PROFILE -> {

                    val profile: ResponseUserData? = response.userInfo
                    Log.e("FETCH_SELF_PROFILE", "Name: ${profile!!.name}")
                    Log.e("FETCH_SELF_PROFILE", "count: ${profile.available_connection_counts}")
                    binding!!.includedUserImg.imagecount1.text = "1/" + profile.images.size
                }

                else -> {}
            }

            //hideProgressView(rl_progress)
        }

    }

    private fun featchProfile() {

        if (null == mPresenter) mPresenter = CreateProfilePresenter(presenter)

        run {
            mPresenter!!.addCreateUserObject(requestCreateUser)
            mPresenter!!.callRetrofit(ConstantsApi.FETCH_PROFILE)
        }
    }

    private fun fetchSelfProfile() {

        if (null == mPresenter) mPresenter = CreateProfilePresenter(presenter)

        run {
            mPresenter!!.callRetrofit(ConstantsApi.FETCH_SELF_PROFILE)
        }
    }

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var apiConnect: ApiConnect
    var linkIntentData = 0

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    private var profile: ResponseUserData? = null
    private var txtProUser: TextView? = null

    //private lateinit var userInterestRv : RecyclerView
    private var recycleViewModels = ArrayList<RecycleModel>()
    private lateinit var adapter: CustomViewAdapter
    private lateinit var recycleViewUserInterest: RecycleViewInterest
    private var name = ""
    private var mPresenter: CreateProfileConnector.PresenterOps? = null
    private var profileListener: ProfileListener? = null
    private var mPresenterP: PrivateAlbumConnector.PresenterOps? = null
    private lateinit var recycleViewUserPic: RecycleViewUserPicAdapter
    lateinit var userPicRv: RecyclerView
    private var photoFile: File? = null
    lateinit var dialogEdTxtAge: TextInputEditText
    lateinit var userEditShortBiotxt: TextInputEditText
    lateinit var dialogEdTxtEmail: TextInputEditText
    lateinit var dialogEdTxtHome: TextInputEditText
    lateinit var dialogEdTxtInsta: TextInputEditText
    lateinit var dialogEdTxtTwit: TextInputEditText
    lateinit var dialogEdTxtYou: TextInputEditText
    lateinit var dob_picker_edit: EditText
    lateinit var userAge_txt_edt: TextView
    var userAge = ""
    lateinit var dialog: Dialog
    lateinit var dialogImg: Dialog
    lateinit var dialogSocial: Dialog
    private var position = -1
    lateinit var userImg1: ImageView
    lateinit var userImg2: ImageView
    lateinit var userImg3: ImageView
    lateinit var userImg4: ImageView
    lateinit var userImg5: ImageView
    lateinit var userCross1: ImageView
    lateinit var userCross2: ImageView
    lateinit var userCross3: ImageView
    lateinit var userCross4: ImageView
    lateinit var userCross5: ImageView
    var isUpdated = false
    var Allimages = ""
    private val images = arrayListOf<ProfileImageModel>()
    private val privatePicsLocal = arrayListOf<PrivateAlbumModel>()
    private val privateAlbumAdapter = PrivateAlbumAdapter()
    val sliderDataArrayList = ArrayList<SliderData>()
    var imgFile: ArrayList<File> = ArrayList<File>()
    private var requestCreateUser = RequestCreateUser()
    private var reqPrivetUserProfile = ReqPrivetUserProfile()
    var selectedImg = 0
    val myCalendar = Calendar.getInstance()

    lateinit var tvFisting1: TextView
    lateinit var tvFisting2: TextView
    lateinit var tvFisting3: TextView
    lateinit var tvIntercourse1: TextView
    lateinit var tvIntercourse2: TextView
    lateinit var tvIntercourse3: TextView
    lateinit var tvCondomYes: TextView
    lateinit var tvCondomNo: TextView
    lateinit var tvPrevention: TextView
    private val identity = arrayListOf<TextView>()
    private val selectedIdentity = arrayListOf<String>()
    lateinit var tvIdentity1: TextView
    lateinit var tvIdentity2: TextView
    lateinit var tvIdentity3: TextView
    lateinit var tvIdentity4: TextView
    lateinit var tvIdentity5: TextView
    lateinit var tvIdentity6: TextView
    lateinit var tvIdentity7: TextView
    lateinit var tvIdentity8: TextView
    lateinit var tvIdentity9: TextView
    lateinit var tvIdentity10: TextView
    lateinit var tvIdentity11: TextView
    lateinit var tvIdentity12: TextView
    lateinit var tvIdentity13: TextView
    lateinit var tvIdentity14: TextView
    lateinit var tvIdentity15: TextView
    lateinit var tvIdentity16: TextView
    lateinit var tvIdentity17: TextView
    lateinit var tvIdentity18: TextView
    lateinit var tvIdentity19: TextView
    private val members = arrayListOf<TextView>()
    private val selectedMembers = arrayListOf<String>()
    lateinit var tvMember1: TextView
    lateinit var tvMember2: TextView
    lateinit var tvMember3: TextView
    lateinit var tvMember4: TextView
    lateinit var tvMember5: TextView
    lateinit var tvMember6: TextView
    lateinit var tvMember7: TextView
    lateinit var tvMember8: TextView
    lateinit var tvMember9: TextView
    lateinit var tvMember10: TextView
    lateinit var tvMember11: TextView
    lateinit var tvMember12: TextView
    lateinit var tvMember13: TextView
    lateinit var tvMember14: TextView
    lateinit var tvMember15: TextView
    lateinit var tvMember16: TextView
    lateinit var tvMember17: TextView
    lateinit var tvMember18: TextView
    lateinit var tvMember19: TextView
    private var binding: ActivityProfileSelfBinding? = null
    private lateinit var progressBar: LinearLayout
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage  = 0
    val imgPathArr = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSelfBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        // setContentView(R.layout.activity_profile_self)
        component.inject(this@ProfileActivity)
        setupActionBar(getString(R.string.title_profile), true)
        progressBar = binding!!.includedLoading.rlProgress
        startLoadingAnim()
        init()
        Log.e("ProfileActivity", "activity_profile_self@#")
        /// Show for interstitial ad
        /* val interstitialAdManager : InterstitialAdManager = InterstitialAdManager.getInstance(this)
               interstitialAdManager.loadInterstitialAd(this)*/

        if (intent != null && intent.hasExtra(Constants.IntentDataKeys.LINK)) {
            linkIntentData = intent.getIntExtra(Constants.IntentDataKeys.LINK, 0)
        }
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
            ArchitectureApp.instance!!.tag, "onPause ::: " + System.currentTimeMillis().toString()
        )

    }

    //tv_pro_user
    override fun onResume() {
        super.onResume()

        showProgressView(progressBar)

        RecyclerViewClick.enableClick(itemClick)

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

        updateUserStatus()
        checkRequiredPermissions()

        profile = sharedPreferencesUtil.fetchUserProfile()
        Log.e("Your_Profile", "OnResume_Profile ${profile}")
        if (null != profile) {
            Log.e("Your_Profile", "OnResume_Profile22")
            setProfileDetails()
        } else {
            finish()
            return
        }

        /*  if (isUpdated) {
              showSuccessDialog()
              isUpdated = false
          }*/

    }

    override fun onBackPressed() {
        if (linkIntentData == 1) {
            switchActivity(Constants.Intent.Home, true, Bundle())
            finish()
        }
        super.onBackPressed()
    }

    private fun showSuccessDialog() {

        Log.d("run1111", "run")

        val dialog = Dialog(this@ProfileActivity)

        dialog.setCancelable(false)

        //val activity = context as AppCompatActivity

        val view = layoutInflater.inflate(R.layout.profile_update_sucess_dialog, null)

        dialog.setContentView(view)

        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation

        if (dialog.window != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val ok_btn = view.findViewById<Button>(R.id.ok_btn)

        ok_btn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_profile, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_edit -> {
                switchActivity(Constants.Intent.Edit, false, Bundle())
                true
            }

            R.id.menu_private -> {
                switchActivity(Constants.Intent.PrivateAlbum, false, Bundle())
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun init() {

        fetchPrivateAlbum()

        userPicRv = findViewById(R.id.userPicsRv)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        userPicRv.layoutManager = layoutManager
        userPicRv.itemAnimator = null

        recycleViewModels.clear()

        binding!!.tabLayout.addTab(
            binding!!.tabLayout.newTab().setText(getString(R.string.label_stats_text))
        )
        binding!!.tabLayout.addTab(
            binding!!.tabLayout.newTab().setText(getString(R.string.label_bio_text))
        )
        binding!!.tabLayout.addOnTabSelectedListener(pageChangeListener)

        binding!!.tvUserEdit.setOnClickListener {

            switchActivity(Constants.Intent.Edit, false, Bundle())
        }

        binding!!.stats.editUserAgeIc.visibility = View.VISIBLE
        binding!!.stats.addPrivateImgIc.visibility = View.VISIBLE
        binding!!.stats.addSocialLinksIc.visibility = View.VISIBLE

        //tvLabelEmail.visibility = View.GONE
        binding!!.stats.tvEmail1.visibility = View.VISIBLE
        //viewEmail.visibility = View.GONE
        //tvLabelMobile.visibility = View.GONE
        binding!!.stats.tvMobileNumber1.visibility = View.VISIBLE
        binding!!.stats.phoneEmailLay.visibility = View.VISIBLE
        //viewMobile.visibility = View.GONE

        binding!!.includedUserImg.rlShared1.visibility = View.GONE

        binding!!.includedUserImg.likeCount.visibility = View.VISIBLE
        binding!!.includedUserImg.followerCount.visibility = View.VISIBLE

        binding!!.includedUserImg.userProfileBackBtn.setOnClickListener {
            if (linkIntentData == 1) {
                switchActivity(Constants.Intent.Home, true, Bundle())
                finish()
            } else {
                finish()
            }
        }

        binding!!.includedUserImg.rlProfileImage1.setOnClickListener {
            switchActivity(Constants.Intent.ImagePager, false, Bundle().apply {
                putString(Constants.IntentDataKeys.ProfileImages, Gson().toJson(profile!!.images))
                putString(Constants.IntentDataKeys.UserId, profile!!.userId)
                putString("Class", "Profile")
            })
        }

        binding!!.stats.becomeProTxt.setOnClickListener {

            AlertBuyPremium.Builder(this@ProfileActivity, Constants.DialogTheme.NoTitleBar).build()
                .show()
        }


        binding!!.includedUserImg.addUserImage.setOnClickListener {
            Log.e("profile_Act0", "addUserImg_dialog")
            showUserImgUploadDialog()

        }

        binding!!.stats.editUserAgeIc.setOnClickListener {
            showEditPopUp()
        }

        binding!!.stats.addPreferencesIc.setOnClickListener {
            showPreferencePopUp()
        }

        binding!!.stats.ivAddPref.setOnClickListener {
            showPreferencePopUp()
        }

        binding!!.stats.addPrivateImgIc.setOnClickListener {

            switchActivity(Constants.Intent.PrivateAlbum, false, Bundle())

        }

        binding!!.stats.shareProfile.setOnClickListener {
            // Create a dynamic link with pagination parameters share your profile
            FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.example.com/?contentId=${profile!!.userId}")) // Pagination parameters included
                .setDomainUriPrefix("https://meetupsfellow.page.link")
                .setAndroidParameters(DynamicLink.AndroidParameters.Builder().build())
                .buildShortDynamicLink().addOnSuccessListener(this) { shortDynamicLink ->
                    val shortLink = shortDynamicLink.shortLink.toString()
                    // Handle the short link, e.g., share it or open it
                    Log.e("DeepLink@*", "data: $shortLink")

                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shortLink)
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                }

        }

        binding!!.stats.addSocialLinksIc.setOnClickListener {

            showSocialLinkDialog()
        }

        binding!!.stats.addImgBtn.setOnClickListener {

            switchActivity(Constants.Intent.PrivateAlbum, false, Bundle())
        }

        binding!!.stats.tvTwitter111.setOnClickListener {

            Log.d("SocialLinks", profile!!.twitterLink)

            if (null != profile && profile!!.twitterLink.isNotEmpty()) {
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

        binding!!.stats.tvInstagram111.setOnClickListener {

            Log.d("SocialLinks", profile!!.instagramLink)

            if (null != profile && profile!!.instagramLink.isNotEmpty()) {
                switchActivity(Constants.Intent.Webview, false, Bundle().apply {
                    putString(
                        Constants.IntentDataKeys.TITLE,
                        getString(R.string.label_insta_text).replace(":", "")
                    )
                    putString(Constants.IntentDataKeys.LINK, profile!!.instagramLink)
                })
            }
        }

        binding!!.stats.tvYoutube111.setOnClickListener {

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

        binding!!.includedUserImg.addUserImage.visibility = View.VISIBLE
        binding!!.stats.ivChat1Lay.visibility = View.VISIBLE
        binding!!.includedUserImg.ivFavorite1.visibility = View.GONE
        //  binding!!.stats.tvUserAway1.visibility = View.GONE
        binding!!.stats.view1.visibility = View.GONE
        binding!!.stats.ivUserStatus1.visibility = View.GONE
        binding!!.includedUserImg.ivUserMultipleLike1.visibility = View.VISIBLE
        binding!!.includedUserImg.ivFollow.visibility = View.VISIBLE

        binding!!.includedUserImg.ivFollow.setOnClickListener {
            Log.e("profile_Click", "ivFollow_Click")
            switchActivity(Constants.Intent.FollowFollowingActivity, false, Bundle())
        }

        binding!!.includedUserImg.ivUserMultipleLike1.setOnClickListener {
            Log.e("profile_Click", "ivLike_Click")
            switchActivity(Constants.Intent.FollowFollowingActivity, false, Bundle())
        }

        binding!!.includedUserImg.ivFavorite1.setOnClickListener {
            Log.e("profile_Click", "ivFavorite_Click")
            switchActivity(Constants.Intent.Favorite, false, Bundle())
        }

    }

    private fun showSocialLinkDialog() {

        dialogSocial = Dialog(this@ProfileActivity)

        dialogSocial.setCancelable(true)

        //val activity = context as AppCompatActivity

        val view = layoutInflater.inflate(R.layout.user_social_links_dialog, null)

        dialogSocial.setContentView(view)

        dialogSocial.window!!.attributes.windowAnimations = R.style.DialogAnimation

        if (dialogSocial.window != null) {
            dialogSocial.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        dialogSocial.window!!.setGravity(Gravity.CENTER)
        dialogSocial.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogEdTxtInsta = view.findViewById(R.id.dialogEdTxtInsta)
        dialogEdTxtYou = view.findViewById(R.id.dialogEdTxtYou)
        dialogEdTxtTwit = view.findViewById(R.id.dialogEdTxtTwit)

        dialogEdTxtTwit.setText(binding!!.stats.tvTwitter1.text.toString())
        dialogEdTxtInsta.setText(binding!!.stats.tvInstagram1.text.toString())
        dialogEdTxtYou.setText(binding!!.stats.youTubeTv.text.toString())

        val dialogUpdateBtn = view.findViewById<Button>(R.id.dialogUpdateBtn)

        dialogUpdateBtn.setOnClickListener {

            ///  if (dialogEdTxtInsta.text!!.isNotEmpty() || dialogEdTxtTwit.text!!.isNotEmpty() || dialogEdTxtYou.text!!.isNotEmpty()) {

            dialogSocial.dismiss()
            val myExecutor = Executors.newSingleThreadExecutor()
            val myHandler = Handler(Looper.getMainLooper())
            val profile = sharedPreferencesUtil.fetchUserProfile()

            showProgressView(progressBar)
            imgFile.clear()

            myExecutor.execute {

                imgFile.clear()

                for (i in profile.images.indices) {
                    var mImage: Bitmap?
                    val img = profile.images.get(i).imagePath
                    if (!img.equals("") || !img.isEmpty()) {
                        mImage = mLoad(img)
                    } else {
                        mImage = null
                    }
                    myHandler.post {
                        //mImageView.setImageBitmap(mImage)
                        if (mImage != null) {
                            imgFile.add(mSaveMediaToStorage(mImage)!!)
                            Log.d("imgFile", imgFile.get(i).absolutePath)

                        }
                    }
                }
                myHandler.post {

                    updateUserSocialLinks()
                    dialogSocial.dismiss()
                }
            }/* } else {
                 Toast.makeText(this, "Pleas enter your social link", Toast.LENGTH_SHORT).show()
                 dialogSocial.dismiss()
             }*/
        }

        dialogSocial.show()
    }


    private fun fetchPrivateAlbum() {
        if (null == mPresenterP) mPresenterP = PrivateAlbumPresenter(presenterP)

        run {
            mPresenterP!!.callRetrofit(ConstantsApi.PRIVATE_ALBUM)
        }
    }

    fun formatCount(count: Int): String {
        return when {
            count >= 1_000_000 -> String.format("%.1fm", count / 1_000_000.0)
            count >= 1_000 -> String.format("%.1fk", count / 1_000.0)
            else -> count.toString()
        }
    }

    private fun showUserImgUploadDialog() {

        dialogImg = Dialog(this@ProfileActivity)

        dialogImg.setCancelable(true)

        val view = layoutInflater.inflate(R.layout.user_image_upload_dialog, null)
        dialogImg.setContentView(view)

        dialogImg.window!!.attributes.windowAnimations = R.style.DialogAnimation

        if (dialogImg.window != null) {
            dialogImg.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        dialogImg.window!!.setGravity(Gravity.CENTER)
        dialogImg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val loadingDialog = view.findViewById<LinearLayout>(R.id.loading_dialog)
        val loadingLogo = view.findViewById<ImageView>(R.id.loadingLogo)
        userImg1 = view.findViewById<ImageView>(R.id.userImg1)
        userImg2 = view.findViewById<ImageView>(R.id.userImg2)
        userImg3 = view.findViewById<ImageView>(R.id.userImg3)
        userImg4 = view.findViewById<ImageView>(R.id.userImg4)
        userImg5 = view.findViewById<ImageView>(R.id.userImg5)
        userCross1 = view.findViewById<ImageView>(R.id.ivCross1)
        userCross2 = view.findViewById<ImageView>(R.id.ivCross2)
        userCross3 = view.findViewById<ImageView>(R.id.ivCross3)
        userCross4 = view.findViewById<ImageView>(R.id.ivCross4)
        userCross5 = view.findViewById<ImageView>(R.id.ivCross5)

        val myExecutor = Executors.newSingleThreadExecutor()
        val myHandler = Handler(Looper.getMainLooper())
        val profile = sharedPreferencesUtil.fetchUserProfile()
        //showProgressView(rl_progress)

        val animation = android.view.animation.AnimationUtils.loadAnimation(
            applicationContext, R.anim.blink_anim
        )

        loadingLogo.startAnimation(animation)
        dialogImg.show()

        imgFile.clear()
        Log.d("imgSize", profile.images.size.toString())

        // below code coment 19-dec
        /*  myExecutor.execute {

              imgFile.clear()

              Thread(Runnable {

                  kotlin.run {

                      for (i in profile.images.indices) {
                          var mImage :Bitmap?
                          val img = profile.images.get(i).imagePath
                          if(!img.equals("") || !img.isEmpty()){
                              mImage = mLoad(img)
                          }else{
                              mImage = null
                          }

                          //mImageView.setImageBitmap(mImage)
                          if (mImage != null) {
                              imgFile.add(mSaveMediaToStorage(mImage)!!)

                              Log.d("imgFile", imgFile.get(i).absolutePath)
                              Log.d("imgSize", imgFile.size.toString())
                          }

                      }

                      Log.d("imgSizeT1", profile.images.size.toString())

                      Thread(Runnable {

                          kotlin.run {

                              Log.d("imgSize", imgFile.size.toString())

                              //myHandler.post {

                              when (imgFile.size) {

                                  1 -> {

                                      requestCreateUser.image1 = imgFile[0]
                                      requestCreateUser.imagePath1 = imgFile[0].absolutePath
                                      requestCreateUser.id1 = 0
                                  }

                                  2 -> {

                                      requestCreateUser.image1 = imgFile[0]
                                      requestCreateUser.imagePath1 = imgFile[0].absolutePath
                                      requestCreateUser.id1 = 0

                                      requestCreateUser.image2 = imgFile[1]
                                      requestCreateUser.imagePath2 = imgFile[1].absolutePath
                                      requestCreateUser.id2 = 0
                                  }

                                  3 -> {

                                      requestCreateUser.image1 = imgFile[0]
                                      requestCreateUser.imagePath1 = imgFile[0].absolutePath
                                      requestCreateUser.id1 = 0

                                      requestCreateUser.image2 = imgFile[1]
                                      requestCreateUser.imagePath2 = imgFile[1].absolutePath
                                      requestCreateUser.id2 = 0

                                      requestCreateUser.image3 = imgFile[2]
                                      requestCreateUser.imagePath3 = imgFile[2].absolutePath
                                      requestCreateUser.id3 = 0
                                  }

                                  4 -> {

                                      requestCreateUser.image1 = imgFile[0]
                                      requestCreateUser.imagePath1 = imgFile[0].absolutePath
                                      requestCreateUser.id1 = 0

                                      requestCreateUser.image2 = imgFile[1]
                                      requestCreateUser.imagePath2 = imgFile[1].absolutePath
                                      requestCreateUser.id2 = 0

                                      requestCreateUser.image3 = imgFile[2]
                                      requestCreateUser.imagePath3 = imgFile[2].absolutePath
                                      requestCreateUser.id3 = 0

                                      requestCreateUser.image4 = imgFile[3]
                                      requestCreateUser.imagePath4 = imgFile[3].absolutePath
                                      requestCreateUser.id4 = 0
                                  }

                                  5 -> {

                                      requestCreateUser.image1 = imgFile[0]
                                      requestCreateUser.imagePath1 = imgFile[0].absolutePath
                                      requestCreateUser.id1 = 0

                                      requestCreateUser.image2 = imgFile[1]
                                      requestCreateUser.imagePath2 = imgFile[1].absolutePath
                                      requestCreateUser.id2 = 0

                                      requestCreateUser.image3 = imgFile[2]
                                      requestCreateUser.imagePath3 = imgFile[2].absolutePath
                                      requestCreateUser.id3 = 0

                                      requestCreateUser.image4 = imgFile[3]
                                      requestCreateUser.imagePath4 = imgFile[3].absolutePath
                                      requestCreateUser.id4 = 0

                                      requestCreateUser.image5 = imgFile[4]
                                      requestCreateUser.imagePath5 = imgFile[4].absolutePath
                                      requestCreateUser.id5 = 0
                                  }
                              }

                              Log.d("thread finish", "thread finish")
                              // }

                              Log.d("imgSizeT2", profile.images.size.toString())

                              Handler(Looper.getMainLooper()).post {

                                  loadingDialog.visibility = View.GONE
                              }

                          }
                      }).start()
                  }

              }).start()

              myHandler.post {

                  //hideProgressView(rl_progress)
                  dialogImg.show()
                 /// loadingDialog.visibility = View.VISIBLE
              }
          }*/


        userCross1.setOnClickListener {
            dialogImg.dismiss()
            showAlertDelete(0)
        }
        userCross2.setOnClickListener {
            dialogImg.dismiss()
            showAlertDelete(1)
        }
        userCross3.setOnClickListener {
            dialogImg.dismiss()
            showAlertDelete(2)
        }
        userCross4.setOnClickListener {
            dialogImg.dismiss()
            showAlertDelete(3)
        }
        userCross5.setOnClickListener {
            dialogImg.dismiss()
            showAlertDelete(4)
        }

        val uploadImgBtn = view.findViewById<Button>(R.id.dialogUpdateImgBtn)

        val images = Gson().toJson(profile.images)

        val image = Gson().fromJson<ArrayList<ResponseUserProfileImages>>(
            images, object : TypeToken<ArrayList<ResponseUserProfileImages>>() {}.type
        )

        val imgPathArr = ArrayList<String>()

        for (i in image.indices) {

            imgPathArr.add(image[i].imagePath)

            Log.d("image", image[i].imagePath)


            //recycleViewModels.add(RecycleModel(image[i].imagePath, profile!!.userId, images))
        }


        Log.d("size", imgPathArr.size.toString())

        when (imgPathArr.size) {

            0 -> {
                userCross1.visibility = View.GONE
                userCross2.visibility = View.GONE
                userCross3.visibility = View.GONE
                userCross4.visibility = View.GONE
                userCross5.visibility = View.GONE

            }

            1 -> {

                userCross1.visibility = View.GONE
                userCross2.visibility = View.GONE
                userCross3.visibility = View.GONE
                userCross4.visibility = View.GONE
                userCross5.visibility = View.GONE

                Glide.with(this).load(imgPathArr[0])
                    .placeholder(R.drawable.meetupsfellow_transpatent).dontAnimate().into(userImg1)

            }

            2 -> {

                userCross1.visibility = View.VISIBLE
                userCross2.visibility = View.VISIBLE
                userCross3.visibility = View.GONE
                userCross4.visibility = View.GONE
                userCross5.visibility = View.GONE

                Glide.with(this).load(imgPathArr[0])
                    .placeholder(R.drawable.meetupsfellow_transpatent).dontAnimate().into(userImg1)

                Glide.with(this).load(imgPathArr[1])
                    .placeholder(R.drawable.meetupsfellow_transpatent).dontAnimate().into(userImg2)
            }

            3 -> {

                userCross1.visibility = View.VISIBLE
                userCross2.visibility = View.VISIBLE
                userCross3.visibility = View.VISIBLE
                userCross4.visibility = View.GONE
                userCross5.visibility = View.GONE

                Glide.with(this).load(imgPathArr[0])
                    .placeholder(R.drawable.meetupsfellow_transpatent).dontAnimate().into(userImg1)

                Glide.with(this).load(imgPathArr[1])
                    .placeholder(R.drawable.meetupsfellow_transpatent).dontAnimate().into(userImg2)

                Glide.with(this).load(imgPathArr[2])
                    .placeholder(R.drawable.meetupsfellow_transpatent).dontAnimate().into(userImg3)
            }

            4 -> {

                userCross1.visibility = View.VISIBLE
                userCross2.visibility = View.VISIBLE
                userCross3.visibility = View.VISIBLE
                userCross4.visibility = View.VISIBLE
                userCross5.visibility = View.GONE

                Glide.with(this).load(imgPathArr[0])
                    .placeholder(R.drawable.meetupsfellow_transpatent).dontAnimate().into(userImg1)

                Glide.with(this).load(imgPathArr[1])
                    .placeholder(R.drawable.meetupsfellow_transpatent).dontAnimate().into(userImg2)

                Glide.with(this).load(imgPathArr[2])
                    .placeholder(R.drawable.meetupsfellow_transpatent).dontAnimate().into(userImg3)

                Glide.with(this).load(imgPathArr[3])
                    .placeholder(R.drawable.meetupsfellow_transpatent).dontAnimate().into(userImg4)
            }

            5 -> {

                userCross1.visibility = View.VISIBLE
                userCross2.visibility = View.VISIBLE
                userCross3.visibility = View.VISIBLE
                userCross4.visibility = View.VISIBLE
                userCross5.visibility = View.VISIBLE

                Glide.with(this).load(imgPathArr[0])
                    .placeholder(R.drawable.meetupsfellow_transpatent).dontAnimate().into(userImg1)

                Glide.with(this).load(imgPathArr[1])
                    .placeholder(R.drawable.meetupsfellow_transpatent).dontAnimate().into(userImg2)

                Glide.with(this).load(imgPathArr[2])
                    .placeholder(R.drawable.meetupsfellow_transpatent).dontAnimate().into(userImg3)

                Glide.with(this).load(imgPathArr[3])
                    .placeholder(R.drawable.meetupsfellow_transpatent).dontAnimate().into(userImg4)

                Glide.with(this).load(imgPathArr[4])
                    .placeholder(R.drawable.meetupsfellow_transpatent).dontAnimate().into(userImg5)
            }
        }

        userImg1.setOnClickListener {

            selectedImg = 1
            EasyImage.openGallery(this@ProfileActivity, 0)

        }

        userImg2.setOnClickListener {

            selectedImg = 2
            EasyImage.openGallery(this@ProfileActivity, 0)

        }

        userImg3.setOnClickListener {

            selectedImg = 3
            EasyImage.openGallery(this@ProfileActivity, 0)

        }

        userImg4.setOnClickListener {

            selectedImg = 4
            EasyImage.openGallery(this@ProfileActivity, 0)

        }

        userImg5.setOnClickListener {

            selectedImg = 5
            EasyImage.openGallery(this@ProfileActivity, 0)

        }

        uploadImgBtn.setOnClickListener {

            dialogImg.dismiss()
            uploadUserImgs("upload")
            showProgressView(progressBar)
        }

        //dialogImg.show()
    }

    private fun showAlertDelete(position: Int) {

        val myExecutor = Executors.newSingleThreadExecutor()
        val myHandler = Handler(Looper.getMainLooper())
        val profile = sharedPreferencesUtil.fetchUserProfile()

        showProgressView(progressBar)

        Log.d("imgDelete", imgFile.size.toString())

        imgFile.clear()

        Log.d("imgDeleteC", imgFile.size.toString())

        myExecutor.execute {

            imgFile.clear()

            for (i in profile.images.indices) {

                var mImage: Bitmap?
                val img = profile.images.get(i).imagePath
                if (!img.equals("") || !img.isEmpty()) {
                    mImage = mLoad(img)
                } else {
                    mImage = null
                }
                myHandler.post {

                    //mImageView.setImageBitmap(mImage)
                    if (mImage != null) {
                        imgFile.add(mSaveMediaToStorage(mImage)!!)

                        Log.d("imgFile", imgFile.get(i).absolutePath)

                    }
                }
            }

            Log.e("delete$#", "Positn: " + position)

            myHandler.post {

                images.clear()

                when (profile.images.isNotEmpty()) {
                    true -> {
                        profile.images.asSequence().forEach {
                            val index = profile.images.indexOf(it)
                            // when (indexExists(cross, index)) {
                            //  true -> {
                            // cross[index].visibility = View.VISIBLE
                            //   showImage(profileImage[index], it.imagePath)
                            this.images.add(ProfileImageModel().apply {
                                imageId = it.id
                                imagePath = it.imagePath
                                if (imgFile != null && imgFile.size != 0) {
                                    try {
                                        this.image = imgFile.get(index)
                                    } catch (e: IndexOutOfBoundsException) {
                                        //Add something to fill this position.
                                        Log.e("images%$", "its Catch Error")
                                    }

                                } else Log.e("empty imgFile", "1213")
                            })
                            //  }
                            // }
                        }
                    }

                    false -> {}
                }
                Log.e("Delete^& Dilog:", images.size.toString())
                Log.e("Delete^& Dilog:", profile.images.size.toString())
                val alertDialog = androidx.appcompat.app.AlertDialog.Builder(
                    this@ProfileActivity, R.style.MyDialogTheme2
                )
                alertDialog.setMessage(getText(R.string.alert_delete))
                alertDialog.setPositiveButton(getString(R.string.text_delete)) { dialog, _ ->
                    dialog.dismiss()
                    when (indexExists(images, position)) {
                        true -> {
                            Log.e("Delete^& ", "Remove_Pos: " + position)
                            dialogImg.dismiss()
                            images.removeAt(position)
                        }

                        false -> {}
                    }
                    when (indexExists(profile.images, position)) {
                        true -> {
                            Log.e("Delete^&2 ", "Remove_Pos: " + position)
                            profile.images.removeAt(position)
                        }

                        false -> {}
                    }
                    Log.e("Delete^& Dilog22:", images.size.toString())
                    Log.e("Delete^& Dilog22:", profile.images.size.toString())
                    setCrossImages(position)
                }
                alertDialog.setNegativeButton(getString(R.string.label_cancel_text)) { dialog, _ ->
                    dialog.cancel()
                }
                alertDialog.setCancelable(false)
                // Showing Alert Message
                alertDialog.show()
                hideProgressView(progressBar)
                Log.d("thread", "thread com")
            }
        }

    }

    private fun setCrossImages(position: Int) {
        Log.e("Delete##$", "Pos: " + position)
        when (position) {

            0 -> {

                userCross1.visibility = View.GONE
                userImg1.setImageResource(R.drawable.ic_baseline_add_24)
            }

            1 -> {

                userCross2.visibility = View.GONE
                userImg2.setImageResource(R.drawable.ic_baseline_add_24)
            }

            2 -> {

                userCross3.visibility = View.GONE
                userImg3.setImageResource(R.drawable.ic_baseline_add_24)
            }

            3 -> {

                userCross4.visibility = View.GONE
                userImg4.setImageResource(R.drawable.ic_baseline_add_24)
            }

            4 -> {

                userCross5.visibility = View.GONE
                userImg5.setImageResource(R.drawable.ic_baseline_add_24)
            }
        }



        LogManager.logger.i(ArchitectureApp.instance!!.tag, "Images is : ${Gson().toJson(images)}")

        uploadUserImgs("delete")

        /*if (images.isNotEmpty()) {
            images.asSequence().forEach {
                if (!URLUtil.isValidUrl(it.imagePath)) {
                    val index = images.indexOf(it)
                    cross[index].visibility = View.VISIBLE
                    profileImage[index].setImageURI(Uri.parse(it.imagePath))
                } else {
                    val index = images.indexOf(it)
                    cross[index].visibility = View.VISIBLE
                    showImage(profileImage[index], "${it.imagePath}")
                }
            }
        }*/
    }

    private fun uploadUserImgs(action: String) {

        showProgressView(progressBar)

        val currentLocation = sharedPreferencesUtil.fetchLocation()

        Log.e("Delete^&%% ", images.size.toString())

        if (action == "delete") {

            Log.d("imagesSizeIn", images.size.toString())

            requestCreateUser = convertResponseToRequest(
                profile!!, RequestCreateUser::class.java
            ) as RequestCreateUser
            LogManager.logger.i(
                ArchitectureApp.instance!!.tag, "Profile_is1 : ${Gson().toJson(images)}"
            )
            images.asSequence().forEach {
                when (images.indexOf(it)) {
                    0 -> {
                        if (null != it.image) {
                            requestCreateUser.apply {
                                image1 = it.image!!
                                id1 = 0
                            }
                        } else {
                            requestCreateUser.apply {
                                /*id1 = profile!!.images[0].id
                                imagePath1 = profile!!.images[0].imagePath*/
                                id1 = images[0].imageId
                                imagePath1 = images[0].imagePath.toString()
                            }
                        }
                    }

                    1 -> {
                        if (null != it.image) {
                            requestCreateUser.apply {
                                image2 = it.image!!
                                id2 = 0
                            }
                        } else {
                            requestCreateUser.apply {
                                /* id2 = profile!!.images[1].id
                                 imagePath2 = profile!!.images[1].imagePath*/

                                id2 = images[1].imageId
                                imagePath2 = images[1].imagePath.toString()
                            }
                        }
                    }

                    2 -> {

                        if (null != it.image) {
                            requestCreateUser.apply {
                                image3 = it.image!!
                                id3 = 0
                            }
                        } else {
                            requestCreateUser.apply {
                                /* id3 = profile!!.images[2].id
                                 imagePath3 = profile!!.images[2].imagePath*/
                                id3 = images[2].imageId
                                imagePath3 = images[2].imagePath.toString()

                            }
                        }
                    }

                    3 -> {

                        if (null != it.image) {
                            requestCreateUser.apply {
                                image4 = it.image!!
                                id4 = 0
                            }
                        } else {
                            requestCreateUser.apply {
                                /* id4 = profile!!.images[3].id
                                 imagePath4 = profile!!.images[3].imagePath*/
                                id4 = images[3].imageId
                                imagePath4 = images[3].imagePath.toString()

                            }
                        }
                    }

                    4 -> {
                        if (null != it.image) {
                            requestCreateUser.apply {
                                image5 = it.image!!
                                id5 = 0
                            }
                        } else {
                            requestCreateUser.apply {
                                /*id5 = profile!!.images[4].id
                                imagePath5 = profile!!.images[4].imagePath*/
                                id5 = images[4].imageId
                                imagePath5 = images[4].imagePath.toString()
                            }
                        }
                    }
                }
            }
        } else {
            Log.e("images%$", "FileSize-:  ${imgFile.size}")
            images.clear()

            when (profile!!.images.isNotEmpty()) {
                true -> {
                    profile!!.images.asSequence().forEach {
                        val index = profile!!.images.indexOf(it)
                        // when (indexExists(cross, index)) {
                        //  true -> {
                        // cross[index].visibility = View.VISIBLE
                        //   showImage(profileImage[index], it.imagePath)
                        Log.e("images%$", "index-:  ${index}")
                        images.add(ProfileImageModel().apply {
                            imageId = it.id
                            imagePath = it.imagePath

                            if (index == 0 && requestCreateUser.image1 != null) {
                                image = requestCreateUser.image1
                            } else if (index == 1 && requestCreateUser.image2 != null) {
                                image = requestCreateUser.image2
                            } else if (index == 2 && requestCreateUser.image3 != null) {
                                image = requestCreateUser.image3
                            } else if (index == 3 && requestCreateUser.image4 != null) {
                                image = requestCreateUser.image4
                            } else if (index == 4 && requestCreateUser.image5 != null) {
                                image = requestCreateUser.image5
                            }

                            /*  if (imgFile != null && imgFile.size != 0){
                                  try {
                                      image = imgFile.get(index)
                                  } catch (e: IndexOutOfBoundsException) {
                                      //Add something to fill this position.
                                      Log.e("images%$","its Catch Error")
                                  }

                              }else
                                  Log.e("empty imgFile","1213");*/

                        })
                        //  }
                        // }
                    }

                }

                false -> {}
            }

            if (requestCreateUser.image1 != null && images.size < 1) {
                images.add(ProfileImageModel().apply {
                    imageId = 0
                    imagePath = null
                    image = requestCreateUser.image1
                })
            }
            if (requestCreateUser.image2 != null && images.size < 2) {
                images.add(ProfileImageModel().apply {
                    imageId = 0
                    imagePath = null
                    image = requestCreateUser.image2
                })
            }
            if (requestCreateUser.image3 != null && images.size < 3) {
                images.add(ProfileImageModel().apply {
                    imageId = 0
                    imagePath = null
                    image = requestCreateUser.image3
                })
            }

            if (requestCreateUser.image4 != null && images.size < 4) {
                images.add(ProfileImageModel().apply {
                    imageId = 0
                    imagePath = null
                    image = requestCreateUser.image4
                })
            }
            if (requestCreateUser.image5 != null && images.size < 5) {
                images.add(ProfileImageModel().apply {
                    imageId = 0
                    imagePath = null
                    image = requestCreateUser.image5
                })
            }

            Log.e("images%$", "Size-:  ${images.size}")

            requestCreateUser = convertResponseToRequest(
                profile!!, RequestCreateUser::class.java
            ) as RequestCreateUser
            LogManager.logger.i(
                ArchitectureApp.instance!!.tag, "Images_is : ${Gson().toJson(images)}"
            )
            images.asSequence().forEach {
                when (images.indexOf(it)) {
                    0 -> {
                        Log.e("images%$", "00-:  ${it.image}")
                        if (null != it.image) {
                            requestCreateUser.apply {
                                image1 = it.image!!
                                id1 = 0
                            }
                        } else {
                            requestCreateUser.apply {
                                id1 = profile!!.images[0].id
                                imagePath1 = profile!!.images[0].imagePath
                            }
                        }
                    }

                    1 -> {
                        Log.e("images%$", "11-:  ${it.image}")
                        if (null != it.image) {
                            requestCreateUser.apply {
                                image2 = it.image!!
                                id2 = 0
                            }
                        } else {
                            requestCreateUser.apply {
                                id2 = profile!!.images[1].id
                                imagePath2 = profile!!.images[1].imagePath
                            }
                        }
                    }

                    2 -> {
                        Log.e("images%$", "22-:  ${it.image}")
                        if (null != it.image) {
                            requestCreateUser.apply {
                                image3 = it.image!!
                                id3 = 0
                            }
                        } else {
                            requestCreateUser.apply {
                                id3 = profile!!.images[2].id
                                imagePath3 = profile!!.images[2].imagePath
                            }
                        }
                    }

                    3 -> {

                        if (null != it.image) {
                            requestCreateUser.apply {
                                image4 = it.image!!
                                id4 = 0
                            }
                        } else {
                            requestCreateUser.apply {
                                id4 = profile!!.images[3].id
                                imagePath4 = profile!!.images[3].imagePath
                            }
                        }
                    }

                    4 -> {
                        if (null != it.image) {
                            requestCreateUser.apply {
                                image5 = it.image!!
                                id5 = 0
                            }
                        } else {
                            requestCreateUser.apply {
                                id5 = profile!!.images[4].id
                                imagePath5 = profile!!.images[4].imagePath
                            }
                        }
                    }
                }
            }
        }
        Log.e("Delete^&22 ", images.size.toString())
        requestCreateUser.name = profile!!.name

        requestCreateUser.gender = profile!!.gender

        requestCreateUser.age = profile!!.age

        requestCreateUser.email = profile!!.email

        requestCreateUser.homeLocation = profile!!.homeLocation
        requestCreateUser.instagramLink = profile!!.instagramLink
        requestCreateUser.twitterLink = profile!!.twitterLink
        requestCreateUser.xtubeLink = profile!!.xtubeLink

        Log.d("userLat", "${currentLocation.latitude}")
        Log.d("userLong", "${currentLocation.longitude}")

        requestCreateUser.homeLat = "${currentLocation.latitude}"
        requestCreateUser.homeLong = "${currentLocation.longitude}"
        requestCreateUser.currentLat = "${currentLocation.latitude}"
        requestCreateUser.currentLong = "${currentLocation.longitude}"

        requestCreateUser.aboutMe = profile!!.aboutMe
        Log.e("Reqest%$", "image11-:  ${requestCreateUser.image1}")
        Log.e("Reqest%$", "image22-:  ${requestCreateUser.image2}")
        Log.e("Reqest%$", "image33-:  ${requestCreateUser.image3}")
        Log.e("Reqest%$", "image44-:  ${requestCreateUser.image4}")

        LogManager.logger.e(
            ArchitectureApp.instance!!.tag,
            "Profile_Request_is : ${Gson().toJson(requestCreateUser)}"
        )
        Handler().postDelayed({
            createProfile()
        }, 200)
    }

    private fun setSelected(view: View) {
        binding!!.stats.llMain.visibility = View.GONE
        view.visibility = View.VISIBLE
    }

    private fun mackProfilePrivet(isHide: Int) {
        reqPrivetUserProfile.isprofilehidden = isHide

        if (null == mPresenterP) mPresenterP = PrivateAlbumPresenter(presenterP)

        run {
            mPresenterP!!.addObjectForPrivetProfile(reqPrivetUserProfile)
            mPresenterP!!.callRetrofit(ConstantsApi.MACK_PROFILE_PRIVET)
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun showAlertPrivetDialog() {

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
        val dialogImg = view.findViewById<CircleImageView>(R.id.dialog_img)
        val hideProfile = view.findViewById<AppCompatCheckBox>(R.id.checkBox_hide_profile)
        val dialogContent = view.findViewById<TextView>(R.id.dialog_content)
        val cancelBtn = view.findViewById<Button>(R.id.noExitBtn)
        val confirm = view.findViewById<Button>(R.id.yesExitBtn)
        Log.e("Profile", "Privet_dialog")
        dialogHead.text = "MeetupsFellow"
        dialogHead.visibility = View.GONE
        val isHide =
            profile!!.currentPlanInfo!!.isProfilePrivateHidden == 2 || profile!!.currentPlanInfo!!.isProfilePrivateHidden == 3
        dialogImg.visibility = View.GONE

        if (profile!!.isprofileprivate == 0) {
            hideProfile.visibility = View.VISIBLE
            when (profile!!.currentPlanInfo!!.planTitle) {
                getString(R.string.membership_standard) -> {
                    dialogContent.text = getString(R.string.want_to_privet_profile)
                }

                getString(R.string.membership_premium) -> {
                    // dialogContent.text = getString(R.string.want_to_privet_profile_premium)
                    dialogContent.text = getString(R.string.want_to_privet_profile)
                }

                getString(R.string.membership_featured) -> {
                    // dialogContent.text = getString(R.string.want_to_privet_profile_featured)
                    dialogContent.text = getString(R.string.want_to_privet_profile)
                }

                else -> dialogContent.text = getString(R.string.want_to_privet_profile)
            }
        } else {
            hideProfile.visibility = View.GONE
            if (profile!!.isprofilehidden == 1) {
                hideProfile.visibility = View.VISIBLE
                hideProfile.isChecked = true
                hideProfile.isClickable = false
                hideProfile.text = getString(R.string.unhide_profile_checkbox_text)
            }
            dialogContent.text = getString(R.string.want_to_public_profile)
        }

        hideProfile.setOnClickListener {
            if (isHide) {
                if (profile!!.isprofilehidden == 1) {
                    hideProfile.isChecked = true
                }
                Log.e("Profile", "Privet_dialog_IsHide")
            } else {
                hideProfile.isChecked = false
                universalToast("Upgrade your plan")
            }
        }

        cancelBtn.setOnClickListener {
            dialog.cancel()
        }

        confirm.setOnClickListener {
            if (profile!!.currentPlanInfo!!.isProfilePrivateHidden != 0) {
                if (hideProfile.isChecked) {
                    mackProfilePrivet(1)
                } else {
                    mackProfilePrivet(0)
                }
            } else universalToast("Please upgrade your plan")
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun showPrivateDialogForFree() {

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
        Log.e("Profile", "Privet_Alert_dialog_Box")
        dialogHead.text = "Profile Private"
        dialogHead.visibility = View.VISIBLE

        if (sharedPreferencesUtil.fetchUserProfile().currentPlanInfo!!.planId == 2) {
            dialogContent.text = getString(R.string.upgrade_plan_for_private)
            cancelBtn.text = "Okay"
            confirm.text = "Upgrade now"
            confirm.visibility = View.GONE
        } else {
            dialogContent.text = getString(R.string.make_privet_hint)
            cancelBtn.text = "Not now"
            confirm.text = "Subscribe now"
        }

        cancelBtn.setOnClickListener {
            dialog.cancel()
        }

        confirm.setOnClickListener {
            AlertBuyPremium.Builder(this@ProfileActivity, Constants.DialogTheme.NoTitleBar).build()
                .show()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setProfileDetails() {

        showProgressView(progressBar)

        name = profile!!.name

        /*adapter = CustomViewAdapter(supportFragmentManager)

        adapter.addFragment(UserPictures(), "Pictures")
        adapter.addFragment(UserVideos(), "Videos")
        adapter.addFragment(UserBio(), name+" Bio")
        adapter.addFragment(UserMore(), "More")

        viewPager.adapter = adapter

        tvUserTabLay.setupWithViewPager(viewPager)

        tvUserTabLay.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })*/
        Log.e("Your_Profile", "Pro: ${profile!!.isProMembership}")
        Log.e("Your_Profile", "USer_name: ${profile!!.name}")
        Log.e("Your_Profile", "USer_email: ${profile!!.email}")
        Log.e("Your_Profile", "USer_Privet: ${profile!!.isprofileprivate}")

        binding!!.btnProfilePrivet.visibility = View.VISIBLE
        Log.e("Your_Profile", "followers_count: ${profile!!.followers_count}")
        Log.e("Your_Profile", "likedUserCount: ${profile!!.likedUserCount}")
        Log.e(
            "Your_Profile",
            "available_connection_counts: ${profile!!.available_connection_counts}"
        )
        if (profile!!.isprofileprivate == 0) {
            binding!!.btnTxtPrivet.text = getString(R.string.make_privet)
        } else {
            binding!!.btnTxtPrivet.text = getString(R.string.make_public)
        }

        binding!!.includedUserImg.followerCount.text = profile!!.followers_count.toString()
        binding!!.includedUserImg.likeCount.text = profile!!.likedUserCount.toString()

        if (profile!!.likedUserCount > 999) {
            binding!!.includedUserImg.likeCount.text = formatCount(profile!!.likedUserCount)
        }
        if (profile!!.followers_count > 999) {
            binding!!.includedUserImg.followerCount.text = formatCount(profile!!.followers_count)
        }

        binding!!.btnProfilePrivet.setOnClickListener {
            if (profile!!.isProMembership && profile!!.currentPlanInfo!!.isProfilePrivateHidden != 0) {
                showAlertPrivetDialog()
            } else {
                showPrivateDialogForFree()
                /// Toast.makeText(this,"Upgrade to pro",Toast.LENGTH_SHORT).show()
            }
        }

        recycleViewModels.clear()

        val images = Gson().toJson(profile!!.images)

        val image = Gson().fromJson<ArrayList<ResponseUserProfileImages>>(
            images, object : TypeToken<ArrayList<ResponseUserProfileImages>>() {}.type
        )

        sliderDataArrayList.clear()
        imgPathArr.clear()

        for (i in image.indices) {

            imgPathArr.add(image[i].imagePath)
            sliderDataArrayList.add(SliderData(image[i].imagePath))
            Log.d("image", image[i].imagePath)
        }

        if (imgPathArr.isNotEmpty() && imgPathArr.size > 0){
            // Set adapter
            val adapter = ImageSliderAdapter(this@ProfileActivity,imgPathArr, profile!!.userId, profile!!.images)
            binding!!.includedUserImg.userImgPager.adapter = adapter

            // Attach dots to ViewPager2
            TabLayoutMediator(binding!!.includedUserImg.tabLayoutIndecatore, binding!!.includedUserImg.userImgPager) { tab, position ->
                // Dots are automatically handled, nothing needed here
                tab.view.setBackgroundResource(R.drawable.tab_indicator_selector)
                tab.view.post {
                    val layoutParams = tab.view.layoutParams
                    if (layoutParams is ViewGroup.MarginLayoutParams) {
                        layoutParams.setMargins(4, 0, 4, 0)  // Increase space between dots
                        tab.view.layoutParams = layoutParams
                    }
                }
            }.attach()
            if (imgPathArr.size > 1){
                // Auto-scroll images every 3 seconds
                autoScrollImages()
            }

            binding!!.includedUserImg.userImgPager.visibility = View.VISIBLE

        }else{
            if (image.isEmpty()) {
                binding!!.includedUserImg.ivUserProfile1.setImageResource(R.drawable.meetupsfellow_transpatent)
            }else{
                Glide.with(this)
                    .load(image[0].imagePath)
                    .placeholder(R.drawable.meetupsfellow_transpatent)
                    .into( binding!!.includedUserImg.ivUserProfile1)
                binding!!.includedUserImg.ivUserProfile1.visibility = View.VISIBLE
            }
            binding!!.includedUserImg.userImgPager.visibility = View.GONE
            binding!!.includedUserImg.ivUserProfile1.visibility = View.VISIBLE
        }

        Log.d("privateAlbumAdapter", privateAlbumAdapter.itemCount.toString())

        LogManager.logger.i(
            ArchitectureApp.instance!!.tag, "Profile is2 : ${Gson().toJson(profile)}"
        )

        fetchSelfProfile()

        binding!!.stats.tvUserName1.text =
            String.format(getString(R.string.text_height_weight), profile!!.name, profile!!.age)

        val paint = binding!!.stats.tvUserName1.paint
        val width = paint.measureText(binding!!.stats.tvUserName1.text.toString())
        val textShader: Shader = LinearGradient(
            0f, 0f, width, binding!!.stats.tvUserName1.textSize, intArrayOf(
                Color.parseColor("#F4447E"), Color.parseColor("#8448F4")/*Color.parseColor("#64B678"),
                Color.parseColor("#478AEA"),*/
                //Color.parseColor("#8446CC")
            ), null, Shader.TileMode.REPEAT
        )

        binding!!.stats.tvUserName1.paint.shader = textShader


        Log.d("name", profile!!.name)

        if (profile!!.homeLocation.isEmpty()) {

            binding!!.stats.tvHomeTown1.text = "Not available"
        } else {

            binding!!.stats.tvHomeTown1.text = profile!!.homeLocation
        }
//        tvUserLocation.visibility = View.VISIBLE
//        binding!!.stats.view1.visibility = View.VISIBLE

        binding!!.stats.tvUserBio.text = profile!!.aboutMe

        if (profile!!.twitterLink.isEmpty()) {
            binding!!.stats.icTitter.setImageDrawable(resources.getDrawable(R.drawable.twitter))
            /// binding!!.stats.icTitter.backgroundTintList = resources.getColorStateList(R.color.grey)
        } else {

            binding!!.stats.icTitter.setImageDrawable(resources.getDrawable(R.drawable.twitter_select))
        }

        Log.d("SocialLinkUpdated", profile!!.instagramLink)
        Log.e("SocialLinkUpdated", profile!!.xtubeLink)

        if (profile!!.instagramLink.isEmpty()) {
            binding!!.stats.icInsta.setImageDrawable(resources.getDrawable(R.drawable.instagram))
            ///  binding!!.stats.icInsta.backgroundTintList = resources.getColorStateList(R.color.grey)
        } else {

            //binding!!.stats.icInsta.setImageDrawable(resources.getDrawable(R.drawable.instagram_svgrepo_com))
            binding!!.stats.icInsta.setImageDrawable(resources.getDrawable(R.drawable.selected_instagram))
        }

        if (profile!!.xtubeLink.isEmpty()) {
            binding!!.stats.icYoutube.setImageDrawable(resources.getDrawable(R.drawable.youtube))
            ///  binding!!.stats.icYoutube.backgroundTintList = resources.getColorStateList(R.color.grey)
        } else {

            binding!!.stats.icYoutube.setImageDrawable(resources.getDrawable(R.drawable.youtube_select))
        }


        Log.d("isPremium", profile!!.isProMembership.toString())



        if (profile!!.status == 1) {
            binding!!.stats.imgStatus1.setImageResource(R.drawable.drawable_user_online)
            //Green Dot
            binding!!.stats.textOnlineAgo1.text = "Online"
        } else {
            binding!!.stats.imgStatus1.setImageResource(R.drawable.drawable_user_offline)
            //Gray Dot
            binding!!.stats.textOnlineAgo1.text = "Offline"
        }

       /// binding!!.stats.llOnOfLine.visibility = View.GONE

        if (profile!!.twitterLink.isNotEmpty()) {
            val twitter = profile!!.twitterLink.split("/")
            if (twitter.isNotEmpty()) {
                binding!!.stats.tvTwitter1.text = twitter[twitter.size - 1]
            }
        } else {
            binding!!.stats.tvTwitter1.text = ""
        }

        if (profile!!.instagramLink.isNotEmpty()) {
            val instag = profile!!.instagramLink.split("/")
            if (instag.isNotEmpty()) {
                binding!!.stats.tvInstagram1.text = instag[instag.size - 1]
            }
        } else {
            binding!!.stats.tvInstagram1.text = ""
        }
        if (profile!!.xtubeLink.isNotEmpty()) {
            val xtube = profile!!.xtubeLink.split("/")
            if (xtube.isNotEmpty()) {
                binding!!.stats.youTubeTv.text = xtube[xtube.size - 1]
            }
        } else {
            binding!!.stats.youTubeTv.text = ""
        }

        if (profile!!.email.length >= 25) {

            // val tempEmail = profile!!.email.substring(0, 25) + "..."
            binding!!.stats.tvEmail1.text = profile!!.email

        } else {

            binding!!.stats.tvEmail1.text = profile!!.email
        }

        ///  val userPhone = getSharedPreferences("userPhone", MODE_PRIVATE).getString("userPhone", "")
        val userPhone = profile!!.number
        Log.d("UserPhone*:", profile!!.number)
        if (userPhone.isNotEmpty()) {
            Log.d("cc", profile!!.countryCode)

            binding!!.stats.tvMobileNumber1.text = profile!!.countryCode + userPhone
        } else {

            binding!!.stats.tvMobileNumber1.text = profile!!.countryCode + profile!!.number
        }

        binding!!.stats.memberSinceTxt.text = "Member Since " + profile!!.membersince
        binding!!.stats.llMemberSince.visibility = View.GONE

        if (binding!!.stats.tvEmail1.text.toString().isEmpty()) {
            binding!!.stats.ivEmail.visibility = View.GONE
            binding!!.stats.tvEmail1.visibility = View.GONE
        } else if (binding!!.stats.tvMobileNumber1.text.toString().isEmpty()) {
            binding!!.stats.ivPhone.visibility = View.GONE
            binding!!.stats.ivPhone.visibility = View.GONE
            binding!!.stats.tvMobileNumber1.visibility = View.GONE
        } else {
            binding!!.stats.ivPhone.visibility = View.VISIBLE
            binding!!.stats.ivEmail.visibility = View.VISIBLE
            binding!!.stats.tvMobileNumber1.visibility = View.VISIBLE
            binding!!.stats.tvEmail1.visibility = View.VISIBLE
        }

        //tvPartnerStatus.text = profile!!.partnerStatus
        binding!!.stats.rlLocation1.visibility = View.GONE
        binding!!.stats.tvUserAway1.visibility = View.GONE
        binding!!.stats.ivChat1Lay.visibility = View.GONE
        binding!!.stats.llThreeDote.visibility = View.GONE

        if (profile!!.isProMembership) {
            binding!!.stats.becomeProTxt.visibility = View.GONE
            //  proTxt.text = "Premium"
        } else {
            binding!!.stats.becomeProTxt.visibility = View.VISIBLE
            // binding!!.includedUserImg.proImg.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
            // proTxt.text = "Free"
        }
        when (profile?.currentPlanInfo!!.planId) {
            2 -> {
                binding!!.includedUserImg.proTxt.text = profile!!.currentPlanInfo!!.planTitle
                binding!!.includedUserImg.proImg.setImageResource(R.drawable.special_badge)
            }

            3 -> {
                binding!!.includedUserImg.proTxt.text = profile!!.currentPlanInfo!!.planTitle
                binding!!.includedUserImg.proImg.setImageResource(R.drawable.standers_badge)
            }

            4 -> {
                binding!!.includedUserImg.proTxt.text = profile!!.currentPlanInfo!!.planTitle
                binding!!.includedUserImg.proImg.setImageResource(R.drawable.ic_baseline_workspace_premium_24)
                //  binding!!.includedUserImg.proImg.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            }

            5 -> {
                binding!!.includedUserImg.proTxt.text = profile!!.currentPlanInfo!!.planTitle
                binding!!.includedUserImg.proImg.setImageResource(R.drawable.featured_badge)
            }

            else -> {
                binding!!.includedUserImg.proTxt.text = profile!!.currentPlanInfo!!.planTitle
                binding!!.includedUserImg.proImg.setImageResource(R.drawable.free_user_badge)
            }
        }

        /* if (profile?.currentPlanInfo!!.bedge == 2){
             binding!!.includedUserImg.proTxt.text = profile!!.currentPlanInfo!!.planTitle
             binding!!.includedUserImg.proImg.setImageResource(R.drawable.ic_baseline_workspace_premium_24)
         }else if (profile?.currentPlanInfo!!.bedge == 1){
             binding!!.includedUserImg.proTxt.text = profile!!.currentPlanInfo!!.planTitle
             proImg.setImageResource(R.drawable.ic_baseline_workspace_premium_24)
             proImg.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
         }else{
           //  proImg.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
             proTxt.text = profile!!.currentPlanInfo!!.planTitle
             proImg.setImageResource(R.drawable.free_user_badge)
         }*/

        if (profile!!.fistingId.isEmpty() && profile!!.generalIntercourse.isEmpty() && profile!!.condomPreference.isEmpty() && profile!!.myIdentity.isEmpty()) {
            binding!!.stats.llPrefText.visibility = View.VISIBLE
            binding!!.stats.ivAddPref.visibility = View.VISIBLE
            binding!!.stats.llPreferencesMain.visibility = View.GONE
            binding!!.stats.addPreferencesIc.visibility = View.GONE
        } else {
            binding!!.stats.llPrefText.visibility = View.VISIBLE
            binding!!.stats.llPreferencesMain.visibility = View.VISIBLE
            binding!!.stats.addPreferencesIc.visibility = View.VISIBLE
            binding!!.stats.ivAddPref.visibility = View.GONE

            if (profile!!.fistingId.isNotEmpty()) {
                binding!!.stats.llHandballPref.visibility = View.VISIBLE
                binding!!.stats.handballPrefValu.text = profile!!.fistingId
            } else {
                binding!!.stats.llHandballPref.visibility = View.GONE
            }

            if (profile!!.generalIntercourse.isNotEmpty()) {
                binding!!.stats.llIntercours.visibility = View.VISIBLE
                binding!!.stats.generalInterPrefValu.text = profile!!.generalIntercourse
            } else {
                binding!!.stats.llIntercours.visibility = View.GONE
            }

            if (profile!!.condomPreference.isNotEmpty()) {
                binding!!.stats.llSexPractice.visibility = View.VISIBLE
                binding!!.stats.condomPrefValu.text = profile!!.condomPreference
            } else {
                binding!!.stats.llSexPractice.visibility = View.GONE
            }
            Log.d("myIdentity%: ", profile!!.myIdentity)
            if (profile!!.myIdentity.isNotEmpty() && !profile!!.myIdentity.equals("")) {
                binding!!.stats.tvIdentityHeading.visibility = View.VISIBLE
                binding!!.stats.intoIdentityPrefValu.visibility = View.VISIBLE
                val input = profile!!.myIdentity

                // Step 1: Remove the first comma and everything before it (if necessary)
                val modifiedInput = input.substringAfter(", ")

                // Step 2: Split the modified string into parts
                val parts = modifiedInput.split(", ")

                // Step 3: Prepend `#` to each item
                val modifiedParts = parts.map { "#$it" }

                // Step 4: Join them back together
                val output = modifiedParts.joinToString(", ")
                Log.d("output*1: ", output)
                binding!!.stats.intoIdentityPrefValu.text = output
            } else {
                binding!!.stats.tvIdentityHeading.visibility = View.GONE
                binding!!.stats.intoIdentityPrefValu.visibility = View.GONE
            }
        }

        fetchPrivateAlbum()

        //hideProgressView(rl_progress)
        //Toast.makeText(this@ProfileActivity, "Profile Updated", Toast.LENGTH_SHORT).show()
        //showImage()

    }

    private fun autoScrollImages() {
        val runnable = object : Runnable {
            override fun run() {
                if (currentPage < imgPathArr.size - 1) {
                    currentPage++  // Go to the next page
                } else {
                    currentPage = 0  // Reset to the first page after last one
                }
                binding!!.includedUserImg.userImgPager.setCurrentItem(currentPage, true)

                // Schedule the next scroll after 5 seconds
                handler.postDelayed(this, 5000)
            }
        }

        // Start auto-scroll after initial delay of 5 seconds
        handler.postDelayed(runnable, 3000)

        // Stop auto-scroll on user interaction
        binding!!.includedUserImg.userImgPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPage = position
                handler.removeCallbacks(runnable) // Stop auto-scroll on manual swipe
                handler.postDelayed(runnable, 3000)  // Restart after 5 seconds
            }
        })
    }


    private fun showImage() {
        DisplayImage.with(applicationContext)
            .load(if (profile!!.images.isNotEmpty()) profile!!.images[0].imagePath else "")
            .placeholder(R.drawable.meetupsfellow_transpatent).shape(DisplayImage.Shape.DEFAULT)
            .into(binding!!.includedUserImg.ivUserProfile1).build()
    }

    private fun callForUserProfile(type: ConstantsApi) {
        try {
            val user = sharedPreferencesUtil.fetchUserProfile()

            LogManager.logger.e(
                ArchitectureApp.instance!!.tag,
                " FETCH_PROFILE: ${URL("${BuildConfig.URL_LIVE}profile/{id}")}, ${sharedPreferencesUtil.fetchDeviceToken()}"
            )
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
                            ArchitectureApp.instance!!.tag,
                            "FETCH_PROFILE Response is ::: $response"
                        )
                        if (response != null) {
                            when (response.isSuccessful) {
                                true -> {
                                    val response: CommonResponse? = response.body()
                                    val profile: ResponseUserData? = response?.userInfo
                                    binding!!.includedUserImg.imagecount1.text =
                                        "1/" + profile!!.images.size
                                    //  mModel.saveUserProfile(Gson().toJson(response?.userInfo))
                                    /*  var visible_pro_user: Boolean? = profile.isProMembership
                                      if (visible_pro_user!!) {
                                          tv_pro_visibility!!.visibility = View.VISIBLE
                                      } else {
                                          tv_pro_visibility!!.visibility = View.GONE
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

    private fun getPics(position: Int) {
        when (position) {
            0 -> {
                if (privatePicsLocal.size == 3) {
                    universalToast("You can upload maximum 3 pictures at once.")
                } else {/*ActionSheetDialogChooseImage(
                        this@ProfileActivity, actionSheetInterface
                    ).show()*/
                    selectedImg = 6
                    EasyImage.openGallery(this@ProfileActivity, 0)
                }
            }

            else -> {
            }
        }
    }

    private fun openCalender() {
        val maxDateCalendar = Calendar.getInstance()
        maxDateCalendar.add(Calendar.YEAR, -18)
        val maxDateInMillis = maxDateCalendar.timeInMillis

        val date: DatePickerDialog.OnDateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                updateLabel()
            }
        }

        val datePickerDialog = DatePickerDialog(
            this,
            R.style.DatePickerDialogTheme,
            date,
            myCalendar[Calendar.YEAR],
            myCalendar[Calendar.MONTH],
            myCalendar[Calendar.DAY_OF_MONTH]
        )

        // Set the maximum date to today's date
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.datePicker.maxDate = maxDateInMillis
        datePickerDialog.show()

    }

    private fun updateLabel() {

        // val myFormat = "dd-MM-yyyy"
        val myFormat = "yyyy-MM-dd"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        dob_picker_edit.setText(dateFormat.format(myCalendar.time))

        val todayCal = Calendar.getInstance()

        Log.d("year", todayCal.get(Calendar.YEAR).toString())
        Log.d("yearC", myCalendar.get(Calendar.YEAR).toString())

        val currentYear = todayCal.get(Calendar.YEAR)
        val selectedYear = myCalendar.get(Calendar.YEAR)

        val age = currentYear - selectedYear
        userAge = age.toString().trim()
        Log.e("USER_AGE", "Age;- " + age)
        userAge_txt_edt.text = "You are $age year's old"
        /// userAge_txt.visibility = View.VISIBLE

        /// requestCreateUser.age = age.toString()
    }

    private fun showEditPopUp() {

        dialog = Dialog(this@ProfileActivity)

        dialog.setCancelable(true)

        //val activity = context as AppCompatActivity

        val view = layoutInflater.inflate(R.layout.user_edit_dialog, null)

        dialog.setContentView(view)

        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation

        if (dialog.window != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogEdTxtAge = view.findViewById<TextInputEditText>(R.id.dialogEdTxtAge)
        dob_picker_edit = view.findViewById<EditText>(R.id.dob_picker_edit)
        userAge_txt_edt = view.findViewById<TextView>(R.id.userAge_txt_edt)
        dialogEdTxtHome = view.findViewById<TextInputEditText>(R.id.dialogEdTxtHome)
        val editTxtBtn = view.findViewById<Button>(R.id.dialogUpdateBtn)
        userEditShortBiotxt = view.findViewById<TextInputEditText>(R.id.userEditShortBiotxt)
        //val userEditShortBiotxtlay = view.findViewById<TextInputLayout>(R.id.userEditShortBiotxtlay)
        //val edTxtTitleEmail = view.findViewById<TextView>(R.id.edTxtTitleEmail)
        dialogEdTxtEmail = view.findViewById<TextInputEditText>(R.id.dialogEdTxtEmail)

        val age = binding!!.stats.tvUserName1.text.toString()

        val arr = age.split(", ")
        userAge = arr[1].trim()
        dialogEdTxtAge.setText(arr[1])
        userAge_txt_edt.text = "You are ${arr[1]} year's old"
        dialogEdTxtHome.setText(binding!!.stats.tvHomeTown1.text.toString())

        dialogEdTxtEmail.setText(binding!!.stats.tvEmail1.text.toString())

        if (dialogEdTxtEmail.text.toString().isNotEmpty()) {
            dialogEdTxtEmail.isEnabled = false
        }

        userEditShortBiotxt.setText(binding!!.stats.tvUserBio.text.toString())

        dob_picker_edit.setText(profile!!.dob)
        ///  dob_picker_edit.setText("02-12-1990")

        dialogEdTxtHome.setOnClickListener {

            autoPlacePicker()
        }

        // Date_Picker
        dob_picker_edit.setOnClickListener {

            openCalender()
        }


        editTxtBtn.setOnClickListener {

            if (dialogEdTxtAge.text!!.isNotEmpty() && dialogEdTxtHome.text!!.isNotEmpty() && userEditShortBiotxt.text!!.isNotEmpty()) {

                /* if (dialogEdTxtEmail.text!!.trim().isNotEmpty()){
                     if (!isValidEmail(dialogEdTxtEmail.text!!.trim())){
                         universalToast("please enter valid email")
                         dialogEdTxtEmail.requestFocus()
                         return@setOnClickListener
                     }
                 }*/

                val myExecutor = Executors.newSingleThreadExecutor()
                val myHandler = Handler(Looper.getMainLooper())
                val profile = sharedPreferencesUtil.fetchUserProfile()
                Log.e("userProfile:- ", profile.toString())
                showProgressView(progressBar)
                dialog.dismiss()

                myExecutor.execute {

                    imgFile.clear()
                    for (i in profile.images.indices) {

                        var mImage: Bitmap?
                        val img = profile.images.get(i).imagePath
                        if (img != "" || img.isNotEmpty()) {
                            mImage = mLoad(img)
                        } else {
                            mImage = null
                        }
                        myHandler.post {
                            //mImageView.setImageBitmap(mImage)
                            if (mImage != null) {
                                imgFile.add(mSaveMediaToStorage(mImage)!!)

                                Log.d("imgFile", imgFile.get(i).absolutePath)

                            }
                        }
                    }
                    myHandler.post {
                        // comment by nilu
                        generateCreateProfileRequest()
                    }
                }

            } else {

                Toast.makeText(this@ProfileActivity, "Fields cannot be empty", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        dialog.show()
    }

    private fun isValidEmail(email: CharSequence?): Boolean {
        return !email.isNullOrEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }

    private fun showPreferencePopUp() {
        dialog = BottomSheetDialog(this, R.style.BottomSheetStyle)
        dialog.setCancelable(true)

        val view = layoutInflater.inflate(R.layout.layout_user_preferences, null)
        dialog.setContentView(view)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation

        val btnSavePref = view.findViewById<AppCompatButton>(R.id.btnSavePref)
        tvFisting1 = view.findViewById<TextView>(R.id.tvFisting1)
        tvFisting2 = view.findViewById<TextView>(R.id.tvFisting2)
        tvFisting3 = view.findViewById<TextView>(R.id.tvFisting3)
        tvIntercourse1 = view.findViewById<TextView>(R.id.tvIntercourse1)
        tvIntercourse2 = view.findViewById<TextView>(R.id.tvIntercourse2)
        tvIntercourse3 = view.findViewById<TextView>(R.id.tvIntercourse3)
        tvCondomYes = view.findViewById<TextView>(R.id.tvCondomYes)
        tvCondomNo = view.findViewById<TextView>(R.id.tvCondomNo)
        tvPrevention = view.findViewById<TextView>(R.id.tvPrevention)
        tvIdentity1 = view.findViewById<TextView>(R.id.tvIdentity1)
        tvIdentity2 = view.findViewById<TextView>(R.id.tvIdentity2)
        tvIdentity3 = view.findViewById<TextView>(R.id.tvIdentity3)
        tvIdentity4 = view.findViewById<TextView>(R.id.tvIdentity4)
        tvIdentity5 = view.findViewById<TextView>(R.id.tvIdentity5)
        tvIdentity6 = view.findViewById<TextView>(R.id.tvIdentity6)
        tvIdentity7 = view.findViewById<TextView>(R.id.tvIdentity7)
        tvIdentity8 = view.findViewById<TextView>(R.id.tvIdentity8)
        tvIdentity9 = view.findViewById<TextView>(R.id.tvIdentity9)
        tvIdentity10 = view.findViewById<TextView>(R.id.tvIdentity10)
        tvIdentity11 = view.findViewById<TextView>(R.id.tvIdentity11)
        tvIdentity12 = view.findViewById<TextView>(R.id.tvIdentity12)
        tvIdentity13 = view.findViewById<TextView>(R.id.tvIdentity13)
        tvIdentity14 = view.findViewById<TextView>(R.id.tvIdentity14)
        tvIdentity15 = view.findViewById<TextView>(R.id.tvIdentity15)
        tvIdentity16 = view.findViewById<TextView>(R.id.tvIdentity16)
        tvIdentity17 = view.findViewById<TextView>(R.id.tvIdentity17)
        tvIdentity18 = view.findViewById<TextView>(R.id.tvIdentity18)
        tvIdentity19 = view.findViewById<TextView>(R.id.tvIdentity19)

        tvMember1 = view.findViewById<TextView>(R.id.tvMember1)
        tvMember2 = view.findViewById<TextView>(R.id.tvMember2)
        tvMember3 = view.findViewById<TextView>(R.id.tvMember3)
        tvMember4 = view.findViewById<TextView>(R.id.tvMember4)
        tvMember5 = view.findViewById<TextView>(R.id.tvMember5)
        tvMember6 = view.findViewById<TextView>(R.id.tvMember6)
        tvMember7 = view.findViewById<TextView>(R.id.tvMember7)
        tvMember8 = view.findViewById<TextView>(R.id.tvMember8)
        tvMember9 = view.findViewById<TextView>(R.id.tvMember9)
        tvMember10 = view.findViewById<TextView>(R.id.tvMember10)
        tvMember11 = view.findViewById<TextView>(R.id.tvMember11)
        tvMember12 = view.findViewById<TextView>(R.id.tvMember12)
        tvMember13 = view.findViewById<TextView>(R.id.tvMember13)
        tvMember14 = view.findViewById<TextView>(R.id.tvMember14)
        tvMember15 = view.findViewById<TextView>(R.id.tvMember15)
        tvMember16 = view.findViewById<TextView>(R.id.tvMember16)
        tvMember17 = view.findViewById<TextView>(R.id.tvMember17)
        tvMember18 = view.findViewById<TextView>(R.id.tvMember18)
        tvMember19 = view.findViewById<TextView>(R.id.tvMember19)
        selectedIdentity.clear()
        selectedMembers.clear()
        if (profile!!.fistingId.isNotEmpty()) {
            when (profile!!.fistingId) {
                getString(R.string.text_fisting_id_1) -> {
                    profile!!.fistingId = getString(R.string.text_fisting_id_1)
                    tvFisting1.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
                }

                getString(R.string.text_fisting_id_2) -> {
                    profile!!.fistingId = getString(R.string.text_fisting_id_2)
                    tvFisting2.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
                }

                getString(R.string.text_fisting_id_3) -> {
                    profile!!.fistingId = getString(R.string.text_fisting_id_3)
                    tvFisting3.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
                }
            }
        }

        if (profile!!.generalIntercourse.isNotEmpty()) {
            when (profile!!.generalIntercourse) {
                getString(R.string.text_intercourse_1) -> {
                    profile!!.generalIntercourse = getString(R.string.text_intercourse_1)
                    tvIntercourse1.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
                }

                getString(R.string.text_intercourse_2) -> {
                    profile!!.generalIntercourse = getString(R.string.text_intercourse_2)
                    tvIntercourse2.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
                }

                getString(R.string.text_intercourse_3) -> {
                    profile!!.generalIntercourse = getString(R.string.text_intercourse_3)
                    tvIntercourse3.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
                }
            }
        }

        if (profile!!.condomPreference.isNotEmpty()) {
            when (profile!!.condomPreference) {
                getString(R.string.text_yes) -> {
                    profile!!.condomPreference = getString(R.string.text_yes)
                    tvCondomYes.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
                }

                getString(R.string.text_no) -> {
                    profile!!.condomPreference = getString(R.string.text_no)
                    tvCondomNo.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
                }

                getString(R.string.text_prevention) -> {
                    profile!!.condomPreference = getString(R.string.text_prevention)
                    tvPrevention.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
                }
            }
        }


        tvFisting1.setOnClickListener(View.OnClickListener {
            Log.e("Select_Pref", "fisting1")
            profile!!.fistingId = getString(R.string.text_fisting_id_1)
            tvFisting1.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
            tvFisting2.setBackgroundResource(R.drawable.rounded_corners_filter_default)
            tvFisting3.setBackgroundResource(R.drawable.rounded_corners_filter_default)

        })
        tvFisting2.setOnClickListener(View.OnClickListener {
            Log.e("Select_Pref", "fisting2")
            profile!!.fistingId = getString(R.string.text_fisting_id_2)
            tvFisting1.setBackgroundResource(R.drawable.rounded_corners_filter_default)
            tvFisting2.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
            tvFisting3.setBackgroundResource(R.drawable.rounded_corners_filter_default)
        })
        tvFisting3.setOnClickListener(View.OnClickListener {
            Log.e("Select_Pref", "fisting3")
            profile!!.fistingId = getString(R.string.text_fisting_id_3)
            tvFisting1.setBackgroundResource(R.drawable.rounded_corners_filter_default)
            tvFisting2.setBackgroundResource(R.drawable.rounded_corners_filter_default)
            tvFisting3.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
        })

        tvIntercourse1.setOnClickListener(View.OnClickListener {
            Log.e("Select_Pref", "text_intercourse_1")
            profile!!.generalIntercourse = getString(R.string.text_intercourse_1)
            tvIntercourse1.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
            tvIntercourse2.setBackgroundResource(R.drawable.rounded_corners_filter_default)
            tvIntercourse3.setBackgroundResource(R.drawable.rounded_corners_filter_default)

        })
        tvIntercourse2.setOnClickListener(View.OnClickListener {
            Log.e("Select_Pref", "text_intercourse_2")
            profile!!.generalIntercourse = getString(R.string.text_intercourse_2)
            tvIntercourse1.setBackgroundResource(R.drawable.rounded_corners_filter_default)
            tvIntercourse2.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
            tvIntercourse3.setBackgroundResource(R.drawable.rounded_corners_filter_default)
        })
        tvIntercourse3.setOnClickListener(View.OnClickListener {
            Log.e("Select_Pref", "text_intercourse_3")
            profile!!.generalIntercourse = getString(R.string.text_intercourse_3)
            tvIntercourse1.setBackgroundResource(R.drawable.rounded_corners_filter_default)
            tvIntercourse2.setBackgroundResource(R.drawable.rounded_corners_filter_default)
            tvIntercourse3.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
        })

        tvCondomYes.setOnClickListener(View.OnClickListener {
            Log.e("Select_Pref", "text_yes")
            profile!!.condomPreference = getString(R.string.text_yes)
            tvCondomYes.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
            tvCondomNo.setBackgroundResource(R.drawable.rounded_corners_filter_default)
            tvPrevention.setBackgroundResource(R.drawable.rounded_corners_filter_default)

        })
        tvCondomNo.setOnClickListener(View.OnClickListener {
            Log.e("Select_Pref", "text_No")
            profile!!.condomPreference = getString(R.string.text_no)
            tvCondomYes.setBackgroundResource(R.drawable.rounded_corners_filter_default)
            tvCondomNo.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
            tvPrevention.setBackgroundResource(R.drawable.rounded_corners_filter_default)
        })
        tvPrevention.setOnClickListener(View.OnClickListener {
            Log.e("Select_Pref", "text_prevention")
            profile!!.condomPreference = getString(R.string.text_prevention)
            tvCondomYes.setBackgroundResource(R.drawable.rounded_corners_filter_default)
            tvCondomNo.setBackgroundResource(R.drawable.rounded_corners_filter_default)
            tvPrevention.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
        })


        identity.add(tvIdentity1)
        identity.add(tvIdentity2)
        identity.add(tvIdentity3)
        identity.add(tvIdentity4)
        identity.add(tvIdentity5)
        identity.add(tvIdentity6)
        identity.add(tvIdentity7)
        identity.add(tvIdentity8)
        identity.add(tvIdentity9)
        identity.add(tvIdentity10)
        identity.add(tvIdentity11)
        identity.add(tvIdentity12)
        identity.add(tvIdentity13)
        identity.add(tvIdentity14)
        identity.add(tvIdentity15)
        identity.add(tvIdentity16)
        identity.add(tvIdentity17)
        identity.add(tvIdentity18)
        identity.add(tvIdentity19)

        identity.asSequence().forEach { it ->
            it.setOnClickListener {
                it.isSelected = !it.isSelected
                if (it.isSelected) {
                    Log.e("Select_Pref", "identity_ADD: " + it.tag)
                    selectedIdentity.add("${it.tag}")
                    it.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
                } else {
                    selectedIdentity.remove("${it.tag}")
                    Log.e("Select_Pref", "identity_Remove: " + it.tag)
                    it.setBackgroundResource(R.drawable.rounded_corners_filter_default)
                }
                selectedIdentity.sort()
                //  profile!!.myIdentity = selectedIdentity.toString().removeSurrounding("[", "]")
            }
        }
        selectedIdentity.addAll(profile!!.myIdentity.split(", "))
        identity.asSequence().forEach {
            if (selectedIdentity.contains("${it.tag}")) {
                it.isSelected = true
                it.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
                Log.e("Select_Pref", "selectedIdentity: " + it.tag)
            }
        }

        members.add(tvMember1)
        members.add(tvMember2)
        members.add(tvMember3)
        members.add(tvMember4)
        members.add(tvMember5)
        members.add(tvMember6)
        members.add(tvMember7)
        members.add(tvMember8)
        members.add(tvMember9)
        members.add(tvMember10)
        members.add(tvMember11)
        members.add(tvMember12)
        members.add(tvMember13)
        members.add(tvMember14)
        members.add(tvMember15)
        members.add(tvMember16)
        members.add(tvMember17)
        members.add(tvMember18)
        members.add(tvMember19)

        members.asSequence().forEach { it ->
            it.setOnClickListener {
                it.isSelected = !it.isSelected
                if (it.isSelected) {
                    selectedMembers.add("${it.tag}")
                    it.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
                } else {
                    selectedMembers.remove("${it.tag}")
                    it.setBackgroundResource(R.drawable.rounded_corners_filter_default)
                }
                selectedMembers.sort()
                //  profile!!.intoIdentity = selectedMembers.toString().removeSurrounding("[", "]")
            }
        }
        selectedMembers.addAll(profile!!.intoIdentity.split(", "))
        members.asSequence().forEach {
            if (selectedMembers.contains("${it.tag}")) {
                it.isSelected = true
                it.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
                Log.e("Select_Pref", "selected_Member: " + it.tag)
            }
        }

        btnSavePref.setOnClickListener {
            Log.e("Select_Pref", "Save_identity: " + selectedIdentity.size)
            Log.e("Select_Pref", "save_Member: " + selectedMembers.size)
            //selectedMembers.clear()
            // selectedIdentity.clear()

            profile!!.myIdentity = selectedIdentity.toString().removeSurrounding("[", "]")
            profile!!.intoIdentity = selectedMembers.toString().removeSurrounding("[", "]")

            updateUserPreference()
            dialog.cancel()
        }

        dialog.show()
    }

    private fun mLoad(string: String): Bitmap? {
        Log.e("String:- ", string)
        var demoUrl: String = "$AWS_URL_PROFILE$string"

        if (string != null && string.isNotEmpty()) {
            val url: URL? = mStringToURL(demoUrl)!!
            val connection: HttpURLConnection?
            try {
                if (url != null) {
                    connection = url.openConnection() as HttpURLConnection
                    connection.connect()
                    val inputStream: InputStream = connection.inputStream
                    val bufferedInputStream = BufferedInputStream(inputStream)
                    return BitmapFactory.decodeStream(bufferedInputStream)
                }
                return null
            } catch (e: IOException) {
                e.printStackTrace()
                // Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
                e.message?.let { Log.e("Error catch", it) }
            }
        }

        return null
    }

    // Function to convert string to URL
    private fun mStringToURL(string: String): URL? {
        try {
            Log.e("StringUrl:- ", URL(string).toString())
            val myURl = URL(string)
            Log.e("myURl:- ", myURl.toString())
            return myURl
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return null
    }

    // Function to save image on the device.
    // Refer: https://www.geeksforgeeks.org/circular-crop-an-image-and-save-it-to-the-file-in-android/
    private fun mSaveMediaToStorage(bitmap: Bitmap?): File {
        var imgFile: File? = null
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {/*this.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                //val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                //fos = imageUri?.let { resolver.openOutputStream(it) }
            }*/

            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            imgFile = image
            fos = FileOutputStream(image)

        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            imgFile = image
            fos = FileOutputStream(image)
        }
        fos.use {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            //Toast.makeText(this , "Saved to Gallery" , Toast.LENGTH_SHORT).show()
        }

        return imgFile
    }

    private fun updateUserSocialLinks() {

        val currentLocation = sharedPreferencesUtil.fetchLocation()

        images.clear()

        when (profile!!.images.isNotEmpty()) {
            true -> {
                profile!!.images.asSequence().forEach {
                    val index = profile!!.images.indexOf(it)
                    // when (indexExists(cross, index)) {
                    //  true -> {
                    // cross[index].visibility = View.VISIBLE
                    //   showImage(profileImage[index], it.imagePath)

                    images.add(ProfileImageModel().apply {
                        imageId = it.id
                        imagePath = it.imagePath
                        if (imgFile != null && imgFile.size != 0) {
                            image = imgFile.get(index)
                        }
                    })
                    //  }
                    // }
                }
            }

            false -> {}
        }

        profile!!.name = profile!!.name

        profile!!.gender = profile!!.gender

        profile!!.instagramLink = dialogEdTxtInsta.text.toString()

        Log.d("insta", profile!!.instagramLink.toString())

        profile!!.twitterLink = dialogEdTxtTwit.text.toString()
        profile!!.xtubeLink = dialogEdTxtYou.text.toString()

        profile!!.age = profile!!.age

        profile!!.email = profile!!.email

        profile!!.homeLocation = profile!!.homeLocation

        Log.d("userLat", "${currentLocation.latitude}")
        Log.d("userLong", "${currentLocation.longitude}")

        profile!!.homeLat = "${currentLocation.latitude}"
        profile!!.homeLong = "${currentLocation.longitude}"
        profile!!.currentLat = "${currentLocation.latitude}"
        profile!!.currentLong = "${currentLocation.longitude}"

        profile!!.aboutMe = profile!!.aboutMe

        requestCreateUser =
            convertResponseToRequest(profile!!, RequestCreateUser::class.java) as RequestCreateUser
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag,
            "Profile is3 : ${Gson().toJson(images)}"
        )
        images.asSequence().forEach {
            when (images.indexOf(it)) {
                0 -> {
                    if (null != it.image) {
                        requestCreateUser.apply {
                            image1 = it.image!!
                            id1 = 0
                        }
                    } else {
                        requestCreateUser.apply {
                            id1 = profile!!.images[0].id
                            imagePath1 = profile!!.images[0].imagePath
                        }
                    }
                }

                1 -> {

                    if (null != it.image) {
                        requestCreateUser.apply {
                            image2 = it.image!!
                            id2 = 0
                        }
                    } else {
                        requestCreateUser.apply {
                            id2 = profile!!.images[1].id
                            imagePath2 = profile!!.images[1].imagePath
                        }
                    }
                }

                2 -> {

                    if (null != it.image) {
                        requestCreateUser.apply {
                            image3 = it.image!!
                            id3 = 0
                        }
                    } else {
                        requestCreateUser.apply {
                            id3 = profile!!.images[2].id
                            imagePath3 = profile!!.images[2].imagePath
                        }
                    }
                }

                3 -> {

                    if (null != it.image) {
                        requestCreateUser.apply {
                            image4 = it.image!!
                            id4 = 0
                        }
                    } else {
                        requestCreateUser.apply {
                            id4 = profile!!.images[3].id
                            imagePath4 = profile!!.images[3].imagePath
                        }
                    }
                }

                4 -> {
                    if (null != it.image) {
                        requestCreateUser.apply {
                            image5 = it.image!!
                            id5 = 0
                        }
                    } else {
                        requestCreateUser.apply {
                            id5 = profile!!.images[4].id
                            imagePath5 = profile!!.images[4].imagePath
                        }
                    }
                }
            }
        }



        LogManager.logger.e(
            ArchitectureApp.instance!!.tag,
            "Profile_Request_Social_link : ${Gson().toJson(requestCreateUser)}"
        )
        Handler().postDelayed({
            createProfile()
        }, 200)


    }

    private fun generateCreateProfileRequest() {

        val currentLocation = sharedPreferencesUtil.fetchLocation()

        //val imgFile = createImageFileWith()

        images.clear()

        when (profile!!.images.isNotEmpty()) {
            true -> {
                profile!!.images.asSequence().forEach {
                    val index = profile!!.images.indexOf(it)
                    // when (indexExists(cross, index)) {
                    //  true -> {
                    // cross[index].visibility = View.VISIBLE
                    //   showImage(profileImage[index], it.imagePath)
                    images.add(ProfileImageModel().apply {
                        imageId = it.id
                        imagePath = it.imagePath
                        if (imgFile != null && imgFile.size != 0) {
                            image = imgFile.get(index)
                        } else Log.e("empty imgFile", "1213")
                    })
                    //  }
                    // }
                }
            }

            false -> {}
        }


        //val mImage: Bitmap? = mLoad(profile!!.images.get(0).imagePath)

        //mSaveMediaToStorage(mImage)

        /*val images1 = Gson().toJson(profile!!.images)

        val image = Gson().fromJson<ArrayList<ResponseUserProfileImages>>(
            images1,
            object : TypeToken<ArrayList<ResponseUserProfileImages>>() {}.type
        )*/



        requestCreateUser =
            convertResponseToRequest(profile!!, RequestCreateUser::class.java) as RequestCreateUser
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag,
            "Profile is4 : ${Gson().toJson(images)}"
        )
        images.asSequence().forEach {
            when (images.indexOf(it)) {
                0 -> {
                    if (null != it.image) {
                        requestCreateUser.apply {
                            image1 = it.image!!
                            id1 = 0
                        }
                    } else {
                        requestCreateUser.apply {
                            id1 = profile!!.images[0].id
                            imagePath1 = profile!!.images[0].imagePath
                        }
                    }
                }

                1 -> {

                    if (null != it.image) {
                        requestCreateUser.apply {
                            image2 = it.image!!
                            id2 = 0
                        }
                    } else {
                        requestCreateUser.apply {
                            id2 = profile!!.images[1].id
                            imagePath2 = profile!!.images[1].imagePath
                        }
                    }
                }

                2 -> {

                    if (null != it.image) {
                        requestCreateUser.apply {
                            image3 = it.image!!
                            id3 = 0
                        }
                    } else {
                        requestCreateUser.apply {
                            id3 = profile!!.images[2].id
                            imagePath3 = profile!!.images[2].imagePath
                        }
                    }
                }

                3 -> {

                    if (null != it.image) {
                        requestCreateUser.apply {
                            image4 = it.image!!
                            id4 = 0
                        }
                    } else {
                        requestCreateUser.apply {
                            id4 = profile!!.images[3].id
                            imagePath4 = profile!!.images[3].imagePath
                        }
                    }
                }

                4 -> {
                    if (null != it.image) {
                        requestCreateUser.apply {
                            image5 = it.image!!
                            id5 = 0
                        }
                    } else {
                        requestCreateUser.apply {
                            id5 = profile!!.images[4].id
                            imagePath5 = profile!!.images[4].imagePath
                        }
                    }
                }
            }
        }

        /*val imgFileNew = Compressor(this@ProfileActivity)
             .setMaxWidth(1280)
             .setMaxHeight(720)
             .setQuality(50)
             .setCompressFormat(Bitmap.CompressFormat.JPEG)
             .compressToFile(imgFile)

 */

        //val file = File(image.get(0).imagePath)


        /*images.asSequence().forEach {
            when (val index = images.indexOf(it)) {
                0 -> requestCreateUser.apply {
                    image1 = images[index].image!!
                    id1 = 0
                }

            }
        }*/

        requestCreateUser.name = profile!!.name

        requestCreateUser.gender = profile!!.gender

        //requestCreateUser.phoneNumber = sharedPreferencesUtil.fetchUserPhoneNumber()

        requestCreateUser.instagramLink = profile!!.instagramLink

        requestCreateUser.twitterLink = profile!!.twitterLink
        requestCreateUser.xtubeLink = profile!!.xtubeLink

        /// requestCreateUser.age = dialogEdTxtAge.text.toString()
        requestCreateUser.age = userAge
        requestCreateUser.dob = dob_picker_edit.text.toString().trim()

        ///   requestCreateUser.email = dialogEdTxtEmail.text.toString().trim()
        requestCreateUser.email = profile!!.email

        requestCreateUser.homeLocation = dialogEdTxtHome.text.toString()

        Log.e("Email*&", "${requestCreateUser.email}")
        Log.d("userLat", "${currentLocation.latitude}")
        Log.d("userLong", "${currentLocation.longitude}")

        requestCreateUser.homeLat = "${currentLocation.latitude}"
        requestCreateUser.homeLong = "${currentLocation.longitude}"
        requestCreateUser.currentLat = "${currentLocation.latitude}"
        requestCreateUser.currentLong = "${currentLocation.longitude}"

        requestCreateUser.aboutMe = userEditShortBiotxt.text.toString()

        requestCreateUser.condomPreference = profile!!.condomPreference
        requestCreateUser.intoIdentity = profile!!.intoIdentity
        requestCreateUser.myIdentity = profile!!.myIdentity
        requestCreateUser.generalIntercourse = profile!!.generalIntercourse
        requestCreateUser.fistingId = profile!!.fistingId

        LogManager.logger.e(
            ArchitectureApp.instance!!.tag,
            "Profile_Request_edit : ${Gson().toJson(requestCreateUser)}"
        )
        Handler().postDelayed({
            createProfile()
        }, 200)
    }

    private fun updateUserPreference() {
        showProgressView(progressBar)
        val currentLocation = sharedPreferencesUtil.fetchLocation()

        //val imgFile = createImageFileWith()

        images.clear()

        when (profile!!.images.isNotEmpty()) {
            true -> {
                profile!!.images.asSequence().forEach {
                    val index = profile!!.images.indexOf(it)
                    // when (indexExists(cross, index)) {
                    //  true -> {
                    // cross[index].visibility = View.VISIBLE
                    //   showImage(profileImage[index], it.imagePath)
                    images.add(ProfileImageModel().apply {
                        imageId = it.id
                        imagePath = it.imagePath
                        if (imgFile != null && imgFile.size != 0) {
                            image = imgFile.get(index)
                        } else Log.e("empty imgFile", "1213")
                    })
                    //  }
                    // }
                }
            }

            false -> {}
        }


        requestCreateUser =
            convertResponseToRequest(profile!!, RequestCreateUser::class.java) as RequestCreateUser
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag,
            "Profile is4 : ${Gson().toJson(images)}"
        )
        images.asSequence().forEach {
            when (images.indexOf(it)) {
                0 -> {
                    if (null != it.image) {
                        requestCreateUser.apply {
                            image1 = it.image!!
                            id1 = 0
                        }
                    } else {
                        requestCreateUser.apply {
                            id1 = profile!!.images[0].id
                            imagePath1 = profile!!.images[0].imagePath
                        }
                    }
                }

                1 -> {

                    if (null != it.image) {
                        requestCreateUser.apply {
                            image2 = it.image!!
                            id2 = 0
                        }
                    } else {
                        requestCreateUser.apply {
                            id2 = profile!!.images[1].id
                            imagePath2 = profile!!.images[1].imagePath
                        }
                    }
                }

                2 -> {

                    if (null != it.image) {
                        requestCreateUser.apply {
                            image3 = it.image!!
                            id3 = 0
                        }
                    } else {
                        requestCreateUser.apply {
                            id3 = profile!!.images[2].id
                            imagePath3 = profile!!.images[2].imagePath
                        }
                    }
                }

                3 -> {

                    if (null != it.image) {
                        requestCreateUser.apply {
                            image4 = it.image!!
                            id4 = 0
                        }
                    } else {
                        requestCreateUser.apply {
                            id4 = profile!!.images[3].id
                            imagePath4 = profile!!.images[3].imagePath
                        }
                    }
                }

                4 -> {
                    if (null != it.image) {
                        requestCreateUser.apply {
                            image5 = it.image!!
                            id5 = 0
                        }
                    } else {
                        requestCreateUser.apply {
                            id5 = profile!!.images[4].id
                            imagePath5 = profile!!.images[4].imagePath
                        }
                    }
                }
            }
        }

        requestCreateUser.name = profile!!.name

        requestCreateUser.gender = profile!!.gender

        //requestCreateUser.phoneNumber = sharedPreferencesUtil.fetchUserPhoneNumber()

        requestCreateUser.instagramLink = profile!!.instagramLink

        requestCreateUser.twitterLink = profile!!.twitterLink
        requestCreateUser.xtubeLink = profile!!.xtubeLink

        /// requestCreateUser.age = dialogEdTxtAge.text.toString()
        requestCreateUser.age = profile!!.age
        requestCreateUser.dob = profile!!.dob

        requestCreateUser.email = profile!!.email

        requestCreateUser.homeLocation = profile!!.homeLocation

        Log.d("userLat", "${currentLocation.latitude}")
        Log.d("userLong", "${currentLocation.longitude}")

        requestCreateUser.homeLat = "${currentLocation.latitude}"
        requestCreateUser.homeLong = "${currentLocation.longitude}"
        requestCreateUser.currentLat = "${currentLocation.latitude}"
        requestCreateUser.currentLong = "${currentLocation.longitude}"

        requestCreateUser.aboutMe = profile!!.aboutMe

        requestCreateUser.condomPreference = profile!!.condomPreference
        requestCreateUser.intoIdentity = profile!!.intoIdentity
        requestCreateUser.myIdentity = profile!!.myIdentity
        requestCreateUser.generalIntercourse = profile!!.generalIntercourse
        requestCreateUser.fistingId = profile!!.fistingId

        LogManager.logger.e(
            ArchitectureApp.instance!!.tag,
            "Profile_Request_Pref : ${Gson().toJson(requestCreateUser)}"
        )
        Handler().postDelayed({
            createProfile()
        }, 200)
    }

    private fun createProfile() {
        if (null == mPresenter) mPresenter = CreateProfilePresenter(presenter)

        run {
            mPresenter!!.addCreateUserObject(requestCreateUser)
            mPresenter!!.callRetrofit(ConstantsApi.CREATE_PROFILE)
        }
    }


    @Suppress("DEPRECATION")
    @Throws(IOException::class)
    private fun createImageFileWith(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFileName = "JPEG_$timestamp"
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "pics"
        )
        storageDir.mkdirs()
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }


    private fun autoPlacePicker() {

        // nearByList.clear()
        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        // dialog.dismiss()

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_server_key))
        }


        // Set the fields to specify which types of place data to return.
        val fields =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .setHint("Search Users by City").setTypeFilter(TypeFilter.CITIES).build(this)
        startActivityForResult(intent, Constants.RequestCode.AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    Constants.RequestCode.AUTOCOMPLETE_REQUEST_CODE -> {

                        val place = Autocomplete.getPlaceFromIntent(data!!)
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag,
                            "Place: " + place.name + ", " + place.id + ", Address : " + place.address
                        )
                        dialogEdTxtHome.setText(place.name)

                        //showEditPopUp("HomeTown", place.name.toString())
                    }

                    ConstantsImage.RequestCodes.PICK_PICTURE_FROM_GALLERY -> {

                        Log.d("image1111", " called")

                        imageFromGallery(requestCode, resultCode, data)
                    }
                }
            }

        }

    }

    private fun imageFromGallery(requestCode: Int, resultCode: Int, data: Intent?) {
        EasyImage.handleActivityResult(requestCode,
            resultCode,
            data,
            this,
            object : DefaultCallback() {
                override fun onImagesPicked(
                    imageFiles: MutableList<File>, source: EasyImage.ImageSource?, type: Int
                ) {

                    when (selectedImg) {

                        1 -> {
                            requestCreateUser.image1 = imageFiles[0]
                            requestCreateUser.imagePath1 = imageFiles[0].absolutePath
                            requestCreateUser.id1 = 0
                            requestCreateUser.image1 = compressImage(requestCreateUser.image1!!, maxWidth = 1920, maxHeight = 1080, quality = 80)
                           /* requestCreateUser.image1 = compressImage(
                                file = requestCreateUser.image1!!, // Original file
                                maxWidth = 1280,           // Max width
                                maxHeight = 720,           // Max height
                                quality = 50               // Compression quality
                            )*/

                            userImg1.setImageURI(Uri.fromFile(requestCreateUser.image1))
                        }

                        2 -> {

                            requestCreateUser.image2 = imageFiles[0]
                            requestCreateUser.imagePath2 = imageFiles[0].absolutePath
                            requestCreateUser.id2 = 0
                            requestCreateUser.image2 = compressImage(requestCreateUser.image2!!, maxWidth = 1920, maxHeight = 1080, quality = 80)
                            /*requestCreateUser.image2 = compressImage(
                                file = requestCreateUser.image2!!, // Original file
                                maxWidth = 1280,           // Max width
                                maxHeight = 720,           // Max height
                                quality = 50               // Compression quality
                            )*/


                            userImg2.setImageURI(Uri.fromFile(requestCreateUser.image2))
                            Log.e("Reqest_Pic%$", "image11-:  ${requestCreateUser.image2}")

                        }

                        3 -> {

                            requestCreateUser.image3 = imageFiles[0]
                            requestCreateUser.imagePath3 = imageFiles[0].absolutePath
                            requestCreateUser.id3 = 0

                           /* requestCreateUser.image3 = compressImage(
                                file = requestCreateUser.image3!!, // Original file
                                maxWidth = 1280,           // Max width
                                maxHeight = 720,           // Max height
                                quality = 50               // Compression quality
                            )*/
                            requestCreateUser.image3 = compressImage(requestCreateUser.image3!!, maxWidth = 1920, maxHeight = 1080, quality = 80)

                            userImg3.setImageURI(Uri.fromFile(requestCreateUser.image3))

                            Log.e("Reqest_pic", "image33-:  ${requestCreateUser.image3}")

                        }

                        4 -> {

                            requestCreateUser.image4 = imageFiles[0]
                            requestCreateUser.imagePath4 = imageFiles[0].absolutePath
                            requestCreateUser.id4 = 0

                               /* requestCreateUser.image4 = compressImage(
                                file = requestCreateUser.image4!!, // Original file
                                maxWidth = 1280,           // Max width
                                maxHeight = 720,           // Max height
                                quality = 50               // Compression quality
                            )*/
                            requestCreateUser.image4 = compressImage(requestCreateUser.image4!!, maxWidth = 1920, maxHeight = 1080, quality = 80)

                            userImg4.setImageURI(Uri.fromFile(requestCreateUser.image4))
                            Log.e("Reqest_pic", "image44-:  ${requestCreateUser.image4}")
                        }

                        5 -> {

                            requestCreateUser.image5 = imageFiles[0]
                            requestCreateUser.imagePath5 = imageFiles[0].absolutePath
                            requestCreateUser.id5 = 0

                           /* requestCreateUser.image5 = compressImage(
                                file = requestCreateUser.image5!!, // Original file
                                maxWidth = 1280,           // Max width
                                maxHeight = 720,           // Max height
                                quality = 50               // Compression quality
                            )*/
                            requestCreateUser.image5 = compressImage(requestCreateUser.image5!!, maxWidth = 1920, maxHeight = 1080, quality = 80)

                            userImg5.setImageURI(Uri.fromFile(requestCreateUser.image5))
                        }

                        6 -> {

                            var image = imageFiles[0]/* var exif: androidx.exifinterface.media.ExifInterface? = null
                             try {
                                 exif = androidx.exifinterface.media.ExifInterface(image.path)
                             } catch (e: IOException) {
                                 e.printStackTrace()
                             }*/


                           /* image = compressImage(
                                file = image!!, // Original file
                                maxWidth = 1280,           // Max width
                                maxHeight = 720,           // Max height
                                quality = 50               // Compression quality
                            )*/
                            image = compressImage(image, maxWidth = 1920, maxHeight = 1080, quality = 80)
                            privatePicsLocal.add(PrivateAlbumModel().apply {
                                this.image = image
                            })

                            privateAlbumAdapter.update(ResponsePrivatePics().apply {
                                this.path = image.absolutePath
                                this.type = "image"
                            })
                        }
                    }

                }

                override fun onCanceled(source: EasyImage.ImageSource, type: Int) {
                    if (source == EasyImage.ImageSource.CAMERA_IMAGE) {
                        EasyImage.lastlyTakenButCanceledPhoto(this@ProfileActivity)?.delete()
                    }
                }

                override fun onImagePickerError(
                    e: Exception, source: EasyImage.ImageSource, type: Int
                ) {
                    e.printStackTrace()
                }
            })
    }

    private fun updateUserStatus() {
        val user = sharedPreferencesUtil.fetchUserProfile()
        if (user.id.isNotEmpty()) {
            firebaseUtil.saveUserProfile(
                ProfileFirebase(
                    "",
                    getApprovedImage(user.images),
                    true,
                    user.id.toInt(),
                    user.name,
                    sharedPreferencesUtil.fetchDeviceToken(),
                    sharedPreferencesUtil.fetchSettings().allowPush != 0,
                    1,
                    "" + getUTCTime()
                )
            )
        }
    }

    private fun getApprovedImage(images: ArrayList<ResponseUserProfileImages>): String {
        var image = ""
        var imgeFill = ""

        run breaker@{
            images.asSequence().forEach {
                when (it.status) {
                    "approved" -> {
                        image = it.imagePath
                        Log.v("profileact", "" + image)
                        return@breaker
                    }
                }

            }
        }
        return image
    }

    class CustomViewAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private val arrayFragment = ArrayList<Fragment>()
        private val stringArray = ArrayList<String>()

        fun addFragment(fragment: Fragment, s: String) {

            this.arrayFragment.add(fragment)
            this.stringArray.add(s)
        }

        override fun getCount(): Int {
            return arrayFragment.size
        }

        override fun getItem(position: Int): Fragment {
            return arrayFragment[position]
        }

        override fun getPageTitle(position: Int): CharSequence {
            return stringArray[position]
        }
    }

}



