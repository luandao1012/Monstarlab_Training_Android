package com.example.service_broadcast

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaMetadata
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class Mp3Service : Service() {
    companion object {
        const val CHANNEL_ID = "CHANNEL_ID"
        const val SAVE_INFO_MP3 = "SAVE_INFO_MP3"
        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_PREV = "ACTION_PREV"
        const val ACTION_NEXT = "ACTION_NEXT"
        const val ACTION_SEEK_TO = "ACTION_SEEK_TO"
        const val INFO_MP3 = "INFO_MP3"
        const val DURATION_MP3 = "DURATION_MP3"
        const val PLAY_OR_PAUSE = "PLAY_OR_PAUSE"
        const val MP3_POSITION = "MP3_POSITION"
        const val MP3_CURRENT_TIME = "MP3_CURRENT_TIME"
        const val IS_CURRENT_MP3 = "IS_CURRENT_MP3"
    }

    private var mp3Receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_PREV -> setPrevMp3()
                ACTION_NEXT -> setNextMp3()
                ACTION_PLAY -> setPlayPauseMp3()
            }
        }
    }

    private val mediaPlayer by lazy { MediaPlayer() }
    private val mediaMetadataRetriever by lazy { MediaMetadataRetriever() }
    private val binder = LocalBinder()
    private val mediaSession by lazy { MediaSessionCompat(baseContext, "tag") }
    private var mp3Playlist = arrayListOf<Song>()
    private var mp3Position = -1
    private var positionList = arrayListOf<Int>()
    private var playbackSpeed = 0f
    private var playMode: PlayMode = PlayMode.DEFAULT

    private val sharedPreferences by lazy {
        getSharedPreferences(SAVE_INFO_MP3, Context.MODE_PRIVATE)
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        val intentFilter = IntentFilter().apply {
            addAction(ACTION_PLAY)
            addAction(ACTION_PREV)
            addAction(ACTION_NEXT)
        }
        registerReceiver(mp3Receiver, intentFilter)
        mediaPlayer.setOnCompletionListener {
            if (playMode == PlayMode.DEFAULT && mp3Position == mp3Playlist.size - 1) {
                mediaPlayer.pause()
                mediaPlayer.seekTo(0)
                showNotification(R.drawable.ic_play)
                sendBroadcastToUi(
                    PLAY_OR_PAUSE,
                    Bundle().apply { putBoolean(PLAY_OR_PAUSE, false) })
                sendBroadcastToUi(ACTION_SEEK_TO, Bundle().apply { putInt(ACTION_SEEK_TO, 0) })
            } else {
                playMp3(getMp3PositionContinue(ActionPlay.ACTION_COMPLETE))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferences.edit().apply {
            putInt(MP3_POSITION, mp3Position)
            putInt(MP3_CURRENT_TIME, mediaPlayer.currentPosition)
            apply()
        }
        unregisterReceiver(mp3Receiver)
        mediaPlayer.stop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    fun setPlayMode(mode: PlayMode) {
        playMode = mode
    }

    fun getPlayMode() = playMode

    fun setMp3List(list: ArrayList<Song>) {
        mp3Playlist = list
        mp3Playlist.forEachIndexed { index, _ -> positionList.add(index) }
        mp3Position = sharedPreferences.getInt(MP3_POSITION, -1)
        val time = sharedPreferences.getInt(MP3_CURRENT_TIME, 0)
        if (mp3Position != -1) {
            playMp3(mp3Position)
            setPlayPauseMp3()
            setMp3Time(time)
            sendBroadcastToUi(ACTION_SEEK_TO, Bundle().apply { putInt(ACTION_SEEK_TO, time) })
        }
    }

    fun playMp3(position: Int) {
        mediaPlayer.apply {
            reset()
            setDataSource(applicationContext, Uri.parse(mp3Playlist[position].uri))
            prepare()
            start()
        }
        mp3Position = position
        val bundle = bundleOf().apply {
            putString(INFO_MP3, Json.encodeToString(mp3Playlist[position]))
            putInt(DURATION_MP3, mediaPlayer.duration)
            putInt(MP3_POSITION, mp3Position)
        }
        sendBroadcastToUi(INFO_MP3, bundle)
        sendBroadcastToUi(PLAY_OR_PAUSE, Bundle().apply { putBoolean(PLAY_OR_PAUSE, true) })
        sendBroadcastToUi(ACTION_SEEK_TO, Bundle().apply { putInt(ACTION_SEEK_TO, 0) })
        mediaMetadataRetriever.setDataSource(
            applicationContext,
            Uri.parse(mp3Playlist[position].uri)
        )
        showNotification(R.drawable.ic_pause)
    }

    private fun sendBroadcastToUi(action: String, bundle: Bundle) {
        val intent = Intent(action)
        intent.putExtras(bundle)
        sendBroadcast(intent)
    }

    private fun getMp3PositionContinue(action: ActionPlay): Int {
        return when {
            playMode == PlayMode.SHUFFLE -> getShufflePosition()
            action == ActionPlay.ACTION_NEXT -> if (mp3Position == mp3Playlist.size - 1) 0 else mp3Position + 1
            action == ActionPlay.ACTION_PREV -> if (mp3Position == 0) mp3Playlist.size - 1 else mp3Position - 1
            else -> {
                if (playMode == PlayMode.REPEAT_ONE) mp3Position
                else {
                    if (mp3Position == mp3Playlist.size - 1) 0 else mp3Position + 1
                }
            }
        }
    }

    private fun getShufflePosition(): Int {
        if (positionList.size == 1) {
            mp3Playlist.forEachIndexed { index, _ ->
                if (index != mp3Position) positionList.add(index)
            }
        }
        positionList.remove(mp3Position)
        return positionList.random()
    }

    fun setPlayPauseMp3() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            showNotification(R.drawable.ic_play)
        } else {
            mediaPlayer.start()
            showNotification(R.drawable.ic_pause)
        }
        sendBroadcastToUi(
            PLAY_OR_PAUSE,
            Bundle().apply { putBoolean(PLAY_OR_PAUSE, mediaPlayer.isPlaying) }
        )
    }

    fun setNextMp3() {
        playMp3(getMp3PositionContinue(ActionPlay.ACTION_NEXT))
    }

    fun setPrevMp3() {
        playMp3(getMp3PositionContinue(ActionPlay.ACTION_PREV))
    }

    fun setMp3Time(time: Int) {
        mediaPlayer.seekTo(time)
        setPlaybackState(playbackSpeed)
    }

    fun getInfoCurrentMp3(callback: ((song: Song, duration: Int, currentTime: Int, isPlaying: Boolean) -> Unit)) {
        callback.invoke(
            mp3Playlist[mp3Position],
            mediaPlayer.duration,
            mediaPlayer.currentPosition,
            mediaPlayer.isPlaying
        )
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun showNotification(imagePlay: Int) {
        setMediaSession()
        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
            .setMediaSession(mediaSession.sessionToken)
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val intent = Intent(this, PlayMp3Activity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val bundle = bundleOf().apply {
            putBoolean(IS_CURRENT_MP3, true)
            putInt(MP3_POSITION, mp3Position)
        }
        intent.putExtras(bundle)
        val pendingIntent = PendingIntent.getActivity(
            baseContext, 0, intent, flag
        )
        val playPendingIntent = PendingIntent.getBroadcast(
            baseContext, 0, Intent(ACTION_PLAY), flag
        )
        val prevPendingIntent = PendingIntent.getBroadcast(
            baseContext, 0, Intent(ACTION_PREV), flag
        )
        val nextPendingIntent = PendingIntent.getBroadcast(
            baseContext, 0, Intent(ACTION_NEXT), flag
        )
        val builder =
            NotificationCompat.Builder(baseContext, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.cd)
                .setContentTitle(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE))
                .setContentText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST))
                .setStyle(mediaStyle)
                .addAction(R.drawable.ic_previous, "Previous", prevPendingIntent)
                .addAction(imagePlay, "Pause", playPendingIntent)
                .addAction(R.drawable.ic_next, "Next", nextPendingIntent).build()
        startForeground(1, builder)
    }

    private fun setMediaSession() {
        playbackSpeed = if (mediaPlayer.isPlaying) 1F else 0F
        mediaSession.setMetadata(
            MediaMetadataCompat.Builder()
                .putLong(MediaMetadata.METADATA_KEY_DURATION, mediaPlayer.duration.toLong())
                .build()
        )
        setPlaybackState(playbackSpeed)
        mediaSession.setCallback(object : MediaSessionCompat.Callback() {
            override fun onSeekTo(pos: Long) {
                super.onSeekTo(pos)
                sendBroadcastToUi(
                    ACTION_SEEK_TO,
                    Bundle().apply { putInt(ACTION_SEEK_TO, pos.toInt() + 1000) }
                )
                mediaPlayer.seekTo(pos.toInt())
                setPlaybackState(playbackSpeed)
            }
        })
    }

    private fun setPlaybackState(playbackSpeed: Float) {
        val playBackState = PlaybackStateCompat.Builder().setState(
            PlaybackStateCompat.STATE_PLAYING,
            mediaPlayer.currentPosition.toLong(),
            playbackSpeed
        ).setActions(PlaybackStateCompat.ACTION_SEEK_TO).build()
        mediaSession.setPlaybackState(playBackState)
    }

    inner class LocalBinder : Binder() {
        fun getService(): Mp3Service = this@Mp3Service
    }

    enum class PlayMode {
        DEFAULT, REPEAT_ALL, REPEAT_ONE, SHUFFLE;

        fun nextPlayMode(): PlayMode {
            return values()[(ordinal + 1) % (values().size)]
        }
    }

    enum class ActionPlay {
        ACTION_NEXT, ACTION_PREV, ACTION_COMPLETE
    }
}