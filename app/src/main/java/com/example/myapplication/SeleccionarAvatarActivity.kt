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
import android.view.LayoutInflater
import android.view.animation.ScaleAnimation
import androidx.constraintlayout.widget.ConstraintSet
import org.json.JSONArray
import com.example.myapplication.model.Avatar
import com.example.myapplication.model.FilesManager
import com.example.myapplication.model.Partida
import android.widget.EditText
import androidx.appcompat.app.AlertDialog


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

                mostrarDialogoNombreJugador(avatar)
            }
        }
    }
}

private fun SeleccionarAvatarActivity.mostrarDialogoNombreJugador(avatar: Avatar) {
    val dialogView = LayoutInflater.from(this)
        .inflate(R.layout.dialog_nombre_jugador, null)

    val inputNombre = dialogView.findViewById<EditText>(R.id.etNombreJugador)

    AlertDialog.Builder(this)
        .setTitle("Nombre del jugador")
        .setView(dialogView)
        .setCancelable(false)
        .setPositiveButton("Aceptar") { _, _ ->
            val nombreJugador = inputNombre.text.toString().trim()

            if (nombreJugador.isEmpty()) {
                Toast.makeText(this, "Debes introducir un nombre", Toast.LENGTH_SHORT).show()
            }

            // Pasamos avatar + nombre del jugador
            val intent = Intent(this, SeleccionarPreguntasActivity::class.java)
            intent.putExtra("avatarNombre", avatar.nombre)
            intent.putExtra("avatarImagen", avatar.imagen)
            intent.putExtra("nombreJugador", nombreJugador)

            Toast.makeText(
                this,
                "Jugador: $nombreJugador\nAvatar: ${avatar.nombre}",
                Toast.LENGTH_SHORT
                          ).show()

            startActivity(intent)
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
            finish()
        }
        .show()
}




