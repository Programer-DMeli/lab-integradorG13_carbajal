package com.carbajal.rickandmorty.ui

import java.io.IOException
import retrofit2.HttpException

fun mensajeDe(error: Throwable): String = when (error) {
    is IOException -> "No hay conexión a internet. Revisa tu conexión e inténtalo otra vez."
    is HttpException -> "El servidor respondió con error ${error.code()}. Inténtalo nuevamente."
    else -> "No se pudo completar la operación. Inténtalo nuevamente."
}
