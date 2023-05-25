package com.example.musicapplication.network

import com.example.musicapplication.model.CustomResponse
import com.example.musicapplication.model.Mp3Charts
import com.example.musicapplication.model.Mp3Genres
import com.example.musicapplication.model.Mp3Recommend
import com.example.musicapplication.model.SearchResponse
import com.example.musicapplication.model.Song
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url

interface Mp3ApiService {
    @GET("/xhr/chart-realtime?songId=0&videoId=0&albumId=0&chart=song&time=-1")
    suspend fun getMp3Charts(): Response<CustomResponse<Mp3Charts>>

    @GET("/xhr/media/get-source?type=audio")
    suspend fun getMp3Info(@Query("key") key: String): Response<CustomResponse<Song>>

    @GET("/xhr/media/get-info?type=audio")
    suspend fun getGenres(@Query("id") id: String): Response<CustomResponse<Mp3Genres>>

    @GET("/complete?type=artist,song,key,code&num=10")
    suspend fun searchMp3(@Query("query") query: String): Response<SearchResponse>

    @GET("/xhr/recommend?type=audio")
    suspend fun getMp3Recommend(@Query("id") id: String): Response<CustomResponse<Mp3Recommend>>

    @Streaming
    @GET
    fun getLinkStreaming(@Url url: String): Call<ResponseBody>
}