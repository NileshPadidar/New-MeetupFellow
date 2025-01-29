package com.connect.meetupsfellow.mvp.view.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.core.net.toUri
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.global.utils.TouchImageView
import com.connect.meetupsfellow.mvp.view.activities.VideoPlayerActivity
import com.connect.meetupsfellow.retrofit.response.ResponsePrivatePics

class ImageAdapterPvt(val context: Context,private val images: ArrayList<ResponsePrivatePics>, val className : String, val type : String) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView =
            LayoutInflater.from(container.context)
                .inflate(R.layout.layout_view_pager_image, container, false)
        val imageView = itemView.findViewById(R.id.imageView) as TouchImageView
        val imageViewThumb = itemView.findViewById(R.id.video_thumbnail) as ImageView
        val imagePath = images[position].path
        Log.e("ImagePagerActivity", "IMGAdapter$position: $imagePath")
        if(imagePath.endsWith(".jpg")
            || imagePath.endsWith(".jpeg")
            || imagePath.endsWith(".png")) {
            imageViewThumb.visibility = View.GONE
        } else {
            imageViewThumb.visibility = View.VISIBLE
        }
   if(className.equals(Constants.IntentDataKeys.PrivateAlbumImages)) {
       val imageUri = Uri.parse(imagePath)
       Glide.with(context)
           .load(imageUri)
           .placeholder(R.drawable.meetupsfellow_transpatent) // Optional placeholder
           .error(R.drawable.meetupsfellow_transpatent)            // Optional error image
           .into(imageView)  // Or directly into the TouchImageView
       container.addView(itemView)
       if (type.equals(Constants.IntentDataKeys.ImgUri)){
           imageViewThumb.visibility = View.GONE
       }else{
           imageViewThumb.visibility = View.VISIBLE
       }
       Log.e("ImagePagerActivity", "IMGAdapter_IFF")
   } else {
       Glide.with(container.context)
           .load(if (images.isNotEmpty()) imagePath else "")
           .placeholder(R.drawable.meetupsfellow_transpatent)
           .dontAnimate()
           .into(imageView)
       container.addView(itemView)
       Log.e("ImagePagerActivity", "IMGAdapter_ELSS")
   }


        imageView.setOnClickListener{
            Log.e("ImagePagerActivity", "IMGAdapter: CLICK")
            if(!(imagePath.endsWith(".jpg")
                        || imagePath.endsWith(".jpeg")
                        || imagePath.endsWith(".png")
                        || type.equals(Constants.IntentDataKeys.ImgUri))) {
               /* val intent = Intent(Intent.ACTION_VIEW)
                intent.setData(Uri.parse(imagePath))
                container.context.startActivity(intent)*/

                val context = it.context
                // Create an intent to switch to a new activity
                val intent = Intent(context, VideoPlayerActivity::class.java)
                intent.putExtra(Constants.IntentDataKeys.VideoUrl, imagePath)
                context.startActivity(intent)
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