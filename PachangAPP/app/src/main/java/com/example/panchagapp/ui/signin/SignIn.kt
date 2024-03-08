package com.example.panchagapp.ui.signin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.panchagapp.MainActivity
import com.example.panchagapp.R

class SignIn : AppCompatActivity() {

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sig_in)

        val loginbutton = findViewById<Button>(R.id.signInButton)
         loginbutton.setOnClickListener {
             val Intent = Intent(this,MainActivity::class.java)
             Toast.makeText(
                 this@SignIn,
                 "Login Correcto!",
                 Toast.LENGTH_SHORT
             ).show()
             startActivity(Intent)
         }

    }


}