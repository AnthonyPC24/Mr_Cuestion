package com.example.myapplication.model

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File

class FilesManager {
    companion object {

        private fun getJsonFile(context: Context): File {
            val dir = File(context.filesDir, "json")
            if (!dir.exists()) dir.mkdirs()
            return File(dir, "partidas.json")
        }

        fun savePartida(context: Context, partida: Partida) {
            val partidasExistentes = loadPartidas(context).toMutableList()
            partidasExistentes.add(partida)
            savePartidas(context, partidasExistentes)
        }

        fun savePartidas(context: Context, partidas: List<Partida>) {
            val gson = GsonBuilder().setPrettyPrinting().create()
            val file = getJsonFile(context)
            file.writeText(gson.toJson(partidas))
        }

        fun loadPartidas(context: Context): List<Partida> {
            val gson = Gson()
            val file = getJsonFile(context)
            if (!file.exists()) return emptyList()

            val json = file.readText()
            if (json.isEmpty()) return emptyList()

            val type = object : TypeToken<List<Partida>>() {}.type
            return gson.fromJson(json, type)
        }
    }
}

