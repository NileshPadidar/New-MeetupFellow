package com.connect.meetupsfellow.developement.components

import com.connect.meetupsfellow.developement.implementation.FirebaseUtilImpl
import com.connect.meetupsfellow.developement.modules.AppModule
import com.connect.meetupsfellow.developement.modules.NetworkModule
import com.connect.meetupsfellow.developement.modules.PrimitivesModule
import com.connect.meetupsfellow.developement.modules.UtilityModule
import com.connect.meetupsfellow.firebase.NotificationService
import com.connect.meetupsfellow.global.model.FirebaseModel
import com.connect.meetupsfellow.global.utils.MediaChatData
import com.connect.meetupsfellow.global.view.UploadChatMediaOnFireabase
import com.connect.meetupsfellow.mvp.model.activity.*
import com.connect.meetupsfellow.mvp.model.fragment.FavoriteEventModel
import com.connect.meetupsfellow.mvp.model.fragment.FavoriteUserModel
import com.connect.meetupsfellow.mvp.model.model.UpdateLocationModel
import com.connect.meetupsfellow.mvp.presenter.activity.*
import com.connect.meetupsfellow.mvp.presenter.fragment.FavoriteEventPresenter
import com.connect.meetupsfellow.mvp.presenter.fragment.FavoriteUserPresenter
import com.connect.meetupsfellow.mvp.presenter.model.LocationPresenter
import com.connect.meetupsfellow.mvp.view.activities.*
import com.connect.meetupsfellow.mvp.view.adapter.ChatAdapter
import com.connect.meetupsfellow.mvp.view.adapter.ConversationAdapter
import com.connect.meetupsfellow.mvp.view.adapter.MostRecentlyJoinedUserAdapter
import com.connect.meetupsfellow.mvp.view.adapter.NearByAdapter
import com.connect.meetupsfellow.mvp.view.dialog.AlertBuyPremium
import com.connect.meetupsfellow.mvp.view.model.LocationModel
import com.connect.meetupsfellow.mvp.view.model.SkuDetail
import com.connect.meetupsfellow.mvp.view.viewholder.*
import com.connect.meetupsfellow.receiver.ConnectionReceiver
import com.connect.meetupsfellow.service.LocationUpdatesService
import com.connect.meetupsfellow.service.StickyService
import dagger.Component
import javax.inject.Singleton
import com.connect.meetupsfellow.mvp.presenter.model.UserPaymentPresenter
import com.connect.meetupsfellow.mvp.model.model.UpdateUserPaymentModel
import com.connect.meetupsfellow.mvp.view.fragment.*

/**
 * Created by Jammwal on 08-02-2018.
 */
@Singleton
@Component(modules = [(AppModule::class), (NetworkModule::class), (PrimitivesModule::class), (UtilityModule::class)])
interface ApplicationComponent {

