package com.connect.meetupsfellow.global.utils

import android.app.Activity
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import com.connect.meetupsfellow.R

/**
 * Created by Shasin on 14/03/2018.
 */

class Banner {

    private val mContext: Context? = null
    private var activity: Activity? = null
    /**
     * This method returns the banner view set by setLayout
     * This method must be invoke after setLayout in order to avoid null pointer
     */
    var bannerView: View? = null
        private set
    private var rootView: View? = null
    /**
     * Banner will be disappear when clicked outside ot the banner, when focusable is true
     * focusable default value is false
     * @param focusable
     */
    var isFocusable: Boolean = false
    var isAsDropDown: Boolean = false
    var isFillScreen: Boolean = false
    private var popupWindow: PopupWindow? = null

    private var showBanner = false
    /**
     * Set the location of the banner to TOP,CENTER,BOTTOM
     * By defauly gravity is set to TOP
     * @param gravity
     */
    var gravity = TOP
    /**
     * Set this delay for showing notification banner
     * By defauly delay is 1500
     * @param delay
     */
    var delay = 1500
    /**
     * Set this to auto dismiss  notification banner
     * By defauly duration is 0
     * @param duration
     */
    var duration = 0
    private var bannerType: Int = 0

    private var textMessage: TextView? = null
    private var rlCancel: RelativeLayout? = null

    private var animationStyle: Int? = null

    private var layout: Int = 0

    private val TAG = javaClass.name

    interface BannerListener {
        fun onViewClickListener(view: View)
    }

    //Constructors
    constructor() {

    }

    constructor(view: View, activity: Activity) {
        // create the popup window
        this.activity = activity
        this.rootView = view
    }


    private fun setBannerLayout(type: Int) {

        bannerType = type
        var result = 0
        when (bannerType) {
            1 -> result = R.layout.success
            2 -> result = R.layout.info
            3 -> result = R.layout.warning
            4 -> result = R.layout.error
        }
        layout = result
    }


    /**
     * This method set textview for the default banners
     * @param text
     */
    private fun setBannerText(text: String) {
        when (bannerType) {
            1 -> {
                textMessage = bannerView!!.findViewById(R.id.success_message)
                textMessage!!.text = text
            }
            2 -> {
                textMessage = bannerView!!.findViewById(R.id.info_message)
                textMessage!!.text = text
            }
            3 -> {
                textMessage = bannerView!!.findViewById(R.id.warning_message)
                textMessage!!.text = text
            }
            4 -> {
                textMessage = bannerView!!.findViewById(R.id.error_message)
                textMessage!!.text = text
            }
        }
    }

    /**
     * Hide close icon if duration is already set
     *
     *
     */
    private fun setCancelButton() {
        if (duration > 0) {
            rlCancel!!.visibility = View.INVISIBLE
        } else {
            rlCancel!!.setOnClickListener { popupWindow!!.dismiss() }
        }
    }

    /**
     * Initialize the banner view
     * @param layout
     */
    fun setLayout(layout: Int) {
        if (activity != null) {
            val inflater = activity!!.baseContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            bannerView = inflater.inflate(layout, null)
            rlCancel = bannerView!!.findViewById(R.id.rlCancel)
        }
    }

    fun dismissBanner() {
        try {
            popupWindow!!.dismiss()
            showBanner = false
            isAsDropDown = false
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }

    }

    /**
     * This method create a new popup window
     * This method must be called after setLayout
     * focusable default value is false
     *
     */
    fun show() {

        if (activity != null) {
            //            if(activity.hasWindowFocus()){  //this will prevent activity not running crash due to async call
            showBanner = true

            val width = LinearLayout.LayoutParams.MATCH_PARENT
            var height = LinearLayout.LayoutParams.WRAP_CONTENT

            if (isFillScreen) {
                height = LinearLayout.LayoutParams.MATCH_PARENT
            }

            popupWindow = PopupWindow(bannerView, width, height, isFocusable)

            if (animationStyle != null) {
                popupWindow!!.animationStyle = animationStyle!!
            }

            rootView!!.post {
                if (isAsDropDown) {
                    popupWindow!!.showAsDropDown(rootView, 0, 0)
                } else {
                    popupWindow!!.showAtLocation(rootView, gravity, 0, 0)
                }
            }

            autoDismiss(duration)
            //            }
        }
    }

