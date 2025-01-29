package com.connect.meetupsfellow.mvp.view.model

import com.android.billingclient.api.*
import com.google.gson.Gson
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.log.LogManager
import javax.inject.Inject

class SkuDetail {

    private lateinit var mBillingClient: BillingClient
    private val mSkuDetailsMap = HashMap<String, SkuDetails>()

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.component!!.inject(this@SkuDetail)
        querySkuDetails()
    }

    private fun querySkuDetails() {
        mBillingClient =
            BillingClient.newBuilder(ArchitectureApp.instance!!).setListener { _, _ ->

            }.enablePendingPurchases().build()

        mBillingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                LogManager.logger.i(ArchitectureApp.instance!!.tag, "Connection ended.")
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                when (billingResult!!.responseCode) {
                    BillingClient.BillingResponseCode.ERROR -> {
                        return
                    }
                }
                fetchSKUDetails()
            }

        })
    }

    private fun fetchSKUDetails() {
        val skuDetailsParamsBuilder = SkuDetailsParams.newBuilder()
        val skuList = arrayListOf<String>()
        skuList.add(Constants.SkuDetails.subscriptionOne)
        skuList.add(Constants.SkuDetails.subscriptionTwo)
        skuDetailsParamsBuilder.setSkusList(skuList)
        skuDetailsParamsBuilder.setType(BillingClient.SkuType.SUBS)
        mBillingClient.querySkuDetailsAsync(
            skuDetailsParamsBuilder.build()
        ) { responseCode, skuDetailsList ->
            if (responseCode.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                for (skuDetails in skuDetailsList) {
                    mSkuDetailsMap[skuDetails.sku] = skuDetails
                    sharedPreferencesUtil.saveSkuDetails(
                        skuDetails.sku,
                        Gson().toJson(mSkuDetailsMap)
                    )
                }
            }
            if (mBillingClient.isReady)
                mBillingClient.endConnection()
        }
    }
}