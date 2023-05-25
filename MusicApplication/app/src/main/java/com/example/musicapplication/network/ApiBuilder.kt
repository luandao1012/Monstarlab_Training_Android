package com.example.musicapplication.network

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiBuilder {
    private const val ZING_MP3_URL = "https://mp3.zing.vn"
    const val ZING_MP3_URL_AC = "http://ac.mp3.zing.vn"
    const val IMAGE_URL = "https://photo-resize-zmp3.zmdcdn.me/w240_r1x1_jpeg/"
    private val client = OkHttpClient.Builder()
        .followRedirects(false)
        .addInterceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            var redirectedUrl = request.url
            if (response.isRedirect) {
                redirectedUrl =
                    response.header("Location")?.toHttpUrlOrNull() ?: request.url
            }
            val newRequest = request.newBuilder()
                .url(redirectedUrl)
                .build()
            response.close()
            val newResponse = chain.proceed(newRequest)
            newResponse
        }
        .build()

    private fun provideApi(url: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(url)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val mp3ApiService: Mp3ApiService by lazy { provideApi(ZING_MP3_URL).create(Mp3ApiService::class.java) }
    val mp3ApiSearchService: Mp3ApiService by lazy {
        provideApi(ZING_MP3_URL_AC).create(
            Mp3ApiService::class.java
        )
    }
}