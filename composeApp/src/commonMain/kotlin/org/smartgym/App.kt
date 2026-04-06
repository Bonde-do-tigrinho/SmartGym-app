package org.smartgym

import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import org.smartgym.Screens.HomeScreen
import org.smartgym.theme.AppTheme

@Composable
fun App() {
    AppTheme {
        AppNavigation()
    }
}