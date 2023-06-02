package com.example.musicapplication.ui.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.example.musicapplication.collectFlow
import com.example.musicapplication.databinding.FragmentDownloadBinding
import com.example.musicapplication.model.PlaylistType
import com.example.musicapplication.model.Song
import com.example.musicapplication.services.Mp3Service
import com.example.musicapplication.ui.activities.MainActivity
import com.example.musicapplication.ui.activities.PlayActivity
import com.example.musicapplication.ui.adapter.SongOfflineAdapter

class DownloadFragment : BaseFragment() {
    private lateinit var binding: FragmentDownloadBinding
    private val songOfflineAdapter by lazy { SongOfflineAdapter() }
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                binding.tvWarning.visibility = View.GONE
                activity?.applicationContext?.let { dataMp3ViewModel.getOfflineMp3(it) }
            } else {
                Toast.makeText(
                    activity?.applicationContext,
                    "Không có quyền truy cập",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDownloadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
    }

    private fun initViews() {
        binding.rv.adapter = songOfflineAdapter
    }

    override fun onResume() {
        super.onResume()
        checkPermission()
    }

    private fun checkPermission() {
        val readPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        when {
            activity?.applicationContext?.let {
                ContextCompat.checkSelfPermission(it, readPermission)
            } == PackageManager.PERMISSION_GRANTED -> {
                binding.tvWarning.visibility = View.GONE
                activity?.applicationContext?.let { dataMp3ViewModel.getOfflineMp3(it) }
            }

            shouldShowRequestPermissionRationale(readPermission) -> {
                val builder = AlertDialog.Builder(this.requireContext())
                builder.setTitle("Yêu cầu quyền đọc TỆP ÂM THANH")
                builder.setMessage("Ứng dụng cần quyền đọc TỆP ÂM THANH để lấy dữ liệu các bài hát đã tải. CÀI ĐẶT ỨNG DỤNG -> QUYỀN -> NHẠC VÀ ÂM THANH -> CHO PHÉP")
                builder.setPositiveButton("Đồng ý", null)
                builder.show()
            }

            else -> {
                requestPermissionLauncher.launch(readPermission)
            }
        }
    }

    private fun initListeners() {
        collectFlow(dataMp3ViewModel.mp3OfflineList) {
            songOfflineAdapter.setData(it)
        }
        songOfflineAdapter.setOnClickItem {
            playlistType = (activity as? MainActivity)?.mp3Service?.getPlaylistType()
            if (playlistType != PlaylistType.OFFLINE_PLAYLIST) {
                (activity as? MainActivity)?.mp3Service?.apply {
                    setPlaylistType(PlaylistType.OFFLINE_PLAYLIST)
                    setMp3List(dataMp3ViewModel.mp3OfflineList.value)
                }
            }
            val intent = Intent(activity, PlayActivity::class.java)
            val bundle = bundleOf().apply {
                putBoolean(Mp3Service.IS_CURRENT_MP3, false)
                putInt(Mp3Service.MP3_POSITION, it)
            }
            intent.putExtras(bundle)
            activity?.startActivity(intent)
        }
    }

    override fun onPlayNewMp3(song: Song) {
        super.onPlayNewMp3(song)
        song.id?.let { songOfflineAdapter.setMp3IdPlaying(it) }
    }
}