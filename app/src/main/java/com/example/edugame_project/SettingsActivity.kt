package com.example.edugame_project

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private var soundPool: SoundPool? = null
    private var sampleSoundId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val musicSeekBar = findViewById<SeekBar>(R.id.music_volume_seekbar)
        val sfxSeekBar = findViewById<SeekBar>(R.id.sfx_volume_seekbar)
        val backButton = findViewById<ImageView>(R.id.back_button_settings)

        val sharedPreferences = getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE)
        
        // Initialize SoundPool for SFX feedback
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder().setMaxStreams(1).setAudioAttributes(audioAttributes).build()
        sampleSoundId = soundPool?.load(this, R.raw.correct_answer, 1) ?: 0

        // Load saved volumes
        val savedMusicVolume = sharedPreferences.getInt("music_volume", 70)
        val savedSfxVolume = sharedPreferences.getInt("sfx_volume", 80)

        musicSeekBar.progress = savedMusicVolume
        sfxSeekBar.progress = savedSfxVolume

        musicSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val volume = progress / 100f
                mediaPlayer?.setVolume(volume, volume)
                sharedPreferences.edit().putInt("music_volume", progress).apply()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        sfxSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                sharedPreferences.edit().putInt("sfx_volume", progress).apply()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Play sample sound when user releases the slider to hear the new volume
                val volume = sfxSeekBar.progress / 100f
                soundPool?.play(sampleSoundId, volume, volume, 1, 0, 1f)
            }
        })

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun startMusic() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.bgmusic_main)
            mediaPlayer?.isLooping = true
            
            val sharedPreferences = getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE)
            val volume = sharedPreferences.getInt("music_volume", 70) / 100f
            mediaPlayer?.setVolume(volume, volume)
        }
        if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    private fun stopMusic() {
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
    }

    override fun onResume() {
        super.onResume()
        startMusic()
    }

    override fun onPause() {
        super.onPause()
        stopMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMusic()
        soundPool?.release()
        soundPool = null
    }
}