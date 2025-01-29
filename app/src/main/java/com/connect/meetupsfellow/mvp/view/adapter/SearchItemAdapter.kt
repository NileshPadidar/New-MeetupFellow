package com.connect.meetupsfellow.mvp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.databinding.ItemSearchBinding
import com.connect.meetupsfellow.mvp.view.viewholder.SearchItemViewHolder
import com.connect.meetupsfellow.retrofit.response.ResponseUserData

class SearchItemAdapter :
    RecyclerView.Adapter<SearchItemViewHolder>() {

    private val searchItem = arrayListOf<ResponseUserData>()

    internal fun update(searchItem: ArrayList<ResponseUserData>) {
        this.searchItem.addAll(searchItem)

        update()
    }

    private fun update() {
        val dummy = linkedSetOf<ResponseUserData>()
        dummy.addAll(searchItem)
        searchItem.clear()
        searchItem.addAll(dummy)
        dummy.clear()
        notifyDataSetChanged()

    }

    internal fun removeSelf(id: String) {
        val index = searchItem.firstOrNull {
            it.userId == id
        }?.let { searchItem.indexOf(it) } ?: -1

        if (index > -1) {
            searchItem.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    internal fun clearAll() {
        if (searchItem.isNotEmpty()) {
            searchItem.clear()
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
       /* return SearchItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)
        )*/
        val binding = ItemSearchBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SearchItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchItem.size
    }

    override fun onBindViewHolder(viewHolder: SearchItemViewHolder, position: Int) {
        viewHolder.bindUserSearch(searchItem[viewHolder.adapterPosition])
    }

}