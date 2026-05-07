package com.example.nancalculator

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Activityinstructions : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instructions)

        // Close Button
        val btnClose = findViewById<Button>(R.id.btnCloseInstructions)
        btnClose.setOnClickListener {
            finish() // Closes this screen and returns to the app
        }
    }
}