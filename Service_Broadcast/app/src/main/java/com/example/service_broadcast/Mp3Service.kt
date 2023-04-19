package com.example.service_broadcast

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaMetadata
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat


class Mp3Service : Service() {
    companion object {
        const val CHANNEL_ID = "Channel id"
        const val ACTION_PLAY = "play"
        const val ACTION_PREV = "previous"
        const val ACTION_NEXT = "next"
        const val ACTION_GET_TIME = "action get time"
        const val ACTION_SEEK_TO = "seek to"
        const val ACTION_COMPLETE = "complete"
    }

    private val mediaPlayer by lazy { MediaPlayer() }
    private val mediaMetadataRetriever by lazy { MediaMetadataRetriever() }
    private val binder = LocalBinder()
    private val mediaSession by lazy { MediaSessionCompat(baseContext, "tag") }
    private var mp3Playlist = arrayListOf<Song>()
    private var mp3Position = -1
    private var positionList = arrayListOf<Int>()

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    fun setMp3List(list: ArrayList<Song>) {
        mp3Playlist = list
        mp3Playlist.forEachIndexed { index, _ -> positionList.add(index) }
    }

    fun getMp3IsPlaying() = mediaPlayer.isPlaying

    fun play(position: Int, callback: ((song: Song, duration: Int) -> Unit)? = null) {
        if (position != mp3Position) {
            mediaPlayer.apply {
                reset()
                setDataSource(applicationContext, mp3Playlist[position].uri)
                prepare()
                start()
            }
            mp3Position = position
        } else {
            if (mediaPlayer.isPlaying) mediaPlayer.start()
        }
        callback?.invoke(mp3Playlist[position], mediaPlayer.duration)
        mediaMetadataRetriever.setDataSource(applicationContext, mp3Playlist[position].uri)
        mediaPlayer.setOnCompletionListener {
            sendBroadcast(Intent(ACTION_COMPLETE))
        }
        if (mediaPlayer.isPlaying) showNotification(R.drawable.ic_pause)
        else showNotification(R.drawable.ic_play)
    }

    fun getCurrentTime(position: Int): Int {
        if (position == mp3Position) return mediaPlayer.currentPosition
        return 0
    }

    fun nextMp3(isIncrease: Boolean, callback: ((song: Song, duration: Int) -> Unit)? = null) {
        var position = mp3Position
        if (isIncrease) {
            if (position == mp3Playlist.size - 1) {
                position = 0
            } else {
                position++
            }
        }
        play(position, callback)
        callback?.invoke(mp3Playlist[position], mediaPlayer.duration)
    }

    fun prevMp3(isDecrease: Boolean, callback: ((song: Song, duration: Int) -> Unit)? = null) {
        var position = mp3Position
        if (isDecrease) {
            if (position == 0) {
                position = mp3Playlist.size - 1
            } else {
                position--
            }
        }
        play(position, callback)
        callback?.invoke(mp3Playlist[position], mediaPlayer.duration)
    }

    fun setShuffleMode(
        isIncrease: Boolean,
        callback: ((song: Song, duration: Int) -> Unit)? = null
    ) {
        if (positionList.size == 1) {
            mp3Playlist.forEachIndexed { index, _ ->
                if (index != mp3Position) positionList.add(
                    index
                )
            }
        }
        if (isIncrease) {
            positionList.remove(mp3Position)
        }
        val randomPosition = positionList.random()
        play(randomPosition, callback)
        callback?.invoke(mp3Playlist[randomPosition], mediaPlayer.duration)
    }

    fun setDefaultMode(): Boolean {
        if (mp3Position == mp3Playlist.size - 1) {
            mediaPlayer.pause()
            mediaPlayer.seekTo(0)
            setMediaSession()
            return true
        }
        return false
    }

    fun setRepeatOneMode(callback: ((song: Song, duration: Int) -> Unit)? = null) {
        mediaPlayer.start()
        mp3Position = mp3Position
        callback?.invoke(mp3Playlist[mp3Position], mediaPlayer.duration)
        mediaPlayer.setOnCompletionListener {
            sendBroadcast(Intent(ACTION_COMPLETE))
        }
        showNotification(R.drawable.ic_pause)
    }

    fun pauseOrReplay(isPlaying: Boolean) {
        if (isPlaying) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.start()
        }
        setMediaSession()
    }

    fun setTime(time: Int) {
        mediaPlayer.seekTo(time)
        setMediaSession()
    }

    inner class LocalBinder : Binder() {
        fun getService(): Mp3Service = this@Mp3Service
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun showNotification(imagePlay: Int) {
        setMediaSession()
        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(mediaSession.sessionToken)
        val intent = Intent(this, PlayMp3Activity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra(MainActivity.MP3_POSITION, mp3Position)
        val pendingIntent = PendingIntent.getActivity(
            baseContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val playPendingIntent = PendingIntent.getBroadcast(
            baseContext, 0, Intent(ACTION_PLAY), PendingIntent.FLAG_UPDATE_CURRENT
        )
        val prevPendingIntent = PendingIntent.getBroadcast(
            baseContext, 0, Intent(ACTION_PREV), PendingIntent.FLAG_UPDATE_CURRENT
        )
        val nextPendingIntent = PendingIntent.getBroadcast(
            baseContext, 0, Intent(ACTION_NEXT), PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder =
            NotificationCompat.Builder(baseContext, CHANNEL_ID).setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.cd)
                .setContentTitle(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE))
                .setContentText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST))
                .setStyle(mediaStyle)
                .addAction(R.drawable.ic_previous, "Previous", prevPendingIntent)
                .addAction(imagePlay, "Pause", playPendingIntent)
                .addAction(R.drawable.ic_next, "Next", nextPendingIntent).build()
        startForeground(11, builder)
    }

    private fun setMediaSession() {
        val playbackSpeed = if (mediaPlayer.isPlaying) 1F else 0F
        mediaSession.setMetadata(
            MediaMetadataCompat.Builder()
                .putLong(MediaMetadata.METADATA_KEY_DURATION, mediaPlayer.duration.toLong()).build()
        )
        setPlaybackState(playbackSpeed)
        mediaSession.setCallback(object : MediaSessionCompat.Callback() {
            override fun onSeekTo(pos: Long) {
                super.onSeekTo(pos)
                val intent = Intent(ACTION_SEEK_TO)
                intent.putExtra(ACTION_SEEK_TO, pos.toInt() + 1000)
                sendBroadcast(intent)
                mediaPlayer.seekTo(pos.toInt())
                setPlaybackState(playbackSpeed)
            }
        })
    }

    private fun setPlaybackState(playbackSpeed: Float) {
        val playBackState = PlaybackStateCompat.Builder().setState(
            PlaybackStateCompat.STATE_PLAYING, mediaPlayer.currentPosition.toLong(), playbackSpeed
        ).setActions(PlaybackStateCompat.ACTION_SEEK_TO).build()
        mediaSession.setPlaybackState(playBackState)
    }
}