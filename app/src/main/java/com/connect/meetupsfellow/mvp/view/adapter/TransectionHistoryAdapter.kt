package com.connect.meetupsfellow.mvp.view.adapter

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.retrofit.response.TransactionHistory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TransectionHistoryAdapter() :  RecyclerView.Adapter<TransectionHistoryAdapter.TransectionViewHolder>() {

    private val connectArray = ArrayList<TransactionHistory>()
    private lateinit var context: Context

    internal fun add(
        context: Context,
        users: ArrayList<TransactionHistory>
    ) {
        this.connectArray.clear()
        this.connectArray.addAll(users)
        this.context = context
        Log.e("MyConnections^*", "AdapterSize- ${connectArray.size}")
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransectionHistoryAdapter.TransectionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payment_history, parent, false)
        return TransectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransectionViewHolder, position: Int) {
        val connectItem = connectArray[position]
        holder.bind(connectItem, position)
    }

    override fun getItemCount(): Int {
        return connectArray.size
    }

    inner class TransectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTransectionId: TextView = itemView.findViewById(R.id.tvTransectionId)
        private val tvPlanName: TextView = itemView.findViewById(R.id.tvPlanName)
        private val planBadge: ImageView = itemView.findViewById(R.id.planBadge)
        private val tvPlanPrice: TextView = itemView.findViewById(R.id.tvPlanPrice)
        private val tv_plan_purchase_date: TextView = itemView.findViewById(R.id.tv_plan_purchase_date)


        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(connectItem: TransactionHistory, position: Int) {
            Log.e("adapterHistory","name: ${connectItem.planTitle}")
            tvPlanName.text = connectItem.planTitle
            tvTransectionId.text = connectItem.transactionId
            tvPlanPrice.text = connectItem.planPrice + "/Month"
           // tv_plan_purchase_date.text = connectItem.planPurchaseDate
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val outputFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy h:mm a")

            val parsedDateTime = LocalDateTime.parse(connectItem.planPurchaseDate, inputFormatter)
            val formattedDateTime = parsedDateTime.format(outputFormatter)
          ///  tv_plan_purchase_date.text = connectItem.planPurchaseDate!!.split(" ")[0]
            tv_plan_purchase_date.text = formattedDateTime

            when (connectItem.planId) {
                2 -> {
                    planBadge.setImageResource(R.drawable.special_badge)
                }

                3 -> {
                    planBadge.setImageResource(R.drawable.standers_badge)
                }

                4 -> {
                    planBadge.setImageResource(R.drawable.ic_baseline_workspace_premium_24)
                }

                5 -> {
                    planBadge.setImageResource(R.drawable.featured_badge)
                }

                else -> {
                    planBadge.setImageResource(R.drawable.free_user_badge)
                }
            }

           /* LlMain.setOnClickListener {
                val intent = Intent(
                    context,
                    UserProfileActivity::class.java
                )
                intent.putExtra(Constants.IntentDataKeys.UserId, connectItem.userId)
                context.startActivity(intent)
            }*/

        }

    }

}