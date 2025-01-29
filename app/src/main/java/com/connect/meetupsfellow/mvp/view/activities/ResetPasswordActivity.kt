package com.connect.meetupsfellow.mvp.view.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.databinding.ActivityResetPasswordBinding
import com.connect.meetupsfellow.mvp.connector.activity.LoginConnector
import com.connect.meetupsfellow.mvp.presenter.activity.LoginPresenter
import com.connect.meetupsfellow.retrofit.request.RequestResetPassword
import com.connect.meetupsfellow.retrofit.response.CommonResponse

class ResetPasswordActivity : CustomAppActivityCompatViewImpl() {

    private var user_email = ""
    private var verify_code = ""
    private val requestResetPassword = RequestResetPassword()
    private var mPresenter: LoginConnector.PresenterOps? = null

    private var binding: ActivityResetPasswordBinding? = null
    private lateinit var progressBar: LinearLayout

    private val presenter = object : LoginConnector.RequiredViewOps {
        override fun showResponse(response: CommonResponse, type: ConstantsApi) {

            when (type) {
                ConstantsApi.RESET_PASSWORD -> {
                    //  switchActivity(Constants.Intent.Home, true, Bundle())
                    if (response.message.equals("Your Password has been reseted successfully.")) {
                        finish()
                    }
                    universalToast(response.message)
                    hideProgressView(progressBar)
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

                false -> TODO()
                null -> TODO()
            }
            universalToast(error)
            hideProgressView(progressBar)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_reset_password)
        component.inject(this@ResetPasswordActivity)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        progressBar = binding!!.includedLoading.rlProgress
        context = this@ResetPasswordActivity
        getIntentData()
        init()
    }

    fun init() {
        Log.e("Activity", "ResetPassword_New")
        binding!!.createNewPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (!binding!!.createNewPassword.text.toString().trim().isEmpty()) {
                    binding!!.createNewPassword.background =
                        resources.getDrawable(R.drawable.rounded_corners_new)
                } else {
                    binding!!.createNewPassword.background =
                        resources.getDrawable(R.drawable.round_corners_default)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding!!.createNewConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (!binding!!.createNewConfirmPassword.text.toString().trim().isEmpty()) {
                    binding!!.createNewConfirmPassword.background =
                        resources.getDrawable(R.drawable.rounded_corners_new)
                } else {
                    binding!!.createNewConfirmPassword.background =
                        resources.getDrawable(R.drawable.round_corners_default)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding!!.btnChangePassword.setOnClickListener {
            if (binding!!.createNewPassword.text!!.isEmpty()) {
                Toast.makeText(
                    this@ResetPasswordActivity, "password cannot be empty.", Toast.LENGTH_SHORT
                ).show()
                binding!!.createNewPassword.requestFocus()
            } else if (binding!!.createNewPassword.text.toString().trim().length < 4) {
                binding!!.createNewPassword.requestFocus()
                Toast.makeText(
                    this@ResetPasswordActivity,
                    "Password should grater than equal to 4 character.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!isValidPassword(binding!!.createNewPassword.text.toString().trim())) {
                binding!!.createNewPassword.requestFocus()
                Toast.makeText(
                    this@ResetPasswordActivity,
                    "Password should Contains at least one alphabet, one number, and one special character.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding!!.createNewConfirmPassword.text!!.isEmpty()) {
                Toast.makeText(
                    this@ResetPasswordActivity,
                    "Confirm password cannot be empty.",
                    Toast.LENGTH_SHORT
                ).show()
                binding!!.createNewConfirmPassword.requestFocus()
            } else if (binding!!.createNewConfirmPassword.text.toString() != binding!!.createNewPassword.text.toString()) {
                binding!!.createNewConfirmPassword.requestFocus()
                Toast.makeText(
                    this@ResetPasswordActivity, "Please enter same password", Toast.LENGTH_SHORT
                ).show()
            } else {
                ReSetPassword()
                ///Toast.makeText(this@ResetPasswordActivity, "Implement Api", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getIntentData() {
        if (intent.hasExtra(Constants.IntentDataKeys.User_email)) {
            user_email = intent.getStringExtra(Constants.IntentDataKeys.User_email).toString()
            verify_code = intent.getStringExtra(Constants.IntentDataKeys.Verify_Code).toString()
            Log.e("IntentData&", "user_email: $user_email")
            Log.e("IntentData&", "verify_code: $verify_code")
        } else {

            Log.e("IntentData&", "Email_Is_null")
        }

    }

    fun isValidPassword(password: String): Boolean {
        // Regular expression to check if the password contains at least one alphabet, one number, and one special character
        val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{4,12}$"
        val passwordMatcher = Regex(passwordPattern)
        return passwordMatcher.matches(password)
    }


    private fun ReSetPassword() {
        showProgressView(progressBar)
        requestResetPassword.email = user_email
        requestResetPassword.code = verify_code
        requestResetPassword.password = binding!!.createNewPassword.text.toString().trim()
        requestResetPassword.confirm_password =
            binding!!.createNewConfirmPassword.text.toString().trim()
        if (null == mPresenter) mPresenter = LoginPresenter(presenter)

        run {
            showProgressView(progressBar)
            mPresenter!!.addResetPasswordObject(requestResetPassword)
            mPresenter!!.callRetrofit(ConstantsApi.RESET_PASSWORD)
        }
    }
}