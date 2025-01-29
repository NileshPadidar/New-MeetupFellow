package com.connect.meetupsfellow.mvp.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.databinding.ActivityInterestedPeopleBinding
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.utils.ToolbarUtils
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.InterestedPeopleConnector
import com.connect.meetupsfellow.mvp.presenter.activity.InterestedPeoplePresenter
import com.connect.meetupsfellow.mvp.view.adapter.InterestedPeopleAdapter
import com.connect.meetupsfellow.retrofit.response.CommonResponse

import javax.inject.Inject

class InterestedPeopleActivity : CustomAppActivityCompatViewImpl() {

    private val presenter = object : InterestedPeopleConnector.RequiredViewOps {
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
                ConstantsApi.INTERESTED_PEOPLE -> {
                    interestedPeopleAdapter.update(response.interestedUsers)
                }

                else -> {

                }
            }
            hideProgressView(progressBar)
        }

    }

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    private val interestedPeopleAdapter = InterestedPeopleAdapter()

    private var mPresenter: InterestedPeopleConnector.PresenterOps? = null

    private var eventId = -1
    private var binding: ActivityInterestedPeopleBinding? = null
    private lateinit var progressBar: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIntentData()
        binding = ActivityInterestedPeopleBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        //  setContentView(R.layout.activity_interested_people)
      //  setupActionBar(getString(R.string.title_interested_people), true)
        ToolbarUtils.setupActionBar(
            this,
            binding!!.header.toolbar,
            getString(R.string.title_interested_people),
            binding!!.header.tvTitle,
            showBackArrow = true,
            activity = this
        )
        progressBar = binding!!.includedLoading.rlProgress
        if (eventId == -1) {
            finish()
            return
        }

        component.inject(this@InterestedPeopleActivity)
        init()
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

        checkRequiredPermissions()
    }

    private fun getIntentData() {
        if (intent.hasExtra(Constants.IntentDataKeys.EventId)) {
            eventId = intent.getIntExtra(Constants.IntentDataKeys.EventId, -1)
        }
    }

    private fun init() {
        showProgressView(progressBar)
        with(binding!!.rvInterestedPeople) {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                this@InterestedPeopleActivity,
                androidx.recyclerview.widget.RecyclerView.VERTICAL,
                false
            )
            adapter = interestedPeopleAdapter
            addItemDecoration(
                androidx.recyclerview.widget.DividerItemDecoration(
                    binding!!.rvInterestedPeople.context,
                    androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
                )
            )
        }

        interestedPeople()
    }

    private fun interestedPeople() {
        if (null == mPresenter) mPresenter = InterestedPeoplePresenter(presenter)

        run {
            mPresenter!!.addEventIdObject("$eventId")
            mPresenter!!.callRetrofit(ConstantsApi.INTERESTED_PEOPLE)
        }
    }
}