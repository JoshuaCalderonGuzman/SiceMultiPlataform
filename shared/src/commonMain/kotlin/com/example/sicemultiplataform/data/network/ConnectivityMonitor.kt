package com.example.sicemultiplataform.data.network

import kotlinx.coroutines.flow.Flow

expect class ConnectivityMonitor(context: Any) {
    val isConnected: Flow<Boolean>
}