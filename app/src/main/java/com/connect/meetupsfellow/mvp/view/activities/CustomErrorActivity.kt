package com.connect.meetupsfellow.mvp.view.activities

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import cat.ereza.customactivityoncrash.CustomActivityOnCrash
import com.connect.meetupsfellow.R

class CustomErrorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)
        val llRestart = findViewById<LinearLayout>(R.id.llRestart)
        val config = CustomActivityOnCrash.getConfigFromIntent(intent)
        if (config!!.isShowRestartButton && config.restartActivityClass != null) {
            llRestart.setOnClickListener {
                CustomActivityOnCrash.restartApplication(
                    this@CustomErrorActivity, config
                )
            }
        } else {
            llRestart.setOnClickListener {
                CustomActivityOnCrash.closeApplication(
                    this@CustomErrorActivity, config
                )
            }
        }
    }

}
