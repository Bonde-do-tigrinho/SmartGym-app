package org.smartgym

import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import org.smartgym.theme.AppTheme

@Composable
fun App() {

    var DarkTheme by remember { mutableStateOf(false) }
    val userRole = UserRole.ALUNO
    AppTheme() {
        AppNavigation(
            userRole = userRole,
        )
    }
}