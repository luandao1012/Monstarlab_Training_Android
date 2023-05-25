package com.example.musicapplication.model

import com.google.gson.annotations.SerializedName

data class Song(
    val id: String? = null,
    val name: String,
    @SerializedName("artists_names")
    val singer: String,
    val code: String? = null,
    val duration: Int,
    @SerializedName("thumbnail")
    val image: String,
    var source: Source? = null,
    var isFavourite: Boolean = false,
    var genre: Genre? = null
)

data class Mp3Charts(
    @SerializedName("song")
    val mp3Charts: ArrayList<Song>
)

data class Source(
    @SerializedName("128")
    val link: String
)

data class Genre(
    val name: String
)

data class Mp3Genres(
    @SerializedName("genres")
    val mp3Genres: ArrayList<Genre>
)

data class Mp3Recommend(
    @SerializedName("items")
    val mp3Recommend: ArrayList<Song>
)
