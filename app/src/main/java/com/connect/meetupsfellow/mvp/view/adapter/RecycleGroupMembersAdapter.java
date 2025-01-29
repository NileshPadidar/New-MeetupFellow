package com.connect.meetupsfellow.mvp.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.connect.meetupsfellow.R;
import com.connect.meetupsfellow.constants.Constants;
import com.connect.meetupsfellow.mvp.view.activities.UserProfileActivity;
import com.connect.meetupsfellow.mvp.view.model.RecycleModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecycleGroupMembersAdapter extends RecyclerView.Adapter<RecycleGroupMembersAdapter.MyViewHolder> implements Filterable {

    public ArrayList<Boolean> clickedItems = new ArrayList<>();
    Context context;
    ArrayList<RecycleModel> users;
    public ArrayList<String> userIds = new ArrayList<>();
    List<RecycleModel> userAll;
    String userId;
    public OptionsMenuClickListener optionMenuClickListener;


    public RecycleGroupMembersAdapter(Context context, ArrayList<RecycleModel> users, OptionsMenuClickListener optionMenuClickListener) {
        this.context = context;
        this.users = users;
        this.userAll = new ArrayList<>(users);
        this.optionMenuClickListener = optionMenuClickListener;
    }

    public interface OptionsMenuClickListener {

        void onOptionMenuClicked(int position);
    }

    @NonNull
    @Override
    public RecycleGroupMembersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout (Giving look)
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_group_members, parent, false);
        return new RecycleGroupMembersAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleGroupMembersAdapter.MyViewHolder holder, int position) {

        int pos = position;

        Glide.with(context)
                .load(users.get(pos).getUserImg())
                .placeholder(R.drawable.meetupsfellow_transpatent)
                .into(holder.userImg);

        if (users.get(pos).getUserName().length() >= 14){

            String text = users.get(pos).getUserName().substring(0, 14) + "...";
            holder.userName.setText(text);
        } else {

            holder.userName.setText(users.get(pos).getUserName());
        }

        if (users.get(pos).getOnlineStatus().equals("false")){

            holder.userStatus.setText("Offline");

        } else {

            holder.userStatus.setText("Online");
        }

        holder.userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra(Constants.IntentDataKeys.UserId, Integer.parseInt(users.get(pos).getUserId()));
                context.startActivity(intent);
            }
        });

        holder.moreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                optionMenuClickListener.onOptionMenuClicked(pos);

            }
        });

        if (users.get(pos).getIAmAdmin()){

            if (users.get(pos).isAdmin()){

                holder.adminTxt.setVisibility(View.VISIBLE);
                holder.moreOptions.setVisibility(View.GONE);
            } else {

                holder.moreOptions.setVisibility(View.VISIBLE);
                holder.adminTxt.setVisibility(View.GONE);
            }
        } else {

            if (users.get(pos).isAdmin()){

                holder.adminTxt.setVisibility(View.VISIBLE);
                holder.moreOptions.setVisibility(View.GONE);
            } else {

                holder.moreOptions.setVisibility(View.GONE);
                holder.adminTxt.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemCount() {
        //Number of Items you want to display
        return users.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            ArrayList<RecycleModel> filteredList = new ArrayList<>();

            if (charSequence.toString().isEmpty()){

                filteredList.addAll(userAll);
            }
            else {

                for (RecycleModel filterData : userAll) {

                    if (filterData.getUserName().toLowerCase().contains(charSequence.toString().toLowerCase())) {

                        filteredList.add(filterData);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;


            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            users.clear();
            users.addAll((Collection<? extends RecycleModel>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //grabbing the views from rv_column.xml

        ImageView userImg;
        TextView userName, userStatus, moreOptions, adminTxt;
        LinearLayout llConversation;
        LinearLayout rlOptions;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            userImg = itemView.findViewById(R.id.ivUserImage);
            userName = itemView.findViewById(R.id.tvUserName);
            userStatus = itemView.findViewById(R.id.userStatus);
            llConversation = itemView.findViewById(R.id.llConversation);
            moreOptions = itemView.findViewById(R.id.moreOptions);
            adminTxt = itemView.findViewById(R.id.adminTxt);
            rlOptions = itemView.findViewById(R.id.rlOptions);
        }
    }
}
