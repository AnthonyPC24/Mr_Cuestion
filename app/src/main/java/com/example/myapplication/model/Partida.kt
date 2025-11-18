package com.example.myapplication.model

import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

data class Partida(
    val id: Long = System.currentTimeMillis(),
    var avatar: String,
    var nombreJugador: String = "",
    var numPreguntas: Int = 0,
    var dificultad: String = "",
    var puntuacion: Int = 0,
    var errores: Int = 0,
    var tiempoPartida: String = "",
    var fechaHoraInicio: String = obtenerFechaActual()
                  ) {
    companion object {
        fun obtenerFechaActual(): String {
            val formato = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            return formato.format(Date())
        }
    }
}