package com.example.sicemultiplataform

import android.app.Application
import com.example.sicemultiplataform.data.AppContainer
import com.example.sicemultiplataform.data.DefaultAppContainer
import com.example.sicemultiplataform.data.local.DatabaseDriverFactory
import com.example.sicemultiplataform.data.network.ConnectivityMonitor
import com.example.sicemultiplataform.data.segurity.SecureSessionManager

class SICENETApplication : Application() {
    lateinit var container: AppContainer
    lateinit var connectivityMonitor: ConnectivityMonitor

    lateinit var sessionManager: SecureSessionManager


    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(
            driverFactory = DatabaseDriverFactory(applicationContext),
            context = applicationContext
        )
        connectivityMonitor = ConnectivityMonitor(applicationContext)
        sessionManager = SecureSessionManager(applicationContext)
    }
}
