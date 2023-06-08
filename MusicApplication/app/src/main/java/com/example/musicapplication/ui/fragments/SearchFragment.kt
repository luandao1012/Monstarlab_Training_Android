package com.example.musicapplication.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.example.musicapplication.collectFlow
import com.example.musicapplication.databinding.FragmentSearchBinding
import com.example.musicapplication.isConnectInternet
import com.example.musicapplication.model.PlaylistType
import com.example.musicapplication.model.Song
import com.example.musicapplication.network.ApiBuilder
import com.example.musicapplication.services.Mp3Service
import com.example.musicapplication.ui.activities.MainActivity
import com.example.musicapplication.ui.activities.PlayActivity
import com.example.musicapplication.ui.adapter.ItemSearchAdapter
import com.example.musicapplication.ui.viewmodel.SearchViewModel

class SearchFragment : BaseFragment() {
    private lateinit var binding: FragmentSearchBinding
    private var itemSearchAdapter: ItemSearchAdapter? = null
    private val searchViewModel by viewModels<SearchViewModel>()
    private var song: Song? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
    }

    private fun initViews() {
        itemSearchAdapter = ItemSearchAdapter()
        binding.rv.adapter = itemSearchAdapter
    }

    private fun initListeners() {
        itemSearchAdapter?.setItemOnClick {
            if (isConnectInternet()) {
                song = Song(
                    id = it.id,
                    name = it.name,
                    singer = it.single,
                    duration = it.duration,
                    image = ApiBuilder.IMAGE_URL + it.image
                )
                playlistType = (activity as? MainActivity)?.mp3Service?.getPlaylistType()
                if (playlistType != PlaylistType.RECOMMEND_PLAYLIST) {
                    (activity as? MainActivity)?.mp3Service?.apply {
                        setPlaylistType(PlaylistType.RECOMMEND_PLAYLIST)
                    }
                }
                searchViewModel.getMp3Recommend(it.id)
                val intent = Intent(activity, PlayActivity::class.java)
                val bundle = bundleOf().apply {
                    putBoolean(Mp3Service.IS_CURRENT_MP3, false)
                    putInt(Mp3Service.MP3_POSITION, 0)
                }
                intent.putExtras(bundle)
                activity?.startActivity(intent)
            } else {
                Toast.makeText(this.requireContext(), "Không có kết nối Internet", Toast.LENGTH_SHORT).show()
            }
        }
        collectFlow(searchViewModel.mp3RecommendList) {
            if (it.isNotEmpty()) {
                song?.let { song -> it.add(0, song) }
                (activity as? MainActivity)?.mp3Service?.setMp3List(it)
            }
        }
        collectFlow(searchViewModel.mp3SearchList) {
            if (it.isNotEmpty()) {
                itemSearchAdapter?.setData(it)
            }
        }
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun afterTextChanged(key: Editable?) {
                search(key.toString())
            }
        })
    }

    private fun search(key: String) {
        if (isConnectInternet()) {
            binding.tvNoInternet.visibility = View.GONE
            searchViewModel.search(key)
        } else {
            binding.tvNoInternet.visibility = View.VISIBLE
        }
    }
}