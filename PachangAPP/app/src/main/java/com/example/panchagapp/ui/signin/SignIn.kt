package com.example.panchagapp.ui.signin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.panchagapp.MainActivity
import com.example.panchagapp.R

class SignIn : AppCompatActivity() {

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sig_in)

        val loginbutton = findViewById<Button>(R.id.signInButton)
         loginbutton.setOnClickListener {
             val Intent = Intent(this,MainActivity::class.java)
             startActivity(Intent)
         }

    }


}