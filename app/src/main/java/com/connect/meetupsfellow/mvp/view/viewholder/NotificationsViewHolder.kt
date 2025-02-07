package com.connect.meetupsfellow.mvp.view.viewholder

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.connect.meetupsfellow.R
import com.connect.meetupsfellow.constants.Constants
import com.connect.meetupsfellow.constants.ItemClickStatus
import com.connect.meetupsfellow.databinding.ItemNotificationBinding
import com.connect.meetupsfellow.global.utils.RecyclerViewClick
import com.connect.meetupsfellow.retrofit.response.ResponseNotifications
import com.daimajia.swipe.SwipeLayout
import com.google.gson.Gson

@Suppress("SENSELESS_COMPARISON")
class NotificationsViewHolder(private val binding: ItemNotificationBinding) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.includeNotification.llNotification.setOnClickListener {
            Log.e("Notification*&", "Bind_Click: ${(responseNotifications.type)}")
            when (responseNotifications.type) {
                Constants.Notification.NewUser, Constants.Notification.Connection, Constants.Notification.BirthdayWish, Constants.Notification.EventLike -> {
                    itemView.context.startActivity(
                        Intent(Constants.Intent.ProfileUser).putExtras(Bundle().apply {
                            putInt(
                                Constants.IntentDataKeys.UserId, responseNotifications.pushMgsId
                            )
                        })
                    )
                }

                Constants.Notification.User_Viewed_profile -> {
                    if (responseNotifications.is_multiple_visitor == 1) {
                        itemView.context.startActivity(
                            Intent(Constants.Intent.MultiUserViewLike).putExtras(Bundle().apply {
                                putInt(
                                    Constants.IntentDataKeys.NotificationId,
                                    responseNotifications.notificationId
                                )
                                putString(
                                    Constants.IntentDataKeys.Type,
                                    Constants.Notification.User_Viewed_profile
                                )
                            })
                        )
                    } else {
                        if (proMembership && plan_id != 2) {
                            itemView.context.startActivity(
                                Intent(Constants.Intent.ProfileUser).putExtras(Bundle().apply {
                                    putInt(
                                        Constants.IntentDataKeys.UserId,
                                        responseNotifications.pushMgsId
                                    )
                                })
                            )
                        }
                    }
                }

                Constants.Notification.Favourite -> {
                    if (responseNotifications.is_multiple_profile_favourite == 1) {
                        itemView.context.startActivity(
                            Intent(Constants.Intent.MultiUserViewLike).putExtras(Bundle().apply {
                                putInt(
                                    Constants.IntentDataKeys.NotificationId,
                                    responseNotifications.notificationId
                                )
                                putString(
                                    Constants.IntentDataKeys.Type,
                                    Constants.Notification.Favourite
                                )
                            })
                        )
                    } else {
                        itemView.context.startActivity(
                            Intent(Constants.Intent.ProfileUser).putExtras(Bundle().apply {
                                putInt(
                                    Constants.IntentDataKeys.UserId,
                                    responseNotifications.pushMgsId
                                )
                            })
                        )
                    }
                }

                Constants.Notification.ProfileLike -> {
                    if (responseNotifications.is_multiple_profile_like == 1) {
                        itemView.context.startActivity(
                            Intent(Constants.Intent.MultiUserViewLike).putExtras(Bundle().apply {
                                putInt(
                                    Constants.IntentDataKeys.NotificationId,
                                    responseNotifications.notificationId
                                )
                                putString(
                                    Constants.IntentDataKeys.Type,
                                    Constants.Notification.ProfileLike
                                )
                            })
                        )
                    } else {

                        itemView.context.startActivity(
                            Intent(Constants.Intent.ProfileUser).putExtras(Bundle().apply {
                                putInt(
                                    Constants.IntentDataKeys.UserId,
                                    responseNotifications.pushMgsId
                                )
                            })
                        )
                    }
                }

                Constants.Notification.Following -> {
                    Log.e("MultiUserActivity", "IS-MUlti_Following^@% : ${responseNotifications.is_multiple_following}")
                    if (responseNotifications.is_multiple_following == 1) {
                        itemView.context.startActivity(
                            Intent(Constants.Intent.MultiUserViewLike).putExtras(Bundle().apply {
                                putInt(
                                    Constants.IntentDataKeys.NotificationId,
                                    responseNotifications.notificationId
                                )
                                putString(
                                    Constants.IntentDataKeys.Type,
                                    Constants.Notification.Following
                                )
                            })
                        )
                    } else {
                        itemView.context.startActivity(
                            Intent(Constants.Intent.ProfileUser).putExtras(Bundle().apply {
                                putInt(
                                    Constants.IntentDataKeys.UserId, responseNotifications.pushMgsId
                                )
                            })
                        )
                    }
                }

                Constants.Notification.eventInterest -> {
                    Log.e("MultiUserActivity", "IS-MUlti_Following^@% : ${responseNotifications.is_multiple_event_interest}")
                    if (responseNotifications.is_multiple_event_interest == 1) {
                       /* itemView.context.startActivity(
                            Intent(Constants.Intent.MultiUserViewLike).putExtras(Bundle().apply {
                                putInt(
                                    Constants.IntentDataKeys.NotificationId,
                                    responseNotifications.notificationId
                                )
                                putString(
                                    Constants.IntentDataKeys.Type,
                                    Constants.Notification.eventInterest
                                )
                            })
                        )*/
                        itemView.context.startActivity(
                            Intent(Constants.Intent.EventDetails).putExtras(Bundle().apply {
                                putInt(
                                    Constants.IntentDataKeys.EventId,
                                    responseNotifications.eventId
                                )
                            })
                        )
                    } else {
                        itemView.context.startActivity(
                            Intent(Constants.Intent.ProfileUser).putExtras(Bundle().apply {
                                putInt(
                                    Constants.IntentDataKeys.UserId, responseNotifications.pushMgsId
                                )
                            })
                        )
                    }
                }

                Constants.Notification.EventLike -> {
                    Log.e("MultiUserActivity", "IS-MUlti_EventLike^@% : ${responseNotifications.is_multiple_event_like}")
                    if (responseNotifications.is_multiple_event_like == 1) {
                        itemView.context.startActivity(
                            Intent(Constants.Intent.MultiUserViewLike).putExtras(Bundle().apply {
                                putInt(
                                    Constants.IntentDataKeys.NotificationId,
                                    responseNotifications.notificationId
                                )
                                putString(
                                    Constants.IntentDataKeys.Type,
                                    Constants.Notification.EventLike
                                )
                            })
                        )
                    } else {
                        itemView.context.startActivity(
                            Intent(Constants.Intent.ProfileUser).putExtras(Bundle().apply {
                                putInt(
                                    Constants.IntentDataKeys.UserId, responseNotifications.pushMgsId
                                )
                            })
                        )
                    }
                }

                Constants.Notification.NewEvent, Constants.Notification.EventStatusApproved, Constants.Notification.EventApproved, Constants.Notification.EventUnblock -> {
                    itemView.context.startActivity(
                        Intent(Constants.Intent.EventDetails).putExtras(Bundle().apply {
                            putInt(
                                Constants.IntentDataKeys.EventId,
                                responseNotifications.pushMgsId
                            )
                        })
                    )
                }

                Constants.Notification.Chat -> {
                    if (null == responseNotifications.otherSide) return@setOnClickListener
                    itemView.context.startActivity(Intent(Constants.Intent.Chat).putExtras(Bundle().apply {
                        putString(
                            Constants.IntentDataKeys.Conversation,
                            Gson().toJson(responseNotifications.otherSide)
                        )
                        putString(
                            Constants.IntentDataKeys.UserImage, responseNotifications.imagePath
                        )
                        putInt(
                            Constants.IntentDataKeys.Status,
                            if (responseNotifications.otherSide.otherUserOnline) 1 else 0
                        )
                    }))
                }

                Constants.Notification.PostStatus, Constants.Notification.PostMention, Constants.Notification.PostNewComment, Constants.Notification.PostMentionComment, Constants.Notification.PostNewLike, Constants.Notification.PostNewUnlike, Constants.Notification.LikeComment, Constants.Notification.UnlikeComment -> {
                    itemView.context.startActivity(Intent(Constants.Intent.Home).putExtras(Bundle().apply {
                        putString(
                            Constants.IntentDataKeys.TimeLinePushId,
                            responseNotifications.pushMgsId.toString()
                        )
                        putString(Constants.IntentDataKeys.TimeLinePost, responseNotifications.type)
                    }))
                }

                Constants.Notification.Admin_Notify ->{
                    itemView.context.startActivity(Intent(Constants.Intent.Webview).putExtras(Bundle().apply {
                        putString(
                            Constants.IntentDataKeys.TITLE,
                            itemView.context.getString(R.string.label_meetups_admin)
                        )
                        putString(
                            Constants.IntentDataKeys.LINK,
                            responseNotifications.notification_web_url
                        )
                    }))
                }

                Constants.Notification.PrivateAlbumShare -> {
                    itemView.context.startActivity(
                        Intent(Constants.Intent.ProfileUser).putExtras(Bundle().apply {
                            putInt(
                                Constants.IntentDataKeys.UserId, responseNotifications.pushMgsId
                            )
                        })
                    )/*itemView.context.startActivity(
                            Intent(Constants.Intent.PrivateAlbumImages).putExtras(
                                Bundle().apply {
                                    putString(
                                        Constants.IntentDataKeys.UserId,
                                        "${responseNotifications.pushMgsId}"
                                    )
                                    putString(
                                        Constants.IntentDataKeys.Type,
                                        "${Constants.ImagePicker.View}"
                                    )
                                })
                        )*/
                }
            }

