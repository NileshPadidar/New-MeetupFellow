package com.connect.meetupsfellow.mvp.view.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.databinding.ActivityMultiUserViewLikeBinding
import com.connect.meetupsfellow.global.utils.ToolbarUtils
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.NotificationConnector
import com.connect.meetupsfellow.mvp.presenter.activity.NotificationPresenter
import com.connect.meetupsfellow.mvp.view.adapter.MultiUserViewLikeAdapter
import com.connect.meetupsfellow.mvp.view.dialog.AlertBuyPremium
import com.connect.meetupsfellow.retrofit.request.RequestReadNotification
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.VisitorsList
import com.google.gson.Gson


class MultiUserViewLikeActivity : CustomAppActivityCompatViewImpl() {

    private val presenter = object : NotificationConnector.RequiredViewOps {
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
            when (type) {
                ConstantsApi.GET_PROFILE_VISITORS_LIST -> {
                    LogManager.logger.i(
                        ArchitectureApp.instance!!.tag,
                        "Response_Notification33::: ${Gson().toJson(response.isShow)}"
                    )
                    if (response.visitorsLists != null) {
                        visitorsList = response.visitorsLists
                        ViewLikeAdapter.add(this@MultiUserViewLikeActivity, visitorsList)
                        Log.e("MultiUserActivity", "response: ${visitorsList.size}")
                    }
                    if (response.isShow == 1) {
                        binding!!.llViewList.visibility = View.VISIBLE
                        binding!!.llFreeUser.visibility = View.GONE
                    } else {
                        binding!!.llViewList.visibility = View.GONE
                        binding!!.llFreeUser.visibility = View.VISIBLE
                    }
                }

                ConstantsApi.GET_LIKE_USER_LIST -> {

                    if (response.profile_like_user_lists != null) {
                        visitorsList = response.profile_like_user_lists
                        ViewLikeAdapter.add(this@MultiUserViewLikeActivity, visitorsList)
                        Log.e(
                            "MultiUserActivity", "GET_LIKE_USER_LIST_response: ${visitorsList.size}"
                        )
                        binding!!.llViewList.visibility = View.VISIBLE
                        binding!!.llFreeUser.visibility = View.GONE
                    } else {
                        binding!!.llViewList.visibility = View.GONE
                        binding!!.llFreeUser.visibility = View.VISIBLE
                    }
                }

                ConstantsApi.GET_EVENT_INTEREST_USER_LIST -> {

                    if (response.event_interested_user_lists != null) {
                        visitorsList = response.event_interested_user_lists
                        ViewLikeAdapter.add(this@MultiUserViewLikeActivity, visitorsList)
                        Log.e(
                            "MultiUserActivity", "event_interested_user_lists: ${visitorsList.size}"
                        )
                        binding!!.llViewList.visibility = View.VISIBLE
                        binding!!.llFreeUser.visibility = View.GONE
                    } else {
                        binding!!.llViewList.visibility = View.GONE
                        binding!!.llFreeUser.visibility = View.VISIBLE
                    }
                }

                ConstantsApi.GET_EVENT_LIKE_USER_LIST -> {

                    if (response.event_liked_user_lists != null) {
                        visitorsList = response.event_liked_user_lists
                        ViewLikeAdapter.add(this@MultiUserViewLikeActivity, visitorsList)
                        Log.e(
                            "MultiUserActivity", "event_liked_user_lists: ${visitorsList.size}"
                        )
                        binding!!.llViewList.visibility = View.VISIBLE
                        binding!!.llFreeUser.visibility = View.GONE
                    } else {
                        binding!!.llViewList.visibility = View.GONE
                        binding!!.llFreeUser.visibility = View.VISIBLE
                    }
                }

                ConstantsApi.GET_FOLLOWING_VISITORS_LIST -> {

                    if (response.following_user_lists != null) {
                        visitorsList = response.following_user_lists
                        ViewLikeAdapter.add(this@MultiUserViewLikeActivity, visitorsList)
                        Log.e(
                            "MultiUserActivity", "GET_LIKE_USER_LIST_response: ${visitorsList.size}"
                        )
                        binding!!.llViewList.visibility = View.VISIBLE
                        binding!!.llFreeUser.visibility = View.GONE
                    } else {
                        binding!!.llViewList.visibility = View.GONE
                        binding!!.llFreeUser.visibility = View.VISIBLE
                    }
                }

                ConstantsApi.GET_FAVOURITE_USER_LIST -> {

                    if (response.profile_favourite_user_lists != null) {
                        visitorsList = response.profile_favourite_user_lists
                        ViewLikeAdapter.add(this@MultiUserViewLikeActivity, visitorsList)
                        Log.e("MultiUserActivity", "response: ${visitorsList.size}")
                        binding!!.llViewList.visibility = View.VISIBLE
                        binding!!.llFreeUser.visibility = View.GONE
                    } else {
                        binding!!.llViewList.visibility = View.GONE
                        binding!!.llFreeUser.visibility = View.VISIBLE
                    }
                }

                else -> {
                    Log.e("MultiUserActivity", "API__Elss")
                    binding!!.llViewList.visibility = View.GONE
                    binding!!.llFreeUser.visibility = View.VISIBLE
                }
            }
            hideProgressView(progressBar)
        }

    }

    private var mPresenter: NotificationConnector.PresenterOps? = null
    private val ViewLikeAdapter = MultiUserViewLikeAdapter()
    private var visitorsList = arrayListOf<VisitorsList>()
    private var binding: ActivityMultiUserViewLikeBinding? = null
    private lateinit var progressBar: LinearLayout

    var notificationIdd = 0
    var type = ""
    var title = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this@MultiUserViewLikeActivity)
        binding = ActivityMultiUserViewLikeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        /// setContentView(R.layout.activity_multi_user_view_like)
        //   setupActionBar("Who's Viewed", true)
        progressBar = binding!!.includedLoading.rlProgress
        startLoadingAnim()

        if (intent != null && intent.hasExtra(Constants.IntentDataKeys.NotificationId)) {
            notificationIdd = intent.getIntExtra(Constants.IntentDataKeys.NotificationId, 0)
            type = intent.getStringExtra(Constants.IntentDataKeys.Type).toString()

            Log.e("MultiUserActivity", "notificationId: $notificationIdd")
            Log.e("MultiUserActivity", "type: $type")
            init()
        } else {
            binding!!.llViewList.visibility = View.GONE
            binding!!.llFreeUser.visibility = View.VISIBLE
        }
        when (type) {
            Constants.Notification.ProfileLike -> {
                ToolbarUtils.setupActionBar(
                    this,
                    binding!!.header.toolbar,
                    "Who's Like",
                    binding!!.header.tvTitle,
                    showBackArrow = true,
                    activity = this
                )
            }

            Constants.Notification.Favourite -> {
                ToolbarUtils.setupActionBar(
                    this,
                    binding!!.header.toolbar,
                    "Who's Favourite",
                    binding!!.header.tvTitle,
                    showBackArrow = true,
                    activity = this
                )
            }

            Constants.Notification.eventInterest -> {
                ToolbarUtils.setupActionBar(
                    this,
                    binding!!.header.toolbar,
                    "Event Interested",
                    binding!!.header.tvTitle,
                    showBackArrow = true,
                    activity = this
                )
            }

            Constants.Notification.EventLike -> {
                ToolbarUtils.setupActionBar(
                    this,
                    binding!!.header.toolbar,
                    "Event Likes",
                    binding!!.header.tvTitle,
                    showBackArrow = true,
                    activity = this
                )
            }

            Constants.Notification.Following -> {
                ToolbarUtils.setupActionBar(
                    this,
                    binding!!.header.toolbar,
                    "Who's Following",
                    binding!!.header.tvTitle,
                    showBackArrow = true,
                    activity = this
                )
            }

            else -> {
                ToolbarUtils.setupActionBar(
                    this,
                    binding!!.header.toolbar,
                    "Who's Viewed",
                    binding!!.header.tvTitle,
                    showBackArrow = true,
                    activity = this
                )
            }
        }/*ToolbarUtils.setupActionBar(
            this,
            binding!!.header.toolbar,
            title,
            binding!!.header.tvTitle,
            showBackArrow = true,
            activity = this
        )*/


        binding!!.btnPlanPurchaseNow.setOnClickListener {
            AlertBuyPremium.Builder(
                this@MultiUserViewLikeActivity, Constants.DialogTheme.NoTitleBar
            ).build().show()
        }
    }

    private fun startLoadingAnim() {
        val animation = android.view.animation.AnimationUtils.loadAnimation(
            applicationContext, R.anim.blink_anim
        )
        binding!!.includedLoading.animLogo.startAnimation(animation)
    }

    private fun init() {
        with(binding!!.rvUserViewLike) {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                binding!!.rvUserViewLike.context,
                androidx.recyclerview.widget.RecyclerView.VERTICAL,
                false
            )
            adapter = ViewLikeAdapter
            Log.e("MultiUserActivity", "Adapter_Init")
            // below code add a line/view after each position in adapter
            addItemDecoration(
                androidx.recyclerview.widget.DividerItemDecoration(
                    binding!!.rvUserViewLike.context,
                    androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
                )
            )
        }

        when (type) {
            Constants.Notification.Favourite -> {
                getFavouriteUserList()
                Log.e("MultiUserActivity", "Call - Favourite")
            }

            Constants.Notification.User_Viewed_profile -> {
                getVisitorList()
                Log.e("MultiUserActivity", "Call - User_Viewed_profile")
            }

            Constants.Notification.Following -> {
                getFollowingList()
                Log.e("MultiUserActivity", "Call - getFollowingUserList")
            }

            Constants.Notification.ProfileLike -> {
                getLikeUserList()
                Log.e("MultiUserActivity", "Call - getLikeUserList")
            }

            Constants.Notification.eventInterest -> {
                getEventInterestUserList()
                Log.e("MultiUserActivity", "Call - eventInterest")
            }

            Constants.Notification.EventLike -> {
                getEventLikeUserList()
                Log.e("MultiUserActivity", "Call - getEventLikeUserList")
            }

            else -> {
                Log.e("MultiUserActivity", "Going to else")
            }
        }


    }

    private fun getVisitorList() {
        showProgressView(progressBar)
        if (null == mPresenter) mPresenter = NotificationPresenter(presenter)

        run {
            mPresenter!!.getVisitorListObject(RequestReadNotification().apply {
                notificationId = notificationIdd.toString()
            })
            mPresenter!!.callRetrofit(ConstantsApi.GET_PROFILE_VISITORS_LIST)
        }
    }

    private fun getFollowingList() {
        showProgressView(progressBar)
        if (null == mPresenter) mPresenter = NotificationPresenter(presenter)

        run {
            mPresenter!!.getFollowingListObject(RequestReadNotification().apply {
                notificationId = notificationIdd.toString()
            })
            mPresenter!!.callRetrofit(ConstantsApi.GET_FOLLOWING_VISITORS_LIST)
        }
    }

    private fun getFavouriteUserList() {
        showProgressView(progressBar)
        if (null == mPresenter) mPresenter = NotificationPresenter(presenter)

        run {
            mPresenter!!.getFavouriteListObject(RequestReadNotification().apply {
                notificationId = notificationIdd.toString()
            })
            mPresenter!!.callRetrofit(ConstantsApi.GET_FAVOURITE_USER_LIST)
        }
    }

    private fun getLikeUserList() {
        showProgressView(progressBar)
        if (null == mPresenter) mPresenter = NotificationPresenter(presenter)

        run {
            mPresenter!!.getLikeUserListObject(RequestReadNotification().apply {
                notificationId = notificationIdd.toString()
            })
            mPresenter!!.callRetrofit(ConstantsApi.GET_LIKE_USER_LIST)
        }
    }

    private fun getEventInterestUserList() {
        showProgressView(progressBar)
        if (null == mPresenter) mPresenter = NotificationPresenter(presenter)

        run {
            mPresenter!!.getEventInterestListObject(RequestReadNotification().apply {
                notificationId = notificationIdd.toString()
            })
            mPresenter!!.callRetrofit(ConstantsApi.GET_EVENT_INTEREST_USER_LIST)
        }
    }

    private fun getEventLikeUserList() {
        showProgressView(progressBar)
        if (null == mPresenter) mPresenter = NotificationPresenter(presenter)

        run {
            mPresenter!!.getEventLikeListObject(RequestReadNotification().apply {
                notificationId = notificationIdd.toString()
            })
            mPresenter!!.callRetrofit(ConstantsApi.GET_EVENT_LIKE_USER_LIST)
        }
    }

}