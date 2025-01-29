package com.connect.meetupsfellow.mvp.view.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.connect.meetupsfellow.BuildConfig
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.databinding.ActivityLoginBinding
import com.connect.meetupsfellow.developement.interfaces.FirebaseUtil
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.firebase.FirebaseListener
import com.connect.meetupsfellow.global.utils.Connected
import com.connect.meetupsfellow.global.view.otp.OTPListener
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.LoginConnector
import com.connect.meetupsfellow.mvp.presenter.activity.LoginPresenter
import com.connect.meetupsfellow.mvp.view.model.ChatModel
import com.connect.meetupsfellow.mvp.view.model.ConversationModel
import com.connect.meetupsfellow.retrofit.request.RequestUserAuth
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Suppress("UNUSED_PARAMETER", "SENSELESS_COMPARISON")
class LoginActivity : CustomAppActivityCompatViewImpl() {


    private val firebaseCallback =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                Log.e("Error:", "phoneAuthCredential: " + phoneAuthCredential)
                if (null == phoneAuthCredential) {
                    loginUser()
                } else {
                    val code = phoneAuthCredential.smsCode
                    if (null != code) {
                        binding!!.verify.otpView.otp = code
//                        hideKeyboard(etPhoneNumber)
//                        verificationCode = binding!!.verify.otpView.otp
//                        setSelected(verify)
//                        authenticateOtp()
                    } else {
                        loginUser()
                    }
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                e.printStackTrace()
                universalToast("${e.message}")
                Log.e("Error:", "Firebase_Fail: " + e.message)
                binding!!.verify.otpSr.visibility = View.GONE
                hideProgressView(progressBar)
            }

