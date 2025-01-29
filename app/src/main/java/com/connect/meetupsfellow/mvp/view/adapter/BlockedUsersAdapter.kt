package com.connect.meetupsfellow.mvp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.databinding.ItemBlockBinding
import com.connect.meetupsfellow.mvp.view.viewholder.BlockedUsersViewHolder
import com.connect.meetupsfellow.retrofit.response.ResponseBlockedUsers

class BlockedUsersAdapter :
    androidx.recyclerview.widget.RecyclerView.Adapter<BlockedUsersViewHolder>() {

    private val users = arrayListOf<ResponseBlockedUsers>()

    internal fun add(users: ArrayList<ResponseBlockedUsers>) {
        this.users.addAll(users)
        removeDuplicateIfAny()
    }

    internal fun remove(position: Int): String {
        return when (indexExists(users, position)) {
            true -> {
                val id = users[position].blocUserId
                users.removeAt(position)
                notifyItemRemoved(position)
                "$id"
            }

            else -> ""
        }
    }

    private fun removeDuplicateIfAny() {
        val dummy = linkedSetOf<ResponseBlockedUsers>()
        dummy.addAll(users)
        users.clear()
        users.addAll(dummy)
        dummy.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockedUsersViewHolder {
       /* return BlockedUsersViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_block,
                parent,
                false
            )
        )*/

        // Use ViewBinding to inflate the layout
        val binding = ItemBlockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BlockedUsersViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(viewHolder: BlockedUsersViewHolder, position: Int) {
        viewHolder.bind(users[viewHolder.adapterPosition])
    }

    private fun indexExists(list: java.util.ArrayList<*>, index: Int): Boolean =
        index >= 0 && index < list.size
}