    fun inject(locationUpdatesService: LocationUpdatesService)
    fun inject(notificationFragment: NotificationFragment)
    fun inject(notificationService: NotificationService)
    fun inject(loginModel: LoginModel)
    fun inject(loginPresenter: LoginPresenter)
    fun inject(loginActivity: LoginActivity)
    fun inject(createProfileActivityNew: CreateProfileActivityNew)
    fun inject(splashActivity: SplashActivity)
    fun inject(groupChatActivity: GroupChatActivity)
    fun inject(createProfileModel: CreateProfileModel)
    fun inject(createProfilePresenter: CreateProfilePresenter)
    fun inject(homeActivity: HomeActivity)
    fun inject(settingsActivity: SettingsActivity)
    fun inject(searchByLocationActivity: SearchByLocationActivity)
    fun inject(searchActivity: SearchActivity)
    fun inject(homeModel: HomeModel)
    fun inject(homePresenter: HomePresenter)
    fun inject(interestedPeopleModel: InterestedPeopleModel)
    fun inject(interestedPeoplePresenter: InterestedPeoplePresenter)
    fun inject(interestedPeopleActivity: InterestedPeopleActivity)
    fun inject(profileActivity: ProfileActivity)
    fun inject(eventDetailModel: EventDetailModel)
    fun inject(callHistoryFragment: CallHistoryFragment)
    fun inject(eventDetailPresenter: EventDetailPresenter)
    fun inject(interestedPeopleViewHolder: InterestedPeopleViewHolder)
    fun inject(userProfileModel: UserProfileModel)
    fun inject(userProfilePresenter: UserProfilePresenter)
    fun inject(recentUsersFragment: RecentUsersFragment)
    fun inject(userProfileActivity: UserProfileActivity)
    fun inject(eventDetailsActivity: EventDetailsActivity)
    fun inject(newGroupActivity: NewGroupActivity)
    fun inject(personalChatFragment: PersonalChatFragment)
    fun inject(settingsModel: SettingsModel)
    fun inject(settingsPresenter: SettingsPresenter)
    fun inject(createEventModel: CreateEventModel)
    fun inject(createEventPresenter: CreateEventPresenter)
    fun inject(groupChatDetailsActivity: GroupChatDetailsActivity)
    fun inject(locationModel: LocationModel)
    fun inject(updateLocationModel: UpdateLocationModel)
    fun inject(locationPresenter: LocationPresenter)
    fun inject(editProfileModel: EditProfileModel)
    fun inject(editProfilePresenter: EditProfilePresenter)
    fun inject(favoriteUserModel: FavoriteUserModel)
    fun inject(favoriteEventModel: FavoriteEventModel)
    fun inject(favoriteUserPresenter: FavoriteUserPresenter)
    fun inject(addNewGroupMembersActivity: AddNewGroupMembersActivity)
    fun inject(favoriteEventPresenter: FavoriteEventPresenter)
    fun inject(favoritePeopleViewHolder: FavoritePeopleViewHolder)
    fun inject(favoriteUserFragment: FavoriteUserFragment)
    fun inject(changeNumberActivity: ChangeNumberActivity)
    fun inject(changeNumberPresenter: ChangeNumberPresenter)
    fun inject(changeNumberModel: ChangeNumberModel)
    fun inject(addGroupMembersActivity: AddGroupMembersActivity)
    fun inject(imagePagerActivity: ImagePagerActivity)
    fun inject(profileModel: ProfileModel)
    fun inject(profilePresenter: ProfilePresenter)
    fun inject(searchModel: SearchModel)
    fun inject(searchPresenter: SearchPresenter)
    fun inject(searchItemViewHolder: SearchItemViewHolder)
    fun inject(eventFeedsViewHolder: EventFeedsViewHolder)
    fun inject(favoriteUserEvent: FavoriteUserEvent)
    fun inject(favoriteEventViewHolder: FavoriteEventViewHolder)
    fun inject(favoriteEventFragment: FavoriteEventFragment)
    fun inject(chatActivity: ChatActivity)
    fun inject(profileFragment: ProfileFragment)
    fun inject(conversationViewHolder: ConversationViewHolder)
    fun inject(chatViewHolder: ChatViewHolder)
    fun inject(firebaseModel: FirebaseModel)
    fun inject(privateAccessListActivity: PrivateAccessListActivity)
    fun inject(chatAdapter: ChatAdapter)
    fun inject(nearByViewHolder: NearByViewHolder)
    fun inject(uploadChatMediaOnFireabase: UploadChatMediaOnFireabase)
    fun inject(blockedUsersActivity: BlockedUsersActivity)
    fun inject(blockedUsersPresenter: BlockedUsersPresenter)
    fun inject(conversationAdapter: ConversationAdapter)
    fun inject(chatPresenter: ChatPresenter)
    fun inject(blockedUserModel: BlockedUserModel)
    fun inject(connectionRequestModel: ConnectionRequestModel)
    fun inject(connectionRequestPresenter: ConnectionRequestPresenter)
    fun inject(chatModel: ChatModel)
    fun inject(notificationPresenter: NotificationPresenter)
    fun inject(nearByAdapter: NearByAdapter)
    fun inject(verifyPinActivity: VerifyPinActivity)
    fun inject(stickyService: StickyService)
    fun inject(firebaseUtilImpl: FirebaseUtilImpl)
    fun inject(connectionReceiver: ConnectionReceiver)
    fun inject(notificationActivity: NotificationActivity)
    fun inject(notificationSettingActivity: NotificationSettingActivity)
    fun inject(skuDetail: SkuDetail)
    fun inject(alertBuyPremium: AlertBuyPremium)
    fun inject(privateAccessModel: PrivateAccessModel)
    fun inject(privateAccessPresenter: PrivateAccessPresenter)
    fun inject(privateAlbumModel: PrivateAlbumImagesModel)
    fun inject(groupChatFragment: GroupChatFragment)
    fun inject(privateAlbumImagesPresenter: PrivateAlbumImagesPresenter)
    fun inject(mediaChatData: MediaChatData)
    fun inject(privateAlbumModel: PrivateAlbumModel)
    fun inject(privateAlbumPresenter: PrivateAlbumPresenter)
    fun inject(allUsersFragment: AllUsersFragment)
    fun inject(privateAlbumViewHolder: PrivateAlbumViewHolder)
    fun inject(webViewActivity: WebViewActivity)
    fun inject(mostRecentlyJoinedViewHolder: MostRecentlyJoinedViewHolder)
    fun inject(F: MostRecentlyJoinedUserAdapter)
    fun inject(userPaymentPresenter: UserPaymentPresenter)
    fun inject(updateUserPaymentModel: UpdateUserPaymentModel)
    fun inject(createEventActivity: CreateEventActivity)
    fun inject(privateAlbumActivity: PrivateAlbumActivity)
    fun inject(privateAccessActivity: PrivateAccessActivity)
    fun inject(adminAdsViewHolder: AdminAdsViewHolder)
    fun inject(connectRequestActivity: ConnectRequestActivity)
    fun inject(getConnectionRequestFragment: GetConnectionRequestFragment)
    fun inject(sendConnectionRequestFragment: SendConnectionRequestFragment)
    fun inject(myConnectionsFragment: MyConnectionsFragment)
    fun inject(followingActivity: FollowFollowingActivity)
    fun inject(followerFragment: FollowerFragment)
    fun inject(followingFragment: FollowingFragment)
    fun inject(likeUserListFragment: LikeUserListFragment)
    fun inject(forgotPasswordActivity: ForgotPasswordActivity)
    fun inject(resetPasswordActivity: ResetPasswordActivity)
    fun inject(loginWithEmailActivity: LoginWithEmailActivity)
     fun inject(transactionHistoryActivity: TransactionHistoryActivity)
    fun inject(multiUserViewLikeActivity: MultiUserViewLikeActivity)

    //  fun inject(conversationViewModel: ConversationViewHolder)



}