            override fun onCodeSent(
                verificationId: String, token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)
                this@LoginActivity.verificationId = verificationId
                this@LoginActivity.resendingToken = token
                hideProgressView(progressBar)
                universalToast("Verification code sent successfully.")
                Log.e("Error:", "onCodeSend- Verify ")
                binding!!.verify.otpView.otp = ""
                setSelected(binding!!.verify.otpSr)
            }
        }

    private val termsOfServicesClick = object : ClickableSpan() {
        override fun onClick(view: View) {
            when (Connected.isConnected()) {
                true -> {
                    switchActivity(Constants.Intent.Webview, false, Bundle().apply {
                        putString(
                            Constants.IntentDataKeys.TITLE,
                            getString(R.string.label_conditions_text)
                        )
                        Log.e("API", "term_&_Con")
                        putString(
                            Constants.IntentDataKeys.LINK,
                            Constants.Firebase.Term_Condition_URL
                        )
                    })
                }

                false -> {
                    universalToast(getString(R.string.internet_not_connected))
                }
            }
        }
    }

    private val privacyPolicyClick = object : ClickableSpan() {
        override fun onClick(view: View) {
            when (Connected.isConnected()) {
                true -> {
                    switchActivity(Constants.Intent.Webview, false, Bundle().apply {
                        putString(
                            Constants.IntentDataKeys.TITLE, getString(R.string.label_privacy_text)
                        )
                        putString(Constants.IntentDataKeys.LINK, BuildConfig.TERMS)
                    })
                }

                false -> {
                    universalToast(getString(R.string.internet_not_connected))
                }
            }
        }
    }

    private val checkedChangeListener: CompoundButton.OnCheckedChangeListener =
        CompoundButton.OnCheckedChangeListener { _, isChecked ->
            binding!!.login.cbTermsPolicy.isChecked = isChecked
        }

    private val presenter = object : LoginConnector.RequiredViewOps {
        override fun showResponse(response: CommonResponse, type: ConstantsApi) {

            sharedPreferencesUtil.premiunNearby(response.userInfo.isProMembership.toString())
            when (type) {
                ConstantsApi.FETCH_PROFILE -> {
                    // switchActivity(Constants.Intent.Home, true, Bundle())
                    val intent = Intent(this@LoginActivity,HomeActivity::class.java)
                    startActivity(intent,null)
                    finish()
                    hideProgressView(progressBar)
                }

                ConstantsApi.LOGIN -> {
                    when (response.status) {
                        Constants.ProfileStatus.Approved -> {
                            hideProgressView(progressBar)
                            profile()
                        }

                        Constants.ProfileStatus.Blocked -> {
                            showAlertForStatusRejected(
                                response.message, response.status.uppercase(Locale.getDefault())
                            )
                            hideProgressView(progressBar)
                        }

                        Constants.ProfileStatus.Unapproved -> {
                            universalToast(response.message)
                            switchActivity(Constants.Intent.Edit, false, Bundle())
                            Log.e("Error:", "Presenter_ Login1")
                            setSelected(binding!!.login.llTop)
                            hideProgressView(progressBar)
                        }

                        Constants.ProfileStatus.Pending -> {
                            val bundle = Bundle()
                            bundle.putString(
                                Constants.IntentDataKeys.User_Phone,
                                binding!!.login.etPhoneNumber.text.toString().trim()
                            )
                            bundle.putString(
                                Constants.IntentDataKeys.Country_Code,
                                binding!!.login.countryCodePicker.selectedCountryNameCode
                            )
                            bundle.putString(Constants.IntentDataKeys.User_email, "")
                            bundle.putBoolean(Constants.IntentDataKeys.Is_Verify, true)
                            //   switchActivity(Constants.Intent.Create, true, bundle)
                            val intent = Intent(this@LoginActivity,CreateProfileActivityNew::class.java)
                            startActivity(intent,bundle)
                            finish()
                            Log.e("Error:", "Presenter_ Login1")
                            hideProgressView(progressBar)
                        }

                        else -> {
                            when (response.created) {
                                0 -> {

                                    val bundle = Bundle()
                                    bundle.putString(
                                        Constants.IntentDataKeys.User_Phone,
                                        binding!!.login.etPhoneNumber.text.toString().trim()
                                    )
                                    bundle.putString(
                                        Constants.IntentDataKeys.Country_Code,
                                        binding!!.login.countryCodePicker.selectedCountryNameCode
                                    )
                                    bundle.putString(Constants.IntentDataKeys.User_email, "")
                                    bundle.putBoolean(Constants.IntentDataKeys.Is_Verify, true)
                                    //  switchActivity(Constants.Intent.Create, true, bundle)
                                    val intent = Intent(this@LoginActivity,CreateProfileActivityNew::class.java)
                                    startActivity(intent,bundle)
                                    finish()
                                }

                                else -> {
                                    showAlertForStatusRejected(
                                        response.message,
                                        response.status.uppercase(Locale.getDefault())
                                    )
                                }
                            }
                            hideProgressView(progressBar)
                        }
                    }
                }

                else -> {
                    hideProgressView(progressBar)
                }
            }
        }

        override fun showToast(error: String, logout: Boolean?) {
            when (logout) {
                true -> {
                    logout()
                }

                false -> {}
                null ->{}
            }
            universalToast(error)
            hideProgressView(progressBar)
        }
    }

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var firebaseUtil: FirebaseUtil

    private var mPresenter: LoginConnector.PresenterOps? = null

    private var mAuth: FirebaseAuth? = null
    private var requestUserAuth = RequestUserAuth()

    private var verificationCode = ""
    private var verificationId = ""
    private var resendingToken: PhoneAuthProvider.ForceResendingToken? = null
    private var binding: ActivityLoginBinding? = null
    private lateinit var progressBar: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_login)
        context = this@LoginActivity
        component.inject(this@LoginActivity)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        progressBar = binding!!.includedLoading.rlProgress
        mAuth = FirebaseAuth.getInstance()
        //  mAuth!!.setLanguageCode("fr")
        startLoadingAnim()