            if (RecyclerViewClick.onClickListener == null) return@setOnClickListener

            when (responseNotifications.readMgs) {
                Constants.NotificationStatus.UnRead -> RecyclerViewClick.onClickListener!!.onItemClick(
                    adapterPosition, ItemClickStatus.Unread
                )
            }
        }

        binding.ivDelete.setOnClickListener {
            if (null != RecyclerViewClick.onClickListener) {
                RecyclerViewClick.onClickListener!!.onItemClick(
                    adapterPosition, ItemClickStatus.Delete
                )
                binding.swipe.close(true, true)
            }
        }

        binding.includeNotification.ivNotificationImage.setOnClickListener {
            Log.e("Notification*&", "Bind_Click22: ${(responseNotifications.type)}")
            when (responseNotifications.type) {
                Constants.Notification.NewUser, Constants.Notification.Connection, Constants.Notification.EventLike, Constants.Notification.BirthdayWish, Constants.Notification.PrivateAlbumShare -> {
                    itemView.context.startActivity(
                        Intent(Constants.Intent.ProfileUser).putExtras(Bundle().apply {
                            putInt(
                                Constants.IntentDataKeys.UserId, responseNotifications.pushMgsId
                            )
                        })
                    )
                }

                Constants.Notification.Admin_Notify ->{
                    itemView.context.startActivity(Intent(Constants.Intent.Webview).putExtras(Bundle().apply {
                        putString(
                            Constants.IntentDataKeys.TITLE,
                            itemView.context.getString(R.string.label_meetups_admin)
                        )
                        putString(
                            Constants.IntentDataKeys.LINK,
                            responseNotifications.notification_web_url
                        )
                    }))

                }

                Constants.Notification.NewEvent, Constants.Notification.EventStatusApproved, Constants.Notification.EventApproved, Constants.Notification.EventUnblock -> {
                    itemView.context.startActivity(
                        Intent(Constants.Intent.EventDetails).putExtras(Bundle().apply {
                            putInt(
                                Constants.IntentDataKeys.EventId,
                                responseNotifications.pushMgsId
                            )
                        })
                    )
                }

                Constants.Notification.User_Viewed_profile -> {
                    if (responseNotifications.is_multiple_visitor == 1) {
                        itemView.context.startActivity(
                            Intent(Constants.Intent.MultiUserViewLike).putExtras(Bundle().apply {
                                putInt(
                                    Constants.IntentDataKeys.NotificationId,
                                    responseNotifications.notificationId
                                )
                                putString(
                                    Constants.IntentDataKeys.Type,
                                    Constants.Notification.User_Viewed_profile
                                )
                            })
                        )
                    } else {
                        if (proMembership && plan_id != 2) {
                            itemView.context.startActivity(
                                Intent(Constants.Intent.ProfileUser).putExtras(Bundle().apply {
                                    putInt(
                                        Constants.IntentDataKeys.UserId,
                                        responseNotifications.pushMgsId
                                    )
                                })
                            )
                        }
                    }
                }

                Constants.Notification.ProfileLike -> {
                    if (responseNotifications.is_multiple_profile_like == 1) {
                        itemView.context.startActivity(
                            Intent(Constants.Intent.MultiUserViewLike).putExtras(Bundle().apply {
                                putInt(
                                    Constants.IntentDataKeys.NotificationId,
                                    responseNotifications.notificationId
                                )
                                putString(
                                    Constants.IntentDataKeys.Type,
                                    Constants.Notification.ProfileLike
                                )
                            })
                        )
                    } else {
                        itemView.context.startActivity(
                            Intent(Constants.Intent.ProfileUser).putExtras(Bundle().apply {
                                putInt(
                                    Constants.IntentDataKeys.UserId,
                                    responseNotifications.pushMgsId
                                )
                            })
                        )
                    }
                }

                Constants.Notification.Following -> {
                    Log.e("MultiUserActivity", "IS-MUlti_Following : ${responseNotifications.is_multiple_following}")
                    if (responseNotifications.is_multiple_following == 1) {
                        itemView.context.startActivity(
                            Intent(Constants.Intent.MultiUserViewLike).putExtras(Bundle().apply {
                                putInt(
                                    Constants.IntentDataKeys.NotificationId,
                                    responseNotifications.notificationId
                                )
                                putString(
                                    Constants.IntentDataKeys.Type,
                                    Constants.Notification.Following
                                )
                            })
                        )
                    } else {
                        itemView.context.startActivity(
                            Intent(Constants.Intent.ProfileUser).putExtras(Bundle().apply {
                                putInt(
                                    Constants.IntentDataKeys.UserId, responseNotifications.pushMgsId
                                )
                            })
                        )
                    }
                }

                Constants.Notification.eventInterest -> {
                    Log.e("MultiUserActivity", "IS-MUlti_Following^@% : ${responseNotifications.is_multiple_event_interest}")
                    if (responseNotifications.is_multiple_event_interest == 1) {
                       /* itemView.context.startActivity(
                            Intent(Constants.Intent.MultiUserViewLike).putExtras(Bundle().apply {
                                putInt(
                                    Constants.IntentDataKeys.NotificationId,
                                    responseNotifications.notificationId
                                )
                                putString(
                                    Constants.IntentDataKeys.Type,
                                    Constants.Notification.eventInterest
                                )
                            })
                        )*/
                        itemView.context.startActivity(
                            Intent(Constants.Intent.EventDetails).putExtras(Bundle().apply {
                                putInt(
                                    Constants.IntentDataKeys.EventId,
                                    responseNotifications.eventId
                                )
                            })
                        )
                    } else {
                        itemView.context.startActivity(
                            Intent(Constants.Intent.ProfileUser).putExtras(Bundle().apply {
                                putInt(
                                    Constants.IntentDataKeys.UserId, responseNotifications.pushMgsId
                                )
                            })
                        )
                    }
                }

                Constants.Notification.EventLike -> {
                    Log.e("MultiUserActivity", "IS-MUlti_EventLike^@% : ${responseNotifications.is_multiple_event_like}")
                    if (responseNotifications.is_multiple_event_like == 1) {
                        itemView.context.startActivity(
                            Intent(Constants.Intent.MultiUserViewLike).putExtras(Bundle().apply {
                                putInt(
                                    Constants.IntentDataKeys.NotificationId,
                                    responseNotifications.notificationId
                                )
                                putString(
                                    Constants.IntentDataKeys.Type,
                                    Constants.Notification.EventLike
                                )
                            })
                        )
                    } else {
                        itemView.context.startActivity(
                            Intent(Constants.Intent.ProfileUser).putExtras(Bundle().apply {
                                putInt(
                                    Constants.IntentDataKeys.UserId, responseNotifications.pushMgsId
                                )
                            })
                        )
                    }
                }

                Constants.Notification.Favourite -> {
                    if (responseNotifications.is_multiple_profile_favourite == 1) {
                        itemView.context.startActivity(
                            Intent(Constants.Intent.MultiUserViewLike).putExtras(Bundle().apply {
                                putInt(
                                    Constants.IntentDataKeys.NotificationId,
                                    responseNotifications.notificationId
                                )
                                putString(
                                    Constants.IntentDataKeys.Type,
                                    Constants.Notification.Favourite
                                )
                            })
                        )
                    } else {
                        itemView.context.startActivity(
                            Intent(Constants.Intent.ProfileUser).putExtras(Bundle().apply {
                                putInt(
                                    Constants.IntentDataKeys.UserId,
                                    responseNotifications.pushMgsId
                                )
                            })
                        )
                    }
                }

            }

        }
    }

    private lateinit var responseNotifications: ResponseNotifications
    private var proMembership: Boolean = false
    private var plan_id: Int = 2

    internal fun bind(
        responseNotifications: ResponseNotifications, proMembership: Boolean, plan_id: Int
    ) {
        this.responseNotifications = responseNotifications
        this.proMembership = proMembership

        Log.e("Notification*&", "Date: ${responseNotifications.created_at}")
        binding.swipe.showMode = SwipeLayout.ShowMode.PullOut
        binding.swipe.isRightSwipeEnabled = true
        binding.swipe.isClickToClose = true

        binding.includeNotification.tvNotificationMessage.text =
            responseNotifications.message.trim()
        binding.includeNotification.tvNotificationTime.text = responseNotifications.created_at

        /* itemView.ivUnread.isSelected =
             responseNotifications.readMgs == Constants.NotificationStatus.UnRead*/

        if (responseNotifications.readMgs == 0) {
            binding.includeNotification.ivUnread.isSelected = true
            val customFontBold: Typeface? = ResourcesCompat.getFont(
                binding.includeNotification.tvNotificationMessage.context,
                R.font.montserrat_semibold
            )
            binding.includeNotification.tvNotificationMessage.typeface = customFontBold
        } else {
            binding.includeNotification.ivUnread.isSelected = false
            val customFontRegular: Typeface? = ResourcesCompat.getFont(
                binding.includeNotification.tvNotificationMessage.context,
                R.font.montserrat_regular
            )
            binding.includeNotification.tvNotificationMessage.typeface = customFontRegular
        }

        showCircleImage(
            binding.includeNotification.ivNotificationImage,
            responseNotifications.imagePath
        )
    }

    private fun showCircleImage(imageView: ImageView, imageUrl: String) {

        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(12))

        Glide.with(itemView.context).load(imageUrl)
            .placeholder(R.drawable.meetupsfellow_transpatent).apply(requestOptions).into(imageView)


        /*  DisplayImage.with(itemView.context)
              .load(imageUrl)
              .placeholder(R.drawable.meetupsfellow_transpatent)
              .shape(DisplayImage.Shape.CropSquare)
              .into(imageView)
              .build()*/
    }
}