package com.example.otpdetector.ui.fragments

import `in`.aabhasjindal.otptextview.OtpTextView
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.otpdetector.BroadcastReceivers.SmsBroadcastReceiver
import com.example.otpdetector.R
import com.example.otpdetector.utils.Constants.PHONE_NUMBER
import com.example.otpdetector.utils.PhoneAuthState
import com.example.otpdetector.viewmodels.LoginViewModel
import com.example.otpdetector.viewmodels.VerifyViewModel
import com.google.android.gms.auth.api.phone.SmsRetriever
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class verificationFragment : Fragment(R.layout.fragment_verification) {

    private val verifyViewModel:VerifyViewModel by viewModels()
    private val REQ_USER_CONSENT = 200

    var smsBroadcastReceiver:SmsBroadcastReceiver?=null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val progressBar2:ProgressBar = view.findViewById(R.id.progressBar2)
        val otpView = view.findViewById<OtpTextView>(R.id.otp_view)
        view.findViewById<TextView>(R.id.tvMobile).text= "OTP sent to mobile $PHONE_NUMBER"
        view.findViewById<Button>(R.id.btnVerifyOtp).setOnClickListener{
            Log.d("OtpView",otpView.otp.toString())
            if(otpView.otp.toString().isValidOTP())
            {
                verifyViewModel.signInUser(requireActivity(),otpView.otp.toString())
            }

            else
                Toast.makeText(requireContext(),"Invalid OTP",Toast.LENGTH_SHORT).show()
        }



        startSmartUserConsent()

        verifyViewModel.success.observe(viewLifecycleOwner, Observer {
            when(it)
            {
                true -> progressBar2.visibility = View.INVISIBLE
                else -> progressBar2.visibility = View.VISIBLE
            }
        })



    }

    private fun startSmartUserConsent(){
        val client = SmsRetriever.getClient(requireContext())
        client.startSmsUserConsent(null)
    }

    private fun registerBroadcastReceiver(){

        smsBroadcastReceiver = SmsBroadcastReceiver()
        smsBroadcastReceiver?.smsBroadcastReceiverListener = object : SmsBroadcastReceiver.SmsBroadcastReceiverListener{
            override fun onSuccess(intent: Intent?) {
                startActivityForResult(intent,REQ_USER_CONSENT)
            }
            override fun onFailure() {
              Log.d("verificationFragment", "Failure in registerBroadcastReceiver")
            }

        }
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        activity?.registerReceiver(smsBroadcastReceiver,intentFilter,SmsRetriever.SEND_PERMISSION,null)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQ_USER_CONSENT)
        {

            if(resultCode == RESULT_OK && data!=null)
            {
                val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)

                if (message != null) {
                    getOtpFromMessage(message)
                }
            }
        }
    }

    private fun getOtpFromMessage(message: String) {
        if (message.isNotEmpty()) {
            val otpPattern = Pattern.compile("(|^)\\d{6}")
            val matcher = otpPattern.matcher(message)

            if (matcher.find()) {
                matcher.group(0)?.let { view?.findViewById<OtpTextView>(R.id.otp_view)?.setOTP(it) }
            }
        } else {
            Log.d("verificationFragment", "Message is null")
        }
    }

    override fun onStart() {
        super.onStart()
        registerBroadcastReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(smsBroadcastReceiver)
    }

    fun String.isValidOTP():Boolean{
        val regex = Regex("^\\d{6}$")
        return regex.matches(this)
    }
}