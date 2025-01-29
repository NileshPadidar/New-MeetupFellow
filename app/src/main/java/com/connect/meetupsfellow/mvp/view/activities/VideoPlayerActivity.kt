package com.connect.meetupsfellow.mvp.view.activities

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.databinding.ActivityVideoPlayerBinding


class VideoPlayerActivity : AppCompatActivity() {

    private var videoUrl = ""
    private var binding: ActivityVideoPlayerBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        //setContentView(R.layout.activity_video_player)
        getIntentData()
        init()
    }

    private fun getIntentData() {
        when (intent.hasExtra(Constants.IntentDataKeys.VideoUrl)) {
            true -> {
                videoUrl = intent.getStringExtra(Constants.IntentDataKeys.VideoUrl).toString()
                Log.e("ActivityVideo", "personal_Video_URL: " + videoUrl)
            }

            false -> {}
        }
        when (intent.hasExtra(Constants.IntentDataKeys.ImgPath)) {

            true -> {
                videoUrl = intent.getStringExtra(Constants.IntentDataKeys.ImgPath).toString()
                Log.e("ActivityVideo", "personal_IMG_URL: " + videoUrl)
            }

            false -> {}
        }
    }

    private fun init() {
        if (videoUrl.isEmpty()) {
//            universalToast("No image found")
        }

        if (intent.hasExtra(Constants.IntentDataKeys.ImgPath)) {

            showImage(binding!!.ivImageView, videoUrl)
        } else {

            showVideo(videoUrl)
        }

        binding!!.ivImageClose.setOnClickListener { onBackPressed() }
    }

    private fun showVideo(videoUrl: String) {

        binding!!.videoView.visibility = View.VISIBLE
        binding!!.ivImageView.visibility = View.GONE

        val uri = Uri.parse(videoUrl)

        Log.e("ActivityVideo", "personal_Video_URLShow: " + uri)
        binding!!.videoView.setVideoURI(uri)

        val mediaController = MediaController(this)

        mediaController.setAnchorView(binding!!.videoView)

        mediaController.setMediaPlayer(binding!!.videoView)

        binding!!.videoView.setMediaController(mediaController)

        binding!!.videoView.start()

        /*val videoInfo = VideoInfo(videoUrl)
            .setTitle("test video") //config title
            //aspectRatio
            .setShowTopBar(true) //show mediacontroller top bar
            .setPortraitWhenFullScreen(true) //portrait when full screen


        GiraffePlayer.play(this, videoInfo)*/


    }

    private fun showImage(imageView: ImageView, imageUrl: String) {

        Log.d("ChatImgPath", videoUrl)
        Log.e("ActivityVideo", "personal_IMG_URLShow: " + imageUrl)

        binding!!.videoView.visibility = View.GONE
        binding!!.ivImageView.visibility = View.VISIBLE

        Glide.with(this@VideoPlayerActivity).load(imageUrl)
            .placeholder(com.hbb20.R.drawable.flag_transparent).dontAnimate().into(imageView)

//        DisplayImage.with(this@VideoPlayerActivity)
//            .load(imageUrl)
//            .placeholder(R.drawable.meetupsfellow_transpatent)
//            .shape(DisplayImage.Shape.DEFAULT)
//            .into(imageView)
//            .build()
    }

}