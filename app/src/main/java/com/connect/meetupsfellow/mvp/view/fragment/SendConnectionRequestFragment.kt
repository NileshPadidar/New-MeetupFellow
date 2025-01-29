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
import com.connect.meetupsfellow.mvp.connector.activity.ConnectionRequestConnector
import com.connect.meetupsfellow.mvp.presenter.activity.ConnectionRequestPresenter
import com.connect.meetupsfellow.mvp.view.activities.SplashActivity
import com.connect.meetupsfellow.mvp.view.adapter.SendConnectionAdapter
import com.connect.meetupsfellow.retrofit.request.CancelSendConnectionRequest
import com.connect.meetupsfellow.retrofit.request.getConectionRequest
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import javax.inject.Inject

class SendConnectionRequestFragment : CustomFragment() {

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
                ConstantsApi.GET_CONNECTION_REQUEST -> {
                    ///  blockedUsersAdapter.add(response.blockedUsers)
                    sendConnectionAdapter.add(
                        requireContext(),
                        this@SendConnectionRequestFragment,
                        response.getConnectionRequestList
                    )
                    sendConnectionAdapter.notifyDataSetChanged()
                    Log.e(
                        "ConnectRequest",
                        "getConnectionData_Done- ${response.getConnectionRequestList.size}"
                    )
                    updateUi()
                }

                ConstantsApi.CANCEL_SEND_CONNECTION_REQUEST -> {
                    Toast.makeText(
                        requireContext(), response.message, Toast.LENGTH_SHORT
                    ).show()
                    hideProgressView(rl_progress)
                    getConnectionRequest()
                    Log.e(
                        "CancelConnectRequest", "${response.message}"
                    )
                }

                else -> {
                    Log.e("ConnectRequest", "Api_Else")
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
    private val sendConnectionAdapter = SendConnectionAdapter()
    lateinit var rvFragConnectRequest: RecyclerView
    lateinit var tvNoUser_Request: TextView
    lateinit var no_connect_img: ImageView
    lateinit var rl_progress: LinearLayout
    lateinit var animLogo: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_send_connection_request, container, false)

        ArchitectureApp.component!!.inject(this@SendConnectionRequestFragment)
        // Inflate the layout for this fragment

        rvFragConnectRequest = view.findViewById(R.id.rvFragSendConnectRequest)
        mAdManagerAdView = view.findViewById(R.id.adManagerAdView_send_connect)
        no_connect_img = view.findViewById(R.id.send_no_connect_img)
        tvNoUser_Request = view.findViewById(R.id.tvNoUser_Request_send)
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
        with(rvFragConnectRequest) {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                rvFragConnectRequest.context,
                RecyclerView.VERTICAL,
                false
            )
            adapter = sendConnectionAdapter
            addItemDecoration(
                androidx.recyclerview.widget.DividerItemDecoration(
                    rvFragConnectRequest.context,
                    androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
                )
            )
            // Set the adapter to your RecyclerView
            rvFragConnectRequest.adapter = adapter

            //  getConnectionRequest()
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

    private fun getConnectionRequest() {
        if (null == mPresenter) mPresenter = ConnectionRequestPresenter(presenter)

        run {
            showProgressView(rl_progress)
            mPresenter!!.getConnectionRequestObject(getConectionRequest().apply {
                sort_by = "request_sent"
            })
            mPresenter!!.callRetrofit(ConstantsApi.GET_CONNECTION_REQUEST)
        }
    }

    fun cancelConnectionRequest(receiverId_data: Int) {
        if (null == mPresenter) mPresenter = ConnectionRequestPresenter(presenter)

        run {
            showProgressView(rl_progress)
            mPresenter!!.cancelConnectionRequestObject(CancelSendConnectionRequest().apply {
                receiver_id = receiverId_data
            })
            mPresenter!!.callRetrofit(ConstantsApi.CANCEL_SEND_CONNECTION_REQUEST)
        }
    }

    private fun updateUi() {
        when (sendConnectionAdapter.itemCount > 0) {
            true -> {
                rvFragConnectRequest.visibility = View.VISIBLE
                tvNoUser_Request.visibility = View.GONE
                no_connect_img.visibility = View.GONE
            }

            false -> {
                rvFragConnectRequest.visibility = View.GONE
                tvNoUser_Request.visibility = View.VISIBLE
                no_connect_img.visibility = View.VISIBLE
            }
        }
    }

    private fun startLoadingAnim() {

        val animation = android.view.animation.AnimationUtils.loadAnimation(
            context, R.anim.blink_anim
        )

        animLogo.startAnimation(animation)
    }


    override fun onResume() {
        super.onResume()

        var lasttrackTime = sharedPreferencesUtil.featchTimmerTime()
        var currentbackTime: Long = System.currentTimeMillis()
        Log.e("lasttrackTime", lasttrackTime)
        LogManager.logger.i(ArchitectureApp.instance!!.tag, "onResume ::: " + lasttrackTime)
        getConnectionRequest()

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
            ArchitectureApp.instance!!.tag,
            "onPause ::: " + System.currentTimeMillis().toString()
        )

    }

}