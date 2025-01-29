package com.connect.meetupsfellow.global.view

import android.content.Context

class CustomLinearLayoutManager(context: Context, @androidx.recyclerview.widget.RecyclerView.Orientation orientation: Int,
                                reverseLayout: Boolean) : androidx.recyclerview.widget.LinearLayoutManager(context, orientation, reverseLayout) {

    /**
     * Disable predictive animations. There is a bug in RecyclerView which causes views that
     * are being reloaded to pull invalid ViewHolders from the internal recycler stack if the
     * adapter size has decreased since the ViewHolder was recycled.
     */
    override fun supportsPredictiveItemAnimations(): Boolean {
        return false
    }
}