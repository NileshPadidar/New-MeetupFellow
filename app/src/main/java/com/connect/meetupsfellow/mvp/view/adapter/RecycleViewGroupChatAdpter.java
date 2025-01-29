package com.connect.meetupsfellow.mvp.view.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daimajia.swipe.SwipeLayout;
import com.giphy.sdk.core.GPHCore;
import com.giphy.sdk.core.models.enums.RenditionType;
import com.giphy.sdk.core.network.response.MediaResponse;
import com.giphy.sdk.ui.views.GPHMediaView;
import com.connect.meetupsfellow.R;
import com.connect.meetupsfellow.constants.Constants;
import com.connect.meetupsfellow.mvp.view.activities.GroupChatActivity;
import com.connect.meetupsfellow.mvp.view.activities.VideoPlayerActivity;
import com.connect.meetupsfellow.mvp.view.model.RecycleModel;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

///import io.github.ponnamkarthik.richlinkpreview.RichLinkView;
///import io.github.ponnamkarthik.richlinkpreview.ViewListener;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class RecycleViewGroupChatAdpter extends RecyclerView.Adapter<RecycleViewGroupChatAdpter.MyViewHolder> implements Filterable {

    public ArrayList<Boolean> clickedItems = new ArrayList<>();
    Context context;
    ArrayList<RecycleModel> chats;
    public ArrayList<String> userIds = new ArrayList<>();
    List<RecycleModel> chatsAll;
    public ArrayList<String> selectedPos = new ArrayList<>();
    Boolean isSelecting = false;
    ArrayList<RecycleViewGroupChatAdpter.MyViewHolder> holderArr = new ArrayList<>();
    String userId;
    String dateTimeChat;

    public RecycleViewGroupChatAdpter(Context context) {
        this.context = context;
        //this.chats = chats;
        //this.chatsAll = new ArrayList<>(chats);
        holderArr.clear();

    }

    public void addChats(ArrayList<RecycleModel> chats) {

        this.chats = chats;
        Log.e("Personal_Adap", "addChats: "+this.chats.size());
    }

    public void updateChats(RecycleModel chat) {

        this.chats.set(chats.size()-1, chat);
        //notifyItemChanged(chats.size()-1);
        Log.e("Personal_Adap", "updateChats: "+this.chats.size());
    }

    public void removeChats(int pos) {

        this.chats.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(0, chats.size());
    }

    @NonNull
    @Override
    public RecycleViewGroupChatAdpter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout (Giving look)
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_new_replay_chat, parent, false);
        return new RecycleViewGroupChatAdpter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewGroupChatAdpter.MyViewHolder holder, int position) {

        int pos = position;
        holder.setIsRecyclable(false);
        holderArr.add(holder);
        String currentUserId = context.getSharedPreferences("UserId", MODE_PRIVATE).getString("UserId", "");

        GroupChatActivity chatActivity = (GroupChatActivity) context;

        if (!selectedPos.isEmpty()) {

            if (selectedPos.contains(String.valueOf(pos))) {

                if (chats.get(pos).getUserId().equals(currentUserId)) {

                    holder.selfChatLay.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me_selected));
                    holder.gifViewLay.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me_selected));
                } else {

                    holder.othersChatLay.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_other_selected));
                    holder.gifViewLayO.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me_selected));
                }
            }
        }

        Long dateTime = Long.parseLong(chats.get(pos).getLastMsgTime());

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Date parsed = new Date(Long.parseLong(chats.get(pos).getLastMsgTime()));

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

        float radius = context.getResources().getDimension(R.dimen.inner_img_xxl_1);

        holder.videoBlurImg.setShapeAppearanceModel(holder.videoBlurImg.getShapeAppearanceModel()
                .toBuilder()
                //.setAllCorners(CornerFamily.ROUNDED,radius)
                .setTopLeftCorner(CornerFamily.ROUNDED,radius)
                .setTopRightCorner(CornerFamily.ROUNDED,radius)
                .build());

        holder.videoBlurImgO.setShapeAppearanceModel(holder.videoBlurImgO.getShapeAppearanceModel()
                .toBuilder()
                //.setAllCorners(CornerFamily.ROUNDED,radius)
                //.setTopLeftCorner(CornerFamily.ROUNDED,radius)
                .setTopRightCorner(CornerFamily.ROUNDED,radius)
                .build());

        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy h:mm aa", Locale.getDefault());
        SimpleDateFormat simpleDateFormatC = new SimpleDateFormat("dd MMM yyyy h:mm:ss aa", Locale.getDefault());
        //String dateTimeChat = simpleDateFormat.format(dateTime);
        String dateTimeChatC = simpleDateFormatC.format(dateTime);

        if (!currentUserId.isEmpty()) {

            if (!chats.get(pos).getGroupNotice().isEmpty() && !chats.get(pos).getGroupNotice().equals("null")) {

                String newNotice = "";

                if (chats.get(pos).getGroupNotice().contains(Constants.INSTANCE.getCurrentUser())){

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

                    if (chats.get(pos).getUserId().equals(currentUserId)) {

                        holder.selfChatLay.setVisibility(View.VISIBLE);
                        holder.othersChatLay.setVisibility(View.GONE);
                        holder.rlGroupNotice.setVisibility(View.GONE);
                        holder.flImage.setVisibility(View.VISIBLE);
                        holder.flImageO.setVisibility(View.GONE);
                        holder.chatId.setText(chats.get(pos).getChatId());
                        //holder.userNameChatSelf.setText(dateTimeChat);
                        holder.chatImgDate.setText(dateTimeChat);

                        if (!chats.get(pos).getImgPath().equals("null") && !chats.get(pos).getImgPath().isEmpty()) {

                            MediaScannerConnection.scanFile(context, new String[]{chats.get(pos).getImgPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.e("ExternalStorage_AdpIFF", "Scanned " + path + ":");
                                    Log.d("ExternalStorage_Adp", "-> uri=" + uri);

                                    //imgUri = uri;

                                    if (uri != null && !uri.equals("null")) {

                                        chatActivity.runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {
                                                Log.e("IFF_Adp", "set_uri=" + uri);
                                                // Stuff that updates the UI
                                                holder.ivChatImage.setImageURI(uri);
                                                holder.ivChatImageBlur.setVisibility(View.GONE);
                                            }
                                        });
                                    }
                                    else {

                                        chatActivity.runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {

                                                // Stuff that updates the UI

                                                Glide.with(context)
                                                        .load(chats.get(pos).getThumbImg())
                                                        .thumbnail(0.01f)
                                                        .override(100,100)
                                                        .into(holder.ivChatImage);

                                                holder.ivChatImageBlur.setVisibility(View.VISIBLE);
                                                holder.downloadProgress.setVisibility(View.GONE);
                                                holder.downloadImgBtn.setVisibility(View.VISIBLE);
                                            }
                                        });


                                    }

                                }
                            });


                        } else {

                            Glide.with(context)
                                    .load(chats.get(pos).getThumbImg())
                                    .thumbnail(0.01f)
                                    .override(100,100)
                                    .into(holder.ivChatImage);

                            holder.downloadProgress.setVisibility(View.GONE);
                            holder.downloadImgBtn.setVisibility(View.VISIBLE);
                            holder.ivChatImageBlur.setVisibility(View.VISIBLE);

                            /*Glide.with(context)
                                    .load(Uri.parse(chats.get(pos).getUserImg()))
                                    .placeholder(R.drawable.meetupsfellow_transpatent)
                                    .into(holder.ivChatImage);*/
                        }

                        /*Glide.with(context)
                                .load(Uri.parse(chats.get(pos).getUserImg()))
                                .placeholder(R.drawable.meetupsfellow_transpatent)
                                .into(holder.ivChatImage);*/

                        if (chats.get(pos).isReadByAll()) {

                            holder.ivChatStatus.setBackgroundResource(R.drawable.double_tick_seen);
                            holder.ivChatStatusImg.setBackgroundResource(R.drawable.double_tick_seen);

                        } else {

                            holder.ivChatStatus.setBackgroundResource(R.drawable.double_tick);
                            holder.ivChatStatusImg.setBackgroundResource(R.drawable.double_tick);
                        }


                    }
                    else {

                        holder.selfChatLay.setVisibility(View.GONE);
                        holder.othersChatLay.setVisibility(View.VISIBLE);
                        holder.rlGroupNotice.setVisibility(View.GONE);
                        holder.flImage.setVisibility(View.GONE);
                        holder.flImageO.setVisibility(View.VISIBLE);
                        holder.chatIdO.setText(chats.get(pos).getChatId());
                        holder.userNameChatOther.setText(chats.get(pos).getUserName());
                        holder.chatImgDateO.setText(dateTimeChat);

                        /*Glide.with(context)
                                .load(Uri.parse(chats.get(pos).getUserImg()))
                                .placeholder(R.drawable.meetupsfellow_transpatent)
                                .into(holder.ivChatImageO);*/

                        if (!chats.get(pos).getImgPath().equals("null") && !chats.get(pos).getImgPath().isEmpty()) {

                            MediaScannerConnection.scanFile(context, new String[]{chats.get(pos).getImgPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.e("ExternalStorage_AdpELSS", "Scanned " + path + ":");
                                    Log.d("ExternalStorage_Adp", "-> uri=" + uri);
                                    //imgUri = uri;

                                    if (uri != null && !uri.equals("null")) {

                                        chatActivity.runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {

                                                // Stuff that updates the UI
                                                holder.ivChatImageO.setImageURI(uri);
                                                holder.ivChatImageBlurO.setVisibility(View.GONE);
                                            }
                                        });
                                    } else {

                                        chatActivity.runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {

                                                Glide.with(context)
                                                        .load(chats.get(pos).getThumbImg())
                                                        .thumbnail(0.01f)
                                                        .override(100,100)
                                                        .into(holder.ivChatImageO);

                                                // Stuff that updates the UI
                                                holder.ivChatImageBlurO.setVisibility(View.GONE);
                                                holder.downloadProgressO.setVisibility(View.GONE);
                                                holder.downloadImgBtnO.setVisibility(View.VISIBLE);
                                            }
                                        });


                                    }
                                }
                            });

                            /*if (imgUri != null && !imgUri.equals("null")) {

                                holder.ivChatImageO.setImageURI(imgUri);
                            } else {

                                holder.downloadProgressO.setVisibility(View.GONE);
                                holder.downloadImgBtnO.setVisibility(View.VISIBLE);
                            }*/

                        } else {

                            Glide.with(context)
                                    .load(chats.get(pos).getThumbImg())
                                    .thumbnail(0.01f)
                                    .override(100,100)
                                    .into(holder.ivChatImageO);

                            holder.downloadProgressO.setVisibility(View.GONE);
                            holder.downloadImgBtnO.setVisibility(View.VISIBLE);
                            holder.ivChatImageBlurO.setVisibility(View.VISIBLE);

                            /*Glide.with(context)
                                    .load(Uri.parse(chats.get(pos).getUserImg()))
                                    .placeholder(R.drawable.meetupsfellow_transpatent)
                                    .into(holder.ivChatImageO);*/
                        }

                    }


                }
                else {
                    Log.e("Group_Adap", "elsss__1&*");
                    if (!chats.get(pos).getVideoUrl().isEmpty() && !chats.get(pos).getVideoUrl().equals("null")) {
                        Log.e("Group_Adap", "IFF__Video");
                        if (chats.get(pos).getUserId().equals(currentUserId)) {

                            holder.selfChatLay.setVisibility(View.VISIBLE);
                            holder.othersChatLay.setVisibility(View.GONE);
                            holder.rlGroupNotice.setVisibility(View.GONE);
                            //holder.chatImgDate.setText(dateTimeChat);
                            holder.chatId.setText(chats.get(pos).getChatId());
                            holder.flImage.setVisibility(View.GONE);
                            holder.flImageO.setVisibility(View.GONE);
                            holder.flPlayO.setVisibility(View.GONE);
                            holder.flPlay.setVisibility(View.VISIBLE);
                            holder.userNameChatVideo.setText(chats.get(pos).getUserName());
                            holder.chatVideoDate.setText(dateTimeChat);

                            Log.d("VideoURL", chats.get(pos).getVideoUrl());

                            Glide.with(context)
                                    .load(chats.get(pos).getThumbImg())
                                    .into(holder.ivChatVideoPlay);

                            if (chats.get(pos).isReadByAll()) {

                                holder.ivChatStatus.setVisibility(View.GONE);
                                holder.ivChatStatusImg.setBackgroundResource(R.drawable.double_tick_seen);
                                holder.ivChatStatusVideo.setBackgroundResource(R.drawable.double_tick_seen);

                            } else {

                                holder.ivChatStatus.setVisibility(View.GONE);
                                holder.ivChatStatusImg.setBackgroundResource(R.drawable.double_tick);
                                holder.ivChatStatusVideo.setBackgroundResource(R.drawable.double_tick);
                            }

                        }
                        else {

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

                            Log.e("Group_Adap", "Implement New code Below");
                            if (!chats.get(pos).getImgPath().equals("null") && !chats.get(pos).getImgPath().isEmpty()) {

                                MediaScannerConnection.scanFile(context, new String[]{chats.get(pos).getImgPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                                    public void onScanCompleted(String path, Uri uri) {
                                        Log.d("ExternalStorage", "Scanned " + path + ":");
                                        Log.d("ExternalStorage", "-> uri=" + uri);
                                        //imgUri = uri;

                                        if (uri != null && !uri.equals("null")) {

                                            String[] extArr = path.split("\\.");

                                            Log.d("DocumentFile", extArr[extArr.length-1]);
                                            String ext = extArr[extArr.length-1];

                                            chatActivity.runOnUiThread(new Runnable() {

                                                @Override
                                                public void run() {

                                                    // Stuff that updates the UI

                                                    if ((ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("mp4") || ext.equals("mkv"))){

                                                        Glide.with(context)
                                                                .load(chats.get(pos).getImgPath())
                                                                .thumbnail(0.01f)
                                                                .override(100,100)
                                                                .into(holder.ivChatVideoPlayO);

                                                        holder.videoBlurImg.setVisibility(View.GONE);
                                                        holder.videoBlurImgO.setVisibility(View.GONE);
                                                        holder.videoDownloadBtnO.setVisibility(View.GONE);
                                                        holder.ivChatVideoPlayO.setVisibility(View.VISIBLE);

                                                    }else {
                                                        Log.e("Group_Adap", "In_Elss**");
                                                    }
                                                }
                                            });
                                        }
                                        else {

                                            chatActivity.runOnUiThread(new Runnable() {

                                                @Override
                                                public void run() {

                                                    // Stuff that updates the UI
                                                    Glide.with(context)
                                                            .load(chats.get(pos).getThumbImg())
                                                            .thumbnail(0.01f)
                                                            .override(100,100)
                                                            .into(holder.ivChatVideoPlayO);

                                                    holder.videoBlurImg.setVisibility(View.VISIBLE);
                                                    holder.videoBlurImgO.setVisibility(View.VISIBLE);
                                                    holder.videoDownloadBtnO.setVisibility(View.VISIBLE);
                                                    holder.ivChatVideoPlayO.setVisibility(View.GONE);

                                                }
                                            });


                                        }
                                    }
                                });

                            }
                            else {

                                Glide.with(context)
                                        .load(chats.get(pos).getThumbImg())
                                        .thumbnail(0.01f)
                                        .override(100,100)
                                        .into(holder.ivChatVideoPlayO);

                                //holder.downloadProgressO.setVisibility(View.GONE);
                                holder.videoBlurImgO.setVisibility(View.VISIBLE);
                                holder.videoDownloadBtnO.setVisibility(View.VISIBLE);

                            }

                          /*  Glide.with(context)
                                    .load(chats.get(pos).getThumbImg())
                                    .into(holder.ivChatVideoPlayO);*/
                        }
                    }

                    else if (!chats.get(pos).getGifId().isEmpty() && !chats.get(pos).getGifId().equals("null")) {

                        if (chats.get(pos).getUserId().equals(currentUserId)) {

                            holder.selfChatLay.setVisibility(View.GONE);
                            holder.othersChatLay.setVisibility(View.GONE);
                            holder.gifViewLay.setVisibility(View.VISIBLE);
                            holder.gifViewLayO.setVisibility(View.GONE);
                            holder.tvTimeGif.setText(dateTimeChat);
                            Log.e("TAG", "GifItem_Visible-IFFF");
                            GPHCore.INSTANCE.gifById(chats.get(pos).getGifId(), new Function2<MediaResponse, Throwable, Unit>() {
                                @Override
                                public Unit invoke(MediaResponse mediaResponse, Throwable throwable) {

                                    holder.gifView.setMedia(mediaResponse.getData(), RenditionType.original, context.getDrawable(R.drawable.meetupsfellow_transpatent));
                                    return null;
                                }
                            });

                            if (chats.get(pos).isReadByAll()) {

                                holder.ivStatusGif.setBackgroundResource(R.drawable.double_tick_seen);
                            }
                            else {

                                holder.ivStatusGif.setBackgroundResource(R.drawable.double_tick);
                            }
                        }
                        else {

                            holder.selfChatLay.setVisibility(View.GONE);
                            holder.othersChatLay.setVisibility(View.GONE);
                            holder.gifViewLay.setVisibility(View.GONE);
                            holder.gifViewLayO.setVisibility(View.VISIBLE);
                            holder.tvTimeGifO.setText(dateTimeChat);
                            holder.userNameChatGifO.setText(chats.get(pos).getUserName());
                            holder.userNameChatGifO.setVisibility(View.VISIBLE);
                            Log.e("TAG", "GifItem_Visible--ELSE");
                            GPHCore.INSTANCE.gifById(chats.get(pos).getGifId(), new Function2<MediaResponse, Throwable, Unit>() {
                                @Override
                                public Unit invoke(MediaResponse mediaResponse, Throwable throwable) {

                                    holder.gifViewO.setMedia(mediaResponse.getData(), RenditionType.original, context.getDrawable(R.drawable.meetupsfellow_transpatent));
                                    return null;
                                }
                            });


                        }
                    }

                    else {

                        if (!chats.get(pos).getLastMsg().isEmpty()) {

                            if (chats.get(pos).getUserId().equals(currentUserId)) {

                                if (!chats.get(pos).getReplyTo().isEmpty() && !chats.get(pos).getReplyTo().equals("null")) {

                                    if (!chats.get(pos).getReplyImg().isEmpty() && !chats.get(pos).getReplyImg().equals("null")) {

                                        Glide.with(context)
                                                .load(chats.get(pos).getReplyImg())
                                                .thumbnail(0.01f)
                                                .override(100, 100)
                                                .placeholder(R.drawable.meetupsfellow_transpatent)
                                                .into(holder.selfReplyImg);

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

                                holder.othersChatLay.setVisibility(View.GONE);
                                holder.selfChatLay.setVisibility(View.VISIBLE);
                                holder.rlGroupNotice.setVisibility(View.GONE);
                                holder.selfChat.setText(chats.get(pos).getLastMsg());
                                holder.chatId.setText(chats.get(pos).getChatId());
                                holder.selfChatTime.setText(dateTimeChat);

                                if (chats.get(pos).isReadByAll()) {

                                    holder.ivChatStatus.setBackgroundResource(R.drawable.double_tick_seen);
                                    holder.ivChatStatusImg.setBackgroundResource(R.drawable.double_tick_seen);

                                } else {

                                    holder.ivChatStatus.setBackgroundResource(R.drawable.double_tick);
                                    holder.ivChatStatusImg.setBackgroundResource(R.drawable.double_tick);
                                }

                                if (chats.get(pos).getLastMsg().matches("(([A-Za-z]{3,9}:(?://)?)(?:[-;:&=+$,\\w]+@)?[A-Za-z0-9.-]+|(?:www\\.|[-;:&=+$,\\w]+@)[A-Za-z0-9.-]+)((?:/[+~%/.\\w-]*)?\\??(?:[-+=&;%@.\\w]*)#?(?:[.!/\\\\\\w]*))?")){

                                    Log.e("Do_Impement","RecycleViewGroupChatAdpter_richlinkpreview");
                                   /* holder.richLinkView.setLink(chats.get(pos).getLastMsg(), new ViewListener() {
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

                            } else {

                                if (!chats.get(pos).getReplyTo().isEmpty() && !chats.get(pos).getReplyTo().equals("null")) {

                                    if (!chats.get(pos).getReplyImg().isEmpty() && !chats.get(pos).getReplyImg().equals("null")) {

                                        Glide.with(context)
                                                .load(chats.get(pos).getReplyImg())
                                                .thumbnail(0.01f)
                                                .override(100, 100)
                                                .placeholder(R.drawable.meetupsfellow_transpatent)
                                                .into(holder.otherReplyImg);

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

                                holder.selfChatLay.setVisibility(View.GONE);
                                holder.rlGroupNotice.setVisibility(View.GONE);
                                holder.othersChatTime.setText(dateTimeChat);
                                holder.othersName.setText(chats.get(pos).getUserName());
                                holder.chatIdO.setText(chats.get(pos).getChatId());
                                holder.othersChatLay.setVisibility(View.VISIBLE);
                                holder.othersChat.setText(chats.get(pos).getLastMsg());

                                if (chats.get(pos).getLastMsg().matches("(([A-Za-z]{3,9}:(?://)?)(?:[-;:&=+$,\\w]+@)?[A-Za-z0-9.-]+|(?:www\\.|[-;:&=+$,\\w]+@)[A-Za-z0-9.-]+)((?:/[+~%/.\\w-]*)?\\??(?:[-+=&;%@.\\w]*)#?(?:[.!/\\\\\\w]*))?")){
                                    Log.e("Do_Impement","RecycleViewGroupChatAdpter_richlinkpreview_22");
                                  /*  holder.richLinkViewO.setLink(chats.get(pos).getLastMsg(), new ViewListener() {
                                        @Override
                                        public void onSuccess(boolean status) {

                                            Log.d("LinkPreview", String.valueOf(status));
                                            holder.richLinkViewO.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onError(Exception e) {

                                            Log.d("LinkPreview", String.valueOf(e));
                                            holder.richLinkViewO.setVisibility(View.GONE);
                                        }
                                    });*/
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

        holder.selfChatLay.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                holder.selfChatLay.setClickable(false);
                holder.selfChatLay.setEnabled(false);
                isSelecting = true;
                holder.selfChatLay.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me_selected));
                selectedPos.add(String.valueOf(pos));
              ///  chatActivity.chatsToDelete(chats.get(pos).getChatId());
               /// chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatC, chats.get(pos).getChatId());
                //holder.selfChatLay.setClickable(true);

                Log.d("isSelecting", isSelecting.toString());

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
             ///   chatActivity.chatsToDelete(chats.get(pos).getChatId());
              ///  chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatC, chats.get(pos).getChatId());
                //holder.selfChatLay.setClickable(true);

                Log.d("isSelecting", isSelecting.toString());

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

                    selectedPos.remove(String.valueOf(pos));
               ///     chatActivity.chatsToDelete(chats.get(pos).getChatId());
                ///    chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatC, chats.get(pos).getChatId());
                    holder.gifViewLay.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me));

                    if (selectedPos.isEmpty()) {

                        isSelecting = false;
                    }

                } else {

                    if (isSelecting) {

                    ///    chatActivity.chatsToDelete(chats.get(pos).getChatId());
                    ///    chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatC, chats.get(pos).getChatId());
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

                Log.d("isSelecting", isSelecting.toString());

                if (selectedPos.contains(String.valueOf(pos))) {

                    selectedPos.remove(String.valueOf(pos));
                ///    chatActivity.chatsToDelete(chats.get(pos).getChatId());
                ///    chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatC, chats.get(pos).getChatId());
                    holder.selfChatLay.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me));

                    if (selectedPos.isEmpty()) {

                        isSelecting = false;
                    }

                } else {

                    if (isSelecting) {

                   ///     chatActivity.chatsToDelete(chats.get(pos).getChatId());
                   ///     chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatC, chats.get(pos).getChatId());
                        selectedPos.add(String.valueOf(pos));
                        holder.selfChatLay.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_me_selected));
                    } else {

                        if (!chats.get(pos).getImgPath().equals("null") && !chats.get(pos).getImgPath().isEmpty()) {
                            Log.e("Adapter","videoDeviceUrl_First: "+chats.get(pos).getImgPath());
                            Intent intent = new Intent(context, VideoPlayerActivity.class);
                            intent.putExtra(Constants.IntentDataKeys.ImgPath, chats.get(pos).getImgPath());
                            context.startActivity(intent);

                        }/* else if (!chats.get(pos).getVideoUrl().equals("null") && !chats.get(pos).getVideoUrl().isEmpty()){
                            Log.e("Adapter","videoDeviceUrl_First$%: "+chats.get(pos).getVideoUrl());
                            Intent intent = new Intent(context, VideoPlayerActivity.class);
                            intent.putExtra(Constants.IntentDataKeys.VideoUrl, chats.get(pos).getVideoUrl());
                            context.startActivity(intent);
                        }*/
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
                //chatActivity.chatsToDelete(chats.get(pos).getChatId());
             ///   chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatC, chats.get(pos).getChatId());
                Log.d("isSelecting", isSelecting.toString());

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

                if (selectedPos.contains(String.valueOf(pos))) {

                    selectedPos.remove(String.valueOf(pos));
               ///     chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatC, chats.get(pos).getChatId());
                    //chatActivity.chatsToDelete(chats.get(pos).getChatId());
                    holder.othersChatLay.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_other));

                    if (selectedPos.isEmpty()) {

                        isSelecting = false;
                    }

                } else {

                   /* if (!chats.get(pos).getUserImg().equals("null") && !chats.get(pos).getUserImg().isEmpty()) {

                        Intent intent = new Intent(context, VideoPlayerActivity.class);
                        intent.putExtra(Constants.IntentDataKeys.VideoUrl, chats.get(pos).getUserImg());
                        context.startActivity(intent);
                    }*/

                    if (isSelecting) {

                        selectedPos.add(String.valueOf(pos));
                    ///    chatActivity.showCopyOption(chats.get(pos).getLastMsg(), chats.get(pos).getUserName(), dateTimeChatC, chats.get(pos).getChatId());
                        //chatActivity.chatsToDelete(chats.get(pos).getChatId());
                        holder.othersChatLay.setBackground(context.getResources().getDrawable(R.drawable.drawable_chat_other_selected));
                    } else {

                        if (!chats.get(pos).getImgPath().equals("null") && !chats.get(pos).getImgPath().isEmpty()) {
                            Log.e("Adapter","videoDeviceUrl00: "+chats.get(pos).getImgPath());
                            Intent intent = new Intent(context, VideoPlayerActivity.class);
                            intent.putExtra(Constants.IntentDataKeys.ImgPath, chats.get(pos).getImgPath());
                            context.startActivity(intent);

                        } /*else if (!chats.get(pos).getVideoUrl().equals("null") && !chats.get(pos).getVideoUrl().isEmpty()){
                            Log.e("Adapter","videoDeviceUrl001: "+chats.get(pos).getVideoUrl());
                            Intent intent = new Intent(context, VideoPlayerActivity.class);
                            intent.putExtra(Constants.IntentDataKeys.VideoUrl, chats.get(pos).getVideoUrl());
                            context.startActivity(intent);
                        }*/
                    }
                }
            }
        });

        holder.replyChatLayU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Collections.reverse(holderArr);

             ///   chatActivity.scrollToMsg(chats.get(pos).getReplyId());

                //mRecyclerView.findViewHolderForAdapterPosition(msgPos).itemView.setBackgroundColor(Color.GRAY);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        for (int i = 0; i < chats.size(); i++) {

                            if (chats.get(i).getChatId().equals(chats.get(pos).getReplyId())) {

                                Log.d("replyMsgID", String.valueOf(i));

                                for (int j = 0; j < holderArr.size(); j++) {

                                    if (holderArr.get(j).chatId.getText().equals(chats.get(pos).getReplyId()) || holderArr.get(j).chatIdO.getText().equals(chats.get(pos).getReplyId())) {

                                        holderArr.get(j).chatMsgLay.setBackgroundColor(Color.GRAY);

                                        int finalJ = j;
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                holderArr.get(finalJ).chatMsgLay.setBackgroundColor(Color.TRANSPARENT);
                                            }
                                        }, 400);

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

           ///     chatActivity.scrollToMsg(chats.get(pos).getReplyId());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        for (int i = 0; i < chats.size(); i++) {

                            if (chats.get(i).getChatId().equals(chats.get(pos).getReplyId())) {

                                Log.d("replyMsgID", String.valueOf(i));

                                for (int j = 0; j < holderArr.size(); j++) {

                                    if (holderArr.get(j).chatId.getText().equals(chats.get(pos).getReplyId()) || holderArr.get(j).chatIdO.getText().equals(chats.get(pos).getReplyId())) {

                                        holderArr.get(j).chatMsgLay.setBackgroundColor(Color.GRAY);

                                        int finalJ = j;
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                holderArr.get(finalJ).chatMsgLay.setBackgroundColor(Color.TRANSPARENT);
                                            }
                                        }, 400);

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
                chatActivity.downloadAndSaveImage(chats.get(pos).getUserImg(), chats.get(pos).getChatId(),2);

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
                chatActivity.downloadAndSaveImage(chats.get(pos).getUserImg(),chats.get(pos).getChatId(),2);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        holder.downloadProgressO.setVisibility(View.GONE);
                    }
                }, 2000);
            }
        });

        holder.ivChatVideoBtnO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Adapter","personal_VideoRe: "+chats.get(pos).getImgPath());
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

                Log.e("UrlTAG", "video_Url: " +chats.get(pos).getVideoUrl());
                Log.e("UrlTAG", "Image_Url: " +chats.get(pos).getUserImg());
                Log.e("UrlTAG", "ImagePath_Url: " +chats.get(pos).getImgPath());
                holder.downloadProgressO.setVisibility(View.VISIBLE);
                holder.downloadImgBtnO.setVisibility(View.GONE);
                chatActivity.downloadAndSaveImage(chats.get(pos).getVideoUrl(),chats.get(pos).getChatId(),4);

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

                if (chats.get(pos).getImgPath() != null && chats.get(pos).getVideoUrl() != null){
                    Log.e("Adapter","videoDeviceUrl: "+chats.get(pos).getImgPath());
                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra(Constants.IntentDataKeys.VideoUrl, chats.get(pos).getImgPath());
                    context.startActivity(intent);
                }else {
                    Log.e("Adapter","VideoUrl: "+chats.get(pos).getVideoUrl());
                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra(Constants.IntentDataKeys.VideoUrl, chats.get(pos).getVideoUrl());
                    context.startActivity(intent);
                }

            }
        });

        holder.ivChatVideoBtnO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chats.get(pos).getImgPath() != null && chats.get(pos).getVideoUrl() != null){
                    Log.e("Adapter","videoDeviceUrl22: "+chats.get(pos).getImgPath());
                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra(Constants.IntentDataKeys.VideoUrl, chats.get(pos).getImgPath());
                    context.startActivity(intent);
                }else {
                    Log.e("Adapter","VideoUrl22: "+chats.get(pos).getVideoUrl());
                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra(Constants.IntentDataKeys.VideoUrl, chats.get(pos).getVideoUrl());
                    context.startActivity(intent);
                }
            }
        });

        holder.swipeRepLay.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {

                Log.d("swipeChat", "startOpen");

                if (!chats.get(pos).getUserImg().isEmpty() && !chats.get(pos).getUserImg().equals("null")){

                    if (!chats.get(pos).getImgPath().isEmpty() && !chats.get(pos).getImgPath().equals("null")){

                        if (chats.get(pos).getUserId().equals(currentUserId)) {

                            chatActivity.replyToChat("You", "image", dateTimeChat, chats.get(pos).getChatId(),chats.get(pos).getUserImg(), chats.get(pos).getImgPath());
                        } else {

                            chatActivity.replyToChat(chats.get(pos).getUserName(), "image", dateTimeChat, chats.get(pos).getChatId(),chats.get(pos).getUserImg(), chats.get(pos).getImgPath());
                        }
                    }

                    else {

                        if (chats.get(pos).getUserId().equals(currentUserId)) {

                            chatActivity.replyToChat("You", "image", dateTimeChat, chats.get(pos).getChatId(), chats.get(pos).getThumbImg(), "");
                        } else {

                            chatActivity.replyToChat(chats.get(pos).getUserName(), "image", dateTimeChat, chats.get(pos).getChatId(), chats.get(pos).getThumbImg(), "");
                        }
                    }

                } else {

                    if (!chats.get(pos).getVideoUrl().isEmpty() && !chats.get(pos).getVideoUrl().equals("null")){

                        if (chats.get(pos).getUserId().equals(currentUserId)) {

                            chatActivity.replyToChat("You", "video", dateTimeChatC, chats.get(pos).getChatId(), chats.get(pos).getThumbImg(), "");
                        } else {

                            chatActivity.replyToChat(chats.get(pos).getUserName(), "video", dateTimeChatC, chats.get(pos).getChatId(), chats.get(pos).getThumbImg(), "");
                        }

                    }

                    else {

                        if (chats.get(pos).getUserId().equals(currentUserId)) {

                            chatActivity.replyToChat("You", chats.get(pos).getLastMsg(), dateTimeChat, chats.get(pos).getChatId(), "", "");
                        } else {

                            chatActivity.replyToChat(chats.get(pos).getUserName(), chats.get(pos).getLastMsg(), dateTimeChat, chats.get(pos).getChatId(), "", "");
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

    @Override
    public int getItemCount() {
        //Number of Items you want to display
        return chats.size();
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

                filteredList.addAll(chatsAll);
            }
            else {

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

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //grabbing the views from rv_column.xml

        TextView selfChat, othersChat, selfChatTime, othersChatTime, othersName, groupNotice, userNameChatSelf, userNameChatOther;
        TextView userReplyName, userReplyNameO, replyChatMessage, replyChatMessageO, chatId, chatIdO, chatImgDateO, chatImgDate,
                userNameChatVideo1, chatVideoDateO, chatVideoDate, userNameChatVideo, tvTimeGif, tvTimeGifO, userNameChatGifO;
        CardView replyChatLayU, replyChatLayO;
        RelativeLayout selfChatLay, othersChatLay, rlGroupNotice, chatMsgLay, gifViewLay, gifViewLayO;
        ImageView ivChatStatus, ivChatImageO, ivChatImage, otherReplyImg, selfReplyImg, downloadImgBtnO, downloadImgBtn, ivChatStatusImg
                , ivChatImageBlurO, ivChatImageBlur, ivChatVideoPlayO, ivChatStatusVideo, ivChatVideoPlay,
                ivChatVideoBtn, ivChatVideoBtnO, ivStatusGif, videoDownloadBtnO,
        videoDownloadBtn;
        ShapeableImageView  videoBlurImgO, videoBlurImg;
        ProgressBar downloadProgress, downloadProgressO;
        FrameLayout flImage, flImageO, flPlayO, flPlay;
        SwipeLayout swipeRepLay;
        LinearLayout selfReplyImgLay, otherReplyImgLay;
        GPHMediaView gifView, gifViewO;
     ///   RichLinkView richLinkView, richLinkViewO;
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
            videoDownloadBtnO = itemView.findViewById(R.id.videoDownloadBtnO);
            videoDownloadBtn = itemView.findViewById(R.id.videoDownloadBtn);
            videoBlurImgO = itemView.findViewById(R.id.videoBlurImgO);
            videoBlurImg = itemView.findViewById(R.id.videoBlurImg);
            ivChatImage = itemView.findViewById(R.id.ivChatImage);
            ivChatImageO = itemView.findViewById(R.id.ivChatImageO);
            swipeRepLay = itemView.findViewById(R.id.swipeRepLay);
            replyChatLayU = itemView.findViewById(R.id.replyChatLayU);
            replyChatLayO = itemView.findViewById(R.id.replyChatLayO);
            userReplyName = itemView.findViewById(R.id.userReplyNameU);
            userReplyNameO = itemView.findViewById(R.id.userReplyNameO);
            replyChatMessage = itemView.findViewById(R.id.replyChatMessageU);
            replyChatMessageO = itemView.findViewById(R.id.replyChatMessageO);
            chatId = itemView.findViewById(R.id.chatId);
            chatIdO = itemView.findViewById(R.id.chatIdO);
            chatMsgLay = itemView.findViewById(R.id.chatMsgLay);
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
        }
    }

}
