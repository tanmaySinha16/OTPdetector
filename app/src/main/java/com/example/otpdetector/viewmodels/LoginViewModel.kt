package com.example.otpdetector.viewmodels

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.otpdetector.utils.PhoneAuthState
import com.example.otpdetector.utils.VerifyOtpTokenModel
import com.google.firebase.BuildConfig
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltViewModel
class LoginViewModel
         @Inject constructor(private val firebaseAuth: FirebaseAuth,
                                 private val sharedPref : SharedPreferences):ViewModel() {


    private lateinit var callbacks1: PhoneAuthProvider.OnVerificationStateChangedCallbacks


    private val _phoneAuthState = MutableLiveData<PhoneAuthState>()
    val phoneAuthState: LiveData<PhoneAuthState> = _phoneAuthState



    init {
        verifyPhoneNumberCallback()
    }

    private fun verifyPhoneNumberCallback() {
        callbacks1 = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                _phoneAuthState.value = PhoneAuthState.Success
                Log.d("LoginFragment", "onCodeSent:$verificationId")
                val model = VerifyOtpTokenModel()
                model.token = token
                model.verificationId = verificationId

                val gson = Gson()
                val modelJson = gson.toJson(model)

                sharedPref.edit()
                    .putString("model", modelJson)
                    .putString("verificationId", verificationId)
                    .apply()
            }
        }
    }

    fun initiatePhoneAuth(
        phoneNumber: String,
        activity: Activity
    ) {

        val options =
            PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(0L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks1)
                .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }



}


