package com.example.myapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView


class SeleccionarPreguntasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_seleccionar_preguntas)

        val avatarNombre = intent.getStringExtra("avatarNombre")
        val avatarImagen = intent.getIntExtra("avatarImagen", 0)

        val textView = findViewById<TextView>(R.id.textAvatarSeleccionado)
        val imageView = findViewById<ImageView>(R.id.imgAvatarSeleccionado)

        textView.text = "Has elegido: $avatarNombre"
        imageView.setImageResource(avatarImagen)
    }
}