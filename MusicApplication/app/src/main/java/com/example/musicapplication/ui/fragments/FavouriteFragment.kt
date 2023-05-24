package com.example.musicapplication.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.example.musicapplication.R
import com.example.musicapplication.databinding.FragmentFavouriteBinding
import com.example.musicapplication.model.PlaylistType
import com.example.musicapplication.model.Song
import com.example.musicapplication.services.Mp3Service
import com.example.musicapplication.ui.activities.MainActivity
import com.example.musicapplication.ui.activities.PlayActivity
import com.example.musicapplication.ui.adapter.SongFavouriteAdapter
import com.example.musicapplication.ui.viewmodel.Mp3ViewModel

class FavouriteFragment : Fragment() {
    private lateinit var binding: FragmentFavouriteBinding
    private val mp3ViewModel: Mp3ViewModel by activityViewModels()
    private var playlistType: PlaylistType? = null
    private val songFavouriteAdapter by lazy { SongFavouriteAdapter() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
    }

    override fun onStart() {
        super.onStart()
        mp3ViewModel.getAllMp3Favourite()
    }

    private fun initListeners() {
        mp3ViewModel.mp3FavouriteList.observe(this.viewLifecycleOwner) {
            binding.progressBar.visibility = View.GONE
            if (it != null) {
                songFavouriteAdapter.setData(it)
            }
        }
        songFavouriteAdapter.setOnClickItem {
            val intent = Intent(activity, PlayActivity::class.java)
            val bundle = bundleOf().apply {
                putBoolean(Mp3Service.IS_CURRENT_MP3, false)
                putInt(Mp3Service.MP3_POSITION, it)
            }
            intent.putExtras(bundle)
            activity?.startActivity(intent)
            playlistType = (activity as? MainActivity)?.mp3Service?.getPlaylistType()
            if (playlistType != PlaylistType.FAVOURITE_PLAYLIST) {
                (activity as? MainActivity)?.mp3Service?.apply {
                    setPlaylistType(PlaylistType.FAVOURITE_PLAYLIST)
                    setMp3List(mp3ViewModel.mp3FavouriteList.value as ArrayList<Song>)
                }
            }
        }
    }

    private fun initViews() {
        binding.rv.adapter = songFavouriteAdapter
    }
}