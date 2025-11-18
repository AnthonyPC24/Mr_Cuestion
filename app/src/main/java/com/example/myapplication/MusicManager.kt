package com.example.myapplication

import android.content.Context
import android.media.MediaPlayer

object MusicManager {
    private var mediaPlayer: MediaPlayer? = null
    private var currentResId: Int? = null

    fun play(context: Context, resId: Int, loop: Boolean = true, volume: Float = 1.0f) {
        // Si ya está reproduciendo la misma canción, no hacer nada
        if (currentResId == resId && mediaPlayer?.isPlaying == true) return

        stop(context) // Detener la canción anterior

        mediaPlayer = MediaPlayer.create(context, resId).apply {
            isLooping = loop
            setVolume(volume, volume)
            start()
        }
        currentResId = resId
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun resume() {
        mediaPlayer?.start()
    }

    fun stop(context: Context) {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        currentResId = null
    }

    fun setVolume(left: Float, right: Float) {
        mediaPlayer?.setVolume(left, right)
    }

    fun mute() {
        mediaPlayer?.setVolume(0f, 0f)
    }

    fun unmute() {
        mediaPlayer?.setVolume(1f, 1f)
    }

}
