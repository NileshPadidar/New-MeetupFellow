package com.connect.meetupsfellow.mvp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.application.ArchitectureApp
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.databinding.ItemConversationBinding
import com.connect.meetupsfellow.developement.interfaces.SharedPreferencesUtil
import com.connect.meetupsfellow.log.LogManager
import com.connect.meetupsfellow.mvp.view.model.ConversationModel
import com.connect.meetupsfellow.mvp.view.model.ProfileFirebase
import com.connect.meetupsfellow.mvp.view.viewholder.ConversationViewHolder
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import com.google.gson.Gson
import java.util.Locale
import javax.inject.Inject
//import kotlinx.android.synthetic.main.item_conversation.view.*

class ConversationAdapter : RecyclerSwipeAdapter<ConversationViewHolder>(), Filterable {

    private val swipeListener = object : SwipeLayout.SwipeListener {
        override fun onOpen(layout: SwipeLayout?) {
        }

        override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {
        }

        override fun onStartOpen(layout: SwipeLayout?) {
            mItemManger.closeAllExcept(layout)
        }

        override fun onStartClose(layout: SwipeLayout?) {

        }

        override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {

        }

        override fun onClose(layout: SwipeLayout?) {
        }

    }

    private lateinit var binding: ItemConversationBinding

    var conversationAll: List<ConversationModel>? = null
    private val conversation = arrayListOf<ConversationModel>()
    private var adminSupport = ConversationModel()

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    init {
        ArchitectureApp.component!!.inject(this@ConversationAdapter)
        conversationAll = ArrayList<ConversationModel>(conversation)
        refresh()
    }

    private var conversationModel: ConversationModel? = null

    internal fun refresh() {
        val data = sharedPreferencesUtil.fetchConversation()
        LogManager.logger.i(
            ArchitectureApp.instance!!.tag, "Profile Status is userId :: ${Gson().toJson(data)}"
        )
        if (conversation.isNotEmpty()) conversation.clear()
        data.asSequence().forEach {
            when (it.meDeletedConvo) {
                false -> {
                    when (it.convoId == "0") {
                        false -> {
                            when (it.otherUserId != Constants.Admin.ConvoAdminOneToOne) {
                                true -> conversation.add(it)
                                false -> adminSupport = it
                            }
                        }

                        true -> {}
                    }
                }

                true -> {}
            }
        }

        update()
    }

    internal fun convoId(position: Int): String {
        return conversation[position].convoId
    }

    internal fun remove(position: Int) {
        when (indexExists(conversation, position)) {
            true -> {
                conversation.removeAt(position)
                notifyItemRemoved(position)
            }

            false -> {}
        }
    }

    internal fun add(conversationModel: ConversationModel) {
        conversation.add(conversationModel)
        update()
    }

    private fun addAdminConversation() {
        conversationModel?.apply {
            if (conversation.isEmpty()) {
                conversation.add(conversationModel!!)
            } else {
                if (conversation[0].convoId == Constants.Admin.Convo) {

                    if (adminSupport.otherUserId == Constants.Admin.ConvoAdminOneToOne && adminSupport.otherUserImageUrl == "") {
                        conversation.add(0, adminSupport)
                        conversation.add(1, conversationModel!!)
                    } else {
                        conversation.add(0, addDummyAAdmin())
                        conversation.add(1, conversationModel!!)
                    }
                    //   conversation[1]= addDummyAAdmin()
                } else {
                    if (adminSupport.otherUserId == Constants.Admin.ConvoAdminOneToOne) {
                        conversation.add(0, adminSupport)
                        conversation.add(1, conversationModel!!)
                    } else {
                        conversation.add(0, addDummyAAdmin())
                        conversation.add(1, conversationModel!!)
                    }
                }
            }
            updateList(conversation)
            notifyDataSetChanged()
        }
    }

    internal fun addAdminConversation(conversationModel: ConversationModel) {
        this.conversationModel = conversationModel
        update()
    }

    internal fun updateOnlineOfflineStatus(profile: ProfileFirebase) {
        when (conversation.isNotEmpty()) {
            true -> {
                val index =
                    conversation.firstOrNull { it.otherUserId == "${profile.userId}" }?.let {
                        conversation.indexOf(
                            it
                        )
                    } ?: -1
                if (index > -1) {
                    when (indexExists(conversation, index)) {
                        true -> {
                            conversation[index].otherUserOnline = profile.onlineStatus
                            sort()
                            notifyDataSetChanged()
                        }

                        false -> {}
                    }
                }
            }

            false -> {}
        }
    }

