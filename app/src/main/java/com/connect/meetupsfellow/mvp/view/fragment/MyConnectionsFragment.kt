package com.connect.meetupsfellow.mvp.view.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import com.connect.meetupsfellow.mvp.connector.activity.ConnectionRequestConnector
import com.connect.meetupsfellow.mvp.presenter.activity.ConnectionRequestPresenter
import com.connect.meetupsfellow.mvp.view.activities.SplashActivity
import com.connect.meetupsfellow.mvp.view.adapter.MyConnectionsAdapter
import com.connect.meetupsfellow.retrofit.request.UnfriendConnectionReq
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class MyConnectionsFragment : CustomFragment() {


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
            hideProgressView(rl_progress)
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {

            when (type) {
                ConstantsApi.GET_MY_CONNECTIONS -> {
                    myConnectionsAdapter.add(
                        requireContext(), this@MyConnectionsFragment, response.getMyConnectionList
                    )
                    myConnectionsAdapter.notifyDataSetChanged()
                    Log.e(
                        "ConnectRequest",
                        "getMyConnectionData_Done- ${response.getMyConnectionList.size}"
                    )
                    updateUi()
                }

                ConstantsApi.UNFRIEND_CONNECTION_REQUEST -> {
                    Toast.makeText(
                        requireContext(), response.message, Toast.LENGTH_SHORT
                    ).show()


                    hideProgressView(rl_progress)
                    getMyConnectionRequest()
                    Log.e(
                        "CancelConnectRequest", "${response.message}"
                    )
                }

                else -> {
                    Log.e("ConnectRequest", "MY_Api_Else")
                }
            }
            hideProgressView(rl_progress)
        }
    }

    private var mPresenter: ConnectionRequestConnector.PresenterOps? = null

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var apiConnect: ApiConnect
    lateinit var mAdManagerAdView: AdManagerAdView
    private val myConnectionsAdapter = MyConnectionsAdapter()
    lateinit var rvMyConnection: RecyclerView
    lateinit var tvNoUser_Request: TextView
    lateinit var no_connect_img: ImageView
    lateinit var rl_progress: LinearLayout
    lateinit var animLogo: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_connections, container, false)
        ArchitectureApp.component!!.inject(this@MyConnectionsFragment)
        // Inflate the layout for this fragment

        rvMyConnection = view.findViewById(R.id.rvMyConnection)
        mAdManagerAdView = view.findViewById(R.id.adManagerAdView_Myconnection)
        no_connect_img = view.findViewById(R.id.no_connect_img_my)
        tvNoUser_Request = view.findViewById(R.id.tvNoUser_Request_my)
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
        with(rvMyConnection) {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                context, RecyclerView.VERTICAL, false
            )
            adapter = myConnectionsAdapter

            addItemDecoration(
                androidx.recyclerview.widget.DividerItemDecoration(
                    context, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
                )
            )
            // Set the adapter to your RecyclerView
            rvMyConnection.adapter = adapter
        }

        //  getMyConnectionRequest()
    }

    private fun getMyConnectionRequest() {
        if (null == mPresenter) mPresenter = ConnectionRequestPresenter(presenter)

        run {
            showProgressView(rl_progress)
            mPresenter!!.callRetrofit(ConstantsApi.GET_MY_CONNECTIONS)
        }
    }

    fun unfriendConnectionRequestCall(userId: Int) {
        if (null == mPresenter) mPresenter = ConnectionRequestPresenter(presenter)

        run {
            showProgressView(rl_progress)
            mPresenter!!.unfriendConnectionRequestObject(UnfriendConnectionReq().apply {
                user_id = userId
            })
            mPresenter!!.callRetrofit(ConstantsApi.UNFRIEND_CONNECTION_REQUEST)
        }
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

    fun showUnfriendDialog(userId: Int, type: String, chatRoomId: String, userName: String) {

        val dialog = Dialog(requireContext())

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

        dialogHead.text = "Unfriend Connection"
        dialogContent.text =
            "Are you sure you want to unfriend $userName? You will no longer be connected with them."

        cancelBtn.setOnClickListener {
            dialog.cancel()
        }

        confirm.setOnClickListener {

            if (type == "directMessage" && chatRoomId.isNotEmpty()) {

                dialog.dismiss()
                unFriendFromFirebase(userId.toString(), chatRoomId)
                unfriendConnectionRequestCall(userId)
            } else {
                dialog.dismiss()
                unfriendConnectionRequestCall(userId)
            }
        }

        dialog.show()
    }

    private fun UpdateChatListConnected(chatList: DatabaseReference) {
        Log.e("UpdateChatListConnected", ": No")
        chatList.child("isUserConnected").setValue("no")
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
        when (myConnectionsAdapter.itemCount > 0) {
            true -> {
                rvMyConnection.visibility = View.VISIBLE
                tvNoUser_Request.visibility = View.GONE
                no_connect_img.visibility = View.GONE
            }

            false -> {
                rvMyConnection.visibility = View.GONE
                tvNoUser_Request.visibility = View.VISIBLE
                no_connect_img.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()

        var lasttrackTime = sharedPreferencesUtil.featchTimmerTime()
        var currentbackTime: Long = System.currentTimeMillis()
        Log.e("lasttrackTime", lasttrackTime)
        LogManager.logger.i(ArchitectureApp.instance!!.tag, "onResume ::: " + lasttrackTime)
        getMyConnectionRequest()

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