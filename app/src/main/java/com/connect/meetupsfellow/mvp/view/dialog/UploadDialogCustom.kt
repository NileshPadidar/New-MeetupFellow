package com.connect.meetupsfellow.mvp.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.connect.meetupsfellow.databinding.DialogUploadLoadingBinding

class UploadDialogCustom(mContext: Context?, private val message: String?) : Dialog(mContext!!) {

    private var binding: DialogUploadLoadingBinding? = null

    fun setProgress(progress: Int) {
        binding!!.progressBar.progress = progress
        binding!!.tvProgress.text = "$progress%"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //  setContentView(R.layout.dialog_upload_loading)
        binding = DialogUploadLoadingBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
    }

    fun setCount(count: String) {
        binding!!.tvCount.text = count
    }

    private fun init() {
        setCancelable(false)
        binding!!.tvMessageLoading.text = message
        window!!.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    context, android.R.color.transparent
                )
            )
        )
        val lp = window!!.attributes
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window!!.attributes = lp
    }
}
