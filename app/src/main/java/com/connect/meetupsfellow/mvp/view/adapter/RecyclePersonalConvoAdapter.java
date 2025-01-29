package com.connect.meetupsfellow.mvp.view.adapter;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import static androidx.core.app.ActivityCompat.requestPermissions;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.connect.meetupsfellow.mvp.view.activities.HomeActivity;
import com.connect.meetupsfellow.mvp.view.fragment.PersonalChatFragment;
import com.daimajia.swipe.SwipeLayout;
import com.google.firebase.database.FirebaseDatabase;
import com.connect.meetupsfellow.R;
import com.connect.meetupsfellow.constants.Constants;
import com.connect.meetupsfellow.mvp.view.activities.ChatActivity;
import com.connect.meetupsfellow.mvp.view.model.RecycleModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecyclePersonalConvoAdapter extends RecyclerView.Adapter<RecyclePersonalConvoAdapter.MyViewHolder> implements Filterable {

    public ArrayList<Boolean> clickedItems = new ArrayList<>();
    Context context;
    ArrayList<RecycleModel> users;
    public ArrayList<String> userIds = new ArrayList<>();
    List<RecycleModel> userAll;
    String userId;


    public RecyclePersonalConvoAdapter(Context context, ArrayList<RecycleModel> users) {
        this.context = context;
        this.users = users;
        this.userAll = new ArrayList<>(users);

    }

    @NonNull
    @Override
    public RecyclePersonalConvoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout (Giving look)
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_group_conversation, parent, false);
        return new RecyclePersonalConvoAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclePersonalConvoAdapter.MyViewHolder holder, int position) {

        int pos = position;

        Log.d("OtherUserImg", users.get(pos).getUserImg());

        Glide.with(context)
                .load(users.get(pos).getUserImg())
                .placeholder(R.drawable.meetupsfellow_transpatent)
                .into(holder.groupImg);

        holder.groupName.setText(users.get(pos).getUserName());

        holder.lastMsg.setText(users.get(pos).getLastMsg());

        //holder.senderNameGChat.setText(users.get(pos).getLastMsgSender());
        //holder.senderNameGChat.setVisibility(View.GONE);

        switch (users.get(pos).getLastMsg()) {

            case "DOCX" : {
                holder.mediaImg.setVisibility(View.VISIBLE);
                holder.mediaImg.setBackgroundResource(R.drawable.google_docs);
                break;
            }

            case "PDF" : {
                holder.mediaImg.setVisibility(View.VISIBLE);
                holder.mediaImg.setBackgroundResource(R.drawable.pdf);
                break;
            }

            case "XLSX" : {

                holder.mediaImg.setVisibility(View.VISIBLE);
                holder.mediaImg.setBackgroundResource(R.drawable.xls);
                break;
            }

            case "Image" : {

                holder.mediaImg.setVisibility(View.VISIBLE);
                holder.mediaImg.setBackgroundResource(R.drawable.gallery);
                break;
            }

            case "Video" : {

                holder.mediaImg.setVisibility(View.VISIBLE);
                holder.mediaImg.setBackgroundResource(R.drawable.ic_baseline_videocam_24);
                break;
            }

            case "TXT" : {

                holder.mediaImg.setVisibility(View.VISIBLE);
                holder.mediaImg.setBackgroundResource(R.drawable.txt);
                break;
            }

            default: {

                holder.mediaImg.setVisibility(View.GONE);
                break;
            }

        }

        if (users.get(pos).getLastMsg().equals("DOCX")) {


        }

        if (users.get(pos).getUnReadCount().equals("0")){

            holder.unreadCount.setVisibility(View.GONE);
        } else {

            holder.unreadCount.setText(users.get(pos).getUnReadCount());
            holder.lstMsgTime.setTypeface(holder.lstMsgTime.getTypeface(), Typeface.BOLD);
            holder.lastMsg.setTypeface(holder.lastMsg.getTypeface(), Typeface.BOLD);
            holder.unreadCount.setVisibility(View.VISIBLE);
        }

        if (users.get(pos).getLastMsgSender().equals(users.get(pos).getUserName())) {

            holder.lastMsgStatus.setVisibility(View.GONE);

        } else {

            holder.lastMsgStatus.setVisibility(View.VISIBLE);

            switch (users.get(pos).getStatus()){

                case "1" : holder.lastMsgStatus.setBackgroundResource(R.drawable.tick);
                    break;

                case "2" : holder.lastMsgStatus.setBackgroundResource(R.drawable.double_tick);
                    break;

                case "3" : holder.lastMsgStatus.setBackgroundResource(R.drawable.double_tick_seen);
                    break;
            }

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

            /*ong time = Long.parseLong(users.get(pos).getLastMsgTime());

            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

            String date = formatter.format(time);

            holder.lstMsgTime.setText(date);*/
        }

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
                    Log.e("PermissionStore: ", "intent: "+ users.get(pos).getIsUserConnected());
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("ChatRoomId", users.get(pos).getGroupId());
                    intent.putExtra("otherUserId", users.get(pos).getOtherUserId());
                    intent.putExtra("otherUserName", users.get(pos).getUserName());
                    intent.putExtra("otherUserImg", users.get(pos).getUserImg());
                    intent.putExtra("firstTime", true);
                    intent.putExtra("IsUserConnected", users.get(pos).getIsUserConnected());
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

      /*  holder.swipe.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDeleteChatDialog(users.get(pos).getGroupId());
                return false;
            }
        });*/

        holder.swipe.setSwipeEnabled(false);

    }

    private void showDeleteChatDialog(String chatId) {

        Dialog dialog = new Dialog(context);

        dialog.setCancelable(true);

        View view = LayoutInflater.from(context).inflate(R.layout.custom_exit_dialog, null);

        dialog.setContentView(view);

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
        }
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView dialogHead = view.findViewById(R.id.dialog_title);
        TextView dialogContent = view.findViewById(R.id.dialog_content);
        Button cancelBtn  = view.findViewById(R.id.noExitBtn);
        Button confirm = view.findViewById(R.id.yesExitBtn);

        dialogHead.setText("Delete Conversation");
        dialogContent.setText("Are you sure, you want to delete this conversation?");

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.cancel();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance(Constants.Firebase.databaseUrl).getReference("Live").child("UserChat").child(chatId).removeValue();
                dialog.dismiss();
            }
        });

        dialog.show();
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

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //grabbing the views from rv_column.xml

        ImageView groupImg, lastMsgStatus, mediaImg;
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
            lastMsgStatus = itemView.findViewById(R.id.lastMsgStatus);
            mediaImg = itemView.findViewById(R.id.mediaImg);

        }
    }

}
