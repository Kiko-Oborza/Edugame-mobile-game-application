package com.example.edugame_project

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class VictoryActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_victory)

        val mainLayout = findViewById<ConstraintLayout>(R.id.main_layout)
        val scoreText = findViewById<TextView>(R.id.score_text)
        val trophyImage = findViewById<ImageView>(R.id.trophy_image)
        val greatJobText = findViewById<TextView>(R.id.great_job_text)
        val wellDoneText = findViewById<TextView>(R.id.well_done_text)
        val starLeft = findViewById<ImageView>(R.id.star_left)
        val starRight = findViewById<ImageView>(R.id.star_right)
        val homeButton = findViewById<Button>(R.id.home_button)
        val playAgainButton = findViewById<Button>(R.id.play_again_button)

        val score = intent.getIntExtra("SCORE", 0)
        scoreText.text = "You scored: $score / 100"

        if (score < 50) {
            trophyImage.setImageResource(R.drawable.sad_trophy)
            greatJobText.text = "Better Luck Next Time!"
            wellDoneText.text = "Practice More"
            mainLayout.setBackgroundColor(Color.parseColor("#EAECEE"))
            greatJobText.setTextColor(Color.parseColor("#C0392B"))
            starLeft.visibility = View.GONE
            starRight.visibility = View.GONE
            playSfxMusic(R.raw.loss_music)
        } else {
            trophyImage.setImageResource(R.drawable.happy_trophy)
            greatJobText.text = "Great Job!"
            wellDoneText.text = "Well Done!"
            mainLayout.setBackgroundColor(Color.parseColor("#FDF5E6"))
            greatJobText.setTextColor(Color.parseColor("#2C3E50"))
            starLeft.visibility = View.VISIBLE
            starRight.visibility = View.VISIBLE
            playSfxMusic(R.raw.victory_music)
        }

        homeButton.setOnClickListener {
            stopMusic()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        playAgainButton.setOnClickListener {
            stopMusic()
            val intent = Intent(this, GameSelectionActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun playSfxMusic(resId: Int) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, resId)
        
        // Get volume from Sound FX setting
        val prefs = getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE)
        val volume = prefs.getInt("sfx_volume", 80) / 100f
        mediaPlayer?.setVolume(volume, volume)
        
        mediaPlayer?.start()
    }

    private fun stopMusic() {
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMusic()
    }
}