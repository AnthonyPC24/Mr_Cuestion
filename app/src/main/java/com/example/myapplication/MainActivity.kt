package com.example.myapplication

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.Animation
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {

    private lateinit var tapSound: MediaPlayer

    private val gifs = arrayOf(
        R.drawable.tenna_cane, R.drawable.tenna_spinning, R.drawable.personaje_gif
                              )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 🎵 Música de fondo
        MusicManager.play(this, R.raw.musica_main)

        // 🔊 Sonido del botón
        tapSound = MediaPlayer.create(this, R.raw.tap)

        val personaje = findViewById<ImageView>(R.id.personaje)
        val btnInicio = findViewById<ImageView>(R.id.btnInicio)

        // Mostrar GIF aleatorio
        Glide.with(this).asGif().load(gifs.random()).into(personaje)

        // --- Animación de pulso ---
        val scaleAnimation = ScaleAnimation(
            1f, 1.1f,
            1f, 1.1f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f
                                           ).apply {
            duration = 1000
            repeatMode = ScaleAnimation.REVERSE
            repeatCount = ScaleAnimation.INFINITE
        }

        btnInicio.startAnimation(scaleAnimation)

        // --- EFECTO DE BOTÓN PRESIONADO + SONIDO ---
        btnInicio.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    btnInicio.setImageResource(R.drawable.boton_inicio_presionado)

                    // 🔊 Reproducir sonido TAP
                    if (tapSound.isPlaying) {
                        tapSound.seekTo(0)
                    }
                    tapSound.start()
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    btnInicio.setImageResource(R.drawable.boton_inicio)

                    if (event.action == MotionEvent.ACTION_UP) {
                        btnInicio.performClick()
                    }
                }
            }
            true
        }

        val btnInfo = findViewById<ImageView>(R.id.btnInfo)
        val animation = TranslateAnimation(
            0f, 0f,      // fromX, toX
            -20f, 20f    // fromY, toY (sube y baja)
                                          ).apply {
            duration = 2000          // velocidad (2 segundos)
            repeatCount = Animation.INFINITE
            repeatMode = Animation.REVERSE
        }

        btnInfo.startAnimation(animation)

        btnInfo.setOnClickListener {
            val intent = Intent(this, UsuariosActivity::class.java)
            startActivity(intent)
        }


        // --- CLICK PARA CAMBIAR DE ACTIVITY ---
        btnInicio.setOnClickListener {
            btnInicio.clearAnimation()
            val intent = Intent(this, SeleccionarAvatarActivity::class.java)
            startActivity(intent)
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
            finish()
        }

        moverPersonajeAleatorio()
    }

    // --- MOVIMIENTO DEL PERSONAJE ---
    private fun moverPersonajeAleatorio() {
        val personaje = findViewById<ImageView>(R.id.personaje)

        val pantallaAncho = resources.displayMetrics.widthPixels
        val pantallaAlto = resources.displayMetrics.heightPixels

        val zonaProhibidaTop = 0
        val zonaProhibidaBottom = pantallaAlto * 0.35

        personaje.visibility = ImageView.VISIBLE

        val lado = (1..4).random()
        var startX = 0f
        var startY = 0f

        when (lado) {
            1 -> { startX = -200f; startY = (100..pantallaAlto - 200).random().toFloat() }
            2 -> { startX = pantallaAncho + 200f; startY = (100..pantallaAlto - 200).random().toFloat() }
            3 -> { startX = (0..pantallaAncho).random().toFloat(); startY = -200f }
            4 -> { startX = (0..pantallaAncho).random().toFloat(); startY = pantallaAlto + 200f }
        }

        if (startY.toInt() in zonaProhibidaTop..zonaProhibidaBottom.toInt()) {
            startY = zonaProhibidaBottom.toFloat() + 50
        }

        personaje.x = startX
        personaje.y = startY

        val endX = (100..(pantallaAncho - 200)).random().toFloat()
        val endY = ((zonaProhibidaBottom.toInt() + 100)..(pantallaAlto - 200)).random().toFloat()

        personaje.animate().x(endX).y(endY).setDuration(2000).withEndAction {

            val salidaX = if ((0..1).random() == 0) -300f else pantallaAncho + 300f
            val salidaY = (0..pantallaAlto).random().toFloat()

            personaje.animate().x(salidaX).y(salidaY).setDuration(2000).withEndAction {
                if (!isFinishing && !isDestroyed) {
                    Glide.with(this).asGif().load(gifs.random()).into(personaje)
                    moverPersonajeAleatorio()
                }
            }.start()
        }
    }
}
