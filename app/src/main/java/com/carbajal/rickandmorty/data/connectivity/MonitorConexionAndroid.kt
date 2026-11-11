package com.carbajal.rickandmorty.data.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.carbajal.rickandmorty.domain.connectivity.EstadoConexion
import com.carbajal.rickandmorty.domain.connectivity.MonitorConexion
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class MonitorConexionAndroid(context: Context) : MonitorConexion {
    private val manager = context.applicationContext.getSystemService(ConnectivityManager::class.java)

    override val estado = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            private var redActual: Network? = manager.activeNetwork

            override fun onAvailable(network: Network) {
                redActual = network
            }

            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                if (network == redActual) trySend(capabilities.aEstado())
            }

            override fun onLost(network: Network) {
                if (network == redActual) {
                    redActual = null
                    trySend(EstadoConexion.SIN_CONEXION)
                }
            }
        }
        manager.registerDefaultNetworkCallback(callback)
        // La consulta inicial ocurre fuera de los callbacks; sus cambios usan las capacidades recibidas.
        trySend(manager.getNetworkCapabilities(manager.activeNetwork).aEstado())
        awaitClose { manager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()
}

private fun NetworkCapabilities?.aEstado(): EstadoConexion =
    if (this != null && hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
        hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) EstadoConexion.CONECTADO
    else EstadoConexion.SIN_CONEXION
