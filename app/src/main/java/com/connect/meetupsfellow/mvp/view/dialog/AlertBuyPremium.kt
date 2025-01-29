package com.connect.meetupsfellow.mvp.view.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.finishAffinity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.databinding.AlertSubscriptionNewBinding
import com.connect.meetupsfellow.developement.interfaces.FirebaseUtil
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.mvp.connector.activity.CreateProfileConnector
import com.connect.meetupsfellow.mvp.connector.model.UserPaymentConnector
import com.connect.meetupsfellow.mvp.presenter.activity.CreateProfilePresenter
import com.connect.meetupsfellow.mvp.presenter.model.UserPaymentPresenter
import com.connect.meetupsfellow.mvp.view.activities.WebViewActivity
import com.connect.meetupsfellow.retrofit.request.RequestUserPayment
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.PlanList
import com.connect.meetupsfellow.retrofit.response.ResponseUserData
import javax.inject.Inject


class AlertBuyPremium(private var activity: Activity, theme: Int) : AlertDialog(activity) {
    private var mPresenter: UserPaymentConnector.PresenterOps? = null
    private var profilePresenter: CreateProfileConnector.PresenterOps? = null
    private var planList = ArrayList<PlanList>()

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var firebaseUtil: FirebaseUtil
    private var selectedID = 0
    private var productIds = ""
    private var subType = ""
    private var binding: AlertSubscriptionNewBinding? = null
    private lateinit var billingClient: BillingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AlertSubscriptionNewBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        // setContentView(R.layout.alert_subscription_new)

        ArchitectureApp.component!!.inject(this@AlertBuyPremium)

        window!!.attributes.windowAnimations = R.style.DialogAnimation/*window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.clearFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        )*/
        setupBillingClient()

