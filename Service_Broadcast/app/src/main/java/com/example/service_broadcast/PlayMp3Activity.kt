package com.example.service_broadcast

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.View.OnClickListener
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.example.service_broadcast.databinding.ActivityPlayMp3Binding
import java.text.SimpleDateFormat


@SuppressLint("SimpleDateFormat")
class PlayMp3Activity : BaseActivity(), OnClickListener {
    private val binding by lazy { ActivityPlayMp3Binding.inflate(layoutInflater) }
    private val timeFormat by lazy { SimpleDateFormat("mm:ss") }
    private var rotateAnimation: ObjectAnimator? = null
    private var mp3TimeTotal = 0L
    private var mp3CurrentTime = 0
    private var countDownTimer: CountDownTimer? = null
    private var isCurrentMp3 = false
    private var playMode = 0
    private val listMode = intArrayOf(
        R.drawable.ic_repeat, R.drawable.ic_repeat, R.drawable.ic_repeat_one, R.drawable.ic_shuffle
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViews()
        initListeners()
    }

    private fun initViews() {
        val bundle = intent.extras
        mp3Position = bundle?.getInt(Mp3Service.MP3_POSITION, -1)!!
        isCurrentMp3 = bundle.getBoolean(Mp3Service.IS_CURRENT_MP3)
        rotateAnimation = ObjectAnimator.ofFloat(binding.ivCd, "rotation", 0f, 360f)
        rotateAnimation?.apply {
            duration = 10000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            start()
        }
    }

    private fun initListeners() {
        binding.ivPlay.setOnClickListener(this)
        binding.ivNext.setOnClickListener(this)
        binding.ivPre.setOnClickListener(this)
        binding.ivMode.setOnClickListener(this)
        binding.seekbarTime.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) = Unit
            override fun onStartTrackingTouch(p0: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                binding.tvCurrentTime.text = timeFormat.format(binding.seekbarTime.progress)
                mp3Service?.setMp3Time(seekBar.progress)
            }
        })
    }

    private fun setMp3(song: Song, duration: Int) {
        binding.tvSongName.text = song.name
        mp3TimeTotal = duration.toLong()
        setTotalTime()
        setButton()
    }

    private fun setTotalTime() {
        binding.tvTimeTotal.text = timeFormat.format(mp3TimeTotal)
        binding.seekbarTime.apply {
            max = mp3TimeTotal.toInt()
            progress = mp3CurrentTime
        }
        binding.tvCurrentTime.text = timeFormat.format(mp3CurrentTime)
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(mp3TimeTotal, 1000) {
            override fun onTick(p0: Long) {
                if (isPlaying) {
                    binding.tvCurrentTime.text = timeFormat.format(binding.seekbarTime.progress)
                    binding.seekbarTime.progress += 1000
                }
            }

            override fun onFinish() = Unit
        }
        countDownTimer?.start()
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.ivPlay -> mp3Service?.setPlayPauseMp3()
            binding.ivNext -> mp3Service?.playMp3(mp3Service?.getMp3PositionContinue(Mp3Service.ActionPlay.ACTION_NEXT)!!)
            binding.ivPre -> mp3Service?.playMp3(mp3Service?.getMp3PositionContinue(Mp3Service.ActionPlay.ACTION_PREV)!!)
            binding.ivMode -> {
                playMode++
                setButton()
                mp3Service?.setPlayMode(playMode)
            }
        }
    }

    private fun setButton() {
        if (playMode == 4) playMode = Mp3Service.PlayMode.DEFAULT.positionMode
        if (playMode != Mp3Service.PlayMode.DEFAULT.positionMode) {
            binding.ivMode.setColorFilter(Color.BLACK)
        } else {
            binding.ivMode.setColorFilter(Color.GRAY)
        }
        binding.ivMode.setImageResource(listMode[playMode])
    }

    override fun getInfoSong(song: Song, duration: Int) {
        super.getInfoSong(song, duration)
        setMp3(song, duration)
    }

    override fun createdService() {
        super.createdService()
        playMode = mp3Service?.getPlayMode()!!
        if (isCurrentMp3) {
            mp3Service?.getInfoCurrentMp3 { song, duration, currentTime, isPlaying ->
                mp3CurrentTime = currentTime
                this.isPlaying = isPlaying
                setMp3(song, duration)
                setPlayOrPause()
            }
        } else {
            mp3Service?.playMp3(mp3Position)
        }
    }

    override fun setPlayOrPause() {
        super.setPlayOrPause()
        if (isPlaying) {
            rotateAnimation?.resume()
            binding.ivPlay.setImageResource(R.drawable.ic_pause)
        } else {
            rotateAnimation?.pause()
            binding.ivPlay.setImageResource(R.drawable.ic_play)
        }
    }

    override fun setTimeSeekbar(time: Int) {
        super.setTimeSeekbar(time)
        binding.seekbarTime.progress = time
        binding.tvCurrentTime.text = timeFormat.format(time)
    }
}