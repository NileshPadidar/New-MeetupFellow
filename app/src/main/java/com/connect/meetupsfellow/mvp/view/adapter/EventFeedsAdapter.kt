package com.connect.meetupsfellow.mvp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.databinding.ItemFeedBinding
import com.connect.meetupsfellow.mvp.view.viewholder.EventFeedsViewHolder
import com.connect.meetupsfellow.retrofit.response.ResponseEventFeeds

class EventFeedsAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<EventFeedsViewHolder>() {

    private val feeds = arrayListOf<ResponseEventFeeds>()

    internal fun updateFeeds(feeds: ArrayList<ResponseEventFeeds>) {
        this.feeds.addAll(feeds)
        update()
    }

    private fun update() {
        val dummy = linkedSetOf<ResponseEventFeeds>()
        dummy.addAll(feeds)
        feeds.clear()
        feeds.addAll(dummy)
        dummy.clear()
        notifyDataSetChanged()
    }

    internal fun clearFeeds() {
        if (feeds.isNotEmpty()) {
            feeds.clear()
            notifyDataSetChanged()
        }
    }

    internal fun getEventId(position: Int): String {
        return when (indexExists(feeds, position)) {
            true -> "${feeds[position].id}"
            false -> ""
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventFeedsViewHolder {
       /* return EventFeedsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_feed, parent, false)
        )*/
        val binding = ItemFeedBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return EventFeedsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return feeds.size
    }

    override fun onBindViewHolder(viewHolder: EventFeedsViewHolder, position: Int) {
        viewHolder.bind(feeds[viewHolder.adapterPosition])
    }

    private fun indexExists(list: java.util.ArrayList<*>, index: Int): Boolean =
        index >= 0 && index < list.size
}