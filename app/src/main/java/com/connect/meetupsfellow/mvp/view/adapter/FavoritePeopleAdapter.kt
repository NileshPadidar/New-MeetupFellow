package com.connect.meetupsfellow.mvp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.databinding.ItemInterestedPeopleBinding
import com.connect.meetupsfellow.global.interfaces.RecyclerViewClickListener
import com.connect.meetupsfellow.mvp.view.viewholder.FavoritePeopleViewHolder
import com.connect.meetupsfellow.retrofit.response.ResponseFavoriteUser

class FavoritePeopleAdapter(private val clickListener: RecyclerViewClickListener) :
    androidx.recyclerview.widget.RecyclerView.Adapter<FavoritePeopleViewHolder>() {

    private val favoritePeople = arrayListOf<ResponseFavoriteUser>()

    internal fun update(interestedPeople: ArrayList<ResponseFavoriteUser>) {
        this.favoritePeople.addAll(interestedPeople)

        update()
    }

    private fun update() {
        val dummy = linkedSetOf<ResponseFavoriteUser>()
        dummy.addAll(favoritePeople)
        favoritePeople.clear()
        favoritePeople.addAll(dummy)
        dummy.clear()
        notifyDataSetChanged()
    }

    internal fun clear() {
        if (favoritePeople.isNotEmpty()) {
            favoritePeople.clear()
            notifyDataSetChanged()
        }
    }

    internal fun getProfileId(position: Int): String {
        return when (indexExists(favoritePeople, position)) {
            true -> "${favoritePeople[position].userId}"
            false -> ""
        }
    }

    internal fun removeIndex(position: Int) {
        favoritePeople.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritePeopleViewHolder {
       /* return FavoritePeopleViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_interested_people,
                parent,
                false
            ),
            clickListener
        )*/
        val binding = ItemInterestedPeopleBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return FavoritePeopleViewHolder(binding,clickListener)
    }

    override fun getItemCount(): Int {
        return favoritePeople.size
    }

    override fun onBindViewHolder(viewHolder: FavoritePeopleViewHolder, position: Int) {
        viewHolder.bind(favoritePeople[viewHolder.adapterPosition])
    }

    private fun indexExists(list: java.util.ArrayList<*>, index: Int): Boolean =
        index >= 0 && index < list.size
}