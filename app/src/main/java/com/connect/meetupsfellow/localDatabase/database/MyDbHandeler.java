package com.connect.meetupsfellow.localDatabase.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.connect.meetupsfellow.localDatabase.models.PersonalChatModel;
import com.connect.meetupsfellow.localDatabase.params.ChatParams;

import java.util.ArrayList;
import java.util.List;

public class MyDbHandeler extends SQLiteOpenHelper {

    SQLiteDatabase sqLiteDatabase;

    public MyDbHandeler(@Nullable Context context) {
        super(context, ChatParams.DB_NAME, null, ChatParams.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String createPersonalChat = "CREATE TABLE " + ChatParams.PERSONAL_CHAT_TABLE_NAME + " (" + ChatParams.KEY_PRIMARY + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ChatParams.KEY_ID + " INTEGER UNIQUE, " + ChatParams.KEY_MSG + " TEXT, "
                + ChatParams.KEY_REPLY_ID + " TEXT, " + ChatParams.KEY_REPLY_IMG + " TEXT, " + ChatParams.KEY_REPLY_TO + " TEXT, " + ChatParams.KEY_REPLY_MSG + " TEXT, "
                + ChatParams.KEY_IS_READ + " INTEGER, " + ChatParams.KEY_IMG_URL + " TEXT, " + ChatParams.KEY_MSG_TIME + " TEXT, " + ChatParams.KEY_RECEIVER_ID + " TEXT, "
                + ChatParams.KEY_REPLY_MSG_TIME + " TEXT, " + ChatParams.KEY_SENDER_ID + " TEXT, " + ChatParams.KEY_SENDER_NAME + " TEXT, " + ChatParams.KEY_RECEIVER_NAME + " TEXT, "
                + ChatParams.KEY_IMG_PATH_SELF + " TEXT, " + ChatParams.KEY_IMG_PATH_OTHER + " TEXT, " + ChatParams.KEY_IS_CLEARED_SELF + " TEXT, " + ChatParams.KEY_IS_CLEARED_OTHER + " TEXT " + ")";

        sqLiteDatabase.execSQL(createPersonalChat);
        Log.e("ChatDB", "Database Created");

        this.sqLiteDatabase = sqLiteDatabase;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void updateChat(PersonalChatModel personalChatModel) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ChatParams.KEY_ID, personalChatModel.getChat_id());
        values.put(ChatParams.KEY_MSG, personalChatModel.getMsg());
        values.put(ChatParams.KEY_REPLY_ID, personalChatModel.getReplyId());
        values.put(ChatParams.KEY_REPLY_IMG, personalChatModel.getReplyImg());
        values.put(ChatParams.KEY_REPLY_TO, personalChatModel.getReplyTo());
        values.put(ChatParams.KEY_REPLY_MSG, personalChatModel.getReplyMsg());
        values.put(ChatParams.KEY_IS_READ, personalChatModel.getIsRead());
        values.put(ChatParams.KEY_IMG_URL, personalChatModel.getImgUrl());
        values.put(ChatParams.KEY_MSG_TIME, personalChatModel.getMsgTime());
        values.put(ChatParams.KEY_RECEIVER_ID, personalChatModel.getReceiverId());
        values.put(ChatParams.KEY_REPLY_MSG_TIME, personalChatModel.getReplyMsgTime());
        values.put(ChatParams.KEY_SENDER_ID, personalChatModel.getSenderId());
        values.put(ChatParams.KEY_SENDER_NAME, personalChatModel.getSenderName());
        values.put(ChatParams.KEY_RECEIVER_NAME, personalChatModel.getReceiverName());
        values.put(ChatParams.KEY_IMG_PATH_SELF, personalChatModel.getImgPathSelf());
        values.put(ChatParams.KEY_IMG_PATH_OTHER, personalChatModel.getImgPathOther());
        values.put(ChatParams.KEY_IS_CLEARED_SELF, personalChatModel.getIsClearedSelf());
        values.put(ChatParams.KEY_IS_CLEARED_OTHER, personalChatModel.getIsClearedOther());

        Log.e("ChatUpdate", String.valueOf(personalChatModel.getChat_id()) + "   " + personalChatModel.getIsRead() + "   " +
                personalChatModel.getImgPathOther() + "   " + personalChatModel.getImgPathSelf());

        db.update(ChatParams.PERSONAL_CHAT_TABLE_NAME, values, ChatParams.KEY_ID+"=? AND " + ChatParams.KEY_IS_READ+"!=? AND " + ChatParams.KEY_IMG_PATH_OTHER+"!=? AND "
                + ChatParams.KEY_IMG_PATH_SELF+"!=?", new String[] {String.valueOf(personalChatModel.getChat_id()), String.valueOf(personalChatModel.getIsRead()), String.valueOf(personalChatModel.getImgPathOther()),
                String.valueOf(personalChatModel.getImgPathSelf())});

        db.close();

        Log.e("ChatDB", "Database Updated");
    }

