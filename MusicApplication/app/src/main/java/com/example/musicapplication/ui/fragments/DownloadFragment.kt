package com.example.musicapplication.ui.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.example.musicapplication.R
import com.example.musicapplication.databinding.FragmentDownloadBinding
import com.example.musicapplication.model.PlaylistType
import com.example.musicapplication.model.Song
import com.example.musicapplication.services.Mp3Service
import com.example.musicapplication.ui.activities.MainActivity
import com.example.musicapplication.ui.activities.PlayActivity
import com.example.musicapplication.ui.adapter.SongOfflineAdapter
import com.example.musicapplication.ui.viewmodel.Mp3ViewModel

class DownloadFragment : Fragment() {
    private lateinit var binding: FragmentDownloadBinding
    private var playlistType: PlaylistType? = null
    private val songOfflineAdapter by lazy { SongOfflineAdapter() }
    private val mp3ViewModel: Mp3ViewModel by activityViewModels()
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
        activity?.applicationContext?.let { mp3ViewModel.getOfflineMp3(it) }
    }

    private fun initListeners() {
        mp3ViewModel.mp3OfflineList.observe(this.viewLifecycleOwner) {
            songOfflineAdapter.setData(it)
        }
        songOfflineAdapter.setOnClickItem {
            playlistType = (activity as? MainActivity)?.mp3Service?.getPlaylistType()
            if (playlistType != PlaylistType.OFFLINE_PLAYLIST) {
                (activity as? MainActivity)?.mp3Service?.apply {
                    setPlaylistType(PlaylistType.OFFLINE_PLAYLIST)
                    setMp3List(mp3ViewModel.mp3OfflineList.value as ArrayList<Song>)
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
}