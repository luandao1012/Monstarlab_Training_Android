package com.example.musicapplication.repository

import android.util.Log
import com.example.musicapplication.model.Song
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await

class FavouriteRepository {
    private val store = Firebase.firestore
        .collection("ListMp3Favourite")
        .document("List")
        .collection("List")

    suspend fun changeFavourite(song: Song, isFavourite: Boolean) {
        try {
            if (isFavourite) {
                val favourite = hashMapOf(
                    "song" to Gson().toJson(song),
                    "timestamp" to FieldValue.serverTimestamp()
                )
                song.id?.let { id ->
                    store.document(id).set(favourite).await()
                }
            } else {
                song.id?.let { id ->
                    store.document(id).delete().await()
                }
            }
        } catch (e: Exception) {
            Log.e("test123", e.message.toString())
        }
    }

    suspend fun getAllMp3Favourite(): List<Song> {
        val mp3List = arrayListOf<Song>()
        try {
            val data =
                store.orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()
            data?.documents?.forEach { documents ->
                val song = documents.data?.get("song").toString()
                mp3List.add(Gson().fromJson(song, Song::class.java))
            }
        } catch (e: Exception) {
            Log.e("test123", e.message.toString())
        }
        return mp3List
    }

    suspend fun getAllIdMp3Favourite(): List<String> {
        var idList: List<String>
        try {
            val data =
                store.get().await()
            idList = data.documents.map { it.id }
        } catch (e: Exception) {
            idList = listOf()
            Log.e("test123", e.message.toString())
        }
        return idList
    }
}