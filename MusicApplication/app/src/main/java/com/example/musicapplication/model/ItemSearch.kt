package com.example.musicapplication.model

import com.google.gson.annotations.SerializedName

data class ItemSearch(
    val id: String,
    val name: String,
    @SerializedName("artist")
    val single: String,
    val duration: Int,
    @SerializedName("thumb")
    val image: String,
//    var link: String,
//    var genres: List<Genres>? = null
)

data class SearchResponse(
    val result: Boolean,
    val data: List<DataItemSearch>
)

data class DataItemSearch(
    @SerializedName("song")
    val listItem: List<ItemSearch>,
)