package com.example.edugame_project

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GameSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_selection)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<AppCompatButton>(R.id.math_play_button).setOnClickListener {
            startActivity(Intent(this, QuizActivity::class.java))
        }

        findViewById<AppCompatButton>(R.id.word_play_button).setOnClickListener {
            startActivity(Intent(this, WordAdventureActivity::class.java))
        }

        findViewById<AppCompatButton>(R.id.shape_play_button).setOnClickListener {
            startActivity(Intent(this, ShapeSorterActivity::class.java))
        }

        val backButton = findViewById<AppCompatButton>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
    }
}
