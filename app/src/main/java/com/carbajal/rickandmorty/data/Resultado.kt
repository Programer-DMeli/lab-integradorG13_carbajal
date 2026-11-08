package com.carbajal.rickandmorty.data

import kotlinx.coroutines.CancellationException

suspend fun <T> resultado(bloque: suspend () -> T): Result<T> = try {
    Result.success(bloque())
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    Result.failure(e)
}
