package com.connect.meetupsfellow.mvp.view.adapter;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.connect.meetupsfellow.mvp.view.activities.ChatActivity;
import com.daimajia.swipe.SwipeLayout;
import com.connect.meetupsfellow.R;
import com.connect.meetupsfellow.mvp.view.activities.GroupChatActivity;
import com.connect.meetupsfellow.mvp.view.model.RecycleModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecycleGroupConvoAdapter extends RecyclerView.Adapter<RecycleGroupConvoAdapter.MyViewHolder> implements Filterable {

    public ArrayList<Boolean> clickedItems = new ArrayList<>();
    Context context;
    ArrayList<RecycleModel> users;
    public ArrayList<String> userIds = new ArrayList<>();
    List<RecycleModel> userAll;
    String userId;


    public RecycleGroupConvoAdapter(Context context, ArrayList<RecycleModel> users) {
        this.context = context;
        this.users = users;
        this.userAll = new ArrayList<>(users);

    }

    @NonNull
    @Override
    public RecycleGroupConvoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout (Giving look)
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_group_conversation, parent, false);
        return new RecycleGroupConvoAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleGroupConvoAdapter.MyViewHolder holder, int position) {

        int pos = position;

        Glide.with(context)
                .load(users.get(pos).getUserImg())
                .placeholder(R.drawable.meetupsfellow_transpatent)
                .into(holder.groupImg);

        holder.groupName.setText(users.get(pos).getUserName());

        holder.lastMsg.setText(users.get(pos).getLastMsg());

        holder.senderNameGChat.setText(users.get(pos).getLastMsgSender());

        if (users.get(pos).getUnReadCount().equals("0")){

            holder.unreadCount.setVisibility(View.GONE);
        } else {

            holder.unreadCount.setText(users.get(pos).getUnReadCount());
            holder.lstMsgTime.setTypeface(holder.lstMsgTime.getTypeface(), Typeface.BOLD);
            holder.lastMsg.setTypeface(holder.lastMsg.getTypeface(), Typeface.BOLD);
            holder.unreadCount.setVisibility(View.VISIBLE);
        }

        if (!users.get(pos).getLastMsgTime().equals("null")) {

            try {

                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                Date parsed = new Date(Long.parseLong(users.get(pos).getLastMsgTime()));

                Date now = new Date(System.currentTimeMillis()); // 2016-03-10 22:06:10

                Log.d("CurrentDateTime", parsed.toString() + "  " + now.toString());

                SimpleDateFormat date = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());

                String currentDate = date.format(now);

                String msgDate = date.format(parsed);

                int todayDate = Integer.parseInt(currentDate.substring(0,2));
                int msgDateInt = Integer.parseInt(msgDate.substring(0,2));

                Log.d("CurrentDates", todayDate + "  " + msgDateInt);

                if (currentDate.equals(msgDate)){

                    String time = sdf.format(parsed);

                    holder.lstMsgTime.setText(time);

                    Log.d("CurrentTime", time);

                } else if (todayDate-msgDateInt == 1 && currentDate.substring(2).equals(msgDate.substring(2))) {

                    holder.lstMsgTime.setText("Yesterday");

                } else {

                    holder.lstMsgTime.setText(msgDate);
                }

                //System.out.println(parsed.compareTo(now));
            } catch (Exception e) {
                e.printStackTrace();
            }

            /*Long time = Long.parseLong(users.get(pos).getLastMsgTime());

            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

            String date = formatter.format(time);

            holder.lstMsgTime.setText(date);*/
        }

        holder.swipe.setSwipeEnabled(false);

        holder.swipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPermission = false;

                if (Build.VERSION.SDK_INT < 33) {
                    Log.e("PermissionStore: ", "Version_below33");
                    isPermission = checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 1);
                }else {
                    Log.e("PermissionStore: ", "Version_Above%$");
                    isPermission = checkPermission(Manifest.permission.READ_MEDIA_IMAGES, 1);
                }
                Log.e("PermissionStore-: ", String.valueOf(isPermission));

                if (isPermission) {
                    Intent intent = new Intent(context, GroupChatActivity.class);
                    intent.putExtra("groupId", users.get(pos).getGroupId());
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "You must give storage permission to see Images", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = Uri.fromParts("package",context.getPackageName(), null);
                    intent.setData(uri);
                    context.startActivity(intent);
                }

            }
        });
    }
    public boolean checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions((Activity) context, new String[] { permission }, requestCode);
            Log.e("AppPermissionW", "denied");
            return false;
        }
        else {
            //Toast.makeText(activity, "You must give permission", Toast.LENGTH_SHORT).show();
            return true;
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

        ImageView groupImg;
        TextView groupName, lastMsg, unreadCount, lstMsgTime, senderNameGChat;
        SwipeLayout swipe;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            groupImg = itemView.findViewById(R.id.ivUserImage);
            groupName = itemView.findViewById(R.id.tvUserName);
            lastMsg = itemView.findViewById(R.id.tvLastMessage);
            unreadCount = itemView.findViewById(R.id.tvUnReadCount);
            lstMsgTime = itemView.findViewById(R.id.tvTimeAgo);
            swipe = itemView.findViewById(R.id.swipe);
            senderNameGChat = itemView.findViewById(R.id.senderNameGChat);

        }
    }
}
