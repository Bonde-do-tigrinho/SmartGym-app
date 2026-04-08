package org.smartgym

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import org.smartgym.Screens.Aluno.HomeScreen
import org.smartgym.theme.AppTheme

@Composable
fun App() {
    AppTheme {
        AppNavigation(userRole = UserRole.ALUNO)
    }
}
