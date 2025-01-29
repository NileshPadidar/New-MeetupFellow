package com.connect.meetupsfellow.retrofit.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CurrentPlanInfo {

@SerializedName("plan_id")
@Expose
public Integer planId;
@SerializedName("plan_code")
@Expose
public String planCode;
@SerializedName("plan_title")
@Expose
public String planTitle;
@SerializedName("available_connection")
@Expose
public Integer availableConnection;
@SerializedName("add_free")
@Expose
public Integer addFree;
@SerializedName("profile_filters")
@Expose
public String profileFilters;
@SerializedName("my_profile_viewed_by_other")
@Expose
public Integer myProfileViewedByOther;
@SerializedName("upload_media_size")
@Expose
public Integer uploadMediaSize;
@SerializedName("media_unit")
@Expose
public String mediaUnit;
@SerializedName("is_media_private")
@Expose
public Integer isMediaPrivate;
@SerializedName("onetwoone_chat_history_limit")
@Expose
public Integer onetwooneChatHistoryLimit;
@SerializedName("is_profile_private_hidden")
@Expose
public Integer isProfilePrivateHidden;
@SerializedName("event_post_limit")
@Expose
public Integer eventPostLimit;
@SerializedName("chat_message_types")
@Expose
public Integer chatMessageTypes;
@SerializedName("chat_backup")
@Expose
public Integer chatBackup;
@SerializedName("media_upload_type")
@Expose
public Integer mediaUploadType;
@SerializedName("connection_type")
@Expose
public Integer connectionType;
@SerializedName("load_profiles")
@Expose
public Integer loadProfiles;
@SerializedName("bedge")
@Expose
public Integer bedge;

}