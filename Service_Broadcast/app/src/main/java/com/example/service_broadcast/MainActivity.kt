package com.example.service_broadcast

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.service_broadcast.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), ServiceConnection {
    companion object {
        private const val READ_STORAGE_PERMISSION_CODE = 111
        const val MP3_POSITION = "mp3 position"
        var positionMode = 0
    }

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mp3ViewModel: Mp3ViewModel by viewModels()
    private var songAdapter: SongAdapter? = null
    private var mp3Receiver: BroadcastReceiver? = null
    private var mp3Position = -1
    private var mp3Service: Mp3Service? = null
    private var isPlaying = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViews()
        initListeners()
    }


    private fun initViews() {
        bindService(Intent(this, Mp3Service::class.java), this, Context.BIND_AUTO_CREATE)
        songAdapter = SongAdapter()
        binding.rv.adapter = songAdapter
        val channel =
            NotificationChannel(
                Mp3Service.CHANNEL_ID,
                "Playing MP3",
                NotificationManager.IMPORTANCE_LOW
            )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED
        ) {
            mp3ViewModel.getAllMp3Files(this)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_STORAGE_PERMISSION_CODE
            )
        }
    }

    private fun initListeners() {
        mp3ViewModel.allMp3.observe(this) {
            mp3Service?.setMp3List(it)
            binding.tv.visibility = if (it.isNotEmpty()) View.GONE else View.VISIBLE
            songAdapter?.setData(it)
        }
        songAdapter?.setOnClickCallback {
            val intent = Intent(this, PlayMp3Activity::class.java)
            mp3Position = it
            intent.putExtra(MP3_POSITION, it)
            startActivity(intent)
        }
        mp3Receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Mp3Service.ACTION_PREV -> mp3Service?.prevMp3(true)
                    Mp3Service.ACTION_NEXT -> mp3Service?.nextMp3(true)
                    Mp3Service.ACTION_PLAY -> setPlayPauseMp3()
                    Mp3Service.ACTION_COMPLETE -> setPlayMode()
                }
            }
        }
    }

    private fun setPlayPauseMp3() {
        if (isPlaying) {
            isPlaying = false
            mp3Service?.pauseOrReplay(true)
            mp3Service?.showNotification(R.drawable.ic_play)
        } else {
            isPlaying = true
            mp3Service?.pauseOrReplay(false)
            mp3Service?.showNotification(R.drawable.ic_pause)
        }
    }

    private fun setPlayMode() {
        when (positionMode) {
            PlayMp3Activity.MODE_DEFAULT -> {
                if (mp3Service?.setDefaultMode() == true) {
                    isPlaying = true
                    setPlayPauseMp3()
                } else {
                    mp3Service?.nextMp3(true)
                }
            }
            PlayMp3Activity.MODE_REPEAT_ALL -> mp3Service?.nextMp3(true)
            PlayMp3Activity.MODE_REPEAT_ONE -> mp3Service?.setRepeatOneMode { _, _ -> }
            PlayMp3Activity.MODE_SHUFFLE -> mp3Service?.setShuffleMode(true)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mp3ViewModel.getAllMp3Files(this)
        } else {
            Toast.makeText(this, "Không có quyền truy cập", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
        val binder = service as Mp3Service.LocalBinder
        mp3Service = binder.getService()
        mp3ViewModel.getAllMp3Files(this)
    }

    override fun onServiceDisconnected(p0: ComponentName?) = Unit

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter()
        intentFilter.apply {
            addAction(Mp3Service.ACTION_PLAY)
            addAction(Mp3Service.ACTION_PREV)
            addAction(Mp3Service.ACTION_NEXT)
            addAction(Mp3Service.ACTION_COMPLETE)
        }
        registerReceiver(mp3Receiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mp3Receiver)
    }
}