package com.example.otpdetector.utils

import com.google.firebase.auth.PhoneAuthProvider

class VerifyOtpTokenModel() {
    var verificationId:String = ""
    var token : PhoneAuthProvider.ForceResendingToken? = null
}