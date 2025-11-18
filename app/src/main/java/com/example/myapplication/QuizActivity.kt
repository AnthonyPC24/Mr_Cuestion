package com.example.myapplication

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.example.myapplication.model.Question
import com.example.myapplication.util.AudioPlayer
import org.json.JSONArray
import com.bumptech.glide.Glide
import android.media.MediaPlayer
import android.media.MediaRecorder
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

    private var isRecording = false
    private var recorder: MediaRecorder? = null
    private var recordedFile: String? = null

    private var currentQuestionText: String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)



        if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), 200)
        }


        val avatarImagen = intent.getIntExtra("avatarImagen", R.drawable.avatar1)
        val avatarView = findViewById<ImageView>(R.id.avatarJugador)
        avatarView.setImageResource(avatarImagen)

        avatarView.setOnClickListener {
            val characterGif = findViewById<ImageView>(R.id.characterGif)

            if (!isRecording) {
                // 🎙️ Iniciar grabación
                startRecording()

                // 🔄 Cambiar la animación del personaje a "escuchando"
                Glide.with(this).load(R.drawable.personaje_escuchando).into(characterGif)
                questionText.text = "Soy todo oidos, chaval"

            } else {
                // 🛑 Detener grabación
                stopRecording()

                // 🔊 Cambiar a animación de "hablando" mientras reproduce
                playWithEffect(
                    onStartSpeaking = {
                        Glide.with(this).load(R.drawable.personaje_hablando).into(characterGif)
                        questionText.text = "¡Maravillosas palabras!"
                    },
                    onFinishSpeaking = {
                        Glide.with(this).asGif().load(R.drawable.personaje_gif).into(characterGif)
                        questionText.text = currentQuestionText
                    }
                              )
            }

            isRecording = !isRecording
        }

        val dificultad = intent.getStringExtra("DIFICULTAD") ?: "Fácil"
        val numPreguntas = intent.getIntExtra("NUM_PREGUNTAS", 15)


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
        stars.forEachIndexed { index, imageView ->
            imageView.visibility = if (index < numPreguntas) View.VISIBLE else View.GONE
        }

        // 🎵 Música de fondo
        val musicResId = when (dificultad) {
            "Fácil" -> R.raw.musica_quizz_facil
            "Medio" -> R.raw.musica_quizz_medio
            "Difícil" -> R.raw.musica_quizz_dificil
            else -> R.raw.musica_quizz_facil
        }

// Pausar música global (la que venía del Main)
        MusicManager.pause()

// Reproducir música del quiz
        MusicManager.play(this, musicResId)

        // 🔔 Sonidos de respuesta
        correctSound = MediaPlayer.create(this, R.raw.correct)
        wrongSound = MediaPlayer.create(this, R.raw.wrong)

        // 🕺 GIF del personaje
        val characterGif = findViewById<ImageView>(R.id.characterGif)
        Glide.with(this).asGif().load(R.drawable.personaje_gif).into(characterGif)

        characterGif.setOnClickListener {

            val textoOriginal = questionText.text.toString()


            questionText.text = "¡OUCH!"

            val reacciones = listOf(
                R.drawable.personaje_golpeado,
                R.drawable.personaje_golpeado2,
                R.drawable.personaje_golpeado3
                                   )
            val reaccionAleatoria = reacciones.random()
            Glide.with(this).load(reaccionAleatoria).into(characterGif)

            val golpeSound = MediaPlayer.create(this, R.raw.golpe)
            golpeSound.start()

            characterGif.postDelayed({
                                         Glide.with(this).asGif().load(R.drawable.personaje_gif).into(characterGif)
                                         questionText.text = textoOriginal
                                     }, 1500)


            val shake = android.view.animation.TranslateAnimation(
                -10f, 10f, 0f, 0f
                                                                 ).apply {
                duration = 50
                repeatMode = android.view.animation.Animation.REVERSE
                repeatCount = 5
            }
            characterGif.startAnimation(shake)
        }

        // 🧩 Referencias UI
        questionText = findViewById(R.id.questionText)
        option1 = findViewById(R.id.option1)
        option2 = findViewById(R.id.option2)
        option3 = findViewById(R.id.option3) //hola

        // 🔄 Cargar preguntas
        questions = loadQuestions(dificultad, numPreguntas)
        totalPreguntas = numPreguntas
        showQuestion()
    }

    // Carga de preguntas desde questions.json
    private fun loadQuestions(dificultad: String, numPreguntas: Int): List<Question> {
        val jsonResId = when (dificultad) {
            "Fácil" -> R.raw.questions
            "Medio" -> R.raw.mediumquestions
            "Difícil" -> R.raw.hardquestions
            else -> R.raw.questions
        }

        val json = resources.openRawResource(jsonResId).bufferedReader().use { it.readText() }
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

        val seleccionadas = result.shuffled().take(numPreguntas)
        totalPreguntas = seleccionadas.size
        return seleccionadas
    }



    // Mostrar la pregunta actual
    private fun showQuestion() {
        val q = questions[currentIndex]
        questionText.text = q.text
        currentQuestionText = q.text
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
        MusicManager.pause()

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
            MusicManager.resume() // Reanuda la música de fondo del Quiz
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
        MusicManager.pause()

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
        MusicManager.pause() // Pausa música mientras la Activity no está visible
    }

    override fun onResume() {
        super.onResume()
        MusicManager.resume() // Reanuda música
    }

    override fun onDestroy() {
        super.onDestroy()
        correctSound.release()
        wrongSound.release()
        // Detener música del quiz
        MusicManager.stop(this)
        // Opcional: volver a reproducir música del Main
        MusicManager.play(this, R.raw.musica_main)
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
    private fun startRecording() {
        recordedFile = "${externalCacheDir?.absolutePath}/voz_usuario.3gp"

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(recordedFile)
            prepare()
            start()
        }

        questionText.text = "🎙️ Grabando... Habla ahora!"
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    private fun playWithEffect(
        onStartSpeaking: (() -> Unit)? = null,
        onFinishSpeaking: (() -> Unit)? = null
                              ) {
        if (recordedFile == null) return

        val player = MediaPlayer()
        player.setDataSource(recordedFile)
        player.prepare()

        // 🎵 Ajuste de tono/velocidad (voz graciosa)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val params = player.playbackParams
            params.pitch = 1.9f
            params.speed = 1.3f
            player.playbackParams = params
        }

        // 🔊 Subir volumen de la voz
        player.setVolume(1.0f, 1.0f)

        // 🔇 Bajar o mutear la música de fondo mientras habla
        MusicManager.mute()


        // 📢 Notificar inicio
        onStartSpeaking?.invoke()
        player.start()

        // 📢 Cuando termina
        player.setOnCompletionListener {
            // Restaurar la música de fondo
            MusicManager.unmute()

            // Notificar que terminó de hablar
            onFinishSpeaking?.invoke()
            player.release()
        }
    }


}
