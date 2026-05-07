    package com.example.nancalculator

    // Importing Libraries
    import android.content.Context
    import android.content.Intent
    import android.media.AudioAttributes
    import android.media.SoundPool
    import android.os.Bundle
    import android.view.View
    import android.widget.*
    import androidx.appcompat.app.AlertDialog
    import androidx.appcompat.app.AppCompatActivity
    import androidx.appcompat.app.AppCompatDelegate

    class MainActivity : AppCompatActivity() {
        // Store The Values of the 1st and 2nd Slot.
        // When Starting, It starts as Empty
        private var slot1Val: String? = null
        private var slot2Val: String? = null

        // Tracks discovered items and their recipes
        private val discoveredBook = mutableMapOf<String, String>()

        // Secondary Elements Icon
        private val discoveryIcons = mapOf(
            "Steam" to R.drawable.steam_icon,
            "Lava" to R.drawable.lava_icon,
            "Energy" to R.drawable.energy_icon,
            "Mud" to R.drawable.mud_icon,
            "Mist" to R.drawable.mist_icon,
            "Dust" to R.drawable.dust_icon
        )

        // Audio Variables
        private lateinit var soundPool: SoundPool
        private var sfxFire: Int = 0
        private var sfxWater: Int = 0
        private var sfxEarth: Int = 0
        private var sfxAir: Int = 0
        private var sfxTransmute: Int = 0

        // Recipe Book
        // There are currently 4 Basic Elements and 6 Possible Combinations
        // Since this is a key value pair, the arrangement can be interchangeable and still produce the same result
        // This means Water + Fire is the same as Fire + Water
        private val recipes = mapOf(
            setOf("Fire", "Water") to "Steam",
            setOf("Fire", "Earth") to "Lava",
            setOf("Fire", "Air") to "Energy",
            setOf("Water", "Earth") to "Mud",
            setOf("Water", "Air") to "Mist",
            setOf("Earth", "Air") to "Dust"
        )

        override fun onCreate(savedInstanceState: Bundle?) {
            // Storing Preference and dark Mode
            val sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE)
            val isDarkMode = sharedPref.getBoolean("DarkMode", true)

            // Checks if the app is in Dark mode
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)


            // Setup Audio Attributes
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build()

            // Load specific sounds
            // Each element has a certain sounds
            sfxFire = soundPool.load(this, R.raw.fire_sfx, 1)
            sfxWater = soundPool.load(this, R.raw.water_sfx, 1)
            sfxEarth = soundPool.load(this, R.raw.earth_sfx, 1)
            sfxAir = soundPool.load(this, R.raw.air_sfx, 1)
            sfxTransmute = soundPool.load(this, R.raw.transmute_success, 1)

            // Bind UI component
            val btnSlot1 = findViewById<ImageButton>(R.id.slot1)
            val btnSlot2 = findViewById<ImageButton>(R.id.slot2)
            val btnCombine = findViewById<Button>(R.id.btnCombine)
            val txtResult = findViewById<TextView>(R.id.txtResult)
            val btnBook = findViewById<ImageButton>(R.id.btnBook)
            val imgDiscovery = findViewById<ImageView>(R.id.imgDiscovery)
            val btnThemeToggle = findViewById<ImageButton>(R.id.btnThemeToggle)
            val btnHelp = findViewById<ImageButton>(R.id.btnHelp)

            // Icon Dark Mode Icon
            if (isDarkMode) {
                btnThemeToggle.setImageResource(R.drawable.ic_day) // Sun icon
            } else {
                btnThemeToggle.setImageResource(R.drawable.ic_night) // Moon Icon
            }

            // Theme toggle logic
            btnThemeToggle.setOnClickListener { toggleTheme() }

            // Bind Inventory
            val fire = findViewById<ImageButton>(R.id.fireBtn)
            val water = findViewById<ImageButton>(R.id.waterBtn)
            val earth = findViewById<ImageButton>(R.id.earthBtn)
            val air = findViewById<ImageButton>(R.id.airBtn)


            // Element selection logic
            // This allows the buttons to fill the slots
            // It has the sound audio, name, icon, and the slots
            fire.setOnClickListener {
                soundPool.play(sfxFire, 1f, 1f, 0, 0, 1f)
                handleSelection(
                    "Fire",
                    R.drawable.fire_icon,
                    btnSlot1,
                    btnSlot2
                )
            }
            water.setOnClickListener {
                soundPool.play(sfxWater, 1f, 1f, 0, 0, 1f)
                handleSelection(
                    "Water",
                    R.drawable.water_icon,
                    btnSlot1,
                    btnSlot2
                )
            }
            earth.setOnClickListener {
                soundPool.play(sfxEarth, 1f, 1f, 0, 0, 1f)
                handleSelection(
                    "Earth",
                    R.drawable.earth_icon,
                    btnSlot1,
                    btnSlot2
                )
            }
            air.setOnClickListener {
                soundPool.play(sfxAir, 1f, 1f, 0, 0, 1f)
                handleSelection(
                    "Air",
                    R.drawable.air_icon,
                    btnSlot1,
                    btnSlot2) }


            // Combine/Calculate logic
            btnCombine.setOnClickListener {
                // Checks if First and 2nd Slot is Not Empty
                if (slot1Val != null && slot2Val != null) {
                    // Checks If First and 2nd Slot is both the same element
                    if (slot1Val == slot2Val) {
                        txtResult.text = "Result: Pure $slot1Val (NaN)"
                        imgDiscovery.visibility = View.GONE
                    } else {
                        // Checks the Dictionary of the Slot 1 and Slot 2 Values
                        val currentMix = setOf(slot1Val!!, slot2Val!!)
                        // Adds it to the recipe book
                        val discovery = recipes[currentMix]
                        // If the discovery is Not Empty
                        if (discovery != null) {
                            txtResult.text = "Discovered: $discovery"
                            discoveredBook[discovery] = "$slot1Val + $slot2Val"

                            val iconRes = discoveryIcons[discovery]
                            if (iconRes != null) {
                                imgDiscovery.setImageResource(iconRes)
                                imgDiscovery.visibility = View.VISIBLE // Make it visible
                            }
                            // Toast Notification Saying that the discovery is added to the recipe book
                            Toast.makeText(this, "$discovery added to Book!", Toast.LENGTH_SHORT).show()
                            soundPool.play(sfxTransmute, 1f, 1f, 0, 0, 1f)
                        } else {
                            // If the Recipe isn't Available or out of the dictionary
                            txtResult.text = "Result: NaN (Reaction Failed)"
                            imgDiscovery.visibility = View.GONE
                        }
                    }
                    // Reset the board once everything is done.
                    resetBoard(btnSlot1, btnSlot2)
                    // If The Values are Empty
                } else {
                    Toast.makeText(this, "Mix two elements!", Toast.LENGTH_SHORT).show()
                }
            }


            btnHelp.setOnClickListener {
                // Intent to switch from MainActivity to InstructionsActivity
                val intent = Intent(this, Activityinstructions::class.java)
                startActivity(intent)
            }
            btnBook.setOnClickListener { showBook() }

            // Click slot to remove item
            btnSlot1.setOnClickListener { slot1Val = null; btnSlot1.setImageDrawable(null) }
            btnSlot2.setOnClickListener { slot2Val = null; btnSlot2.setImageDrawable(null) }
        }
        // Changing between dark mode and light mode function
        private fun toggleTheme() {
            // Storing values for preferences
            val sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE)
            val isDarkMode = sharedPref.getBoolean("DarkMode", true)
            val editor = sharedPref.edit()

            // Logic that switches between dark mode true and false
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                editor.putBoolean("DarkMode", false)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor.putBoolean("DarkMode", true)
            }
            // Apply Dark Mode or Light
            editor.apply()
            recreate()
        }

        // This Function checks the values of the Slot 1 and Slot 2, Adding the Images and Name of the element
        private fun handleSelection(name: String, icon: Int, s1: ImageButton, s2: ImageButton) {
            findViewById<ImageView>(R.id.imgDiscovery).visibility = View.GONE
            if (slot1Val == null) {
                slot1Val = name
                s1.setImageResource(icon)
            } else if (slot2Val == null) {
                slot2Val = name
                s2.setImageResource(icon)
            }
        }

        // Reset Board Function that clears the slot 1 and slot 2 val and image
        private fun resetBoard(s1: ImageButton, s2: ImageButton) {
            slot1Val = null
            slot2Val = null
            s1.setImageDrawable(null)
            s2.setImageDrawable(null)
        }

        // Recipe Book Button
        private fun showBook() {
            // If discovery book is Empty
            val list = if (discoveredBook.isEmpty()) "No discoveries yet."
            // Shows the recipe and the output of the 2 elements
            else discoveredBook.entries.joinToString("\n") { "✨ ${it.key} (${it.value})" }

            // Alert Dialog for the Pop Up
            AlertDialog.Builder(this)
                .setTitle("Magic Spell Book")
                .setMessage(list)
                .setPositiveButton("Close", null).show()
        }

        // Removes sounds when not used
        override fun onDestroy() {
            super.onDestroy()
            soundPool.release()
        }
    }