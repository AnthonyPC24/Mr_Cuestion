package com.example.myapplication.utils

import com.example.myapplication.model.Partida
import com.example.myapplication.model.UsuarioResumen

fun generarHallOfFame(partidas: List<Partida>): List<UsuarioResumen> {

    return partidas.groupBy { it.nombreJugador.ifBlank { "Anónimo" } }
        .map { (nombre, lista) ->

            val avatarFinal = lista.last().avatar

            UsuarioResumen(
                nombre = nombre,
                avatar = avatarFinal,
                victoriasPerfectas = lista.count {
                    it.puntuacion == it.numPreguntas && it.numPreguntas > 0
                },
                erroresTotales = lista.sumOf { it.errores }
                          )
        }
}
