package com.example.myapplication

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.model.FilesManager
import com.example.myapplication.model.Partida
import java.io.File
import org.json.JSONArray
import android.view.animation.ScaleAnimation

class SeleccionarPreguntasActivity : AppCompatActivity() {

    private lateinit var avatarNombre: String
    private var numPreguntas: Int = 0
    private var dificultad: String = ""
    private lateinit var nombreJugador: String

    private var avatarImagen: Int = 0
    private lateinit var avatarImagenName: String


    private lateinit var tapSound: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle? ) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_seleccionar_preguntas)

        avatarNombre = intent.getStringExtra("avatarNombre") ?: "avatar_desconocido"
        nombreJugador = intent.getStringExtra("nombreJugador") ?: "Jugador"

        avatarImagen = intent.getIntExtra("avatarImagen", R.drawable.avatar1)
        avatarImagenName = intent.getStringExtra("avatarImagenName") ?: "avatar_desconocido"

        // 🔊 Inicializar sonido tap
        tapSound = MediaPlayer.create(this, R.raw.tap)

        mostrarSelectorPregutnas()
    }

    private fun reproducirTap() {
        if (tapSound.isPlaying) {
            tapSound.seekTo(0)
        }
        tapSound.start()
    }

    private fun mostrarSelectorPregutnas() {
        val contenedor = findViewById<android.widget.FrameLayout>(R.id.contenedorOpciones)
        val layout = LayoutInflater.from(this).inflate(R.layout.layout_numero_preguntas, contenedor, false)
        contenedor.removeAllViews()
        contenedor.addView(layout)

        val botones = listOf(
            layout.findViewById<ImageView>(R.id.btn5) to 5,
            layout.findViewById<ImageView>(R.id.btn10) to 10,
            layout.findViewById<ImageView>(R.id.btn15) to 15
                            )

        val anim = crearAnimacion()

        botones.forEach { (boton, cantidad) ->
            boton.startAnimation(anim)

            boton.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // 🔊 Sonido al pulsar
                        reproducirTap()

                        when (cantidad) {
                            5 -> boton.setImageResource(R.drawable.boton_cinco_presionado)
                            10 -> boton.setImageResource(R.drawable.boton_diez_presionado)
                            15 -> boton.setImageResource(R.drawable.boton_quince_presionado)
                        }
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        when (cantidad) {
                            5 -> boton.setImageResource(R.drawable.boton_cinco)
                            10 -> boton.setImageResource(R.drawable.boton_diez)
                            15 -> boton.setImageResource(R.drawable.boton_quince)
                        }
                        if (event.action == MotionEvent.ACTION_UP) {
                            boton.performClick()
                        }
                    }
                }
                true
            }

            boton.setOnClickListener {
                numPreguntas = cantidad
                Toast.makeText(this, "Elegiste $cantidad preguntas", Toast.LENGTH_SHORT).show()
                mostrarSelectorDificultad()
            }
        }
    }

    private fun mostrarSelectorDificultad() {
        val contenedor = findViewById<android.widget.FrameLayout>(R.id.contenedorOpciones)
        val layout = LayoutInflater.from(this).inflate(R.layout.layout_dificultad, contenedor, false)
        contenedor.removeAllViews()
        contenedor.addView(layout)

        val btnFacil = layout.findViewById<ImageView>(R.id.btnFacil)
        val btnMedio = layout.findViewById<ImageView>(R.id.btnMedia)
        val btnDificil = layout.findViewById<ImageView>(R.id.btnDificil)

        val anim = crearAnimacion()
        btnFacil.startAnimation(anim)
        btnMedio.startAnimation(anim)
        btnDificil.startAnimation(anim)

        val bgLlama = layout.findViewById<ImageView>(R.id.bgLlama)
        Glide.with(this)
            .asGif()
            .load(R.drawable.flames)
            .into(bgLlama)

        val botones = mapOf(
            btnFacil to "Fácil",
            btnMedio to "Medio",
            btnDificil to "Difícil"
                           )

        botones.forEach { (boton, dificultadElegida) ->

            boton.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        reproducirTap()  // 🔊 Sonido
                        when (dificultadElegida) {
                            "Fácil" -> boton.setImageResource(R.drawable.boton_facil_presionado)
                            "Medio" -> boton.setImageResource(R.drawable.boton_medio_presionado)
                            "Difícil" -> boton.setImageResource(R.drawable.boton_dificil_presionado)
                        }
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        when (dificultadElegida) {
                            "Fácil" -> boton.setImageResource(R.drawable.boton_facil)
                            "Medio" -> boton.setImageResource(R.drawable.boton_medio)
                            "Difícil" -> boton.setImageResource(R.drawable.boton_dificil)
                        }
                        if (event.action == MotionEvent.ACTION_UP) {
                            boton.performClick()
                        }
                    }
                }
                true
            }

            boton.setOnClickListener {
                dificultad = dificultadElegida

                val partida = Partida(
                    avatar = avatarImagenName.substringBeforeLast("."),
                    nombreJugador = nombreJugador,
                    numPreguntas = numPreguntas,
                    dificultad = dificultad
                                     )

                Toast.makeText(this, "Dificultad: $dificultad ⚡", Toast.LENGTH_SHORT).show()
                FilesManager.savePartida(this, partida)

                val intent = Intent(this, QuizActivity::class.java)

                intent.putExtra("NUM_PREGUNTAS", numPreguntas)
                intent.putExtra("DIFICULTAD", dificultad)

                intent.putExtra("avatarNombre", avatarNombre)
                intent.putExtra("nombreJugador", nombreJugador)
                intent.putExtra("avatarImagen", avatarImagen)
                intent.putExtra("avatarImagenName", avatarImagenName)

                startActivity(intent)
                finish()

            }
        }
    }

    private fun crearAnimacion(): ScaleAnimation {
        return ScaleAnimation(
            1f, 1.1f,
            1f, 1.1f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f
                             ).apply {
            duration = 900
            repeatMode = ScaleAnimation.REVERSE
            repeatCount = ScaleAnimation.INFINITE
        }
    }
}
