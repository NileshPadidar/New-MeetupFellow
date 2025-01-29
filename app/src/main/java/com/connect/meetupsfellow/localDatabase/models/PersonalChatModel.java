package com.connect.meetupsfellow.localDatabase.models;

public class PersonalChatModel {

    String  imgUrl, msg, msgTime, receiverId, receiverName, replyId, replyImg, replyMsg, replyMsgTime, replyTo,
            senderId, senderName, imgPathSelf, imgPathOther, isClearedSelf, isClearedOther;
    int isRead;
    long chat_id;

    public PersonalChatModel() {

    }

    public PersonalChatModel(long chat_id, String imgUrl, String msg, String msgTime, String receiverId, String receiverName, String replyId, String replyImg, String replyMsg, String replyMsgTime, String replyTo, String senderId, String senderName, boolean isRead, String imgPathSelf
            , String imgPathOther, String isClearedSelf, String isClearedOther) {
        this.chat_id = chat_id;
        this.imgUrl = imgUrl;
        this.msg = msg;
        this.msgTime = msgTime;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.replyId = replyId;
        this.replyImg = replyImg;
        this.replyMsg = replyMsg;
        this.replyMsgTime = replyMsgTime;
        this.replyTo = replyTo;
        this.senderId = senderId;
        this.senderName = senderName;
        this.imgPathSelf = imgPathSelf;
        this.imgPathOther = imgPathOther;
        this.isClearedSelf = isClearedSelf;
        this.isClearedOther = isClearedOther;

        if (isRead) {

            this.isRead = 1;

        } else {

            this.isRead = 0;
        }
    }

    public void setImgPathSelf(String imgPathSelf) {
        this.imgPathSelf = imgPathSelf;
    }

    public String getImgPathOther() {
        return imgPathOther;
    }

    public void setImgPathOther(String imgPathOther) {
        this.imgPathOther = imgPathOther;
    }

    public String getIsClearedSelf() {
        return isClearedSelf;
    }

    public void setIsClearedSelf(String isClearedSelf) {
        this.isClearedSelf = isClearedSelf;
    }

    public String getIsClearedOther() {
        return isClearedOther;
    }

    public void setIsClearedOther(String isClearedOther) {
        this.isClearedOther = isClearedOther;
    }

    public String getImgPathSelf() {
        return imgPathSelf;
    }

    public void setImgPath(String imgPath) {
        this.imgPathSelf = imgPath;
    }

    public long getChat_id() {
        return chat_id;
    }

    public void setChat_id(long chat_id) {
        this.chat_id = chat_id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public String getReplyImg() {
        return replyImg;
    }

    public void setReplyImg(String replyImg) {
        this.replyImg = replyImg;
    }

    public String getReplyMsg() {
        return replyMsg;
    }

    public void setReplyMsg(String replyMsg) {
        this.replyMsg = replyMsg;
    }

    public String getReplyMsgTime() {
        return replyMsgTime;
    }

    public void setReplyMsgTime(String replyMsgTime) {
        this.replyMsgTime = replyMsgTime;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }
}
