package com.example.otpdetector.utils

import com.google.firebase.auth.PhoneAuthProvider

sealed class PhoneAuthState {
    class CodeSent(verificationId : String , token : PhoneAuthProvider.ForceResendingToken) : PhoneAuthState()
    object Success : PhoneAuthState()
    data class Error(val exception: Exception) : PhoneAuthState()
}
