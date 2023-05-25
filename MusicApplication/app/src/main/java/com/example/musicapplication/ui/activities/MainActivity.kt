package com.example.musicapplication.ui.activities

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.musicapplication.R
import com.example.musicapplication.databinding.ActivityMainBinding
import com.example.musicapplication.loadImage
import com.example.musicapplication.model.Song
import com.example.musicapplication.services.Mp3Service
import com.example.musicapplication.ui.adapter.ViewPagerAdapter


class MainActivity : BaseActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var viewPagerAdapter: ViewPagerAdapter? = null
    var listPermissions = arrayOf<String>()

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
        val channel = NotificationChannel(
            Mp3Service.CHANNEL_ID, "Playing MP3", NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        listPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        if (ContextCompat.checkSelfPermission(
                this, listPermissions[0]
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, listPermissions, STORAGE_PERMISSION_CODE
            )
        }
        viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewpager.adapter = viewPagerAdapter
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != STORAGE_PERMISSION_CODE || grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, listPermissions[0])) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Yêu cầu quyền đọc TỆP ÂM THANH")
                builder.setMessage("Ứng dụng cần quyền đọc TỆP ÂM THANH để lấy dữ liệu các bài hát đã tải. Vui lòng cấp quyền đọc TỆP ÂM THANH để sử dụng các tính năng này.")
                builder.setPositiveButton("Đồng ý") { _, _ ->
                    ActivityCompat.requestPermissions(
                        this,
                        listPermissions,
                        STORAGE_PERMISSION_CODE
                    )
                }
                builder.setNegativeButton("Hủy", null)
                builder.show()
            } else {
                Toast.makeText(this, "Không có quyền truy cập", Toast.LENGTH_SHORT).show()
            }
        }
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