package com.connect.meetupsfellow.mvp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.gson.Gson
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.databinding.ItemNearbyBinding
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.view.model.ProfileFirebase
import com.connect.meetupsfellow.mvp.view.viewholder.MostRecentlyJoinedViewHolder
import com.connect.meetupsfellow.retrofit.response.ResponseUserData
import javax.inject.Inject

class MostRecentlyJoinedUserAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<MostRecentlyJoinedViewHolder>() {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    private val nearby = arrayListOf<ResponseUserData>()

    init {
        ArchitectureApp.component!!.inject(this@MostRecentlyJoinedUserAdapter)
    }

    internal fun updateNearBy(nearby: ArrayList<ResponseUserData>) {
        this.nearby.addAll(nearby)
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag,
            "Profiles is : ${Gson().toJson(nearby)}"
        )
        addRemoveOwnProfile()
        update()
    }

    internal fun updateOnlineOfflineStatus(profile: ProfileFirebase) {
        when (nearby.isNotEmpty()) {
            true -> {
                val index =
                    nearby.firstOrNull { it.id == "${profile.userId}" }?.let { nearby.indexOf(it) }
                        ?: -1
                if (index > -1) {
                    when (indexExists(nearby, index)) {
                        true -> {
                            nearby[index].onlineStatus = when (profile.onlineStatus) {
                                true -> 1
                                false -> 0
                            }
//                            sort()
                            notifyDataSetChanged()
                        }

                        false -> {}
                    }
                }
            }

            false -> {}
        }
    }

    private fun addRemoveOwnProfile() {
        when (nearby.isNotEmpty()) {
            true -> {
                val index =
                    nearby.firstOrNull { it.id == sharedPreferencesUtil.userId() }?.let {
                        nearby.indexOf(
                            it
                        )
                    } ?: -1
                LogManager.logger.i(
                    ArchitectureApp.instance!!.tag,
                    "NearBy Users data : index $index"
                )
                if (index > -1) {
                    if (index > 0) {
                        LogManager.logger.i(
                            ArchitectureApp.instance!!.tag,
                            "NearBy Users data : index ${indexExists(nearby, index)}"
                        )
                        when (indexExists(nearby, index)) {
                            true -> {
                                val profile = nearby[index]
                                nearby.removeAt(index)
                                if (nearby.isNotEmpty()) {
                                    nearby.add(0, profile.apply {
                                        onlineStatus = 1
                                    })
                                } else {
                                    nearby.add(profile.apply {
                                        onlineStatus = 1
                                    })
                                }
                            }

                            false -> {}
                        }
                    }
                } else {
//                    addSelf()
                }
            }

            false -> {
//                addSelf()
            }
        }

    }

    private fun addSelf() {
        val profile = sharedPreferencesUtil.fetchUserProfile()
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag,
            "NearBy Users data : profile ${Gson().toJson(profile)}"
        )
        nearby.add(0, profile.apply {
            userPic = if (profile.images.isEmpty()) "" else profile.images[0].imagePath
            userThumb = if (profile.images.isEmpty()) "" else profile.images[0].imageThumb
            onlineStatus = 1
        })
    }

    private fun update() {
        val dummy = linkedSetOf<ResponseUserData>()
        dummy.addAll(nearby)
        nearby.clear()
        nearby.addAll(dummy)
        dummy.clear()
//        sort()
        notifyDataSetChanged()
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MostRecentlyJoinedViewHolder {
        return when (viewType) {
            Item ->MostRecentlyJoinedViewHolder(ItemNearbyBinding.inflate(LayoutInflater.from(parent.context),parent,false))
                /*MostRecentlyJoinedViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_nearby, parent, false)
            )*/

            else -> MostRecentlyJoinedViewHolder(ItemNearbyBinding.inflate(LayoutInflater.from(parent.context),parent,false))
                /*MostRecentlyJoinedViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.become_pro, parent, false)
            )*/
        }
    }

    override fun getItemCount(): Int {
        val user = sharedPreferencesUtil.fetchUserProfile()
        when (user.isProMembership) {
            true -> {
                return nearby.size
            }

            false -> {
                if(nearby.size > 80){
                    return 80;
                }
            }
        }
        return nearby.size
    }

    override fun getItemViewType(position: Int): Int {
        return nearby[position].item
    }

    override fun onBindViewHolder(viewHolder: MostRecentlyJoinedViewHolder, position: Int) {
        viewHolder.bind(nearby[viewHolder.adapterPosition])
    }

    private fun indexExists(list: java.util.ArrayList<*>, index: Int): Boolean =
        index >= 0 && index < list.size

    companion object {
        const val Item = 1
        const val Footer = 2
    }

    fun getNearByList(): ArrayList<ResponseUserData> {
        return nearby
    }
}
