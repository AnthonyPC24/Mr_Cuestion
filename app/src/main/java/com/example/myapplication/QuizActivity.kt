package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.example.myapplication.model.Question
import com.example.myapplication.util.AudioPlayer
import org.json.JSONArray
import com.bumptech.glide.Glide
import android.media.MediaPlayer
import android.view.View

class QuizActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var correctSound: MediaPlayer
    private lateinit var wrongSound: MediaPlayer

    private lateinit var questionText: TextView
    private lateinit var option1: ImageView
    private lateinit var option2: ImageView
    private lateinit var option3: ImageView

    private lateinit var stars: List<ImageView>
    private var questions: List<Question> = emptyList()
    private var currentIndex = 0
    private var score = 0

    private var totalPreguntas: Int = 0
    private var respuestasCorrectas: Int = 0
    private val imagenesCorrectas = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        stars = listOf(
            findViewById(R.id.star1),
            findViewById(R.id.star2),
            findViewById(R.id.star3),
            findViewById(R.id.star4),
            findViewById(R.id.star5),
            findViewById(R.id.star6),
            findViewById(R.id.star7),
            findViewById(R.id.star8),
            findViewById(R.id.star9),
            findViewById(R.id.star10),
            findViewById(R.id.star11),
            findViewById(R.id.star12),
            findViewById(R.id.star13),
            findViewById(R.id.star14),
            findViewById(R.id.star15)
                      )

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
        Glide.with(this).asGif().load(R.drawable.personaje_gif).into(characterGif)

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
        val json = resources.openRawResource(R.raw.mediumquestions).bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(json)
        val result = mutableListOf<Question>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val question = Question(
                obj.getString("text"), obj.getString("audio"), listOf(
                    obj.getJSONArray("options").getString(0),
                    obj.getJSONArray("options").getString(1),
                    obj.getJSONArray("options").getString(2)
                                                                     ), obj.getInt("correctIndex")
                                   )
            result.add(question)
        }

        // 🔀 Mezclar aleatoriamente y tomar solo 15 preguntas
        val seleccionadas = result.shuffled().take(15)
        totalPreguntas = seleccionadas.size
        return seleccionadas
    }

    // Mostrar la pregunta actual
    private fun showQuestion() {
        val q = questions[currentIndex]
        questionText.text = q.text
        AudioPlayer.play(this, q.audio)

        val shuffledOptions = q.options.shuffled()
        val correctAnswer = q.options[q.correctIndex]
        val correctIndexInShuffled = shuffledOptions.indexOf(correctAnswer)

// Mostrar las imágenes mezcladas
        val res1 = resources.getIdentifier(shuffledOptions[0], "drawable", packageName)
        val res2 = resources.getIdentifier(shuffledOptions[1], "drawable", packageName)
        val res3 = resources.getIdentifier(shuffledOptions[2], "drawable", packageName)

        option1.setImageResource(res1)
        option2.setImageResource(res2)
        option3.setImageResource(res3)

// Detectar clics y verificar si eligió la correcta
        val options = listOf(option1, option2, option3)
        options.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                handleAnswer(index == correctIndexInShuffled, imageView)
            }
        }
    }

    // Maneja la selección de respuesta
    private fun handleAnswer(isCorrect: Boolean, selectedView: ImageView) {
        mediaPlayer.pause()

        if (isCorrect) {
            correctSound.start()
            respuestasCorrectas++  // ✅ aumenta respuestas correctas

            // ✅ Guarda la imagen correcta
            val correctAnswer = questions[currentIndex].options[questions[currentIndex].correctIndex]
            imagenesCorrectas.add(correctAnswer)

            showStarEffect(selectedView)
        } else {
            wrongSound.start()
        }
        updateStars(currentIndex, isCorrect)

        val soundToWait = if (isCorrect) correctSound else wrongSound
        soundToWait.setOnCompletionListener {
            mediaPlayer.start()
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

        val intent = Intent(this, ResultadoActivity::class.java)
        intent.putExtra("TOTAL_PREGUNTAS", totalPreguntas)
        intent.putExtra("RESPUESTAS_CORRECTAS", respuestasCorrectas)
        intent.putStringArrayListExtra("CORRECT_IMAGES", imagenesCorrectas)

        startActivity(intent)
        finish()
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

    private fun updateStars(index: Int, isCorrect: Boolean) {
        if (index in stars.indices) {
            val starImage = if (isCorrect) R.drawable.star else R.drawable.star_wrong
            stars[index].setImageResource(starImage)
        }
    }

    private fun showStarEffect(targetView: ImageView) {
        val starEffect = findViewById<ImageView>(R.id.starEffect)

        // Esperar a que la vista esté medida y visible
        targetView.post {
            val location = IntArray(2)
            targetView.getLocationOnScreen(location)

            // Calcular posición sobre la opción seleccionada
            starEffect.x = location[0].toFloat() + targetView.width / 2 - starEffect.width / 2
            starEffect.y = location[1].toFloat() - starEffect.height / 2

            // Mostrar y reproducir el GIF
            starEffect.visibility = View.VISIBLE
            Glide.with(this).asGif().load(R.drawable.stars).into(starEffect)

            // Ocultarlo después de un tiempo
            starEffect.postDelayed({
                                       starEffect.visibility = View.GONE
                                   }, 1500)
        }
    }
}
