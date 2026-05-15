package com.example.sicemultiplataform.data.network

import kotlinx.coroutines.flow.Flow

// Clase para monitorizar la conexión a internet
expect class ConnectivityMonitor(context: Any) {
    val isConnected: Flow<Boolean>
}