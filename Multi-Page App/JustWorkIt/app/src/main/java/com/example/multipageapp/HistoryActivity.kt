package com.example.multipageapp

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HistoryActivity : AppCompatActivity() {

    // Using the LinearLayout for the Card Layout when the data is placed
    private lateinit var container: LinearLayout
    // Used to access the data Workout to Text and between pages
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Bind Buttons and the Container
        val backButton = findViewById<ImageButton>(R.id.btnBack)
        val btnClearHistory = findViewById<Button>(R.id.btnClearHistory)
        container = findViewById<LinearLayout>(R.id.historyContainer)

        // Back Button
        backButton.setOnClickListener { finish() }

        // Clear All History Button
        btnClearHistory.setOnClickListener {
            // Alert Dialog to tell the user that it will delete all workout logs
            AlertDialog.Builder(this)
                .setTitle("Clear All Logs?")
                .setMessage("This will permanently delete your entire history.")
                .setPositiveButton("Clear") { _, _ ->
                    // Gets the shared preferences and Deletes them from the list.
                    val prefs = getSharedPreferences("WorkoutPrefs", Context.MODE_PRIVATE)
                    // Deleting the data key
                    prefs.edit().remove("history_list").apply()
                    // Load the new Deleted History, Updating that there is no more logs at all
                    loadHistory()
                    // Tells the user that the history is cleared
                    Toast.makeText(this, "History cleared", Toast.LENGTH_SHORT).show()
                }
                // Cancel Button
                .setNegativeButton("Cancel", null)
                .show()
        }

        loadHistory()
    }

    // Load History Function that builds the UI
    private fun loadHistory() {
        container.removeAllViews() // Clears the screen so theres no duplicate cards
        val prefs = getSharedPreferences("WorkoutPrefs", Context.MODE_PRIVATE)
        val json = prefs.getString("history_list", null) // Fetch the JSOn text and data

        // Checks if there is existing data
        if (json != null) {
            val type = object : TypeToken<MutableList<Workout>>() {}.type
            val history: MutableList<Workout> = gson.fromJson(json, type) // Turns the text into a list
            // Loop through every saved workout
            history.forEachIndexed { index, workout ->
                // The Card Shell Design
                val card = MaterialCardView(this).apply {
                    radius = 24f
                    cardElevation = 4f
                    setCardBackgroundColor(Color.WHITE)
                    strokeWidth = 3
                    strokeColor = Color.parseColor("#0F172A")
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { setMargins(0, 0, 0, 32) }
                }

                // Layout Format of the Text and Data( Horizontal so its left to right)
                val mainRow = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(40, 40, 40, 40)
                    gravity = Gravity.CENTER_VERTICAL
                }

                // Left Side: Text Data
                val textLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }
                // The Workout Date on Top
                textLayout.addView(TextView(this).apply {
                    text = workout.date.uppercase()
                    textSize = 11f
                    typeface = Typeface.DEFAULT_BOLD
                    setTextColor(Color.parseColor("#94A3B8"))
                })
                // Workout Name
                textLayout.addView(TextView(this).apply {
                    text = workout.exercise
                    textSize = 20f
                    typeface = Typeface.create("sans-serif-black", Typeface.NORMAL)
                    setTextColor(Color.parseColor("#0F172A"))
                    setPadding(0, 4, 0, 8)
                })
                // Workout Set, Reps, and Weight. Access those data from the sharedpref and gson
                textLayout.addView(TextView(this).apply {
                    text = "${workout.sets} Sets  •  ${workout.reps} Reps  •  ${workout.weight}kg"
                    textSize = 14f
                    setTextColor(Color.parseColor("#6366F1"))
                    typeface = Typeface.DEFAULT_BOLD
                })

                // Right Side Buttons in a vertical layout
                val actionStack = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(120, LinearLayout.LayoutParams.WRAP_CONTENT)
                }

                // Edit Button (Pencil Icon)
                val btnEdit = ImageButton(this).apply {
                    setImageResource(android.R.drawable.ic_menu_edit)
                    setBackgroundColor(Color.TRANSPARENT)
                    setColorFilter(Color.parseColor("#64748B"))
                    setPadding(0, 0, 0, 20)
                    // Open Edit Popup when clicked
                    setOnClickListener { showEditPopup(index, history) }
                }
                // Delete Button (Trash Icon)
                val btnDelete = ImageButton(this).apply {
                    setImageResource(android.R.drawable.ic_menu_delete)
                    setBackgroundColor(Color.TRANSPARENT)
                    setColorFilter(Color.parseColor("#EF4444"))
                    // Removes The Workout when clicked
                    setOnClickListener { deleteSingleWorkout(index) }
                }

                // Stack the icons and formats the layout of the container and its datas
                actionStack.addView(btnEdit)
                actionStack.addView(btnDelete)
                mainRow.addView(textLayout)
                mainRow.addView(actionStack)
                card.addView(mainRow)
                container.addView(card)
            }
        }
    }

    // Edit Pop up Function when the Edit Button is Clicked
    private fun showEditPopup(position: Int, history: MutableList<Workout>) {
        val workout = history[position]
        // Pop up is an alert dialog
        val builder = AlertDialog.Builder(this)

        // Title View for the alert Dialog
        val titleView = TextView(this).apply {
            text = "Edit Workout Details"
            textSize = 22f
            typeface = Typeface.create("sans-serif-black", Typeface.NORMAL)
            setPadding(64, 48, 64, 0)
            setTextColor(Color.parseColor("#0F172A"))
        }
        builder.setCustomTitle(titleView)

        // Main Layout Container
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(64, 32, 64, 0)
        }

        // Function to create labeled headers for the inputs
        fun createLabel(labelText: String) = TextView(this).apply {
            text = labelText
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#6366F1"))
            setPadding(0, 16, 0, 8)
        }

        // Add Labels and Inputs
        layout.addView(createLabel("WEIGHT (KG)"))
        val etWeight = EditText(this).apply {
            setText(workout.weight)
            backgroundTintList = ColorStateList.valueOf(Color.parseColor("#6366F1"))
        }
        layout.addView(etWeight)

        layout.addView(createLabel("SETS"))
        val etSets = EditText(this).apply {
            setText(workout.sets)
            backgroundTintList = ColorStateList.valueOf(Color.parseColor("#6366F1"))
        }
        layout.addView(etSets)

        layout.addView(createLabel("REPS"))
        val etReps = EditText(this).apply {
            setText(workout.reps)
            backgroundTintList = ColorStateList.valueOf(Color.parseColor("#6366F1"))
        }
        layout.addView(etReps)

        builder.setView(layout)

        // Action Buttons
        builder.setPositiveButton("UPDATE SESSION") { _, _ ->
            // Update the new workout details
            // Creates a duplicate of the workout
            val updatedWorkout = workout.copy(
                weight = etWeight.text.toString(),
                sets = etSets.text.toString(),
                reps = etReps.text.toString()
            )
            // Replace the old history to this new one
            history[position] = updatedWorkout

            // Updates the New workout to sharedprefs and saves it to sharedpref
            getSharedPreferences("WorkoutPrefs", Context.MODE_PRIVATE)
                .edit().putString("history_list", gson.toJson(history)).apply()
            // Load Newly Update Version
            loadHistory()
            // Tells the user that the workout is updated
            Toast.makeText(this, "Workout updated successfully", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("CANCEL", null)

        val dialog = builder.create()
        dialog.show()

        // Styling for Dialog Buttons
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#6366F1"))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#94A3B8"))
    }

    // Function for deleting single workout
    private fun deleteSingleWorkout(position: Int) {
        // Gets data from shardpref and json in the history data
        val prefs = getSharedPreferences("WorkoutPrefs", Context.MODE_PRIVATE)
        val json = prefs.getString("history_list", null)
        val type = object : TypeToken<MutableList<Workout>>() {}.type
        val history: MutableList<Workout> = gson.fromJson(json, type)

        // Removes that workout from the list
        history.removeAt(position)
        // Save the new list
        prefs.edit().putString("history_list", gson.toJson(history)).apply()
        // Load new version without the deleted workout
        loadHistory()
        // Tells the user that the workout is deleted
        Toast.makeText(this, "Workout Deleted", Toast.LENGTH_SHORT).show()
    }
}