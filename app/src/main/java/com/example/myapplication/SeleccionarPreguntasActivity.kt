package com.example.myapplication

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.model.Partida
import android.view.animation.ScaleAnimation
import android.widget.Toast
import org.json.JSONArray
import java.io.File


class SeleccionarPreguntasActivity : AppCompatActivity() {

    private lateinit var partida: Partida

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_seleccionar_preguntas)

        val avatarNombre = intent.getStringExtra("avatarNombew")?:"avatar_desconocido"
        partida = Partida(avatar = avatarNombre)

        mostrarSelectorPregutnas()
    }

    private fun mostrarSelectorPregutnas() {
        val contenedor = findViewById<android.widget.FrameLayout>(R.id.contenedorOpciones)
        val layout = LayoutInflater.from(this).inflate(R.layout.layout_numero_preguntas, contenedor, false)
        contenedor.removeAllViews()
        contenedor.addView(layout)

        val botones = listOf(
            layout.findViewById<ImageView>(R.id.btn5) to 5,
            layout.findViewById<ImageView>(R.id.btn10) to 10,
            layout.findViewById<ImageView>(R.id.btn15) to 15)

        val anim = crearAnimacion()

        botones.forEach { (boton, cantidad) ->
            boton.startAnimation(anim)
            boton.setOnClickListener {
                partida.numPreguntas = cantidad
                Toast.makeText(this, "Elegiste $cantidad preguntas", Toast.LENGTH_SHORT).show()
                mostrarSelectorDificultad()
            }
        }

    }

    private fun mostrarSelectorDificultad(){

        val contenedor = findViewById<android.widget.FrameLayout>(R.id.contenedorOpciones)
        val layout = LayoutInflater.from(this).inflate(R.layout.layout_dificultad, contenedor, false)
        contenedor.removeAllViews()
        contenedor.addView(layout)

        val botones = mapOf(
            layout.findViewById<ImageView>(R.id.btnFacil) to "Fácil",
            layout.findViewById<ImageView>(R.id.btnMedia) to "Medio",
            layout.findViewById<ImageView>(R.id.btnDificil) to "Difícil"
                           )

        val anim = crearAnimacion()

        botones.forEach { (boton, dificultad) ->
            boton.startAnimation(anim)
            boton.setOnClickListener {
                partida.dificultad = dificultad
                Toast.makeText(this, "Dificultad: $dificultad ⚡", Toast.LENGTH_SHORT).show()
                guardarPartida()
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

    private fun guardarPartida(){
        val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val archivo = File(downloads, "partidas.json")

        val jsonArray = if (archivo.exists()){
            JSONArray(archivo.readText())
        } else{
            JSONArray()
        }

        jsonArray.put(partida.toJson())
        archivo.writeText(jsonArray.toString(4))
    }
}