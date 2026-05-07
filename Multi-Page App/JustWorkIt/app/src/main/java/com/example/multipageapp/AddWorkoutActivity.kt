package com.example.multipageapp

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class AddWorkoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_workout)

        // Bind the Back Button
        findViewById<ImageButton>(R.id.btnBackAdd).setOnClickListener { finish() }
        // Bind the save Button
        findViewById<Button>(R.id.btnSave).setOnClickListener {
            // Getting User Input and Saving It
            val name = findViewById<TextInputEditText>(R.id.etExercise).text.toString()
            val weight = findViewById<TextInputEditText>(R.id.etWeight).text.toString()
            val sets = findViewById<TextInputEditText>(R.id.etSets).text.toString()
            val reps = findViewById<TextInputEditText>(R.id.etReps).text.toString()

            // Checks if the inputs are not empty
            if (name.isNotEmpty()) {
                // Automatic Date Generation by Month Date and Year
                val date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
                // Creates the new workout using the user input
                val newWorkout = Workout(name, weight, sets, reps, date)
                // Store the Workout List into sharedpref
                val prefs = getSharedPreferences("WorkoutPrefs", Context.MODE_PRIVATE)
                val gson = Gson()
                // Get the existing history list
                val json = prefs.getString("history_list", null)
                val type = object : TypeToken<MutableList<Workout>>() {}.type
                // Convert String back to a List
                //If it finds a String, Gson turns it back into a Kotlin List. If it finds nothing (null), it creates a brand new empty list.
                val list: MutableList<Workout> = if (json == null) mutableListOf() else gson.fromJson(json, type)
                // Add the new workout at index 0 because thats where lists starts
                list.add(0, newWorkout)
                // Gson turns the updated list back into one long "JSON String" so it can be stored in the sharedpref.
                prefs.edit().putString("history_list", gson.toJson(list)).apply()
                // Tells the user that the workout is saved.
                Toast.makeText(this, "Workout Saved!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}