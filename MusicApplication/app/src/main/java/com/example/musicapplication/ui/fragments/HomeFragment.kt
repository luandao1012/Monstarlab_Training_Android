package com.example.musicapplication.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.musicapplication.collectFlow
import com.example.musicapplication.databinding.FragmentHomeBinding
import com.example.musicapplication.isConnectInternet
import com.example.musicapplication.model.PlaylistType
import com.example.musicapplication.model.Song
import com.example.musicapplication.services.Mp3Service
import com.example.musicapplication.ui.activities.MainActivity
import com.example.musicapplication.ui.activities.PlayActivity
import com.example.musicapplication.ui.adapter.SongAdapter
import com.example.musicapplication.ui.viewmodel.HomeViewModel
import com.example.musicapplication.ui.viewmodel.PlayViewModel

class HomeFragment : BaseFragment() {
    private lateinit var binding: FragmentHomeBinding
    private val songAdapter by lazy { SongAdapter() }
    private val playViewModel by viewModels<PlayViewModel>()
    private val homeViewModel by activityViewModels<HomeViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
    }

    private fun initViews() {
        binding.rv.adapter = songAdapter
        collectFlow(homeViewModel.mp3ChartsList) { listSong ->
            if (listSong.isNotEmpty()) {
                binding.progressBar.visibility = View.GONE
                songAdapter.setData(listSong)
            }
        }
    }

    private fun initListeners() {
        songAdapter.setOnClickItem {
            if (this.requireContext().isConnectInternet()) {
                val intent = Intent(activity, PlayActivity::class.java)
                val bundle = bundleOf().apply {
                    putBoolean(Mp3Service.IS_CURRENT_MP3, false)
                    putInt(Mp3Service.MP3_POSITION, it)
                }
                intent.putExtras(bundle)
                activity?.startActivity(intent)

                playlistType = (activity as? MainActivity)?.mp3Service?.getPlaylistType()
                if (playlistType != PlaylistType.TOP100_PLAYLIST) {
                    (activity as? MainActivity)?.mp3Service?.apply {
                        setPlaylistType(PlaylistType.TOP100_PLAYLIST)
                        setMp3List(homeViewModel.mp3ChartsList.value)
                    }
                }
            } else {
                Toast.makeText(this.requireContext(), "Không có kết nối Internet", Toast.LENGTH_SHORT).show()
            }
        }
        songAdapter.setFavourite {
            if (this.requireContext().isConnectInternet()) {
                playViewModel.changeFavourite(it, it.isFavourite)
            } else {
                Toast.makeText(this.requireContext(), "Không có kết nối Internet", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onFavouriteMp3(position: Int) {
        super.onFavouriteMp3(position)
        songAdapter.notifyItemChanged(position)
    }

    override fun onPlayNewMp3(song: Song) {
        super.onPlayNewMp3(song)
        song.id?.let { songAdapter.setMp3IdPlaying(it) }
    }
}