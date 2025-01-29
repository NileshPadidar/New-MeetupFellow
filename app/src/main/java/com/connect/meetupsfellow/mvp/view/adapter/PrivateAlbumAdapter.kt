package com.connect.meetupsfellow.mvp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.connect.meetupsfellow.databinding.ItemPrivateAlbumBinding
import com.connect.meetupsfellow.databinding.ItemPrivatePictureAccessBinding
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.mvp.view.viewholder.PrivateAccessViewHolder
import com.connect.meetupsfellow.mvp.view.viewholder.PrivateAlbumViewHolder
import com.connect.meetupsfellow.retrofit.response.ResponsePrivateAccess
import com.connect.meetupsfellow.retrofit.response.ResponsePrivatePics
import javax.inject.Inject

class PrivateAlbumAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val images = arrayListOf<ResponsePrivatePics>()
    private var privateAccess = arrayListOf<ResponsePrivateAccess>()
    private var userId = ""
    private var type = ""

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil


    private var Type = Image


    init {
        images.add(ResponsePrivatePics())
    }

    internal fun privateAlbum(images: ArrayList<ResponsePrivatePics>) {
        if (this.images.isNotEmpty())
            this.images.clear()
        if (userId.isEmpty() && type.isEmpty()) {
            this.images.add(ResponsePrivatePics())
        }
        this.images.addAll(images)
        notifyDataSetChanged()
    }

    internal fun update(privateAccess: ArrayList<ResponsePrivateAccess>) {
        this.privateAccess.addAll(privateAccess)
        val dummy = linkedSetOf<ResponsePrivateAccess>()
        dummy.addAll(this.privateAccess)
        this.privateAccess.clear()
        this.privateAccess.addAll(dummy)
        dummy.clear()
        notifyDataSetChanged()
    }

    internal fun userId(userId: String) {
        this.userId = userId
    }

    internal fun type(type: String) {
        this.type = type
    }

    internal fun imageId(position: Int): Int {
        return images[position].id
    }

    internal fun update(album: ResponsePrivatePics) {
        images.add(1, album)
        notifyDataSetChanged()
    }

    internal fun updateImage(album: ResponsePrivatePics, position: Int) {
        images[position] = album
    }

    internal fun delete(position: Int): ResponsePrivatePics {
        return when (indexExists(images, position)) {
            true -> {
                val image = images[position]
                images.removeAt(position)
                notifyItemRemoved(position)
                image
            }

            false -> ResponsePrivatePics()
        }
    }

    fun setType(type: Int) {
        this.Type = type
    }

    internal fun getData(): ArrayList<*> {
        return when (Type) {
            Image -> images
            else -> privateAccess
        }
    }

    internal fun clearAll() {
        images.clear()
        privateAccess.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return when (Type) {
            Image -> {
                images.size
            }
            else -> privateAccess.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return this.Type
    }

    private fun indexExists(list: java.util.ArrayList<*>, index: Int): Boolean =
        index >= 0 && index < list.size

    internal fun image(position: Int): ResponsePrivatePics {
        return images[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            Image -> PrivateAlbumViewHolder(ItemPrivateAlbumBinding.inflate(LayoutInflater.from(parent.context),parent,false), userId, type)
                /* PrivateAlbumViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_private_album,
                    parent,
                    false
                ),
                userId, type
            )*/
            else -> PrivateAccessViewHolder(ItemPrivatePictureAccessBinding.inflate(LayoutInflater.from(parent.context),parent,false), viewType)
                /*PrivateAccessViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_private_picture_access,
                    parent,
                    false
                ), viewType
            )*/
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (Type == Image) {
            true -> {
                (holder as PrivateAlbumViewHolder).bind(images[holder.adapterPosition])
            }

            false -> {
                (holder as PrivateAccessViewHolder).bind(privateAccess[holder.adapterPosition])
            }
        }
    }

    companion object {
        val Image = 0
        val User = 1
        val Other = 2
    }
}
