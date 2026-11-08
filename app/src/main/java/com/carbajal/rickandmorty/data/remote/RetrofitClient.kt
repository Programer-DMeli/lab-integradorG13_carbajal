package com.carbajal.rickandmorty.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    fun crear(baseUrl: String = "https://rickandmortyapi.com/api/"): ApiService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS).callTimeout(30, TimeUnit.SECONDS).build())
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(ApiService::class.java)
}
