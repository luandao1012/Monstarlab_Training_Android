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
                    bundle?.let {
                        val song = it.getString(Mp3Service.INFO_MP3).toString()
                        val duration = it.getInt(Mp3Service.DURATION_MP3, 0)
                        mp3Position = it.getInt(Mp3Service.MP3_POSITION, 0)
                        onPlayNewMp3(Json.decodeFromString(song), duration)
                    }
                }

                Mp3Service.PLAY_OR_PAUSE -> {
                    val isMp3Playing = intent.extras?.getBoolean(Mp3Service.PLAY_OR_PAUSE)
                    if (isMp3Playing != null) {
                        isPlaying = isMp3Playing
                        onPlayOrPauseMp3()
                    }
                }

                Mp3Service.ACTION_SEEK_TO -> {
                    val mp3CurrentTime = intent.extras?.getInt(Mp3Service.ACTION_SEEK_TO, 0)
                    if (mp3CurrentTime != null) {
                        onChangeTimeMp3(mp3CurrentTime)
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

    override fun onServiceConnected(p0: ComponentName, service: IBinder) {
        val binder = service as? Mp3Service.LocalBinder
        mp3Service = binder?.getService()
        mp3ViewModel.getAllMp3Files(this)
        onCreatedService()
    }

    override fun onServiceDisconnected(p0: ComponentName?) = Unit
    open fun onCreatedService() = Unit
    open fun onPlayNewMp3(song: Song, duration: Int) = Unit
    open fun onPlayOrPauseMp3() = Unit
    open fun onChangeTimeMp3(time: Int) = Unit
}