package com.connect.meetupsfellow.mvp.view.fragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
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
import com.connect.meetupsfellow.components.fragment.CustomFragment
import com.connect.meetupsfellow.databinding.LayoutTwitterBinding
import com.connect.meetupsfellow.log.LogManager


@Suppress("DEPRECATION")
class TwitterFragment : CustomFragment() {

    private var coverScreenGravityName = FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER
    )

    private val myWebChromeClient: WebChromeClient = object : WebChromeClient() {
        override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
            if (mCustomView != null) {
                callback!!.onCustomViewHidden()
                return
            }
            binding.fullscreenCustomContent.addView(view, coverScreenGravityName)
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
    private var _binding: LayoutTwitterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // return inflater.inflate(R.layout.layout_twitter, container, false)
        _binding = LayoutTwitterBinding.inflate(inflater, container, false)
        return binding.root
    }

    internal fun loadTwitterProfile(url: String) {
        Handler().postDelayed({
            when (url.isEmpty()) {
                true -> {
                    if (isVisible && isAdded) {
                        binding.tvNoTwitter.visibility = View.VISIBLE
                        binding.fullscreenCustomContent.visibility = View.GONE

                        requireActivity().runOnUiThread {
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }


                false -> {
                    if (isVisible && isAdded) {
                        binding.tvNoTwitter.visibility = View.GONE
                        binding.fullscreenCustomContent.visibility = View.VISIBLE

                        requireActivity().runOnUiThread {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        loadUrl(url)
                    }
                }
            }

        }, 100)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadUrl(url: String) {
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
//        webSettings.userAgentString = "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB; rv:1.9.1.2) " +
//                "Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)"
        binding.activityMainWebview.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        binding.activityMainWebview.loadUrl(url)
        binding.activityMainWebview.webViewClient = MyWebViewClient()
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
            if (isVisible && isAdded) activity!!.runOnUiThread {
                binding.progressBar.visibility = View.GONE
            }
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            if (isVisible && isAdded) activity!!.runOnUiThread {
                binding.progressBar.visibility = View.VISIBLE
            }
        }

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            AlertDialog.Builder(ArchitectureApp.instance!!.baseContext)
                .setMessage(R.string.notification_error_ssl_cert_invalid)
                .setPositiveButton(getString(R.string.text_continue)) { _, _ -> handler.proceed() }
                .setNegativeButton(getString(R.string.label_cancel_text)) { _, _ -> handler.cancel() }
                .create().show()
        }
    }
}