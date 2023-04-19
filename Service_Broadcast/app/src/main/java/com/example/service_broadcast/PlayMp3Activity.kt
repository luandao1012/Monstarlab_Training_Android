package com.example.service_broadcast

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.IBinder
import android.view.View
import android.view.View.OnClickListener
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.example.service_broadcast.databinding.ActivityPlayMp3Binding
import java.text.SimpleDateFormat


@SuppressLint("SimpleDateFormat")
class PlayMp3Activity : AppCompatActivity(), OnClickListener, ServiceConnection {
    companion object {
        const val MODE_DEFAULT = 0
        const val MODE_REPEAT_ALL = 1
        const val MODE_REPEAT_ONE = 2
        const val MODE_SHUFFLE = 3
    }

    private val binding by lazy { ActivityPlayMp3Binding.inflate(layoutInflater) }
    private val timeFormat by lazy { SimpleDateFormat("mm:ss") }
    private var rotateAnimation: ObjectAnimator? = null
    private var mp3Receiver: BroadcastReceiver? = null
    private var mp3TimeTotal = 0L
    private var mp3CurrentTime = 0
    private var countDownTimer: CountDownTimer? = null
    private var mp3Service: Mp3Service? = null
    private var mp3Position = -1
    private var isPlaying = true
    private val listMode = intArrayOf(
        R.drawable.ic_repeat, R.drawable.ic_repeat, R.drawable.ic_repeat_one, R.drawable.ic_shuffle
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViews()
        initListeners()
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter()
        intentFilter.apply {
            addAction(Mp3Service.ACTION_GET_TIME)
            addAction(Mp3Service.ACTION_PLAY)
            addAction(Mp3Service.ACTION_PREV)
            addAction(Mp3Service.ACTION_NEXT)
            addAction(Mp3Service.ACTION_SEEK_TO)
            addAction(Mp3Service.ACTION_COMPLETE)
        }
        registerReceiver(mp3Receiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mp3Receiver)
    }

    private fun initViews() {
        bindService(Intent(this, Mp3Service::class.java), this, Context.BIND_AUTO_CREATE)
        val intent = intent
        mp3Position = intent.getIntExtra(MainActivity.MP3_POSITION, -1)
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
                mp3Service?.setTime(seekBar.progress)
            }
        })
        mp3Receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Mp3Service.ACTION_PREV -> {
                        mp3Service?.prevMp3(false) { song, duration -> setMp3(song, duration) }
                    }
                    Mp3Service.ACTION_NEXT -> {
                        mp3Service?.nextMp3(false) { song, duration -> setMp3(song, duration) }
                    }
                    Mp3Service.ACTION_PLAY -> setPlayPauseMp3()
                    Mp3Service.ACTION_SEEK_TO -> {
                        binding.seekbarTime.progress =
                            intent.getIntExtra(Mp3Service.ACTION_SEEK_TO, 0)
                    }
                    Mp3Service.ACTION_COMPLETE -> setPlayMode()
                }
            }
        }
    }

    private fun setMp3(song: Song, duration: Int) {
        binding.tvSongName.text = song.name
        binding.ivPlay.setImageResource(R.drawable.ic_pause)
        mp3TimeTotal = duration.toLong()
        mp3CurrentTime = mp3Service?.getCurrentTime(mp3Position)!!
        isPlaying = mp3Service?.getMp3IsPlaying()!!
        setTotalTime()
        setButton()
    }


    private fun setPlayMode() {
        when (MainActivity.positionMode) {
            MODE_DEFAULT -> {
                if (mp3Service?.setDefaultMode() == true) {
                    binding.tvCurrentTime.text = timeFormat.format(0)
                    binding.seekbarTime.progress = 0
                    setPlayPauseMp3()
                } else {
                    mp3Service?.nextMp3(false) { song, duration -> setMp3(song, duration) }
                }
            }
            MODE_REPEAT_ALL -> {
                mp3Service?.nextMp3(false) { song, duration -> setMp3(song, duration) }
            }
            MODE_REPEAT_ONE -> {
                mp3Service?.setRepeatOneMode { song, duration -> setMp3(song, duration) }
            }
            MODE_SHUFFLE -> {
                mp3Service?.setShuffleMode(false) { song, duration -> setMp3(song, duration) }
            }
        }
    }

    private fun setTotalTime() {
        binding.tvTimeTotal.text = timeFormat.format(mp3TimeTotal)
        binding.seekbarTime.apply {
            max = mp3TimeTotal.toInt()
            progress = mp3CurrentTime
        }
        binding.tvCurrentTime.text = timeFormat.format(binding.seekbarTime.progress)
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

    private fun setPlayPauseMp3() {
        if (isPlaying) {
            isPlaying = false
            rotateAnimation?.pause()
            mp3Service?.pauseOrReplay(true)
            mp3Service?.showNotification(R.drawable.ic_play)
            binding.ivPlay.setImageResource(R.drawable.ic_play)
        } else {
            isPlaying = true
            rotateAnimation?.resume()
            mp3Service?.pauseOrReplay(false)
            mp3Service?.showNotification(R.drawable.ic_pause)
            binding.ivPlay.setImageResource(R.drawable.ic_pause)
        }
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.ivPlay -> setPlayPauseMp3()
            binding.ivNext -> {
                if (MainActivity.positionMode == MODE_SHUFFLE) {
                    mp3Service?.setShuffleMode(true) { song, duration -> setMp3(song, duration) }
                } else {
                    mp3Service?.nextMp3(true) { song, duration -> setMp3(song, duration) }
                }
            }
            binding.ivPre -> {
                if (MainActivity.positionMode == MODE_SHUFFLE) {
                    mp3Service?.setShuffleMode(true) { song, duration -> setMp3(song, duration) }
                } else {
                    mp3Service?.prevMp3(true) { song, duration -> setMp3(song, duration) }
                }
            }
            binding.ivMode -> {
                MainActivity.positionMode++
                setButton()
            }
        }
    }

    override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
        val binder = service as Mp3Service.LocalBinder
        mp3Service = binder.getService()
        mp3Service?.play(mp3Position) { song, duration ->
            setMp3(song, duration)
        }
    }

    override fun onServiceDisconnected(p0: ComponentName?) = Unit

    private fun setButton() {
        if (MainActivity.positionMode == 4) MainActivity.positionMode = MODE_DEFAULT
        if (MainActivity.positionMode != MODE_DEFAULT) {
            binding.ivMode.setColorFilter(Color.BLACK)
        } else {
            binding.ivMode.setColorFilter(Color.GRAY)
        }
        binding.ivMode.setImageResource(listMode[MainActivity.positionMode])
        if (isPlaying) {
            binding.ivPlay.setImageResource(R.drawable.ic_pause)
            rotateAnimation?.resume()
        } else {
            binding.ivPlay.setImageResource(R.drawable.ic_play)
            rotateAnimation?.pause()
        }
    }
}