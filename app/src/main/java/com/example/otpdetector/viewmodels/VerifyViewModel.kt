package com.example.otpdetector.viewmodels

import android.app.Activity
import android.content.ContentValues
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.otpdetector.BuildConfig
import com.example.otpdetector.utils.Constants.DEBUG_TOKEN
import com.example.otpdetector.utils.PhoneAuthState
import com.example.otpdetector.utils.VerifyOtpTokenModel
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ActionCodeSettings.newBuilder
import com.google.firebase.auth.OAuthProvider.newBuilder
import com.google.firebase.auth.PhoneAuthOptions.newBuilder
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltViewModel
class VerifyViewModel@Inject constructor(private val firebaseAuth: FirebaseAuth, private val sharedPreferences: SharedPreferences):ViewModel() {

    var success  = MutableLiveData<Boolean>()

    fun signInUser(activity: Activity,code:String)
    {
        success.postValue(false)
        val modelJson = sharedPreferences.getString("model","")
        val gson = Gson()
        val model = gson.fromJson(modelJson,VerifyOtpTokenModel::class.java)
        val credential = PhoneAuthProvider.getCredential(model.verificationId,code)
        signInWithPhoneAuthCredential(credential,activity)
    }


     fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential,activity: Activity) {

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    success.postValue(true)
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("VerifyViewModel", "signInWithCredential:success")
                    val user = task.result?.user

                    Toast.makeText(activity,"OTP is verified",Toast.LENGTH_LONG).show()

                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w("VerifyViewModel", "signInWithCredential:failure", task.exception)
                    success.postValue(true)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(activity,"OTP is invalid",Toast.LENGTH_LONG).show()
                    }
                    // Update UI
                }
            }
    }




}