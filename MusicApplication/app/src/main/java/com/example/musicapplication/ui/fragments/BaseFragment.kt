package com.example.musicapplication.ui.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.musicapplication.model.PlaylistType
import com.example.musicapplication.model.Song
import com.example.musicapplication.services.Mp3Service
import com.example.musicapplication.ui.activities.PlayActivity
import com.google.gson.Gson

abstract class BaseFragment : Fragment() {
    var playlistType: PlaylistType? = null
    private var broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            when (intent?.action) {
                PlayActivity.ACTION_FAVOURITE -> {
                    val position = intent.getIntExtra(PlayActivity.ACTION_FAVOURITE, -1)
                    onFavouriteMp3(position)
                }

                Mp3Service.INFO_MP3 -> {
                    val bundle = intent.extras
                    val song = bundle?.getString(Mp3Service.INFO_MP3).toString()
                    onPlayNewMp3(Gson().fromJson(song, Song::class.java))
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = IntentFilter().apply {
            addAction(PlayActivity.ACTION_FAVOURITE)
            addAction(Mp3Service.INFO_MP3)
        }
        activity?.registerReceiver(broadcastReceiver, intent)
    }

    open fun onFavouriteMp3(position: Int) = Unit
    open fun onPlayNewMp3(song: Song) = Unit
}