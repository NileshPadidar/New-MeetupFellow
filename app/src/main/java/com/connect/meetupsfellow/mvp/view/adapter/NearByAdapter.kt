package com.connect.meetupsfellow.mvp.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.google.gson.Gson
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.databinding.ItemNearbyBinding
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.view.model.ProfileFirebase
import com.connect.meetupsfellow.mvp.view.viewholder.NearByViewHolder
import com.connect.meetupsfellow.retrofit.response.ResponseAds
import com.connect.meetupsfellow.retrofit.response.ResponseUserData
import javax.inject.Inject

class NearByAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<NearByViewHolder>(), Filterable {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil
    private var nearbyAll : List<ResponseUserData>? = null
    private val nearby = arrayListOf<ResponseUserData>()

    var AD_TYPE:Int =0

    init {
        ArchitectureApp.component!!.inject(this@NearByAdapter)
        nearbyAll = ArrayList<ResponseUserData>(nearby)
    }

    internal fun updateNearBy(nearby: ArrayList<ResponseUserData>) {
        this.nearby.clear()
        this.nearbyAll = null
        this.nearby.addAll(nearby)
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag,
            "NearByAdapter_Data_A : ${Gson().toJson(nearby)}"
        )
        ///addRemoveOwnProfile()
       /// removeSelf()
        update()
    }

    fun addData(nearbyInsert: ArrayList<ResponseUserData>) {
        val startPos = itemCount
        this.nearby.addAll(nearbyInsert)
        notifyItemInserted(startPos)
       // notifyItemRangeInserted(startPos, nearbyInsert.size)
        Log.e("nearUsers12Adap- ", "startPos: $startPos && ${nearbyInsert.size}")
    }

    private fun update() {
        val dummy = linkedSetOf<ResponseUserData>()
        dummy.addAll(nearby)
        Log.e("nearUsers12Adapter- ", nearby.size.toString())
        nearby.clear()
        nearby.addAll(dummy)
        dummy.clear()
       // sort()
        nearbyAll = ArrayList<ResponseUserData>(nearby)
      //  notifyDataSetChanged()
        notifyItemInserted(0)

    }

    private fun sort() {
        nearby.sortWith(Comparator { abc1, abc2 ->
            val b1 = abc1.onlineStatus != 0
            val b2 = abc2.onlineStatus != 0
            if (b1 != b2) if (b1) -1 else 1 else 0
        })
    }

    internal fun clearNearBy() {
        if (nearby.isNotEmpty()) {
            nearby.clear()
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) :NearByViewHolder {
       return  when (viewType) {
            Item -> NearByViewHolder(ItemNearbyBinding.inflate(LayoutInflater.from(parent.context),parent,false), "Grid")
                /*NearByViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_nearby, parent, false)
            , "Grid")*/

            AD_TYPE -> NearByViewHolder(ItemNearbyBinding.inflate(LayoutInflater.from(parent.context),parent,false), "Grid")
                /*NearByViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.admin_ads, parent, false)
           ,"Ads" )*/

            else -> NearByViewHolder(ItemNearbyBinding.inflate(LayoutInflater.from(parent.context),parent,false), "Grid")
                /*NearByViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.become_pro, parent, false)
           ,"Grid" )*/
        }

    }

    override fun getItemCount(): Int {
        val user = sharedPreferencesUtil.fetchUserProfile()
        when (user.isProMembership) {
            true -> {
                return nearby.size
            }
            false -> {

                if(sharedPreferencesUtil.fetchAdminAdsCount() == "0") {

                    if(nearby.size > 80){
                        return 80;
                    }

                }else {

                    if (sharedPreferencesUtil.fetchAdminAdsCount() == "1") {
                        if (nearby.size > 81) {
                            return 81;
                        }
                    } else if (sharedPreferencesUtil.fetchAdminAdsCount() == "2"){
                        if (nearby.size > 82) {
                            return 82;
                        }
                    }   else if (sharedPreferencesUtil.fetchAdminAdsCount() == "3"){
                        if (nearby.size > 83) {
                            return 83;
                        }

                    } else{
                        if (nearby.size > 84) {
                            return 84;
                        }

                    }

                 }

            }
        }
        return nearby.size
    }

    override fun getItemViewType(position: Int): Int {

        if(sharedPreferencesUtil.fetchAdminAdsCount() == "0"){
            return nearby[position].item

        }else{
            return nearby[position].item
        }

    }

    override fun onBindViewHolder(viewHolder: NearByViewHolder, position: Int) {
        viewHolder.bind(nearby[viewHolder.adapterPosition])
    }

    companion object {
        const val Item = 1
        const val Footer = 2
    }

    override fun getFilter(): Filter {
        return filter1
    }

    var filter1: Filter = object : Filter() {
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            val filteredList: java.util.ArrayList<ResponseUserData> =
                java.util.ArrayList<ResponseUserData>()
            if (charSequence.toString().isEmpty()) {
                filteredList.addAll(nearbyAll!!)
            } else {
                for (filterData in nearbyAll!!) {
                    if (filterData.name.toLowerCase()
                            .contains(charSequence.toString().toLowerCase())
                    ) {
                        Log.d("userName", filterData.name)
                        filteredList.add(filterData)
                    }
                }
            }
            val filterResults = FilterResults()
            filterResults.values = filteredList
            return filterResults
        }

        override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
            nearby.clear()
            nearby.addAll(filterResults.values as Collection<ResponseUserData>)
            notifyDataSetChanged()
        }
    }
}
