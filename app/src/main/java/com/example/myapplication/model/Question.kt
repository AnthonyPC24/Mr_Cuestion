package com.example.myapplication.model

data class Question(
    val text: String,             // Texto de la pregunta
    val audio: String,            // Nombre del archivo mp3 (sin extensión)
    val options: List<String>,    // Lista de imágenes (nombre sin extensión)
    val correctIndex: Int         // Respuesta correcta
                   )