package com.carbajal.rickandmorty.data.mapper

import com.carbajal.rickandmorty.data.remote.dto.ItemDto
import com.carbajal.rickandmorty.domain.model.Item

fun ItemDto.aModelo() = Item(id, name, species, image, status, gender,
    origin.name, location.name, episode.size)
