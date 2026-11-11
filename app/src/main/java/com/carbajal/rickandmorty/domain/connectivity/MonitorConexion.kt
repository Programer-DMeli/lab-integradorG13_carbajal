package com.carbajal.rickandmorty.domain.connectivity

import kotlinx.coroutines.flow.Flow

enum class EstadoConexion { CONECTADO, SIN_CONEXION }

interface MonitorConexion {
    val estado: Flow<EstadoConexion>
}
