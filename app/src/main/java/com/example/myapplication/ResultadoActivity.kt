package com.example.myapplication

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlin.math.cos
import kotlin.math.sin



class ResultadoActivity : AppCompatActivity() {

    private lateinit var clickSound: MediaPlayer
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado)

        clickSound = MediaPlayer.create(this, R.raw.tap)
        clickSound.setVolume(1.0f, 1.0f)

        mediaPlayer = MediaPlayer.create(this, R.raw.resultados)
        mediaPlayer.isLooping = true
        mediaPlayer.setVolume(1.0f, 1.0f)
        mediaPlayer.start()

        val btnReintentar = findViewById<ImageView>(R.id.btnReintentar)

        btnReintentar.setOnTouchListener { view, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    btnReintentar.setImageResource(R.drawable.boton_volver_a_jugar_presionado)
                    clickSound.start()
                }
                android.view.MotionEvent.ACTION_UP,
                android.view.MotionEvent.ACTION_CANCEL -> {
                    btnReintentar.setImageResource(R.drawable.boton_volver_a_jugar)
                    // Acción al soltar: volver al MainActivity
                    mediaPlayer.stop()
                    mediaPlayer.release()

                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    finish()
                }
            }
            true
        }

        val textResultado = findViewById<TextView>(R.id.textResultado)
        val orbitContainer = findViewById<FrameLayout>(R.id.orbitContainer)
        val characterGif = findViewById<ImageView>(R.id.characterGif)

        // 🎯 Datos del quiz
        val totalPreguntas = intent.getIntExtra("TOTAL_PREGUNTAS", 0)
        val respuestasCorrectas = intent.getIntExtra("RESPUESTAS_CORRECTAS", 0)
        val correctImages = intent.getStringArrayListExtra("CORRECT_IMAGES") ?: arrayListOf()

        // 🗨️ Mostrar texto del resultado
        textResultado.text = "¡Has acertado $respuestasCorrectas de $totalPreguntas!"

        // 🕺 Mostrar GIF del personaje
        Glide.with(this)
            .asGif()
            .load(R.drawable.tennagirando)
            .into(characterGif)

        // 🌀 Añadir las imágenes correctas girando alrededor del personaje
        addOrbitingImages(orbitContainer, correctImages)
    }

    private fun addOrbitingImages(container: FrameLayout, imageNames: List<String>) {

        container.post {
            val radius = 350f  // Ajusta el radio si quieres más o menos distancia
            val total = imageNames.size

            imageNames.forEachIndexed { index, imageName ->

                val imageView = ImageView(this)
                val resId = resources.getIdentifier(imageName, "drawable", packageName)
                imageView.setImageResource(resId)

                val size = 160
                imageView.layoutParams = FrameLayout.LayoutParams(size, size)
                container.addView(imageView)

                val angleOffset = (360f / total) * index

                val animator = ValueAnimator.ofFloat(0f, 360f)
                animator.duration = 6000L
                animator.repeatCount = ValueAnimator.INFINITE
                animator.interpolator = LinearInterpolator()

                animator.addUpdateListener { animation ->
                    val angle = (animation.animatedValue as Float + angleOffset) * Math.PI / 180

                    val x = (container.width / 2 + radius * cos(angle)).toFloat() - size / 2
                    val y = (container.height / 2 + radius * sin(angle)).toFloat() - size / 2

                    imageView.x = x
                    imageView.y = y
                }

                animator.start()
            }
        }
    }

}
