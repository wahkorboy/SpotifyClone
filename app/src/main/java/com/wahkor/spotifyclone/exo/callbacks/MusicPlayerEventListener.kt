package com.wahkor.spotifyclone.exo.callbacks

import android.widget.Toast
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.wahkor.spotifyclone.exo.MusicService

class MusicPlayerEventListener(
    private val musicService: MusicService
) : Player.Listener{
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
        if (playbackState==Player.STATE_READY && !playWhenReady){
            musicService.stopForeground(false)
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        Toast.makeText(musicService,"An unknown error occurred",Toast.LENGTH_SHORT).show()
    }
}