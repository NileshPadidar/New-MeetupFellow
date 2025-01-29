package com.connect.meetupsfellow.mvp.view.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.constants.ItemClickStatus
import com.connect.meetupsfellow.databinding.ActivityNotificationBinding
import com.connect.meetupsfellow.developement.interfaces.ApiConnect
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.interfaces.ItemClick
import com.connect.meetupsfellow.global.utils.RecyclerViewClick
import com.connect.meetupsfellow.global.utils.ToolbarUtils
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.NotificationConnector
import com.connect.meetupsfellow.mvp.presenter.activity.NotificationPresenter
import com.connect.meetupsfellow.mvp.view.adapter.NotificationAdapter
import com.connect.meetupsfellow.retrofit.request.RequestReadNotification
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ResponseUserData
import com.google.gson.Gson
import retrofit2.Response
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class NotificationActivity : CustomAppActivityCompatViewImpl() {

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
                ConstantsApi.NOTIFICATION -> {
                    LogManager.logger.i(
                        ArchitectureApp.instance!!.tag,
                        "Response_Notification33::: ${Gson().toJson(response.notification)}"
                    )
                    notificationAdapter.removeAll()
                    notificationAdapter.add(
                        response.notification,
                        sharedPreferencesUtil.fetchUserProfile().isProMembership,
                        sharedPreferencesUtil.fetchUserProfile().currentPlanInfo?.planId ?: 2
                    )
                    updateUi()
                }

                ConstantsApi.CLEAR_NOTIFICATION -> {
                    sharedPreferencesUtil.saveNotificationCount("0")
                    notificationAdapter.removeAll()
                    updateUi()
                }

                ConstantsApi.READ_NOTIFICATION, ConstantsApi.DELETE_NOTIFICATION -> {
                    val count = sharedPreferencesUtil.fetchNotificationCount().toInt()
                    when (count > 0) {
                        true -> sharedPreferencesUtil.saveNotificationCount("${(count - 1)}")
                        false -> {}
                    }
                    fetchNotifications()
                }

                else -> {

                }
            }
            binding!!.swipeRefreshNotification.isRefreshing = false
            hideProgressView(progressBar)
        }

    }

    private val itemClick = object : ItemClick {
        override fun onItemClick(position: Int, status: ItemClickStatus) {
            when (status) {
                ItemClickStatus.Unread -> {
                    when (val id = notificationAdapter.id(position)) {
                        -1 -> universalToast("Notification not found.")
                        else -> {
                            read("$id")
                        }
                    }
                }

                ItemClickStatus.Delete -> {
                    when (val id = notificationAdapter.id(position)) {
                        -1 -> universalToast("Notification not found.")
                        else -> {
                            showProgressView(progressBar)
                            delete("$id")
                        }
                    }
                }

                else -> {

                }
            }
        }
    }

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var apiConnect: ApiConnect

    private val notificationAdapter = NotificationAdapter()

    private var mPresenter: NotificationConnector.PresenterOps? = null
    private var binding: ActivityNotificationBinding? = null
    private lateinit var progressBar: LinearLayout

    private val refresh = SwipeRefreshLayout.OnRefreshListener {
        Handler().postDelayed({
            fetchNotifications()
        }, 100)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this@NotificationActivity)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        // setContentView(R.layout.activity_notification)
        progressBar = binding!!.includedLoading.rlProgress
      //  setupActionBar(getString(R.string.title_notifications), true)
        ToolbarUtils.setupActionBar(
            this,
            binding!!.header.toolbar,
            getString(R.string.title_notifications),
            binding!!.header.tvTitle,
            showBackArrow = true,
            activity = this
        )
        showProgressView(progressBar)
        Log.e("Notification*&", "Activity_Noti")
        startLoadingAnim()
        init()
        callForUserProfile()
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

        RecyclerViewClick.enableClick(itemClick)
    }

    override fun onPause() {
        super.onPause()
        sharedPreferencesUtil.saveTimmerTime(System.currentTimeMillis().toString())
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag,
            "onPause ::: " + System.currentTimeMillis().toString()
        )


        RecyclerViewClick.disableClick()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_notification, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_clear -> {
                when (notificationAdapter.itemCount > 0) {
                    true -> showAlertClear("Are you sure, you want to clear all notifications?")
                    false -> {}
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun init() {
        with(binding!!.rvNotifications) {
            layoutManager = LinearLayoutManager(
                binding!!.rvNotifications.context, RecyclerView.VERTICAL, false
            )
            adapter = notificationAdapter
            addItemDecoration(
                DividerItemDecoration(
                    binding!!.rvNotifications.context, DividerItemDecoration.VERTICAL
                )
            )
        }

        binding!!.swipeRefreshNotification.setOnRefreshListener(refresh)

        Handler().postDelayed({
            fetchNotifications()
        }, 100)
    }

    private fun fetchNotifications() {
        showProgressView(progressBar)
        if (null == mPresenter) mPresenter = NotificationPresenter(presenter)

        run {
            mPresenter!!.callRetrofit(ConstantsApi.NOTIFICATION)
        }
    }

    private fun showAlertClear(message: String) {
        val alertDialog = AlertDialog.Builder(this@NotificationActivity, R.style.MyDialogTheme2)
        // Setting DialogAction Message
        //  alertDialog.setTitle(errorResponse.message)
        alertDialog.setMessage(message)
        // On pressing SettingsActivity button
        alertDialog.setPositiveButton(getString(R.string.text_clear_all)) { dialog, _ ->
            dialog.dismiss()
            showProgressView(progressBar)
            clear()
        }
        alertDialog.setNegativeButton(getString(R.string.label_cancel_text)) { dialog, _ ->
            dialog.cancel()
        }
        alertDialog.setCancelable(false)
        // Showing Alert Message
        alertDialog.show()
    }

    private fun read(id: String) {
        if (null == mPresenter) mPresenter = NotificationPresenter(presenter)

        run {
            mPresenter!!.readNotificationObject(RequestReadNotification().apply {
                notificationId = id
            })
            mPresenter!!.callRetrofit(ConstantsApi.READ_NOTIFICATION)
        }
    }

    private fun delete(id: String) {
        if (null == mPresenter) mPresenter = NotificationPresenter(presenter)

        run {
            mPresenter!!.readNotificationObject(RequestReadNotification().apply {
                notificationId = id
            })
            mPresenter!!.callRetrofit(ConstantsApi.DELETE_NOTIFICATION)
        }
    }

    // claer all notification
    private fun clear() {
        if (null == mPresenter) mPresenter = NotificationPresenter(presenter)

        run {

            mPresenter!!.callRetrofit(ConstantsApi.CLEAR_NOTIFICATION)
        }
    }

    private fun updateUi() {
        when (notificationAdapter.itemCount > 0) {
            true -> {
                binding!!.rvNotifications.visibility = View.VISIBLE
                binding!!.tvNoNotification.visibility = View.GONE
            }

            false -> {
                binding!!.rvNotifications.visibility = View.GONE
                binding!!.tvNoNotification.visibility = View.VISIBLE
            }
        }
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
                                    val profile: ResponseUserData? = response?.userInfo/*var visible_pro_user: Boolean? = profile?.isProMembership
                                    if (visible_pro_user!!) {
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