package com.connect.meetupsfellow.mvp.view.adapter

import android.content.Intent
import android.net.Uri
import androidx.viewpager.widget.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.global.utils.TouchImageView
import com.connect.meetupsfellow.retrofit.response.ResponseUserProfileImages

class ImageAdapter(private val images: ArrayList<ResponseUserProfileImages>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView =
            LayoutInflater.from(container.context)
                .inflate(R.layout.layout_view_pager_image, container, false)
        val imageView = itemView.findViewById(R.id.imageView) as TouchImageView
        val imageViewThumb = itemView.findViewById(R.id.video_thumbnail) as ImageView
        val imagePath = images[position].imagePath
        if(imagePath.endsWith(".jpg")
            || imagePath.endsWith(".jpeg")
            || imagePath.endsWith(".png")) {
            imageViewThumb.visibility = View.GONE
        } else {
            imageViewThumb.visibility = View.VISIBLE
        }
        Glide.with(container.context)
            .load(if (images.isNotEmpty()) imagePath else "")
            .placeholder(R.drawable.meetupsfellow_transpatent)
            .dontAnimate()
            .into(imageView)
        container.addView(itemView)

        imageView.setOnClickListener{
            if(!(imagePath.endsWith(".jpg")
                || imagePath.endsWith(".jpeg")
                || imagePath.endsWith(".png"))) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setData(Uri.parse(imagePath))
                container.context.startActivity(intent)
            }
        }
        return itemView
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RelativeLayout
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }
}