        if (window != null) {
            window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        window!!.setGravity(Gravity.BOTTOM)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        startLoadingAnim()
        setCanceledOnTouchOutside(false)
        setCancelable(false)
        val userProfileData = sharedPreferencesUtil.fetchUserProfile()
        getPlanList()

        binding!!.parent.setOnClickListener { dismiss() }
        //child.setOnClickListener { }
        //logo.setOnClickListener { }
        binding!!.cvSpecialPerk.setOnClickListener {
            if (userProfileData.currentPlanInfo!!.planId == 2) {
                Toast.makeText(activity, "You have already taken this plan", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else {
                setSelected(binding!!.cvSpecialPerk)
                selectedID = planList[0].id ?: 2
                productIds = planList[0].store_id ?: "essen_ace"
                subType = "1 month"

                binding!!.specialPerkGradColorLay.background =
                    context.resources.getDrawable(R.drawable.card_gradient)
                binding!!.standardGradColorLay.setBackgroundColor(context.resources.getColor(R.color.white))
                binding!!.premiumGradColorLay.setBackgroundColor(context.resources.getColor(R.color.white))
                binding!!.featuredGradColorLay.setBackgroundColor(context.resources.getColor(R.color.white))
            }
        }

        binding!!.cvStandard.setOnClickListener {
            if (userProfileData.currentPlanInfo!!.planId == 3) {
                Toast.makeText(activity, "You have already taken this plan", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else {
                setSelected(binding!!.cvStandard)
                selectedID = planList[1].id ?: 3
                productIds = planList[1].store_id ?: "essen_ace"
                subType = "1 month"

                binding!!.standardGradColorLay.background =
                    context.resources.getDrawable(R.drawable.card_gradient)
                binding!!.specialPerkGradColorLay.setBackgroundColor(context.resources.getColor(R.color.white))
                binding!!.premiumGradColorLay.setBackgroundColor(context.resources.getColor(R.color.white))
                binding!!.featuredGradColorLay.setBackgroundColor(context.resources.getColor(R.color.white))
            }
        }

        binding!!.cvPremium.setOnClickListener {
            if (userProfileData.currentPlanInfo!!.planId == 4) {
                Toast.makeText(activity, "You have already taken this plan", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else {

                setSelected(binding!!.cvPremium)
                selectedID = planList[2].id ?: 4
                productIds = planList[2].store_id ?: "essen_ace"
                subType = "1 month"

                binding!!.premiumGradColorLay.background =
                    context.resources.getDrawable(R.drawable.card_gradient)
                binding!!.specialPerkGradColorLay.setBackgroundColor(context.resources.getColor(R.color.white))
                binding!!.standardGradColorLay.setBackgroundColor(context.resources.getColor(R.color.white))
                binding!!.featuredGradColorLay.setBackgroundColor(context.resources.getColor(R.color.white))
            }
        }

        binding!!.cvFeatured.setOnClickListener {
            if (userProfileData.currentPlanInfo!!.planId == 5) {
                Toast.makeText(activity, "You have already taken this plan", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            } else {

                setSelected(binding!!.cvFeatured)
                selectedID = planList[3].id ?: 5
                productIds = planList[3].store_id ?: "essen_ace"
                subType = "1 month"

                binding!!.featuredGradColorLay.background =
                    context.resources.getDrawable(R.drawable.card_gradient)
                binding!!.specialPerkGradColorLay.setBackgroundColor(context.resources.getColor(R.color.white))
                binding!!.standardGradColorLay.setBackgroundColor(context.resources.getColor(R.color.white))
                binding!!.premiumGradColorLay.setBackgroundColor(context.resources.getColor(R.color.white))
            }
        }

        binding!!.btnBuyNow.setOnClickListener {

            if (selectedID != 0) {
                if (productIds.isNotEmpty()) {
                    Log.e("Billing_new", "launchPurchaseFlow_Call")
                    launchPurchaseFlow(productIds)
                } else {
                    Toast.makeText(activity, "Invalid product selection", Toast.LENGTH_SHORT).show()
                }
                Log.e("Click: ", "Subscribe_Now: $selectedID")
                ///  updateUserPlan(1238905, "989725")
                // dismiss()

            } else {
                Toast.makeText(activity, "Please select plan", Toast.LENGTH_SHORT).show()
            }
        }

        ///  binding!!.cvSpecialPerk.callOnClick()

        binding!!.tvspecialReadMore.setOnClickListener {
            // Toast.makeText(activity, "Read More", Toast.LENGTH_SHORT).show()
            openWebView()
        }
        binding!!.tvstandardReadMore.setOnClickListener {
            openWebView()
            // Toast.makeText(activity, "Read More Standard", Toast.LENGTH_SHORT).show()
        }
        binding!!.tvPremiumBenefit.setOnClickListener {
            openWebView()
            // Toast.makeText(activity, "plan benefit", Toast.LENGTH_SHORT).show()
        }
        binding!!.tvFeaturedBenefit.setOnClickListener {
            openWebView()
            // Toast.makeText(activity, "plan benefit feature", Toast.LENGTH_SHORT).show()
        }

    }

  /*  private fun checkSubscriptionRenewal() {
        billingClient.queryPurchasesAsync(BillingClient.ProductType.SUBS) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchasesList != null) {
                for (purchase in purchasesList) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        // Validate purchase on the server
                       /// validatePurchaseWithServer(purchase)
                        Log.e("Billing_new", "checkSubscription (Order ID): ${purchase.orderId}")
                        Log.e("Billing_new", "checkSubscription (Receipt): ${purchase.purchaseToken}")
                    }
                }
            } else {
                Log.e("Billing", "Failed to query purchases: ${billingResult.debugMessage}")
            }
        }
    }*/

    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(activity).setListener { billingResult, purchases ->
                handlePurchaseResponse(billingResult, purchases)
            }.enablePendingPurchases().build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // BillingClient is ready
                    fetchAvailableSubscriptions()
                } else {
                    // Handle error
                    Toast.makeText(
                        activity,
                        "Error initializing Billing: ${billingResult.debugMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Retry connection if needed
            }
        })
    }

    private fun fetchAvailableSubscriptions() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("essen_ace") // Add your store IDs here
                .setProductType(BillingClient.ProductType.SUBS).build(),
            QueryProductDetailsParams.Product.newBuilder().setProductId("pro_persona")
                .setProductType(BillingClient.ProductType.SUBS).build(),
            QueryProductDetailsParams.Product.newBuilder().setProductId("base_elite_ultimate")
                .setProductType(BillingClient.ProductType.SUBS).build(),
            QueryProductDetailsParams.Product.newBuilder().setProductId("luxe_exclusive")
                .setProductType(BillingClient.ProductType.SUBS).build()
        )

        val params = QueryProductDetailsParams.newBuilder().setProductList(productList).build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // Show subscription plans to the user
                /// showSubscriptionPlans(productDetailsList)
                ///  launchPurchaseFlow(productIds)
                Log.e("Billing_new", "BillingClient_is_Ready")
            } else {
                // Handle error
                Toast.makeText(
                    activity,
                    "Error fetching products: ${billingResult.debugMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun launchPurchaseFlow(productId: String) {
        Log.e("Billing_new", "launchPurchaseFlow : $productId")
        if (!billingClient.isReady) {
            Log.e("Billing_new", "BillingClient is not ready!")
            return
        }

        fetchProductDetails(listOf(productId)) { productDetailsMap ->
            val productDetails = productDetailsMap[productId]
            if (productDetails != null) {
                Log.e("Billing_new", "ProductDetails: $productDetails")
                /// val subscriptionOfferDetails = productDetails.subscriptionOfferDetails?.get(0) // Select first offer
                val subscriptionOfferDetails = productDetails.subscriptionOfferDetails?.getOrNull(0)
                if (subscriptionOfferDetails == null) {
                    Log.e("Billing_new", "No SubscriptionOfferDetails found!")
                    return@fetchProductDetails
                }
                Log.e("Billing_new", "Offer_token: ${subscriptionOfferDetails.offerToken}")

                val billingFlowParams = BillingFlowParams.newBuilder().setProductDetailsParamsList(
                        listOf(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .setOfferToken(subscriptionOfferDetails.offerToken) // Use the offer token
                                .build()
                        )
                    ).build()

                // Log.e("Billing_new", "billingFlowParams: ${billingFlowParams}")
                val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
                Log.e("Billing_new", "billingResult: ${billingResult.responseCode}")
                if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                    Toast.makeText(
                        activity,
                        "Error launching purchase flow: ${billingResult.debugMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(
                    activity,
                    "Product details not found for productId: $productId",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun handlePurchaseResponse(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    // Acknowledge purchase and fetch receipt details
                    Log.e("Billing_new", "handlePurchaseResponse")
                    Log.e("Billing_new", "Transaction ID (Order ID): ${purchase.orderId}")
                    Log.e("Billing_new", "Purchase Token (Receipt): ${purchase.purchaseToken}")
                    purchase.orderId?.let { updateUserPlan(it, purchase.purchaseToken) }
                    acknowledgePurchase(purchase)
                }
            }
        } else {
            Toast.makeText(
                activity,
                "Purchase failed: ${billingResult.debugMessage}",
                Toast.LENGTH_SHORT
            ).show()
            Log.e("Billing_new", "Purchase_failed: ${billingResult.debugMessage}")
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val acknowledgeParams =
            AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()

        billingClient.acknowledgePurchase(acknowledgeParams) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // Acknowledgment succeeded
                Log.e("Billing_new", "Purchase acknowledged successfully.")
                ///  updateUserPlan(purchase.orderId!!.toInt(),purchase.purchaseToken)
            }
        }
    }

    private fun fetchProductDetails(
        productIds: List<String>,
        callback: (Map<String, ProductDetails>) -> Unit
    ) {
        val productList = productIds.map { productId ->
            QueryProductDetailsParams.Product.newBuilder().setProductId(productId)
                .setProductType(BillingClient.ProductType.SUBS) // Use ProductType.INAPP for one-time purchases
                .build()
        }

        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder().setProductList(productList).build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                // Create a map of productId to ProductDetails
                val productDetailsMap = productDetailsList.associateBy { it.productId }
                Log.e("Billing_new", "productDetailsMap: ${productDetailsMap.size}")
                callback(productDetailsMap)
            } else {
                // Handle error
                Toast.makeText(
                    activity,
                    "Failed to fetch product details: ${billingResult.debugMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun openWebView() {

        val intent = Intent(activity, WebViewActivity::class.java).apply {
            // Add data to the Intent using putExtra
            putExtra(Constants.IntentDataKeys.TITLE, "Plan benefits")
            putExtra(
                Constants.IntentDataKeys.LINK, "https://frontui.meetupsfellow.com/plans-detail"
            )
            // https://frontui.meetupsfellow.com/MemberDetail old url
        }
        activity.startActivity(intent)
    }

    private fun startLoadingAnim() {
        val animation = android.view.animation.AnimationUtils.loadAnimation(
            context, R.anim.blink_anim
        )
        binding!!.animLogoSubscribe.startAnimation(animation)
    }

    private fun setSelected(view: View) {
        binding!!.cvSpecialPerk.isSelected = false
        binding!!.cvStandard.isSelected = false
        binding!!.cvPremium.isSelected = false
        binding!!.cvFeatured.isSelected = false
        view.isSelected = true
    }

    private val presenter = object : UserPaymentConnector.RequiredViewOps {
        override fun showToast(error: String, logout: Boolean?) {
            Log.e("Pay_Error: ", error)
            Toast.makeText(activity, "Something went wrong!", Toast.LENGTH_SHORT).show()
            binding!!.progressbarSubscribe.visibility = View.GONE
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {
            when (type) {
                ConstantsApi.USER_PAYMENT -> {
                    binding!!.progressbarSubscribe.visibility = View.GONE
                    Log.e("Pay Response", response.message)
                    showAlertSuccessPlan(response.message)
                    Toast.makeText(activity, response.message, Toast.LENGTH_SHORT).show()
                    fetchSelfProfile()
                    dismiss()
                }

                ConstantsApi.GET_PLAN_LIST -> {
                    binding!!.progressbarSubscribe.visibility = View.GONE
                    if (response.plansList.isNotEmpty()) {
                        planList = response.plansList

                        binding!!.tvPlanName1.text = planList[0].title
                        binding!!.tvPlanPrice1.text =
                            planList[0].offerPrice + "/${planList[0].validity}"
                        binding!!.tvPlanName2.text = planList[1].title
                        binding!!.tvPlanPrice2.text =
                            planList[1].offerPrice + "/${planList[1].validity}"

                        binding!!.tvPlanName3.text = planList[2].title
                        binding!!.tvPlanPrice3.text =
                            planList[2].offerPrice + "/${planList[2].validity}"
                        binding!!.tvPlanName4.text = planList[3].title
                        binding!!.tvPlanPrice4.text =
                            planList[3].offerPrice + "/${planList[3].validity}"

                    }
                    Log.e("updateUserPlan", "response_COming")

                }

                else -> {}
            }
        }
    }

    private val reProfilePresenter = object : CreateProfileConnector.RequiredViewOps {
        override fun showToast(error: String, logout: Boolean?) {
            when (logout) {
                true -> {
                    firebaseUtil.userLogout()
                }

                else -> {}
            }
            Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
            binding!!.progressbarSubscribe.visibility = View.GONE
            dismiss()
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {
            when (type) {
                ConstantsApi.FETCH_SELF_PROFILE -> {

                    val profile: ResponseUserData = response.userInfo
                    Log.e("FETCH_SELF_PROFILE", "Name: ${profile!!.name}")
                    Log.e("FETCH_SELF_PROFILE", "Plan: ${profile.currentPlanInfo!!.planTitle}")
                    Log.e("FETCH_SELF_PROFILE", "count: ${profile.available_connection_counts}")
                }

                else -> {}
            }
            binding!!.progressbarSubscribe.visibility = View.GONE
            dismiss()
        }

    }

    private fun fetchSelfProfile() {

        if (null == profilePresenter) profilePresenter = CreateProfilePresenter(reProfilePresenter)

        binding!!.progressbarSubscribe.visibility = View.VISIBLE
        run {
            profilePresenter!!.callRetrofit(ConstantsApi.FETCH_SELF_PROFILE)
        }
    }

    private fun updateUserPlan(transectionId: String, tReceipt: String) {
        if (null == mPresenter) {
            mPresenter = UserPaymentPresenter(presenter)
        }
        Log.e("Billing_new", "updateUserPlan_Call TId: $transectionId , Recpt: $tReceipt")
        run {
            mPresenter!!.addCurrentLocationObject(RequestUserPayment().apply {

                planId = selectedID
                transaction_id = transectionId
                pay_method = "Android"
                transaction_receipt = tReceipt
            })
            Log.e("updateUserPlan", "API_Call")
            binding!!.progressbarSubscribe.visibility = View.VISIBLE
            mPresenter!!.callRetrofit(ConstantsApi.USER_PAYMENT)
        }
    }


    private fun getPlanList() {
        if (null == mPresenter) {
            mPresenter = UserPaymentPresenter(presenter)
        }
        run {
            Log.e("updateUserPlan", "getPlanList_CALl")
            binding!!.progressbarSubscribe.visibility = View.VISIBLE
            mPresenter!!.callRetrofit(ConstantsApi.GET_PLAN_LIST)
        }
    }

    private fun showAlertSuccessPlan(message: String) {

        val dialog = Dialog(activity)

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
        Log.e("showAlertSuccessPlan", "Plan_purchased_successfully")
        // dialogHead.text = "Subscribe"
        dialogHead.visibility = View.GONE
        dialogContent.text = message

        cancelBtn.text = "Cancel"
        confirm.text = "Okay"

        cancelBtn.setOnClickListener {
            dialog.dismiss()
            dismiss()
        }

        confirm.setOnClickListener {
            dialog.dismiss()
            dismiss()
            finishAffinity(activity) // Closes all activities
            System.exit(0) // Kills the process close app
        }

        dialog.show()

    }


    class Builder(private var context: Activity, private var theme: Int) {

        fun build(): AlertBuyPremium {
            val dialog = AlertBuyPremium(context, theme)
            return dialog
        }
    }
}
