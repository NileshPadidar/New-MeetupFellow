package com.connect.meetupsfellow.mvp.view.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.databinding.ActivityForgotPasswordBinding
import com.connect.meetupsfellow.global.view.otp.OTPListener
import com.connect.meetupsfellow.global.view.otp.OtpTextView
import com.connect.meetupsfellow.mvp.connector.activity.LoginConnector
import com.connect.meetupsfellow.mvp.presenter.activity.LoginPresenter
import com.connect.meetupsfellow.retrofit.request.CheckSentEmailCode
import com.connect.meetupsfellow.retrofit.request.ForgetPasswordReq
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*

class ForgotPasswordActivity : CustomAppActivityCompatViewImpl() {
    private var mPresenter: LoginConnector.PresenterOps? = null
    private val ForgetPasswordReq = ForgetPasswordReq()
    private val checkSentEmailCode = CheckSentEmailCode()
    private lateinit var email_code: OtpTextView
    private var verificationCode = ""
    private var flag = false
    private var binding: ActivityForgotPasswordBinding? = null
    private lateinit var progressBar: LinearLayout

    private val presenter = object : LoginConnector.RequiredViewOps {
        override fun showResponse(response: CommonResponse, type: ConstantsApi) {

            when (type) {
                ConstantsApi.FORGET_PASSWORD -> {
                    //  switchActivity(Constants.Intent.Home, true, Bundle())
                    if (!response.message.equals("Please enter valid Email ID.") && !flag) {
                        Log.e("Code#%", "Verify code : ${response.code}")
                        openBottomSheetVerify()
                        flag = true
                    }
                    universalToast(response.message)
                    hideProgressView(progressBar)
                }

                ConstantsApi.CHECK_SENT_EMAIL_CODE -> {
                    universalToast(response.message)
                    hideProgressView(progressBar)
                    if (response.message.equals("Code is Valid.")) {
                        val bundle = Bundle()
                        bundle.putString(
                            Constants.IntentDataKeys.User_email,
                            binding!!.userEmailId.text.toString().trim()
                        )
                        bundle.putString(Constants.IntentDataKeys.Verify_Code, verificationCode)
                          switchActivity(Constants.Intent.ResetPasswordActivity, true, bundle)
                        /*val intent = Intent(this@ForgotPasswordActivity,ResetPasswordActivity::class.java)
                        startActivity(intent,null)*/
                        Log.e("IntentData&", "send_email: ${binding!!.userEmailId.text.toString().trim()}")
                        Log.e("IntentData&", "send_code: $verificationCode")
                        finish()
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
                null -> {}
            }
            universalToast(error)
            hideProgressView(progressBar)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        //setContentView(R.layout.activity_forgot_password)
        progressBar = binding!!.includedLoading.rlProgress
        component.inject(this@ForgotPasswordActivity)
        context = this@ForgotPasswordActivity

        init()
    }

    fun init() {
        Log.e("Activity", "ForgotPassword_New")
        binding!!.userEmailId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (!binding!!.userEmailId.text.toString().trim().isEmpty()) {
                    binding!!.userEmailId.background =
                        resources.getDrawable(R.drawable.rounded_corners_new)
                } else {
                    binding!!.userEmailId.background =
                        resources.getDrawable(R.drawable.round_corners_default)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding!!.ivBackForgot.setOnClickListener {
            finish()
        }

        binding!!.btnForgotContinue.setOnClickListener {
            if (binding!!.userEmailId.text!!.isEmpty()) {
                Toast.makeText(
                    this@ForgotPasswordActivity, "Email cannot be empty.", Toast.LENGTH_SHORT
                ).show()

            } else if (!isValidEmail(binding!!.userEmailId.text.toString())) {
                Toast.makeText(
                    this@ForgotPasswordActivity, "Please Enter valid email.", Toast.LENGTH_SHORT
                ).show()

            } else {
                ///  switchActivity(Constants.Intent.ResetPasswordActivity, false, Bundle())
                /// Toast.makeText(this@ForgotPasswordActivity, "Call Api", Toast.LENGTH_SHORT).show()
                ForgetPassword()
            }
        }
    }

    private fun isValidEmail(email: CharSequence?): Boolean {
        return !email.isNullOrEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }

    private fun ForgetPassword() {
        showProgressView(progressBar)
        ForgetPasswordReq.email = binding!!.userEmailId.text.toString()
        if (null == mPresenter) mPresenter = LoginPresenter(presenter)

        run {
            showProgressView(progressBar)
            mPresenter!!.addUserForgetPasswordObject(ForgetPasswordReq)
            mPresenter!!.callRetrofit(ConstantsApi.FORGET_PASSWORD)
        }
    }

    private fun verifyEmailCode() {
        showProgressView(progressBar)
        checkSentEmailCode.email = binding!!.userEmailId.text.toString()
        checkSentEmailCode.code = verificationCode
        if (null == mPresenter) mPresenter = LoginPresenter(presenter)

        run {
            showProgressView(progressBar)
            mPresenter!!.addCheckSentCodeObject(checkSentEmailCode)
            mPresenter!!.callRetrofit(ConstantsApi.CHECK_SENT_EMAIL_CODE)
        }
    }

    private fun openBottomSheetVerify() {
        val dialog = BottomSheetDialog(this, R.style.BottomSheetStyle)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_verify_otp, null)
        val btnClose = view.findViewById<ImageView>(R.id.close)
        val btnVerifyOtp = view.findViewById<AppCompatButton>(R.id.btn_otp_verify)
        val titleText = view.findViewById<TextView>(R.id.tv_cantaint)
        email_code = view.findViewById<OtpTextView>(R.id.phone_otp_view)
        val resendOtp = view.findViewById<TextView>(R.id.tvResendOtp)

        titleText.text = "we have sent the code verification \n to your email."
        dialog.setCancelable(false)

        btnClose.setOnClickListener {
            dialog.setCancelable(true)
            flag = false
            dialog.dismiss()
        }

        resendOtp.setOnClickListener {
            universalToast("resend Code")
            ForgetPassword()
        }
        btnVerifyOtp.setOnClickListener {
            if (verificationCode.isNotEmpty()) {
                verifyEmailCode()
                flag = false
                dialog.setCancelable(true)
                dialog.dismiss()
            }
        }
        email_code.otpListener = object : OTPListener {
            override fun onInteractionListener() {
            }

            override fun onOTPComplete(otp: String) {
                verificationCode = otp
            }
        }

        dialog.setContentView(view)

        dialog.show()
    }

}

