package com.example.sicemultiplataform.ui.theme.screens

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.sicemultiplataform.SICENETApplication

actual fun snViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        val app = this[APPLICATION_KEY] as SICENETApplication
        SNViewModel(
            snRepository        = app.container.snRepository,
            localRepository     = app.container.localRepository,
            connectivityMonitor = app.connectivityMonitor
        )
    }
}