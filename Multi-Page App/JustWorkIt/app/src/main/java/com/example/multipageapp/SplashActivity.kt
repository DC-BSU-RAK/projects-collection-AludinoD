package com.example.multipageapp


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Link the image from XML
        val barbell = findViewById<ImageView>(R.id.ivbarbell)

        // Load and start the rotation animation
        val rotation = AnimationUtils.loadAnimation(this, R.anim.rotate)
        barbell.startAnimation(rotation)

        // Set a Timer (3 Seconds) before opening MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // This kills the splash activity so you can't go back to it
        }, 3000) // 3000 milliseconds = 3 seconds
    }
}