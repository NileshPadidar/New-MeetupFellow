package com.connect.meetupsfellow.mvp.connector.fragment

import com.connect.meetupsfellow.constants.ConstantsApi
import com.connect.meetupsfellow.mvp.connector.reusable.ReusableRetrofitConnector
import com.connect.meetupsfellow.retrofit.request.RequestFeed
import com.connect.meetupsfellow.retrofit.response.CommonResponse

interface FavoriteEventConnector {

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
        fun addFavoriteEventObject(requestFeed: RequestFeed)
        fun addLikeEventObject(eventId: String)
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
    interface ModelOps
}