package com.example.sicemultiplataform.ui.theme.screens

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.sicemultiplataform.data.local.DatabaseDriverFactory
import com.example.sicemultiplataform.data.DefaultAppContainerDesktop
import com.example.sicemultiplataform.data.network.ConnectivityMonitor

actual fun snViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        val driverFactory = DatabaseDriverFactory()
        val container = DefaultAppContainerDesktop(driverFactory)
        SNViewModel(
            snRepository    = container.snRepository,
            localRepository = container.localRepository,
            connectivityMonitor = ConnectivityMonitor(Unit)
        )
    }
}