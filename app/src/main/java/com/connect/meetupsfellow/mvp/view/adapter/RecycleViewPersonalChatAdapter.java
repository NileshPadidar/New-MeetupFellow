package com.connect.meetupsfellow.mvp.view.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.connect.meetupsfellow.R;
import com.connect.meetupsfellow.constants.Constants;
import com.connect.meetupsfellow.mvp.view.activities.ChatActivity;
import com.connect.meetupsfellow.mvp.view.activities.VideoPlayerActivity;
import com.connect.meetupsfellow.mvp.view.model.RecycleModel;
import com.daimajia.swipe.SwipeLayout;
import com.giphy.sdk.core.GPHCore;
import com.giphy.sdk.core.models.enums.RenditionType;
import com.giphy.sdk.core.network.response.MediaResponse;
import com.giphy.sdk.ui.views.GPHMediaView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import io.github.ponnamkarthik.richlinkpreview.RichLinkView;
//import io.github.ponnamkarthik.richlinkpreview.ViewListener;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class RecycleViewPersonalChatAdapter extends RecyclerView.Adapter<RecycleViewPersonalChatAdapter.MyViewHolder> implements Filterable {

    private static final int MAX_LINES_COLLAPSED = 10;
    private final String regex = "([\\u20a0-\\u32ff\\ud83c\\udc00-\\ud83d\\udeff\\udbb9\\udce5-\\udbb9\\udcee])";

    public ArrayList<String> selectedPos = new ArrayList<>();
    Context context;
    RecyclerView mRecyclerView;
    ArrayList<RecycleModel> chats = new ArrayList<>();
    List<RecycleModel> chatsAll;
    ArrayList<MyViewHolder> holderArr = new ArrayList<>();
    Boolean isSelecting = false;

    String dateTimeChat;
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            ArrayList<RecycleModel> filteredList = new ArrayList<>();

            if (charSequence.toString().isEmpty()) {

                filteredList.addAll(chatsAll);
            } else {

                for (RecycleModel filterData : chatsAll) {

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

            chats.clear();
            chats.addAll((Collection<? extends RecycleModel>) filterResults.values);
            notifyDataSetChanged();
        }
    };
    private final boolean isExpanded = false;

    public RecycleViewPersonalChatAdapter(Context context) {
        this.context = context;
        //this.chats = chats;
        //this.chatsAll = new ArrayList<>(chats);
        selectedPos.clear();
        holderArr.clear();
    }

    private static boolean isEmoji(String message) {
        return message.matches("(?:[\uD83C\uDF00-\uD83D\uDDFF]|[\uD83E\uDD00-\uD83E\uDDFF]|" + "[\uD83D\uDE00-\uD83D\uDE4F]|[\uD83D\uDE80-\uD83D\uDEFF]|" + "[\u2600-\u26FF]\uFE0F?|[\u2700-\u27BF]\uFE0F?|\u24C2\uFE0F?|" + "[\uD83C\uDDE6-\uD83C\uDDFF]{1,2}|" + "[\uD83C\uDD70\uD83C\uDD71\uD83C\uDD7E\uD83C\uDD7F\uD83C\uDD8E\uD83C\uDD91-\uD83C\uDD9A]\uFE0F?|" + "[\u0023\u002A\u0030-\u0039]\uFE0F?\u20E3|[\u2194-\u2199\u21A9-\u21AA]\uFE0F?|[\u2B05-\u2B07\u2B1B\u2B1C\u2B50\u2B55]\uFE0F?|" + "[\u2934\u2935]\uFE0F?|[\u3030\u303D]\uFE0F?|[\u3297\u3299]\uFE0F?|" + "[\uD83C\uDE01\uD83C\uDE02\uD83C\uDE1A\uD83C\uDE2F\uD83C\uDE32-\uD83C\uDE3A\uD83C\uDE50\uD83C\uDE51]\uFE0F?|" + "[\u203C\u2049]\uFE0F?|[\u25AA\u25AB\u25B6\u25C0\u25FB-\u25FE]\uFE0F?|" + "[\u00A9\u00AE]\uFE0F?|[\u2122\u2139]\uFE0F?|\uD83C\uDC04\uFE0F?|\uD83C\uDCCF\uFE0F?|" + "[\u231A\u231B\u2328\u23CF\u23E9-\u23F3\u23F8-\u23FA]\uFE0F?)+");
    }

    public void addChats(ArrayList<RecycleModel> chats) {
        this.chats = chats;
        //notifyItemInserted(pos);
        //  notifyItemInserted(this.chats.size() - 1);
        Log.e("Send_Msg%", "Pers_Adapter-addChats: " + this.chats.size());
    }

    public void updateChats(RecycleModel chat) {

        this.chats.set(chats.size() - 1, chat);
        //notifyItemChanged(chats.size()-1);
        Log.e("Send_Msg%", "Pers_Adapter-updateChats: " + this.chats.size());
    }
   /* public void appendNewData(ArrayList<RecycleModel> newData) {
        if (newData != null && !newData.isEmpty()) {
            // Get the old size of recycleModel before appending the new data
            int oldSize = chats.size();

            // Append the new data to recycleModel
            chats.addAll(0,newData);

            // Update the adapter's data
            //recycleViewPersonalChatAdapter.addChats(recycleModel); // Assuming this method updates the data in the adapter

            // Notify the adapter about the new items added
            notifyItemRangeInserted(oldSize, newData.size());

            Log.e("Send_Msg%", "New data appended successfully");
            Log.e("Send_Msg%", "Adapter_OLD_Size: "+oldSize);
            Log.e("Send_Msg%", "Adapter_NEW_Size: "+newData.size());
            Log.e("Send_Msg%", "Adapter_ALL_Size: "+chats.size());
        }
    }*/

    @SuppressLint("NotifyDataSetChanged")
    public void appendNewData(ArrayList<RecycleModel> newData) {
        if (newData != null && !newData.isEmpty()) {
            int oldSize = chats.size();
            ArrayList<RecycleModel> oldData = new ArrayList<>(chats);
            Log.e("Send_Msg%", "oldData_Size: " + oldData.size());
            // Step 2: Clear the current 'chats' list
            chats.clear();
            // Step 3: Add the new data to 'chats'
            chats.addAll(0, newData);
            // Step 4: Append the old data (copied earlier) to 'chats'
            chats.addAll(oldData);

            notifyItemRangeInserted(0, newData.size());
            notifyDataSetChanged();

            Log.e("Send_Msg%", "New data appended successfully");
            Log.e("Send_Msg%", "Adapter_OLD_Size: " + oldSize);
            Log.e("Send_Msg%", "Adapter_NEW_Size: " + newData.size());
            Log.e("Send_Msg%", "Adapter_ALL_Size: " + chats.size());
            // Restore the scroll position
            ((ChatActivity) context).mantanScrollPostion(newData.size(), chats);
        }
    }

    public void removeChats(int pos) {

        this.chats.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(0, chats.size());
    }

    @NonNull
    @Override
    public RecycleViewPersonalChatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout (Giving look)
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_new_replay_chat, parent, false);
        mRecyclerView = (RecyclerView) parent;
        return new RecycleViewPersonalChatAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewPersonalChatAdapter.MyViewHolder holder, int position) {
        Log.e("Send_Msg%", "Pers_Adapter_Position: " + position);
        ChatActivity chatActivity = (ChatActivity) context;
        int pos = position;
        holder.setIsRecyclable(false);
        holderArr.add(holder);
       /* if (chats.size() == position){
            Log.e("ChatLog$", "Call_scrollToBottomNested");
            chatActivity.scrollToBottomNested();
        }*/

        String currentUserId = context.getSharedPreferences("UserId", MODE_PRIVATE).getString("UserId", "");

        if (!selectedPos.isEmpty()) {

            if (selectedPos.contains(String.valueOf(pos))) {

                if (chats.get(pos).getUserId().equals(currentUserId)) {

                    holder.selfChatLay.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me_selected));
                    holder.gifViewLay.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me_selected));
                    holder.flFile.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me_selected));
                } else {

                    holder.othersChatLay.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_other_selected));
                    holder.gifViewLayO.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_other_selected));
                    holder.flFileO.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_other_selected));
                }
            }
        }

        Long dateTime = Long.parseLong(chats.get(pos).getLastMsgTime());

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Date parsed = new Date(Long.parseLong(chats.get(pos).getLastMsgTime()));

            Date now = new Date(System.currentTimeMillis()); // 2016-03-10 22:06:10

            Log.d("CurrentDateTime", parsed + "  " + now);

            SimpleDateFormat date = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());

            String currentDate = date.format(now);

            String msgDate = date.format(parsed);

            int todayDate = Integer.parseInt(currentDate.substring(0, 2));
            int msgDateInt = Integer.parseInt(msgDate.substring(0, 2));

            Log.d("CurrentDates", todayDate + "  " + msgDateInt);

            if (currentDate.equals(msgDate)) {

                String time = sdf.format(parsed);

                dateTimeChat = time;

                Log.d("CurrentTime", time);

            } else {

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy h:mm aa", Locale.getDefault());
                dateTimeChat = simpleDateFormat.format(dateTime);
                Log.d("msgTime", dateTimeChat);

            }

            //System.out.println(parsed.compareTo(now));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("ChatLog", "chatId: " + chats.get(pos).getChatId());
        Log.e("ChatLog", "messageText: " + chats.get(pos).getLastMsg());
      /*  Log.e("ChatLog", "senderName: " + chats.get(pos).getUserName());
        Log.e("ChatLog", "receiverId: " + currentUserId);
        Log.e("ChatLog", "msgTime: " + chats.get(pos).getLastMsgTime());
        Log.e("ChatLog", "mediaUrl: " + chats.get(pos).getVideoUrl());
        Log.e("ChatLog", "mediaSize: " + chats.get(pos).getMediaSize());
        // Log.e("ChatLog", "messagesDeletedBY_Self: " +chats.get(pos).ge);
        // Log.e("ChatLog", "messagesDeletedBY: " +isClearedOther);
        Log.e("ChatLog", "mediaDeviceUrlOf: " + chats.get(pos).getImgPath());
        Log.e("ChatLog", "thumbnailUrl: " + chats.get(pos).getThumbImg());
        Log.e("ChatLog", "deliveryStatus: 3");
        Log.e("ChatLog", "mediaType: 1");
        Log.e("ChatLog", "messageType: 1");*/


        float radius = context.getResources().getDimension(R.dimen.inner_img_xxl_1);
        holder.ivChatImage.setShapeAppearanceModel(holder.ivChatImage.getShapeAppearanceModel().toBuilder()
                //.setAllCorners(CornerFamily.ROUNDED,radius)
                .setTopLeftCorner(CornerFamily.ROUNDED, radius).setTopRightCorner(CornerFamily.ROUNDED, radius).build());

        holder.ivChatImageO.setShapeAppearanceModel(holder.ivChatImageO.getShapeAppearanceModel().toBuilder()
                //.setAllCorners(CornerFamily.ROUNDED,radius)
                //.setTopLeftCorner(CornerFamily.ROUNDED,radius)
                .setTopRightCorner(CornerFamily.ROUNDED, radius).build());

        holder.ivChatImageBlur.setShapeAppearanceModel(holder.ivChatImageBlur.getShapeAppearanceModel().toBuilder()
                //.setAllCorners(CornerFamily.ROUNDED,radius)
                .setTopLeftCorner(CornerFamily.ROUNDED, radius).setTopRightCorner(CornerFamily.ROUNDED, radius).build());

        holder.ivChatImageBlurO.setShapeAppearanceModel(holder.ivChatImageBlurO.getShapeAppearanceModel().toBuilder()
                //.setAllCorners(CornerFamily.ROUNDED,radius)
                //.setTopLeftCorner(CornerFamily.ROUNDED,radius)
                .setTopRightCorner(CornerFamily.ROUNDED, radius).build());

        holder.videoBlurImg.setShapeAppearanceModel(holder.videoBlurImg.getShapeAppearanceModel().toBuilder()
                //.setAllCorners(CornerFamily.ROUNDED,radius)
                .setTopLeftCorner(CornerFamily.ROUNDED, radius).setTopRightCorner(CornerFamily.ROUNDED, radius).build());

        holder.videoBlurImgO.setShapeAppearanceModel(holder.videoBlurImgO.getShapeAppearanceModel().toBuilder()
                //.setAllCorners(CornerFamily.ROUNDED,radius)
                //.setTopLeftCorner(CornerFamily.ROUNDED,radius)
                .setTopRightCorner(CornerFamily.ROUNDED, radius).build());

        holder.pdfPreviewImg.setShapeAppearanceModel(holder.pdfPreviewImg.getShapeAppearanceModel().toBuilder()
                //.setAllCorners(CornerFamily.ROUNDED,radius)
                .setTopLeftCorner(CornerFamily.ROUNDED, radius).setTopRightCorner(CornerFamily.ROUNDED, radius).build());

        holder.pdfPreviewImgO.setShapeAppearanceModel(holder.pdfPreviewImgO.getShapeAppearanceModel().toBuilder()
                //.setAllCorners(CornerFamily.ROUNDED,radius)
                //.setTopLeftCorner(CornerFamily.ROUNDED,radius)
                .setTopRightCorner(CornerFamily.ROUNDED, radius).build());

        holder.ivChatVideoPlay.setShapeAppearanceModel(holder.ivChatVideoPlay.getShapeAppearanceModel().toBuilder()
                //.setAllCorners(CornerFamily.ROUNDED,radius)
                .setTopLeftCorner(CornerFamily.ROUNDED, radius).setTopRightCorner(CornerFamily.ROUNDED, radius).build());

        holder.ivChatVideoPlayO.setShapeAppearanceModel(holder.ivChatVideoPlayO.getShapeAppearanceModel().toBuilder()
                //.setAllCorners(CornerFamily.ROUNDED,radius)
                //.setTopLeftCorner(CornerFamily.ROUNDED,radius)
                .setTopRightCorner(CornerFamily.ROUNDED, radius).build());

        /*holder.fileBgIv.setShapeAppearanceModel(holder.fileBgIv.getShapeAppearanceModel()
                .toBuilder()
                //.setAllCorners(CornerFamily.ROUNDED,radius)
                //.setTopLeftCorner(CornerFamily.ROUNDED,radius)
                .setBottomLeftCorner(CornerFamily.ROUNDED,radius)
                .build());*/

        SimpleDateFormat simpleDateFormatR = new SimpleDateFormat("dd MMM yyyy h:mm:ss aa", Locale.getDefault());
        String dateTimeChatR = simpleDateFormatR.format(dateTime);

        if (!currentUserId.isEmpty()) {

            if (!chats.get(pos).getGroupNotice().isEmpty() && !chats.get(pos).getGroupNotice().equals("null")) {

                String newNotice = "";

                if (chats.get(pos).getGroupNotice().contains(Constants.INSTANCE.getCurrentUser())) {

                    newNotice = chats.get(pos).getGroupNotice().replace(Constants.INSTANCE.getCurrentUser(), "You");
                    holder.groupNotice.setText(newNotice);
                } else {

                    holder.groupNotice.setText(chats.get(pos).getGroupNotice());
                }

                holder.selfChatLay.setVisibility(View.GONE);
                holder.othersChatLay.setVisibility(View.GONE);
                holder.rlGroupNotice.setVisibility(View.VISIBLE);

            } else {

                if (!chats.get(pos).getUserImg().isEmpty() && !chats.get(pos).getUserImg().equals("null")) {

                    Log.d("ChatImgPath", chats.get(pos).getImgPath());

                    if (chats.get(pos).getUserId().equals(currentUserId)) {

                        holder.selfChatLay.setVisibility(View.VISIBLE);
                        holder.othersChatLay.setVisibility(View.GONE);
                        holder.rlGroupNotice.setVisibility(View.GONE);
                        holder.chatImgDate.setText(dateTimeChat);
                        holder.chatId.setText(chats.get(pos).getChatId());
                        holder.flImage.setVisibility(View.VISIBLE);
                        holder.flImageO.setVisibility(View.GONE);
                        Log.e("ReplyImg%%", "!!!11");

                        if (!chats.get(pos).getImgPath().equals("null") && !chats.get(pos).getImgPath().isEmpty()) {

                            MediaScannerConnection.scanFile(context, new String[]{chats.get(pos).getImgPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.d("ExternalStorage", "Scanned " + path + ":");
                                    Log.d("ExternalStorage", "-> uri=" + uri);

                                    //imgUri = uri;

                                    String[] extArr = path.split("\\.");

                                    Log.d("DocumentFile", extArr[extArr.length - 1]);
                                    String ext = extArr[extArr.length - 1];

                                    if (uri != null && !uri.equals("null")) {

                                        chatActivity.runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {

                                                // Stuff that updates the UI

                                                if ((ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("mp4") || ext.equals("mkv"))) {

                                                    try {
                                                        holder.ivChatImage.setImageURI(uri);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        Toast.makeText(chatActivity, "Please give media access permission", Toast.LENGTH_SHORT).show();
                                                    }

                                                } else {

                                                    Glide.with(context).load(chats.get(pos).getThumbImg()).thumbnail(0.01f).override(100, 100).into(holder.ivChatImage);
                                                }

                                                holder.ivChatImageBlur.setVisibility(View.GONE);
                                            }
                                        });
                                    } else {

                                        chatActivity.runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {

                                                // Stuff that updates the UI
                                                Glide.with(context).load(chats.get(pos).getThumbImg()).thumbnail(0.01f).override(100, 100).into(holder.ivChatImage);

                                                    /*float radius = context.getResources().getDimension(R.dimen.inner_img_xxl_1);
                                                    holder.ivChatImageBlur.setShapeAppearanceModel(holder.ivChatImageBlur.getShapeAppearanceModel()
                                                            .toBuilder()
                                                            //.setAllCorners(CornerFamily.ROUNDED,radius)
                                                            .setTopLeftCorner(CornerFamily.ROUNDED,radius)
                                                            .setTopRightCorner(CornerFamily.ROUNDED,radius)
                                                            .build());*/

                                                holder.ivChatImageBlur.setVisibility(View.VISIBLE);
                                                holder.downloadProgress.setVisibility(View.GONE);
                                                holder.downloadImgBtn.setVisibility(View.VISIBLE);
                                            }
                                        });


                                    }

                                }
                            });

                            /// Senser Replay Image from all type show below ****
                            if (!chats.get(pos).getReplyTo().isEmpty() && !chats.get(pos).getReplyTo().equals("null")) {

                                Log.d("ReplyImgS", chats.get(pos).getReplyImg());

                                if (!chats.get(pos).getReplyImg().isEmpty() && !chats.get(pos).getReplyImg().equals("null")) {

                                    Glide.with(context).load(chats.get(pos).getReplyImg()).thumbnail(0.01f).override(100, 100).placeholder(R.drawable.meetupsfellow_transpatent).into(holder.selfReplyImg);

                                    holder.selfReplyImgLay.setVisibility(View.VISIBLE);
                                    holder.otherReplyImgLay.setVisibility(View.GONE);

                                } else {

                                    holder.selfReplyImgLay.setVisibility(View.GONE);
                                    holder.otherReplyImgLay.setVisibility(View.GONE);
                                }

                                holder.replyChatLayU.setVisibility(View.VISIBLE);
                                holder.replyChatLayO.setVisibility(View.GONE);
                                holder.replyChatMessage.setText(chats.get(pos).getReplyMsg());
                                holder.userReplyName.setText(chats.get(pos).getReplyTo());

                            } else {

                                holder.replyChatLayU.setVisibility(View.GONE);
                                holder.replyChatLayO.setVisibility(View.GONE);
                            }

                        } else {

                            Glide.with(context).load(chats.get(pos).getThumbImg()).thumbnail(0.01f).override(100, 100).into(holder.ivChatImage);
                            /// Replay Image from all type show below ****
                            if (!chats.get(pos).getReplyTo().isEmpty() && !chats.get(pos).getReplyTo().equals("null")) {

                                Log.d("ReplyImgS", chats.get(pos).getReplyImg());

                                if (!chats.get(pos).getReplyImg().isEmpty() && !chats.get(pos).getReplyImg().equals("null")) {

                                    Glide.with(context).load(chats.get(pos).getReplyImg()).thumbnail(0.01f).override(100, 100).placeholder(R.drawable.meetupsfellow_transpatent).into(holder.selfReplyImg);

                                    holder.selfReplyImgLay.setVisibility(View.VISIBLE);
                                    holder.otherReplyImgLay.setVisibility(View.GONE);

                                } else {

                                    holder.selfReplyImgLay.setVisibility(View.GONE);
                                    holder.otherReplyImgLay.setVisibility(View.GONE);
                                }

                                holder.replyChatLayU.setVisibility(View.VISIBLE);
                                holder.replyChatLayO.setVisibility(View.GONE);
                                holder.replyChatMessage.setText(chats.get(pos).getReplyMsg());
                                holder.userReplyName.setText(chats.get(pos).getReplyTo());

                            } else {

                                holder.replyChatLayU.setVisibility(View.GONE);
                                holder.replyChatLayO.setVisibility(View.GONE);
                            }
                            //float radius = context.getResources().getDimension(R.dimen.inner_img_xxl_1);
                                /*holder.ivChatImageBlur.setShapeAppearanceModel(holder.ivChatImageBlur.getShapeAppearanceModel()
                                        .toBuilder()
                                        //.setAllCorners(CornerFamily.ROUNDED,radius)
                                        .setTopLeftCorner(CornerFamily.ROUNDED,radius)
                                        .setTopRightCorner(CornerFamily.ROUNDED,radius)
                                        .build());*/

                            holder.ivChatImageBlur.setVisibility(View.VISIBLE);
                            holder.downloadProgress.setVisibility(View.GONE);
                            holder.downloadImgBtn.setVisibility(View.VISIBLE);

                        }


                        switch (chats.get(pos).isReadByUser()) {

                            case "1": {
                                holder.ivChatStatus.setBackgroundResource(R.drawable.tick);
                                holder.ivChatStatusImg.setBackgroundResource(R.drawable.tick);
                            }
                            break;

                            case "2": {
                                holder.ivChatStatus.setBackgroundResource(R.drawable.double_tick);
                                holder.ivChatStatusImg.setBackgroundResource(R.drawable.double_tick);
                            }
                            break;

                            case "3": {
                                holder.ivChatStatus.setBackgroundResource(R.drawable.double_tick_seen);
                                holder.ivChatStatusImg.setBackgroundResource(R.drawable.double_tick_seen);
                            }
                            break;
                        }


                       /* if (chats.get(pos).isReadByAll()) {

                            holder.ivChatStatus.setBackgroundResource(R.drawable.double_tick_seen);
                            holder.ivChatStatusImg.setBackgroundResource(R.drawable.double_tick_seen);

                        } else {

                            holder.ivChatStatus.setBackgroundResource(R.drawable.double_tick);
                            holder.ivChatStatusImg.setBackgroundResource(R.drawable.double_tick);
                        }*/

                    } else {

                        holder.selfChatLay.setVisibility(View.GONE);
                        holder.othersChatLay.setVisibility(View.VISIBLE);
                        holder.rlGroupNotice.setVisibility(View.GONE);
                        holder.chatIdO.setText(chats.get(pos).getChatId());
                        holder.userNameChatOther.setText(chats.get(pos).getUserName());
                        holder.chatImgDateO.setText(dateTimeChat);
                        holder.flImage.setVisibility(View.GONE);
                        holder.flImageO.setVisibility(View.VISIBLE);

                        Log.e("ReplyImg%%", "@@222");
                        if (!chats.get(pos).getImgPath().equals("null") && !chats.get(pos).getImgPath().isEmpty()) {

                            MediaScannerConnection.scanFile(context, new String[]{chats.get(pos).getImgPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.d("ExternalStorage", "Scanned " + path + ":");
                                    Log.d("ExternalStorage", "-> uri=" + uri);
                                    //imgUri = uri;

                                    if (uri != null && !uri.equals("null")) {

                                        String[] extArr = path.split("\\.");

                                        Log.d("DocumentFile", extArr[extArr.length - 1]);
                                        String ext = extArr[extArr.length - 1];

                                        chatActivity.runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {

                                                // Stuff that updates the UI

                                                if ((ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("mp4") || ext.equals("mkv"))) {
                                                    holder.ivChatImageO.setImageURI(uri);
                                                    // Glide.with(context).load(chats.get(pos).getImgPath()).thumbnail(0.01f).override(100, 100).placeholder(R.drawable.meetupsfellow_transpatent).into(holder.ivChatImageO);

                                                } else {

                                                    Glide.with(context).load(chats.get(pos).getThumbImg()).thumbnail(0.01f).override(100, 100).into(holder.ivChatImageO);
                                                }

                                                holder.ivChatImageBlurO.setVisibility(View.GONE);
                                            }
                                        });
                                    } else {

                                        chatActivity.runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {

                                                // Stuff that updates the UI
                                                Glide.with(context).load(chats.get(pos).getThumbImg()).thumbnail(0.01f).override(100, 100).into(holder.ivChatImageO);

                                                holder.ivChatImageBlurO.setVisibility(View.GONE);
                                                holder.downloadProgressO.setVisibility(View.GONE);
                                                holder.downloadImgBtnO.setVisibility(View.VISIBLE);
                                            }
                                        });


                                    }
                                }
                            });
                            /// Reciver Replay Image from all type show below ****
                            if (!chats.get(pos).getReplyTo().isEmpty() && !chats.get(pos).getReplyTo().equals("null")) {

                                Log.e("ReplyImgO", chats.get(pos).getReplyImg());

                                if (!chats.get(pos).getReplyImg().isEmpty() && !chats.get(pos).getReplyImg().equals("null")) {

                                    Glide.with(context).load(chats.get(pos).getReplyImg()).thumbnail(0.01f).override(100, 100).placeholder(R.drawable.meetupsfellow_transpatent).into(holder.otherReplyImg);

                                    holder.selfReplyImgLay.setVisibility(View.GONE);
                                    holder.otherReplyImgLay.setVisibility(View.VISIBLE);
                                } else {

                                    holder.selfReplyImgLay.setVisibility(View.GONE);
                                    holder.otherReplyImgLay.setVisibility(View.GONE);
                                }

                                holder.replyChatLayU.setVisibility(View.GONE);
                                holder.replyChatLayO.setVisibility(View.VISIBLE);
                                holder.replyChatMessageO.setText(chats.get(pos).getReplyMsg());

                                if (chats.get(pos).getReplyTo().equals(Constants.INSTANCE.getCurrentUser())) {

                                    holder.userReplyNameO.setText("You");
                                } else {
                                    holder.userReplyNameO.setText(chats.get(pos).getReplyTo());
                                }

                            } else {

                                holder.replyChatLayU.setVisibility(View.GONE);
                                holder.replyChatLayO.setVisibility(View.GONE);
                            }
                            /*if (imgUri != null && !imgUri.equals("null")) {

                                holder.ivChatImageO.setImageURI(imgUri);
                            } else {

                                holder.downloadProgressO.setVisibility(View.GONE);
                                holder.downloadImgBtnO.setVisibility(View.VISIBLE);
                            }*/

                        } else {
                            Log.e("ReplyImg%%", "@@222_Els");
                            Glide.with(context).load(chats.get(pos).getThumbImg()).thumbnail(0.01f).override(100, 100).into(holder.ivChatImageO);

                            holder.downloadProgressO.setVisibility(View.GONE);
                            holder.downloadImgBtnO.setVisibility(View.VISIBLE);
                            holder.ivChatImageBlurO.setVisibility(View.GONE);

                            /// Reciver Replay Image from all type show below ****
                            if (!chats.get(pos).getReplyTo().isEmpty() && !chats.get(pos).getReplyTo().equals("null")) {

                                Log.e("ReplyImgO", chats.get(pos).getReplyImg());

                                if (!chats.get(pos).getReplyImg().isEmpty() && !chats.get(pos).getReplyImg().equals("null")) {

                                    Glide.with(context).load(chats.get(pos).getReplyImg()).thumbnail(0.01f).override(100, 100).placeholder(R.drawable.meetupsfellow_transpatent).into(holder.otherReplyImg);

                                    holder.selfReplyImgLay.setVisibility(View.GONE);
                                    holder.otherReplyImgLay.setVisibility(View.VISIBLE);
                                } else {

                                    holder.selfReplyImgLay.setVisibility(View.GONE);
                                    holder.otherReplyImgLay.setVisibility(View.GONE);
                                }

                                holder.replyChatLayU.setVisibility(View.GONE);
                                holder.replyChatLayO.setVisibility(View.VISIBLE);
                                holder.replyChatMessageO.setText(chats.get(pos).getReplyMsg());

                                if (chats.get(pos).getReplyTo().equals(Constants.INSTANCE.getCurrentUser())) {

                                    holder.userReplyNameO.setText(chats.get(pos).getReplyTo());
                                } else {
                                    holder.userReplyNameO.setText("You");
                                }

                            } else {

                                holder.replyChatLayU.setVisibility(View.GONE);
                                holder.replyChatLayO.setVisibility(View.GONE);
                            }
                        }
                    }


                } else {
                    Log.e("Personal_Adap", "elsss__1&*");
                    if (!chats.get(pos).getVideoUrl().isEmpty() && !chats.get(pos).getVideoUrl().equals("null")) {
                        Log.e("Personal_Adap", "IFF__Video");
                        if (chats.get(pos).getUserId().equals(currentUserId)) {

                            holder.selfChatLay.setVisibility(View.VISIBLE);
                            holder.othersChatLay.setVisibility(View.GONE);
                            holder.rlGroupNotice.setVisibility(View.GONE);
                            holder.chatImgDate.setText(dateTimeChat);
                            holder.chatId.setText(chats.get(pos).getChatId());
                            holder.flImage.setVisibility(View.GONE);
                            holder.flImageO.setVisibility(View.GONE);
                            holder.flPlayO.setVisibility(View.GONE);
                            holder.flPlay.setVisibility(View.VISIBLE);
                            holder.userNameChatVideo.setText(chats.get(pos).getUserName());
                            holder.chatVideoDate.setText(dateTimeChat);

                            /// sender Replay from  video all type show below ****
                            if (!chats.get(pos).getReplyTo().isEmpty() && !chats.get(pos).getReplyTo().equals("null")) {

                                Log.d("ReplyImgS", chats.get(pos).getReplyImg());
                                // Get the existing layout params or create new ones if they don't exist
                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.replyChatLayU.getLayoutParams();
                                if (layoutParams == null) {
                                    layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                                }
                                // Set the alignment rule
                                layoutParams.addRule(RelativeLayout.ALIGN_END, R.id.flPlay);
                                // Apply the updated layout params
                                holder.replyChatLayU.setLayoutParams(layoutParams);

                                if (!chats.get(pos).getReplyImg().isEmpty() && !chats.get(pos).getReplyImg().equals("null")) {

                                    Glide.with(context).load(chats.get(pos).getReplyImg()).thumbnail(0.01f).override(100, 100).placeholder(R.drawable.meetupsfellow_transpatent).into(holder.selfReplyImg);

                                    holder.selfReplyImgLay.setVisibility(View.VISIBLE);
                                    holder.otherReplyImgLay.setVisibility(View.GONE);


                                } else {

                                    holder.selfReplyImgLay.setVisibility(View.GONE);
                                    holder.otherReplyImgLay.setVisibility(View.GONE);
                                }

                                holder.replyChatLayU.setVisibility(View.VISIBLE);
                                holder.replyChatLayO.setVisibility(View.GONE);
                                holder.replyChatMessage.setText(chats.get(pos).getReplyMsg());
                                holder.userReplyName.setText(chats.get(pos).getReplyTo());

                            } else {

                                holder.replyChatLayU.setVisibility(View.GONE);
                                holder.replyChatLayO.setVisibility(View.GONE);
                            }

                            if (!chats.get(pos).getImgPath().equals("null") && !chats.get(pos).getImgPath().isEmpty()) {

                                MediaScannerConnection.scanFile(context, new String[]{chats.get(pos).getImgPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                                    public void onScanCompleted(String path, Uri uri) {
                                        Log.d("ExternalStorage", "Scanned " + path + ":");
                                        Log.e("ExternalStorage", "-> uri= " + uri);
                                        //imgUri = uri;

                                        if (uri != null && !uri.equals("null")) {

                                            String[] extArr = path.split("\\.");

                                            Log.d("DocumentFile", extArr[extArr.length - 1]);
                                            String ext = extArr[extArr.length - 1];
                                            Log.e("Personal_Adap", "IFF__Video_URI- " + ext);
                                            chatActivity.runOnUiThread(new Runnable() {

                                                @Override
                                                public void run() {

                                                    // Stuff that updates the UI

                                                    if ((ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("mp4") || ext.equals("mkv"))) {

                                                        Glide.with(context).load(chats.get(pos).getImgPath()).thumbnail(0.01f).override(100, 100).into(holder.ivChatVideoPlay);

                                                        holder.videoBlurImg.setVisibility(View.GONE);
                                                        holder.videoDownloadBtn.setVisibility(View.GONE);
                                                        Log.e("Personal_Adap", "InIF**");
                                                    } else {
                                                        Log.e("Personal_Adap", "In_Elss**");
                                                    }
                                                }
                                            });
                                        } else {
                                            Log.e("Personal_Adap", "ELSS__Video_URI_Not");
                                            chatActivity.runOnUiThread(new Runnable() {

                                                @Override
                                                public void run() {
                                                    // Stuff that updates the UI
                                                    Glide.with(context).load(chats.get(pos).getThumbImg()).thumbnail(0.01f).override(100, 100).into(holder.ivChatVideoPlay);

                                                    holder.videoBlurImg.setVisibility(View.GONE);
                                                    holder.videoDownloadBtn.setVisibility(View.GONE);

                                                }
                                            });


                                        }
                                    }
                                });

                            } else {

                                Glide.with(context).load(chats.get(pos).getThumbImg()).thumbnail(0.01f).override(100, 100).into(holder.ivChatVideoPlay);

                                //holder.downloadProgressO.setVisibility(View.GONE);
                                holder.videoBlurImg.setVisibility(View.VISIBLE);
                                holder.videoDownloadBtn.setVisibility(View.VISIBLE);

                            }

                            /*Glide.with(context)
                                    .load(chats.get(pos).getThumbImg())
                                    .into(holder.ivChatVideoPlay);*/


                            holder.ivChatStatus.setVisibility(View.GONE);
                            switch (chats.get(pos).isReadByUser()) {

                                case "1": {
                                    holder.ivChatStatusVideo.setBackgroundResource(R.drawable.tick);
                                    holder.ivChatStatusImg.setBackgroundResource(R.drawable.tick);
                                }
                                break;

                                case "2": {
                                    holder.ivChatStatusVideo.setBackgroundResource(R.drawable.double_tick);
                                    holder.ivChatStatusImg.setBackgroundResource(R.drawable.double_tick);
                                }
                                break;

                                case "3": {
                                    holder.ivChatStatusVideo.setBackgroundResource(R.drawable.double_tick_seen);
                                    holder.ivChatStatusImg.setBackgroundResource(R.drawable.double_tick_seen);
                                }
                                break;
                            }

                          /*  if (chats.get(pos).isReadByAll()) {

                                holder.ivChatStatus.setVisibility(View.GONE);
                                holder.ivChatStatusImg.setBackgroundResource(R.drawable.double_tick_seen);
                                holder.ivChatStatusVideo.setBackgroundResource(R.drawable.double_tick_seen);

                            } else {

                                holder.ivChatStatus.setVisibility(View.GONE);
                                holder.ivChatStatusImg.setBackgroundResource(R.drawable.double_tick);
                                holder.ivChatStatusVideo.setBackgroundResource(R.drawable.double_tick);
                            }*/

                        } else {

                            holder.selfChatLay.setVisibility(View.GONE);
                            holder.othersChatLay.setVisibility(View.VISIBLE);
                            holder.rlGroupNotice.setVisibility(View.GONE);
                            holder.chatImgDateO.setText(dateTimeChat);
                            holder.chatIdO.setText(chats.get(pos).getChatId());
                            holder.flImage.setVisibility(View.GONE);
                            holder.flImageO.setVisibility(View.GONE);
                            holder.flPlayO.setVisibility(View.VISIBLE);
                            holder.flPlay.setVisibility(View.GONE);
                            holder.userNameChatVideo1.setText(chats.get(pos).getUserName());
                            holder.chatVideoDateO.setText(dateTimeChat);

                            /// Resiver Replay from  video all type show below ****
                            if (!chats.get(pos).getReplyTo().isEmpty() && !chats.get(pos).getReplyTo().equals("null")) {

                                Log.e("ReplyImgO", chats.get(pos).getReplyImg());
                                // Get the existing layout params or create new ones if they don't exist
                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.replyChatLayO.getLayoutParams();
                                if (layoutParams == null) {
                                    layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                                }
                                // Set the alignment rule
                                layoutParams.addRule(RelativeLayout.ALIGN_END, R.id.flPlayO);
                                // Apply the updated layout params
                                holder.replyChatLayO.setLayoutParams(layoutParams);

                                if (!chats.get(pos).getReplyImg().isEmpty() && !chats.get(pos).getReplyImg().equals("null")) {

                                    Glide.with(context).load(chats.get(pos).getReplyImg()).thumbnail(0.01f).override(100, 100).placeholder(R.drawable.meetupsfellow_transpatent).into(holder.otherReplyImg);

                                    holder.selfReplyImgLay.setVisibility(View.GONE);
                                    holder.otherReplyImgLay.setVisibility(View.VISIBLE);
                                } else {

                                    holder.selfReplyImgLay.setVisibility(View.GONE);
                                    holder.otherReplyImgLay.setVisibility(View.GONE);
                                }

                                holder.replyChatLayU.setVisibility(View.GONE);
                                holder.replyChatLayO.setVisibility(View.VISIBLE);
                                holder.replyChatMessageO.setText(chats.get(pos).getReplyMsg());

                                if (chats.get(pos).getReplyTo().equals(Constants.INSTANCE.getCurrentUser())) {

                                    holder.userReplyNameO.setText("You");
                                } else {

                                    holder.userReplyNameO.setText(chats.get(pos).getReplyTo());
                                }

                            } else {

                                holder.replyChatLayU.setVisibility(View.GONE);
                                holder.replyChatLayO.setVisibility(View.GONE);
                            }

                            if (!chats.get(pos).getImgPath().equals("null") && !chats.get(pos).getImgPath().isEmpty()) {

                                MediaScannerConnection.scanFile(context, new String[]{chats.get(pos).getImgPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                                    public void onScanCompleted(String path, Uri uri) {
                                        Log.d("ExternalStorage", "Scanned " + path + ":");
                                        Log.d("ExternalStorage", "-> uri=" + uri);
                                        //imgUri = uri;

                                        if (uri != null && !uri.equals("null")) {

                                            String[] extArr = path.split("\\.");

                                            Log.d("DocumentFile", extArr[extArr.length - 1]);
                                            String ext = extArr[extArr.length - 1];

                                            chatActivity.runOnUiThread(new Runnable() {

                                                @Override
                                                public void run() {

                                                    // Stuff that updates the UI

                                                    if ((ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("mp4") || ext.equals("mkv"))) {

                                                        Glide.with(context).load(chats.get(pos).getImgPath()).thumbnail(0.01f).override(100, 100).into(holder.ivChatVideoPlayO);

                                                        holder.videoBlurImgO.setVisibility(View.GONE);
                                                        holder.videoDownloadBtnO.setVisibility(View.GONE);
                                                        holder.ivChatVideoPlayO.setVisibility(View.VISIBLE);

                                                    }
                                                }
                                            });
                                        } else {

                                            chatActivity.runOnUiThread(new Runnable() {

                                                @Override
                                                public void run() {

                                                    // Stuff that updates the UI
                                                    Glide.with(context).load(chats.get(pos).getThumbImg()).thumbnail(0.01f).override(100, 100).into(holder.ivChatVideoPlayO);

                                                    holder.videoBlurImgO.setVisibility(View.VISIBLE);
                                                    holder.videoDownloadBtnO.setVisibility(View.VISIBLE);
                                                    holder.ivChatVideoPlayO.setVisibility(View.GONE);

                                                }
                                            });


                                        }
                                    }
                                });

                            } else {

                                Glide.with(context).load(chats.get(pos).getThumbImg()).thumbnail(0.01f).override(100, 100).into(holder.ivChatVideoPlayO);

                                //holder.downloadProgressO.setVisibility(View.GONE);
                                holder.videoBlurImgO.setVisibility(View.VISIBLE);
                                holder.videoDownloadBtnO.setVisibility(View.VISIBLE);

                            }

                            /*Glide.with(context)
                                    .load(chats.get(pos).getThumbImg())
                                    .into(holder.ivChatVideoPlayO);*/
                        }
                    } else if (!chats.get(pos).getFileUrl().isEmpty() && !chats.get(pos).getFileUrl().equals("null")) {
                        Log.e("Personal_Adap", "elsssIFF__File$");
                        if (chats.get(pos).getUserId().equals(currentUserId)) {

                            holder.selfChatLay.setVisibility(View.VISIBLE);
                            holder.othersChatLay.setVisibility(View.GONE);
                            holder.flFile.setVisibility(View.VISIBLE);
                            holder.flFileO.setVisibility(View.GONE);

                            holder.chatFileDate.setText(dateTimeChat);

                            if (chats.get(pos).getLastMsg().length() >= 50) {

                                holder.fileNameTv.setText(chats.get(pos).getLastMsg().substring(0, 46) + "...");

                            } else {

                                holder.fileNameTv.setText(chats.get(pos).getLastMsg());
                            }


                            String s = chats.get(pos).getMediaSize();
                            holder.fileSizeTv.setText(s);

                           /* s =  s.replace("mb","");
                            // long fileSize = Long.parseLong(chats.get(pos).getMediaSize())/1024;
                           if (s.equals("0.0 ")){
                               holder.fileSizeTv.setText(s + " KB");
                           }else {
                               long fileSize = Long.parseLong(s)/1024;
                               if (fileSize <= 0){

                                   holder.fileSizeTv.setText(chats.get(pos).getMediaSize() + " KB");

                               } else {

                                   holder.fileSizeTv.setText(Long.toString(fileSize) + " MB");
                               }
                           }*/

                            if (chats.get(pos).getFileUrl().contains(".pdf")) {

                                holder.pdfPreviewImg.setVisibility(View.VISIBLE);
                                holder.fileIconIv.setBackgroundResource(R.drawable.pdf);
                                Log.e("Personal_Adap", "File_Thu: " + chats.get(pos).getThumbImg());
                                Glide.with(context).load(chats.get(pos).getThumbImg()).centerCrop().into(holder.pdfPreviewImg);

                            } else if (chats.get(pos).getFileUrl().contains(".doc") || chats.get(pos).getFileUrl().contains(".docx") || chats.get(pos).getFileUrl().contains(".ppt")) {

                                holder.fileIconIv.setBackgroundResource(R.drawable.google_docs);

                                holder.fileBgIv.setShapeAppearanceModel(holder.fileBgIv.getShapeAppearanceModel().toBuilder()
                                        //.setAllCorners(CornerFamily.ROUNDED,radius)
                                        .setTopLeftCorner(CornerFamily.ROUNDED, radius).setTopRightCorner(CornerFamily.ROUNDED, radius).build());

                                holder.pdfPreviewImg.setVisibility(View.GONE);

                            } else if (chats.get(pos).getFileUrl().contains(".xls") || chats.get(pos).getFileUrl().contains(".xlsx")) {

                                holder.fileIconIv.setBackgroundResource(R.drawable.xls);

                                holder.fileBgIv.setShapeAppearanceModel(holder.fileBgIv.getShapeAppearanceModel().toBuilder()
                                        //.setAllCorners(CornerFamily.ROUNDED,radius)
                                        .setTopLeftCorner(CornerFamily.ROUNDED, radius).setTopRightCorner(CornerFamily.ROUNDED, radius).build());

                                holder.pdfPreviewImg.setVisibility(View.GONE);

                            } else if (chats.get(pos).getFileUrl().contains(".txt")) {

                                holder.fileIconIv.setBackgroundResource(R.drawable.txt);

                                holder.fileBgIv.setShapeAppearanceModel(holder.fileBgIv.getShapeAppearanceModel().toBuilder()
                                        //.setAllCorners(CornerFamily.ROUNDED,radius)
                                        .setTopLeftCorner(CornerFamily.ROUNDED, radius).setTopRightCorner(CornerFamily.ROUNDED, radius).build());

                                holder.pdfPreviewImg.setVisibility(View.GONE);
                            } else {
                                ///    holder.fileIconIv.setBackgroundResource(R.drawable.ic_baseline_file_present_24);
                                holder.fileBgIv.setShapeAppearanceModel(holder.fileBgIv.getShapeAppearanceModel().toBuilder()
                                        //.setAllCorners(CornerFamily.ROUNDED,radius)
                                        .setTopLeftCorner(CornerFamily.ROUNDED, radius).setTopRightCorner(CornerFamily.ROUNDED, radius).build());
                            }
                            Log.e("ExternalStorage%", "imagPath: " + chats.get(pos).getImgPath());
                            if (!chats.get(pos).getImgPath().equals("null") && !chats.get(pos).getImgPath().isEmpty()) {
                                Log.e("ExternalStorage%", "IFF_DownloadFile");
                                holder.fileDownload.setVisibility(View.GONE);
                               /* MediaScannerConnection.scanFile(context, new String[]{chats.get(pos).getImgPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                                    public void onScanCompleted(String path, Uri uri) {
                                        Log.e("ExternalStorage%", "Scanned " + path + ":");
                                        Log.d("ExternalStorage", "-> uri=" + uri);
                                        //imgUri = uri;

                                        if (uri != null && !uri.equals("null")) {

                                            String[] extArr = path.split("\\.");

                                            Log.d("DocumentFile", extArr[extArr.length - 1]);
                                            String ext = extArr[extArr.length - 1];

                                            chatActivity.runOnUiThread(new Runnable() {

                                                @Override
                                                public void run() {

                                                    // Stuff that updates the UI
                                                    holder.fileDownload.setVisibility(View.GONE);
                                                }
                                            });
                                        } else {

                                            chatActivity.runOnUiThread(new Runnable() {

                                                @Override
                                                public void run() {
                                                    Log.e("ExternalStorage", "Down_Vis");
                                                    holder.fileDownload.setVisibility(View.VISIBLE);
                                                }
                                            });


                                        }
                                    }
                                });*/

                            } else {
                                Log.e("ExternalStorage%", "Elss_DownloadFile");
                                holder.fileDownload.setVisibility(View.VISIBLE);

                            }


                            switch (chats.get(pos).isReadByUser()) {

                                case "1": {
                                    holder.ivChatStatusFile.setBackgroundResource(R.drawable.tick);
                                }
                                break;

                                case "2": {
                                    holder.ivChatStatusFile.setBackgroundResource(R.drawable.double_tick);
                                }
                                break;

                                case "3": {
                                    holder.ivChatStatusFile.setBackgroundResource(R.drawable.double_tick_seen);
                                }
                                break;
                            }

                           /* if (chats.get(pos).isReadByAll()) {

                                holder.ivChatStatusFile.setBackgroundResource(R.drawable.double_tick_seen);
                            } else {

                                holder.ivChatStatusFile.setBackgroundResource(R.drawable.double_tick);
                            }*/

                            /// sender Replay from  Files all type show below ****
                            if (!chats.get(pos).getReplyTo().isEmpty() && !chats.get(pos).getReplyTo().equals("null")) {
                                holder.pdfPreviewImg.setVisibility(View.VISIBLE);
                                Log.d("ReplyImgS", chats.get(pos).getReplyImg());
                                // Get the existing layout params or create new ones if they don't exist
                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.replyChatLayU.getLayoutParams();
                                if (layoutParams == null) {
                                    layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                                }
                                // Set the alignment rule
                                layoutParams.addRule(RelativeLayout.ALIGN_END, R.id.flFile);
                                // Apply the updated layout params
                                holder.replyChatLayU.setLayoutParams(layoutParams);

                                if (!chats.get(pos).getReplyImg().isEmpty() && !chats.get(pos).getReplyImg().equals("null")) {

                                    Glide.with(context).load(chats.get(pos).getReplyImg()).thumbnail(0.01f).override(100, 100).placeholder(R.drawable.meetupsfellow_transpatent).into(holder.selfReplyImg);

                                    holder.selfReplyImgLay.setVisibility(View.VISIBLE);
                                    holder.otherReplyImgLay.setVisibility(View.GONE);


                                } else {

                                    holder.selfReplyImgLay.setVisibility(View.GONE);
                                    holder.otherReplyImgLay.setVisibility(View.GONE);
                                }

                                holder.replyChatLayU.setVisibility(View.VISIBLE);
                                holder.replyChatLayO.setVisibility(View.GONE);
                                holder.replyChatMessage.setText(chats.get(pos).getReplyMsg());
                                holder.userReplyName.setText(chats.get(pos).getReplyTo());

                            } else {

                                holder.replyChatLayU.setVisibility(View.GONE);
                                holder.replyChatLayO.setVisibility(View.GONE);
                            }


                        } else {

                            holder.selfChatLay.setVisibility(View.GONE);
                            holder.othersChatLay.setVisibility(View.VISIBLE);
                            holder.flFile.setVisibility(View.GONE);
                            holder.flFileO.setVisibility(View.VISIBLE);

                            holder.chatFileDateO.setText(dateTimeChat);

                            if (chats.get(pos).getLastMsg().length() >= 50) {

                                holder.fileNameTvO.setText(chats.get(pos).getLastMsg().substring(0, 46) + "...");

                            } else {

                                holder.fileNameTvO.setText(chats.get(pos).getLastMsg());
                            }

                            String s = chats.get(pos).getMediaSize();
                            holder.fileSizeTvO.setText(s);
                            Log.e("FileGet", "sssFIrst:" + s);
                           /* if(s.contains("kb") || s.contains("mb")){
                                holder.fileSizeTvO.setText(chats.get(pos).getMediaSize());
                            }else {
                                s = s.replace("kb", "");
                                s = s.replace("mb", "");
                                Log.e("FileGet", "sss:" + s);
                                // long fileSize = Long.parseLong(chats.get(pos).getMediaSize())/1024;
                                Double fileSize = Double.parseDouble(s) / 1024;
                                Log.e("FileGet", "filesize:" + fileSize);
                                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                                String formattedValue = decimalFormat.format(fileSize);
                                Log.e("FileGet", "formattedValue:" + formattedValue);
                                if (fileSize <= 0 || formattedValue.contains("0.")) {

                                    holder.fileSizeTvO.setText(chats.get(pos).getMediaSize() + " kb");

                                } else {
                                    holder.fileSizeTvO.setText(formattedValue + " mb");

                                }
                            }*/

                            if (chats.get(pos).getFileUrl().contains(".pdf")) {

                                holder.pdfPreviewImgO.setVisibility(View.VISIBLE);
                                holder.fileIconIvO.setBackgroundResource(R.drawable.pdf);
                                Log.e("Personal_Adap", "File_Thu@#: " + chats.get(pos).getThumbImg());
                                Glide.with(context).load(chats.get(pos).getThumbImg()).centerCrop().into(holder.pdfPreviewImgO);

                            } else if (chats.get(pos).getFileUrl().contains(".doc") || chats.get(pos).getFileUrl().contains(".docx") || chats.get(pos).getFileUrl().contains(".ppt")) {

                                holder.fileIconIvO.setBackgroundResource(R.drawable.google_docs);

                                holder.fileBgIvO.setShapeAppearanceModel(holder.fileBgIvO.getShapeAppearanceModel().toBuilder()
                                        //.setAllCorners(CornerFamily.ROUNDED,radius)
                                        //.setTopLeftCorner(CornerFamily.ROUNDED,radius)
                                        .setTopRightCorner(CornerFamily.ROUNDED, radius).build());

                                holder.pdfPreviewImgO.setVisibility(View.GONE);

                            } else if (chats.get(pos).getFileUrl().contains(".xls") || chats.get(pos).getFileUrl().contains(".xlsx")) {

                                holder.fileIconIvO.setBackgroundResource(R.drawable.xls);

                                holder.fileBgIvO.setShapeAppearanceModel(holder.fileBgIvO.getShapeAppearanceModel().toBuilder()
                                        //.setAllCorners(CornerFamily.ROUNDED,radius)
                                        //.setTopLeftCorner(CornerFamily.ROUNDED,radius)
                                        .setTopRightCorner(CornerFamily.ROUNDED, radius).build());

                                holder.pdfPreviewImgO.setVisibility(View.GONE);

                            } else if (chats.get(pos).getFileUrl().contains(".txt")) {

                                holder.fileIconIvO.setBackgroundResource(R.drawable.txt);

                                holder.fileBgIvO.setShapeAppearanceModel(holder.fileBgIvO.getShapeAppearanceModel().toBuilder()
                                        //.setAllCorners(CornerFamily.ROUNDED,radius)
                                        //.setTopLeftCorner(CornerFamily.ROUNDED,radius)
                                        .setTopRightCorner(CornerFamily.ROUNDED, radius).build());

                                holder.pdfPreviewImgO.setVisibility(View.GONE);
                            } else {

                                holder.fileBgIvO.setShapeAppearanceModel(holder.fileBgIvO.getShapeAppearanceModel().toBuilder()
                                        //.setAllCorners(CornerFamily.ROUNDED,radius)
                                        //.setTopLeftCorner(CornerFamily.ROUNDED,radius)
                                        .setTopRightCorner(CornerFamily.ROUNDED, radius).build());
                            }

                            if (!chats.get(pos).getImgPath().equals("null") && !chats.get(pos).getImgPath().isEmpty()) {

                                MediaScannerConnection.scanFile(context, new String[]{chats.get(pos).getImgPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                                    public void onScanCompleted(String path, Uri uri) {
                                        Log.d("ExternalStorage", "Scanned " + path);
                                        Log.e("ExternalStorage", "-> Fileuri=" + uri);
                                        //imgUri = uri;

                                        if (path != null && !path.equals("null")) {

                                            String[] extArr = path.split("\\.");

                                            Log.e("DocumentFile", extArr[extArr.length - 1]);
                                            String ext = extArr[extArr.length - 1];

                                            chatActivity.runOnUiThread(new Runnable() {

                                                @Override
                                                public void run() {

                                                    // Stuff that updates the UI
                                                    holder.fileDownloadO.setVisibility(View.GONE);
                                                }
                                            });
                                        } else {

                                            chatActivity.runOnUiThread(new Runnable() {

                                                @Override
                                                public void run() {
                                                    Log.e("ExternalStorage", "File_DOW_Vis");
                                                    holder.fileDownloadO.setVisibility(View.VISIBLE);
                                                }
                                            });


                                        }
                                    }
                                });

                            } else {
                                Log.e("ExternalStorage", "File_DOW_Vis@@");
                                holder.fileDownloadO.setVisibility(View.VISIBLE);

                            }

                            /// Resiver Replay from  Files all type show below ****
                            if (!chats.get(pos).getReplyTo().isEmpty() && !chats.get(pos).getReplyTo().equals("null")) {
                                holder.pdfPreviewImgO.setVisibility(View.VISIBLE);
                                Log.e("ReplyImgO", chats.get(pos).getReplyImg());
                                // Get the existing layout params or create new ones if they don't exist
                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.replyChatLayO.getLayoutParams();
                                if (layoutParams == null) {
                                    layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                                }
                                // Set the alignment rule
                                layoutParams.addRule(RelativeLayout.ALIGN_END, R.id.flFileO);
                                // Apply the updated layout params
                                holder.replyChatLayO.setLayoutParams(layoutParams);

                                if (!chats.get(pos).getReplyImg().isEmpty() && !chats.get(pos).getReplyImg().equals("null")) {

                                    Glide.with(context).load(chats.get(pos).getReplyImg()).thumbnail(0.01f).override(100, 100).placeholder(R.drawable.meetupsfellow_transpatent).into(holder.otherReplyImg);

                                    holder.selfReplyImgLay.setVisibility(View.GONE);
                                    holder.otherReplyImgLay.setVisibility(View.VISIBLE);
                                } else {

                                    holder.selfReplyImgLay.setVisibility(View.GONE);
                                    holder.otherReplyImgLay.setVisibility(View.GONE);
                                }

                                holder.replyChatLayU.setVisibility(View.GONE);
                                holder.replyChatLayO.setVisibility(View.VISIBLE);
                                holder.replyChatMessageO.setText(chats.get(pos).getReplyMsg());

                                if (chats.get(pos).getReplyTo().equals(Constants.INSTANCE.getCurrentUser())) {

                                    holder.userReplyNameO.setText("You");
                                } else {

                                    holder.userReplyNameO.setText(chats.get(pos).getReplyTo());
                                }

                            } else {

                                holder.replyChatLayU.setVisibility(View.GONE);
                                holder.replyChatLayO.setVisibility(View.GONE);
                            }

                        }
                    } else if (!chats.get(pos).getGifId().isEmpty() && !chats.get(pos).getGifId().equals("null")) {
                        Log.e("Personal_Adap", "elsssIFF__Gif");
                        if (chats.get(pos).getUserId().equals(currentUserId)) {

                            holder.selfChatLay.setVisibility(View.VISIBLE);
                            holder.othersChatLay.setVisibility(View.GONE);
                            holder.gifViewLay.setVisibility(View.VISIBLE);
                            holder.gifViewLayO.setVisibility(View.GONE);
                            holder.tvTimeGif.setText(dateTimeChat);
                            Log.e("Personal_Adap", "GifItem_Visible");
                            GPHCore.INSTANCE.gifById(chats.get(pos).getGifId(), new Function2<MediaResponse, Throwable, Unit>() {
                                @Override
                                public Unit invoke(MediaResponse mediaResponse, Throwable throwable) {
                                    Log.e("Personal_Adap", "GifItem_Visible_innn");
                                    holder.gifView.setMedia(mediaResponse.getData(), RenditionType.original, context.getDrawable(R.drawable.meetupsfellow_transpatent));
                                    return null;
                                }
                            });


                            switch (chats.get(pos).isReadByUser()) {
                                case "1": {
                                    holder.ivStatusGif.setBackgroundResource(R.drawable.tick);
                                }
                                break;

                                case "2": {
                                    holder.ivStatusGif.setBackgroundResource(R.drawable.double_tick);
                                }
                                break;

                                case "3": {
                                    holder.ivStatusGif.setBackgroundResource(R.drawable.double_tick_seen);
                                }
                                break;
                            }

                           /* if (chats.get(pos).isReadByAll()) {

                                holder.ivStatusGif.setBackgroundResource(R.drawable.double_tick_seen);
                            } else {

                                holder.ivStatusGif.setBackgroundResource(R.drawable.double_tick);
                            }*/

                            /// sender Replay from  video all type show below ****
                            if (!chats.get(pos).getReplyTo().isEmpty() && !chats.get(pos).getReplyTo().equals("null")) {

                                Log.d("ReplyImgS", chats.get(pos).getReplyImg());
                                // Get the existing layout params or create new ones if they don't exist
                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.replyChatLayU.getLayoutParams();
                                if (layoutParams == null) {
                                    layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                                }
                                // Set the alignment rule
                                layoutParams.addRule(RelativeLayout.ALIGN_END, R.id.gifViewLay);
                                // Apply the updated layout params
                                holder.replyChatLayU.setLayoutParams(layoutParams);

                                if (!chats.get(pos).getReplyImg().isEmpty() && !chats.get(pos).getReplyImg().equals("null")) {

                                    Glide.with(context).load(chats.get(pos).getReplyImg()).thumbnail(0.01f).override(100, 100).placeholder(R.drawable.meetupsfellow_transpatent).into(holder.selfReplyImg);

                                    holder.selfReplyImgLay.setVisibility(View.VISIBLE);
                                    holder.otherReplyImgLay.setVisibility(View.GONE);


                                } else {

                                    holder.selfReplyImgLay.setVisibility(View.GONE);
                                    holder.otherReplyImgLay.setVisibility(View.GONE);
                                }

                                holder.replyChatLayU.setVisibility(View.VISIBLE);
                                holder.replyChatLayO.setVisibility(View.GONE);
                                holder.replyChatMessage.setText(chats.get(pos).getReplyMsg());
                                holder.userReplyName.setText(chats.get(pos).getReplyTo());

                            } else {

                                holder.replyChatLayU.setVisibility(View.GONE);
                                holder.replyChatLayO.setVisibility(View.GONE);
                            }


                        } else {

                            holder.selfChatLay.setVisibility(View.GONE);
                            holder.othersChatLay.setVisibility(View.VISIBLE);
                            holder.gifViewLay.setVisibility(View.GONE);
                            holder.gifViewLayO.setVisibility(View.VISIBLE);
                            holder.tvTimeGifO.setText(dateTimeChat);
                            holder.userNameChatGifO.setText(chats.get(pos).getUserName());
                            Log.e("Personal_Adap", "GifItem_Visible00");
                            GPHCore.INSTANCE.gifById(chats.get(pos).getGifId(), new Function2<MediaResponse, Throwable, Unit>() {
                                @Override
                                public Unit invoke(MediaResponse mediaResponse, Throwable throwable) {

                                    holder.gifViewO.setMedia(mediaResponse.getData(), RenditionType.original, context.getDrawable(R.drawable.meetupsfellow_transpatent));
                                    return null;
                                }
                            });

                            /// Resiver Replay from  GIF all type show below ****
                            if (!chats.get(pos).getReplyTo().isEmpty() && !chats.get(pos).getReplyTo().equals("null")) {

                                Log.e("ReplyImgO", chats.get(pos).getReplyImg());
                                // Get the existing layout params or create new ones if they don't exist
                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.replyChatLayO.getLayoutParams();
                                if (layoutParams == null) {
                                    layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                                }
                                // Set the alignment rule
                                layoutParams.addRule(RelativeLayout.ALIGN_END, R.id.gifViewLayO);
                                // Apply the updated layout params
                                holder.replyChatLayO.setLayoutParams(layoutParams);

                                if (!chats.get(pos).getReplyImg().isEmpty() && !chats.get(pos).getReplyImg().equals("null")) {

                                    Glide.with(context).load(chats.get(pos).getReplyImg()).thumbnail(0.01f).override(100, 100).placeholder(R.drawable.meetupsfellow_transpatent).into(holder.otherReplyImg);

                                    holder.selfReplyImgLay.setVisibility(View.GONE);
                                    holder.otherReplyImgLay.setVisibility(View.VISIBLE);
                                } else {

                                    holder.selfReplyImgLay.setVisibility(View.GONE);
                                    holder.otherReplyImgLay.setVisibility(View.GONE);
                                }

                                holder.replyChatLayU.setVisibility(View.GONE);
                                holder.replyChatLayO.setVisibility(View.VISIBLE);
                                holder.replyChatMessageO.setText(chats.get(pos).getReplyMsg());

                                if (chats.get(pos).getReplyTo().equals(Constants.INSTANCE.getCurrentUser())) {

                                    holder.userReplyNameO.setText("You");
                                } else {

                                    holder.userReplyNameO.setText(chats.get(pos).getReplyTo());
                                }

                            } else {

                                holder.replyChatLayU.setVisibility(View.GONE);
                                holder.replyChatLayO.setVisibility(View.GONE);
                            }

                        }
                    } else {
                        Log.e("Personal_Adap", "Else__End$");
                        if (!chats.get(pos).getLastMsg().isEmpty()) {

                            if (chats.get(pos).getUserId().equals(currentUserId)) {

                                if (!chats.get(pos).getReplyTo().isEmpty() && !chats.get(pos).getReplyTo().equals("null")) {

                                    Log.d("ReplyImgS", chats.get(pos).getReplyImg());

                                    if (!chats.get(pos).getReplyImg().isEmpty() && !chats.get(pos).getReplyImg().equals("null")) {

                                        Glide.with(context).load(chats.get(pos).getReplyImg()).thumbnail(0.01f).override(100, 100).placeholder(R.drawable.meetupsfellow_transpatent).into(holder.selfReplyImg);

                                        holder.selfReplyImgLay.setVisibility(View.VISIBLE);
                                        holder.otherReplyImgLay.setVisibility(View.GONE);

                                    } else {

                                        holder.selfReplyImgLay.setVisibility(View.GONE);
                                        holder.otherReplyImgLay.setVisibility(View.GONE);
                                    }

                                    holder.replyChatLayU.setVisibility(View.VISIBLE);
                                    holder.replyChatLayO.setVisibility(View.GONE);
                                    holder.replyChatMessage.setText(chats.get(pos).getReplyMsg());
                                    holder.userReplyName.setText(chats.get(pos).getReplyTo());

                                } else {

                                    holder.replyChatLayU.setVisibility(View.GONE);
                                    holder.replyChatLayO.setVisibility(View.GONE);
                                }
                                Log.e("ChatTime#", " selfChatTime_iFF");

                                holder.othersChatLay.setVisibility(View.GONE);
                                holder.selfChatLay.setVisibility(View.VISIBLE);
                                holder.rlGroupNotice.setVisibility(View.GONE);
                                holder.chatId.setText(chats.get(pos).getChatId());
                                holder.selfChatTime.setText(dateTimeChat);
                                holder.selfChat.setText(chats.get(pos).getLastMsg());
                                holder.selfChat.post(new Runnable() {
                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    @Override
                                    public void run() {
                                        // Past the maximum number of lines we want to display.
                                        if (holder.selfChat.getLineCount() > MAX_LINES_COLLAPSED) {
                                            int lastCharShown = holder.selfChat.getLayout().getLineVisibleEnd(MAX_LINES_COLLAPSED - 1);

                                            holder.selfChat.setMaxLines(MAX_LINES_COLLAPSED);

                                            Log.e("Set_Text", "ReadMore_Visible_self: " + chats.get(pos).getLastMsg());
                                            // String moreString = context.getString(R.string.more);
                                            String moreString = context.getString(R.string.read_more);
                                            String suffix = moreString;

                                            // 3 is a "magic number" but it's just basically the length of the ellipsis we're going to insert
                                            String actionDisplayText = chats.get(pos).getLastMsg().substring(0, lastCharShown - suffix.length() - 10) + "..." + suffix;
                                            SpannableString truncatedSpannableString = new SpannableString(actionDisplayText);
                                            int startIndex = actionDisplayText.indexOf(moreString);
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                truncatedSpannableString.setSpan(new ForegroundColorSpan(context.getColor(android.R.color.holo_blue_dark)), startIndex, startIndex + moreString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            }
                                            holder.selfChat.setText(truncatedSpannableString);
                                            holder.selfChat.setClickable(true);
                                        } else {
                                            holder.selfChat.setClickable(false);
                                        }
                                    }
                                });


                                switch (chats.get(pos).isReadByUser()) {

                                    case "1": {
                                        holder.ivChatStatus.setBackgroundResource(R.drawable.tick);
                                        holder.ivChatStatusImg.setBackgroundResource(R.drawable.tick);
                                    }
                                    break;

                                    case "2": {
                                        holder.ivChatStatus.setBackgroundResource(R.drawable.double_tick);
                                        holder.ivChatStatusImg.setBackgroundResource(R.drawable.double_tick);
                                    }
                                    break;

                                    case "3": {
                                        holder.ivChatStatus.setBackgroundResource(R.drawable.double_tick_seen);
                                        holder.ivChatStatusImg.setBackgroundResource(R.drawable.double_tick_seen);
                                    }
                                    break;
                                }

                               /* if (chats.get(pos).isReadByAll()) {

                                    holder.ivChatStatus.setBackgroundResource(R.drawable.double_tick_seen);
                                    holder.ivChatStatusImg.setBackgroundResource(R.drawable.double_tick_seen);

                                } else {

                                    holder.ivChatStatus.setBackgroundResource(R.drawable.double_tick);
                                    holder.ivChatStatusImg.setBackgroundResource(R.drawable.double_tick);
                                }*/

                                //chats.get(pos).getLastMsg().matches("(([A-Za-z]{3,9}:(?://)?)(?:[-;:&=+$,\\w]+@)?[A-Za-z0-9.-]+|(?:www\\.|[-;:&=+$,\\w]+@)[A-Za-z0-9.-]+)((?:/[+~%/.\\w-]*)?\\??(?:[-+=&;%@.\\w]*)#?(?:[.!/\\\\\\w]*))?")

                                if (chats.get(pos).getLastMsg().matches("(([A-Za-z]{3,9}:(?://)?)(?:[-;:&=+$,\\w]+@)?[A-Za-z0-9.-]+|(?:www\\.|[-;:&=+$,\\w]+@)[A-Za-z0-9.-]+)((?:/[+~%/.\\w-]*)?\\??(?:[-+=&;%@.\\w]*)#?(?:[.!/\\\\\\w]*))?") && !Patterns.EMAIL_ADDRESS.matcher(chats.get(pos).getLastMsg()).matches()) {

                                    holder.richLinkView.setText(chats.get(pos).getLastMsg());
                                    holder.richLinkView.setAutoLinkMask(View.FOCUSABLE);
                                  /*  holder.richLinkView.setLink(chats.get(pos).getLastMsg(), new ViewListener() {
                                        @Override
                                        public void onSuccess(boolean status) {

                                            Log.d("LinkPreview", String.valueOf(status));
                                            holder.richLinkView.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onError(Exception e) {

                                            Log.d("LinkPreview", String.valueOf(e));
                                            holder.richLinkView.setVisibility(View.GONE);
                                        }
                                    });*/
                                }

                                if (chats.get(pos).getLastMsg().length() == 2) {

                                    if (isEmoji(chats.get(pos).getLastMsg())) {

                                        holder.selfChat.setTextSize(50);
                                    }
                                    Matcher matchEmo = Pattern.compile(regex).matcher(chats.get(pos).getLastMsg());
                                    if (matchEmo.find()) {

                                        //System.out.println(matchEmo.group());
                                        Log.d("MsgIsEmoji", matchEmo.group());
                                        Log.d("Emoji Size", String.valueOf(chats.get(pos).getLastMsg().length()));
                                    }
                                }

                            } else {

                                if (!chats.get(pos).getReplyTo().isEmpty() && !chats.get(pos).getReplyTo().equals("null")) {

                                    Log.e("ReplyImgO", chats.get(pos).getReplyImg());

                                    if (!chats.get(pos).getReplyImg().isEmpty() && !chats.get(pos).getReplyImg().equals("null")) {

                                        Glide.with(context).load(chats.get(pos).getReplyImg()).thumbnail(0.01f).override(100, 100).placeholder(R.drawable.meetupsfellow_transpatent).into(holder.otherReplyImg);

                                        holder.selfReplyImgLay.setVisibility(View.GONE);
                                        holder.otherReplyImgLay.setVisibility(View.VISIBLE);
                                    } else {

                                        holder.selfReplyImgLay.setVisibility(View.GONE);
                                        holder.otherReplyImgLay.setVisibility(View.GONE);
                                    }

                                    holder.replyChatLayU.setVisibility(View.GONE);
                                    holder.replyChatLayO.setVisibility(View.VISIBLE);
                                    holder.replyChatMessageO.setText(chats.get(pos).getReplyMsg());

                                    if (chats.get(pos).getReplyTo().equals(Constants.INSTANCE.getCurrentUser())) {

                                        holder.userReplyNameO.setText("You");
                                    } else {

                                        holder.userReplyNameO.setText(chats.get(pos).getReplyTo());
                                    }

                                } else {

                                    holder.replyChatLayU.setVisibility(View.GONE);
                                    holder.replyChatLayO.setVisibility(View.GONE);
                                }
                                Log.e("ChatTime#", " othersChatTime_ELSS");
                                holder.selfChatLay.setVisibility(View.GONE);
                                holder.rlGroupNotice.setVisibility(View.GONE);
                                holder.othersChatTime.setText(dateTimeChat);
                                holder.othersName.setText(chats.get(pos).getUserName());
                                holder.chatIdO.setText(chats.get(pos).getChatId());
                                holder.othersChatLay.setVisibility(View.VISIBLE);
                                holder.othersChat.setText(chats.get(pos).getLastMsg());

                                holder.othersChat.post(new Runnable() {
                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    @Override
                                    public void run() {
                                        // Past the maximum number of lines we want to display.
                                        if (holder.othersChat.getLineCount() > MAX_LINES_COLLAPSED) {
                                            int lastCharShown = holder.othersChat.getLayout().getLineVisibleEnd(MAX_LINES_COLLAPSED - 1);

                                            holder.othersChat.setMaxLines(MAX_LINES_COLLAPSED);

                                            Log.e("Set_Text", "ReadMore_Visible_other  : " + chats.get(pos).getLastMsg());
                                            // String moreString = context.getString(R.string.more);
                                            String moreString = context.getString(R.string.read_more);
                                            String suffix = moreString;

                                            // 3 is a "magic number" but it's just basically the length of the ellipsis we're going to insert
                                            String actionDisplayText = chats.get(pos).getLastMsg().substring(0, lastCharShown - suffix.length() - 10) + "..." + suffix;

                                            SpannableString truncatedSpannableString = new SpannableString(actionDisplayText);
                                            int startIndex = actionDisplayText.indexOf(moreString);
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                truncatedSpannableString.setSpan(new ForegroundColorSpan(context.getColor(android.R.color.holo_blue_dark)), startIndex, startIndex + moreString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            }
                                            holder.othersChat.setText(truncatedSpannableString);
                                            holder.othersChat.setClickable(true);
                                        } else {
                                            holder.othersChat.setClickable(false);
                                        }
                                    }
                                });

                                if (chats.get(pos).getLastMsg().matches("(([A-Za-z]{3,9}:(?://)?)(?:[-;:&=+$,\\w]+@)?[A-Za-z0-9.-]+|(?:www\\.|[-;:&=+$,\\w]+@)[A-Za-z0-9.-]+)((?:/[+~%/.\\w-]*)?\\??(?:[-+=&;%@.\\w]*)#?(?:[.!/\\\\\\w]*))?") && !Patterns.EMAIL_ADDRESS.matcher(chats.get(pos).getLastMsg()).matches()) {

                                    holder.richLinkViewO.setText(chats.get(pos).getLastMsg());
                                    holder.richLinkViewO.setAutoLinkMask(View.FOCUSABLE);
                                  /*  holder.richLinkViewO.setLink(chats.get(pos).getLastMsg(), new ViewListener() {
                                        @Override
                                        public void onSuccess(boolean status) {

                                            Log.e("LinkPreview", String.valueOf(status));
                                            holder.richLinkViewO.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onError(Exception e) {

                                            Log.e("LinkPreview", String.valueOf(e));
                                            holder.richLinkViewO.setVisibility(View.GONE);
                                        }
                                    });*/
                                }

                                if (chats.get(pos).getLastMsg().length() == 2) {

                                    if (isEmoji(chats.get(pos).getLastMsg())) {

                                        holder.othersChat.setTextSize(50);
                                    }

                                    Matcher matchEmo = Pattern.compile(regex).matcher(chats.get(pos).getLastMsg());
                                    if (matchEmo.find()) {
                                        //System.out.println(matchEmo.group());
                                        Log.d("MsgIsEmoji", matchEmo.group());
                                        Log.d("Emoji Size", String.valueOf(chats.get(pos).getLastMsg().length()));
                                    }
                                }

                            }
                        } else {

                            holder.selfChatLay.setVisibility(View.GONE);
                            holder.othersChatLay.setVisibility(View.GONE);
                            holder.rlGroupNotice.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }

        // Set click listener to toggle expansion
        holder.othersChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Expand text
                holder.othersChat.setText(chats.get(pos).getLastMsg());
                holder.othersChat.setMaxLines(Integer.MAX_VALUE);
            }
        });
        // Set click listener to toggle expansion
        holder.selfChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Expand text
                holder.selfChat.setText(chats.get(pos).getLastMsg());
                holder.selfChat.setMaxLines(Integer.MAX_VALUE);
            }
        });

        holder.fileDownloadO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.fileProgressO.setVisibility(View.VISIBLE);
                holder.fileDownloadO.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    chatActivity.downloadAndSaveImage(chats.get(pos).getFileUrl(), chats.get(pos).getChatId(), 3);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        holder.fileProgressO.setVisibility(View.GONE);
                    }
                }, 20000);
            }
        });

        holder.flFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("isSelecting11", isSelecting.toString());

                if (selectedPos.contains(String.valueOf(pos))) {

                    selectedPos.remove(String.valueOf(pos));
                    chatActivity.chatsToDelete(chats.get(pos).getChatId());
                    chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatR, chats.get(pos).getChatId());
                    holder.flFile.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me));
                    Log.e("Copy_Delet: ", "1");
                    if (selectedPos.isEmpty()) {
                        isSelecting = false;
                    }

                } else {

                    if (isSelecting) {
                        Log.e("Copy_Delet: ", "2");
                        chatActivity.chatsToDelete(chats.get(pos).getChatId());
                        chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatR, chats.get(pos).getChatId());
                        selectedPos.add(String.valueOf(pos));
                        holder.flFile.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me_selected));
                    }
                }

            }
        });

        holder.flFile.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                holder.flFile.setClickable(false);
                holder.flFile.setEnabled(false);
                isSelecting = true;
                holder.flFile.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me_selected));
                selectedPos.add(String.valueOf(pos));
                chatActivity.chatsToDelete(chats.get(pos).getChatId());
                chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatR, chats.get(pos).getChatId());
                //holder.selfChatLay.setClickable(true);
                Log.e("Copy_Delet: ", "3");
                Log.e("isSelecting22", isSelecting.toString());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.flFile.setEnabled(true);

                    }
                }, 500);

                return false;
            }
        });

        holder.pdfPreviewImg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                holder.pdfPreviewImg.setClickable(false);
                holder.pdfPreviewImg.setEnabled(false);
                isSelecting = true;
                holder.flFile.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me_selected));
                selectedPos.add(String.valueOf(pos));
                chatActivity.chatsToDelete(chats.get(pos).getChatId());
                chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatR, chats.get(pos).getChatId());
                //holder.selfChatLay.setClickable(true);
                Log.e("Copy_Delet: ", "4");
                Log.e("isSelecting33", isSelecting.toString());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.pdfPreviewImg.setEnabled(true);

                    }
                }, 500);

                return false;
            }
        });

        holder.pdfPreviewImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedPos.contains(String.valueOf(pos))) {

                    selectedPos.remove(String.valueOf(pos));
                    chatActivity.chatsToDelete(chats.get(pos).getChatId());
                    chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatR, chats.get(pos).getChatId());
                    holder.flFile.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me));
                    Log.e("Copy_Delet: ", "5");
                    if (selectedPos.isEmpty()) {

                        isSelecting = false;
                    }

                } else {

                    if (isSelecting) {
                        Log.e("Copy_Delet: ", "6");
                        chatActivity.chatsToDelete(chats.get(pos).getChatId());
                        chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatR, chats.get(pos).getChatId());
                        selectedPos.add(String.valueOf(pos));
                        holder.flFile.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me_selected));
                    } else {

                        holder.fileBgIv.callOnClick();
                    }
                }
            }
        });

        holder.fileIconIv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                holder.fileIconIv.setClickable(false);
                holder.fileIconIv.setEnabled(false);
                isSelecting = true;
                holder.flFile.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me_selected));
                selectedPos.add(String.valueOf(pos));
                chatActivity.chatsToDelete(chats.get(pos).getChatId());
                chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatR, chats.get(pos).getChatId());
                //holder.selfChatLay.setClickable(true);
                Log.e("Copy_Delet: ", "7");
                Log.e("isSelecting44", isSelecting.toString());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.fileIconIv.setEnabled(true);

                    }
                }, 500);

                return false;
            }
        });

        holder.fileIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedPos.contains(String.valueOf(pos))) {
                    Log.e("Copy_Delet: ", "8");
                    selectedPos.remove(String.valueOf(pos));
                    chatActivity.chatsToDelete(chats.get(pos).getChatId());
                    chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatR, chats.get(pos).getChatId());
                    holder.flFile.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me));

                    if (selectedPos.isEmpty()) {

                        isSelecting = false;
                    }

                } else {

                    if (isSelecting) {
                        Log.e("Copy_Delet: ", "9");
                        chatActivity.chatsToDelete(chats.get(pos).getChatId());
                        chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatR, chats.get(pos).getChatId());
                        selectedPos.add(String.valueOf(pos));
                        holder.flFile.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me_selected));
                    } else {

                        holder.fileBgIv.callOnClick();
                    }
                }
            }
        });

        holder.fileNameTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                holder.fileNameTv.setClickable(false);
                holder.fileNameTv.setEnabled(false);
                isSelecting = true;
                holder.flFile.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me_selected));
                selectedPos.add(String.valueOf(pos));
                chatActivity.chatsToDelete(chats.get(pos).getChatId());
                chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatR, chats.get(pos).getChatId());
                //holder.selfChatLay.setClickable(true);
                Log.e("Copy_Delet: ", "10");
                Log.e("isSelecting66", isSelecting.toString());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.fileNameTv.setEnabled(true);

                    }
                }, 500);

                return false;
            }
        });

        holder.fileNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedPos.contains(String.valueOf(pos))) {
                    Log.e("Copy_Delet: ", "11");
                    selectedPos.remove(String.valueOf(pos));
                    chatActivity.chatsToDelete(chats.get(pos).getChatId());
                    chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatR, chats.get(pos).getChatId());
                    holder.flFile.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me));

                    if (selectedPos.isEmpty()) {

                        isSelecting = false;
                    }

                } else {

                    if (isSelecting) {
                        Log.e("Copy_Delet: ", "12");
                        chatActivity.chatsToDelete(chats.get(pos).getChatId());
                        chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatR, chats.get(pos).getChatId());
                        selectedPos.add(String.valueOf(pos));
                        holder.flFile.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me_selected));
                    } else {

                        holder.fileBgIv.callOnClick();
                    }
                }
            }
        });

        holder.fileBgIv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                holder.fileBgIv.setClickable(false);
                holder.fileBgIv.setEnabled(false);
                isSelecting = true;
                holder.flFile.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me_selected));
                selectedPos.add(String.valueOf(pos));
                chatActivity.chatsToDelete(chats.get(pos).getChatId());
                chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatR, chats.get(pos).getChatId());
                //holder.selfChatLay.setClickable(true);
                Log.e("Copy_Delet: ", "13");
                Log.d("isSelecting77", isSelecting.toString());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.fileBgIv.setEnabled(true);

                    }
                }, 500);

                return false;
            }
        });

        holder.fileBgIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("isSelecting88", isSelecting.toString());

                if (selectedPos.contains(String.valueOf(pos))) {
                    Log.e("Copy_Delet: ", "14");
                    selectedPos.remove(String.valueOf(pos));
                    chatActivity.chatsToDelete(chats.get(pos).getChatId());
                    chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatR, chats.get(pos).getChatId());
                    holder.flFile.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me));

                    if (selectedPos.isEmpty()) {

                        isSelecting = false;
                    }

                } else {

                    if (isSelecting) {
                        Log.e("Copy_Delet: ", "15");
                        chatActivity.chatsToDelete(chats.get(pos).getChatId());
                        chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatR, chats.get(pos).getChatId());
                        selectedPos.add(String.valueOf(pos));
                        holder.flFile.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me_selected));
                    } else {
                        Log.e("ExternalStorage", "firebase_path: "+ chats.get(pos).getImgPath());
                        MediaScannerConnection.scanFile(context, new String[]{chats.get(pos).getImgPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Log.d("ExternalStorage", "Scanned " + path + ":");
                                Log.d("ExternalStorage", "-> uri=" + uri);

                                //imgUri = uri;

                                String[] extArr = path.split("\\.");

                                Log.d("DocumentFile", extArr[extArr.length - 1]);
                                String ext = extArr[extArr.length - 1];

                                if (uri != null && !uri.equals("null")) {

                                   /* if (chats.get(pos).getLastMsg().contains(".pdf")) {

                                        String mimeType = getMimeType(".pdf");

                                        Log.d("MimeType", mimeType);

                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setDataAndType(uri, mimeType);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        context.startActivity(intent);

                                    }
                                    else if (chats.get(pos).getLastMsg().contains(".doc") || chats.get(pos).getLastMsg().contains(".docx")) {

                                        String mimeType = getMimeType(".docx");

                                        Log.d("MimeType", mimeType);

                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setDataAndType(uri, mimeType);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        context.startActivity(intent);

                                    }
                                    else if (chats.get(pos).getLastMsg().contains(".xls") || chats.get(pos).getLastMsg().contains(".xlsx")) {

                                        String mimeType = getMimeType(".xlsx");

                                        Log.d("MimeType", mimeType);

                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setDataAndType(uri, mimeType);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        context.startActivity(intent);

                                    }
                                    else if (chats.get(pos).getLastMsg().contains(".txt")) {

                                        String mimeType = getMimeType(".txt");

                                        Log.d("MimeType", mimeType);

                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setDataAndType(uri, mimeType);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        context.startActivity(intent);
                                    }
                                    else if (chats.get(pos).getLastMsg().contains(".ppt")) {

                                        String mimeType = getMimeType(".ppt");
                                        Log.d("MimeType", mimeType);
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setDataAndType(uri, mimeType);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        context.startActivity(intent);
                                    }*/
                                   /* try {
                                        String mimeType = getMimeType("." + ext);
                                        Log.e("MimeType", "first: " + mimeType);
                                        Log.e("Uri_Open", "Uri: " + uri);
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setDataAndType(uri, mimeType);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        context.startActivity(intent);
                                    } catch (Exception e) {
                                        Toast.makeText(chatActivity, "Please open this file in local folder", Toast.LENGTH_SHORT).show();
                                    }*/

                                    try {
                                        chatActivity.openFile(context, chats.get(pos).getImgPath());
                                    } catch (Exception e) {
                                        Log.e("FileOpenError1", "Error opening file: " + e.getMessage(), e);
                                        Toast.makeText(context, "Unable to open this file!", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    try {

                                        chatActivity.openFile(context, chats.get(pos).getImgPath());

                                      /*  File file = new File(chats.get(pos).getImgPath());
                                        if (!file.exists()) {
                                            Log.e("FileError", "File does not exist at path: " + file.getAbsolutePath());
                                            Toast.makeText(context, "File not found!", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
                                        Log.d("FileProvider", "Authority: " + context.getPackageName() + ".fileprovider");
                                        String mimeType = getMimeType(file.getName());
                                        Log.d("MimeType", "MIME_type2: " + mimeType);
                                        Log.d("Uri", "Generated_URI2: " + fileUri);

                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setDataAndType(fileUri, mimeType);
                                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        context.startActivity(intent);*/

                                    } catch (Exception e) {
                                        Log.e("FileOpenError2", "Error opening file: " + e.getMessage(), e);
                                        Toast.makeText(context, "Unable to open this file!", Toast.LENGTH_SHORT).show();
                                    }

/*
                                    try {
                                        String mimeType = getMimeType("." + ext);
                                        Log.e("MimeType", "first: " + mimeType);
                                        Log.e("Uri_Open22", "Uri: " + uri);
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setDataAndType(uri, mimeType);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        context.startActivity(intent);
                                    } catch (Exception e) {
                                        Toast.makeText(chatActivity, "Please open this file in local folder!", Toast.LENGTH_SHORT).show();
                                    }*/
                                }

                            }
                        });
                    }
                }
            }
        });


        holder.selfChatLay.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                holder.selfChatLay.setClickable(false);
                holder.selfChatLay.setEnabled(false);
                isSelecting = true;
                holder.selfChatLay.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me_selected));
                selectedPos.add(String.valueOf(pos));
                chatActivity.chatsToDelete(chats.get(pos).getChatId());
                chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatR, chats.get(pos).getChatId());
                //holder.selfChatLay.setClickable(true);
                Log.e("Copy_Delet: ", "16");
                Log.d("isSelecting99", isSelecting.toString());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.selfChatLay.setEnabled(true);

                    }
                }, 500);


                return false;
            }
        });

        holder.gifViewLay.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                holder.gifViewLay.setClickable(false);
                holder.gifViewLay.setEnabled(false);
                isSelecting = true;
                holder.gifViewLay.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me_selected));
                selectedPos.add(String.valueOf(pos));
                chatActivity.chatsToDelete(chats.get(pos).getChatId());
                chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatR, chats.get(pos).getChatId());
                //holder.selfChatLay.setClickable(true);
                Log.e("Copy_Delet: ", "17");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.gifViewLay.setEnabled(true);

                    }
                }, 500);

                return false;
            }
        });

        holder.gifViewLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedPos.contains(String.valueOf(pos))) {
                    Log.e("Copy_Delet: ", "18");
                    selectedPos.remove(String.valueOf(pos));
                    chatActivity.chatsToDelete(chats.get(pos).getChatId());
                    chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatR, chats.get(pos).getChatId());
                    holder.gifViewLay.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me));

                    if (selectedPos.isEmpty()) {

                        isSelecting = false;
                    }

                } else {

                    if (isSelecting) {
                        Log.e("Copy_Delet: ", "19");
                        chatActivity.chatsToDelete(chats.get(pos).getChatId());
                        chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatR, chats.get(pos).getChatId());
                        selectedPos.add(String.valueOf(pos));
                        holder.gifViewLay.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me_selected));
                    } else {

                        /*if (!chats.get(pos).getImgPath().equals("null") && !chats.get(pos).getImgPath().isEmpty()) {

                            Intent intent = new Intent(context, VideoPlayerActivity.class);
                            intent.putExtra(Constants.IntentDataKeys.ImgPath, chats.get(pos).getImgPath());
                            context.startActivity(intent);

                        } else if (!chats.get(pos).getVideoUrl().equals("null") && !chats.get(pos).getVideoUrl().isEmpty()){

                            Intent intent = new Intent(context, VideoPlayerActivity.class);
                            intent.putExtra(Constants.IntentDataKeys.VideoUrl, chats.get(pos).getVideoUrl());
                            context.startActivity(intent);
                        }*/
                    }
                }
            }
        });

        holder.gifViewLayO.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                holder.gifViewLayO.setClickable(false);
                holder.gifViewLayO.setEnabled(false);
                isSelecting = true;
                holder.gifViewLayO.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me_selected));
                selectedPos.add(String.valueOf(pos));
                chatActivity.chatsToDelete(chats.get(pos).getChatId());
                chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatR, chats.get(pos).getChatId());
                //holder.selfChatLay.setClickable(true);
                Log.e("Copy_Delet: ", "Other_17");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.gifViewLayO.setEnabled(true);

                    }
                }, 500);

                return false;
            }
        });

        holder.gifViewLayO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPos.contains(String.valueOf(pos))) {
                    Log.e("Copy_Delet: ", "other_18");
                    selectedPos.remove(String.valueOf(pos));
                    chatActivity.chatsToDelete(chats.get(pos).getChatId());
                    chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatR, chats.get(pos).getChatId());
                    holder.gifViewLayO.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me));

                    if (selectedPos.isEmpty()) {

                        isSelecting = false;
                    }

                } else {

                    if (isSelecting) {
                        Log.e("Copy_Delet: ", "19");
                        chatActivity.chatsToDelete(chats.get(pos).getChatId());
                        chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatR, chats.get(pos).getChatId());
                        selectedPos.add(String.valueOf(pos));
                        holder.gifViewLay.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me_selected));
                    } else {

                        /*if (!chats.get(pos).getImgPath().equals("null") && !chats.get(pos).getImgPath().isEmpty()) {

                            Intent intent = new Intent(context, VideoPlayerActivity.class);
                            intent.putExtra(Constants.IntentDataKeys.ImgPath, chats.get(pos).getImgPath());
                            context.startActivity(intent);

                        } else if (!chats.get(pos).getVideoUrl().equals("null") && !chats.get(pos).getVideoUrl().isEmpty()){

                            Intent intent = new Intent(context, VideoPlayerActivity.class);
                            intent.putExtra(Constants.IntentDataKeys.VideoUrl, chats.get(pos).getVideoUrl());
                            context.startActivity(intent);
                        }*/
                    }
                }
            }
        });

        holder.selfChatLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("isSelecting111", isSelecting.toString());

                if (selectedPos.contains(String.valueOf(pos))) {
                    Log.e("Copy_Delet: ", "20");
                    selectedPos.remove(String.valueOf(pos));
                    chatActivity.chatsToDelete(chats.get(pos).getChatId());
                    chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatR, chats.get(pos).getChatId());
                    holder.selfChatLay.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me));

                    if (selectedPos.isEmpty()) {

                        isSelecting = false;
                    }

                } else {

                    if (isSelecting) {
                        Log.e("Copy_Delet: ", "21");
                        chatActivity.chatsToDelete(chats.get(pos).getChatId());
                        chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatR, chats.get(pos).getChatId());
                        selectedPos.add(String.valueOf(pos));
                        holder.selfChatLay.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me_selected));
                    } else {

                        if (!chats.get(pos).getImgPath().equals("null") && !chats.get(pos).getImgPath().isEmpty()) {

                            Intent intent = new Intent(context, VideoPlayerActivity.class);
                            intent.putExtra(Constants.IntentDataKeys.ImgPath, chats.get(pos).getImgPath());
                            context.startActivity(intent);

                        } else if (!chats.get(pos).getVideoUrl().equals("null") && !chats.get(pos).getVideoUrl().isEmpty()) {

                            Intent intent = new Intent(context, VideoPlayerActivity.class);
                            intent.putExtra(Constants.IntentDataKeys.VideoUrl, chats.get(pos).getVideoUrl());
                            context.startActivity(intent);
                        }
                    }
                }
            }
        });

        holder.othersChatLay.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                holder.othersChatLay.setClickable(false);
                holder.othersChatLay.setEnabled(false);
                isSelecting = true;
                holder.othersChatLay.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_other_selected));
                selectedPos.add(String.valueOf(pos));
                chatActivity.chatsToDelete(chats.get(pos).getChatId());
                chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatR, chats.get(pos).getChatId());
                Log.d("isSelecting12", isSelecting.toString());
                Log.e("Copy_Delet: ", "222");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.othersChatLay.setEnabled(true);
                    }
                }, 500);

                return false;
            }
        });

        holder.othersChatLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("isSelecting13", isSelecting.toString());

               /* if (!chats.get(pos).getUserImg().equals("null") && !chats.get(pos).getUserImg().isEmpty()) {

                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra(Constants.IntentDataKeys.VideoUrl, chats.get(pos).getUserImg());
                    context.startActivity(intent);
                }*/

                if (selectedPos.contains(String.valueOf(pos))) {
                    Log.e("Copy_Delet: ", "Second_last");
                    selectedPos.remove(String.valueOf(pos));
                    chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatR, chats.get(pos).getChatId());
                    chatActivity.chatsToDelete(chats.get(pos).getChatId());
                    holder.othersChatLay.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_other));

                    if (selectedPos.isEmpty()) {

                        isSelecting = false;
                    }

                } else {
                    Log.e("isSelecting13", "first__Elss");
                   /* if (!chats.get(pos).getUserImg().equals("null") && !chats.get(pos).getUserImg().isEmpty()) {

                        Intent intent = new Intent(context, VideoPlayerActivity.class);
                        intent.putExtra(Constants.IntentDataKeys.VideoUrl, chats.get(pos).getUserImg());
                        context.startActivity(intent);
                    }*/

                    if (isSelecting) {
                        Log.e("Copy_Delet: ", "last");
                        selectedPos.add(String.valueOf(pos));
                        chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatR, chats.get(pos).getChatId());
                        chatActivity.chatsToDelete(chats.get(pos).getChatId());
                        holder.othersChatLay.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_other_selected));
                    } else {

                        if (!chats.get(pos).getImgPath().equals("null") && !chats.get(pos).getImgPath().isEmpty()) {

                            if (chats.get(pos).getImgPath().contains(".mp4")) {
                                Log.e("isSelecting13", "second__IFF: " + chats.get(pos).getImgPath());
                                Intent intent = new Intent(context, VideoPlayerActivity.class);
                                intent.putExtra(Constants.IntentDataKeys.ImgPath, chats.get(pos).getImgPath());
                                context.startActivity(intent);
                            } else if (chats.get(pos).getImgPath().contains(".png") || chats.get(pos).getImgPath().contains(".jpg") || chats.get(pos).getImgPath().contains(".jpeg")) {
                                Log.e("isSelecting13", "Els_Iff: " + chats.get(pos).getImgPath());
                                Intent intent = new Intent(context, VideoPlayerActivity.class);
                                intent.putExtra(Constants.IntentDataKeys.ImgPath, chats.get(pos).getImgPath());
                                context.startActivity(intent);
                            } else {
                                Log.e("isSelecting13", "second__Elss^&: " + chats.get(pos).getImgPath());
                                MediaScannerConnection.scanFile(context, new String[]{chats.get(pos).getImgPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                                    public void onScanCompleted(String path, Uri uri) {
                                        Log.d("ExternalStorage", "Scanned " + path + ":");
                                        Log.d("ExternalStorage", "-> uri=" + uri);

                                        //imgUri = uri;

                                        String[] extArr = path.split("\\.");

                                        String ext = extArr[extArr.length - 1];
                                        Log.e("DocumentFile_EXT: ", ext);

                                        if (uri != null && !uri.equals("null")) {

                                            /*if (ext.equals("pdf")) {

                                                String mimeType = getMimeType(".pdf");

                                                Log.d("MimeType", mimeType);

                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setDataAndType(uri, mimeType);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                context.startActivity(intent);

                                            }
                                            else if (ext.equals("doc") || ext.equals("docx")) {

                                                String mimeType = getMimeType(".docx");

                                                Log.d("MimeType", mimeType);

                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setDataAndType(uri, mimeType);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                context.startActivity(intent);

                                            }
                                            else if (ext.equals("xls") || ext.equals("xlsx")) {

                                                String mimeType = getMimeType(".xlsx");

                                                Log.d("MimeType", mimeType);

                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setDataAndType(uri, mimeType);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                context.startActivity(intent);

                                            }
                                            else if (ext.equals("txt")) {

                                                String mimeType = getMimeType(".txt");

                                                Log.e("MimeType", mimeType);

                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setDataAndType(uri, mimeType);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                context.startActivity(intent);
                                            }
                                            else if (ext.equals("ppt")) {

                                                String mimeType = getMimeType(".ppt");

                                                Log.e("MimeType", mimeType);

                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setDataAndType(uri, mimeType);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                context.startActivity(intent);
                                            }*/
                                          /*  try {
                                                String mimeType = getMimeType("." + ext);
                                                Log.e("MimeType", "therd: " + mimeType);
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setDataAndType(uri, mimeType);
                                                Log.e("Uri_Open", "Uri33 : " + uri);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                context.startActivity(intent);
                                            } catch (Exception e) {
                                                Toast.makeText(chatActivity, "Please open this file in local folder", Toast.LENGTH_SHORT).show();
                                            }*/
                                            try {
                                                chatActivity.openFile(context, chats.get(pos).getImgPath());
                                            } catch (Exception e) {
                                                Log.e("FileOpenError3", "Error opening file: " + e.getMessage(), e);
                                                Toast.makeText(context, "Unable to open this file!", Toast.LENGTH_SHORT).show();
                                            }

                                        } else {
                                           /* try {
                                                String mimeType = getMimeType("." + ext);
                                                Log.e("MimeType", "forth: " + mimeType);
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setDataAndType(uri, mimeType);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                context.startActivity(intent);
                                            } catch (Exception e) {
                                                Toast.makeText(chatActivity, "Please open this file in local folder!", Toast.LENGTH_SHORT).show();
                                            }*/
                                            try {
                                                chatActivity.openFile(context, chats.get(pos).getImgPath());
                                            } catch (Exception e) {
                                                Log.e("FileOpenError4", "Error opening file: " + e.getMessage(), e);
                                                Toast.makeText(context, "Unable to open this file!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });

                            }

                        } else if (!chats.get(pos).getVideoUrl().equals("null") && !chats.get(pos).getVideoUrl().isEmpty()) {
                            Log.e("isSelecting13", "secound__Elss");
                            Intent intent = new Intent(context, VideoPlayerActivity.class);
                            intent.putExtra(Constants.IntentDataKeys.VideoUrl, chats.get(pos).getVideoUrl());
                            context.startActivity(intent);
                        }
                    }
                }
            }
        });

        holder.replyChatLayU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Collections.reverse(holderArr);
                Log.e("ScrollIN", "ID11: " + chats.get(pos).getReplyId());
                chatActivity.scrollToMsg(chats.get(pos).getReplyId());
                //mRecyclerView.findViewHolderForAdapterPosition(msgPos).itemView.setBackgroundColor(Color.GRAY);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        for (int i = 0; i < chats.size(); i++) {
                            Log.e("replyMsgID", "chatID: " + chats.get(i).getChatId());
                            if (chats.get(i).getChatId().equals(chats.get(pos).getReplyId())) {

                                Log.e("holderArr_Size!", String.valueOf(holderArr.size()));

                                for (int j = 0; j < holderArr.size(); j++) {
                                    Log.e("replyMsgID!!", "chatID: " + holderArr.get(j).chatId.getText());
                                    Log.d("replyMsgID!!!2", "replayID: " + chats.get(pos).getReplyId());
                                    Log.e("replyMsgID!!!3", "chatID0: " + holderArr.get(j).chatIdO.getText());
                                    if (holderArr.get(j).chatId.getText().equals(chats.get(pos).getReplyId()) || holderArr.get(j).chatIdO.getText().equals(chats.get(pos).getReplyId())) {
                                        showBackgroundColor(holderArr.get(j).chatMsgLay);
                                        // holderArr.get(j).chatMsgLay.setBackgroundColor(Color.GRAY);
                                        Log.e("replyMsgID@", "Color_Gray1");
                                        int finalJ = j;
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                hideBackgroundColor(holderArr.get(finalJ).chatMsgLay);
                                                //  holderArr.get(finalJ).chatMsgLay.setBackgroundColor(Color.TRANSPARENT);
                                                Log.e("replyMsgID!", "Color_TRANSPARENT1");
                                            }
                                        }, 1300);
                                    }
                                }
                            }
                        }
                    }
                }, 200);


                //mRecyclerView.getChildAt(chats.size() - msgPos).findViewById(R.id.chatMsgLay).setBackgroundColor(Color.GRAY);
            }
        });

        holder.replyChatLayO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ScrollIN", "ID22: " + chats.get(pos).getReplyId());
                chatActivity.scrollToMsg(chats.get(pos).getReplyId());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        for (int i = 0; i < chats.size(); i++) {

                            if (chats.get(i).getChatId().equals(chats.get(pos).getReplyId())) {

                                Log.d("replyMsgID", String.valueOf(i));

                                for (int j = 0; j < holderArr.size(); j++) {
                                    Log.d("replyMsgID%", "replayID: " + chats.get(pos).getReplyId());
                                    Log.e("replyMsgID%%", "Id: " + holderArr.get(j).chatId.getText());
                                    Log.e("replyMsgID%%%", "Id0: " + holderArr.get(j).chatIdO.getText());
                                    if (holderArr.get(j).chatId.getText().equals(chats.get(pos).getReplyId()) || holderArr.get(j).chatIdO.getText().equals(chats.get(pos).getReplyId())) {
                                        showBackgroundColor(holderArr.get(j).chatMsgLay);
                                        //  holderArr.get(j).chatMsgLay.setBackgroundColor(Color.GRAY);
                                        Log.e("replyMsgID!", "Color_Gray2");
                                        int finalJ = j;
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                hideBackgroundColor(holderArr.get(finalJ).chatMsgLay);
                                                //    holderArr.get(finalJ).chatMsgLay.setBackgroundColor(Color.TRANSPARENT);
                                                Log.e("replyMsgID!", "Color_TRANSPARENT2");
                                            }
                                        }, 1300);

                                    }
                                }
                            }
                        }

                    }
                }, 200);
            }
        });

        holder.downloadImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.downloadProgress.setVisibility(View.VISIBLE);
                holder.downloadImgBtn.setVisibility(View.GONE);
                Log.e("download*&", "IMG-Sen: " + chats.get(pos).getUserImg());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    chatActivity.downloadAndSaveImage(chats.get(pos).getUserImg(), chats.get(pos).getChatId(), 2);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        holder.downloadProgress.setVisibility(View.GONE);
                    }
                }, 2000);
            }
        });

        holder.downloadImgBtnO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.downloadProgressO.setVisibility(View.VISIBLE);
                holder.downloadImgBtnO.setVisibility(View.GONE);
                Log.e("download*&", "IMGRec: " + chats.get(pos).getUserImg());
                chatActivity.downloadAndSaveImage(chats.get(pos).getUserImg(), chats.get(pos).getChatId(), 2);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.downloadProgressO.setVisibility(View.GONE);
                    }
                }, 2000);
            }
        });

        holder.ivChatVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Adapter", "personal_VideoSe: " + chats.get(pos).getImgPath());
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra(Constants.IntentDataKeys.VideoUrl, chats.get(pos).getImgPath());
                context.startActivity(intent);

               /* holder.downloadProgress.setVisibility(View.VISIBLE);
                holder.downloadImgBtn.setVisibility(View.GONE);
                chatActivity.downloadAndSaveImage(chats.get(pos).getVideoUrl(), chats.get(pos).getChatId());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        holder.downloadProgress.setVisibility(View.GONE);
                    }
                }, 2000);*/

            }
        });

        holder.ivChatVideoBtnO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Adapter", "personal_VideoRe: " + chats.get(pos).getImgPath());
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra(Constants.IntentDataKeys.VideoUrl, chats.get(pos).getImgPath());
                context.startActivity(intent);

               /* holder.downloadProgressO.setVisibility(View.VISIBLE);
                holder.downloadImgBtnO.setVisibility(View.GONE);
                chatActivity.downloadAndSaveImage(chats.get(pos).getVideoUrl(),chats.get(pos).getChatId());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        holder.downloadProgressO.setVisibility(View.GONE);
                    }
                }, 2000);*/

            }
        });

        holder.videoDownloadBtnO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("UrlTAG", "video_Url: " + chats.get(pos).getVideoUrl());
                Log.e("UrlTAG", "Image_Url: " + chats.get(pos).getUserImg());
                Log.e("UrlTAG", "ImagePath_Url: " + chats.get(pos).getImgPath());
                holder.downloadProgressO.setVisibility(View.VISIBLE);
                holder.downloadImgBtnO.setVisibility(View.GONE);
                chatActivity.downloadAndSaveImage(chats.get(pos).getVideoUrl(), chats.get(pos).getChatId(), 4);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.videoDownloadBtnO.setVisibility(View.GONE);
                        holder.downloadProgressO.setVisibility(View.GONE);
                    }
                }, 2000);

            }
        });

        holder.videoDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("UrlTAG", "video_Url: " + chats.get(pos).getVideoUrl());
                Log.e("UrlTAG", "Image_Url: " + chats.get(pos).getUserImg());
                Log.e("UrlTAG", "ImagePath_Url: " + chats.get(pos).getImgPath());
                if (chats.get(pos).getImgPath().equals("") || chats.get(pos).getImgPath() == null) {

                    holder.downloadProgress.setVisibility(View.VISIBLE);
                    holder.downloadImgBtn.setVisibility(View.GONE);
                    chatActivity.downloadAndSaveImage(chats.get(pos).getVideoUrl(), chats.get(pos).getChatId(), 4);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            holder.downloadProgress.setVisibility(View.GONE);
                            holder.videoDownloadBtn.setVisibility(View.GONE);
                        }
                    }, 2000);

                } else {
                    Log.e("UrlTAG", "Self_Video_Download@@#");
                }
            }
        });

        holder.swipeRepLay.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {

                Log.e("swipeChat", "Type: " + chats.get(pos).getChatId());

                if (!chats.get(pos).getUserImg().isEmpty() && !chats.get(pos).getUserImg().equals("null")) {

                    if (!chats.get(pos).getImgPath().isEmpty() && !chats.get(pos).getImgPath().equals("null")) {

                        if (chats.get(pos).getUserId().equals(currentUserId)) {
                            Log.e("swipeChat", "imageIff11");
                            chatActivity.replyToChat("You", "image", dateTimeChatR, chats.get(pos).getChatId(), chats.get(pos).getThumbImg(), chats.get(pos).getImgPath());
                        } else {
                            Log.e("swipeChat", "imageElsss22");
                            chatActivity.replyToChat(chats.get(pos).getUserName(), "image", dateTimeChatR, chats.get(pos).getChatId(), chats.get(pos).getThumbImg(), chats.get(pos).getImgPath());
                        }
                    } else {

                        if (chats.get(pos).getUserId().equals(currentUserId)) {
                            Log.e("swipeChat", "imageIff_Below");
                            chatActivity.replyToChat("You", "image", dateTimeChatR, chats.get(pos).getChatId(), chats.get(pos).getThumbImg(), "");
                        } else {
                            Log.e("swipeChat", "imageElss_Below");
                            chatActivity.replyToChat(chats.get(pos).getUserName(), "image", dateTimeChatR, chats.get(pos).getChatId(), chats.get(pos).getThumbImg(), "");
                        }

                    }

                } else if (!chats.get(pos).getGifId().isEmpty() && !chats.get(pos).getGifId().equals("null")) {
                    if (chats.get(pos).getUserId().equals(currentUserId)) {
                        Log.e("swipeChat", "Gif_Iff: " + chats.get(pos).getChatId());
                        chatActivity.replyToChat("You", "GIF", dateTimeChatR, chats.get(pos).getChatId(), chats.get(pos).getGifId(), chats.get(pos).getImgPath());
                    } else {
                        Log.e("swipeChat", "Gif_Elss");
                        chatActivity.replyToChat(chats.get(pos).getUserName(), "GIF", dateTimeChatR, chats.get(pos).getChatId(), chats.get(pos).getGifId(), chats.get(pos).getImgPath());
                    }
                } else {
                    Log.e("swipeChat", "VideoIff");
                    if (!chats.get(pos).getVideoUrl().isEmpty() && !chats.get(pos).getVideoUrl().equals("null")) {

                        if (chats.get(pos).getUserId().equals(currentUserId)) {

                            chatActivity.replyToChat("You", "video", dateTimeChatR, chats.get(pos).getChatId(), chats.get(pos).getThumbImg(), "");
                        } else {

                            chatActivity.replyToChat(chats.get(pos).getUserName(), "video", dateTimeChatR, chats.get(pos).getChatId(), chats.get(pos).getThumbImg(), "");
                        }

                    } else {
                        Log.e("swipeChat", "VideoElss");
                        if (chats.get(pos).getUserId().equals(currentUserId)) {
                            chatActivity.replyToChat("You", chats.get(pos).getLastMsg(), dateTimeChatR, chats.get(pos).getChatId(), chats.get(pos).getThumbImg(), "");
                        } else {
                            chatActivity.replyToChat(chats.get(pos).getUserName(), chats.get(pos).getLastMsg(), dateTimeChatR, chats.get(pos).getChatId(), chats.get(pos).getThumbImg(), "");
                        }
                    }
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.swipeRepLay.close(true);

                    }
                }, 500);
            }

            @Override
            public void onOpen(SwipeLayout layout) {
                holder.swipeRepLay.close(true);
                Log.d("swipeChat", "Open");
            }

            @Override
            public void onStartClose(SwipeLayout layout) {
                Log.d("swipeChat", "startClose");
            }

            @Override
            public void onClose(SwipeLayout layout) {
                Log.d("swipeChat", "Close");
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                Log.d("swipeChat", "update");
            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                Log.d("swipeChat", "release");
            }
        });

        holder.swipeRepLay.setClickToClose(true);

    }

    private void hideBackgroundColor(RelativeLayout chatMsgLay) {
        TransitionDrawable transition = new TransitionDrawable(new Drawable[]{new ColorDrawable(Color.TRANSPARENT), new ColorDrawable(Color.TRANSPARENT)});
        chatMsgLay.setBackground(transition);
        transition.startTransition(500); // Adjust the duration as needed
    }

    private void showBackgroundColor(RelativeLayout chatMsgLay) {
        TransitionDrawable transition = new TransitionDrawable(new Drawable[]{new ColorDrawable(Color.TRANSPARENT), new ColorDrawable(Color.GRAY)});
        chatMsgLay.setBackground(transition);
        transition.startTransition(500); // Adjust the duration as needed
    }

    public String getMimeType(String fileName) {
        // Let's check the official map first. Webkit has a nice extension-to-MIME map.
        // Be sure to remove the first character from the extension, which is the "." character.

      /*  if (extension.length() > 0) {
            String webkitMimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1));
            if (webkitMimeType != null) return webkitMimeType;
        }*/

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        //return "*/*";
    }

    @Override
    public int getItemCount() {
        //Number of Items you want to display
        return chats.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //grabbing the views from rv_column.xml

        TextView selfChat, othersChat, selfChatTime, othersChatTime, othersName, groupNotice, userNameChatSelf, userNameChatOther;
        TextView userReplyName, userReplyNameO, replyChatMessage, replyChatMessageO, chatId, chatIdO, chatImgDateO, chatImgDate, userNameChatVideo1, chatVideoDateO, chatVideoDate, userNameChatVideo, tvTimeGif, tvTimeGifO, userNameChatGifO, fileNameTv, fileSizeTv, chatFileDate, fileNameTvO, fileSizeTvO, chatFileDateO;
        RelativeLayout selfChatLay, othersChatLay, rlGroupNotice, chatMsgLay, gifViewLay, gifViewLayO;
        CardView replyChatLayU, replyChatLayO;
        ImageView ivChatStatus, otherReplyImg, selfReplyImg, downloadImgBtnO, downloadImgBtn, ivChatStatusImg, ivChatStatusVideo, ivChatVideoBtn, ivChatVideoBtnO, ivStatusGif, videoDownloadBtnO, videoDownloadBtn, fileIconIv, ivChatStatusFile, fileIconIvO, fileDownload, fileDownloadO;
        ShapeableImageView ivChatImageBlur, ivChatImage, ivChatImageBlurO, ivChatImageO, videoBlurImgO, videoBlurImg, pdfPreviewImg, fileBgIv, pdfPreviewImgO, fileBgIvO, ivChatVideoPlay, ivChatVideoPlayO;
        ProgressBar downloadProgress, downloadProgressO, fileProgressO;
        FrameLayout flImage, flImageO, flPlayO, flPlay, flFileO, flFile;
        SwipeLayout swipeRepLay;
        LinearLayout selfReplyImgLay, otherReplyImgLay;
        GPHMediaView gifView, gifViewO;
      ///  RichLinkView richLinkView, richLinkViewO;
        TextView richLinkView, richLinkViewO;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            selfChat = itemView.findViewById(R.id.tvChatMessage);
            selfChatTime = itemView.findViewById(R.id.tvChatTime);
            othersChat = itemView.findViewById(R.id.tvChatMessageO);
            othersChatTime = itemView.findViewById(R.id.tvChatTimeO);
            othersName = itemView.findViewById(R.id.userNameChat1);
            selfChatLay = itemView.findViewById(R.id.rlChat);
            othersChatLay = itemView.findViewById(R.id.rlChatO);
            ivChatStatus = itemView.findViewById(R.id.ivChatStatus);
            rlGroupNotice = itemView.findViewById(R.id.rlGroupNotice);
            groupNotice = itemView.findViewById(R.id.groupNotice);
            flImage = itemView.findViewById(R.id.flImage);
            flImageO = itemView.findViewById(R.id.flImageO);
            userNameChatSelf = itemView.findViewById(R.id.userNameChatImg);
            userNameChatOther = itemView.findViewById(R.id.userNameChatImg1);
            ivChatImage = itemView.findViewById(R.id.ivChatImage);
            ivChatImageO = itemView.findViewById(R.id.ivChatImageO);
            swipeRepLay = itemView.findViewById(R.id.swipeRepLay);
            replyChatLayU = itemView.findViewById(R.id.replyChatLayU);
            replyChatLayO = itemView.findViewById(R.id.replyChatLayO);
            userReplyName = itemView.findViewById(R.id.userReplyNameU);
            userReplyNameO = itemView.findViewById(R.id.userReplyNameO);
            replyChatMessage = itemView.findViewById(R.id.replyChatMessageU);
            replyChatMessageO = itemView.findViewById(R.id.replyChatMessageO);
            chatMsgLay = itemView.findViewById(R.id.chatMsgLay);
            chatId = itemView.findViewById(R.id.chatId);
            chatIdO = itemView.findViewById(R.id.chatIdO);
            otherReplyImg = itemView.findViewById(R.id.otherReplyImg);
            selfReplyImg = itemView.findViewById(R.id.selfReplyImg);
            otherReplyImgLay = itemView.findViewById(R.id.otherReplayImgLay);
            selfReplyImgLay = itemView.findViewById(R.id.selfReplayImgLay);
            downloadProgress = itemView.findViewById(R.id.downloadProgress);
            downloadProgressO = itemView.findViewById(R.id.downloadProgressO);
            downloadImgBtn = itemView.findViewById(R.id.downloadImgBtn);
            downloadImgBtnO = itemView.findViewById(R.id.downloadImgBtnO);
            chatImgDateO = itemView.findViewById(R.id.chatImgDateO);
            chatImgDate = itemView.findViewById(R.id.chatImgDate);
            ivChatStatusImg = itemView.findViewById(R.id.ivChatStatusImg);
            ivChatImageBlurO = itemView.findViewById(R.id.ivChatImageBlurO);
            ivChatImageBlur = itemView.findViewById(R.id.ivChatImageBlur);
            flPlayO = itemView.findViewById(R.id.flPlayO);
            flPlay = itemView.findViewById(R.id.flPlay);
            ivChatVideoPlayO = itemView.findViewById(R.id.ivChatVideoPlayO);
            ivChatVideoPlay = itemView.findViewById(R.id.ivChatVideoPlay);
            userNameChatVideo1 = itemView.findViewById(R.id.userNameChatVideo1);
            chatVideoDateO = itemView.findViewById(R.id.chatVideoDateO);
            chatVideoDate = itemView.findViewById(R.id.chatVideoDate);
            userNameChatVideo = itemView.findViewById(R.id.userNameChatVideo);
            ivChatStatusVideo = itemView.findViewById(R.id.ivChatStatusVideo);
            ivChatVideoBtn = itemView.findViewById(R.id.ivChatVideoBtn);
            ivChatVideoBtnO = itemView.findViewById(R.id.ivChatVideoBtnO);
            gifViewLay = itemView.findViewById(R.id.gifViewLay);
            gifView = itemView.findViewById(R.id.gifView);
            gifViewLayO = itemView.findViewById(R.id.gifViewLayO);
            gifViewO = itemView.findViewById(R.id.gifViewO);
            ivStatusGif = itemView.findViewById(R.id.ivStatusGif);
            tvTimeGif = itemView.findViewById(R.id.tvTimeGif);
            tvTimeGifO = itemView.findViewById(R.id.tvTimeGifO);
            userNameChatGifO = itemView.findViewById(R.id.userNameChatGifO);
            richLinkView = itemView.findViewById(R.id.richLinkView);
            richLinkViewO = itemView.findViewById(R.id.richLinkViewO);
            videoDownloadBtnO = itemView.findViewById(R.id.videoDownloadBtnO);
            videoDownloadBtn = itemView.findViewById(R.id.videoDownloadBtn);
            videoBlurImgO = itemView.findViewById(R.id.videoBlurImgO);
            videoBlurImg = itemView.findViewById(R.id.videoBlurImg);
            pdfPreviewImg = itemView.findViewById(R.id.pdfPreviewImg);
            fileIconIv = itemView.findViewById(R.id.fileIconIv);
            fileNameTv = itemView.findViewById(R.id.fileNameTv);
            fileSizeTv = itemView.findViewById(R.id.fileSizeTv);
            flFile = itemView.findViewById(R.id.flFile);
            fileBgIv = itemView.findViewById(R.id.fileBgIv);
            ivChatStatusFile = itemView.findViewById(R.id.ivChatStatusFile);
            chatFileDate = itemView.findViewById(R.id.chatFileDate);
            pdfPreviewImgO = itemView.findViewById(R.id.pdfPreviewImgO);
            fileBgIvO = itemView.findViewById(R.id.fileBgIvO);
            fileIconIvO = itemView.findViewById(R.id.fileIconIvO);
            fileDownload = itemView.findViewById(R.id.fileDownload);
            fileDownloadO = itemView.findViewById(R.id.fileDownloadO);
            fileNameTvO = itemView.findViewById(R.id.fileNameTvO);
            fileSizeTvO = itemView.findViewById(R.id.fileSizeTvO);
            chatFileDateO = itemView.findViewById(R.id.chatFileDateO);
            flFileO = itemView.findViewById(R.id.flFileO);
            fileProgressO = itemView.findViewById(R.id.fileProgressO);

        }
    }
}
