package com.connect.meetupsfellow.mvp.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.constants.ItemClickStatus
import com.connect.meetupsfellow.databinding.ActivityPrivateAccessBinding
import com.connect.meetupsfellow.developement.interfaces.ApiConnect
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.interfaces.ItemClick
import com.connect.meetupsfellow.global.utils.RecyclerViewClick
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.PrivateAccessConnector
import com.connect.meetupsfellow.mvp.presenter.activity.PrivateAccessPresenter
import com.connect.meetupsfellow.mvp.view.adapter.PrivateAccessAdapter
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ResponseUserData
import retrofit2.Response
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class PrivateAccessActivity : CustomAppActivityCompatViewImpl() {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var apiConnect: ApiConnect

    private val presenter = object : PrivateAccessConnector.RequiredViewOps {
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
                ConstantsApi.PRIVATE_ACCESS -> {
                    privateAccessAdapter.update(response.picAccessList)
                    when (response.picAccessList.isEmpty()) {
                        true -> {
                            binding!!.rvPrivateAccess.visibility = View.GONE
                            binding!!.tvNoUser.visibility = View.VISIBLE
                        }

                        false -> {
                            binding!!.rvPrivateAccess.visibility = View.VISIBLE
                            binding!!.tvNoUser.visibility = View.GONE
                        }
                    }
                }

                ConstantsApi.SHARE_PRIVATE -> {
                    showProgressView(progressBar)
                    fetchPrivateAccessList()
                }

                else -> {}
            }
            hideProgressView(progressBar)
        }
    }

    private val itemClick = object : ItemClick {
        override fun onItemClick(position: Int, status: ItemClickStatus) {
            if (status == ItemClickStatus.Private) {
                showAlertUnshare(
                    "Are you sure you want to unshare your private pictures from this user?",
                    "$position"
                )
            } else if (status == ItemClickStatus.Profile) {
                switchActivity(Constants.Intent.ProfileUser, false, Bundle().apply {
                    putInt(Constants.IntentDataKeys.UserId, position)
                })
            }
        }
    }

    private var mPresenter: PrivateAccessConnector.PresenterOps? = null

    private val privateAccessAdapter = PrivateAccessAdapter()
    private var binding: ActivityPrivateAccessBinding? = null
    private lateinit var progressBar: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivateAccessBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        // setContentView(R.layout.activity_private_access)
        component.inject(this)
        progressBar = binding!!.includedLoading.rlProgress
        showProgressView(progressBar)
        setupActionBar(getString(R.string.label_private_pictures_access), true)
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



        fetchPrivateAccessList()
        RecyclerViewClick.enableClick(itemClick)
    }

    override fun onDestroy() {
        super.onDestroy()
        RecyclerViewClick.disableClick()
    }

    private fun init() {
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        with(binding!!.rvPrivateAccess) {
            layoutManager = LinearLayoutManager(binding!!.rvPrivateAccess.context)
            adapter = privateAccessAdapter
            hasFixedSize()
        }
    }

    private fun fetchPrivateAccessList() {
        if (null == mPresenter) mPresenter = PrivateAccessPresenter(presenter)

        run {
            mPresenter!!.callRetrofit(ConstantsApi.PRIVATE_ACCESS)
        }
    }

    private fun sharePrivateAccess(userId: String) {
        if (null == mPresenter) mPresenter = PrivateAccessPresenter(presenter)

        run {
            mPresenter!!.addObjectForPrivateAccess(userId)
            mPresenter!!.callRetrofit(ConstantsApi.SHARE_PRIVATE)
        }
    }

    private fun showAlertUnshare(message: String, position: String) {
        val alertDialog = AlertDialog.Builder(this@PrivateAccessActivity, R.style.MyDialogTheme2)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton(getString(R.string.text_Unshare)) { dialog, _ ->
            dialog.dismiss()
            showProgressView(progressBar)
            sharePrivateAccess(position)
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
                                    val profile: ResponseUserData? = response?.userInfo/* var visible_pro_user: Boolean? = profile?.isProMembership
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