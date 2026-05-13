package com.example.sicemultiplataform.data.network

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.InetSocketAddress
import java.net.Socket

actual class ConnectivityMonitor actual constructor(context: Any) {
    actual val isConnected: Flow<Boolean> = flow {
        var lastState = false
        val initialState = try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
                true
            }
        } catch (e: Exception) { false }

        emit(initialState)
        lastState = initialState
        while (true) {
            val connected = try {
                Socket().use { socket ->
                    socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
                    true
                }
            } catch (e: Exception) { false }

            if (connected != lastState) {
                emit(connected)
                lastState = connected
            }
            delay(3000)
        }
    }
}