package org.smartgym

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import org.smartgym.theme.AppTheme

@Composable
@Preview
fun App() {
    var DarkTheme by remember { mutableStateOf(false) }
    val userRole = UserRole.ADMIN
    AppTheme() {
        AppNavigation(
            userRole = userRole,
        )
    }
}