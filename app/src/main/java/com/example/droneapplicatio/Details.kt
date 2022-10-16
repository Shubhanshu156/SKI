package com.example.droneapplicatio

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.details.*
import android.widget.ArrayAdapter

import com.example.droneapplicatio.R
import com.google.firebase.auth.*

import kotlinx.android.synthetic.main.otp.*
import kotlin.check




class Details:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details)
        val items = arrayOf("Beans","Maize","Wheat","Barley","Rice")
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.adapter = adapter
    }
}