package com.connect.meetupsfellow.mvp.view.model

import com.connect.meetupsfellow.retrofit.response.ResponsePrivateAccess
import com.connect.meetupsfellow.retrofit.response.ResponsePrivatePics

class RecycleModel : Comparable<RecycleModel> {

    var userPics: Int? = null
    var interestName: String? = null
    var userId: String? = null
    var userIdPvt: Int? = null
    var userName: String? = null
    var userLoc: String? = null
    var userImg: String? = null
    var images: String? = null
    var allImg: String? = null
    var totalImg: Int? = null
    var lastMsg: String? = null
    var groupId: String? = null
    var lastMsgTime: String? = null
    var unReadCount: String? = null
    var lastMsgSender: String? = null
    var onlineStatus: String? = null
    var isBlocked: String? = null
    var isReadByAll: Boolean? = null
    var isReadByUser: String? = null
    var isAdmin: Boolean? = null
    var iAmAdmin: Boolean? = null
    var groupNotice: String? = null
    var otherUserId: String? = null
    var hideMsg: Boolean? = null
    var replyTo: String? = null
    var replyMsg: String? = null
    var chatId: String? = null
    var replyId: String? = null
    var replyImg: String? = null
    var imgPath: String? = null
    var thumbImg: String? = null
    var isMsgCleared: String? = null
    var videoUrl : String? = null
    var gifId : String? = null
    var status : String? = null
    var fileUrl : String? = null
    var mediaSize : String? = null
    var IsUserConnected : String? = null
    var mediaType : String? = null

    var pvtImg: ArrayList<ResponsePrivatePics>? = null
    var pvtList: ArrayList<ResponsePrivateAccess>? = null

    constructor(img: Int) {

        userPics = img
    }

    constructor(
        img: String,
        name: String,
        msg: String,
        count: String,
        lstMsgTime: String,
        grpId: String,
        lstMsgSender: String
    ) {

        userImg = img
        userName = name
        lastMsg = msg
        unReadCount = count
        lastMsgTime = lstMsgTime
        groupId = grpId
        lastMsgSender = lstMsgSender
    }

    constructor(
        img: String,
        name: String,
        msg: String,
        count: String,
        lstMsgTime: String,
        grpId: String,
        lstMsgSender: String,
        otherUserId: String,
        status: String,
        IsUserConnected: String
    ) {

        userImg = img
        userName = name
        lastMsg = msg
        unReadCount = count
        lastMsgTime = lstMsgTime
        groupId = grpId
        lastMsgSender = lstMsgSender
        this.otherUserId = otherUserId
        this.status = status
        this.IsUserConnected = IsUserConnected
    }

    constructor(
        userName: String,
        userStatus: String,
        userImg: String,
        userId: String,
        isBlocked: String,
        isAdmin: Boolean,
        iAmAdmin: Boolean
    ) {

        this.userName = userName
        this.userImg = userImg
        this.onlineStatus = userStatus
        this.userId = userId
        this.isBlocked = isBlocked
        this.isAdmin = isAdmin
        this.iAmAdmin = iAmAdmin

    }

    constructor(
        sendName: String,
        sendMsg: String,
        sendMsgTime: String,
        sendId: String,
        isRead: Boolean,
        notice: String,
        img: String
    ) {

        userName = sendName
        lastMsg = sendMsg
        lastMsgTime = sendMsgTime
        userId = sendId
        isReadByAll = isRead
        groupNotice = notice
        userImg = img
    }

    constructor(
        sendName: String,
        sendMsg: String,
        sendMsgTime: String,
        sendId: String,
        isRead: Boolean,
        notice: String,
        img: String,
        replyTo: String,
        replyMsg: String,
        chatId: String,
        replyId: String,
        replyImg: String,
        imgPath: String,
        thumbImg: String,
        videoUrl : String
    ) {

        userName = sendName
        lastMsg = sendMsg
        lastMsgTime = sendMsgTime
        userId = sendId
        isReadByAll = isRead
        groupNotice = notice
        userImg = img
        this.replyTo = replyTo
        this.replyMsg = replyMsg
        this.chatId = chatId
        this.replyId = replyId
        this.replyImg = replyImg
        this.imgPath = imgPath
        this.thumbImg = thumbImg
        this.videoUrl = videoUrl
    }

