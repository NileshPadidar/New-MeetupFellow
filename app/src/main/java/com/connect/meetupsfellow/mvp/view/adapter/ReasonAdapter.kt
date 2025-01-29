package com.connect.meetupsfellow.mvp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.databinding.ItemReasonBinding
import com.connect.meetupsfellow.global.interfaces.RecyclerViewClickListener
import com.connect.meetupsfellow.mvp.view.viewholder.ReasonsViewHolder
import com.connect.meetupsfellow.retrofit.response.ResponseFlagReason

/*
class ReasonAdapter(private val clickListener: RecyclerViewClickListener) :
    androidx.recyclerview.widget.RecyclerView.Adapter<ReasonsViewHolder>() {

    private val reasons = arrayListOf<ResponseFlagReason>()

    internal fun update(reasons: ArrayList<ResponseFlagReason>) {
        this.reasons.addAll(reasons)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, ViewType: Int): ReasonsViewHolder {
       */
/* return ReasonsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_reason, parent, false),
            clickListener
        )*//*

        val binding = ItemReasonBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ReasonsViewHolder(binding,clickListener)
    }

    override fun getItemCount(): Int {
        return reasons.size
    }

    override fun onBindViewHolder(viewHolder: ReasonsViewHolder, position: Int) {
        viewHolder.bind(reasons[viewHolder.adapterPosition])
    }
}
*/

class ReasonAdapter(private val clickListener: RecyclerViewClickListener) :
    RecyclerView.Adapter<ReasonsViewHolder>() {

    private val reasons = arrayListOf<ResponseFlagReason>()

    internal fun update(newReasons: ArrayList<ResponseFlagReason>) {
        reasons.clear()
        reasons.addAll(newReasons)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, ViewType: Int): ReasonsViewHolder {
        val binding = ItemReasonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReasonsViewHolder(binding, clickListener)
    }

    override fun getItemCount(): Int = reasons.size

    override fun onBindViewHolder(viewHolder: ReasonsViewHolder, position: Int) {
        viewHolder.bind(reasons[position])
    }
}
