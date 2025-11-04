package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.example.myapplication.model.Question
import com.example.myapplication.util.AudioPlayer
import org.json.JSONArray
import com.bumptech.glide.Glide
import android.media.MediaPlayer

class QuizActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var correctSound: MediaPlayer
    private lateinit var wrongSound: MediaPlayer

    private lateinit var questionText: TextView
    private lateinit var option1: ImageView
    private lateinit var option2: ImageView
    private lateinit var option3: ImageView

    private var questions: List<Question> = emptyList()
    private var currentIndex = 0
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        // 🎵 Música de fondo
        mediaPlayer = MediaPlayer.create(this, R.raw.musica_quizz_dificil)
        mediaPlayer.isLooping = true
        mediaPlayer.setVolume(1.0f, 1.0f)
        mediaPlayer.start()

        // 🔔 Sonidos de respuesta
        correctSound = MediaPlayer.create(this, R.raw.correct)
        wrongSound = MediaPlayer.create(this, R.raw.wrong)

        // 🕺 GIF del personaje
        val characterGif = findViewById<ImageView>(R.id.characterGif)
        Glide.with(this)
            .asGif()
            .load(R.drawable.personaje_gif)
            .into(characterGif)

        // 🧩 Referencias UI
        questionText = findViewById(R.id.questionText)
        option1 = findViewById(R.id.option1)
        option2 = findViewById(R.id.option2)
        option3 = findViewById(R.id.option3) //hola

        // 🔄 Cargar preguntas
        questions = loadQuestions()
        showQuestion()
    }

    // Carga de preguntas desde questions.json
    private fun loadQuestions(): List<Question> {
        val json = resources.openRawResource(R.raw.questions).bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(json)
        val result = mutableListOf<Question>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val question = Question(
                obj.getString("text"),
                obj.getString("audio"),
                listOf(
                    obj.getJSONArray("options").getString(0),
                    obj.getJSONArray("options").getString(1),
                    obj.getJSONArray("options").getString(2)
                      ),
                obj.getInt("correctIndex")
                                   )
            result.add(question)
        }
        return result
    }

    // Mostrar la pregunta actual
    private fun showQuestion() {
        val q = questions[currentIndex]
        questionText.text = q.text
        AudioPlayer.play(this, q.audio)

        val res1 = resources.getIdentifier(q.options[0], "drawable", packageName)
        val res2 = resources.getIdentifier(q.options[1], "drawable", packageName)
        val res3 = resources.getIdentifier(q.options[2], "drawable", packageName)

        option1.setImageResource(res1)
        option2.setImageResource(res2)
        option3.setImageResource(res3)

        val options = listOf(option1, option2, option3)
        options.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                handleAnswer(index == q.correctIndex)
            }
        }
    }

    // Maneja la selección de respuesta
    private fun handleAnswer(isCorrect: Boolean) {
        // 🔇 Pausar la música de fondo momentáneamente
        mediaPlayer.pause()

        if (isCorrect) {
            correctSound.start()
            score++
        } else {
            wrongSound.start()
        }

        // ⏳ Esperar al fin del sonido para continuar
        val soundToWait = if (isCorrect) correctSound else wrongSound
        soundToWait.setOnCompletionListener {
            mediaPlayer.start() // Reanudar música de fondo
            if (currentIndex < questions.size - 1) {
                currentIndex++
                showQuestion()
            } else {
                finishGame()
            }
        }
    }

    // Fin del juego
    private fun finishGame() {
        mediaPlayer.pause()
        // Aquí puedes mostrar una pantalla de resultado o animación
    }

    // Control del ciclo de vida
    override fun onPause() {
        super.onPause()
        if (mediaPlayer.isPlaying) mediaPlayer.pause()
    }

    override fun onResume() {
        super.onResume()
        if (!mediaPlayer.isPlaying) mediaPlayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        correctSound.release()
        wrongSound.release()
    }
}
