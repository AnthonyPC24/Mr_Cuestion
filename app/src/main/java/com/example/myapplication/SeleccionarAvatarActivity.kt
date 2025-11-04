package com.example.myapplication

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Toast
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale

class SeleccionarAvatarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_seleccionar_avatar)
        val avatares = listOf(findViewById<ImageView>(R.id.avatar1) ,
                    findViewById<ImageView>(R.id.avatar2),
                    findViewById<ImageView>(R.id.avatar3),
                    findViewById<ImageView>(R.id.avatar4),
                    findViewById<ImageView>(R.id.avatar5),
                    findViewById<ImageView>(R.id.avatar6))

        val nombresAvatar = listOf("avata1.png",
                                   "avata2.png",
                                   "avata3.png",
                                   "avata4.png",
                                   "avata5.png",
                                   "avata5.png",)


        avatares.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                val nombreAvatar = nombresAvatar[index]
                guardarDatosIniciales(nombreAvatar)
                Toast.makeText(this, "Elegiste $nombreAvatar 🎉", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun guardarDatosIniciales(nombreAvatar: String) {
        val json = JSONObject()
        json.put("avatar", nombreAvatar)
        json.put("tiempoPartida", "")
        json.put("errores", 0)
        json.put("puntuacion", 0)
        json.put("fechaHora", obtenerFechaActual())

        val archivo = File(filesDir, "partida.json")
        archivo.writeText(json.toString(4)) // Formateado y legible

        println("✅ Archivo JSON creado en: ${archivo.absolutePath}")
    }

    private fun obtenerFechaActual(): String {
        val formato = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return formato.format(Date())
    }
}




