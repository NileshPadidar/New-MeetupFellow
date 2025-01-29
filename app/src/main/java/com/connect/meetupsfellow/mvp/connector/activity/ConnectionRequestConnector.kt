package com.connect.meetupsfellow.mvp.connector.activity

import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.mvp.connector.reusable.ReusableRetrofitConnector
import com.connect.meetupsfellow.retrofit.request.CancelSendConnectionRequest
import com.connect.meetupsfellow.retrofit.request.RequestConnectionRequest
import com.connect.meetupsfellow.retrofit.request.UnfriendConnectionReq
import com.connect.meetupsfellow.retrofit.request.getConectionRequest
import com.connect.meetupsfellow.retrofit.response.CommonResponse

interface ConnectionRequestConnector {


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
       /// fun addBlockUserObject(requestUserBlock: RequestUserBlock)
        fun addConnectionRequestObject(connectionRequest: RequestConnectionRequest)
        fun cancelConnectionRequestObject(cancelSendConnectionRequest: CancelSendConnectionRequest)
        fun getConnectionRequestObject(getConnectionRequest: getConectionRequest)
        fun unfriendConnectionRequestObject(unfriendConnectionReq: UnfriendConnectionReq)
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
      ///  fun unBlockUser(userId: String)
        fun acceptConnectionRequest(userId: Int)
        fun declineConnectionRequest(userId: Int)
    }
}