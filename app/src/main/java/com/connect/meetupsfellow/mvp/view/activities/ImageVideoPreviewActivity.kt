package com.connect.meetupsfellow.mvp.view.activities

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.databinding.ActivityImageVideoPreviewBinding

//import com.shockwave.pdfium.PdfiumCore


class ImageVideoPreviewActivity : AppCompatActivity() {
    private var videoUrl = ""
    private var imageUrl = ""
    private var fileUri = ""
    private var filePath = ""
    private var fileEXT = ""
    private var binding: ActivityImageVideoPreviewBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageVideoPreviewBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        // setContentView(R.layout.activity_image_video_preview)
        getIntentData()
    }

    private fun getIntentData() {
        when (intent.hasExtra(Constants.IntentDataKeys.VideoUrl)) {
            true -> {
                videoUrl = intent.getStringExtra(Constants.IntentDataKeys.VideoUrl).toString()
                Log.e("ActivityImgVideo", "personal_Video_URL: " + videoUrl)
            }

            false -> {}
        }
        when (intent.hasExtra(Constants.IntentDataKeys.ImgPath)) {
            true -> {
                imageUrl = intent.getStringExtra(Constants.IntentDataKeys.ImgPath).toString()
                Log.e("ActivityImgVideo", "personal_IMG_URL: " + imageUrl)
            }

            false -> {}
        }
        when (intent.hasExtra(Constants.IntentDataKeys.FileUri)) {
            true -> {
                fileUri = intent.getStringExtra(Constants.IntentDataKeys.FileUri).toString()
                filePath = intent.getStringExtra(Constants.IntentDataKeys.FilePath).toString()
                fileEXT = intent.getStringExtra(Constants.IntentDataKeys.FileExt).toString()
                Log.e("ActivityImgVideo", "personal_FILE_URi: " + fileUri)
            }

            false -> {}
        }
        init()
    }

    private fun init() {

        if (videoUrl.isNotEmpty()) {
            showVideo(videoUrl)
        } else if (fileUri.isNotEmpty() && filePath.isNotEmpty()) {
            showFile()
        } else {
            showImage(binding!!.ivImageViewPreview, imageUrl)
        }

        binding!!.ivImageClosePreview.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        binding!!.ivImageSend.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun showVideo(videoUrl: String) {

        binding!!.videoViewPreview.visibility = View.VISIBLE
        binding!!.ivImageViewPreview.visibility = View.GONE

        val uri = Uri.parse(videoUrl)

        Log.e("ActivityVideo", "personal_Video_URLShow: " + uri)
        binding!!.videoViewPreview.setVideoURI(uri)

        val mediaController = MediaController(this)

        mediaController.setAnchorView(binding!!.videoViewPreview)

        mediaController.setMediaPlayer(binding!!.videoViewPreview)

        binding!!.videoViewPreview.setMediaController(mediaController)

        binding!!.videoViewPreview.start()

    }

    private fun showImage(imageView: ImageView, imageUrl: String) {

        Log.d("ChatImgPath", videoUrl)
        Log.e("ActivityVideo", "personal_IMG_URLShow: " + imageUrl)

        binding!!.videoViewPreview.visibility = View.GONE
        binding!!.ivImageViewPreview.visibility = View.VISIBLE

        Glide.with(this@ImageVideoPreviewActivity).load(imageUrl)
           /// .placeholder(R.drawable.flag_transparent).dontAnimate().into(imageView)
            .placeholder(com.hbb20.R.drawable.flag_transparent).dontAnimate().into(imageView)

//        DisplayImage.with(this@VideoPlayerActivity)
//            .load(imageUrl)
//            .placeholder(R.drawable.meetupsfellow_transpatent)
//            .shape(DisplayImage.Shape.DEFAULT)
//            .into(imageView)
//            .build()
    }

    private fun showFile() {
        Log.e("ActivityVideo", "personal_File_URLShow: " + fileUri)

        binding!!.videoViewPreview.visibility = View.GONE
        binding!!.ivImageViewPreview.visibility = View.GONE
        binding!!.previewProgressbar.visibility = View.GONE


        val fileName = filePath.split("/")

        if (fileEXT.equals("pdf")) {
            binding!!.llFileView.visibility = View.GONE
            binding!!.previewFileImg.visibility = View.VISIBLE
            binding!!.previewFileNamePdf.visibility = View.VISIBLE
            binding!!.previewFileNamePdf.text = fileName[fileName.lastIndex]
            val fileBitmap =
                generateImageFromPdf(this@ImageVideoPreviewActivity, fileUri.toUri(), 0)
            binding!!.previewFileImg.setImageBitmap(fileBitmap)
        } else {
            binding!!.previewFileImg.visibility = View.GONE
            binding!!.previewFileNamePdf.visibility = View.GONE
            binding!!.llFileView.visibility = View.VISIBLE
            binding!!.previewFileName.visibility = View.VISIBLE
            binding!!.previewFileName.text = fileName[fileName.lastIndex]
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
    }

    /*  fun generateImageFromPdf(pdfUri: Uri?): Bitmap? {
          val pageNumber = 0
          val pdfiumCore = PdfiumCore(this@ImageVideoPreviewActivity)
          var bmp: Bitmap? = null
          try {
              //http://www.programcreek.com/java-api-examples/index.php?api=android.os.ParcelFileDescriptor
              val fd =
                  this@ImageVideoPreviewActivity.contentResolver.openFileDescriptor(pdfUri!!, "r")
              val pdfDocument: com.shockwave.pdfium.PdfDocument = pdfiumCore.newDocument(fd)
              pdfiumCore.openPage(pdfDocument, pageNumber)
              val width: Int = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber)
              val height: Int = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber)
              Log.e("Width_height", "$width $height")
              bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
              pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height)
              pdfiumCore.closeDocument(pdfDocument)
          } catch (e: java.lang.Exception) {
              //todo with exception
          }
          return bmp
      }*/
    fun generateImageFromPdf(context: Context, pdfUri: Uri, pageNumber: Int = 0): Bitmap? {
        val fileDescriptor = context.contentResolver.openFileDescriptor(pdfUri, "r") ?: return null
        val pdfRenderer = PdfRenderer(fileDescriptor)
        val page = pdfRenderer.openPage(pageNumber)
        val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()
        pdfRenderer.close()
        return bitmap
    }

}