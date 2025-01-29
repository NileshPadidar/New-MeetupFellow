package com.connect.meetupsfellow.mvp.view.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.fragment.CustomFragment
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.constants.ItemClickStatus
import com.connect.meetupsfellow.databinding.LayoutNotificationBinding
import com.connect.meetupsfellow.developement.interfaces.ApiConnect
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.interfaces.ItemClick
import com.connect.meetupsfellow.global.utils.RecyclerViewClick
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.NotificationConnector
import com.connect.meetupsfellow.mvp.presenter.activity.NotificationPresenter
import com.connect.meetupsfellow.mvp.view.activities.SplashActivity
import com.connect.meetupsfellow.mvp.view.adapter.NotificationAdapter
import com.connect.meetupsfellow.retrofit.request.RequestReadNotification
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.google.gson.Gson
import retrofit2.Response
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class NotificationFragment : CustomFragment() {

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
            // hideProgressView(rl_progress)
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {
            when (type) {
                ConstantsApi.NOTIFICATION -> {
                    LogManager.logger.i(
                        ArchitectureApp.instance!!.tag,
                        "Response_Notification444::: ${Gson().toJson(response.notification)}"
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
                        false -> TODO()
                    }
                    fetchNotifications()
                }

                else -> {

                }
            }
            swipeRefreshNotification.isRefreshing = false
            //hideProgressView(rl_progress)
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
                            // showProgressView(rl_progress)
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

    lateinit var rvNotifications: RecyclerView
    lateinit var tvNoNotification: TextView
    lateinit var swipeRefreshNotification: SwipeRefreshLayout
    lateinit var noNotyImg: ImageView
    private var _binding: LayoutNotificationBinding? = null
    private val binding get() = _binding!!

    private val refresh = SwipeRefreshLayout.OnRefreshListener {
        Handler().postDelayed({
            fetchNotifications()
        }, 100)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        ArchitectureApp.component!!.inject(this@NotificationFragment)
        val view = inflater.inflate(R.layout.layout_notification, container, false)
        _binding = LayoutNotificationBinding.inflate(inflater, container, false)

        Log.e("Notification*&", "Fragment_Noti")
        rvNotifications = view.findViewById(R.id.rvNotifications)
        tvNoNotification = view.findViewById(R.id.tvNoNotification)
        noNotyImg = view.findViewById(R.id.noNoti_img)
        swipeRefreshNotification = view.findViewById(R.id.swipeRefreshNotification)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setHasOptionsMenu(true)
        //showProgressView(rl_progress)
        startLoadingAnim()
        init()
        callForUserProfile()
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
                startActivity(Intent(context, SplashActivity::class.java))
                //finish()
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

    private fun startLoadingAnim() {

        val animation = android.view.animation.AnimationUtils.loadAnimation(
            requireContext(), R.anim.blink_anim
        )

        binding.includedLoding.animLogo.startAnimation(animation)
    }

    /*fun onCreateOptionsMenu(menu: Menu?): Boolean {
       activity!!.menuInflater.inflate(R.menu.menu_notification, menu)
       return super.onCreateOptionsMenu(menu )
   }

   override fun onOptionsItemSelected(item: MenuItem): Boolean {
       return when (item!!.itemId) {
           R.id.menu_clear -> {
               when (notificationAdapter.itemCount > 0) {
                   true -> showAlertClear("Are you sure, you want to clear all notifications?")
               }
               true
           }
           else -> super.onOptionsItemSelected(item)
       }
   }*/

    private fun init() {
        with(rvNotifications) {
            layoutManager = LinearLayoutManager(
                rvNotifications.context, RecyclerView.VERTICAL, false
            )
            adapter = notificationAdapter
            addItemDecoration(
                DividerItemDecoration(
                    rvNotifications.context, DividerItemDecoration.VERTICAL
                )
            )
        }

        swipeRefreshNotification.setOnRefreshListener(refresh)

        Handler().postDelayed({
            fetchNotifications()
        }, 100)
    }

    private fun fetchNotifications() {
        //showProgressView(rl_progress)
        if (null == mPresenter) mPresenter = NotificationPresenter(presenter)

        run {
            mPresenter!!.callRetrofit(ConstantsApi.NOTIFICATION)
        }
    }

    private fun showAlertClear(message: String) {
        val alertDialog = AlertDialog.Builder(requireContext(), R.style.MyDialogTheme2)
        // Setting DialogAction Message
        //  alertDialog.setTitle(errorResponse.message)
        alertDialog.setMessage(message)
        // On pressing SettingsActivity button
        alertDialog.setPositiveButton(getString(R.string.text_clear_all)) { dialog, _ ->
            dialog.dismiss()
            //showProgressView(rl_progress)
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
                rvNotifications.visibility = View.VISIBLE
                tvNoNotification.visibility = View.GONE
                noNotyImg.visibility = View.GONE
            }

            false -> {
                rvNotifications.visibility = View.GONE
                noNotyImg.visibility = View.VISIBLE
                tvNoNotification.visibility = View.VISIBLE
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