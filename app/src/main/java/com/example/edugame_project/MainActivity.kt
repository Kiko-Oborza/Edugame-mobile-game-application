package com.example.edugame_project

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val playButton = findViewById<Button>(R.id.play_button)
        playButton.setOnClickListener {
            stopMusic()
            val intent = Intent(this, GameSelectionActivity::class.java)
            startActivity(intent)
        }

        val learnButton = findViewById<Button>(R.id.learn_button)
        learnButton.setOnClickListener {
            stopMusic()
            val intent = Intent(this, LearnActivity::class.java)
            startActivity(intent)
        }

        val profileButton = findViewById<Button>(R.id.profile_button)
        profileButton.setOnClickListener {
            stopMusic()
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        val settingsButton = findViewById<Button>(R.id.settings_button)
        settingsButton.setOnClickListener {
            stopMusic()
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startMusic() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.bgmusic_main)
            mediaPlayer?.isLooping = true
            
            // Apply volume from settings
            val sharedPreferences = getSharedPreferences("SettingsPrefs", MODE_PRIVATE)
            val volume = sharedPreferences.getInt("music_volume", 70) / 100f
            mediaPlayer?.setVolume(volume, volume)
        }
        if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    private fun stopMusic() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
    }

    override fun onResume() {
        super.onResume()
        startMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMusic()
    }
}