package com.example.nancalculator

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Set Theme before super.onCreate to avoid a "Theme Flash"
        val sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("DarkMode", true)

        // Checks if its dark mode or not
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Initialize variables
        val container = findViewById<ConstraintLayout>(R.id.splashContainer)
        val splashLogo = findViewById<ImageView>(R.id.splashLogo)
        val splashText = findViewById<TextView>(R.id.splashText)

        // Checks if its in dark mode and changes the text colors to black or white
        if (!isDarkMode) {
            container.setBackgroundColor(Color.WHITE)
            splashText.setTextColor(Color.parseColor("#333333"))
        } else {
            container.setBackgroundColor(Color.parseColor("#121212"))
            splashText.setTextColor(Color.WHITE)
        }

        // Storing the Icons
        // Animation logic
        val elements = listOf(
            R.drawable.fire_icon,
            R.drawable.water_icon,
            R.drawable.earth_icon,
            R.drawable.air_icon
        )

        // This handles the loop timer
        val handler = Handler(Looper.getMainLooper())

        // Animation Area
        // I want the icons to fade and switch to another element so its fire - water - earth - air then the main app.
        // So this loop will iterate on the elements
        for (i in elements.indices) {
            handler.postDelayed({
                // Fade in animation
                val fadeIn = AlphaAnimation(0f, 1f)
                // Fade in transition 500ms
                fadeIn.duration = 500

                // Sets image and starts fade in animation
                splashLogo.setImageResource(elements[i])
                splashLogo.startAnimation(fadeIn)

                // If it's the last element, transition to MainActivity
                if (i == elements.size - 1) {
                    handler.postDelayed({
                        // Calls the Main Activity to switch screens.
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }, 1000)
                }
                // 1000ms for each element
            }, (i * 1000).toLong())
        }
    }
}