    internal fun update(conversationModel: ConversationModel) {
        val index = conversation.firstOrNull { it.convoId == conversationModel.convoId }?.let {
            conversation.indexOf(
                it
            )
        } ?: -1
        if (index > -1) {
            when (indexExists(conversation, index)) {
                true -> {
                    when (conversationModel.meDeletedConvo) {
                        true -> {
                            conversation.removeAt(index)
                        }

                        false -> {
                            conversation[index] = conversationModel
                        }
                    }
                    sort()
                    //addAdminConversation()
                    notifyDataSetChanged()
                }

                false -> {}
            }
        }
    }

    private fun update() {
        updateList(conversation)
        conversationAll = ArrayList<ConversationModel>(conversation)
        // Log.e("cc++", conversation.toString())
        sort()
        //addAdminConversation()
        notifyDataSetChanged()
    }

    private fun removeDeletedConversations() {
        val indexes = arrayListOf<Int>()
        this.conversation.asSequence().forEach {
            when (it.meDeletedConvo) {
                true -> {
                    indexes.add(this.conversation.indexOf(it))
                }

                false -> {}
            }
        }
        when (indexes.isNotEmpty()) {
            true -> {
                indexes.asSequence().forEach {
                    when (indexExists(conversation, it)) {
                        true -> {
                            conversation.removeAt(it)
                        }

                        false -> {}
                    }
                }
            }

            false -> {}
        }
    }

    internal fun remove(convoID: String) {
        val index =
            conversation.firstOrNull { it.convoId == convoID }?.let { conversation.indexOf(it) }
                ?: -1
        if (index > -1) {
            when (indexExists(conversation, index)) {
                true -> {
                    conversation.removeAt(index)
                    sort()
                    //addAdminConversation()
                    notifyItemRemoved(index)
                }

                false -> {}
            }
        }
    }

    internal fun clearAll() {
        when (conversation.isNotEmpty()) {
            true -> {
                conversation.clear()
                notifyDataSetChanged()
            }

            false -> {}
        }
    }

    private fun sort() {
        conversation.sortWith(Comparator { lhs, rhs ->
            rhs.lastMessagetime.compareTo(lhs.lastMessagetime)
        })

        sharedPreferencesUtil.saveConversation(Gson().toJson(conversation))
    }

    private fun updateList(conversation: ArrayList<ConversationModel>) {
        val dummy = linkedSetOf<ConversationModel>()
        dummy.addAll(conversation)
        this.conversation.clear()
        this.conversation.addAll(dummy)
        dummy.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {/* return ConversationViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_conversation, parent, false)
        )*/
        binding =
            ItemConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConversationViewHolder(binding)
    }

    override fun getItemCount(): Int {
//        Log.e("abc+", conversation.toString())
        return conversation.size
    }

    override fun onBindViewHolder(viewHolder: ConversationViewHolder, position: Int) {
        viewHolder.bind(conversation[viewHolder.adapterPosition])
        ///  viewHolder.itemView.swipe.addSwipeListener(swipeListener)
        mItemManger.bindView(viewHolder.itemView, viewHolder.adapterPosition)

    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipe
    }

    private fun indexExists(list: ArrayList<*>, index: Int): Boolean =
        index >= 0 && index < list.size

    private fun universalToast(message: String) {
        Toast.makeText(ArchitectureApp.instance, message, Toast.LENGTH_SHORT).show()
    }

    private fun addDummyAAdmin(): ConversationModel {

        val adminSupportss = ConversationModel(
            "",
            sharedPreferencesUtil.userId(),
            "",
            Constants.Admin.ConvoAdminOneToOne + "_" + sharedPreferencesUtil.userId(),
            false,
            false,
            true,
            false,
            "",
            0,
            Constants.Admin.ConvoAdminOneToOne,
            "Admin Support Chat",
            "0",
            "Reegur_00120_test"
        )

//String.format( getString(R.string.text_admin_support))
        //  ConversationModel  con = new ConversationModel()

        return adminSupportss

    }

    override fun getFilter(): Filter {
        return filter1
    }

    var filter1: Filter = object : Filter() {
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            val filteredList: ArrayList<ConversationModel> =
                ArrayList<ConversationModel>()
            if (charSequence.toString().isEmpty()) {
                filteredList.addAll(conversationAll!!)
            } else {
                for (filterData in conversationAll!!) {
                    if (filterData.otherUserName.lowercase(Locale.getDefault())
                            .contains(charSequence.toString().lowercase(Locale.getDefault()))
                    ) {
                        filteredList.add(filterData)
                    }
                }
            }
            val filterResults = FilterResults()
            filterResults.values = filteredList
            return filterResults
        }

        override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
            conversation.clear()
            conversation.addAll(filterResults.values as Collection<ConversationModel>)
            notifyDataSetChanged()
        }
    }

}


