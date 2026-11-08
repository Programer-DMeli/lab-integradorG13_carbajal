package com.carbajal.rickandmorty.data.remote

import com.carbajal.rickandmorty.data.remote.dto.CharacterPageDto
import com.carbajal.rickandmorty.data.remote.dto.ItemDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("character")
    suspend fun listar(@Query("page") pagina: Int = 1, @Query("species") especie: String = "Alien"): CharacterPageDto
    @GET("character/{id}")
    suspend fun detalle(@Path("id") id: Int): ItemDto
}
