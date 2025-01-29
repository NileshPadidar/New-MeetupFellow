package com.connect.meetupsfellow.mvp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.databinding.ItemInterestedPeopleBinding
import com.connect.meetupsfellow.mvp.view.viewholder.FavoritePeopleViewHolder
import com.connect.meetupsfellow.mvp.view.viewholder.InterestedPeopleViewHolder
import com.connect.meetupsfellow.retrofit.response.ResponseInterestedPeople

class InterestedPeopleAdapter :
    androidx.recyclerview.widget.RecyclerView.Adapter<InterestedPeopleViewHolder>() {

    private val interestedPeople = arrayListOf<ResponseInterestedPeople>()

    internal fun update(interestedPeople: ArrayList<ResponseInterestedPeople>) {
        this.interestedPeople.addAll(interestedPeople)
        update()
    }

    private fun update() {
        val dummy = linkedSetOf<ResponseInterestedPeople>()
        dummy.addAll(interestedPeople)
        interestedPeople.clear()
        interestedPeople.addAll(dummy)
        dummy.clear()
        notifyDataSetChanged()
    }

    internal fun clear() {
        if (interestedPeople.isNotEmpty()) {
            interestedPeople.clear()
            notifyDataSetChanged()
        }
    }

    internal fun isProfileSelf() {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterestedPeopleViewHolder {
       /* return InterestedPeopleViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_interested_people,
                parent,
                false
            )
        )*/
        val binding = ItemInterestedPeopleBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return InterestedPeopleViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return interestedPeople.size
    }

    override fun onBindViewHolder(viewHolder: InterestedPeopleViewHolder, position: Int) {
        viewHolder.bind(interestedPeople[viewHolder.adapterPosition])
    }
}