package com.example.service_broadcast

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.example.service_broadcast.databinding.ActivityMainBinding

class MainActivity : BaseActivity(), OnClickListener {
    companion object {
        private const val READ_STORAGE_PERMISSION_CODE = 111
    }

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var songAdapter: SongAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViews()
        initListeners()
    }


    private fun initViews() {
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
        binding.tvName.isSelected = true
        binding.ivPlay.setOnClickListener(this)
        binding.ivNext.setOnClickListener(this)
        binding.ivPre.setOnClickListener(this)
        binding.layoutPlayMp3Main.setOnClickListener(this)
        mp3ViewModel.allMp3.observe(this) {
            mp3Service?.setMp3List(it)
            binding.tv.visibility = if (it.isNotEmpty()) View.GONE else View.VISIBLE
            songAdapter?.setData(it)
        }
        songAdapter?.setOnClickItem {
            val intent = Intent(this, PlayMp3Activity::class.java)
            val bundle = bundleOf().apply {
                putBoolean(Mp3Service.IS_CURRENT_MP3, false)
                putInt(Mp3Service.MP3_POSITION, it)
            }
            intent.putExtras(bundle)
            startActivity(intent)
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


    override fun onClick(view: View?) {
        when (view) {
            binding.ivPlay -> mp3Service?.setPlayPauseMp3()
            binding.ivNext -> mp3Service?.setNextMp3(true)
            binding.ivPre -> mp3Service?.setNextMp3(false)
            binding.layoutPlayMp3Main -> {
                val intent = Intent(this, PlayMp3Activity::class.java)
                val bundle = bundleOf().apply {
                    putBoolean(Mp3Service.IS_CURRENT_MP3, true)
                    putInt(Mp3Service.MP3_POSITION, mp3Position)
                }
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
    }

    override fun getInfoSong(song: Song, duration: Int, position: Int) {
        super.getInfoSong(song, duration, position)
        mp3Position = position
        songAdapter?.setMp3Position(mp3Position)
        binding.tvName.text = song.name
        binding.layoutPlayMp3Main.visibility = View.VISIBLE
    }

    override fun setPlayOrPause(isPlaying: Boolean) {
        super.setPlayOrPause(isPlaying)
        if (isPlaying) {
            binding.ivPlay.setImageResource(R.drawable.ic_pause)
        } else {
            binding.ivPlay.setImageResource(R.drawable.ic_play)
        }
    }
}