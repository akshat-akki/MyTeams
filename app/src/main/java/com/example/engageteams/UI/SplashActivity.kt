package com.example.engageteams.UI

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.engageteams.R
import com.example.engageteams.UI.Auth.AuthenticationActivity

//Splash Activity visible for 3 seconds!!

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val delay = 3000 //delay period of the splash screen is 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(applicationContext, AuthenticationActivity::class.java)//Calling the AuthenticationActivity
            startActivity(intent)
            finish()
        }, delay.toLong())
    }
}