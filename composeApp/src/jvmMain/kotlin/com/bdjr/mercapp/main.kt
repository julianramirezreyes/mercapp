package com.bdjr.mercapp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.bdjr.mercapp.di.initKoin
import com.bdjr.mercapp.platform.PlatformContext

fun main() = application {
    initKoin(PlatformContext())
    Window(
        onCloseRequest = ::exitApplication,
        title = "Mercapp",
    ) {
        App()
    }
}