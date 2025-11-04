package com.example.myapplication.util

import android.content.Context
import android.media.MediaPlayer

object AudioPlayer {
    private var mediaPlayer: MediaPlayer? = null

    fun play(context: Context, audioName: String) {
        val resId = context.resources.getIdentifier(audioName, "raw", context.packageName)
        if (resId != 0) {
            stop()
            mediaPlayer = MediaPlayer.create(context, resId)
            mediaPlayer?.start()
        }
    }

    fun stop() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
