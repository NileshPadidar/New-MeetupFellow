package com.connect.meetupsfellow.mvp.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.connect.meetupsfellow.R;
import com.connect.meetupsfellow.mvp.view.activities.AddGroupMembersActivity;
import com.connect.meetupsfellow.mvp.view.model.RecycleModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecycleAddGroupMembersAdapter extends RecyclerView.Adapter<RecycleAddGroupMembersAdapter.MyViewHolder> implements Filterable {

    public ArrayList<Boolean> clickedItems = new ArrayList<>();
    Context context;
    ArrayList<RecycleModel> users;
    public ArrayList<String> userIds = new ArrayList<>();
    List<RecycleModel> userAll;
    String userId;
    //private ArrayList<ResponsePrivatePics> images = new ArrayList<>();
    //double[] mImageAspectRatios;
    /*private int height[] = {200,400,300,
            200,-2,-2,-2};
    private int width[] = {800,200,300,
            300,400,-1,-1};*/
    //private ArrayList<ResponseUserProfileImages> images = null;

    public RecycleAddGroupMembersAdapter(Context context, ArrayList<RecycleModel> users) {
        this.context = context;
        this.users = users;
        this.userAll = new ArrayList<>(users);

    }

    @NonNull
    @Override
    public RecycleAddGroupMembersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout (Giving look)
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_user_chat_list, parent, false);
        return new RecycleAddGroupMembersAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleAddGroupMembersAdapter.MyViewHolder holder, int position) {

        int pos = position;

        Glide.with(context)
                .load(users.get(pos).getUserImg())
                .placeholder(R.drawable.meetupsfellow_transpatent)
                .into(holder.ivFellowImage);

        holder.fellowName.setText(users.get(pos).getUserName());

        if (!userIds.isEmpty()){

            if (userIds.contains(users.get(pos).getUserId())){

                holder.addFellow.setChecked(true);
            }
            else {
                holder.addFellow.setChecked(false);
            }
        }

        boolean isChecked = holder.addFellow.isChecked();

        Log.d("fellowChecked", String.valueOf(isChecked));

        holder.addFellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isChecked) {

                    if (users.get(pos).getUserId() != null) {

                        userIds.remove(users.get(pos).getUserId());

                        ((AddGroupMembersActivity) context).addOrRemoveFellowsToGroup(users.get(pos).getUserId(), users.get(pos).getUserName());
                    }
                } else {

                    if (users.get(pos).getUserId() != null) {

                        userIds.add(users.get(pos).getUserId());

                        ((AddGroupMembersActivity) context).addOrRemoveFellowsToGroup(users.get(pos).getUserId(), users.get(pos).getUserName());
                    }
                }
            }
        });

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

        ImageView ivFellowImage;
        TextView fellowName;
        CheckBox addFellow;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ivFellowImage = itemView.findViewById(R.id.fellowImg);
            fellowName = itemView.findViewById(R.id.fellowNameTxt);
            addFellow = itemView.findViewById(R.id.addFellowCb);

        }
    }
}
