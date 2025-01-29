package com.connect.meetupsfellow.mvp.view.fragment

//import com.smarteist.autoimageslider.SliderView
import android.accounts.Account
import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.android.billingclient.api.*
import com.bumptech.glide.Glide
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.fragment.CustomFragment
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.constants.ProfileStatus
import com.connect.meetupsfellow.databinding.BlurBottomSheetDialogBinding
import com.connect.meetupsfellow.databinding.LayoutImageUserNewBinding
import com.connect.meetupsfellow.databinding.LayoutUserProfileBinding
import com.connect.meetupsfellow.databinding.LyoutStatsViewNewBinding
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.interfaces.ProfileListener
import com.connect.meetupsfellow.global.utils.DisplayImage
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.ConnectionRequestConnector
import com.connect.meetupsfellow.mvp.connector.activity.PrivateAlbumImagesConnector
import com.connect.meetupsfellow.mvp.connector.fragment.FavoriteUserConnector
import com.connect.meetupsfellow.mvp.presenter.activity.ConnectionRequestPresenter
import com.connect.meetupsfellow.mvp.presenter.activity.PrivateAlbumImagesPresenter
import com.connect.meetupsfellow.mvp.presenter.fragment.FavoriteUserPresenter
import com.connect.meetupsfellow.mvp.view.activities.UserProfileActivity
import com.connect.meetupsfellow.mvp.view.adapter.ImageSliderAdapter
import com.connect.meetupsfellow.mvp.view.adapter.PrivateAlbumAdapter
import com.connect.meetupsfellow.mvp.view.adapter.RecycleViewUserPvtPicAdapter
///import com.connect.meetupsfellow.mvp.view.adapter.SliderAdapter
import com.connect.meetupsfellow.mvp.view.model.ConversationModel
import com.connect.meetupsfellow.mvp.view.model.PrivateAlbumModel
import com.connect.meetupsfellow.mvp.view.model.RecycleModel
import com.connect.meetupsfellow.mvp.view.model.SliderData
import com.connect.meetupsfellow.retrofit.request.*
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ResponsePrivatePics
import com.connect.meetupsfellow.retrofit.response.ResponseUserData
import com.connect.meetupsfellow.retrofit.response.ResponseUserProfileImages
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.database.*
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ProfileFragment : CustomFragment() {

    private val pageChangeListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
        }

        override fun onTabSelected(tab: TabLayout.Tab?) {
            when (tab!!.position) {
                0 -> setSelected(binding.stats.llMain)
                1 -> setSelected(binding.stats.llMain)
            }
        }
    }

    private val presenterP = object : PrivateAlbumImagesConnector.RequiredViewOps {
        override fun showToast(error: String, logout: Boolean?) {
            when (logout) {
                true -> {
                    logout()
                }

                else -> {}
            }
            universalToast(error)
            privateAlbumAdapter.clearAll()
            //rvPrivateAlbum.visibility = View.GONE
            // tvNoUser.visibility = View.VISIBLE
///            hideProgressView(rlProgressBar)
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {
            when (type) {
                ConstantsApi.UPLOAD_PRIVATE -> {
                    universalToast(response.message)
                    showProgressView(rlProgressBar)
                    privatePicsLocal.clear()
                    //showHideSave()
                    fetchPrivateAlbum()
                }

                ConstantsApi.SEND_CONNECTION_REQUEST -> {
                    Toast.makeText(userProfileActivity, response.message, Toast.LENGTH_SHORT).show()
                    // universalToast(response.message)
                    binding.ivBlockUser.text = "Wait for accept request"
                    binding.ivBlockUser.visibility = View.VISIBLE
                    statsViewNewBinding.llThreeDote.visibility = View.GONE
                    statsViewNewBinding.ivChat1Lay.visibility = View.GONE
                    if (profile!!.isprofileprivate != 0) {
                        binding.ivBlockUser.visibility = View.GONE
                        statsViewNewBinding.llPrivetReciev.visibility = View.GONE
                        statsViewNewBinding.tvPrivetSentRecive.text =
                            getString(R.string.you_sent_connection_request)
                        statsViewNewBinding.btnConnectSendPrivet.visibility = View.GONE
                    }
                    Log.e(
                        "SEND_CONNECTION_REQUEST",
                        "Api_REsponse_Profile ${response.sendConnectionReqResponse.receiver_id}"
                    )
                    Log.e(
                        "SEND_CONNECTION_REQUEST",
                        "Status ${response.sendConnectionReqResponse.connection_status}"
                    )
                }

                ConstantsApi.PRIVATE_ALBUM, ConstantsApi.OTHER_PRIVATE_ALBUM -> {
                    userPicRv.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    privateAlbumAdapter.setType(PrivateAlbumAdapter.Image)
                    privateAlbumAdapter.privateAlbum(response.privatePics)
                    showProgressView(rlProgressBar)
                    setData(response.privatePics)
                    thisProfile = response.privatePics

                    Log.d("responsePics", response.privatePics.size.toString())
                    //userPicRv.visibility = View.VISIBLE
                    //tvNoUser.visibility = View.GONE
                }

                ConstantsApi.DELETE_PRIVATE -> {
                    universalToast(response.message)
                    //showHideSave()
                    showProgressView(rlProgressBar)
                    fetchPrivateAlbum()
                }

                ConstantsApi.PRIVATE_ACCESS -> {/*rvPrivateAlbum.layoutManager =
                        GridLayoutManager(rvPrivateAlbum.context!!, 1)
                    privateAlbumAdapter.setType(PrivateAlbumAdapter.User)
                    privateAlbumAdapter.getData().clear()
                    privateAlbumAdapter.update(response.picAccessList)
                    when (response.picAccessList.isEmpty()) {
                        true -> {
                            rvPrivateAlbum.visibility = View.GONE
                            tvNoUser.visibility = View.VISIBLE
                        }

                        false -> {
                            rvPrivateAlbum.visibility = View.VISIBLE
                            tvNoUser.visibility = View.GONE
                        }
                    }*/
                }

                ConstantsApi.PRIVATE_ACCESS_OTHERS -> {/*rvPrivateAlbum.layoutManager =
                        GridLayoutManager(rvPrivateAlbum.context!!, 1)
                    privateAlbumAdapter.setType(PrivateAlbumAdapter.Other)
                    privateAlbumAdapter.getData().clear()
                    privateAlbumAdapter.update(response.picAccessList)
                    when (response.picAccessList.isEmpty()) {
                        true -> {
                            rvPrivateAlbum.visibility = View.GONE
                            tvNoUser.visibility = View.VISIBLE
                        }

                        false -> {
                            rvPrivateAlbum.visibility = View.VISIBLE
                            tvNoUser.visibility = View.GONE
                        }
                    }*/
                }

                ConstantsApi.SHARE_PRIVATE, ConstantsApi.REMOVE_PRIVATE -> {
                    showProgressView(rlProgressBar)
                    //fetchPrivateAccessList()
                }

                else -> {
                }
            }
            hideProgressView(rlProgressBar)
        }
    }
    private val presenter = object : ConnectionRequestConnector.RequiredViewOps {
        override fun showToast(error: String, logout: Boolean?) {
            when (logout) {
                true -> {
                    logout()
                }

                false -> {}
                null -> {}
            }
            universalToast(error)
            hideProgressView(rlProgressBar)
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {

            when (type) {

                ConstantsApi.ACCEPT_CONNECTION_REQUEST -> {
                    if (response.sendConnectionReqResponse.connection_status.equals("connected")) {
                        binding.ivBlockUser.visibility = View.GONE
                        binding.llBtnReceive.visibility = View.GONE
                        binding.ivBlockUser.text = "Action"
                        statsViewNewBinding.ivChat1.setImageResource(R.drawable.ic_profile_chat)
                        statsViewNewBinding.llThreeDote.visibility = View.VISIBLE
                        statsViewNewBinding.ivChat1.visibility = View.VISIBLE
                        statsViewNewBinding.ivChat1Lay.visibility = View.VISIBLE

                        statsViewNewBinding.rlBlur.visibility = View.GONE
                        if (profile!!.isprofileprivate != 0) {
                            userProfileActivity.fetchUserProfile()
                        }
                    }
                    Toast.makeText(userProfileActivity, response.message, Toast.LENGTH_SHORT).show()

                    Log.e(
                        "ConnectRequest",
                        "Status ${response.message} and ${response.connection_status}"
                    )
                }

                ConstantsApi.DECLINE_CONNECTION_REQUEST -> {
                    Log.e(
                        "ConnectRequest",
                        "Status ${response.message} and^* ${response.connection_status}"
                    )
                    Toast.makeText(userProfileActivity, response.message, Toast.LENGTH_SHORT).show()
                    binding.ivBlockUser.visibility = View.GONE
                    binding.llBtnReceive.visibility = View.GONE
                    statsViewNewBinding.llThreeDote.visibility = View.GONE
                    statsViewNewBinding.ivChat1.visibility = View.VISIBLE
                    statsViewNewBinding.ivChat1.setImageResource(R.drawable.ic_send_request)
                    statsViewNewBinding.ivChat1Lay.visibility = View.VISIBLE
                    binding.ivBlockUser.text = "Sent connect request"

                    statsViewNewBinding.tvPrivetSentRecive.text =
                        getString(R.string.you_not_connect)
                    statsViewNewBinding.btnConnectSendPrivet.visibility = View.VISIBLE
                    statsViewNewBinding.llPrivetReciev.visibility = View.GONE
                    if (profile!!.isprofileprivate != 0) {
                        statsViewNewBinding.ivChat1Lay.visibility = View.GONE
                    }
                }

                ConstantsApi.UNFRIEND_CONNECTION_REQUEST -> {
                    Toast.makeText(
                        requireContext(), response.message, Toast.LENGTH_SHORT
                    ).show()

                    userProfileActivity.finish()
                    Log.e(
                        "CancelConnectRequest", "${response.message}"
                    )
                }

                else -> {
                    Log.e("ConnectRequest", "Api_Else")
                }
            }
            hideProgressView(rlProgressBar)
        }
    }

    private val favpresenter = object : FavoriteUserConnector.RequiredViewOps {
        override fun showToast(error: String, logout: Boolean?) {
            when (logout) {
                true -> {
                    logout()
                }

                false -> {}
                null -> {}
            }
            layoutImageUserNewBinding.ivFollow.isClickable = true
            layoutImageUserNewBinding.ivFavorite1.isClickable = true
            universalToast(error)
            hideProgressView(rlProgressBar)
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {
            when (type) {
                ConstantsApi.USER_FAVOURIT_UNFAVOURIT -> {
                    layoutImageUserNewBinding.ivFavorite1.isSelected = response.is_favourite == 1
                    layoutImageUserNewBinding.ivFavorite1.isClickable = true
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                }

                ConstantsApi.UNFOLLOW_USER -> {
                    profile!!.follower_status = "not_follow"
                    layoutImageUserNewBinding.ivFollow.isClickable = true
                    layoutImageUserNewBinding.ivFollow.isSelected = false
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                }

                ConstantsApi.FOLLOW_TO_USER -> {
                    profile!!.follower_status = "following"
                    layoutImageUserNewBinding.ivFollow.isClickable = true
                    layoutImageUserNewBinding.ivFollow.isSelected = true
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                }

                else -> {
                }
            }
            hideProgressView(rlProgressBar)
        }
    }

    private var mPresenter: ConnectionRequestConnector.PresenterOps? = null
    private var favoritePresenter: FavoriteUserConnector.PresenterOps? = null
    private fun setData(privatePics: ArrayList<ResponsePrivatePics>) {

        showProgressView(rlProgressBar)

        Log.d("pvrAccess", profile!!.hasSharedPrivateAlbumWithMe.toString())
        Log.d("pvrAccess", profile!!.ismyprivatealbumaccess.toString())

        if (profile!!.ismyprivatealbumaccess) {

            if (privatePics.size >= 1) {

                if (privatePics.size > 10) {

                    //addPrivateImgIc.visibility = View.VISIBLE
                }

                userPicRv.visibility = View.VISIBLE
                statsViewNewBinding.noPrivateImgLay.visibility = View.GONE
                statsViewNewBinding.addImgBtn.visibility = View.GONE

                Log.d("privateSize", privatePics.size.toString())

                val privatePicNew = ArrayList<ResponsePrivatePics>()
                privatePicNew.clear()/*if (privatePics.size > 10) {

                    for (i in 0..10) {

                        privatePicNew.add(privatePics[i])

                        *//*recycleViewModels.add(RecycleModel(privatePics[i].path, profile!!.userId, Allimages))
                        recycleViewUserPic = RecycleViewUserPicAdapter(this, recycleViewModels)
                        userPicRv.adapter = recycleViewUserPic
                        recycleViewUserPic.notifyItemRangeInserted(i, privatePics.lastIndex)*//*
                    }
                    Allimages = Gson().toJson(privatePicNew)

                    for (i in 0..10) {

                        recycleViewModels.add(
                            RecycleModel(
                                privatePics[i].path,
                                profile!!.userId,
                                Allimages,
                                privatePics.size
                            )
                        )
                        recycleViewUserPic =
                            RecycleViewUserPvtPicAdapter(requireContext(), recycleViewModels)
                        userPicRv.adapter = recycleViewUserPic
                        recycleViewUserPic.notifyItemRangeInserted(i, privatePics.lastIndex)

                    }
                    hideProgressView(rlProgressBar)
                } else {*/

                for (i in privatePics.indices) {

                    privatePicNew.add(privatePics[i])

                }

                Allimages = Gson().toJson(privatePicNew)
                Log.d("userPvtImg", Allimages)

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
                    recycleViewUserPic =
                        RecycleViewUserPvtPicAdapter(requireContext(), recycleViewModels)
                    userPicRv.adapter = recycleViewUserPic
                    recycleViewUserPic.notifyItemRangeInserted(i, privatePics.lastIndex)

                }
                hideProgressView(rlProgressBar)

            } else {

                userPicRv.visibility = View.GONE
                statsViewNewBinding.addImgBtn.visibility = View.GONE
                statsViewNewBinding.addPrivateImgIc.visibility = View.GONE
                statsViewNewBinding.noPrivateImgLay.visibility = View.VISIBLE
                statsViewNewBinding.noUserPrivateImg.text =
                    "${profile!!.name} don't have any media images"
            }
        } else {

            Log.d("privateSize", privatePics.size.toString())

            userPicRv.visibility = View.GONE
            statsViewNewBinding.addImgBtn.visibility = View.GONE
            statsViewNewBinding.addPrivateImgIc.visibility = View.GONE
            statsViewNewBinding.noPrivateImgLay.visibility = View.VISIBLE
            statsViewNewBinding.noUserPrivateImg.text =
                "You don't have access to ${profile!!.name}'s media images"
        }
    }


    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    private lateinit var mBillingClient: BillingClient
    private var thisProfile: ArrayList<ResponsePrivatePics>? = null
    private var profile: ResponseUserData? = null
    private var profileListener: ProfileListener? = null
    private val privatePicsLocal = arrayListOf<PrivateAlbumModel>()
    private val privateAlbumAdapter = PrivateAlbumAdapter()
    private var recycleViewModels = ArrayList<RecycleModel>()
    private lateinit var recycleViewUserPic: RecycleViewUserPvtPicAdapter
    lateinit var userPicRv: RecyclerView
    private var userId = ""
    var Allimages = ""
    val sliderDataArrayList = ArrayList<SliderData>()
    private var mPresenterP: PrivateAlbumImagesConnector.PresenterOps? = null
    lateinit var userProfileActivity: UserProfileActivity
    lateinit var phoneEmailLay: LinearLayout
    private var _binding: LayoutUserProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var rlProgressBar: LinearLayout
    private lateinit var statsViewNewBinding: LyoutStatsViewNewBinding
    private lateinit var layoutImageUserNewBinding: LayoutImageUserNewBinding
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage  = 0
    val imgPathArr = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        //  val view = inflater.inflate(R.layout.layout_user_profile, container, false)
        _binding = LayoutUserProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ArchitectureApp.component!!.inject(this@ProfileFragment)
        phoneEmailLay = view.findViewById(R.id.phoneEmailLay)
        userProfileActivity = activity as UserProfileActivity
        statsViewNewBinding = binding.stats
        layoutImageUserNewBinding = binding.includedUserImg
        rlProgressBar = binding.includedLoading.rlProgress

        phoneEmailLay.visibility = View.GONE
        Log.e("userProfileAc*", "otherUser_Profile_Fragment")
        userPicRv = view.findViewById(R.id.userPicsRv)

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        userPicRv.layoutManager = layoutManager
        recycleViewModels.clear()

        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText(getString(R.string.label_stats_text))
        )
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText(getString(R.string.label_bio_text))
        )
        binding.tabLayout.addOnTabSelectedListener(pageChangeListener)
        setOnClickListener()


    }

    @SuppressLint("ResourceType")
    private fun openBottomSheet(type: String, userId: Int) {

        // on below line we are creating a new bottom sheet dialog.
      /*  val dialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetStyle)
        val view = layoutInflater.inflate(R.layout.blur_bottom_sheet_dialog, null)
        dialog.setContentView(view)*/
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetStyle)
        val binding = BlurBottomSheetDialogBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.show()

        val btnClose = binding.idBtnDismiss
        val btnConnectReq = binding.ivConnectNow
        val btnSendDirectMsg = binding.btnSentDirectMsg
        val editTextMsg = binding.etDirectMsg
        val tvWordCount = binding.tvWordCount
        val llButtonPrivet = binding.llButtonPrivet


       /// editTextMsg.filters += InputFilter.LengthFilter(100)

        if (type.equals("MSG")) {
            editTextMsg.visibility = View.VISIBLE
            tvWordCount.visibility = View.VISIBLE
            llButtonPrivet.visibility = View.GONE
            btnSendDirectMsg.visibility = View.VISIBLE
            dialog.setCancelable(true)
        } else {
            editTextMsg.visibility = View.GONE
            tvWordCount.visibility = View.GONE
            llButtonPrivet.visibility = View.VISIBLE
            btnSendDirectMsg.visibility = View.GONE
            dialog.setCancelable(false)

            dialog.setOnShowListener {
                val bottomSheet = dialog.findViewById<View>(R.layout.blur_bottom_sheet_dialog)
                   /// dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                bottomSheet?.let {
                    val behavior = BottomSheetBehavior.from(it)
                    val layoutParams = it.layoutParams
                    layoutParams.height = statsViewNewBinding.scroll.height + 80  // Set your desired height here
                    it.layoutParams = layoutParams
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    behavior.skipCollapsed = true
                }
            }

        }
