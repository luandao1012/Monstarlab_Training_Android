package com.example.musicapplication.ui.activities

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.example.musicapplication.R
import com.example.musicapplication.databinding.ActivityPlayBinding
import com.example.musicapplication.formatSongTime
import com.example.musicapplication.loadImage
import com.example.musicapplication.model.PlaylistType
import com.example.musicapplication.model.Song
import com.example.musicapplication.network.ApiBuilder
import com.example.musicapplication.services.Mp3Service
import com.example.musicapplication.ui.adapter.SongAdapter
import com.example.musicapplication.ui.adapter.SongOfflineAdapter

class PlayActivity : BaseActivity() {
    private val binding by lazy { ActivityPlayBinding.inflate(layoutInflater) }
    private var countDownTimer: CountDownTimer? = null
    private var isCurrentMp3 = false
    private var mp3CurrentTime = 0
    private val songAdapter by lazy { SongOfflineAdapter() }
    private var mp3Playlist = arrayListOf<Song>()
    private var playlistType: PlaylistType? = null
    private var currentSong: Song? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViews()
        initListeners()
    }

    private fun initViews() {
        val bundle = intent.extras
        bundle?.let {
            mp3Position = it.getInt(Mp3Service.MP3_POSITION, -1)
            isCurrentMp3 = it.getBoolean(Mp3Service.IS_CURRENT_MP3)
        }
        mp3ViewModel.mp3Genres.observe(this) { listGenres ->
            var genre = ""
            listGenres?.forEach {
                genre += "${it.name}, "
            }
            binding.tvGenres.text = genre.substring(0, genre.length - 2)
        }
        binding.rvPlaylist.adapter = songAdapter
    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.ivPlay.setOnClickListener { mp3Service?.setPlayPauseMp3() }
        binding.ivNext.setOnClickListener { mp3Service?.setNextMp3() }
        binding.ivPre.setOnClickListener { mp3Service?.setPrevMp3() }
        songAdapter.setOnClickItem {
            mp3Service?.playMp3(it)
        }
        binding.ivFavourite.setOnClickListener {
            currentSong?.let {
                currentSong?.isFavourite = !currentSong?.isFavourite!!
                currentSong?.isFavourite?.let { isFavourite ->
                    mp3ViewModel.addFavourite(
                        currentSong?.id.toString(), currentSong?.code.toString(), isFavourite
                    )
                }
            }
            if (currentSong?.isFavourite == true) {
                binding.ivFavourite.setImageResource(R.drawable.ic_favourite)
            } else {
                binding.ivFavourite.setImageResource(R.drawable.ic_not_favourite)
            }
            songAdapter.notifyItemChanged(mp3Position)
        }
        binding.ivPlaylist.setOnClickListener {
            if (binding.rvPlaylist.visibility == View.VISIBLE) {
                binding.rvPlaylist.visibility = View.GONE
            } else {
                binding.rvPlaylist.visibility = View.VISIBLE
            }
        }
        binding.seekbarTime.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) = Unit

            override fun onStartTrackingTouch(p0: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                binding.tvCurrentTime.text = (binding.seekbarTime.progress / 1000).formatSongTime()
                mp3Service?.setMp3Time(seekBar.progress)
            }

        })
    }

    private fun setMp3(song: Song) {
        currentSong = song
        binding.tvNameMp3.text = song.name
        binding.tvSingleMp3.text = song.singer
        binding.tvTimeTotal.text = song.duration.formatSongTime()
        var image = song.image
        if (playlistType != PlaylistType.OFFLINE_PLAYLIST) {
            val index: Int = if (image.contains("cover")) {
                image.indexOf("cover")
            } else {
                image.indexOf("banner")
            }
            image = image.substring(index, image.length)
            image = ApiBuilder.IMAGE_URL + image
        }
        binding.ivMp3.loadImage(image)
        song.id?.let { mp3ViewModel.getGenres(it) }
        setTime(song.duration * 1000)
        if (song.isFavourite) {
            binding.ivFavourite.setImageResource(R.drawable.ic_favourite)
        } else {
            binding.ivFavourite.setImageResource(R.drawable.ic_not_favourite)
        }
    }

    private fun setTime(timeTotal: Int) {
        binding.seekbarTime.apply {
            max = timeTotal
            progress = mp3CurrentTime
        }
        binding.tvCurrentTime.text = (mp3CurrentTime / 1000).formatSongTime()
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(timeTotal.toLong(), 1000) {
            override fun onTick(p0: Long) {
                if (isPlaying) {
                    binding.tvCurrentTime.text =
                        (binding.seekbarTime.progress / 1000).formatSongTime()
                    binding.seekbarTime.progress += 1000
                }
            }

            override fun onFinish() = Unit
        }
        countDownTimer?.start()
    }


    override fun onCreatedService() {
        super.onCreatedService()
        playlistType = mp3Service?.getPlaylistType()
        mp3Playlist = mp3Service?.getMp3List() ?: ArrayList()
        songAdapter?.setData(mp3Playlist)
        if (isCurrentMp3) {
            mp3Service?.getInfoCurrentMp3 { song, currentTime, isPlaying ->
                mp3CurrentTime = currentTime
                this.isPlaying = isPlaying
                setMp3(song)
                onPlayOrPauseMp3()
            }
        } else {
            if (playlistType != PlaylistType.RECOMMEND_PLAYLIST) {
                mp3Service?.playMp3(mp3Position)
            }
        }
    }

    override fun onPlayNewMp3(song: Song) {
        super.onPlayNewMp3(song)
        setMp3(song)
    }

    override fun onLoadDataComplete() {
        super.onLoadDataComplete()
        mp3Playlist = mp3Service?.getMp3List() ?: ArrayList()
        songAdapter?.setData(mp3Playlist)
        if (playlistType == PlaylistType.RECOMMEND_PLAYLIST) {
            mp3Service?.playMp3(mp3Position)
        }
    }

    override fun onPlayOrPauseMp3() {
        super.onPlayOrPauseMp3()
        if (isPlaying) {
            binding.ivPlay.setImageResource(R.drawable.ic_pause)
        } else {
            binding.ivPlay.setImageResource(R.drawable.ic_play)
        }
    }

    override fun onChangeTimeMp3(time: Int) {
        super.onChangeTimeMp3(time)
        binding.seekbarTime.progress = time
        binding.tvCurrentTime.text = (time / 1000).formatSongTime()
    }
}