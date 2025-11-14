package com.example.myapplication

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.view.animation.ScaleAnimation
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {

    // Arreglo de GIFs para el personaje
    private val gifs = arrayOf(
        R.drawable.tenna_cane, R.drawable.tenna_spinning, R.drawable.personaje_gif
                              )




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        MusicManager.play(this, R.raw.musica_main)

        val personaje = findViewById<ImageView>(R.id.personaje)

        // Cargar un GIF aleatorio inicialmente
        Glide.with(this).asGif().load(gifs.random()).into(personaje)

        val btnInicio = findViewById<ImageView>(R.id.btnInicio)

        // Animación de "pulso" para el botón
        val scaleAnimation = ScaleAnimation(
            1f, 1.1f, // Escala X de 1 a 1.1
            1f, 1.1f, // Escala Y de 1 a 1.1
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f, // Centro X
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f  // Centro Y
                                           ).apply {
            duration = 1000
            repeatMode = ScaleAnimation.REVERSE
            repeatCount = ScaleAnimation.INFINITE
        }

        // Inicia la animación del botón
        btnInicio.startAnimation(scaleAnimation)

        // Inicia el movimiento aleatorio del personaje
        moverPersonajeAleatorio()

        // Al hacer clic, detener animación y pasar a la siguiente pantalla
        btnInicio.setOnClickListener {
            btnInicio.clearAnimation()
            val intent = Intent(this, SeleccionarAvatarActivity::class.java)
            startActivity(intent)
            @Suppress("DEPRECATION") overridePendingTransition(0, 0)
            finish()
        }
    }

    private fun moverPersonajeAleatorio() {
        val personaje = findViewById<ImageView>(R.id.personaje)

        val pantallaAncho = resources.displayMetrics.widthPixels
        val pantallaAlto = resources.displayMetrics.heightPixels

        // Área prohibida (donde está el título del juego)
        val zonaProhibidaTop = 0
        val zonaProhibidaBottom = pantallaAlto * 0.35  // 35% desde arriba

        personaje.visibility = ImageView.VISIBLE

        // Elige un lado aleatorio de donde aparecer
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

        // Inicia animación hacia el destino
        personaje.animate().x(endX).y(endY).setDuration(2000).withEndAction {

            // Animar salida hacia afuera
            val salidaX = if ((0..1).random() == 0) -300f else pantallaAncho + 300f
            val salidaY = (0..pantallaAlto).random().toFloat()

            personaje.animate().x(salidaX).y(salidaY).setDuration(2000).withEndAction {

                // Solo cargar Glide si la Activity sigue activa
                if (!isFinishing && !isDestroyed) {
                    Glide.with(this).asGif().load(gifs.random()).into(personaje)

                    // Reiniciar movimiento
                    moverPersonajeAleatorio()
                }

            }.start()
        }
    }
}