    //Personal Chat Model
    constructor(
        sendName: String,
        sendMsg: String,
        sendMsgTime: String,
        sendId: String,
        isRead: String,
        notice: String,
        img: String,
        replyTo: String,
        replyMsg: String,
        chatId: String,
        replyId: String,
        replyImg: String,
        imgPath: String,
        thumbImg: String,
        videoUrl : String,
        gifId : String,
        fileUrl : String,
        mediaSize : String
    ) {

        userName = sendName
        lastMsg = sendMsg
        lastMsgTime = sendMsgTime
        userId = sendId
        isReadByUser = isRead
        groupNotice = notice
        userImg = img
        this.replyTo = replyTo
        this.replyMsg = replyMsg
        this.chatId = chatId
        this.replyId = replyId
        this.replyImg = replyImg
        this.imgPath = imgPath
        this.thumbImg = thumbImg
        this.videoUrl = videoUrl
        this.gifId = gifId
        this.fileUrl = fileUrl
        this.mediaSize = mediaSize
    }

    //Group Chat Model
    constructor(
        sendName: String,
        sendMsg: String,
        sendMsgTime: String,
        sendId: String,
        isRead: Boolean,
        notice: String,
        img: String,
        replyTo: String,
        replyMsg: String,
        chatId: String,
        replyId: String,
        replyImg: String,
        imgPath: String,
        thumbImg: String,
        videoUrl : String,
        gifId : String
    ) {

        userName = sendName
        lastMsg = sendMsg
        lastMsgTime = sendMsgTime
        userId = sendId
        isReadByAll = isRead
        groupNotice = notice
        userImg = img
        this.replyTo = replyTo
        this.replyMsg = replyMsg
        this.chatId = chatId
        this.replyId = replyId
        this.replyImg = replyImg
        this.imgPath = imgPath
        this.thumbImg = thumbImg
        this.videoUrl = videoUrl
        this.gifId = gifId
    }

    constructor(
        sendName: String,
        sendMsg: String,
        sendMsgTime: String,
        sendId: String,
        isRead: Boolean,
        notice: String,
        img: String,
        hideMsg: Boolean
    ) {

        userName = sendName
        lastMsg = sendMsg
        lastMsgTime = sendMsgTime
        userId = sendId
        isReadByAll = isRead
        groupNotice = notice
        userImg = img
        this.hideMsg = hideMsg
    }

    constructor(img: Int, name: String) {

        userPics = img
        interestName = name

    }

    constructor(Img: String, name: String, id: String) {

        userImg = Img
        userName = name
        userId = id

    }

    constructor(name: String, imgPath: String, userId: Int, location: String) {

        this.userName = name
        this.userImg = imgPath
        this.userIdPvt = userId
        this.userLoc = location

    }

    constructor(imgPath: String) {

        images = imgPath

    }

    /*constructor(pvtImg : ArrayList<ResponsePrivatePics>){

        this.pvtImg = pvtImg
    }*/

    constructor(pvtList: ArrayList<ResponsePrivateAccess>) {

        this.pvtList = pvtList
    }

    constructor(imgPath: String, Id: String, allImg: String, size: Int, type:String) {

        images = imgPath
        userId = Id
        this.allImg = allImg
        this.totalImg = size
        this.mediaType = type
    }

    fun getUnread(): Int {

        return Integer.valueOf(unReadCount)
    }

    override fun compareTo(other: RecycleModel): Int {

        return (other.lastMsgTime!!.toLong() - this.lastMsgTime!!.toLong()).toInt()
    }
}