    public void addChat(PersonalChatModel personalChatModel) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ChatParams.KEY_ID, personalChatModel.getChat_id());
        values.put(ChatParams.KEY_MSG, personalChatModel.getMsg());
        values.put(ChatParams.KEY_REPLY_ID, personalChatModel.getReplyId());
        values.put(ChatParams.KEY_REPLY_IMG, personalChatModel.getReplyImg());
        values.put(ChatParams.KEY_REPLY_TO, personalChatModel.getReplyTo());
        values.put(ChatParams.KEY_REPLY_MSG, personalChatModel.getReplyMsg());
        values.put(ChatParams.KEY_IS_READ, personalChatModel.getIsRead());
        values.put(ChatParams.KEY_IMG_URL, personalChatModel.getImgUrl());
        values.put(ChatParams.KEY_MSG_TIME, personalChatModel.getMsgTime());
        values.put(ChatParams.KEY_RECEIVER_ID, personalChatModel.getReceiverId());
        values.put(ChatParams.KEY_REPLY_MSG_TIME, personalChatModel.getReplyMsgTime());
        values.put(ChatParams.KEY_SENDER_ID, personalChatModel.getSenderId());
        values.put(ChatParams.KEY_SENDER_NAME, personalChatModel.getSenderName());
        values.put(ChatParams.KEY_RECEIVER_NAME, personalChatModel.getReceiverName());
        values.put(ChatParams.KEY_IMG_PATH_SELF, personalChatModel.getImgPathSelf());
        values.put(ChatParams.KEY_IMG_PATH_OTHER, personalChatModel.getImgPathOther());
        values.put(ChatParams.KEY_IS_CLEARED_SELF, personalChatModel.getIsClearedSelf());
        values.put(ChatParams.KEY_IS_CLEARED_OTHER, personalChatModel.getIsClearedOther());

        db.insert(ChatParams.PERSONAL_CHAT_TABLE_NAME, null, values);

        db.close();

        Log.e("ChatDB", "Inserted");
    }

    public boolean isChatExists(long chatId) {

        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM "+ ChatParams.PERSONAL_CHAT_TABLE_NAME +" WHERE " + ChatParams.KEY_ID + " == " + chatId;
        Cursor cursor = db.rawQuery(select, null);

        if (cursor.moveToFirst()) {

            Log.d("ChatEXISTSI", String.valueOf(chatId));
            Log.d("ChatEXISTS", String.valueOf(cursor.getLong(1)));

            if (!cursor.getString(1).isEmpty()) {

                return true;
            } else {

                return false;
            }

        } else {

            return false;
        }

    }

    public List<PersonalChatModel> getAllChats(String id) {
        List<PersonalChatModel> personalChatModelsArr = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Log.d("LocalChatsId", id);

        String select = "SELECT * FROM "+ ChatParams.PERSONAL_CHAT_TABLE_NAME +" WHERE " + "(" + ChatParams.KEY_RECEIVER_ID + " = " + "'" + id + "'" + ")" + " OR " + "(" + ChatParams.KEY_SENDER_ID + " = " + "'" + id + "'" + ")";
        Cursor cursor = db.rawQuery(select, null);

        if (cursor.moveToFirst()){

            do {

                PersonalChatModel personalChatModel = new PersonalChatModel();

                personalChatModel.setChat_id(cursor.getLong(1));
                personalChatModel.setMsg(cursor.getString(2));
                personalChatModel.setReplyId(cursor.getString(3));
                personalChatModel.setReplyImg(cursor.getString(4));
                personalChatModel.setReplyTo(cursor.getString(5));
                personalChatModel.setReplyMsg(cursor.getString(6));
                personalChatModel.setIsRead(cursor.getInt(7));
                personalChatModel.setImgUrl(cursor.getString(8));
                personalChatModel.setMsgTime(cursor.getString(9));
                personalChatModel.setReceiverId(cursor.getString(10));
                personalChatModel.setReplyMsgTime(cursor.getString(11));
                personalChatModel.setSenderId(cursor.getString(12));
                personalChatModel.setSenderName(cursor.getString(13));
                personalChatModel.setReceiverName(cursor.getString(14));
                personalChatModel.setImgPathSelf(cursor.getString(15));
                personalChatModel.setImgPathOther(cursor.getString(16));
                personalChatModel.setIsClearedSelf(cursor.getString(17));
                personalChatModel.setIsClearedOther(cursor.getString(18));

                personalChatModelsArr.add(personalChatModel);

            } while (cursor.moveToNext());
        }

        return personalChatModelsArr;
    }
}
