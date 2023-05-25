package com.example.musicapplication.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import com.example.musicapplication.R
import com.example.musicapplication.databinding.ActivityPlayBinding
import com.example.musicapplication.formatSongTime
import com.example.musicapplication.loadImage
import com.example.musicapplication.model.PlayMode
import com.example.musicapplication.model.PlaylistType
import com.example.musicapplication.model.Song
import com.example.musicapplication.network.ApiBuilder
import com.example.musicapplication.services.Mp3Service
import com.example.musicapplication.ui.adapter.SongOfflineAdapter

class PlayActivity : BaseActivity() {
    companion object {
        const val ACTION_FAVOURITE = "ACTION_FAVOURITE"
    }

    private val binding by lazy { ActivityPlayBinding.inflate(layoutInflater) }
    private var countDownTimer: CountDownTimer? = null
    private var isCurrentMp3 = false
    private var mp3CurrentTime = 0
    private val songAdapter by lazy { SongOfflineAdapter() }
    private var mp3Playlist = arrayListOf<Song>()
    private var currentSong: Song? = null
    private var playMode = PlayMode.DEFAULT
    private val listMode = intArrayOf(
        R.drawable.ic_repeat, R.drawable.ic_repeat, R.drawable.ic_repeat_one, R.drawable.ic_shuffle
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        overridePendingTransition(R.anim.anim_up, R.anim.anim_down)
        initViews()
        initListeners()
    }

    private fun initViews() {
        binding.progressBar.visibility = View.VISIBLE
        binding.layoutPlay.visibility = View.INVISIBLE
        binding.tvNameMp3.isSelected = true
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
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            overridePendingTransition(R.anim.anim_up, R.anim.anim_down)
        }
        binding.ivPlay.setOnClickListener { mp3Service?.setPlayPauseMp3() }
        binding.ivNext.setOnClickListener {
            mp3Service?.setNextMp3()
            binding.ivNext.isEnabled = false
        }
        binding.ivPre.setOnClickListener {
            mp3Service?.setPrevMp3()
            binding.ivPre.isEnabled = false
        }
        songAdapter.setOnClickItem {
            mp3Service?.playMp3(it)
        }
        binding.ivDownload.setOnClickListener {
            Toast.makeText(applicationContext, "Bắt đầu tải xuống", Toast.LENGTH_SHORT).show()
            currentSong?.id?.let { id ->
                mp3ViewModel.downloadMp3(
                    applicationContext,
                    id, "${currentSong?.singer} - ${currentSong?.name}.mp3"
                )
            }
        }
        binding.ivMode.setOnClickListener {
            playMode = playMode.nextPlayMode()
            setButton()
            mp3Service?.setPlayMode(playMode)
        }
        binding.ivFavourite.setOnClickListener {
            currentSong?.let {
                mp3Service?.setFavouriteMp3()
                mp3Service?.getFavouriteMp3()?.let { isFavourite ->
                    mp3ViewModel.addFavourite(
                        currentSong?.id.toString(),
                        currentSong?.code.toString(),
                        isFavourite
                    )
                }
            }
            if (mp3Service?.getFavouriteMp3() == true) {
                binding.ivFavourite.setImageResource(R.drawable.ic_favourite)
            } else {
                binding.ivFavourite.setImageResource(R.drawable.ic_not_favourite)
            }
            val intent = Intent(ACTION_FAVOURITE)
            intent.putExtra(ACTION_FAVOURITE, mp3Position)
            sendBroadcast(intent)
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
        setButton()
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
            song.id?.let { mp3ViewModel.getGenres(it) }
        } else {
            binding.tvGenres.text = song.genre?.name
        }
        binding.ivMp3.loadImage(image)
        setTime(song.duration * 1000)
        if (song.isFavourite) {
            binding.ivFavourite.setImageResource(R.drawable.ic_favourite)
        } else {
            binding.ivFavourite.setImageResource(R.drawable.ic_not_favourite)
        }
        binding.ivNext.isEnabled = true
        binding.ivPre.isEnabled = true
    }

    private fun setViews() {
        binding.progressBar.visibility = View.GONE
        binding.layoutPlay.visibility = View.VISIBLE
        if (playlistType == PlaylistType.OFFLINE_PLAYLIST) {
            binding.ivFavourite.visibility = View.GONE
            binding.ivDownload.visibility = View.GONE
        } else {
            binding.ivFavourite.visibility = View.VISIBLE
            binding.ivDownload.visibility = View.VISIBLE
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
        songAdapter.setData(mp3Playlist)
        mp3Service?.let {
            playMode = it.getPlayMode()
        }
        if (isCurrentMp3) {
            mp3Service?.getInfoCurrentMp3 { song, currentTime, isPlaying ->
                mp3CurrentTime = currentTime
                this.isPlaying = isPlaying
                setViews()
                setMp3(song)
                onPlayOrPauseMp3()
            }
        } else {
            if (playlistType != PlaylistType.RECOMMEND_PLAYLIST) {
                mp3Service?.playMp3(mp3Position)
            }
        }
    }

    private fun setButton() {
        if (playMode != PlayMode.DEFAULT) {
            binding.ivMode.setColorFilter(Color.WHITE)
        } else {
            binding.ivMode.setColorFilter(Color.GRAY)
        }
        binding.ivMode.setImageResource(listMode[playMode.ordinal])
    }

    override fun onPlayNewMp3(song: Song) {
        super.onPlayNewMp3(song)
        song.id?.let { songAdapter.setMp3IdPlaying(it) }
        setViews()
        setMp3(song)
    }

    override fun onLoadDataComplete() {
        super.onLoadDataComplete()
        mp3Playlist = mp3Service?.getMp3List() ?: ArrayList()
        songAdapter.setData(mp3Playlist)
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