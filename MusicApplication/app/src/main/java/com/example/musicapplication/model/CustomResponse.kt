package com.example.musicapplication.model

import com.google.gson.annotations.SerializedName

data class CustomResponse<T>(
    @SerializedName("err")
    val error: Int,
    @SerializedName("msg")
    val message: String,
    val data: T,
    @SerializedName("timestamp")
    val timeStamp: Long
)