package com.connect.meetupsfellow.mvp.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.databinding.ActivityChangeNumberBinding
import com.connect.meetupsfellow.developement.interfaces.ApiConnect
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.utils.ToolbarUtils
import com.connect.meetupsfellow.global.view.otp.OTPListener
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.connector.activity.ChangeNumberConnector
import com.connect.meetupsfellow.mvp.presenter.activity.ChangeNumberPresenter
import com.connect.meetupsfellow.retrofit.request.RequestUpdateNumber
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.connect.meetupsfellow.retrofit.response.ResponseUserData
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.gson.Gson
import retrofit2.Response
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Suppress("UNUSED_PARAMETER")
class ChangeNumberActivity : CustomAppActivityCompatViewImpl() {

    private val presenter = object : ChangeNumberConnector.RequiredViewOps {
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
            universalToast(response.message)
            finish()
            hideProgressView(progressBar)
        }
    }

    private val firebaseCallback =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                if (null == phoneAuthCredential) return
                binding!!.rlVerify.otpView.otp = phoneAuthCredential.smsCode
                Log.e("authenticatePhoneNumber", "OTP: ${phoneAuthCredential.smsCode}")
                this@ChangeNumberActivity.verificationCode = "${phoneAuthCredential.smsCode}"
                authenticateOtp()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                universalToast("${e.message}")
                hideProgressView(progressBar)
            }

            override fun onCodeSent(
                verificationId: String, token: PhoneAuthProvider.ForceResendingToken
            ) {
                this@ChangeNumberActivity.verificationId = "$verificationId"
                this@ChangeNumberActivity.resendingToken = token
                hideProgressView(progressBar)
                Log.e("authenticatePhoneNumber", "OTP: $verificationCode")
                Log.e("authenticatePhoneNumber", "OTP: ")
                universalToast("Verification code sent successfully.")
                binding!!.rlVerify.otpView.otp = ""
                setSelected(binding!!.rlVerify.otpSr)
            }
        }

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var apiConnect: ApiConnect

    private var mPresenter: ChangeNumberConnector.PresenterOps? = null

    private var requestUpdateNumber = RequestUpdateNumber()

    private var mAuth: FirebaseAuth? = null

    private var verificationCode = ""
    private lateinit var btnNext: AppCompatButton
    private var verificationId = ""
    private var resendingToken: PhoneAuthProvider.ForceResendingToken? = null
    private var binding: ActivityChangeNumberBinding? = null
    private lateinit var progressBar: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeNumberBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        // setContentView(R.layout.activity_change_number)
        component.inject(this@ChangeNumberActivity)
       // setupActionBar(getString(R.string.title_change_number), true)
        ToolbarUtils.setupActionBar(
            this,
            binding!!.header.toolbar,
            getString(R.string.title_change_number),
            binding!!.header.tvTitle,
            showBackArrow = true,
            activity = this
        )
        mAuth = FirebaseAuth.getInstance()
        mAuth!!.setLanguageCode("fr")
        startLoadingAnim()
        btnNext = findViewById(R.id.btnNext)
        init()
        ///   callForUserProfile()
        btnNext.setOnClickListener(View.OnClickListener {
            Log.e("Click:", " nextBtn")
            when (validateNumbers()) {
                true -> {
                    showProgressView(progressBar)
                    authenticatePhoneNumber()
                }

                false -> {}
            }
        })
    }

    private fun startLoadingAnim() {

        val animation = android.view.animation.AnimationUtils.loadAnimation(
            applicationContext, R.anim.blink_anim
        )

        binding!!.includedLoading.animLogo.startAnimation(animation)
    }

    private fun init() {
        setSelected(binding!!.rlChangeNumber.rlChangeNumberTop)

        binding!!.rlChangeNumber.countryCodePickerNew.registerCarrierNumberEditText(binding!!.rlChangeNumber.etPhoneNumberNew)
        binding!!.rlChangeNumber.countryCodePickerNew.setCountryForNameCode(sharedPreferencesUtil.fetchUserProfile().isoCode)
        binding!!.rlChangeNumber.countryCodePickerNew.setNumberAutoFormattingEnabled(false)

        binding!!.rlChangeNumber.countryCodePickerOld.registerCarrierNumberEditText(binding!!.rlChangeNumber.etPhoneNumberOld)
        binding!!.rlChangeNumber.countryCodePickerOld.setCountryForNameCode(sharedPreferencesUtil.fetchUserProfile().isoCode)
        binding!!.rlChangeNumber.countryCodePickerOld.setNumberAutoFormattingEnabled(false)

        binding!!.rlVerify.ivBack.visibility = View.GONE

        binding!!.rlChangeNumber.etPhoneNumberNew.afterTextChanged { number ->
            requestUpdateNumber.number = number.replace(" ", "")
        }

        binding!!.rlVerify.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
            }

            override fun onOTPComplete(otp: String) {
                hideKeyboard(binding!!.rlChangeNumber.etPhoneNumberNew)
                verificationCode = otp
                authenticateOtp()
            }
        }
    }

    private fun setSelected(view: View) {
        binding!!.rlVerify.otpSr.visibility = View.GONE
        binding!!.rlChangeNumber.rlChangeNumberTop.visibility = View.GONE

        view.visibility = View.VISIBLE
    }

    fun onContinueClick(view: View) {
        when (validateOtp()) {
            true -> {
                authenticateOtp()
            }

            false -> {}
        }
    }

    fun onResendClick(view: View) {
        showProgressView(progressBar)
        resendOtp()
    }

    fun onNextClick(view: View) {/* when (validateNumbers()) {
            true -> {
                showProgressView(progressBar)
                authenticatePhoneNumber()
            }
        }*/
    }

    private fun authenticatePhoneNumber() {
        Log.e(
            "ChangeNumAct:",
            "authenticatePhoneNumber: " + binding!!.rlChangeNumber.countryCodePickerNew.fullNumberWithPlus
        )/* PhoneAuthProvider.getInstance().verifyPhoneNumber(
            countryCodePickerNew.fullNumberWithPlus, // Phone number to verify
            30, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            TaskExecutors.MAIN_THREAD, // Activity (for callback binding)
            firebaseCallback
        )*/
        val options = PhoneAuthOptions.newBuilder(mAuth!!)
            .setPhoneNumber(binding!!.rlChangeNumber.countryCodePickerNew.fullNumberWithPlus) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(firebaseCallback) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun resendOtp() {/* PhoneAuthProvider.getInstance().verifyPhoneNumber(
            countryCodePickerNew.fullNumberWithPlus, // Phone number to verify
            30, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            TaskExecutors.MAIN_THREAD, // Activity (for callback binding)
            firebaseCallback,
            resendingToken
        )*/
        val options = PhoneAuthOptions.newBuilder(mAuth!!)
            .setPhoneNumber(binding!!.rlChangeNumber.countryCodePickerNew.fullNumberWithPlus) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(firebaseCallback) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun validateOtp(): Boolean {
        return when {
            (binding!!.rlVerify.otpView.otp.isEmpty()) -> {
                universalToast("Please enter verification code")
                false
            }

            (binding!!.rlVerify.otpView.otp.length < 6) -> {
                universalToast("Entered verification code incorrect")
                false
            }

            else -> true
        }
    }

    private fun authenticateOtp() {
        showProgressView(progressBar)
        val credential = PhoneAuthProvider.getCredential(verificationId, verificationCode)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential?) {
        if (null == mAuth) {
            mAuth = FirebaseAuth.getInstance()
        }
        mAuth!!.signInWithCredential(credential!!).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    changeNumber()
                } else {
                    hideProgressView(progressBar)
                    universalToast("You have entered invalid verification code")
                }
            }
    }

    override fun onBackPressed() {
        when {
            binding!!.rlChangeNumber.rlChangeNumberTop.visibility == View.VISIBLE -> {
                super.onBackPressed()
            }

            binding!!.rlVerify.otpSr.visibility == View.VISIBLE -> {
                setSelected(binding!!.rlChangeNumber.rlChangeNumberTop)
            }
        }
    }

    private fun validateNumbers(): Boolean {
        if (binding!!.rlChangeNumber.etPhoneNumberOld.text.isEmpty()) {
            binding!!.rlChangeNumber.etPhoneNumberOld.error = "Number cannot be empty"
            universalToast("Number cannot be empty")
            return false
        }

        if (binding!!.rlChangeNumber.etPhoneNumberNew.text.isEmpty()) {
            binding!!.rlChangeNumber.etPhoneNumberNew.error = "Number cannot be empty"
            universalToast("Number cannot be empty")
            return false
        }

        if (!binding!!.rlChangeNumber.countryCodePickerOld.isValidFullNumber) {
            binding!!.rlChangeNumber.etPhoneNumberOld.error = "Invalid number"
            universalToast("Invalid number")
            return false
        }

        if (!binding!!.rlChangeNumber.countryCodePickerNew.isValidFullNumber) {
            binding!!.rlChangeNumber.etPhoneNumberNew.error = "Invalid number"
            universalToast("Invalid number")
            return false
        }

        if (binding!!.rlChangeNumber.countryCodePickerOld.fullNumberWithPlus != sharedPreferencesUtil.fetchUserPhoneNumberPrivate()
                .replace(
                    " ", ""
                )
        ) {
            binding!!.rlChangeNumber.etPhoneNumberOld.error =
                "Number doesn't matches with registered number"
            universalToast("Number doesn't matches with registered number")
            Log.e("ChangeNo*", "oldNo: ${sharedPreferencesUtil.fetchUserPhoneNumberPrivate()}")
            return false
        }

        if (binding!!.rlChangeNumber.countryCodePickerOld.fullNumberWithPlus == binding!!.rlChangeNumber.countryCodePickerNew.fullNumberWithPlus) {
            universalToast("Both numbers cannot be the same")
            return false
        }
        return true
    }

    private fun changeNumber() {
        requestUpdateNumber.countryCode =
            binding!!.rlChangeNumber.countryCodePickerNew.selectedCountryCodeWithPlus
        requestUpdateNumber.isoCode =
            binding!!.rlChangeNumber.countryCodePickerNew.selectedCountryNameCode

        LogManager.logger.i(
            ArchitectureApp.instance!!.tag, "Change Number : ${Gson().toJson(requestUpdateNumber)}"
        )

        if (null == mPresenter) mPresenter = ChangeNumberPresenter(presenter)

        run {
            mPresenter!!.addChangeNumberObject(requestUpdateNumber)
            mPresenter!!.callRetrofit(ConstantsApi.UPDATE_NUMBER)
        }

//        hideProgressView(progressBar)
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
                                    val profile: ResponseUserData? = response?.userInfo
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

    }


}