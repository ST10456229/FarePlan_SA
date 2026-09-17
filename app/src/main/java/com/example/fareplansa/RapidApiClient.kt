package com.example.fareplansa

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Shared Retrofit instance for all RapidAPI calls.
 * Long timeouts because flight APIs can be slow.
 */
object RapidApiClient {

    // ⚠️ Replace with your RapidAPI key and host
    const val API_KEY = "c4395b027amsh808fde3b0b8a200p118bbbjsn26fab2e43d35"
    const val API_HOST = "sky-scrapper.p.rapidapi.com"

    val instance: Retrofit by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl("https://$API_HOST/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}