    private fun setAnimationstyle() {
        if (gravity == TOP)
            animationStyle = R.style.topAnimation
        else if (gravity == BOTTOM)
            animationStyle = R.style.bottomAnimation
    }

    fun setCustomAnimationStyle(customAnimationStyle: Int) {
        animationStyle = customAnimationStyle
    }


    /**
     * This method auto dismiss banner
     *
     * @param duration
     */
    private fun autoDismiss(duration: Int) {
        if (duration > 0) {
            val handler = android.os.Handler()
            handler.postDelayed({ dismissBanner() }, duration.toLong())
        }
    }

    companion object {

        var TOP = Gravity.TOP
        var BOTTOM = Gravity.BOTTOM

        var SUCCESS = 1
        var INFO = 2
        var WARNING = 3
        var ERROR = 4
        var CUSTOM = 5

        private var instance: Banner? = null

        fun make(view: View, activity: Activity, bannerType: Int, message: String, position: Int): Banner {

            if (instance == null) {
                instance = Banner()
            } else {
                if (instance!!.showBanner) {
                    instance!!.dismissBanner()
                }
            }
            instance!!.rootView = view
            //            instance.mContext = context;
            instance!!.activity = activity
            instance!!.setBannerLayout(bannerType)
            instance!!.setLayout(instance!!.layout)
            instance!!.setBannerText(message)
            instance!!.duration = 0
            instance!!.gravity = position
            instance!!.setCancelButton()
            instance!!.setAnimationstyle()
            instance!!.isFillScreen = false
            instance!!.isAsDropDown = false

            return instance!!
        }

        /**
         * This constructor is used for autodismiss
         *
         */
        fun make(
            view: View,
            activity: Activity,
            bannerType: Int,
            message: String,
            position: Int,
            duration: Int
        ): Banner {
            if (instance == null) {
                instance = Banner()
            } else {
                if (instance!!.showBanner) {
                    instance!!.dismissBanner()
                }
            }
            instance!!.rootView = view
            instance!!.activity = activity
            instance!!.setBannerLayout(bannerType)
            instance!!.setLayout(instance!!.layout)
            instance!!.setBannerText(message)
            instance!!.duration = duration
            instance!!.gravity = position
            instance!!.setCancelButton()
            instance!!.setAnimationstyle()
            instance!!.isFillScreen = false
            instance!!.isAsDropDown = false
            return instance!!
        }

        /**
         * this constructor is used for customlayout
         *
         */
        fun make(view: View, activity: Activity, position: Int, Customlayout: Int): Banner {

            if (instance == null) {
                instance = Banner()
            } else {
                if (instance!!.showBanner) {
                    instance!!.dismissBanner()
                }
            }
            instance!!.rootView = view
            instance!!.activity = activity
            instance!!.setLayout(Customlayout)
            instance!!.duration = 0
            instance!!.gravity = position
            instance!!.isFillScreen = false
            instance!!.isAsDropDown = false

            return instance!!
        }

        /**
         * this constructor is used for customlayout and show notification as dropdown of a view
         *
         */
        fun make(view: View, activity: Activity, position: Int, Customlayout: Int, asDropDown: Boolean): Banner {

            if (instance == null) {
                instance = Banner()
            } else {
                if (instance!!.showBanner) {
                    instance!!.dismissBanner()
                }
            }
            instance!!.rootView = view
            instance!!.activity = activity
            instance!!.setLayout(Customlayout)
            instance!!.duration = 0
            instance!!.gravity = position
            instance!!.isAsDropDown = asDropDown
            instance!!.isFillScreen = false

            return instance!!
        }

        fun make(view: View, activity: Activity, Customlayout: Int, fillScreen: Boolean): Banner {

            if (instance == null) {
                instance = Banner()
            } else {
                if (instance!!.showBanner) {
                    instance!!.dismissBanner()
                }
            }
            instance!!.rootView = view
            instance!!.activity = activity
            instance!!.setLayout(Customlayout)
            instance!!.duration = 0
            instance!!.isFillScreen = fillScreen
            instance!!.isAsDropDown = false

            return instance!!
        }


        fun getInstance(): Banner {
            if (instance == null) {
                instance = Banner()
            }
            return instance!!
        }
    }
}
