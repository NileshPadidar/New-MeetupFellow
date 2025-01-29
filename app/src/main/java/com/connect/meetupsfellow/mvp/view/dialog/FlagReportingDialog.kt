package com.connect.meetupsfellow.mvp.view.dialog

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.databinding.ActivityFlagBinding
import com.connect.meetupsfellow.databinding.ItemReasonBinding
import com.connect.meetupsfellow.global.interfaces.RecyclerViewClickListener
import com.connect.meetupsfellow.mvp.view.adapter.ReasonAdapter
import com.connect.meetupsfellow.retrofit.response.ResponseFlagReason

class FlagReportingDialog(context: Context, theme: Int) : AlertDialog(context, theme) {


    /*private val clickListener = object : RecyclerViewClickListener {
        override fun onClick(view: View, position: Int) {
            when (indexExists(reasons, position)) {
                true -> {

                    if (itemBinding.flagCheck.isChecked) {

                        itemBinding.flagCheck.isChecked = false
                        itemBinding.flagLay.setBackgroundResource(R.drawable.rounded_corners_filter_default)
                    } else {

                        Log.d("reason", itemBinding.tvFlagReason.text.toString())

                        val paint = itemBinding.tvFlagReason.paint

                        val width = paint.measureText(itemBinding.tvFlagReason.text.toString())
                        val textShader: Shader = LinearGradient(
                            0f, 0f, width, itemBinding.tvFlagReason.textSize, intArrayOf(
                                Color.parseColor("#F4447E"), Color.parseColor("#8448F4")*//*Color.parseColor("#64B678"),
                        Color.parseColor("#478AEA"),*//*
                                //Color.parseColor("#8446CC")
                            ), null, Shader.TileMode.REPEAT
                        )

                        itemBinding.tvFlagReason.paint.setShader(textShader)

                        itemBinding.flagCheck.isChecked = true
                        itemBinding.flagLay.setBackgroundResource(R.drawable.rounded_corners_filter_selected)
                    }

                    reasons.asSequence().forEach {
                        it.selected = false
                    }
                    reasons[position].selected = !reasons[position].selected
                    //reasonAdapter!!.notifyDataSetChanged()
                }

                else -> {}
            }
        }
    }*/

    private val clickListener = object : RecyclerViewClickListener {
        override fun onClick(view: View, position: Int) {
            // Toggle the selected multiple state for the clicked position
        /*    reasons[position].selected = !reasons[position].selected
            // Notify adapter to refresh
            reasonAdapter!!.notifyItemChanged(position)*/
            reasons.asSequence().forEach {
                it.selected = false
            }
            reasons[position].selected = !reasons[position].selected
            reasonAdapter!!.notifyDataSetChanged()
        }
    }


    private lateinit var listener: OnCompleteListener

    private val reasons = arrayListOf<ResponseFlagReason>()
    private var reasonAdapter: ReasonAdapter? = null
    private var heading = ""
    private var binding: ActivityFlagBinding? = null

    private val itemBinding: ItemReasonBinding =
        ItemReasonBinding.inflate(LayoutInflater.from(context))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  setContentView(R.layout.activity_flag)
        binding = ActivityFlagBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.clearFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        )
        init()
    }

    private fun init() {

        binding!!.tvHeading.text = heading
        val paint = binding!!.tvHeading.paint

        val width = paint.measureText(binding!!.tvHeading.text.toString())
        val textShader: Shader = LinearGradient(
            0f, 0f, width, binding!!.tvHeading.textSize, intArrayOf(
                Color.parseColor("#F4447E"), Color.parseColor("#8448F4")/*Color.parseColor("#64B678"),
            Color.parseColor("#478AEA"),*/
                //Color.parseColor("#8446CC")
            ), null, Shader.TileMode.REPEAT
        )

        binding!!.tvHeading.paint.setShader(textShader)

        reasonAdapter = ReasonAdapter(clickListener)

        if (binding!!.rlReason.layoutParams != null) binding!!.rlReason.layoutParams.height =
            ArchitectureApp.instance!!.resources.displayMetrics.heightPixels / 4

        with(binding!!.rvReasons) {
            layoutManager = GridLayoutManager(
                binding!!.rvReasons.context, 2, RecyclerView.VERTICAL, false
            )
            adapter = reasonAdapter
        }

        binding!!.ivCrossFlag.setOnClickListener {
            listener.onComplete("", this@FlagReportingDialog)
            dismiss()
        }

        /*view1.setOnClickListener {
            listener.onComplete("", this@FlagReportingDialog)
        }*/

        binding!!.btnSend.setOnClickListener {
            getReason()
            Log.e("Reason_List","size: ${reasons.size}")
        }

        reasonAdapter!!.update(reasons)
    }

    private fun getReason() {
        val index = reasons.firstOrNull { it.selected }?.let { reasons.indexOf(it) } ?: -1
        if (index > -1) {
            listener.onComplete(reasons[index].reason_flag, this@FlagReportingDialog)
            Log.e("Reason_List","Flag_reson: ${reasons[index].reason_flag}")
        } else {
          //  universalToast("Please select at least 1 reason")
            Toast.makeText(context,"Please select reason",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        listener.onComplete("", this@FlagReportingDialog)
        dismiss()
    }

    class Builder(private var context: Context, private var theme: Int) {
        private lateinit var listener: OnCompleteListener
        private val reasons = arrayListOf<ResponseFlagReason>()
        private var heading = context.getString(R.string.label_give_feedback_text)

        fun reasons(reasons: ArrayList<ResponseFlagReason>): Builder {
            this.reasons.addAll(reasons)
            return this
        }

        fun setOnCompletionListener(listener: OnCompleteListener): Builder {
            this.listener = listener
            return this
        }

        fun heading(heading: String): Builder {
            this.heading = heading
            return this
        }

        fun build(): FlagReportingDialog {
            val dialog = FlagReportingDialog(context, theme)
//            dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            dialog.listener = listener
            dialog.heading = heading
            dialog.reasons.addAll(reasons)
            dialog.show()
            return dialog
        }
    }

    private fun indexExists(list: java.util.ArrayList<*>, index: Int): Boolean =
        index >= 0 && index < list.size

    private fun universalToast(message: String) {
        //Toast.makeText(this@FlagReportingDialog.context, message, Toast.LENGTH_SHORT).show()
    }

    interface OnCompleteListener {
        fun onComplete(param: String, dialog: DialogInterface)
    }
}