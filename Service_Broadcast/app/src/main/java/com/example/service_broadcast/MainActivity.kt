package com.example.service_broadcast

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.service_broadcast.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object {
        private const val READ_STORAGE_PERMISSION_CODE = 111
    }

    private val mp3ViewModel: Mp3ViewModel by viewModels()
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
            binding.tv.visibility = if (it.isNotEmpty()) View.GONE else View.VISIBLE
            songAdapter?.setData(it)
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
}