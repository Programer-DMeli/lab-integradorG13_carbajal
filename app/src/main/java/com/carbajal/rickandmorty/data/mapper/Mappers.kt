package com.carbajal.rickandmorty.data.mapper

import com.carbajal.rickandmorty.data.remote.dto.ItemDto
import com.carbajal.rickandmorty.domain.model.Item
import com.carbajal.rickandmorty.data.local.ItemEntity

fun ItemDto.aModelo() = Item(id, name, species, image, status, gender,
    origin.name, location.name, episode.size)

fun Item.aEntity() = ItemEntity(id, titulo, subtitulo, imagenUrl, estado, genero,
    origen, ubicacion, episodios, esFavorito)
fun ItemEntity.aModelo() = Item(id, titulo, subtitulo, imagenUrl, estado, genero,
    origen, ubicacion, episodios, esFavorito)
