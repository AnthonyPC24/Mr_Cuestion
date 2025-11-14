package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.animation.ScaleAnimation
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val personaje = findViewById<ImageView>(R.id.personaje)

        Glide.with(this)
            .asGif()
            .load(R.drawable.tenna_cane)
            .into(personaje)

        val btnInicio = findViewById<ImageView>(R.id.btnInicio)

        // Animación de profundidad (efecto "pulso")
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

        moverPersonajeAleatorio()


        // inicia la animacion de profundidad
        btnInicio.startAnimation(scaleAnimation)

        // Al hacer clic, para animación y pasar a la siguiente pantalla
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
            val zonaProhibidaBottom = pantallaAlto * 0.35  // 35% del top no permitido

            personaje.visibility = ImageView.VISIBLE

            // Elige un lado aleatorio
            val lado = (1..4).random()
            var startX = 0f
            var startY = 0f

            when (lado) {
                1 -> { // Izquierda
                    startX = -200f
                    startY = (100..pantallaAlto - 200).random().toFloat()
                }
                2 -> { // Derecha
                    startX = pantallaAncho + 200f
                    startY = (100..pantallaAlto - 200).random().toFloat()
                }
                3 -> { // Arriba
                    startX = (0..pantallaAncho).random().toFloat()
                    startY = -200f
                }
                4 -> { // Abajo
                    startX = (0..pantallaAncho).random().toFloat()
                    startY = pantallaAlto + 200f
                }
            }

            // Evitar la zona prohibida del título
            if (startY.toInt() in zonaProhibidaTop..zonaProhibidaBottom.toInt()) {
                startY = zonaProhibidaBottom.toFloat() + 50
            }

            personaje.x = startX
            personaje.y = startY

            // Punto de destino dentro de pantalla
            val endX = (100..(pantallaAncho - 200)).random().toFloat()
            val endY = ((zonaProhibidaBottom.toInt() + 100)..(pantallaAlto - 200)).random().toFloat()

            personaje.animate()
                .x(endX)
                .y(endY)
                .setDuration(2000)
                .withEndAction {
                    // Luego sale hacia afuera por otro lado
                    val salidaX = if ((0..1).random() == 0) -300f else pantallaAncho + 300f
                    val salidaY = (0..pantallaAlto).random().toFloat()

                    personaje.animate()
                        .x(salidaX)
                        .y(salidaY)
                        .setDuration(2000)
                        .withEndAction {
                            // Repetir luego de un tiempo aleatorio
                            personaje.visibility = ImageView.INVISIBLE
                            personaje.postDelayed({
                                                      moverPersonajeAleatorio()
                                                  }, (2000..5000).random().toLong())
                        }
                }
                .start()
        }

    }
