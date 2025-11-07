package com.example.myapplication.model

import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

data class Partida(
    var avatar: String,
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

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("avatar", avatar)
            put("numPreguntas", numPreguntas)
            put("dificultad", dificultad)
            put("puntuacion", puntuacion)
            put("errores", errores)
            put("tiempoPartida", tiempoPartida)
            put("fechaHora", fechaHoraInicio)
        }
    }
}