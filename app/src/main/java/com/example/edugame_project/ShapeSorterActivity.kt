package com.example.edugame_project

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

class ShapeSorterActivity : AppCompatActivity() {

    private lateinit var questionCountText: TextView
    private lateinit var scoreText: TextView
    private lateinit var shapeImageView: ImageView
    private lateinit var option1: AppCompatButton
    private lateinit var option2: AppCompatButton
    private lateinit var option3: AppCompatButton

    private var soundPool: SoundPool? = null
    private var correctSoundId = 0
    private var wrongSoundId = 0

    private var currentShapeIndex = 0
    private var score = 0
    private val totalQuestions = 10
    private lateinit var currentShape: ShapeData

    data class ShapeData(val name: String, val imageResId: Int)

    private val shapeList = listOf(
        ShapeData("Circle", R.drawable.circle),
        ShapeData("Square", R.drawable.square),
        ShapeData("Triangle", R.drawable.triangle),
        ShapeData("Star", R.drawable.star),
        ShapeData("Heart", R.drawable.heart),
        ShapeData("Oval", R.drawable.oval),
        ShapeData("Diamond", R.drawable.diamond),
        ShapeData("Hexagon", R.drawable.hexagon),
        ShapeData("Pentagon", R.drawable.pentagon),
        ShapeData("Rectangle", R.drawable.rectangle)
    ).shuffled()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_shape_sorter)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        questionCountText = findViewById(R.id.question_count)
        scoreText = findViewById(R.id.score_badge_text)
        shapeImageView = findViewById(R.id.shape_image)
        option1 = findViewById(R.id.option1)
        option2 = findViewById(R.id.option2)
        option3 = findViewById(R.id.option3)

        findViewById<ImageView>(R.id.back_icon).setOnClickListener {
            finish()
        }

        initSoundPool()
        loadNextShape()
    }

    private fun initSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder().setMaxStreams(5).setAudioAttributes(audioAttributes).build()
        correctSoundId = soundPool?.load(this, R.raw.correct_answer, 1) ?: 0
        wrongSoundId = soundPool?.load(this, R.raw.wrong_answer, 1) ?: 0
    }

    private fun playSfx(soundId: Int) {
        val prefs = getSharedPreferences("SettingsPrefs", MODE_PRIVATE)
        val volume = prefs.getInt("sfx_volume", 80) / 100f
        soundPool?.play(soundId, volume, volume, 1, 0, 1f)
    }

    private fun loadNextShape() {
        if (currentShapeIndex < totalQuestions) {
            currentShape = shapeList[currentShapeIndex]
            currentShapeIndex++

            questionCountText.text = "Shape $currentShapeIndex / $totalQuestions"
            shapeImageView.setImageResource(currentShape.imageResId)

            option1.setBackgroundResource(R.drawable.btn_blue)
            option2.setBackgroundResource(R.drawable.btn_blue)
            option3.setBackgroundResource(R.drawable.btn_blue)
            option1.isEnabled = true
            option2.isEnabled = true
            option3.isEnabled = true

            val options = mutableListOf(currentShape.name)
            val otherShapes = shapeList.map { it.name }.filter { it != currentShape.name }.shuffled()
            options.addAll(otherShapes.take(2))
            options.shuffle()

            option1.text = options[0]
            option2.text = options[1]
            option3.text = options[2]

            val clickListener = View.OnClickListener { view ->
                val selectedButton = view as AppCompatButton
                val selectedAnswer = selectedButton.text.toString()

                option1.isEnabled = false
                option2.isEnabled = false
                option3.isEnabled = false

                if (selectedAnswer == currentShape.name) {
                    score += 10
                    scoreText.text = "Score: $score"
                    selectedButton.setBackgroundResource(R.drawable.btn_green)
                    playSfx(correctSoundId)
                } else {
                    selectedButton.setBackgroundResource(R.drawable.btn_red)
                    playSfx(wrongSoundId)

                    // Highlight correct answer
                    if (option1.text == currentShape.name) {
                        option1.setBackgroundResource(R.drawable.btn_green)
                    } else {
                        option1.setBackgroundResource(R.drawable.btn_red)
                    }

                    if (option2.text == currentShape.name) {
                        option2.setBackgroundResource(R.drawable.btn_green)
                    } else {
                        option2.setBackgroundResource(R.drawable.btn_red)
                    }

                    if (option3.text == currentShape.name) {
                        option3.setBackgroundResource(R.drawable.btn_green)
                    } else {
                        option3.setBackgroundResource(R.drawable.btn_red)
                    }
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    loadNextShape()
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

    override fun onDestroy() {
        soundPool?.release()
        super.onDestroy()
    }
}
