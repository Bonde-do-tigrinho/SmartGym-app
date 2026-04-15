package org.smartgym

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.smartgym.Screens.Auth.LoginScreen
import org.smartgym.theme.AppTheme

@Composable
fun App() {
    AppTheme {
        val usuarioLogado = remember { mutableStateOf<UserRole?>(null) }
        if (usuarioLogado.value == null) {
            LoginScreen(
                onLoginSuccess = { userRole ->
                    usuarioLogado.value = userRole
                }
            )
        } else {
            AppNavigation(userRole = usuarioLogado.value!!)
        }
    }
}
