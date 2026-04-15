package org.smartgym

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.unit.dp

fun main() {
    System.setProperty("skiko.renderApi", "SOFTWARE")

    try {
        application {
            val windowState = rememberWindowState(width = 450.dp, height = 800.dp)

            Window(
                onCloseRequest = ::exitApplication,
                state = windowState,
                title = "SmartGym Desktop"
            ) {
                App()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}