package com.connect.meetupsfellow.components.activitytouch

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.widget.EditText
import com.google.gson.Gson
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.mvp.view.activities.ChatActivity
import java.lang.reflect.Type
import java.util.*


@Suppress("UNUSED_PARAMETER")
@SuppressLint("Registered")
open class CustomAppActivityCompatViewImpltouch() : CustomPermissionActivitytouch() {


//    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
//
//    }
//
//    private var mIABServiceConn: ServiceConnection = object : ServiceConnection {
//        override fun onServiceDisconnected(name: ComponentName) {
//            mIABService = null
//            mBoundIAB = false
//        }
//
//        override fun onServiceConnected(name: ComponentName,
//                                        service: IBinder) {
//            mIABService = IInAppBillingService.Stub.asInterface(service)
//            getSkuDetails()
//            mBoundIAB = true
//        }
//    }

    // Tracks the bound state of the service.
    private var mBoundIAB = false

    override fun onResume() {
        super.onResume()

//
//            when (context) {
//                is SwipeUserActivity -> {
//                    initiateIAPBilling()
//                }
//            }
//        }

    }

//    private fun initiateIAPBilling() {
//        val serviceIntent = Intent("com.android.vending.billing.InAppBillingService.BIND")
//        serviceIntent.`package` = "com.android.vending"
//        bindService(serviceIntent, mIABServiceConn, Context.BIND_AUTO_CREATE)
//    }


    internal fun convertResponseToRequest(from: Any, to: Type): Any {
        return Gson().fromJson(Gson().toJson(from), to)
    }

//    internal fun showAlertRedirectToLogin(message: String) {
//        val alertDialog = AlertDialog.Builder(
//                this@CustomAppActivityCompatViewImpl, R.style.MyDialogTheme2)
//
//        // Setting Dialog Message
//        alertDialog.setMessage(message)
//
//        // On pressing SettingsActivity button
//        alertDialog.setPositiveButton(getString(R.string.text_okay)) { dialog, _ ->
//            switchActivity(this.getString(R.string.intent_login), true, Bundle())
//            dialog.cancel()
//        }
//
//        alertDialog.setCancelable(false)
//        // Showing Alert Message
//        alertDialog.show()
//    }

//    private fun getSkuDetails() {
//        if (mIABService != null) {
//            val skuDetails = mIABService!!.getSkuDetails(3, packageName,
//                    ConstantsImage.ProductIds.TYPE, Bundle().apply {
//                putStringArrayList(
//                    ConstantsImage.ProductIds.ITEM_IDS_LIST,
//                        ConstantsImage.ProductIds.getProductsIds())
//            })
//
//            val response = skuDetails.getInt(ConstantsImage.ProductIds.RESPONSE_CODE)
//
//            if (response == 0) {
//                val responseList = skuDetails.getStringArrayList(ConstantsImage.ProductIds.DETAILS_LIST)
//                for (thisResponse in responseList) {
//                    val id = Gson().fromJson<IAPProductsModel>(thisResponse,
//                            IAPProductsModel::class.java).id
//                    val products = IAPProductsModel()
//                    products.saveProductDetails(id, thisResponse)
//                }
//            }
//        }
//    }


//    fun onShoppingClick(view: View) {
//        switchActivity(getString(R.string.intent_see_people_plans), false, Bundle())
//    }
//
//    fun onContactClick(view: View) {
//        switchActivity(getString(R.string.intent_contact_us), false, Bundle())
//    }
//
//    fun onSettingsClick(view: View) {
//        switchActivity(getString(R.string.intent_settings), false, Bundle().apply {
//            putBoolean(ConstantsImage.IntentData.SETTINGS_NEW_USER, false)
//        })
//    }
//
//    fun onCitiesClick(view: View) {
//        switchActivity(getString(R.string.intent_nearby_cities), false, Bundle().apply {
//            putBoolean(ConstantsImage.IntentData.NEARBY_NEW_USER, false)
//        })
//    }

//    protected fun setupAdvertisement(ads: ArrayList<ResponseAdvertisements>,
//                                     iv_no_user_advertisement: ImageView?,
//                                     tv_ad_title: TextView?,
//                                     tv_ad_subtitle: TextView?) {
//        if (ads.isNotEmpty()) {
//            val random = getRandomNumber(ads.size)
//
//            when (indexExists(ads, random)) {
//                true -> {
//                    DisplayImage.with(this@CustomAppActivityCompatViewImpl)
//                            .load(imageUrl = ads[random].image)
//                            .shape(DisplayImage.Shape.DEFAULT)
//                            .signature(ads[random].image)
//                            .into(iv_advertisement)
//                            .build()
//                    if (tv_ad_title != null)
//                        tv_ad_title.text = ads[random].title
//                    if (tv_ad_subtitle != null)
//                        tv_ad_subtitle.text = ads[random].subtitle
//
//
//                    if (iv_no_user_advertisement != null) {
//                        iv_no_user_advertisement.layoutParams = RelativeLayout.LayoutParams(
//                                RelativeLayout.LayoutParams.MATCH_PARENT, DEVICE_HEIGHT
//                        )
//                        DisplayImage.with(this@CustomAppActivityCompatViewImpl)
//                                .load(imageUrl = ads[random].image)
//                                .shape(DisplayImage.Shape.DEFAULT)
//                                .signature(ads[random].image)
//                                .into(iv_no_user_advertisement)
//                                .build()
//
//                        iv_no_user_advertisement.setOnClickListener {
//                            switchActivity(getString(R.string.intent_web_view), false, Bundle().apply {
//                                putString(ConstantsImage.IntentData.TITLE, getString(R.string.text_advertisement))
//                                putString(ConstantsImage.IntentData.LINK, ads[random].link)
//                            })
//                        }
//                    }
//
//                    rl_advertisement.setOnClickListener {
//                        switchActivity(getString(R.string.intent_web_view), false, Bundle().apply {
//                            putString(ConstantsImage.IntentData.TITLE, getString(R.string.text_advertisement))
//                            putString(ConstantsImage.IntentData.LINK, ads[random].link)
//                        })
//                    }
//                }
//            }
//        }
//    }

    protected fun getRandomNumber(max: Int): Int {
        return Random().nextInt(max)
    }

    protected fun hideKeyboard(editText: EditText) {
        /* val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
         imm.hideSoftInputFromWindow(editText.windowToken, 0)*/
    }

    protected fun showAlert(message: String, title: String) {
        val alertDialog = AlertDialog.Builder(
            this@CustomAppActivityCompatViewImpltouch, R.style.MyDialogTheme2
        )
        // Setting DialogAction Message
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        // On pressing SettingsActivity button
        alertDialog.setPositiveButton(getString(R.string.text_yes)) { dialog, _ ->
            dialog.dismiss()
            System.exit(0)
            finish()
        }
        alertDialog.setNegativeButton(getString(R.string.text_no)) { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.setCancelable(false)
        // Showing Alert Message
        alertDialog.show()
    }


    protected fun showAlertForStatusRejected(message: String, title: String) {
        val alertDialog = AlertDialog.Builder(
            this@CustomAppActivityCompatViewImpltouch, R.style.MyDialogTheme2
        )
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton(getString(R.string.text_okay)) { dialog, _ ->
            dialog.dismiss()
            logout()
        }
        alertDialog.setCancelable(false)

        alertDialog.show()
    }

    companion object {
        internal val DEVICE_HEIGHT = ArchitectureApp.instance!!.resources.displayMetrics.heightPixels
    }
}