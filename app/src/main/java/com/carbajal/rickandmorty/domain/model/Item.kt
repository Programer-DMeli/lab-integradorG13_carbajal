package com.carbajal.rickandmorty.domain.model

data class Item(
    val id: Int,
    val titulo: String,
    val subtitulo: String,
    val imagenUrl: String? = null,
    val estado: String = "unknown",
    val genero: String = "unknown",
    val origen: String = "unknown",
    val ubicacion: String = "unknown",
    val episodios: Int = 0,
    val esFavorito: Boolean = false,
)
