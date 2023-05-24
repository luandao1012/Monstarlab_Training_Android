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
    var isFavourite: Boolean = false
)

data class Mp3Charts(
    @SerializedName("song")
    val mp3Charts: List<Song>
)

data class Source(
    @SerializedName("128")
    val link: String
)

data class Genre(
    val id: String,
    val name: String
)

data class Mp3Genres(
    @SerializedName("genres")
    val mp3Genres: List<Genre>
)

data class Mp3Recommend(
    @SerializedName("items")
    val mp3Recommend: List<Song>
)
