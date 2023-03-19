package com.example.otpdetector.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.findFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.otpdetector.R
import com.example.otpdetector.utils.Constants.PHONE_NUMBER
import com.example.otpdetector.utils.PhoneAuthState
import com.example.otpdetector.viewmodels.LoginViewModel
import com.hbb20.CountryCodePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class loginFragment : Fragment(R.layout.fragment_login) {

    private val loginViewModel:LoginViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ccp = view.findViewById<CountryCodePicker>(R.id.countryCodePicker)
         val progressBar1:ProgressBar = view.findViewById(R.id.progressBar1)

        loginViewModel.phoneAuthState.observe(viewLifecycleOwner, Observer { phoneAuthState ->
            if(phoneAuthState == PhoneAuthState.Success)
            {
                progressBar1.visibility = View.INVISIBLE
                findNavController().navigate(R.id.action_loginFragment_to_verificationFragment)

            }
            else
            {
                progressBar1.visibility = View.INVISIBLE
                Log.d("LoginFragment","Error in phoneAuthState")
            }
        })

        val etPhoneNumber = view.findViewById<EditText>(R.id.etPhoneNumber)

        view.findViewById<AppCompatButton>(R.id.btnSendOtp).setOnClickListener {
            if(etPhoneNumber.text.toString().isValidPhoneNumber())
            {
                progressBar1.visibility = View.VISIBLE
                var countryCode = ccp.selectedCountryCodeWithPlus
                val phoneNumber = countryCode + etPhoneNumber.text
                PHONE_NUMBER = phoneNumber
                Log.d("phoneNumber",phoneNumber)
                loginViewModel.initiatePhoneAuth(phoneNumber,requireActivity())

            }

            else
            {
                Toast.makeText(requireContext(),"Invalid phone number",Toast.LENGTH_SHORT).show()
            }
        }

    }
    fun String.isValidPhoneNumber():Boolean{
        val regex = Regex("^\\d{10}$")
        return regex.matches(this)
    }

}