package com.example.service_broadcast

import android.content.*
import android.os.Bundle
import android.os.IBinder
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

abstract class BaseActivity : AppCompatActivity(), ServiceConnection {

    var mp3Service: Mp3Service? = null
    val mp3ViewModel: Mp3ViewModel by viewModels()
    var isPlaying: Boolean = false
    var mp3Position = -1
    private var mp3Receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Mp3Service.INFO_MP3 -> {
                    val bundle = intent.extras
                    val song = bundle?.getString(Mp3Service.INFO_MP3)
                    val duration = bundle?.getInt(Mp3Service.DURATION_MP3, 0)
                    mp3Position = bundle?.getInt(Mp3Service.MP3_POSITION, 0)!!
                    if (song != null && duration != null) {
                        getInfoSong(Json.decodeFromString(song), duration)
                    }
                }
                Mp3Service.PLAY_OR_PAUSE -> {
                    val isMp3Playing = intent.extras?.getBoolean(Mp3Service.PLAY_OR_PAUSE)
                    if (isMp3Playing != null) {
                        isPlaying = isMp3Playing
                        setPlayOrPause()
                    }
                }
                Mp3Service.ACTION_SEEK_TO -> {
                    val mp3CurrentTime = intent.extras?.getInt(Mp3Service.ACTION_SEEK_TO, 0)
                    if (mp3CurrentTime != null) {
                        setTimeSeekbar(mp3CurrentTime)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindService(Intent(this, Mp3Service::class.java), this, Context.BIND_AUTO_CREATE)
        val intent = IntentFilter().apply {
            addAction(Mp3Service.INFO_MP3)
            addAction(Mp3Service.PLAY_OR_PAUSE)
            addAction(Mp3Service.ACTION_SEEK_TO)
        }
        registerReceiver(mp3Receiver, intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mp3Receiver)
    }

    override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
        val binder = service as Mp3Service.LocalBinder
        mp3Service = binder.getService()
        mp3ViewModel.getAllMp3Files(this)
        createdService()
    }

    override fun onServiceDisconnected(p0: ComponentName?) = Unit
    open fun createdService() = Unit
    open fun getInfoSong(song: Song, duration: Int) = Unit
    open fun setPlayOrPause() = Unit
    open fun setTimeSeekbar(time: Int) = Unit
}