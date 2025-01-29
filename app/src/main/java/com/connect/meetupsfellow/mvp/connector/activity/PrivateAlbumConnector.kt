package com.connect.meetupsfellow.mvp.connector.activity

import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.mvp.connector.reusable.ReusableRetrofitConnector
import com.connect.meetupsfellow.mvp.view.model.PrivateAlbumModel
import com.connect.meetupsfellow.retrofit.request.ReqPrivetUserProfile
import com.connect.meetupsfellow.retrofit.request.setAccessPrivateAlbumPermissionReq
import com.connect.meetupsfellow.retrofit.response.CommonResponse

interface PrivateAlbumConnector {

    /**
     * View mandatory methods. Available to Presenter
     * Presenter -> View
     */
    interface RequiredViewOps {
        fun showToast(error: String, logout: Boolean?)
        fun showResponse(response: CommonResponse, type: ConstantsApi)
    }

    /**
     * Operations offered from Presenter to View
     * View -> Presenter
     */
    interface PresenterOps : ReusableRetrofitConnector {
        fun addObjectForPrivateAlbum(id: String)
        fun addObjectForDeleteImage(id: Int)
        fun addObjectForPrivetProfile(reqPrivetUserProfile: ReqPrivetUserProfile)
        fun addObjectPrivateAlbum(privateAlbumModel: ArrayList<PrivateAlbumModel>)
        fun addObjectSetMyPrivateAlbum(setAccessPrivateAlbumPermissionReq: setAccessPrivateAlbumPermissionReq)
    }

    /**
     * Operations offered from Presenter to Model
     * Model -> Presenter
     */
    interface RequiredPresenterOps

    /**
     * Model operations offered to Presenter
     * Presenter -> Model
     */
    interface ModelOps {
        fun logoutUser()
        fun unBlockUser(userId: String)
    }
}