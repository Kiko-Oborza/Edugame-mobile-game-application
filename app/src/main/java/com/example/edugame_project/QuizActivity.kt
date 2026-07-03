package com.example.edugame_project

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random

class QuizActivity : AppCompatActivity() {

    private lateinit var questionText: TextView
    private lateinit var questionCountText: TextView
    private lateinit var scoreText: TextView
    private lateinit var option1: AppCompatButton
    private lateinit var option2: AppCompatButton
    private lateinit var option3: AppCompatButton

    private var currentQuestionIndex = 0
    private var score = 0
    private val totalQuestions = 10
    private var correctAnswer = 0
    
    // SoundPool for short SFX (more efficient than MediaPlayer)
    private var soundPool: SoundPool? = null
    private var correctSoundId = 0
    private var wrongSoundId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_quiz)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        questionText = findViewById(R.id.question_text)
        questionCountText = findViewById(R.id.question_count)
        scoreText = findViewById(R.id.score_badge)
        option1 = findViewById(R.id.option1)
        option2 = findViewById(R.id.option2)
        option3 = findViewById(R.id.option3)

        findViewById<ImageView>(R.id.back_icon).setOnClickListener {
            finish()
        }

        initSoundPool()
        setupQuiz()
    }

    private fun initSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()
        
        // Preload the sounds
        correctSoundId = soundPool?.load(this, R.raw.correct_answer, 1) ?: 0
        wrongSoundId = soundPool?.load(this, R.raw.wrong_answer, 1) ?: 0
    }

    private fun playSfx(soundId: Int) {
        val prefs = getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE)
        val volume = prefs.getInt("sfx_volume", 80) / 100f
        soundPool?.play(soundId, volume, volume, 1, 0, 1f)
    }

    private fun setupQuiz() {
        currentQuestionIndex = 0
        score = 0
        updateScore()
        loadNextQuestion()
    }

    private fun loadNextQuestion() {
        if (currentQuestionIndex < totalQuestions) {
            currentQuestionIndex++
            questionCountText.text = "Question $currentQuestionIndex / $totalQuestions"

            option1.setBackgroundResource(R.drawable.btn_blue)
            option2.setBackgroundResource(R.drawable.btn_blue)
            option3.setBackgroundResource(R.drawable.btn_blue)
            
            option1.isEnabled = true
            option2.isEnabled = true
            option3.isEnabled = true

            val operators = listOf("+", "-", "x", "÷")
            val operator = operators.random()
            var num1 = 0
            var num2 = 0

            when (operator) {
                "+" -> { num1 = Random.nextInt(1, 100); num2 = Random.nextInt(1, 100); correctAnswer = num1 + num2 }
                "-" -> { num1 = Random.nextInt(20, 100); num2 = Random.nextInt(1, num1); correctAnswer = num1 - num2 }
                "x" -> { num1 = Random.nextInt(1, 13); num2 = Random.nextInt(1, 13); correctAnswer = num1 * num2 }
                "÷" -> { num2 = Random.nextInt(1, 11); correctAnswer = Random.nextInt(1, 11); num1 = num2 * correctAnswer }
            }

            questionText.text = "$num1 $operator $num2 = ?"

            val options = mutableListOf(correctAnswer)
            while (options.size < 3) {
                val wrongAnswer = correctAnswer + Random.nextInt(-10, 11)
                if (wrongAnswer != correctAnswer && wrongAnswer > 0 && !options.contains(wrongAnswer)) {
                    options.add(wrongAnswer)
                }
            }
            options.shuffle()

            option1.text = options[0].toString()
            option2.text = options[1].toString()
            option3.text = options[2].toString()

            val clickListener = View.OnClickListener { view ->
                val selectedButton = view as AppCompatButton
                val selectedAnswer = selectedButton.text.toString().toInt()
                
                option1.isEnabled = false
                option2.isEnabled = false
                option3.isEnabled = false

                if (selectedAnswer == correctAnswer) {
                    score += 10
                    updateScore()
                    selectedButton.setBackgroundResource(R.drawable.btn_green)
                    playSfx(correctSoundId)
                } else {
                    selectedButton.setBackgroundResource(R.drawable.btn_red)
                    playSfx(wrongSoundId)
                    if (option1.text.toString().toInt() == correctAnswer) {
                        option1.setBackgroundResource(R.drawable.btn_green)
                    } else {
                        option1.setBackgroundResource(R.drawable.btn_red)
                    }
                    if (option2.text.toString().toInt() == correctAnswer) {
                        option2.setBackgroundResource(R.drawable.btn_green)
                    } else {
                        option2.setBackgroundResource(R.drawable.btn_red)
                    }
                    if (option3.text.toString().toInt() == correctAnswer) {
                        option3.setBackgroundResource(R.drawable.btn_green)
                    } else {
                        option3.setBackgroundResource(R.drawable.btn_red)
                    }
                }
                
                Handler(Looper.getMainLooper()).postDelayed({
                    loadNextQuestion()
                }, 1500)
            }

            option1.setOnClickListener(clickListener)
            option2.setOnClickListener(clickListener)
            option3.setOnClickListener(clickListener)
        } else {
            val intent = Intent(this, VictoryActivity::class.java)
            intent.putExtra("SCORE", score)
            startActivity(intent)
            finish()
        }
    }

    private fun updateScore() {
        scoreText.text = "Score: $score"
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool?.release()
        soundPool = null
    }
}
