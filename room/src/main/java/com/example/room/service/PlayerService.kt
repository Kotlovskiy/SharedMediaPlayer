package com.example.room.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
import javax.inject.Inject

@OptIn(UnstableApi::class)
class PlayerService constructor(
    exoPlayer: ExoPlayer,
    notificationManager: PlayerNotificationManager,
    mediaSession: MediaSession
): Service() {
    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}
