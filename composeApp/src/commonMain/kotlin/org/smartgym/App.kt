package org.smartgym

import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import org.smartgym.Screens.HomeScreen
import org.smartgym.theme.AppTheme

@Composable
fun App() {

    var DarkTheme by remember { mutableStateOf(false) }
    val userRole = UserRole.ADMIN
    AppTheme() {
        AppNavigation(
            userRole = userRole,
        )
    }
}