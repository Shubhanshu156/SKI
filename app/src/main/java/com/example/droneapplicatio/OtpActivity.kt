package com.example.droneapplicatio

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*


import java.lang.Exception
import java.util.concurrent.TimeUnit
import android.content.DialogInterface
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.droneapplicatio.MainActivity
import com.example.droneapplicatio.R
import com.example.droneapplicatio.SignupActivity
import kotlinx.android.synthetic.main.otp.*
import kotlin.check
class OtpActivity:AppCompatActivity() {
    lateinit var phonenumber:String
    lateinit var progressDialog: ProgressDialog
    lateinit var mCountertimer:CountDownTimer
    val TAG="I AM GERE"
    var mverificationid:String?=null
    lateinit var callbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var mresendingToken:PhoneAuthProvider.ForceResendingToken?=null

    var mAuth=FirebaseAuth.getInstance()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.otp)

        progressDialog=createProgressDialog("Sending OTP please wait",false)
        progressDialog.show()
        val intent=intent
//        intent.putExtra()
        phonenumber= intent.getStringExtra("PHONENUMBER").toString()

        verify.text="Verify $phonenumber"
        initview()
        startverify()
        check.setOnClickListener {
            Toast.makeText(this, "here", Toast.LENGTH_SHORT).show()
            val code = otp.text.toString()

            if (!code.isNullOrEmpty() && !mverificationid.isNullOrEmpty()){


                val credential=PhoneAuthProvider.getCredential(mverificationid!!,code)
                Log.d(TAG, "trying to signin: i am here")

                signInWithPhoneAuthCredential(credential)

            }
            else{Log.d(TAG, "signInWithPhoneAuthCredential: i am here2")
            }



        }

        resend.setOnClickListener  {
            if (mresendingToken!=null){
                showtimer(60000)
            }
        }
        showtimer(60000)
    }


    private fun startverify() {
        try{
            val options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phonenumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)}
        catch (e:Exception){
            AlertDialog.Builder(this)
                .setTitle("Unable To Process")
                .setMessage(e.message) // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes,
                    DialogInterface.OnClickListener { dialog, which ->
                        val i=Intent(this,MainActivity::class.java)
                        startActivity(i)
                        finish()
                        // Continue with delete operation
                    }) // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
//            Toast.makeText(this@OtpActivity, e.message, Toast.LENGTH_SHORT).show()
        }

    }

    private fun initview() {
        val spannable=SpannableString("Waiting to Automatically detect SMS Sent to $phonenumber Wrong Number?")
        val clicablespan= object : ClickableSpan() {
            /**
             * Performs the click action associated with this span.
             */
            override fun onClick(widget: View) {
                val intent=Intent(this@OtpActivity,MainActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText=false
                ds.color=ds.linkColor
            }

        }
        spannable.setSpan(clicablespan,spannable.length-13,spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        waiting.movementMethod=LinkMovementMethod.getInstance()
        waiting.setText(spannable)

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                val smscode:String?=credential.smsCode

                if (!smscode.isNullOrBlank()) {
                    if (smscode.isNotEmpty()){



                    }
                }
                progressDialog=createProgressDialog("Pleasae Wait we are Verifying",false)
                progressDialog.show()
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (progressDialog.isShowing) {
                    progressDialog.dismiss()

                }
                AlertDialog.Builder(this@OtpActivity)
                    .setTitle("Unable To Process")
                    .setMessage(e.message) // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes,
                        DialogInterface.OnClickListener { dialog, which ->
                            val i=Intent(this@OtpActivity,MainActivity::class.java)
                            startActivity(i)
                            finish()
                            // Continue with delete operation
                        }) // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
            }

            override fun onCodeSent(

                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                if (progressDialog.isShowing) {
                    progressDialog.dismiss()

                }
                Toast.makeText(this@OtpActivity, "sending messages", Toast.LENGTH_SHORT).show()
                if (progressDialog.isShowing) {progressDialog.dismiss()}
                mverificationid = verificationId
                mresendingToken = token
            }
        }

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Log.d(TAG, "signInWithPhoneAuthCredential: i am here")

        mAuth.signInWithCredential(credential).addOnSuccessListener{


            if (progressDialog.isShowing){
                progressDialog.dismiss()
            }
            val intent=Intent(this@OtpActivity, SignupActivity::class.java)

            intent.putExtra("phonenumber",intent.getStringExtra("PHONENUMBER").toString());
            startActivity(intent)
            finish()
            Toast.makeText(this@OtpActivity, "welcome you are Signed-In", Toast.LENGTH_SHORT).show()




        }.addOnFailureListener {

            if (progressDialog.isShowing){
                progressDialog.dismiss()
            }

            Toast.makeText(this@OtpActivity, it.message, Toast.LENGTH_LONG).show()
            val i=Intent(this,MainActivity::class.java)
            startActivity(i)
            finish()
        }
    }


    private fun showtimer(milisec: Long) {
        resend.isEnabled=false
        mCountertimer=object : CountDownTimer (milisec,1000){
            /**
             * Callback fired on regular interval.
             * @param millisUntilFinished The amount of time until finished.
             */
            override fun onFinish() {
                resend.isEnabled=true
                counter.isVisible=false
            }
            override fun onTick(millisUntilFinished: Long) {
                var v=millisUntilFinished/1000
                counter.isVisible=true
                counter.text="Seconds Remaining $v"
            }

            /**
             * Callback fired when the time is up.
             */



        }.start()


    }

    override fun onDestroy() {
        super.onDestroy()
        if (mCountertimer!=null){
            mCountertimer.cancel()
        }
    }
}




fun Context.createProgressDialog(message:String,isCanceable:Boolean):ProgressDialog{
    return ProgressDialog(this).apply {
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        setMessage(message)
    }

}
