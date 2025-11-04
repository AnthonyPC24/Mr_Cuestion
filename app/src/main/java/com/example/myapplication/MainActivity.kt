package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.animation.ScaleAnimation

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val btnInicio = findViewById<ImageView>(R.id.btnInicio)

        // Animación de profundidad (efecto "pulso")
        val scaleAnimation = ScaleAnimation(
            1f, 1.1f, // Escala X de 1 a 1.1
            1f, 1.1f, // Escala Y de 1 a 1.1
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f, // Centro X
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f  // Centro Y
                                           ).apply {
            duration = 1000
            repeatMode = ScaleAnimation.REVERSE
            repeatCount = ScaleAnimation.INFINITE
        }

        btnInicio.startAnimation(scaleAnimation)

        // Al hacer clic, parar animación y pasar a la siguiente pantalla
        btnInicio.setOnClickListener {
            btnInicio.clearAnimation()
            val intent = Intent(this, SeleccionarAvatarActivity::class.java)
            startActivity(intent)
        }

    }
}