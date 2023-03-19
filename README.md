# OTPdetector

The application detects OTP and verifies it for a given number.

### Features
- Enter mobile number and select country code.
- Automatic detection of OTP using broadcast receiver
- Notification of verified OTP status through a toast message

### Technologies Used
- Android 
- Kotlin
- MVVM architecture
- Navigation Components
- Firebase
- Shared Prefrence
- Dagger-Hilt 
- Broadcast Receiver

### Libraries Used 
- aabhasr1/OtpView - To implement otp verification screen view.
- hbb20/CountryCodePickerProject - Country code picker to implement dialog for choosing country code.
- Firebase PhoneAuth - To implement phone authentication with OTP

### Limitations
- After clicking the continue button the screen redirects to reCaptcha verification
 as the Android Device Verification API is deprecated and now we have to use Play Integrity API.
 To skip the reCaptcha we have to upload the app in the google cloud console.
- When the OTP is verified then if we again try to verify the same OTP with the same phone number it will show that the OTP is 
 invalid , as after successfull verification the sms code(OTP) gets expired.


### Screenshots

#### Login Screen
<img src="https://res.cloudinary.com/dixttklud/image/upload/v1679234960/Otpdetector/Screenshot_20230319-173710_OTPdetector_gxp5qi.jpg" width = 20% height = 20%>

#### Country Code picker Dialog
<img src="https://res.cloudinary.com/dixttklud/image/upload/v1679234960/Otpdetector/Screenshot_20230319-173719_OTPdetector_pcnvz7.jpg" width = 20% height = 20%>

#### OTP verification screen
<img src="https://res.cloudinary.com/dixttklud/image/upload/v1679234960/Otpdetector/Screenshot_20230319-174655_OTPdetector_et5a5z.jpg" width = 20% height = 20%>
