package com.example.sicemultiplataform.data.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

actual class ConnectivityMonitor actual constructor(context: Any) {
    private val appContext = context as Context

    @SuppressLint("MissingPermission")
    actual val isConnected: Flow<Boolean> = callbackFlow {
        val manager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { trySend(true) }
            override fun onLost(network: Network) { trySend(false) }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        manager.registerNetworkCallback(request, callback)
        awaitClose { manager.unregisterNetworkCallback(callback) }
    }
}