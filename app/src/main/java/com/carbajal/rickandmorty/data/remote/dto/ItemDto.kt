package com.carbajal.rickandmorty.data.remote.dto

// La lista tiene envoltorio; el detalle devuelve un único personaje.
data class CharacterPageDto(val info: PageInfoDto, val results: List<ItemDto>)
data class PageInfoDto(val count: Int, val pages: Int, val next: String?)
data class PlaceDto(val name: String = "unknown")
data class ItemDto(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val origin: PlaceDto,
    val location: PlaceDto,
    val image: String,
    val episode: List<String>,
)
