package com.example.musicapplication.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.musicapplication.collectFlow
import com.example.musicapplication.databinding.FragmentFavouriteBinding
import com.example.musicapplication.model.PlaylistType
import com.example.musicapplication.model.Song
import com.example.musicapplication.services.Mp3Service
import com.example.musicapplication.ui.activities.MainActivity
import com.example.musicapplication.ui.activities.PlayActivity
import com.example.musicapplication.ui.adapter.SongFavouriteAdapter
import com.example.musicapplication.ui.viewmodel.FavouriteViewModel
import com.example.musicapplication.ui.viewmodel.HomeViewModel
import com.example.musicapplication.ui.viewmodel.PlayViewModel

class FavouriteFragment : BaseFragment() {
    private lateinit var binding: FragmentFavouriteBinding
    private val playViewModel by viewModels<PlayViewModel>()
    private val favouriteViewModel by viewModels<FavouriteViewModel>()
    private val homeViewModel by activityViewModels<HomeViewModel>()
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

    override fun onResume() {
        super.onResume()
        favouriteViewModel.getAllMp3Favourite()
    }

    private fun initListeners() {
        collectFlow(favouriteViewModel.mp3FavouriteList) {
            binding.progressBar.visibility = View.GONE
            songFavouriteAdapter.setData(it)
        }
        songFavouriteAdapter.setFavourite {
            playViewModel.changeFavourite(it, it.isFavourite)
            favouriteViewModel.getAllMp3Favourite()
            it.id?.let { id -> homeViewModel.removeFavourite(id) }
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
                    favouriteViewModel.mp3FavouriteList.value.let { list -> setMp3List(list) }
                }
            }
        }
    }

    private fun initViews() {
        binding.rv.adapter = songFavouriteAdapter
    }

    override fun onPlayNewMp3(song: Song) {
        super.onPlayNewMp3(song)
        song.id?.let { songFavouriteAdapter.setMp3IdPlaying(it) }
    }
}