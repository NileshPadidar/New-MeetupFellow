package com.connect.meetupsfellow.mvp.view.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.JsResult
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.databinding.ActivityWebviewBinding
import com.connect.meetupsfellow.developement.interfaces.ApiConnect
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.global.utils.ToolbarUtils
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.view.dialog.AlertBuyPremium
import com.connect.meetupsfellow.retrofit.response.CommonResponse
import retrofit2.Response
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class WebViewActivity : CustomAppActivityCompatViewImpl() {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    @Inject
    lateinit var apiConnect: ApiConnect

    lateinit var binding: ActivityWebviewBinding

    init {
        ArchitectureApp.component!!.inject(this@WebViewActivity)
    }

    private var coverScreenGravityName = FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER
    )

    private val myWebChromeClient: WebChromeClient = object : WebChromeClient() {
        override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
            if (mCustomView != null) {
                callback!!.onCustomViewHidden()
                return
            }

            binding.fullscreenCustomContent.addView(view!!, coverScreenGravityName)
            mCustomView = view
            binding.linearlayout.visibility = View.GONE
            binding.fullscreenCustomContent.visibility = View.VISIBLE
            binding.fullscreenCustomContent.bringToFront()
        }

        override fun onHideCustomView() {
            if (mCustomView == null) return

            mCustomView!!.visibility = View.GONE
            binding.fullscreenCustomContent.removeView(mCustomView)
            mCustomView = null
            binding.fullscreenCustomContent.visibility = View.GONE
            binding.linearlayout.visibility = View.VISIBLE
        }

        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
            LogManager.logger.i("Dating On DL", "Console Message is : $consoleMessage")
            return true
        }

        override fun onJsAlert(
            view: WebView?, url: String?, message: String?, result: JsResult?
        ): Boolean {
            LogManager.logger.d("Dating On DL", "$message")
            AlertDialog.Builder(view!!.context).setMessage(message).setCancelable(true).show()
            result!!.confirm()
            return true
        }
    }

    private var mCustomView: View? = null

    private var titleText = ""
    private var linkText = ""

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        val view = binding.root
        context = this@WebViewActivity
        getIntentData()
        setContentView(view)

        if (titleText.isEmpty()) {
            finish()
        }
      //  setupActionBar(titleText, true)
        ToolbarUtils.setupActionBar(
            this,
            binding!!.header.toolbar,
            titleText,
            binding!!.header.tvTitle,
            showBackArrow = true,
            activity = this
        )
        /// callForUserProfile()
        binding.becamePro.proText.text = getString(R.string.label_subscribe_to_pro)
        binding.becamePro.proText.text = getString(R.string.trail_text)

        if (!sharedPreferencesUtil.fetchUserProfile().isProMembership && titleText.equals(
                getString(
                    R.string.label_pro_feature
                )
            )
        ) {
            //  became_pro.visibility = View.VISIBLE
            binding.becamePro.proText.visibility = View.VISIBLE
        }


        binding.becamePro.proText.setOnClickListener {
//            showAlertForPremium("Your account has been upgraded to PRO.")
            AlertBuyPremium.Builder(this@WebViewActivity, Constants.DialogTheme.NoTitleBar).build()
                .show()
        }

        Handler().postDelayed({
            val webSettings = binding.activityMainWebview.settings
            webSettings.pluginState = WebSettings.PluginState.ON
            webSettings.javaScriptEnabled = true
            webSettings.domStorageEnabled = true
            webSettings.useWideViewPort = true
            webSettings.loadWithOverviewMode = true
            webSettings.javaScriptCanOpenWindowsAutomatically = true
            webSettings.allowFileAccess = true
            webSettings.allowFileAccessFromFileURLs = true
            webSettings.allowUniversalAccessFromFileURLs = true
            webSettings.loadsImagesAutomatically = true
            webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
//            webSettings.userAgentString = "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB; rv:1.9.1.2) " +
//                    "Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)"
            binding.activityMainWebview.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            binding.activityMainWebview.loadUrl(linkText)
            binding.activityMainWebview.webViewClient = MyWebViewClient()
        }, 100)
    }

    private fun getIntentData() {
        if (intent.hasExtra(Constants.IntentDataKeys.TITLE)) {
            titleText = intent.getStringExtra(Constants.IntentDataKeys.TITLE).toString()
            linkText = intent.getStringExtra(Constants.IntentDataKeys.LINK).toString()
        }
    }

    override fun onBackPressed() {
        if (binding.activityMainWebview.canGoBack()) {
            binding.activityMainWebview.goBack()
        } else {
            super.onBackPressed()
        }
    }

    @Suppress("OverridingDeprecatedMember")
    private inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
            webView.webChromeClient = myWebChromeClient
            webView.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            binding.progressBar.visibility = View.GONE
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            runOnUiThread {
                binding.progressBar.visibility = View.VISIBLE
            }
        }

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            AlertDialog.Builder(this@WebViewActivity)
                .setMessage(R.string.notification_error_ssl_cert_invalid)
                .setPositiveButton(getString(R.string.text_continue)) { _, _ -> handler.proceed() }
                .setNegativeButton(getString(R.string.label_cancel_text)) { _, _ -> handler.cancel() }
                .create().show()
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
