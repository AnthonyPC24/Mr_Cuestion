package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.pm.PackageManager
import android.view.animation.ScaleAnimation
import org.json.JSONArray
import com.example.myapplication.model.Avatar


class SeleccionarAvatarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_seleccionar_avatar)



        val avatares = listOf(Avatar(R.id.avatar1, "avatar1", R.drawable.avatar1) ,
                              Avatar(R.id.avatar2, "avatar2", R.drawable.avatar2),
                              Avatar(R.id.avatar3, "avatar3", R.drawable.avatar3),
                              Avatar(R.id.avatar4, "avatar4", R.drawable.avatar4),
                              Avatar(R.id.avatar5, "avatar5", R.drawable.avatar5),
                              Avatar(R.id.avatar6, "avatar6", R.drawable.avatar6))


        // Animacion de movimiento de los avatares
        val animation = ScaleAnimation(
            1f, 1.1f, // Escala X de 1 a 1.1
            1f, 1.1f, // Escala Y de 1 a 1.1
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f, // Centro X
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f  // Centro Y
                                      ).apply {
            duration = 1000
            repeatMode = ScaleAnimation.REVERSE
            repeatCount = ScaleAnimation.INFINITE
        }

        avatares.forEach { avatar ->
            val imageView = findViewById<ImageView>(avatar.id)
            imageView.startAnimation(animation)

            imageView.setOnClickListener {
                guardarDatosIniciales(avatar)
                Toast.makeText(this, "Elegiste ${avatar.nombre} 🎉", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, SeleccionarPreguntasActivity::class.java)

                intent.putExtra("avatarNombre", avatar.nombre)
                intent.putExtra("avatarImagen", avatar.imagen)

                startActivity(intent)
                finish()
            }
        }


        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }

    }
    private fun guardarDatosIniciales(avatar: Avatar) {
        val jsonPartida = JSONObject().apply {
            put("avatar",avatar.nombre )
            put("tiempoPartida", "")
            put("errores", 0)
            put("puntuacion", 0)
            put("fechaHora", obtenerFechaActual()) }

        //guardar copia en la carpeta publica dowloads para que el gestor externo pueda leerlo
        val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val archivosDescargas = File(downloads, "partidas.json")

       val jsonArray = if (archivosDescargas.exists()){
           JSONArray(archivosDescargas.readText())
       } else{
           JSONArray()
       }

        jsonArray.put(jsonPartida)
        archivosDescargas.writeText(jsonArray.toString(4))

    }

    private fun obtenerFechaActual(): String {
        val formato = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return formato.format(Date())
    }
}




