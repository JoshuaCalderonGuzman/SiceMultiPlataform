package com.example.sicemultiplataform

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application


fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "SiceMultiPlataform",
        icon = painterResource("Logo.ico")
    ) {
        App()
    }
}