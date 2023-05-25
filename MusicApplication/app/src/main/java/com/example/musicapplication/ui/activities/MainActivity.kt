package com.example.musicapplication.ui.activities

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.musicapplication.R
import com.example.musicapplication.ui.adapter.ViewPagerAdapter
import com.example.musicapplication.databinding.ActivityMainBinding
import com.example.musicapplication.loadImage
import com.example.musicapplication.model.PlaylistType
import com.example.musicapplication.model.Song
import com.example.musicapplication.services.Mp3Service
import com.example.musicapplication.services.Mp3Service.Companion.ACTION_SEEK_TO
import com.example.musicapplication.services.Mp3Service.Companion.INFO_MP3
import com.example.musicapplication.services.Mp3Service.Companion.MP3_CURRENT_TIME
import com.google.gson.Gson

class MainActivity : BaseActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var viewPagerAdapter: ViewPagerAdapter? = null

    companion object {
        private const val STORAGE_PERMISSION_CODE = 222
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViews()
        initListeners()
    }

    private fun initViews() {
        val channel =
            NotificationChannel(
                Mp3Service.CHANNEL_ID,
                "Playing MP3",
                NotificationManager.IMPORTANCE_LOW
            )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                STORAGE_PERMISSION_CODE
            )
        }
        viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewpager.adapter = viewPagerAdapter
    }

    private fun initListeners() {
        binding.ivNext.setOnClickListener { mp3Service?.setNextMp3() }
        binding.ivPre.setOnClickListener { mp3Service?.setPrevMp3() }
        binding.ivPlay.setOnClickListener { mp3Service?.setPlayPauseMp3() }
        binding.layoutPlayMp3Main.setOnClickListener {
            val intent = Intent(this, PlayActivity::class.java)
            val bundle = bundleOf().apply {
                putBoolean(Mp3Service.IS_CURRENT_MP3, true)
                putInt(Mp3Service.MP3_POSITION, mp3Position)
            }
            intent.putExtras(bundle)
            startActivity(intent)
        }
        binding.viewpager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> binding.bottomNav.selectedItemId = R.id.home
                    1 -> binding.bottomNav.selectedItemId = R.id.search
                    2 -> binding.bottomNav.selectedItemId = R.id.favourite
                    3 -> binding.bottomNav.selectedItemId = R.id.download
                }
            }
        })
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> binding.viewpager.currentItem = 0
                R.id.search -> binding.viewpager.currentItem = 1
                R.id.favourite -> binding.viewpager.currentItem = 2
                R.id.download -> binding.viewpager.currentItem = 3
            }
            true
        }
    }

    override fun onPlayOrPauseMp3() {
        super.onPlayOrPauseMp3()
        binding.layoutPlayMp3Main.visibility = View.VISIBLE
        if (isPlaying) {
            binding.ivPlay.setImageResource(R.drawable.ic_pause)
        } else {
            binding.ivPlay.setImageResource(R.drawable.ic_play)
        }
    }

    override fun onPlayNewMp3(song: Song) {
        super.onPlayNewMp3(song)
        binding.tvName.text = song.name
        binding.tvSingle.text = song.singer
        binding.ivItemMp3.loadImage(song.image)
    }
}