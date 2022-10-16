package com.example.droneapplicatio
//import com.
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.example.droneapplicatio.OtpActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var phonenumber:String
    private lateinit var countrycode:String
    override fun onCreate(savedInstanceState: Bundle?) {
        val mAuth=FirebaseAuth.getInstance()
        if (mAuth.currentUser!=null){
            val intent=Intent(this@MainActivity,MainScreen::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)


        }

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        phnenumber.addTextChangedListener {
            nextbtn.isEnabled=(it!!.length==10)
            phonenumber=it.toString()
        }
        nextbtn.setOnClickListener {


            checknumber()
        }
    }
    private fun checknumber() {
        countrycode=ccp.selectedCountryCodeWithPlus
        phonenumber=countrycode+phnenumber.text.toString()
        notfyuser()
    }
    private fun notfyuser() {
        MaterialAlertDialogBuilder(this).apply {
            setMessage("You entered the phone number:\n\n$phonenumber \n Is this OK,or would you like to edit the phone number")
            setPositiveButton("OK"){dialogInterface, which ->
                showotpactivity()
            }
            setNegativeButton("Edit"){dialogInterface, which ->
                dialogInterface.dismiss()

            }
            setCancelable(false)
            create()
            show()
        }
    }
    private fun showotpactivity() {
        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)

        val myEdit = sharedPreferences.edit()
        myEdit.putString("number", phonenumber)
        myEdit.commit()

        val intent= Intent(this,OtpActivity::class.java).putExtra("PHONENUMBER",phonenumber)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()

    }


}






