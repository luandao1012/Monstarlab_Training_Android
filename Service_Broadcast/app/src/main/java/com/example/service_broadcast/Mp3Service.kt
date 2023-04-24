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
    private var mp3Receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                CompanionObject.ACTION_PREV -> prevMp3()
                CompanionObject.ACTION_NEXT -> nextMp3()
                CompanionObject.ACTION_PLAY -> playOrPause()
                CompanionObject.ACTION_COMPLETE -> {
                    when (playMode) {
                        PlayMode.DEFAULT.positionMode -> setDefaultMode()
                        PlayMode.REPEAT_ALL.positionMode -> nextMp3()
                        PlayMode.REPEAT_ONE.positionMode -> playMp3(mp3Position)
                        PlayMode.SHUFFLE.positionMode -> setShuffleMode()
                    }
                }
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
    private val sharedPreferences by lazy {
        getSharedPreferences(
            CompanionObject.SAVE_INFO_MP3,
            Context.MODE_PRIVATE
        )
    }
    private var playMode = 0
    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        val intentFilter = IntentFilter().apply {
            addAction(CompanionObject.ACTION_GET_TIME)
            addAction(CompanionObject.ACTION_PLAY)
            addAction(CompanionObject.ACTION_PREV)
            addAction(CompanionObject.ACTION_NEXT)
            addAction(CompanionObject.ACTION_SEEK_TO)
            addAction(CompanionObject.ACTION_COMPLETE)
        }
        registerReceiver(mp3Receiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferences.edit().apply {
            putInt(CompanionObject.MP3_POSITION, mp3Position)
            putInt(CompanionObject.MP3_CURRENT_TIME, mediaPlayer.currentPosition)
            apply()
        }
        unregisterReceiver(mp3Receiver)
        mediaPlayer.stop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    fun setPlayMode(mode: Int) {
        playMode = mode
    }

    fun getPlayMode() = playMode

    fun setMp3List(list: ArrayList<Song>) {
        mp3Playlist = list
        mp3Playlist.forEachIndexed { index, _ -> positionList.add(index) }
        mp3Position = sharedPreferences.getInt(CompanionObject.MP3_POSITION, -1)
        val time = sharedPreferences.getInt(CompanionObject.MP3_CURRENT_TIME, 0)
        if (mp3Position != -1) {
            playMp3(mp3Position)
            playOrPause()
            setMp3Time(time)
            sendBroadcastToUi(
                CompanionObject.ACTION_SEEK_TO,
                Bundle().apply { putInt(CompanionObject.ACTION_SEEK_TO, time) })
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
            putString(CompanionObject.INFO_MP3, Json.encodeToString(mp3Playlist[position]))
            putInt(CompanionObject.DURATION_MP3, mediaPlayer.duration)
            putInt(CompanionObject.MP3_POSITION, mp3Position)
        }
        sendBroadcastToUi(CompanionObject.INFO_MP3, bundle)
        sendBroadcastToUi(
            CompanionObject.PLAY_OR_PAUSE,
            Bundle().apply { putBoolean(CompanionObject.PLAY_OR_PAUSE, true) })
        sendBroadcastToUi(
            CompanionObject.ACTION_SEEK_TO,
            Bundle().apply { putInt(CompanionObject.ACTION_SEEK_TO, 0) })
        mediaMetadataRetriever.setDataSource(
            applicationContext,
            Uri.parse(mp3Playlist[position].uri)
        )
        mediaPlayer.setOnCompletionListener { sendBroadcast(Intent(CompanionObject.ACTION_COMPLETE)) }
        showNotification(R.drawable.ic_pause)
    }

    private fun sendBroadcastToUi(action: String, bundle: Bundle) {
        val intent = Intent(action)
        intent.putExtras(bundle)
        sendBroadcast(intent)
    }

    fun nextMp3() {
        if (playMode == PlayMode.SHUFFLE.positionMode) {
            setShuffleMode()
        } else {
            val position = if (mp3Position == mp3Playlist.size - 1) 0 else mp3Position + 1
            playMp3(position)
        }
    }

    fun prevMp3() {
        if (playMode == PlayMode.SHUFFLE.positionMode) {
            setShuffleMode()
        } else {
            val position = if (mp3Position == 0) mp3Playlist.size - 1 else mp3Position - 1
            playMp3(position)
        }
    }

    fun setDefaultMode() {
        if (mp3Position == mp3Playlist.size - 1) {
            mediaPlayer.pause()
            mediaPlayer.seekTo(0)
            showNotification(R.drawable.ic_play)
            sendBroadcastToUi(
                CompanionObject.PLAY_OR_PAUSE,
                Bundle().apply { putBoolean(CompanionObject.PLAY_OR_PAUSE, false) })
            sendBroadcastToUi(
                CompanionObject.ACTION_SEEK_TO,
                Bundle().apply { putInt(CompanionObject.ACTION_SEEK_TO, 0) })
        } else {
            nextMp3()
        }
    }

    fun setShuffleMode() {
        if (positionList.size == 1) {
            mp3Playlist.forEachIndexed { index, _ ->
                if (index != mp3Position) positionList.add(index)
            }
        }
        positionList.remove(mp3Position)
        val randomPosition = positionList.random()
        playMp3(randomPosition)
    }

    fun playOrPause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            showNotification(R.drawable.ic_play)
        } else {
            mediaPlayer.start()
            showNotification(R.drawable.ic_pause)
        }
        sendBroadcastToUi(
            CompanionObject.PLAY_OR_PAUSE,
            Bundle().apply { putBoolean(CompanionObject.PLAY_OR_PAUSE, mediaPlayer.isPlaying) })
    }

    fun setMp3Time(time: Int) {
        mediaPlayer.seekTo(time)
        setMediaSession()
    }

    fun getInfoCurrentMp3(): Int {
        val bundle = bundleOf().apply {
            putString(CompanionObject.INFO_MP3, Json.encodeToString(mp3Playlist[mp3Position]))
            putInt(CompanionObject.DURATION_MP3, mediaPlayer.duration)
        }
        sendBroadcastToUi(CompanionObject.INFO_MP3, bundle)
        sendBroadcastToUi(
            CompanionObject.PLAY_OR_PAUSE,
            Bundle().apply { putBoolean(CompanionObject.PLAY_OR_PAUSE, mediaPlayer.isPlaying) })
        return mediaPlayer.currentPosition
    }

    inner class LocalBinder : Binder() {
        fun getService(): Mp3Service = this@Mp3Service
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
            putBoolean(CompanionObject.IS_CURRENT_MP3, true)
            putInt(CompanionObject.MP3_POSITION, mp3Position)
        }
        intent.putExtras(bundle)
        val pendingIntent = PendingIntent.getActivity(
            baseContext, 0, intent, flag
        )
        val playPendingIntent = PendingIntent.getBroadcast(
            baseContext, 0, Intent(CompanionObject.ACTION_PLAY), flag
        )
        val prevPendingIntent = PendingIntent.getBroadcast(
            baseContext, 0, Intent(CompanionObject.ACTION_PREV), flag
        )
        val nextPendingIntent = PendingIntent.getBroadcast(
            baseContext, 0, Intent(CompanionObject.ACTION_NEXT), flag
        )
        val builder =
            NotificationCompat.Builder(baseContext, CompanionObject.CHANNEL_ID)
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
        val playbackSpeed = if (mediaPlayer.isPlaying) 1F else 0F
        mediaSession.setMetadata(
            MediaMetadataCompat.Builder()
                .putLong(MediaMetadata.METADATA_KEY_DURATION, mediaPlayer.duration.toLong()).build()
        )
        setPlaybackState(playbackSpeed)
        mediaSession.setCallback(object : MediaSessionCompat.Callback() {
            override fun onSeekTo(pos: Long) {
                super.onSeekTo(pos)
                sendBroadcastToUi(
                    CompanionObject.ACTION_SEEK_TO,
                    Bundle().apply { putInt(CompanionObject.ACTION_SEEK_TO, pos.toInt() + 1000) })
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

    enum class PlayMode(val positionMode: Int) {
        DEFAULT(0), REPEAT_ALL(1), REPEAT_ONE(2), SHUFFLE(3)
    }
}