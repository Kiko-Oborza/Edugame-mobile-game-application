package com.example.edugame_project

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class LearnActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn)

        findViewById<ImageView>(R.id.back_button_learn).setOnClickListener {
            finish()
        }
    }
}