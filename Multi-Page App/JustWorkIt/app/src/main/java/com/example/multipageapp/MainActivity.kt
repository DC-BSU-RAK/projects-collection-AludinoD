package com.example.multipageapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bind the Buttons and Variables
        val tvUser = findViewById<TextView>(R.id.tvUser)
        val btnAccount = findViewById<ImageButton>(R.id.btnAccount)
        val btnAddWorkout = findViewById<ConstraintLayout>(R.id.btnAddWorkout)
        val btnHistory = findViewById<ConstraintLayout>(R.id.btnHistory)

        // This is storing the SharedPreferences like the Name
        val prefs = getSharedPreferences("WorkoutPrefs", Context.MODE_PRIVATE)

        // Get Saved name and display it in the textview User, If not it says Athlete
        val savedName = prefs.getString("user_name", "Athlete")
        tvUser.text = "Hello, $savedName"


        // Button to open the Help
        val btnHelp = findViewById<ImageButton>(R.id.btnHelp)

        btnHelp.setOnClickListener {
            startActivity(Intent(this, InstructionActivity::class.java))
        }

        // Button for the Account(Pencil Icon) so the user can edit the name
        btnAccount.setOnClickListener {
            // Store the current added name so for example i edited it to be [Name] it should show that
            val currentName = prefs.getString("user_name", "Athlete")
            // Uses Alert Dialog to change it, with headers and input box
            val builder = AlertDialog.Builder(this)
            // Update Name Header
            builder.setTitle("Update Name")
            // Frame of the Alert Dialog
            val container = FrameLayout(this)
            val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(64, 24, 64, 8)
            // This is where the user edits their name and gets applied in the dashboard
            val input = EditText(this).apply {
                setText(currentName)
                setSelection(this.text.length)
                backgroundTintList = ColorStateList.valueOf(Color.parseColor("#6366F1"))
            }

            input.layoutParams = params
            container.addView(input)
            builder.setView(container)

            // Saving the new name and replacing the old name and Show the added name in the dashboard.
            builder.setPositiveButton("SAVE") { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotEmpty()) {
                    tvUser.text = "Hello, $newName"
                    prefs.edit().putString("user_name", newName).apply()
                }
            }
            builder.setNegativeButton("CANCEL", null)

            val dialog = builder.create()
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#6366F1"))
        }

        // Navigation Area, For Add Workout( Start Training Button) that opens the AddWorkoutActivty XML
        btnAddWorkout.setOnClickListener {
            startActivity(Intent(this, AddWorkoutActivity::class.java))
        }
        // For History ( Workout Logs ) that opens the ActivityHistory XML
        btnHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }

    // Ensures that the Changes are Made when the app is resumed, automatically reflecting the changes.
    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences("WorkoutPrefs", Context.MODE_PRIVATE)
        findViewById<TextView>(R.id.tvUser).text = "Hello, ${prefs.getString("user_name", "Athlete")}"
    }
}