// Set maximum character limit
        val maxCharLimit = 150

// TextWatcher to update character count dynamically
        editTextMsg.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val charCount = s?.length ?: 0

                // Display the increasing character count
                tvWordCount.text = "$charCount/$maxCharLimit"

                // Trim excess characters if needed
                if (charCount > maxCharLimit) {
                    editTextMsg.setText(s?.substring(0, maxCharLimit))
                    editTextMsg.setSelection(maxCharLimit)
                }

            }
        })

        if (profile!!.connection_status.equals("request_sent") || profile!!.connection_status.equals(
                "direct_message_sent"
            )
        ) {
            btnConnectReq.text = "Waiting"
        } else if (profile!!.connection_status.equals("request_received") || profile!!.connection_status.equals(
                "direct_message_received"
            )
        ) {
            btnConnectReq.text = "Request accept"
        }

        btnClose.setOnClickListener {
            if (type.equals("Privet")) {
                requireActivity().finish()
            }
            dialog.dismiss()
        }
        btnConnectReq.setOnClickListener {
            if (btnConnectReq.text.toString().trim().equals("Waiting")) {
                Toast.makeText(requireContext(), "Request already sent", Toast.LENGTH_SHORT).show()
            } else if (btnConnectReq.text.toString().trim().equals("Request accept")) {
                acceptConnectionRequest(profile!!.userId.toInt())
                userProfileActivity.finish()
            } else {
                sendConnectionReq(profile!!.userId.toInt(), 0, "")
                userProfileActivity.finish()
            }
        }
        btnSendDirectMsg.setOnClickListener {
            ///  sendConnectionReq()
            if (editTextMsg.text.toString().isNotEmpty() && !editTextMsg.text.toString()
                    .equals("")
            ) {
                if (sharedPreferencesUtil.fetchUserProfile().available_connection_counts > 0 || sharedPreferencesUtil.fetchUserProfile().available_connection_counts == -1) {
                    sendConnectionReq(userId, 1, editTextMsg.text.toString().trim())
                    sendDirectMsgInFirebase(editTextMsg.text.toString().trim(), userId)
                    if (sharedPreferencesUtil.fetchUserProfile().available_connection_counts != -1) {
                        sharedPreferencesUtil.fetchUserProfile().available_connection_counts =
                            sharedPreferencesUtil.fetchUserProfile().available_connection_counts - 1
                    }
                    dialog.dismiss()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "You don't have available sent direct message!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(requireContext(), "Pleas enter message!", Toast.LENGTH_SHORT).show()
            }
        }

    }


    @SuppressLint("MissingInflatedId")
    private fun openActionBottomSheet() {
        // on below line we are creating a new bottom sheet dialog.
        val dialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetStyle)
        val view = layoutInflater.inflate(R.layout.profile_action_bottom_sheet, null)
        val btnDisconnect = view.findViewById<Button>(R.id.btn_disconnectUser)
        val btnReport = view.findViewById<Button>(R.id.btn_report)
        val btnBlock = view.findViewById<Button>(R.id.iv_blockUser)
        val ll_cancel = view.findViewById<LinearLayout>(R.id.ll_cancel)

        btnDisconnect.setOnClickListener {
            showAlertDisconnectDialog("disconnect")
            dialog.dismiss()
        }
        ll_cancel.setOnClickListener {
            dialog.dismiss()
        }
        btnReport.setOnClickListener {
            userProfileActivity.reportUser()
            dialog.dismiss()
        }
        btnBlock.setOnClickListener {
            showAlertDisconnectDialog("block")/* userProfileActivity.blockUser()
                   blockUserChat()*/
            ///   iv_blockUser.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun conversation(): String {
        if (null == profile) {
            return Gson().toJson(ConversationModel())
        }
        return Gson().toJson(ConversationModel().apply {
            convoId = Constants.ConvoId.id(
                sharedPreferencesUtil.userId().toInt(), profile!!.userId.toInt()
            )
            otherUserName = profile!!.name
            otherUserId = profile!!.id
            otherUserOnline = profile!!.status != 0
            blockedBY = ""
        })
    }

    fun formatCount(count: Int): String {
        return when {
            count >= 1_000_000 -> String.format("%.1fm", count / 1_000_000.0)
            count >= 1_000 -> String.format("%.1fk", count / 1_000.0)
            else -> count.toString()
        }
    }

    private fun setOnClickListener() {

        statsViewNewBinding.ivChat1Lay.setOnClickListener {
            Log.e("ProfileFrag", "youPro ${profile!!.isProMembership}")
            ///  sendDirectMsgInFirebase22("Hii first mdg", profile!!.userId.toInt())
            /// Add_below user connect or not
            /* if (null == profile || profile!!.isProMembership){
                 Toast.makeText(requireContext(),"Please Become Pro",Toast.LENGTH_SHORT).show()
                 AlertBuyPremium.Builder(requireActivity(), Constants.DialogTheme.NoTitleBar)
                     .build().show()
                 return@setOnClickListener
             }else */
            if (profile!!.connection_status.equals("connected")) {
                goToChat()
            } else if (profile!!.connection_status.equals("not_connected")) {
                showSentReqDialog(profile!!)
            }/*else if (profile!!.connection_status.equals("direct_message_received") || profile!!.connection_status.equals("direct_message_sent")){
                Toast.makeText(requireContext(),"Please connect 1",Toast.LENGTH_SHORT).show()
            }*/ else {
                Toast.makeText(requireContext(), "Please connect", Toast.LENGTH_SHORT).show()
            }
        }

        statsViewNewBinding.btnConnectSendPrivet.setOnClickListener {
            showSentReqDialog(profile!!)
        }

        statsViewNewBinding.btnCancelPrivet.setOnClickListener {
            if (profile!!.connection_status.equals("direct_message_received")) {
                goToChat()
            } else {
                declineConnectionRequest(profile!!.userId.toInt())
            }
        }
        statsViewNewBinding.btnConnectPrivet.setOnClickListener {
            if (profile!!.connection_status.equals("direct_message_received")) {
                goToChat()
            } else {
                acceptConnectionRequest(profile!!.userId.toInt())
            }
        }

        statsViewNewBinding.llThreeDote.setOnClickListener {
            openActionBottomSheet()
        }
        layoutImageUserNewBinding.ivFavorite1.setOnClickListener {

            if (null == profile) {
                return@setOnClickListener
            } else {
                layoutImageUserNewBinding.ivFavorite1.isClickable = false
                favouriteunfavouriteuser()/*if (sharedPreferencesUtil.fetchPrivateAlbum() == "true") {
                    profileListener!!.onProfile(ProfileStatus.Shared, profile!!.id)
                } else {
                    universalToast("Please select at least 1 picture")
                }*/
            }


            if (null == profile) return@setOnClickListener
            ///  profile!!.meLiked = !profile!!.meLiked

            if (null != profileListener)

            //   profileListener!!.onProfile(ProfileStatus.Liked, profile!!.twitterLink)
                profileListener!!.onProfile(ProfileStatus.Favorite, profile!!.twitterLink)
            //   profileListener!!.onProfile(ProfileStatus.HeartLike, profile!!.twitterLink)

        }

        layoutImageUserNewBinding.ivFollow.setOnClickListener {
            if (null == profile) {
                return@setOnClickListener
            } else {
                layoutImageUserNewBinding.ivFollow.isClickable = false
                Log.e("otherUser_Profile", "btnClick: ${profile!!.follower_status}")
                if (profile!!.follower_status.equals("following")) {
                    profile!!.followers_count = profile!!.followers_count - 1
                    layoutImageUserNewBinding.followerCount.text =
                        profile!!.followers_count.toString()
                    //  ivFollow.setImageResource(R.drawable.afterfollow_ic)
                    unFollowUser()
                } else {
                    profile!!.followers_count = profile!!.followers_count + 1
                    layoutImageUserNewBinding.followerCount.text =
                        profile!!.followers_count.toString()
                    //  ivFollow.setImageResource(R.drawable.followers_img)
                    followToUser()
                }
            }
        }

        layoutImageUserNewBinding.ivUserMultipleLike1.setOnClickListener {
            if (null == profile) {
                return@setOnClickListener
            } else {
                /// ivUserMultipleLike1.isClickable = false
                profile!!.meLiked = !profile!!.meLiked
                layoutImageUserNewBinding.ivUserMultipleLike1.isSelected = profile!!.meLiked

                if (layoutImageUserNewBinding.ivUserMultipleLike1.isSelected) {
                    profile!!.likedUserCount = profile!!.likedUserCount + 1
                    layoutImageUserNewBinding.likeCount.text = profile!!.likedUserCount.toString()
                } else {
                    profile!!.likedUserCount = profile!!.likedUserCount - 1
                    layoutImageUserNewBinding.likeCount.text = profile!!.likedUserCount.toString()
                }

                if (null != profileListener) profileListener!!.onProfile(
                    ProfileStatus.Liked,
                    profile!!.twitterLink
                )

            }

        }

        layoutImageUserNewBinding.rlProfileImage1.setOnClickListener {
            if (null == profile) {
                return@setOnClickListener
            } else {
                when (profile!!.images.isNotEmpty()) {
                    true -> {
                        switchActivity(Constants.Intent.ImagePager, false, Bundle().apply {
                            putString(
                                Constants.IntentDataKeys.ProfileImages,
                                Gson().toJson(profile!!.images)
                            )
                            putString(Constants.IntentDataKeys.UserId, profile!!.userId)
                            putString("Class", "Profile")
                        })
                    }
                    false -> {}
                    else -> {}
                }
            }
        }

        statsViewNewBinding.tvTwitter111.setOnClickListener {
            if (null == profile || profile!!.twitterLink.isEmpty()) return@setOnClickListener
            if (null != profileListener) profileListener!!.onProfile(
                ProfileStatus.Clicked,
                profile!!.twitterLink
            )
        }

        statsViewNewBinding.tvInstagram111.setOnClickListener {
            if (null == profile || profile!!.instagramLink.isEmpty()) return@setOnClickListener
            if (null != profileListener) profileListener!!.onProfile(
                ProfileStatus.Insta,
                profile!!.instagramLink
            )
        }

        statsViewNewBinding.tvYoutube111.setOnClickListener {
            if (null == profile || profile!!.xtubeLink.isEmpty()) return@setOnClickListener
            if (null != profileListener) profileListener!!.onProfile(
                ProfileStatus.Youtube,
                profile!!.xtubeLink
            )
        }

        statsViewNewBinding.shareProfile.setOnClickListener {
            // Create a dynamic link with pagination parameters other user profile
            FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.example.com/?contentId=${profile!!.userId}")) // Pagination parameters included
                .setDomainUriPrefix("https://meetupsfellow.page.link")
                .setAndroidParameters(DynamicLink.AndroidParameters.Builder().build())
                .buildShortDynamicLink()
                .addOnSuccessListener(requireActivity()) { shortDynamicLink ->
                    val shortLink = shortDynamicLink.shortLink.toString()
                    // Handle the short link, e.g., share it or open it
                    Log.e("DeepLink@*", "other: $shortLink")

                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shortLink)
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                }
        }

        layoutImageUserNewBinding.rlShared1.setOnClickListener {
            if (null == profile) return@setOnClickListener
            if (null != profileListener)

                if (sharedPreferencesUtil.fetchPrivateAlbum() == "true") {
                    profileListener!!.onProfile(ProfileStatus.Shared, profile!!.id)

                } else {
                    universalToast("Please select at least 1 picture")
                }


        }

    }

    override fun onResume() {
        super.onResume()
        if (thisProfile != null && thisProfile!!.isNotEmpty()) {
            showProgressView(rlProgressBar)
        }

    }

    private fun demotestimhg() {

        val accountManager = AccountManager.get(context)

        val accounts: Array<Account> = accountManager.getAccountsByType("com.google")
        val account: Account?
        account = if (accounts.size > 0) {
            accounts[0]
        } else {
            null
        }
        universalToast(account!!.name)

    }

    internal fun profileStatus(profile: ProfileListener) {
        profileListener = profile
    }

    private fun setSelected(view: View) {
        binding.stats.llMain.visibility = View.GONE
        view.visibility = View.VISIBLE
    }

    internal fun shared(share: Boolean) {
        layoutImageUserNewBinding.rlShared1.isSelected = share
        profile?.isMyPrivateAlbumSharedWithUser = share
    }

    private fun sendDirectMsgInFirebase(msg: String, otherUserId: Int) {
        val selfUserId = sharedPreferencesUtil.fetchUserProfile().userId
        val msgTime = System.currentTimeMillis().toString()
        val newMsgId = msgTime
        lateinit var chatDb: DatabaseReference
        lateinit var chatListDb: DatabaseReference
        val chatRoomId = Constants.ConvoId.id(
            selfUserId.toInt(), otherUserId
        )
        // val  chatRoomId = selfUserId + "-" + otherUserId
        //   val  otherChatRoomId =   otherUserId.toString() + "-" + selfUserId
        Log.e("SendMsgTime", newMsgId)
        Log.e("chatRoomId:- ", chatRoomId)

        chatDb =
            FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
                .child("ChatMessages")
        Log.e("chatRoomId:: ", chatRoomId)

        chatListDb =
            FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
                .child("ChatList")

        chatDb.child(chatRoomId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {
                Log.e("NotExists", "Create One")
                val chatList = chatListDb.child(selfUserId).child(chatRoomId)
                val otherChatList = chatListDb.child(otherUserId.toString()).child(chatRoomId)
                Log.e("Send_Sms*&", "createUpdateChatList_Call1_self")

                lateinit var otherProfile: String
                if (profile!!.images.isNotEmpty()) {
                    otherProfile = profile!!.images[0].imagePath
                } else {
                    otherProfile = profile!!.userPic
                }
                createUpdateChatList(
                    chatList,
                    msg,
                    otherProfile,
                    "",
                    "",
                    "",
                    "",
                    0,
                    profile!!.name,
                    chatRoomId,
                    profile!!.userId
                )

                if (sharedPreferencesUtil.fetchUserProfile().images.isNotEmpty()) {
                    Log.e("Send_Sms*&", "createUpdateChatList_Call2_Other")
                    createUpdateChatList(
                        otherChatList,
                        msg,
                        sharedPreferencesUtil.fetchUserProfile().images[0].imagePath,
                        "",
                        "",
                        "",
                        "",
                        1,
                        sharedPreferencesUtil.fetchUserProfile().name,
                        chatRoomId,
                        sharedPreferencesUtil.fetchUserProfile().userId

                    )
                } else {
                    Log.e("Send_Sms*&", "createUpdateChatList_Call33_Other")
                    createUpdateChatList(
                        otherChatList,
                        msg,
                        sharedPreferencesUtil.fetchUserProfile().userPic,
                        "",
                        "",
                        "",
                        "",
                        1,
                        sharedPreferencesUtil.fetchUserProfile().name,
                        chatRoomId,
                        sharedPreferencesUtil.fetchUserProfile().userId
                    )
                }

                val msgId = System.currentTimeMillis().toString()

                val msgDb = chatDb.child(chatRoomId).child("chats").push()

                val dataMap = HashMap<String, Any>()
                dataMap["messageText"] = msg
                dataMap["deliveredTime"] = msgId
                dataMap["deliveryStatus"] = "1"
                val childRefKey: String = msgDb.key.toString()
                val innerHashMap = HashMap<String, String>()
                innerHashMap[otherUserId.toString()] = ""
                innerHashMap[selfUserId] = ""
                dataMap["mediaDeviceUrlOf"] = innerHashMap
                val innerHashMapDelet = HashMap<String, Boolean>()
                innerHashMapDelet[otherUserId.toString()] = false
                innerHashMapDelet[selfUserId] = false
                dataMap["messagesDeletedBY"] = innerHashMapDelet
                dataMap["messagesDeletedBYSender"] = false
                dataMap["msgTime"] = System.currentTimeMillis()
                dataMap["messageTime"] = System.currentTimeMillis()
                dataMap["mediaLength"] = "0"
                dataMap["mediaType"] = 1
                dataMap["mediaUrl"] = ""
                dataMap["messageId"] = childRefKey
                dataMap["messageStatus"] = 1
                dataMap["messageType"] = 1
                dataMap["readTime"] = ""
                dataMap["readStatus"] = ""
                dataMap["mediaSize"] = ""
                dataMap["receiverId"] = otherUserId
                dataMap["repliedOnMediaName"] = ""
                dataMap["repliedOnMediaType"] = ""
                dataMap["repliedOnMediaUrl"] = ""
                dataMap["repliedOnMessageId"] = ""
                dataMap["repliedOnText"] = ""
                dataMap["senderId"] = selfUserId
                dataMap["senderName"] = sharedPreferencesUtil.fetchUserProfile().name
                dataMap["thumbnailUrl"] = ""
                msgDb.setValue(dataMap)

                Log.e("Sent_msg", "object2: $dataMap")
                Log.e("Sent_msg", "object2: $msgDb")


            }

            override fun onCancelled(p0: DatabaseError) {
                Log.e("firebase_Error", "${p0.message}")
            }
        }

        )

    }

    private fun createUpdateChatList(
        chatList: DatabaseReference,
        msg: String,
        otherImg: String,
        replyTo: String,
        replyMsg: String,
        replyId: String,
        replyMedia: String,
        unreadCount: Int,
        userName: String,
        chatRoomId: String,
        otherUserId: String
    ) {

        Log.e("Send_Sms*&", "createUpdateChatList")
        chatList.child("chatOpen").setValue(false)
        chatList.child("convoId").setValue(chatRoomId)
        chatList.child("deliveredTime").setValue(System.currentTimeMillis())
        chatList.child("deliveryStatus").setValue("1")
        chatList.child("messageText").setValue(msg)
        chatList.child("messageTime").setValue(System.currentTimeMillis())
        chatList.child("otherUserOnline").setValue(true)
        chatList.child("readStatus").setValue("")
        chatList.child("readTime").setValue("")
        chatList.child("messagesDeletedBY").setValue("")
        chatList.child("messagesDeletedBYSender").setValue(false)
        chatList.child("receiverId").setValue(profile!!.userId)
        chatList.child("blockedBY").setValue("")
        chatList.child("isUserConnected").setValue("no")
        ///  chatList.child("isFirstMassage").setValue(true)
        chatList.child("otherUserId").setValue(otherUserId)
        chatList.child("repliedOnMediaName").setValue("")
        chatList.child("repliedOnMediaType").setValue("")
        chatList.child("repliedOnMediaUrl").setValue(replyMedia)
        chatList.child("repliedOnMessageId").setValue(replyId)
        chatList.child("repliedOnText").setValue(replyMsg)
        chatList.child("senderId").setValue(sharedPreferencesUtil.fetchUserProfile().userId)
        chatList.child("senderName").setValue(sharedPreferencesUtil.fetchUserProfile().name)
        chatList.child("unReadCount").setValue(unreadCount.toString())
        chatList.child("OtherUserImage").setValue(otherImg)
        chatList.child("otherUserName").setValue(userName)
    }

    internal fun setupUserProfileDetails(profile: ResponseUserData) {
//        Log.e("pp+", profile.toString())
        profile.hasPrivatePic
        statsViewNewBinding.editUserAgeIc.visibility = View.GONE
        statsViewNewBinding.addPreferencesIc.visibility = View.GONE
        layoutImageUserNewBinding.userProfileBackBtn.setOnClickListener {
            /*if ( (activity as? UserProfileActivity)?.linkIntentData == 1){
                switchActivity(Constants.Intent.Home, true, Bundle())
                requireActivity().finish()
            }else{
                requireActivity().finish()
            }*/
            (activity as? UserProfileActivity)?.kilActivity()
        }
        statsViewNewBinding.memberSinceTxt.text = "Member Since " + profile.membersince
        if (isAdded && isVisible) {
            this.profile = profile

            statsViewNewBinding.tvUserName1.text = String.format(
                getString(R.string.text_height_weight), profile.name, profile.age
            )
            statsViewNewBinding.memberSinceTxt.visibility = View.VISIBLE

            val paint = statsViewNewBinding.tvUserName1.paint
            val width = paint.measureText(statsViewNewBinding.tvUserName1.text.toString())
            val textShader: Shader = LinearGradient(
                0f, 0f, width, statsViewNewBinding.tvUserName1.textSize, intArrayOf(
                    Color.parseColor("#F4447E"), Color.parseColor("#8448F4")/*Color.parseColor("#64B678"),
                    Color.parseColor("#478AEA"),*/
                    //Color.parseColor("#8446CC")
                ), null, Shader.TileMode.REPEAT
            )

            statsViewNewBinding.tvUserName1.paint.setShader(textShader)


            layoutImageUserNewBinding.imagecount1.text = "1/" + profile.images.size
            if (profile.isProMembership) {
                // prouser.visibility = View.VISIBLE
                //  pro_user_image.setText("PRO User")
                //pro_user_image1.visibility = View.VISIBLE
            } else {
                layoutImageUserNewBinding.proUserImage1.visibility = View.GONE
                //  prouser.visibility = View.GONE
            }

            layoutImageUserNewBinding.ivFollow.isSelected =
                profile.follower_status.equals("following")


            if (profile.twitterLink.isNotEmpty()) {
                val twitter = profile.twitterLink.split("/")
                if (twitter.isNotEmpty()) {
                    statsViewNewBinding.tvTwitter1.text = twitter[twitter.size - 1]
                    statsViewNewBinding.icTitter.setImageDrawable(resources.getDrawable(R.drawable.twitter_select))
                } else {
                    statsViewNewBinding.icTitter.setImageDrawable(resources.getDrawable(R.drawable.twitter))
                }
            }

            if (profile.instagramLink.isNotEmpty()) {
                val instg = profile.instagramLink.split("/")
                if (instg.isNotEmpty()) {
                    statsViewNewBinding.tvInstagram1.text = instg[instg.size - 1]
                    statsViewNewBinding.icInsta.setImageDrawable(resources.getDrawable(R.drawable.selected_instagram))
                } else {
                    statsViewNewBinding.icInsta.setImageDrawable(resources.getDrawable(R.drawable.instagram))
                }
            }

            if (profile.xtubeLink.isNotEmpty()) {
                val youtube = profile.xtubeLink.split("/")
                if (youtube.isNotEmpty()) {
                    statsViewNewBinding.youTubeTv.text = youtube[youtube.size - 1]
                    statsViewNewBinding.icYoutube.setImageDrawable(resources.getDrawable(R.drawable.youtube_select))
                } else {
                    statsViewNewBinding.icYoutube.setImageDrawable(resources.getDrawable(R.drawable.youtube))
                }
            }
            Log.d("SocialLinkUpdated", profile.instagramLink)
            Log.e("SocialLinkUpdated", profile.xtubeLink)

            /*  if (profile!!.twitterLink.isEmpty()) {
                  icTitter.setImageDrawable(resources.getDrawable(R.drawable.twitter))
                  /// icTitter.backgroundTintList = resources.getColorStateList(R.color.grey)
              } else {

                  icTitter.setImageDrawable(resources.getDrawable(R.drawable.twitter_select))
              }

              if (profile!!.instagramLink.isEmpty()) {
                  icInsta.setImageDrawable(resources.getDrawable(R.drawable.instagram))
                  ///  icInsta.backgroundTintList = resources.getColorStateList(R.color.grey)
              } else {
                  icInsta.setImageDrawable(resources.getDrawable(R.drawable.instagram_svgrepo_com))
              }

              if (profile!!.xtubeLink.isEmpty()) {
                  icYoutube.setImageDrawable(resources.getDrawable(R.drawable.youtube))
                  ///  icYoutube.backgroundTintList = resources.getColorStateList(R.color.grey)
              } else {
                  icYoutube.setImageDrawable(resources.getDrawable(R.drawable.youtube_select))
              }*/

            statsViewNewBinding.addPrivateImgIc.visibility = View.GONE
            statsViewNewBinding.addSocialLinksIc.visibility = View.GONE


            if (profile.status == 0) {
                statsViewNewBinding.imgStatus1.setImageResource(R.drawable.drawable_user_offline)
                statsViewNewBinding.textOnlineAgo1.text = "Offline"
                //Gray Dot
            } else {
                if (profile.lastLoginTimeStamp != "") {
                    try {
                        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

                        Log.d("format", "" + format)

                        var lastDate = format.parse(profile.lastLoginTimeStamp)
                        Log.d("format", "" + lastDate)

                        var currentUTCTime = format.parse(getUTCTime())
                        val diff: Long = currentUTCTime.time - lastDate.time
                        val seconds = diff / 1000
                        val minutes = seconds / 60
                        val hours = minutes / 60
                        Log.d("format", "Profile: $hours")
// 1 day ago

                        if (hours <= 4) {
                            statsViewNewBinding.textOnlineAgo1.text = "Online"
                            statsViewNewBinding.imgStatus1.setImageResource(R.drawable.drawable_user_online)
                            //Green Dot
                        } else if (hours > 4 && hours < 48) {

                            if (hours <= 23) {
                                statsViewNewBinding.textOnlineAgo1.text =
                                    "Online " + hours + " hours ago"
                                statsViewNewBinding.imgStatus1.setImageResource(R.drawable.drawable_user_recent_online)
                            } else if ((hours / 24) > 1) {
                                statsViewNewBinding.textOnlineAgo1.text =
                                    "Online " + hours / 24 + " days ago"
                                statsViewNewBinding.imgStatus1.setImageResource(R.drawable.drawable_user_recent_online)
                            } else {
                                statsViewNewBinding.textOnlineAgo1.text =
                                    "Online " + hours / 24 + " day ago"
                                statsViewNewBinding.imgStatus1.setImageResource(R.drawable.drawable_user_recent_online)
                            }

                            // Orange DOt
                        } else {
                            statsViewNewBinding.imgStatus1.setImageResource(R.drawable.drawable_user_offline)
                            //Gray Dot
                            if ((hours / 24) > 1) {
                                statsViewNewBinding.textOnlineAgo1.text =
                                    "Offline " + hours / 24 + " days ago"
                            } else {
                                statsViewNewBinding.textOnlineAgo1.text =
                                    "Offline " + hours / 24 + " day ago"
                            }


                        }
                    } catch (e: Exception) {

                    }
                } else {
                    if (profile.status == 1) {
                        statsViewNewBinding.imgStatus1.setImageResource(R.drawable.drawable_user_online)
                        //Green Dot
                        statsViewNewBinding.textOnlineAgo1.text = "Online"
                    } else {
                        statsViewNewBinding.imgStatus1.setImageResource(R.drawable.drawable_user_offline)
                        //Gray Dot
                        statsViewNewBinding.textOnlineAgo1.text = "Offline"
                    }
                }
            }

            recycleViewModels.clear()

            val images = Gson().toJson(profile.images)

            val image = Gson().fromJson<ArrayList<ResponseUserProfileImages>>(
                images, object : TypeToken<ArrayList<ResponseUserProfileImages>>() {}.type
            )

            imgPathArr.clear()
            sliderDataArrayList.clear()

            Log.d("userImg22222", profile.images.toString())

         ///   val userViewPagerAdapter = SliderAdapter(requireContext(), images, profile.userId)

                for (i in image.indices) {

                    if (image[i].imagePath.isNotEmpty()) {
                        imgPathArr.add(image[i].imagePath)
                        sliderDataArrayList.add(SliderData(image[i].imagePath))
                        Log.d("image", image[i].imagePath)

                    }
                }


            if (imgPathArr.isNotEmpty() && imgPathArr.size > 0){
                // Set adapter
                val adapter = ImageSliderAdapter(requireContext(),imgPathArr, profile!!.userId, profile!!.images)
                binding.includedUserImg.userImgPager.adapter = adapter


                if (imgPathArr.size > 1){
                    // Auto-scroll images every 5 seconds
                    autoScrollImages()

                    // Attach dots to ViewPager2
                    TabLayoutMediator(binding.includedUserImg.tabLayoutIndecatore, binding.includedUserImg.userImgPager) { tab, _ ->
                        tab.view.setBackgroundResource(R.drawable.tab_indicator_selector)
                        tab.view.post {
                            val layoutParams = tab.view.layoutParams
                            if (layoutParams is ViewGroup.MarginLayoutParams) {
                                layoutParams.setMargins(4, 0, 4, 0)  // Increase space between dots
                                tab.view.layoutParams = layoutParams
                            }
                        }
                    }.attach()
                }

                binding.includedUserImg.userImgPager.visibility = View.VISIBLE

            }else{
                if (image.isEmpty()) {
                    binding.includedUserImg.ivUserProfile1.setImageResource(R.drawable.meetupsfellow_transpatent)
                }else{
                    Glide.with(this)
                        .load(image[0].imagePath)
                        .placeholder(R.drawable.meetupsfellow_transpatent)
                        .into( binding.includedUserImg.ivUserProfile1)
                    binding.includedUserImg.ivUserProfile1.visibility = View.VISIBLE
                }
                binding.includedUserImg.userImgPager.visibility = View.GONE
                binding.includedUserImg.ivUserProfile1.visibility = View.VISIBLE
            }


          /*  if (sliderDataArrayList.size >= 2) {

                layoutImageUserNewBinding.sliderView.autoCycleDirection =
                    layoutImageUserNewBinding.SliderView.LAYOUT_DIRECTION_LTR
                layoutImageUserNewBinding.sliderView.setSliderAdapter(userViewPagerAdapter)
                layoutImageUserNewBinding.sliderView.scrollTimeInSec = 4

                layoutImageUserNewBinding.sliderView.isAutoCycle = true
                layoutImageUserNewBinding.sliderView.startAutoCycle()
            } else {

                layoutImageUserNewBinding.sliderView.setSliderAdapter(userViewPagerAdapter)
                layoutImageUserNewBinding.sliderView.isAutoCycle = false
            }*/

            userId = profile.userId
            /// fetchPrivateAlbumOthers()
            layoutImageUserNewBinding.ivFavorite1.isSelected = profile.is_favourite == 1
            layoutImageUserNewBinding.ivUserMultipleLike1.isSelected = profile.meLiked

            layoutImageUserNewBinding.followerCount.text = profile.followers_count.toString()
            layoutImageUserNewBinding.likeCount.text = profile.likedUserCount.toString()
            if (profile.likedUserCount > 999) {
                layoutImageUserNewBinding.likeCount.text = formatCount(profile.likedUserCount)
            }
            if (profile.followers_count > 999) {
                layoutImageUserNewBinding.followerCount.text = formatCount(profile.followers_count)
            }

            Log.d("Profile_frag", "Bedge: " + profile.currentPlanInfo!!.bedge)/*if (profile.currentPlanInfo!!.bedge == 2){
                proTxt.text = profile.currentPlanInfo!!.planTitle
            }else if (profile.currentPlanInfo!!.bedge == 1){
             //   proImg.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
                proImg.setImageResource(R.drawable.ic_baseline_workspace_premium_24)
                proTxt.text = profile.currentPlanInfo!!.planTitle
            }else{
               // proImg.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
                proImg.setImageResource(R.drawable.free_user_badge)
                proTxt.text = profile.currentPlanInfo!!.planTitle
            }*/

            when (profile.currentPlanInfo!!.planId) {
                2 -> {
                    layoutImageUserNewBinding.proTxt.text = profile.currentPlanInfo!!.planTitle
                    layoutImageUserNewBinding.proImg.setImageResource(R.drawable.special_badge)
                }

                3 -> {
                    layoutImageUserNewBinding.proTxt.text = profile.currentPlanInfo!!.planTitle
                    layoutImageUserNewBinding.proImg.setImageResource(R.drawable.standers_badge)
                }

                4 -> {
                    layoutImageUserNewBinding.proTxt.text = profile.currentPlanInfo!!.planTitle
                    layoutImageUserNewBinding.proImg.setImageResource(R.drawable.ic_baseline_workspace_premium_24)
                    //  proImg.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
                }

                5 -> {
                    layoutImageUserNewBinding.proTxt.text = profile.currentPlanInfo!!.planTitle
                    layoutImageUserNewBinding.proImg.setImageResource(R.drawable.featured_badge)
                }

                else -> {
                    layoutImageUserNewBinding.proTxt.text = profile.currentPlanInfo!!.planTitle
                    layoutImageUserNewBinding.proImg.setImageResource(R.drawable.free_user_badge)
                }
            }

            Log.d(
                "Profile_frag",
                "isprofileprivate: " + profile.currentPlanInfo!!.isProfilePrivateHidden
            )
            if (profile.isprofileprivate != 0 && !profile.connection_status.equals("connected")) {
                statsViewNewBinding.tvTwitter111.isClickable = false
                statsViewNewBinding.tvInstagram111.isClickable = false
                statsViewNewBinding.tvYoutube111.isClickable = false
                statsViewNewBinding.userPicsRv.isClickable = false

                ///  rl_blur.setBackgroundResource(R.drawable.blur_effect)
                statsViewNewBinding.llPrivetHide.visibility = View.GONE
                binding.ivBlockUser.visibility = View.GONE
                statsViewNewBinding.ivChat1Lay.visibility = View.GONE
                statsViewNewBinding.llThreeDote.visibility = View.GONE
                statsViewNewBinding.rlBlur.visibility = View.VISIBLE
                ///  openBottomSheet("Privet",profile.userId.toInt())

                if (profile.connection_status.equals("not_connected")) {
                    statsViewNewBinding.tvPrivetSentRecive.text =
                        getString(R.string.you_not_connect)
                    statsViewNewBinding.btnConnectSendPrivet.visibility = View.VISIBLE
                    statsViewNewBinding.llPrivetReciev.visibility = View.GONE
                } else if (profile.connection_status.equals("request_sent") || profile.connection_status.equals(
                        "direct_message_sent"
                    )
                ) {
                    statsViewNewBinding.tvPrivetSentRecive.text =
                        getString(R.string.you_sent_connection_request)
                    statsViewNewBinding.btnConnectSendPrivet.visibility = View.GONE
                    statsViewNewBinding.llPrivetReciev.visibility = View.GONE
                } else if (profile.connection_status.equals("request_received") || profile.connection_status.equals(
                        "direct_message_received"
                    )
                ) {
                    statsViewNewBinding.tvPrivetSentRecive.text =
                        getString(R.string.you_receiver_connection_request)
                    statsViewNewBinding.btnConnectSendPrivet.visibility = View.GONE
                    statsViewNewBinding.llPrivetReciev.visibility = View.VISIBLE
                }

                return
            } else {
                statsViewNewBinding.ivChat1Lay.visibility = View.VISIBLE
                statsViewNewBinding.llThreeDote.visibility = View.VISIBLE
                binding.ivBlockUser.visibility = View.VISIBLE
                statsViewNewBinding.llPrivetHide.visibility = View.VISIBLE
                statsViewNewBinding.rlBlur.visibility = View.GONE

                statsViewNewBinding.tvTwitter111.isClickable = true
                statsViewNewBinding.tvInstagram111.isClickable = true
                statsViewNewBinding.tvYoutube111.isClickable = true
                statsViewNewBinding.userPicsRv.isClickable = true
            }

            statsViewNewBinding.tvUserLocation1.text = profile.homeLocation
            statsViewNewBinding.tvHomeTown1.text = profile.homeLocation

            statsViewNewBinding.tvUserLocation1.maxEms = 8

//            ivUserStatus.isSelected = sharedPreferencesUtil.fetchOtherUserProfile(this.profile!!.id).onlineStatus
            statsViewNewBinding.ivUserStatus1.isSelected = profile.status != 0

            var location = ""
            if (profile.currentLocation != "") {
                location = profile.currentLocation + ", "
            }
            if (sharedPreferencesUtil.fetchSettings().unit != 0) {
                statsViewNewBinding.tvUserAway1.text = location + String.format(
                    getString(R.string.text_distance_away), profile.mile
                )
            } else {
                statsViewNewBinding.tvUserAway1.text = location + String.format(
                    getString(R.string.text_distance_away_km), profile.km
                )
            }


            if (profile.ismyprivatealbumaccess) {
                fetchPrivateAlbumOthers()
            } else {
                Log.d("private_Access", "No_media_access")
                userPicRv.visibility = View.GONE
                statsViewNewBinding.addImgBtn.visibility = View.GONE
                statsViewNewBinding.addPrivateImgIc.visibility = View.GONE
                statsViewNewBinding.noPrivateImgLay.visibility = View.VISIBLE
                statsViewNewBinding.noUserPrivateImg.text =
                    "You don't have access to ${profile.name}'s media images"
            }
            //showImage()


            /* if (profile!!.isProMembership) {
                 proTxt.text = "Premium"
             } else {

                 proImg.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
                 proTxt.text = "Free"
             }*/


            layoutImageUserNewBinding.addUserImage.visibility = View.GONE
            statsViewNewBinding.tvUserBio.text = profile.aboutMe
            statsViewNewBinding.rlLocation1.visibility = View.VISIBLE
            statsViewNewBinding.tvUserAway1.visibility = View.VISIBLE
            statsViewNewBinding.becomeProTxt.visibility = View.GONE


            val userName = profile.name


            Log.e("connection_status&", profile.connection_status)
            Log.e("otherUser_Profile", "Pro: ${profile.isProMembership}")
            Log.e("otherUser_Profile", "follower_status: ${profile.follower_status}")
            Log.e("otherUser_Profile", "USer_name: ${profile.name}")
            Log.e("otherUser_Profile", "USer_email: ${profile.email}")
            Log.e("otherUser_Profile", "USer_Privet: ${profile.isprofileprivate}")
            Log.e(
                "otherUser_Profile",
                "available_connection_counts: ${profile.available_connection_counts}"
            )

            Log.e(
                "Your_Profile",
                "Pro: ${sharedPreferencesUtil.fetchUserProfile().isProMembership}"
            )
            Log.e("Your_Profile", "USer_name: ${sharedPreferencesUtil.fetchUserProfile().name}")
            Log.e("Your_Profile", "USer_email: ${sharedPreferencesUtil.fetchUserProfile().email}")
            Log.e(
                "Your_Profile",
                "USer_Privet: ${sharedPreferencesUtil.fetchUserProfile().isprofileprivate}"
            )
            Log.e(
                "Your_Profile",
                "available_connection_counts: ${sharedPreferencesUtil.fetchUserProfile().available_connection_counts}"
            )


            binding.llBtnReceive.visibility = View.GONE
            if (profile.connection_status.equals("not_connected")) {
                binding.ivBlockUser.text = "Sent connect request"
                binding.ivBlockUser.visibility = View.GONE
                statsViewNewBinding.ivChat1.setImageResource(R.drawable.ic_send_request)
                statsViewNewBinding.llThreeDote.visibility = View.GONE
                //  ivChat1.visibility = View.GONE
                statsViewNewBinding.ivChat1Lay.visibility = View.VISIBLE
            } else if (profile.connection_status.equals("request_sent") || profile.connection_status.equals(
                    "direct_message_sent"
                )
            ) {
                binding.ivBlockUser.text = "Wait for accept request"
                binding.ivBlockUser.visibility = View.VISIBLE
                statsViewNewBinding.llThreeDote.visibility = View.GONE
                statsViewNewBinding.ivChat1Lay.visibility = View.GONE
            } else if (profile.connection_status.equals("connected")) {
                binding.ivBlockUser.visibility = View.GONE
                statsViewNewBinding.ivChat1.setImageResource(R.drawable.ic_profile_chat)
                statsViewNewBinding.llThreeDote.visibility = View.VISIBLE
                statsViewNewBinding.ivChat1.visibility = View.VISIBLE
                statsViewNewBinding.ivChat1Lay.visibility = View.VISIBLE
            } else if (profile.connection_status.equals("request_received") || profile.connection_status.equals(
                    "direct_message_received")
            ) {
                binding.ivBlockUser.visibility = View.GONE
                statsViewNewBinding.llThreeDote.visibility = View.GONE
                statsViewNewBinding.ivChat1Lay.visibility = View.GONE
                binding.llBtnReceive.visibility = View.VISIBLE
            }

            binding.ivBlockUser.setOnClickListener {
                Log.e("block_Methode", "BTN_block_click")
                if (binding.ivBlockUser.text.toString().equals("Sent connect request")) {
                    Log.e(
                        "block_Methode",
                        "BTN_block_click: ${sharedPreferencesUtil.fetchUserProfile().available_connection_counts}"
                    )
                    showSentReqDialog(profile)
                } else if (binding.ivBlockUser.text.toString().equals("Action")) {
                    openActionBottomSheet()
                } else {
                    if (profile.connection_status.equals("direct_message_sent")){
                       /* Toast.makeText(
                            requireContext(),
                           "Go to Chat",
                            Toast.LENGTH_SHORT
                        ).show()*/
                        goToChat()
                    }else{
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.you_sent_connection_request),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }

            binding.btnReqCancel.setOnClickListener {
                // Toast.makeText(requireContext(),"Cancel connection req",Toast.LENGTH_SHORT).show()
                if (profile.connection_status.equals("direct_message_received")) {
                    goToChat()
                } else {
                    declineConnectionRequest(profile.userId.toInt())
                }
            }

            binding.btnReqConnect.setOnClickListener {
                if (profile.connection_status.equals("direct_message_received")) {
                    goToChat()
                } else {
                    acceptConnectionRequest(profile.userId.toInt())
                }
            }


            if (profile.fistingId.isEmpty() && profile.generalIntercourse.isEmpty() && profile.condomPreference.isEmpty() && profile.myIdentity.isEmpty()) {
                statsViewNewBinding.llPrefText.visibility = View.GONE
                statsViewNewBinding.llPreferencesMain.visibility = View.GONE
            } else {
                statsViewNewBinding.llPrefText.visibility = View.VISIBLE
                statsViewNewBinding.llPreferencesMain.visibility = View.VISIBLE

                if (profile.fistingId.isNotEmpty()) {
                    statsViewNewBinding.llHandballPref.visibility = View.VISIBLE
                    statsViewNewBinding.handballPrefValu.text = profile.fistingId
                } else {
                    statsViewNewBinding.llHandballPref.visibility = View.GONE
                }

                if (profile.generalIntercourse.isNotEmpty()) {
                    statsViewNewBinding.llIntercours.visibility = View.VISIBLE
                    statsViewNewBinding.generalInterPrefValu.text = profile.generalIntercourse
                } else {
                    statsViewNewBinding.llIntercours.visibility = View.GONE
                }

                if (profile.condomPreference.isNotEmpty()) {
                    statsViewNewBinding.llSexPractice.visibility = View.VISIBLE
                    statsViewNewBinding.condomPrefValu.text = profile.condomPreference
                } else {
                    statsViewNewBinding.llSexPractice.visibility = View.GONE
                }
                Log.d("myIdentity%: ", profile.myIdentity)
                if (profile.myIdentity.isNotEmpty() && !profile.myIdentity.equals("")) {
                    statsViewNewBinding.tvIdentityHeading.visibility = View.VISIBLE
                    statsViewNewBinding.intoIdentityPrefValu.visibility = View.VISIBLE
                    val input = profile.myIdentity

                    // Step 1: Remove the first comma and everything before it (if necessary)
                    val modifiedInput = input.substringAfter(", ")

                    // Step 2: Split the modified string into parts
                    val parts = modifiedInput.split(", ")

                    // Step 3: Prepend `#` to each item
                    val modifiedParts = parts.map { "#$it" }

                    // Step 4: Join them back together
                    val output = modifiedParts.joinToString(", ")
                    Log.d("output*1: ", output)
                    statsViewNewBinding.intoIdentityPrefValu.text = output
                } else {
                    statsViewNewBinding.tvIdentityHeading.visibility = View.GONE
                    statsViewNewBinding.intoIdentityPrefValu.visibility = View.GONE
                }
            }


            layoutImageUserNewBinding.ivShared1.isSelected = profile.isMyPrivateAlbumSharedWithUser
            // rlShared1.visibility = View.VISIBLE

        }

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

    private fun goToChat() {
        val conversation = conversation()
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag, "Notification: Conversation1: $conversation"
        )
        startActivity(Intent(Constants.Intent.Chat).putExtras(Bundle().apply {
            putString(Constants.IntentDataKeys.Conversation, conversation)
            putString(
                Constants.IntentDataKeys.UserImage,
                if (profile!!.images.isNotEmpty()) profile!!.images[0].imageThumb else ""
            )
            putString(
                Constants.IntentDataKeys.connection_status,
                if (profile!!.connection_status.isNotEmpty()) profile!!.connection_status else ""
            )

            putBoolean(
                Constants.IntentDataKeys.HasSharedPrivateAlbumWithMe,
                profile!!.ismyprivatealbumaccess
            )
            putBoolean("firstTime", true)
            putBoolean(
                Constants.IntentDataKeys.IsMyPrivateAlbumSharedWithUser,
                profile!!.isMyPrivateAlbumSharedWithUser
            )
        }))
    }

    fun acceptConnectionRequest(userId: Int?) {
        if (null == mPresenter) mPresenter = ConnectionRequestPresenter(presenter)

        run {
            showProgressView(rlProgressBar)
            mPresenter!!.addConnectionRequestObject(RequestConnectionRequest().apply {
                sender_id = userId!!
            })
            mPresenter!!.callRetrofit(ConstantsApi.ACCEPT_CONNECTION_REQUEST)
        }
    }

    fun declineConnectionRequest(userId: Int) {
        if (null == mPresenter) mPresenter = ConnectionRequestPresenter(presenter)

        run {
            showProgressView(rlProgressBar)
            mPresenter!!.addConnectionRequestObject(RequestConnectionRequest().apply {
                sender_id = userId
            })
            mPresenter!!.callRetrofit(ConstantsApi.DECLINE_CONNECTION_REQUEST)
        }
    }

    fun unfriendConnectionRequestCall(userId: Int) {
        if (null == mPresenter) mPresenter = ConnectionRequestPresenter(presenter)

        run {
            showProgressView(rlProgressBar)
            mPresenter!!.unfriendConnectionRequestObject(UnfriendConnectionReq().apply {
                user_id = userId
            })
            mPresenter!!.callRetrofit(ConstantsApi.UNFRIEND_CONNECTION_REQUEST)
        }
    }

    private fun favouriteunfavouriteuser() {
        if (null == favoritePresenter) favoritePresenter = FavoriteUserPresenter(favpresenter)

        run {
            favoritePresenter!!.favouriteunfavouriteuser(MyFavoriteUserReq().apply {
                favourite_user_id = profile!!.userId.toInt()
            })
            favoritePresenter!!.callRetrofit(ConstantsApi.USER_FAVOURIT_UNFAVOURIT)
        }
    }


    private fun followToUser() {
        if (null == favoritePresenter) favoritePresenter = FavoriteUserPresenter(favpresenter)

        run {
            favoritePresenter!!.followUnfollowUser(FollowUnfollowUserReq().apply {
                follower_id = profile!!.userId.toInt()
            })
            favoritePresenter!!.callRetrofit(ConstantsApi.FOLLOW_TO_USER)
        }
    }


    private fun unFollowUser() {
        if (null == favoritePresenter) favoritePresenter = FavoriteUserPresenter(favpresenter)

        run {
            favoritePresenter!!.followUnfollowUser(FollowUnfollowUserReq().apply {
                follower_id = profile!!.userId.toInt()
            })
            favoritePresenter!!.callRetrofit(ConstantsApi.UNFOLLOW_USER)
        }
    }


    private fun showSentReqDialog(profile: ResponseUserData) {

        Log.d("run1111", "run")
        Log.e(
            "showSentReqDialog",
            "direct_Count: ${sharedPreferencesUtil.fetchUserProfile().available_connection_counts}"
        )
        Log.e("showSentReqDialog", "direct_Count22: ${profile.available_connection_counts}")

        val dialog = Dialog(requireContext())

        dialog.setCancelable(false)

        //val activity = context as AppCompatActivity

        val view = layoutInflater.inflate(R.layout.dialog_sent_request, null)

        dialog.setContentView(view)

        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation

        if (dialog.window != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnCancel = view.findViewById<Button>(R.id.dialog_btn_cancel)
        val btnSentMsg = view.findViewById<Button>(R.id.dialog_btn_sent_massage)
        val btnSentDirectReq = view.findViewById<Button>(R.id.dialog_btn_direct_sent)
        val tv_dialog_content = view.findViewById<TextView>(R.id.dialog_content)

        if (sharedPreferencesUtil.fetchUserProfile().available_connection_counts == -1) {
            tv_dialog_content.text = "Unlimited available direct message connection request"
        } else {
            tv_dialog_content.text =
                "${sharedPreferencesUtil.fetchUserProfile().available_connection_counts} available direct message connection request"
        }

        btnSentMsg.visibility = View.VISIBLE/* if (sharedPreferencesUtil.fetchUserProfile().isProMembership){
            btnSentMsg.visibility = View.VISIBLE
        }else{
            btnSentMsg.visibility = View.GONE
        }*/

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        btnSentDirectReq.setOnClickListener {
            ///   Toast.makeText(requireContext(),"sent direct request",Toast.LENGTH_SHORT).show()
            sendConnectionReq(profile.userId.toInt(), 0, "")
            dialog.dismiss()
        }
        btnSentMsg.setOnClickListener {
            if (sharedPreferencesUtil.fetchUserProfile().available_connection_counts > 0 || sharedPreferencesUtil.fetchUserProfile().available_connection_counts == -1) {
                /// Toast.makeText(requireContext(),"sent direct massage",Toast.LENGTH_SHORT).show()
                openBottomSheet("MSG", profile.userId.toInt())
                dialog.dismiss()
            } else {
                Toast.makeText(
                    requireContext(),
                    "You don't have available sent direct message!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        dialog.show()
    }

    private fun showAlertDisconnectDialog(type: String) {
        Log.e("chatRoomId*&", "chatRoomId:- ${profile!!.directMessage}")
        val dialog = Dialog(userProfileActivity)

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

        if (type.equals("disconnect")) {
            dialogContent.text =
                "Are you sure you want to unfriend ${profile!!.name}? You will no longer be connected with them."
        } else if (type.equals("block")) {
            dialogContent.text =
                "Are you sure you want to block ${profile!!.name}? They will no longer be able to contact you or view your profile."
        }

        cancelBtn.setOnClickListener {
            dialog.cancel()
        }

        confirm.setOnClickListener {
            if (type.equals("disconnect")) {
                if (profile!!.directMessage != null && profile!!.directMessage!!.isNotEmpty()) {
                    ///  val chatRoomId = profile!!.senderId.toString() + "-" + profile!!.receiverId.toString()

                    val chatRoomId = Constants.ConvoId.id(
                        profile!!.senderId!!, profile!!.receiverId!!
                    )
                    Log.e("chatRoomId*&", "chatRoomId:- $chatRoomId")
                    unFriendFromFirebase(profile!!.userId, chatRoomId)
                    unfriendConnectionRequestCall(profile!!.userId.toInt())
                } else {
                    Log.e("chatRoomId*&", "only unfriend by Api")
                    unfriendConnectionRequestCall(profile!!.userId.toInt())
                }
            } else if (type.equals("block")) {
                userProfileActivity.blockUser()
                blockUserChat()
            }
            /// unfriendConnectionRequestCall(profile!!.userId.toInt())
            dialog.dismiss()
        }

        dialog.show()

    }

    fun unFriendFromFirebase(otherUserId: String?, chatRoomId: String) {
        var chatListDb: DatabaseReference =
            FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference(Constants.Firebase.ChatDB)
                .child("ChatList")

        val chatList = chatListDb.child(sharedPreferencesUtil.userId()).child(chatRoomId)
        val otherChatList = chatListDb.child(otherUserId.toString()).child(chatRoomId)
        UpdateChatListConnected(chatList)
        UpdateChatListConnected(otherChatList)
    }

    private fun UpdateChatListConnected(chatList: DatabaseReference) {
        Log.e("UpdateChatListConnected", ": No")
        chatList.child("isUserConnected").setValue("no")
    }

    private fun blockUserChat() {

        val chatDb =
            FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference("Live")
                .child("UserChat")

        val fellowId = profile!!.userId
        val userId = sharedPreferencesUtil.fetchUserProfile().userId

        Log.d("Ids", "$fellowId     $userId")

        chatDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {
                Log.e("block_Methode", "blockUserChat")

                if (snap.exists()) {

                    for (chats in snap.children) {
                        Log.e("block_Methode", "userChat_in")

                        if (chats.key.toString().contains(userId) && chats.key.toString()
                                .contains(fellowId)
                        ) {

                            chatDb.child(chats.key.toString()).child("blockedBy").setValue(userId)
                        }
                    }

                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }


        })

    }

    private fun fetchPrivateAlbum() {
        Log.e("API_CAll", "fetchPrivateAlbum")
        if (null == mPresenterP) mPresenterP = PrivateAlbumImagesPresenter(presenterP)

        run {
            mPresenterP!!.callRetrofit(ConstantsApi.PRIVATE_ALBUM)
        }
    }

    private fun sendConnectionReq(userId: Int, directMsg: Int, Massage: String) {
        if (null == mPresenterP) mPresenterP = PrivateAlbumImagesPresenter(presenterP)

        run {
            mPresenterP!!.addSendConnectionObject(SendConnectionRequest().apply {
                receiver_id = userId
                is_direct_message = directMsg
                direct_message = Massage
            })
            Log.e("Call_Api", "sendConnection_Data")
            showProgressView(rlProgressBar)
            mPresenterP!!.callRetrofit(ConstantsApi.SEND_CONNECTION_REQUEST)
        }

    }

    private fun fetchPrivateAlbumOthers() {
        Log.e("API_CAll", "fetchPrivateAlbumOthers")
        if (null == mPresenterP) mPresenterP = PrivateAlbumImagesPresenter(presenterP)

        run {
            mPresenterP!!.addObjectForPrivateAlbum(userId)
            mPresenterP!!.callRetrofit(ConstantsApi.OTHER_PRIVATE_ALBUM)
        }
    }


    private fun showImage() {
        DisplayImage.with(ArchitectureApp.instance!!.baseContext)
            .load(if (profile!!.images.isNotEmpty()) profile!!.images[0].imagePath else "")
            .placeholder(R.drawable.meetupsfellow_transpatent).shape(DisplayImage.Shape.DEFAULT)
            .into(layoutImageUserNewBinding.ivUserProfile1).build()
    }

    fun getUTCTime(): String? {
        /// val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS")
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return formatter.format(Date())
    }


    fun Toast.showCustomToast(message: String, activity: Activity) {
        val layout = activity.layoutInflater.inflate(
            R.layout.custom_toast, activity.findViewById(R.id.toast_container)
        )

        // set the text of the TextView of the message
        val textView = layout.findViewById<TextView>(R.id.toast_custom)
        textView.text = message

        // use the application extension function
        this.apply {
            // setGravity(Gravity.BOTTOM, 0, 40)
            duration = Toast.LENGTH_LONG
            view = layout
            show()
        }
    }


    private fun setupBillingClient() {
        mBillingClient = BillingClient.newBuilder(requireContext())
            .enablePendingPurchases() // Useful for physical stores

            .build()


        mBillingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    //val purchasesResult = mBillingClient?.queryPurchases(BillingClient.SkuType.SUBS)
                    val purchasesResult =
                        mBillingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS,
                            PurchasesResponseListener { billingResult, list ->

                                if (list != null) {

                                    for (purchase in list) {

                                        if (purchase.isAcknowledged) {
                                            when (purchase.skus) {/*skuname ->  {
                                                    Log.e(Config.APPTAG, " Product "+purchase.sku+" is subscribed")
                                                    // The subscription sku is found and active so then purchases to true in prefs
                                                    prefs.purchased = true
                                                }*/
                                            }
                                        }
                                    }
                                }

                            })
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                // TODO Note: It's strongly recommended that you implement your own connection retry policy and override the onBillingServiceDisconnected() method. Make sure you maintain the BillingClient connection when executing any methods.

            }
        })
    }

    fun querySubscriptions() {


        mBillingClient = BillingClient.newBuilder(ArchitectureApp.instance!!)
            .setListener { billingResult, purchases ->
                Log.e("billingResult", "" + billingResult.responseCode)
                // val purchasesResult = mBillingClient?.queryPurchases(BillingClient.SkuType.SUBS)


                when (billingResult.responseCode) {

                    BillingClient.BillingResponseCode.OK -> {
                        //val purchasesResult =
                        //    mBillingClient?.queryPurchases(BillingClient.SkuType.SUBS)

                        val purchasesResult =
                            mBillingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS,
                                PurchasesResponseListener { billingResult, list ->

                                    if (list != null) {
                                        for (purchase in list) {
                                            if (purchase.isAcknowledged) {

                                                when (purchase.skus) {

                                                }
                                            }
                                        }

                                    }

                                })


                        // Init all the purchases to false in the shared preferences (security prevention)
                        // Retrieve and loop all the purchases done by the user
                        // Update all the boolean related to the purchases done in the shared preferences
                        /*if (purchasesResult?.purchasesList != null) {
                            for (purchase in purchasesResult.purchasesList!!) {
                                if (purchase.isAcknowledged) {

                                    when (purchase.sku) {

                                    }
                                }
                            }

                        }*/


                    }

                    BillingClient.BillingResponseCode.ERROR -> {


                    }

                    BillingClient.BillingResponseCode.USER_CANCELED -> {
//

                    }

                    BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {

                    }


                }
            }.enablePendingPurchases().build()


        mBillingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                LogManager.logger.i(ArchitectureApp.instance!!.tag, "Connection ended.")
            }


            override fun onBillingSetupFinished(billingResult: BillingResult) {
                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.ERROR -> {
                        return
                    }


                    BillingClient.BillingResponseCode.OK -> {

                        //val purchasesResult =
                        //    mBillingClient?.queryPurchases(BillingClient.SkuType.SUBS)
                        val purchasesResult =
                            mBillingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS,
                                PurchasesResponseListener { result, purchasesList ->

                                    if (purchasesList != null) {
                                        for (purchase in purchasesList) {
                                            if (purchase.isAcknowledged) {
                                                Log.e("testing when if ", purchase.skus.toString())
                                                when (purchase.skus.toString()) {

                                                    "one_month_subscription_new" -> {

                                                        Log.e(
                                                            "testing when",
                                                            " Product " + purchase.skus + " is subscribed 1"
                                                        )

                                                    }

                                                    "three_month_subscription_new" -> {

                                                        Log.e(
                                                            "testing when",
                                                            " Product " + purchase.skus + " is subscribed 3"
                                                        )
                                                    }
                                                }


                                            }
                                        }

                                    }

                                })
                        // Init all the purchases to false in the shared preferences (security prevention)
                        // Retrieve and loop all the purchases done by the user
                        // Update all the boolean related to the purchases done in the shared preferences
                        /*if (purchasesResult?.purchasesList != null) {
                            for (purchase in purchasesResult.purchasesList!!) {
                                if (purchase.isAcknowledged) {
                                    Log.e("testing when if ", purchase.sku)
                                    when (purchase.sku) {

                                        "one_month_subscription_new" -> {

                                            Log.e(
                                                "testing when",
                                                " Product " + purchase.sku + " is subscribed 1"
                                            )

                                        }
                                        "three_month_subscription_new" -> {

                                            Log.e(
                                                "testing when",
                                                " Product " + purchase.sku + " is subscribed 3"
                                            )
                                        }
                                    }


                                }
                            }

                        }*/


                    }
                }

            }
        })


    }

}