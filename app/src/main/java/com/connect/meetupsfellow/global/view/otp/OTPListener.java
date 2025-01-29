package com.connect.meetupsfellow.global.view.otp;

public interface OTPListener {
    /**
     * Callback Fired when user starts typing in the OTP/PIN box.
     */
    void onInteractionListener();

    /**
     * @param otp Filled OTP
     * Callback Fired when user has completed filling the OTP/PIN.
     */
    void onOTPComplete(String otp);
}
