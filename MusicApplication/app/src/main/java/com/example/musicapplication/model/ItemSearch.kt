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
)

data class SearchResponse(
    val result: Boolean,
    val data: ArrayList<DataItemSearch>
)

data class DataItemSearch(
    @SerializedName("song")
    val listItem: ArrayList<ItemSearch>,
)