@file:Suppress("UnstableApiUsage")

package com.connect.meetupsfellow.mvp.view.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.databinding.ActivityViewPagerImageBinding
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.view.adapter.ImageAdapter
import com.connect.meetupsfellow.mvp.view.adapter.ImageAdapterPvt
import com.connect.meetupsfellow.mvp.view.model.PrivateAlbumModel
import com.connect.meetupsfellow.retrofit.response.ResponsePrivatePics
import com.connect.meetupsfellow.retrofit.response.ResponseUserProfileImages
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class ImagePagerActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.component!!.inject(this@ImagePagerActivity)
    }

    private var images = arrayListOf<ResponseUserProfileImages>()
    private var imagesPvt = arrayListOf<ResponsePrivatePics>()
    private var position = 0
    private var className = ""
    private var Type = "video"
    private var binding: ActivityViewPagerImageBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIntentData()
        binding = ActivityViewPagerImageBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        // setContentView(R.layout.activity_view_pager_image)
        ArchitectureApp.component!!.inject(this@ImagePagerActivity)
        init()
        Log.e("ImagePagerActivity%", "INIT_Call")

    }

    private fun getIntentData() {
        when (intent.hasExtra(Constants.IntentDataKeys.ProfileImages)) {
            true -> {
                val image = Gson().fromJson<ArrayList<ResponseUserProfileImages>>(
                    intent.getStringExtra(Constants.IntentDataKeys.ProfileImages),
                    object : TypeToken<ArrayList<ResponseUserProfileImages>>() {}.type
                )
                if (intent.getStringExtra("Class").equals("PrivateAlbum")) {
                    if (sharedPreferencesUtil.fetchUserProfile().isProMembership) {
                        images.addAll(image)
                    } else if (sharedPreferencesUtil.fetchUserProfile().userId == intent.getStringExtra(
                            Constants.IntentDataKeys.UserId
                        )
                    ) {
                        images.addAll(image)
                    } else {
                        if (image.size > 2) {
                            images.add(image.get(0))
                            images.add(image.get(1))
                        } else {
                            images.addAll(image)
                        }
                    }
                    Log.e("ImagePagerActivity%", "From_Private_Profile")
                } else if (intent.getStringExtra("Class").equals("Profile")) {
                    Log.e("ImagePagerActivity%", "From_Profile")
                    images.addAll(image)
                } else if (intent.getStringExtra("Class").equals("Private")) {

                    val image1 = Gson().fromJson<ArrayList<ResponsePrivatePics>>(
                        intent.getStringExtra(Constants.IntentDataKeys.ProfileImages),
                        object : TypeToken<ArrayList<ResponsePrivatePics>>() {}.type
                    )
                   className = "Private"
                    position = intent.getIntExtra("position", 0)
                    imagesPvt.clear()
                    imagesPvt.addAll(image1)
                    Log.e("userPvtPics", imagesPvt.size.toString())
                }

            }

            false -> {
                if (intent.getStringExtra(Constants.IntentDataKeys.Class) == Constants.IntentDataKeys.PrivateAlbumImages) {
                    val jsonString = intent.getStringExtra(Constants.IntentDataKeys.PrivateAlbumImages)
                    val imageList = Gson().fromJson<ArrayList<ResponsePrivatePics>>(
                        jsonString,
                        object : TypeToken<ArrayList<ResponsePrivatePics>>() {}.type
                    )
                    className = Constants.IntentDataKeys.PrivateAlbumImages
                    Type = intent.getStringExtra(Constants.IntentDataKeys.Type)!!
                    position = intent.getIntExtra("position", 0)
                    imagesPvt.clear()
                    imagesPvt.addAll(imageList)

                    Log.e("userPvtPics", "Select_Array: ${imagesPvt.size}")
                }

            }
        }
    }

    private fun init() {

        if (imagesPvt != null && imagesPvt.isNotEmpty() && className.equals(Constants.IntentDataKeys.PrivateAlbumImages)) {
            Log.e("ImagePagerActivity%", "Path:- ${imagesPvt.get(0).path}")

            binding!!.viewPagerImage.adapter = ImageAdapterPvt(this,imagesPvt,className,Type)
            binding!!.viewPagerImage.currentItem = position
            binding!!.dots.setupWithViewPager(binding!!.viewPagerImage, true)
            binding!!.ivSendData.visibility = View.VISIBLE
            binding!!.ivSendData.setOnClickListener {
                setResult(Activity.RESULT_OK)
                finish()
            }
            binding!!.ivImageClose.setOnClickListener {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }else if (imagesPvt != null && imagesPvt.isNotEmpty()) {
            Log.e("ImagePagerActivity%", "Path:- ${imagesPvt.get(0).path}")

            binding!!.viewPagerImage.adapter = ImageAdapterPvt(this,imagesPvt, className, Type)
            binding!!.viewPagerImage.currentItem = position
            binding!!.dots.setupWithViewPager(binding!!.viewPagerImage, true)
            binding!!.ivImageClose.setOnClickListener {
                finish()
            }
        } else {

            binding!!.viewPagerImage.adapter = ImageAdapter(images)
            binding!!.viewPagerImage.currentItem = position
            binding!!.dots.setupWithViewPager(binding!!.viewPagerImage, true)
            binding!!.ivImageClose.setOnClickListener {
                finish()
            }
        }

        /*if (position == 0){

            back_btn.visibility = View.GONE
        }
        else {

            back_btn.visibility = View.VISIBLE
        }

        if (position == imagesPvt.size-1){

            forward_btn.visibility = View.GONE
        }
        else {

            forward_btn.visibility = View.VISIBLE
        }

        back_btn.setOnClickListener {

            if (position != 0){

                position -= 1
                init()
            }
        }

        forward_btn.setOnClickListener {

            if (position != imagesPvt.size-1){

                position += 1
                init()
            }
        }*/
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