//        sharedPreferencesUtil.premiunNearby("true")
        init()
    }

    private fun startLoadingAnim() {

        val animation = android.view.animation.AnimationUtils.loadAnimation(
            applicationContext, R.anim.blink_anim
        )

        binding!!.includedLoading.animLogo.startAnimation(animation)
    }

    override fun onResume() {
        super.onResume()
        getCurrentVersion()
        checkRequiredPermissions()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        binding!!.login.llNumber.background =
            resources.getDrawable(R.drawable.round_corners_default)

        when {
            binding!!.login.llTop.visibility == View.VISIBLE -> {
                finish()/*showAlert(
                    getString(R.string.alert_exit),
                    getString(R.string.alert_title_exit)
                )*/
            }

            binding!!.verify.otpSr.visibility == View.VISIBLE -> {
                binding!!.verify.otpView.otp = ""
                Log.e("Error:", "Presenter_ Login222")
                setSelected(binding!!.login.llTop)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {

        makeLinks(
            binding!!.login.tvTermsPolicy,
            arrayOf(getString(R.string.text_terms)),
            arrayOf(termsOfServicesClick)
        )

        binding!!.login.cbTermsPolicy.setOnCheckedChangeListener(checkedChangeListener)

        binding!!.login.countryCodePicker.registerCarrierNumberEditText(binding!!.login.etPhoneNumber)
        //  binding!!.login.countryCodePicker.setCountryForNameCode(Locale.getDefault().country)
        binding!!.login.countryCodePicker.setDefaultCountryUsingNameCode("IN")
        binding!!.login.countryCodePicker.setNumberAutoFormattingEnabled(false)
        binding!!.login.etPhoneNumber.afterTextChanged { number ->
            requestUserAuth.phoneNumber = number.replace(" ", "")
        }

        binding!!.verify.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {

            }

            override fun onOTPComplete(otp: String) {
                hideKeyboard(binding!!.login.etPhoneNumber)
                verificationCode = otp
                authenticateOtp()
            }
        }

        binding!!.login.etPhoneNumber.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                binding!!.login.llNumber.background =
                    resources.getDrawable(R.drawable.rounded_corners_new)

                return false
            }

        })

        /* val paint = tvResendOtp.paint
         val width = paint.measureText(tvResendOtp.text.toString())
         val textShader: Shader = LinearGradient(0f, 0f, width, tvResendOtp.textSize, intArrayOf(
             Color.parseColor("#F97C3C"),
             Color.parseColor("#FDB54E"),
             *//*Color.parseColor("#64B678"),
            Color.parseColor("#478AEA"),*//*
            Color.parseColor("#8446CC")
        ), null, Shader.TileMode.REPEAT)

        tvResendOtp.paint.setShader(textShader)*/

        binding!!.verify.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setSelected(view: View) {
        binding!!.login.llTop.visibility = View.GONE
        binding!!.verify.otpSr.visibility = View.GONE
        view.visibility = View.VISIBLE
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        binding!!.login.llNumber.background =
            resources.getDrawable(R.drawable.round_corners_default)

        return false
    }

    fun onVerifyClick(view: View) {

        when (validatePhoneNumber()) {
            true -> {
                when (binding!!.login.cbTermsPolicy.isChecked) {
                    true -> {

                        /*if("+17543045155" == countryCodePicker.fullNumberWithPlus){
                            //Toast.makeText(this, "Number Matched", Toast.LENGTH_SHORT).show()
                            loginUser()
                        }*/
                        if ("+919977699665" == binding!!.login.countryCodePicker.fullNumberWithPlus) {
                            //Toast.makeText(this, "Number Matched", Toast.LENGTH_SHORT).show()  9977699664
                            loginUser()
                        } else if ("+919522360018" == binding!!.login.countryCodePicker.fullNumberWithPlus) {
                            // newdemo_user 919522360018
                            //Toast.makeText(this, "Number Matched", Toast.LENGTH_SHORT).show() // +919713254069
                            loginUser()

                        } else if ("+917000107878" == binding!!.login.countryCodePicker.fullNumberWithPlus) {
                            //Toast.makeText(this, "Number Matched", Toast.LENGTH_SHORT).show() //**  TestUser
                            loginUser()

                        } else if ("+918982261876" == binding!!.login.countryCodePicker.fullNumberWithPlus) {
                            /// this user without profile   //** Android developer
                            //Toast.makeText(this, "Number Matched", Toast.LENGTH_SHORT).show()
                            loginUser()

                        } else if ("+919926957618" == binding!!.login.countryCodePicker.fullNumberWithPlus) {
                            /// this user without profile   //** Android developer number UPDATE developer nilu
                            //Toast.makeText(this, "Number Matched", Toast.LENGTH_SHORT).show()
                            loginUser()

                        }/*if("+919131636765" == countryCodePicker.fullNumberWithPlus){
                            //Toast.makeText(this, "Number Matched", Toast.LENGTH_SHORT).show()
                            loginUser()

                        }*/
                        else {
                            // verify.visibility = View.VISIBLE
                            ///  loginUser()
                            showProgressView(progressBar)
                            authenticatePhoneNumber()
                        }
                    }

                    false -> {
                        universalToast(getString(R.string.text_please_accept))
                    }
                }

            }

            false -> {
                universalToast("Invalid number : ${binding!!.login.countryCodePicker.fullNumberWithPlus}")
            }
        }

    }

    private fun validatePhoneNumber(): Boolean {
        return binding!!.login.countryCodePicker.isValidFullNumber
    }

    private fun authenticatePhoneNumber() {
        Log.e(
            "Error:",
            "authenticatePhoneNumber: " + binding!!.login.countryCodePicker.fullNumberWithPlus
        )/*  PhoneAuthProvider.getInstance().verifyPhoneNumber(
              countryCodePicker.fullNumberWithPlus, // Phone number to verify
              30, // Timeout duration
              TimeUnit.SECONDS, // Unit of timeout
              TaskExecutors.MAIN_THREAD, // Activity (for callback binding)
              firebaseCallback
          )*/
        val options = PhoneAuthOptions.newBuilder(mAuth!!)
            .setPhoneNumber(binding!!.login.countryCodePicker.fullNumberWithPlus) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(firebaseCallback) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun resendOtp() {
        Log.e("Error:", "resendOtp: " + binding!!.login.countryCodePicker.fullNumberWithPlus)/* PhoneAuthProvider.getInstance().verifyPhoneNumber(
             countryCodePicker.fullNumberWithPlus, // Phone number to verify
             30, // Timeout duration
             TimeUnit.SECONDS, // Unit of timeout
             TaskExecutors.MAIN_THREAD, // Activity (for callback binding)
             firebaseCallback,
             resendingToken
         )*/
        val options = PhoneAuthOptions.newBuilder(mAuth!!)
            .setPhoneNumber(binding!!.login.countryCodePicker.fullNumberWithPlus) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(firebaseCallback) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun authenticateOtp() {
        try {
            showProgressView(progressBar)
            val credential = PhoneAuthProvider.getCredential(verificationId, verificationCode)

            signInWithPhoneAuthCredential(credential)
        } catch (e: Exception) {
            hideProgressView(progressBar)
            universalToast("Verification Code is wrong, try again")
            Log.e("Error:", "authenticateOtp: ${e.message}")
        }

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential?) {
        if (null == mAuth) {
            mAuth = FirebaseAuth.getInstance()
        }
        mAuth!!.signInWithCredential(credential!!).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                getSharedPreferences("userPhone", MODE_PRIVATE).edit()
                    .putString("userPhone", binding!!.login.etPhoneNumber.text.toString())
                    .apply()
                loginUser()
            } else {
                hideProgressView(progressBar)
                universalToast("You have entered invalid verification code")
            }
        }
    }

    fun onContinueClick(view: View) {
        when (validateOtp()) {
            true -> {
                authenticateOtp()
            }

            false -> {}
        }
    }

    private fun validateOtp(): Boolean {
        return when {
            (binding!!.verify.otpView.otp.isEmpty()) -> {
                universalToast("Please enter verification code")
                false
            }

            (binding!!.verify.otpView.otp.length < 6) -> {
                universalToast("Entered verification code incorrect")
                false
            }

            else -> true
        }
    }

    private fun loginUser() {
        val deviceToken = sharedPreferencesUtil.fetchDeviceToken()
        LogManager.logger.e(ArchitectureApp.instance!!.tag, "Device Token is ::: $deviceToken   ")
        when (deviceToken.isEmpty()) {
            true -> {
                fetchDeviceToken()
            }

            false -> {
                requestUserAuth.deviceToken = deviceToken
                login()
            }
        }
    }

    private fun login() {
        if (null == mPresenter) {
            mPresenter = LoginPresenter(presenter)
        }
        sharedPreferencesUtil.saveUserPhoneNumberPrivate(binding!!.login.countryCodePicker.fullNumberWithPlus)
        //  Log.e("Number",""+ countryCodePicker.fullNumberWithPlus)

        val location = sharedPreferencesUtil.fetchLocation()
        requestUserAuth.currentLat = if (location.latitude == 0.0) "" else "${location.latitude}"
        requestUserAuth.currentLong = if (location.longitude == 0.0) "" else "${location.longitude}"
        requestUserAuth.currentLocation = sharedPreferencesUtil.fetchLocationName()
        requestUserAuth.countryCode = binding!!.login.countryCodePicker.selectedCountryCodeWithPlus
        requestUserAuth.isoCode = binding!!.login.countryCodePicker.selectedCountryNameCode

        LogManager.logger.i(
            ArchitectureApp.instance!!.tag, "Auth Details::: ${Gson().toJson(requestUserAuth)}"
        )
        run {
            showProgressView(progressBar)
            mPresenter!!.addUserLoginAuthObject(requestUserAuth)
            mPresenter!!.callRetrofit(ConstantsApi.LOGIN)
        }
    }

    private fun profile() {
        showProgressView(progressBar)
        if (null == mPresenter) mPresenter = LoginPresenter(presenter)

        run {
            showProgressView(progressBar)
            mPresenter!!.callRetrofit(ConstantsApi.FETCH_PROFILE)
        }
    }

    private fun settings() {
//        showProgressView(progressBar)
        if (null == mPresenter) mPresenter = LoginPresenter(presenter)

        run {
            mPresenter!!.callRetrofit(ConstantsApi.SETTINGS)
        }
    }

    /* private fun fetchDeviceToken() {
         FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
             LogManager.logger.i(
                 ArchitectureApp.instance!!.tag, "Device_Token_isss ::: ${instanceIdResult.token}"
             )
             sharedPreferencesUtil.saveDeviceToken(instanceIdResult.token)
             loginUser()
         }
     }*/

    private fun fetchDeviceToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                LogManager.logger.e(
                    ArchitectureApp.instance!!.tag,
                    "Fetching FCM device token failed: ${task.exception}"
                )
                return@addOnCompleteListener
            }

            // Get the FCM device token
            val token = task.result
            LogManager.logger.i(
                ArchitectureApp.instance!!.tag,
                "Device_Token_isss ::: $token"
            )

            // Save the device token to shared preferences
            sharedPreferencesUtil.saveDeviceToken(token)

            // Continue with the login process
            loginUser()
        }
    }


    fun onResendClick(view: View) {
        resendOtp()
    }

    private fun makeLinks(
        textView: TextView, links: Array<String>, clickableSpans: Array<ClickableSpan>
    ) {
        val spannableString = SpannableString(textView.text)
        for (i in links.indices) {
            val clickableSpan = clickableSpans[i]
            val link = links[i]

            val startIndexOfLink = textView.text.toString().indexOf(link)
            spannableString.setSpan(
                clickableSpan,
                startIndexOfLink,
                startIndexOfLink + link.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    private fun fetchConversations() {
        showProgressView(progressBar)
        if (sharedPreferencesUtil.userId().isEmpty()) {
            hideProgressView(progressBar)
            //   switchActivity(Constants.Intent.Home, true, Bundle())
            val intent = Intent(this@LoginActivity,HomeActivity::class.java)
            startActivity(intent,null)
            finish()
            return
        }
        firebaseUtil.fetchFullConversation(sharedPreferencesUtil.userId(),
            object : FirebaseListener {
                override fun onCancelled(error: DatabaseError) {
                    LogManager.logger.i(ArchitectureApp().tag, "DataSnapshot is Error : $error")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    LogManager.logger.i(
                        ArchitectureApp().tag, "DataSnapshot is snapshot Conversation : $snapshot"
                    )
                    LogManager.logger.i(
                        ArchitectureApp.instance!!.tag, "Token is fetchConversation : $snapshot"
                    )
                    when (snapshot.exists()) {
                        true -> {

                            val conversation = snapshot.value as Map<*, *>
                            val data = arrayListOf<ConversationModel>()
                            for ((_, value) in conversation) {
                                try {
                                    LogManager.logger.i(
                                        ArchitectureApp.instance!!.tag,
                                        "Token is fetchConversation value : ${value as Map<*, *>}"
                                    )
                                    val it = Gson().toJson(value)
                                    val item = Gson().fromJson(it, ConversationModel::class.java)
                                    if (item.convoId.isNotEmpty()) fetchUserChat(item.convoId)
                                    when (item.meDeletedConvo) {
                                        false -> {
                                            data.add(item)
                                        }

                                        true -> {}
                                    }
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }
                            }
                            sharedPreferencesUtil.saveConversation(
                                if (data.isNotEmpty()) Gson().toJson(
                                    data
                                ) else ""
                            )

                            hideProgressView(progressBar)
                            //  switchActivity(Constants.Intent.Home, true, Bundle())
                            val intent = Intent(this@LoginActivity,HomeActivity::class.java)
                            startActivity(intent,null)
                            finish()
                        }

                        false -> {
                            sharedPreferencesUtil.saveConversation("")
                            hideProgressView(progressBar)
                            // switchActivity(Constants.Intent.Home, true, Bundle())
                            val intent = Intent(this@LoginActivity,HomeActivity::class.java)
                            startActivity(intent,null)
                            finish()
                        }
                    }
                }
            })
    }

    private fun fetchUserChat(convoId: String) {
        firebaseUtil.fetchFullUserChat(convoId, object : FirebaseListener {
            override fun onCancelled(error: DatabaseError) {
                LogManager.logger.i(ArchitectureApp.instance!!.tag, error.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val message = snapshot.value as Map<*, *>
                    LogManager.logger.i(
                        ArchitectureApp.instance!!.tag, "Token is Chat : ${Gson().toJson(message)}"
                    )
                    val chat = arrayListOf<ChatModel>()
                    for ((key, value) in message) {
                        try {
                            val chatModel =
                                Gson().fromJson(Gson().toJson(value), ChatModel::class.java)
                            chat.add(chatModel)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                    if (chat.isNotEmpty()) {
                        sharedPreferencesUtil.saveUserChat(convoId, Gson().toJson(chat))
                    }
                }
            }

        })
    }

    private fun temp(message: String, title: String) {
        val alertDialog = AlertDialog.Builder(
            this@LoginActivity, R.style.MyDialogTheme2
        )
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton(getString(R.string.text_okay)) { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.setCancelable(false)

        alertDialog.show()
    }
}
