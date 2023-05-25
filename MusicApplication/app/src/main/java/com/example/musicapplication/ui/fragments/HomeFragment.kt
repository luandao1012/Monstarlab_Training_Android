package com.example.musicapplication.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.example.musicapplication.ui.viewmodel.Mp3ViewModel
import com.example.musicapplication.ui.adapter.SongAdapter
import com.example.musicapplication.databinding.FragmentHomeBinding
import com.example.musicapplication.model.PlaylistType
import com.example.musicapplication.model.Song
import com.example.musicapplication.services.Mp3Service
import com.example.musicapplication.ui.activities.MainActivity
import com.example.musicapplication.ui.activities.PlayActivity

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val mp3ViewModel: Mp3ViewModel by activityViewModels()
    private var songAdapter: SongAdapter? = null
    private var playlistType: PlaylistType? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
        songAdapter = SongAdapter()
        binding.rv.adapter = songAdapter
    }

    private fun initListeners() {
        songAdapter?.setOnClickItem {
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
                    setMp3List(mp3ViewModel.mp3ChartsList.value as ArrayList<Song>)
                }
            }
        }
        songAdapter?.setDownload { song ->
            activity?.applicationContext?.let { context ->
                Toast.makeText(context, "Bắt đầu tải xuống", Toast.LENGTH_SHORT).show()
                song.id?.let { id ->
                    mp3ViewModel.downloadMp3(
                        context,
                        id, "${song.singer} - ${song.name}.mp3"
                    )
                }
            }
        }
        songAdapter?.setFavourite {
            it.id?.let { id ->
                it.code?.let { code ->
                    mp3ViewModel.addFavourite(
                        id,
                        code, it.isFavourite
                    )
                }
            }
            mp3ViewModel.getAllMp3Favourite()
        }
        mp3ViewModel.mp3ChartsList.observe(this.viewLifecycleOwner) { listSong ->
            binding.progressBar.visibility = View.GONE
            if (listSong != null) {
                songAdapter?.setData(listSong)
            }
        }
    }
}