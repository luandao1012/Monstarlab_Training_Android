package com.example.service_broadcast

import kotlinx.serialization.Serializable

@Serializable
data class Song(val name: String, val singer: String, val uri: String)