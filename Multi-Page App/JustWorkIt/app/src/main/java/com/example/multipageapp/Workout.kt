package com.example.multipageapp

// Kotline file that serves as a blueprint for what the workout has to have in order to be saved.
data class Workout(
    val exercise: String,
    val weight: String,
    val sets: String,
    val reps: String,
    val date: String
)