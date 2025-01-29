package com.connect.meetupsfellow.mvp.view.activities

import android.os.Bundle
import android.widget.Button
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.components.activity.CustomAppActivityCompatViewImpl
import com.connect.meetupsfellow.constants.Constants


class GetStartActivity : CustomAppActivityCompatViewImpl() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_start)
        val getStart_btn = findViewById<Button>(R.id.getStart_btn)
        val firstTime =
            getSharedPreferences("firstTime", MODE_PRIVATE).getBoolean("firstTime", true)

        if (!firstTime) {
            ///  switchActivity(Constants.Intent.Login, true, Bundle())
            switchActivity(Constants.Intent.LoginWithEmailActivity, true, Bundle())
           // switchActivity(Constants.Intent.LoginWithEmailActivity, true, null)

        }

        getStart_btn.setOnClickListener {

            // getSharedPreferences("firstTime", MODE_PRIVATE).edit().putBoolean("firstTime", false).apply()
            switchActivity(Constants.Intent.Welcome, true, null)
        }

    }
}