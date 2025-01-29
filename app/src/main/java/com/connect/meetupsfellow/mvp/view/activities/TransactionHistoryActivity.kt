package com.connect.meetupsfellow.mvp.view.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.databinding.ActivityTransactionHistoryBinding
import com.connect.meetupsfellow.global.utils.ToolbarUtils
import com.connect.meetupsfellow.mvp.connector.model.UserPaymentConnector
import com.connect.meetupsfellow.mvp.presenter.model.UserPaymentPresenter
import com.connect.meetupsfellow.mvp.view.adapter.TransectionHistoryAdapter
import com.connect.meetupsfellow.mvp.view.dialog.AlertBuyPremium
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.CurrentPlans
import com.connect.meetupsfellow.retrofit.response.TransactionHistory
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime


class TransactionHistoryActivity : CustomAppActivityCompatViewImpl() {
    private val presenter = object : UserPaymentConnector.RequiredViewOps {
        override fun showToast(error: String, logout: Boolean?) {
            Log.e("TRANSECTION_HISTORY: ", "error:- $error")
            //  Toast.makeText(this@TransactionHistoryActivity, "Something went wrong!", Toast.LENGTH_SHORT).show()
            hideProgressView(progressBar)
            updateUi()
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {
            when (type) {
                ConstantsApi.GET_TRANSECTION_HISTORY -> {
                    Log.e("TRANSECTION_HISTORY:", response.currentPlans.toString())
                    currentPlans = response.currentPlans
                    transactionHistory.addAll(response.transactionHistory)
                    transectionHistoryAdapter.add(
                        this@TransactionHistoryActivity, transactionHistory
                    )
                    Log.e("TRANSECTION_HISTORY:", "List: ${transactionHistory.size}")
                    transectionHistoryAdapter.notifyDataSetChanged()
                    updateUi()
                    hideProgressView(progressBar)
                }

                else -> hideProgressView(progressBar)
            }
        }
    }

    private val transectionHistoryAdapter = TransectionHistoryAdapter()
    private var transactionHistory = arrayListOf<TransactionHistory>()
    private var currentPlans = CurrentPlans()
    private var mPresenter: UserPaymentConnector.PresenterOps? = null
    private var binding: ActivityTransactionHistoryBinding? = null
    private lateinit var progressBar: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this@TransactionHistoryActivity)
        //  setContentView(R.layout.activity_transaction_history)
        binding = ActivityTransactionHistoryBinding.inflate(layoutInflater)
        progressBar = binding!!.includedLoading.rlProgress
        setContentView(binding!!.root)
       // setupActionBar(getString(R.string.label_my_subscription), true)
        ToolbarUtils.setupActionBar(
            this,
            binding!!.header.toolbar,
            getString(R.string.label_my_subscription),
            binding!!.header.tvTitle,
            showBackArrow = true,
            activity = this
        )
        startLoadingAnim()

        init()
    }

    private fun startLoadingAnim() {

        val animation = android.view.animation.AnimationUtils.loadAnimation(
            applicationContext, R.anim.blink_anim
        )
        binding!!.includedLoading.animLogo.startAnimation(animation)
    }

    private fun init() {
        with(binding!!.rvTransection) {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                binding!!.rvTransection.context,
                androidx.recyclerview.widget.RecyclerView.VERTICAL,
                false
            )
            adapter = transectionHistoryAdapter

            // below code add a line/view after each position in adapter

            /* addItemDecoration(
                 androidx.recyclerview.widget.DividerItemDecoration(
                     rvTransection.context,
                     androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
                 )
             )*/
        }/* var data = ArrayList<String>()
         data.add(0, "$999")
         data.add(1, "Premium Plan")
         data.add(2, "21323435")
         data.add(3, "$76982")
         data.add(4, "Meetups fellow")
         transectionHistoryAdapter.add(this,data)
         transectionHistoryAdapter.notifyDataSetChanged()*/

        getHistory()

        binding!!.tvUpgradePlan.setOnClickListener {
            AlertBuyPremium.Builder(
                this@TransactionHistoryActivity, Constants.DialogTheme.NoTitleBar
            ).build().show()
        }

        binding!!.btnPurchaseNow.setOnClickListener {
            AlertBuyPremium.Builder(
                this@TransactionHistoryActivity, Constants.DialogTheme.NoTitleBar
            ).build().show()
        }

        binding!!.tvProNow.setOnClickListener {
            AlertBuyPremium.Builder(
                this@TransactionHistoryActivity, Constants.DialogTheme.NoTitleBar
            ).build().show()
        }
    }


    @SuppressLint("NewApi")
    private fun updateUi() {
        when (transectionHistoryAdapter.itemCount > 0) {
            true -> {
                binding!!.rvTransection.visibility = View.VISIBLE
                binding!!.llAvailablePlan.visibility = View.VISIBLE
                binding!!.llNoPlan.visibility = View.GONE


                val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                val outputFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy h:mm a")

                val parsedDateTime = LocalDateTime.parse(currentPlans.next_renewal_date, inputFormatter)
                val formattedDateTime = parsedDateTime.format(outputFormatter)
                binding!!.tvPlanNextRenwel.text = formattedDateTime

                binding!!.tvPlanActive.text = currentPlans.planTitle
                binding!!.tvPlanValidate.text = currentPlans.transaction_id
                binding!!.tvPlanAmount.text = "@ " + currentPlans.planPrice + "/Month"
                binding!!.tvPlanStatus.text = currentPlans.status
                if (currentPlans.status.equals("Expire") || currentPlans.status.equals("Expired")) {
                    binding!!.cvPlanStatusColr.setCardBackgroundColor(getColor(R.color.red))
                    binding!!.tvProNow.visibility = View.VISIBLE
                } else {
                    binding!!.cvPlanStatusColr.setCardBackgroundColor(getColor(R.color.colorSuccess))
                    binding!!.tvProNow.visibility = View.GONE
                }

                when (currentPlans.planId) {
                    2 -> {
                        binding!!.planBadge.setImageResource(R.drawable.special_badge)
                    }

                    3 -> {
                        binding!!.planBadge.setImageResource(R.drawable.standers_badge)
                    }

                    4 -> {
                        binding!!.planBadge.setImageResource(R.drawable.ic_baseline_workspace_premium_24)
                    }

                    5 -> {
                        binding!!.planBadge.setImageResource(R.drawable.featured_badge)
                    }

                    else -> {
                        binding!!.planBadge.setImageResource(R.drawable.free_user_badge)
                    }
                }
            }

            false -> {
                binding!!.rvTransection.visibility = View.GONE
                binding!!.llAvailablePlan.visibility = View.GONE
                binding!!.llNoPlan.visibility = View.VISIBLE
            }
        }
    }

    private fun getHistory() {
        if (null == mPresenter) {
            mPresenter = UserPaymentPresenter(presenter)
        }

        run {
            Log.e("TRANSECTION_HISTORY", "API_Call")
            showProgressView(progressBar)
            mPresenter!!.callRetrofit(ConstantsApi.GET_TRANSECTION_HISTORY)
        }
    }
}