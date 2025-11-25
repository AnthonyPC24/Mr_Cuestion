package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.myapplication.model.Partida
import com.example.myapplication.model.UsuarioResumen
import com.google.gson.Gson
import java.io.File
import com.example.myapplication.utils.generarHallOfFame
import com.bumptech.glide.Glide


class UsuariosActivity : AppCompatActivity() {

    private lateinit var users: List<UsuarioResumen>
    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuarios)

        val avatarImage = findViewById<ImageView>(R.id.avatarImage)
        val userName = findViewById<TextView>(R.id.userName)
        val txtVictorias = findViewById<TextView>(R.id.txtVictorias)
        val txtErrores = findViewById<TextView>(R.id.txtErrores)
        val txtPuntuacionTotal = findViewById<TextView>(R.id.txtPuntuacionTotal)
        val txtTiempoTotal = findViewById<TextView>(R.id.txtTiempoTotal)
        val btnNextUser = findViewById<ImageView>(R.id.btnNextUser)
        val volverAlMenuFlecha = findViewById<ImageView>(R.id.volverAlMenuFlecha)


        val miFuente = ResourcesCompat.getFont(this, R.font.hollywood)
        userName.typeface = miFuente

        val gifView = findViewById<ImageView>(R.id.confettiGif)

        Glide.with(this)
            .asGif()
            .load(R.drawable.confetti)
            .into(gifView)



        // 1. Leer JSON real
        val partidas = loadPartidasFromInternalStorage()

        // 2. Convertir partidas a usuarios resumidos
        users = generarHallOfFame(partidas)

        fun updateUser() {
            val u = users[index]

            // Lista de views a animar
            val views = listOf(userName, txtVictorias, txtErrores, txtPuntuacionTotal, txtTiempoTotal, avatarImage)

            // Animación: slide-out a la derecha + fade-out
            views.forEach { v ->
                v.animate()
                    .translationX(200f)
                    .alpha(0f)
                    .setDuration(200)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            }

            // Delay para actualizar valores después de slide-out
            avatarImage.postDelayed({

                                        // Reset posición para slide-in desde la derecha
                                        views.forEach { v ->
                                            v.translationX = -200f
                                        }

                                        // Actualizar textos y avatar
                                        userName.text = u.nombre
                                        txtVictorias.text = "Victorias perfectas: ${u.victoriasPerfectas}"
                                        txtErrores.text = "Errores en total: ${u.erroresTotales}"

                                        val partidasUsuario = partidas.filter { it.nombreJugador == u.nombre }

                                        val puntuacionTotal = partidasUsuario.sumOf { it.puntuacion }
                                        txtPuntuacionTotal.text = "Puntuación total: $puntuacionTotal"

                                        val totalSegundos = partidasUsuario.sumOf { partida ->
                                            val partes = partida.tiempoPartida.split(":")
                                            val minutos = partes.getOrNull(0)?.toIntOrNull() ?: 0
                                            val segundos = partes.getOrNull(1)?.toIntOrNull() ?: 0
                                            minutos * 60 + segundos
                                        }

                                        val horas = totalSegundos / 3600
                                        val minutos = (totalSegundos % 3600) / 60
                                        val segundos = totalSegundos % 60
                                        val tiempoFormateado = if (horas > 0) {
                                            String.format("%02d:%02d:%02d", horas, minutos, segundos)
                                        } else {
                                            String.format("%02d:%02d", minutos, segundos)
                                        }
                                        txtTiempoTotal.text = "Tiempo total jugado: $tiempoFormateado"

                                        val avatarRes = resources.getIdentifier(u.avatar, "drawable", packageName)
                                        avatarImage.setImageResource(
                                            if (avatarRes != 0) avatarRes else R.drawable.circle_white
                                                                    )

                                        // Animación slide-in desde la izquierda + fade-in
                                        views.forEach { v ->
                                            v.animate()
                                                .translationX(0f)
                                                .alpha(1f)
                                                .setDuration(300)
                                                .setInterpolator(DecelerateInterpolator())
                                                .start()
                                        }

                                    }, 200) // Debe coincidir con duración de slide-out
        }

        updateUser()

        btnNextUser.setOnClickListener {
            index = (index + 1) % users.size
            updateUser()
        }

        volverAlMenuFlecha.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun loadPartidasFromInternalStorage(): List<Partida> {
        val file = File(filesDir, "json/partidas.json")

        if (!file.exists()) {
            Log.e("JSON", "Archivo NO encontrado en: ${file.absolutePath}")
            return emptyList()
        }

        val jsonText = file.readText()

        return try {
            Gson().fromJson(jsonText, Array<Partida>::class.java).toList()
        } catch (e: Exception) {
            Log.e("JSON", "Error leyendo JSON: ${e.message}")
            emptyList()
        }
    }


}
