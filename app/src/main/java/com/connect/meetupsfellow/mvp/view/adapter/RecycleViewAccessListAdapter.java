package com.connect.meetupsfellow.mvp.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.connect.meetupsfellow.R;
import com.connect.meetupsfellow.mvp.view.activities.PrivateAccessListActivity;
import com.connect.meetupsfellow.retrofit.response.ResponseConnectionRequest;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewAccessListAdapter extends RecyclerView.Adapter<RecycleViewAccessListAdapter.MyViewHolder> {

    Context context;
    ArrayList<ResponseConnectionRequest> userList;
    ArrayList<ResponseConnectionRequest> filteredUserList;
    ArrayList<Integer> selectUserList = new ArrayList<>();
    boolean isEditTable = false;

    public RecycleViewAccessListAdapter(Context context, boolean isSelected, ArrayList<ResponseConnectionRequest> userList) {
        this.context = context;
        this.userList = userList;
        this.isEditTable = isSelected;
        this.filteredUserList = new ArrayList<>(userList); // Initialize filtered list
        Log.e("PrivateAcces*", "userList_Ad: " + userList.size());
        Log.e("PrivateAcces*", "isEditTable: " + isEditTable);
    }

    @NonNull
    @Override
    public RecycleViewAccessListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_private_picture_access, parent, false);
        return new RecycleViewAccessListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewAccessListAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

       // Log.d("userImgUrl", filteredUserList.get(position).getImagePath());

        Glide.with(context)
                .load(filteredUserList.get(position).getImagePath())
                .placeholder(R.drawable.meetupsfellow_transpatent)
                .dontAnimate()
                .into(holder.ivUserImage);

        holder.tvUserName.setText(filteredUserList.get(position).getName());
        holder.tvUserLocation.setText(filteredUserList.get(position).getAboutMe());

        if (isEditTable) {
            if (filteredUserList.get(position).getIsmyprivatealbumaccess() &&
                    filteredUserList.get(position).getAccess_status().equals("selected_connected")) {
                holder.ivUserAccessCheckBox.setVisibility(View.VISIBLE);
                holder.ivUserAccessCheckBox.setChecked(true);
                selectUserList.add(filteredUserList.get(position).getUserId());
                ((PrivateAccessListActivity) context).selectUser(selectUserList);
            }
            holder.ivUserAccessCheckBox.setVisibility(View.VISIBLE);
        } else {
            if (filteredUserList.get(position).getIsmyprivatealbumaccess()) {
                holder.ivUserAccessCheckBox.setVisibility(View.VISIBLE);
                holder.ivUserAccessCheckBox.setChecked(true);
                holder.ivUserAccessCheckBox.setEnabled(false);
            } else {
                holder.ivUserAccessCheckBox.setVisibility(View.GONE);
            }
        }

       /* holder.llLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra(Constants.IntentDataKeys.UserId, filteredUserList.get(position).getUserId());
                context.startActivity(intent);
            }
        });*/

        holder.ivUserAccessCheckBox.setOnClickListener(v -> {
            if ( holder.ivUserAccessCheckBox.isChecked()){
                Log.e("ConnectRequest", "Check_true");
                selectUserList.add(filteredUserList.get(position).getUserId());
                ((PrivateAccessListActivity) context).selectUser(selectUserList);
            }else {
                Log.e("ConnectRequest", "Check_false");
                selectUserList.remove(Integer.valueOf(filteredUserList.get(position).getUserId()));
                ((PrivateAccessListActivity) context).selectUser(selectUserList);
            }
        });

       /* holder.ivUserAccessCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.e("ConnectRequest", "Check_true");
                    selectUserList.add(filteredUserList.get(position).getUserId());
                    ((PrivateAccessListActivity) context).selectUser(selectUserList);
                } else {
                    Log.e("ConnectRequest", "Check_false");
                    selectUserList.remove(Integer.valueOf(filteredUserList.get(position).getUserId()));
                    ((PrivateAccessListActivity) context).selectUser(selectUserList);
                }
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return filteredUserList.size();
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    filteredUserList = new ArrayList<>(userList);
                } else {
                    List<ResponseConnectionRequest> filteredList = new ArrayList<>();
                    for (ResponseConnectionRequest user : userList) {
                        if (user.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(user);
                        }
                    }
                    filteredUserList = (ArrayList<ResponseConnectionRequest>) filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredUserList;
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredUserList = (ArrayList<ResponseConnectionRequest>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void checkUncheck(boolean isCheck) {
        if (isCheck) {
            selectUserList.clear();
            for (int i = 0; i < filteredUserList.size(); i++) {
                selectUserList.add(filteredUserList.get(i).getUserId());
                ((PrivateAccessListActivity) context).selectUser(selectUserList);
                ///  holder.ivUserAccessCheckBox.setChecked(true);
            }
        } else {
            selectUserList.clear();
            ((PrivateAccessListActivity) context).selectUser(selectUserList);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivUserImage;
        TextView tvUserName, tvUserLocation;
        CheckBox ivUserAccessCheckBox;
        LinearLayout llLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ivUserAccessCheckBox = itemView.findViewById(R.id.ivUserAccess);
            ivUserImage = itemView.findViewById(R.id.ivUserImage);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserLocation = itemView.findViewById(R.id.tvUserLocation);
            llLayout = itemView.findViewById(R.id.llLayout);

        }
    }
}
