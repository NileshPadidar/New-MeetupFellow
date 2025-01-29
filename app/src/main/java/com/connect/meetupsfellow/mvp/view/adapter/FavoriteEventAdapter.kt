package com.connect.meetupsfellow.mvp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.databinding.ItemFeedBinding
import com.connect.meetupsfellow.global.interfaces.RecyclerViewClickListener
import com.connect.meetupsfellow.mvp.view.viewholder.FavoriteEventViewHolder
import com.connect.meetupsfellow.retrofit.response.ResponseEventFeeds

class FavoriteEventAdapter(private val clickListener: RecyclerViewClickListener) :
    androidx.recyclerview.widget.RecyclerView.Adapter<FavoriteEventViewHolder>() {

    private val favoriteEvents = arrayListOf<ResponseEventFeeds>()

    internal fun update(feeds: ArrayList<ResponseEventFeeds>) {
        this.favoriteEvents.addAll(feeds)
        update()
    }

    private fun update() {
        val dummy = linkedSetOf<ResponseEventFeeds>()
        dummy.addAll(favoriteEvents)
        favoriteEvents.clear()
        favoriteEvents.addAll(dummy)
        dummy.clear()
        notifyDataSetChanged()
    }

    internal fun clear() {
        if (favoriteEvents.isNotEmpty()) {
            favoriteEvents.clear()
            notifyDataSetChanged()
        }
    }

    internal fun getEventId(position: Int): String {
        return when (indexExists(favoriteEvents, position)) {
            true -> "${favoriteEvents[position].id}"
            false -> ""
        }
    }

    internal fun removeIndex(position: Int) {
        favoriteEvents.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteEventViewHolder {
       /* return FavoriteEventViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_feed, parent, false),
            clickListener
        )*/
        val binding = ItemFeedBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return  FavoriteEventViewHolder(binding,clickListener)
    }

    override fun getItemCount(): Int {
        return favoriteEvents.size
    }

    override fun onBindViewHolder(viewHolder: FavoriteEventViewHolder, position: Int) {
        viewHolder.bind(favoriteEvents[viewHolder.adapterPosition])
    }

    private fun indexExists(list: java.util.ArrayList<*>, index: Int): Boolean =
        index >= 0 && index < list.size
}