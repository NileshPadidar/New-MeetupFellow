package com.connect.meetupsfellow.mvp.view.activities

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.SkuDetailsParams
import com.connect.meetupsfellow.BuildConfig
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.constants.ItemClickStatus
import com.connect.meetupsfellow.databinding.ActivitySettingsBinding
import com.connect.meetupsfellow.developement.interfaces.ApiConnect
import com.connect.meetupsfellow.developement.interfaces.FirebaseUtil
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.interfaces.ItemClick
import com.connect.meetupsfellow.global.utils.RecyclerViewClick
import com.connect.meetupsfellow.global.utils.ToolbarUtils
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.SettingsConnector
import com.connect.meetupsfellow.mvp.presenter.activity.SettingsPresenter
import com.connect.meetupsfellow.mvp.view.adapter.SettingsAdapter
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ResponseSettings
import com.connect.meetupsfellow.retrofit.response.ResponseUserData
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import retrofit2.Response
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class SettingsActivity : CustomAppActivityCompatViewImpl() {

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var apiConnect: ApiConnect

    private lateinit var mBillingClient: BillingClient

    private val presenter = object : SettingsConnector.RequiredViewOps {
        override fun showToast(error: String, logout: Boolean?) {
            when (logout) {
                true -> {
                    logout()
                }

                false -> {}
                null -> {}
            }
            universalToast(error)
            hideProgressView(ProgressBar)
        }

        override fun showResponse(response: CommonResponse, type: ConstantsApi) {
            when (type) {
                ConstantsApi.DEACTIVATE -> deactivateLogoutAccount()
                ConstantsApi.DELETE -> {
                    logout()
                    hideProgressView(ProgressBar)
                }

                ConstantsApi.DEACTIVATE_LOGOUT -> {
                    logout()
                    hideProgressView(ProgressBar)
                }

                ConstantsApi.UNIT_IMPERIAL -> {
                    setting = sharedPreferencesUtil.fetchSettings()
                    adapter.getSettings(setting!!)
                    hideProgressView(ProgressBar)
                }

                ConstantsApi.UNIT_METRIC -> {
                    setting = sharedPreferencesUtil.fetchSettings()
                    adapter.getSettings(setting!!)
                    hideProgressView(ProgressBar)
                }

                else -> {
                    universalToast(response.message)
                    setting = sharedPreferencesUtil.fetchSettings()
                    adapter.getSettings(setting!!)
                    hideProgressView(ProgressBar)
                }
            }
        }
    }

    private val itemClick = object : ItemClick {
        override fun onItemClick(position: Int, status: ItemClickStatus) {
            when (position) {
                0 -> {
                    switchActivity(Constants.Intent.ChangeNumber, false, Bundle())
                }

                1 -> {
                    when (status) {
                        ItemClickStatus.Settings -> {
                            showProgressView(ProgressBar)
                            notificationUpdate(setting!!.allowPush != 0, ConstantsApi.ALLOW_PUSH)
                        }

                        else -> {

                        }
                    }
                }

                2 -> {
                    when (status) {
                        ItemClickStatus.Settings -> {
                            showProgressView(ProgressBar)
                            notificationUpdate(setting!!.allowEvent != 0, ConstantsApi.ALLOW_EVENT)
                        }

                        else -> {

                        }
                    }
                }

                3 -> {
                    when (status) {
                        ItemClickStatus.Settings -> {
                            showProgressView(ProgressBar)
                            notificationUpdate(
                                setting!!.allowEventUpdate != 0, ConstantsApi.ALLOW_EVENT_UPDATE
                            )
                        }

                        else -> {

                        }
                    }
                }

                4 -> {
                    when (status) {
                        ItemClickStatus.Settings -> {
                            showProgressView(ProgressBar)

                            if (setting!!.nsfw == 0) {
                                nsfwAllow(setting!!.nsfw != 0, ConstantsApi.ALLOW_NSFW_SETTING)
                            } else {
                                showAlertNsfws(getString(R.string.alert_nsfw))

                            }


                        }

                        else -> {

                        }
                    }

                }

                5 -> {
                    switchActivity(Constants.Intent.Private, false, Bundle())
                }

                6 -> {
                    showAlertDeactivate("Are you sure, you want to deactivate your account?")

                }

                7 -> {
                    showAlertDelete("Are you sure, you want to delete your account?")
//                    AlertBuyPremium.Builder(this@SettingsActivity, Constants.DialogTheme.NoTitleBar)
//                        .build().show()

//                    when (mBillingClient.isReady) {
//                        true -> {
//                            fetchSKUDetails()
//                        }
//                    }
                }

                8 -> {
                    when (status) {
                        ItemClickStatus.SettingsImperial -> {
                            showProgressView(ProgressBar)
                            unitSystemUpdate(ConstantsApi.UNIT_IMPERIAL)
                        }

                        ItemClickStatus.SettingsMetric -> {
                            showProgressView(ProgressBar)
                            unitSystemUpdate(ConstantsApi.UNIT_METRIC)
                        }

                        else -> {

                        }
                    }
                }

            }
        }
    }


    private var setting: ResponseSettings? = null
    private val adapter = SettingsAdapter()

    private var mPresenter: SettingsConnector.PresenterOps? = null
    private var mAdManagerInterstitialAd: AdManagerInterstitialAd? = null
    private var binding: ActivitySettingsBinding? = null
    private lateinit var ProgressBar: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        // setContentView(R.layout.activity_settings)
        component.inject(this@SettingsActivity)
        //    setupActionBar(getString(R.string.title_settings), true)
        ToolbarUtils.setupActionBar(
            this,
            binding!!.header.toolbar,
            getString(R.string.title_settings),
            binding!!.header.tvTitle,
            showBackArrow = true,
            activity = this
        )
        startLoadingAnim()
        ProgressBar = binding!!.includedLoading.rlProgress
        init()
        callForUserProfile()
        callForSettings()

        if (!sharedPreferencesUtil.fetchUserProfile().isProMembership) {
            loadInterstitialAd(this)
        }


        mBillingClient = BillingClient.newBuilder(ArchitectureApp.instance!!)
            .setListener { billingResult, purchases ->
                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        universalToast("OK")
                    }

                    BillingClient.BillingResponseCode.ERROR -> {
                        universalToast("ERROR")
                    }

                    BillingClient.BillingResponseCode.USER_CANCELED -> {
                        universalToast("USER CANCELLED")
                    }
                }
            }.enablePendingPurchases().build()

        mBillingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                LogManager.logger.i(ArchitectureApp.instance!!.tag, "Connection ended.")
            }

            override fun onBillingSetupFinished(p0: BillingResult) {
                when (p0.responseCode) {
                    BillingClient.BillingResponseCode.ERROR -> {
                        return
                    }
                }
            }

            /*override fun onBillingSetupFinished(billingResult: BillingResult?) {
                when (billingResult!!.responseCode) {
                    BillingClient.BillingResponseCode.ERROR -> {
                        return
                    }
                }
            }*/

        })
    }

    fun loadInterstitialAd(context: Context) {
        var adRequest = AdManagerAdRequest.Builder().build()
        AdManagerInterstitialAd.load(context,
            getString(R.string.InterstitialAd_unit_id),
            adRequest,
            object : AdManagerInterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("InterstitialAdManager", adError.toString())
                    mAdManagerInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: AdManagerInterstitialAd) {
                    Log.e("InterstitialAdManager", "SettingsActivity Ad was loaded.")
                    mAdManagerInterstitialAd = interstitialAd
                    showInterstitialAd(context)
                }
            })
    }

    fun showInterstitialAd(context: Context) {
        if (mAdManagerInterstitialAd != null) {
            mAdManagerInterstitialAd?.show(context as Activity)
        } else {
            Log.e(
                "InterstitialAdManager", "SettingsActivity The interstitial ad wasn't ready yet."
            )
        }

        mAdManagerInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.e("InterstitialAdManager", "SettingsActivity Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.e("InterstitialAdManager", "SettingsActivity Ad dismissed fullscreen content.")
                mAdManagerInterstitialAd = null
                /// loadInterstitialAd(context)
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                // Called when ad fails to show.
                Log.e("InterstitialAdManager", "Ad failed to show fullscreen content.")
                mAdManagerInterstitialAd = null
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.e("InterstitialAdManager", "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.e("InterstitialAdManager2", "SettingsActivity Ad showed fullscreen content.")
            }
        }
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

        checkRequiredPermissions()
        RecyclerViewClick.enableClick(itemClick)
    }

    override fun onPause() {
        super.onPause()
        sharedPreferencesUtil.saveTimmerTime(System.currentTimeMillis().toString())
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag, "onPause ::: " + System.currentTimeMillis().toString()
        )

        // Log.e("lasttrackTime pause",System.currentTimeMillis().toString())


        RecyclerViewClick.disableClick()
        if (mBillingClient.isReady) mBillingClient.endConnection()
    }

    private fun init() {
        setting = sharedPreferencesUtil.fetchSettings()
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag, "Settings is : ${Gson().toJson(setting)}"
        )
        with(binding!!.rvSettings) {
            layoutManager = LinearLayoutManager(this@SettingsActivity, RecyclerView.VERTICAL, false)
            adapter = this@SettingsActivity.adapter
        }
        adapter.getSettings(setting!!)

        binding!!.tvVersion.text =
            String.format(getString(R.string.text_version), BuildConfig.VERSION_NAME)

        binding!!.touchFaceCard.setOnClickListener {

            showAlertFingerPrint("Enable Finger Print Lock?")
        }

      /*  binding!!.changeNoCard.setOnClickListener {
            switchActivity(Constants.Intent.ChangeNumber, false, Bundle())
        }*/

        binding!!.cvAccsessPrivetAlbum.setOnClickListener {
            switchActivity(Constants.Intent.PrivateAlbumList, false, Bundle())
        }

        binding!!.deactivateCard.setOnClickListener {

            // showDeactivateDialog("Deactivate Account", "Are you sure, you want to deactivate your account?", "deactivate")
            showDeactivateDialog(
                "Deactivate Account", getString(R.string.deactivate_your_account), "deactivate"
            )
        }

        binding!!.deleteCard.setOnClickListener {
            // showDeactivateDialog("Delete Account", "Are you sure, you want to delete your account?", "delete")
            showDeactivateDialog(
                "Delete Account", getString(R.string.delete_your_account), "delete"
            )

        }

        binding!!.setUpdatePin.setOnClickListener {

            showAlertPin()
        }
    }

    private fun showDeactivateDialog(head: String, content: String, type: String) {

        val dialog = Dialog(this)

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

        dialogHead.text = head
        dialogContent.text = content

        cancelBtn.setOnClickListener {

            dialog.cancel()
        }

        confirm.setOnClickListener {

            if (type == "deactivate") {
                dialog.dismiss()
                showProgressView(ProgressBar)
                deactivateAccount()
            } else {
                dialog.dismiss()
                showProgressView(ProgressBar)
                deleteAccount()
            }
        }

        dialog.show()
    }

    private fun showAlertPin() {

        val dialog = Dialog(this)

        dialog.setCancelable(true)

        val view = layoutInflater.inflate(R.layout.set_update_pin_dialog, null)

        dialog.setContentView(view)

        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation

        if (dialog.window != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val oldPinEt = view.findViewById<TextInputEditText>(R.id.oldPinEt)
        val newPinEt = view.findViewById<TextInputEditText>(R.id.newPinEt)
        val confirmPinEt = view.findViewById<TextInputEditText>(R.id.confirmPinEt)
        val setPinBtn = view.findViewById<Button>(R.id.setPinBtn)
        val oldPinLay = view.findViewById<TextInputLayout>(R.id.oldPinLay)
        val pinSwitch = view.findViewById<Switch>(R.id.pinSwitch)
        val tvEnableDeceble = view.findViewById<TextView>(R.id.tvEnableDeceble)

        val appPin = getSharedPreferences("appPin", MODE_PRIVATE).getString("appPin", "")
        val isAppPinEnabled =
            getSharedPreferences("appPinB", MODE_PRIVATE).getBoolean("appPinB", false)

        if (isAppPinEnabled) {

            /// pinSwitch.isChecked = IconSwitch.Checked.RIGHT
            pinSwitch.isChecked = true
            tvEnableDeceble.text = getString(R.string.disable_pin_code)

        } else {

            ///  pinSwitch.isChecked = IconSwitch.Checked.LEFT
            pinSwitch.isChecked = false
            tvEnableDeceble.text = getString(R.string.enable_pin_code)
        }

        if (appPin!!.isNotEmpty()) {

            oldPinLay.visibility = View.VISIBLE
        } else {

            oldPinLay.visibility = View.GONE
        }

        pinSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            when (isChecked) {

                false -> {

                    Toast.makeText(
                        this@SettingsActivity, "Pin Code Unlock Disabled", Toast.LENGTH_SHORT
                    ).show()
                    tvEnableDeceble.text = getString(R.string.enable_pin_code)
                    getSharedPreferences("appPinB", MODE_PRIVATE).edit()
                        .putBoolean("appPinB", false).apply()
                }

                true -> {
                    Toast.makeText(
                        this@SettingsActivity, "Pin Code Unlock Enabled", Toast.LENGTH_SHORT
                    ).show()
                    tvEnableDeceble.text = getString(R.string.disable_pin_code)
                    getSharedPreferences("appPinB", MODE_PRIVATE).edit().putBoolean("appPinB", true)
                        .apply()
                }
            }
        }

        /*  pinSwitch.setCheckedChangeListener(object : IconSwitch.CheckedChangeListener {

              override fun onCheckChanged(current: IconSwitch.Checked?) {

                  when (current) {

                      IconSwitch.Checked.LEFT -> {

                          Toast.makeText(
                              this@SettingsActivity, "Pin Code Unlock Disabled", Toast.LENGTH_SHORT
                          ).show()
                          getSharedPreferences("appPinB", MODE_PRIVATE).edit()
                              .putBoolean("appPinB", false).apply()
                      }

                      IconSwitch.Checked.RIGHT -> {
                          Toast.makeText(
                              this@SettingsActivity, "Pin Code Unlock Enabled", Toast.LENGTH_SHORT
                          ).show()
                          getSharedPreferences("appPinB", MODE_PRIVATE).edit()
                              .putBoolean("appPinB", true).apply()
                      }
                  }
              }

          })*/

        setPinBtn.setOnClickListener {

            if (appPin.isNotEmpty()) {

                if (oldPinEt.text!!.isEmpty()) {

                    oldPinEt.requestFocus()
                    oldPinEt.error = "Old pin is required"
                } else if (oldPinEt.text!!.length != 6) {

                    oldPinEt.requestFocus()
                    oldPinEt.error = "Old pin must be 6 digits"
                } else if (newPinEt.text!!.isEmpty()) {

                    newPinEt.requestFocus()
                    newPinEt.error = "new pin is required"
                } else if (newPinEt.text!!.length != 6) {

                    newPinEt.requestFocus()
                    newPinEt.error = "New pin must be 6 digits"
                } else if (confirmPinEt.text!!.isEmpty()) {

                    confirmPinEt.requestFocus()
                    confirmPinEt.error = "Pin is required"
                } else if (confirmPinEt.text!!.length != 6) {

                    confirmPinEt.requestFocus()
                    confirmPinEt.error = "Pin must be 6 digits"
                } else if (newPinEt.text.toString() != confirmPinEt.text.toString()) {

                    Toast.makeText(this, "Pin does not match", Toast.LENGTH_SHORT).show()
                } else if (oldPinEt.text.toString() != appPin) {

                    oldPinEt.requestFocus()
                    oldPinEt.error = "Old pin does not match"
                } else {

                    dialog.cancel()
                    Toast.makeText(this, "Pin Updated", Toast.LENGTH_SHORT).show()
                    getSharedPreferences("appPin", MODE_PRIVATE).edit()
                        .putString("appPin", confirmPinEt.text.toString()).apply()
                    // getSharedPreferences("appPin", MODE_PRIVATE).edit().putString("appPin", "").apply()
                }
            } else {

                if (newPinEt.text!!.isEmpty()) {

                    newPinEt.requestFocus()
                    newPinEt.error = "new pin is required"
                } else if (newPinEt.text!!.length != 6) {

                    newPinEt.requestFocus()
                    newPinEt.error = "New pin must be 6 digits"
                } else if (confirmPinEt.text!!.isEmpty()) {

                    confirmPinEt.requestFocus()
                    confirmPinEt.error = "Pin is required"
                } else if (confirmPinEt.text!!.length != 6) {

                    confirmPinEt.requestFocus()
                    confirmPinEt.error = "Pin must be 6 digits"
                } else if (newPinEt.text.toString() != confirmPinEt.text.toString()) {

                    Toast.makeText(this, "Pin does not match", Toast.LENGTH_SHORT).show()
                } else {

                    dialog.cancel()
                    Toast.makeText(this, "New Pin Set", Toast.LENGTH_SHORT).show()
                    getSharedPreferences("appPin", MODE_PRIVATE).edit()
                        .putString("appPin", confirmPinEt.text.toString()).apply()
                    /// getSharedPreferences("appPin", MODE_PRIVATE).edit().putString("appPin","").apply()
                }

            }


        }

        dialog.show()
    }

    private fun showAlertFingerPrint(message: String) {

        val dialog = Dialog(this)

        dialog.setCancelable(true)

        val view = layoutInflater.inflate(R.layout.enable_finger_face_unlock_dialog, null)

        dialog.setContentView(view)

        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation

        if (dialog.window != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        dialog.window!!.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val fingerSwitch = view.findViewById<Switch>(R.id.fingerSwitch)
        val closeDialog = view.findViewById<Button>(R.id.closeDialog)
        val tvEnableDecebleFP = view.findViewById<TextView>(R.id.tvEnableDecebleFP)

        Log.d(
            "finger",
            getSharedPreferences("Finger", MODE_PRIVATE).getBoolean("Finger", false).toString()
        )

        val isFingerEnabled =
            getSharedPreferences("Finger", MODE_PRIVATE).getBoolean("Finger", false)

        if (isFingerEnabled) {

            //  fingerSwitch.isChecked = IconSwitch.Checked.RIGHT
            tvEnableDecebleFP.text = getString(R.string.disable_fingerprint)
            fingerSwitch.isChecked = true
        } else {

            // fingerSwitch.checked = IconSwitch.Checked.LEFT
            tvEnableDecebleFP.text = getString(R.string.enable_fingerprint)
            fingerSwitch.isChecked = false
        }

        //  Log.d("finger", fingerSwitch.checked.toString())

        fingerSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            when (isChecked) {

                false -> {

                    Toast.makeText(
                        this@SettingsActivity, "FingerPrint Unlock Disabled", Toast.LENGTH_SHORT
                    ).show()
                    getSharedPreferences("Finger", MODE_PRIVATE).edit().putBoolean("Finger", false)
                        .apply()
                    tvEnableDecebleFP.text = getString(R.string.enable_fingerprint)
                }

                true -> {

                    checkFinger(fingerSwitch)
                    tvEnableDecebleFP.text = getString(R.string.disable_fingerprint)
                }
            }
        }

        /* fingerSwitch.setCheckedChangeListener(object : IconSwitch.CheckedChangeListener {
             override fun onCheckChanged(current: IconSwitch.Checked?) {

                 when (current) {

                     IconSwitch.Checked.LEFT -> {

                         Toast.makeText(
                             this@SettingsActivity, "FingerPrint Unlock Disabled", Toast.LENGTH_SHORT
                         ).show()
                         getSharedPreferences("Finger", MODE_PRIVATE).edit()
                             .putBoolean("Finger", false).apply()
                     }

                     IconSwitch.Checked.RIGHT -> {

                         checkFinger(fingerSwitch)
                     }
                 }

                 //dialog.cancel()

             }
         })*/

        closeDialog.setOnClickListener {

            dialog.cancel()
        }

        dialog.show()

    }

    private fun checkFinger(fingerSwitch: Switch) {

        val bioMetric = androidx.biometric.BiometricManager.from(this)

        when (bioMetric.canAuthenticate()) {

            androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS -> {

                Toast.makeText(this, "FingerPrint Unlock Enabled", Toast.LENGTH_SHORT).show()
                getSharedPreferences("Finger", MODE_PRIVATE).edit().putBoolean("Finger", true)
                    .apply()
            }

            androidx.biometric.BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {

                Toast.makeText(this, "FingerPrint Scanner not Available", Toast.LENGTH_SHORT).show()
                Handler().postDelayed({
                    fingerSwitch.isChecked = false
                }, 500)
            }

            androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {

                Toast.makeText(this, "FingerPrint Scanner not set", Toast.LENGTH_SHORT).show()
                Handler().postDelayed({
                    // fingerSwitch.checked = IconSwitch.Checked.LEFT
                    fingerSwitch.isChecked = false
                }, 500)

            }

            androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {

                Toast.makeText(this, "FingerPrint Scanner Not Found", Toast.LENGTH_SHORT).show()
                Handler().postDelayed({
                    //  fingerSwitch.checked = IconSwitch.Checked.LEFT
                    fingerSwitch.isChecked = false
                }, 500)
            }
        }

    }

    private fun showAlertNsfws(message: String) {
        val alertDialog = AlertDialog.Builder(this@SettingsActivity, R.style.MyDialogTheme2)
        // Setting DialogAction Message
        //  alertDialog.setTitle(errorResponse.message)
        alertDialog.setMessage(message)
        // On pressing SettingsActivity button
        alertDialog.setPositiveButton(getString(R.string.text_ok)) { dialog, _ ->
            dialog.dismiss()
            showProgressView(ProgressBar)
            nsfwAllow(setting!!.nsfw != 0, ConstantsApi.ALLOW_NSFW_SETTING)
        }
        alertDialog.setNegativeButton(getString(R.string.text_cancel)) { dialog, _ ->
            dialog.cancel()
            adapter.getSettings(setting!!)
            hideProgressView(ProgressBar)
        }
        alertDialog.setCancelable(false)
        // Showing Alert Message
        alertDialog.show()
    }


    private fun showAlertDeactivate(message: String) {
        val alertDialog = AlertDialog.Builder(this@SettingsActivity, R.style.MyDialogTheme2)
        // Setting DialogAction Message
        //  alertDialog.setTitle(errorResponse.message)
        alertDialog.setMessage(message)
        // On pressing SettingsActivity button
        alertDialog.setPositiveButton(getString(R.string.text_deactivate)) { dialog, _ ->
            dialog.dismiss()
            showProgressView(ProgressBar)
            deactivateAccount()
        }
        alertDialog.setNegativeButton(getString(R.string.label_cancel_text)) { dialog, _ ->
            dialog.cancel()
        }
        alertDialog.setCancelable(false)
        // Showing Alert Message
        alertDialog.show()
    }

    private fun showAlertDelete(message: String) {
        val alertDialog = AlertDialog.Builder(this@SettingsActivity, R.style.MyDialogTheme2)
        // Setting DialogAction Message
        //  alertDialog.setTitle(errorResponse.message)
        alertDialog.setMessage(message)
        // On pressing SettingsActivity button
        alertDialog.setPositiveButton(getString(R.string.text_delete)) { dialog, _ ->
            dialog.dismiss()
            showProgressView(ProgressBar)
            deleteAccount()
        }
        alertDialog.setNegativeButton(getString(R.string.label_cancel_text)) { dialog, _ ->
            dialog.cancel()
        }
        alertDialog.setCancelable(false)
        // Showing Alert Message
        alertDialog.show()
    }

    private fun notificationUpdate(setting: Boolean, type: ConstantsApi) {
        if (null == mPresenter) mPresenter = SettingsPresenter(presenter)

        run {
            mPresenter!!.addSettingsObject(!setting)
            mPresenter!!.callRetrofit(type)
            firebaseUtil.updatePushNotificationStatus(sharedPreferencesUtil.userId(), !setting)
        }
    }


    private fun nsfwAllow(setting: Boolean, unit: ConstantsApi) {

        if (null == mPresenter) mPresenter = SettingsPresenter(presenter)

        run {
            mPresenter!!.addSettingsObject(!setting)
            mPresenter!!.callRetrofit(unit)
        }
    }


    private fun unitSystemUpdate(unit: ConstantsApi) {

        if (null == mPresenter) mPresenter = SettingsPresenter(presenter)

        run {
            mPresenter!!.callRetrofit(unit)
        }
    }

    private fun deleteAccount() {
        if (null == mPresenter) mPresenter = SettingsPresenter(presenter)

        run {
            mPresenter!!.callRetrofit(ConstantsApi.DELETE)
        }
    }

    private fun deactivateAccount() {
        if (null == mPresenter) mPresenter = SettingsPresenter(presenter)

        run {
            mPresenter!!.callRetrofit(ConstantsApi.DEACTIVATE)
        }
    }

    private fun deactivateLogoutAccount() {
        if (null == mPresenter) mPresenter = SettingsPresenter(presenter)

        run {
            mPresenter!!.callRetrofit(ConstantsApi.DEACTIVATE_LOGOUT)
        }
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
                    val flowParams =
                        BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build()
                    mBillingClient.launchBillingFlow(this@SettingsActivity, flowParams)
                }
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


    private fun callForSettings() {
        try {
            val settings = apiConnect.api.settings(
                sharedPreferencesUtil.loginToken(), Constants.Random.random()
            )

            settings.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response<CommonResponse>> {
                    override fun onError(e: Throwable?) {
                        e!!.printStackTrace()

                    }

                    override fun onNext(response: Response<CommonResponse>?) {
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag, "Response is ::: $response"
                        )
                        if (response != null) {
                            when (response.isSuccessful) {
                                true -> {
                                    val loginResponse = response.body()!!
                                    sharedPreferencesUtil.saveSettings(Gson().toJson(loginResponse.settings))
                                }

                                false -> {

                                }
                            }
                        } else {

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