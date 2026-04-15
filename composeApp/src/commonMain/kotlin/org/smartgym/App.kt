package org.smartgym

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.smartgym.Screens.Auth.LoginScreen
import org.smartgym.Screens.Auth.RegisterScreen
import org.smartgym.theme.AppTheme

@Composable
fun App() {
    AppTheme {
        val usuarioLogado = remember { mutableStateOf<UserRole?>(null) }

        if (usuarioLogado.value == null) {
            val authNavController = rememberNavController()

            NavHost(navController = authNavController, startDestination = "login") {
                composable("login") {
                    LoginScreen(
                        navController = authNavController,
                        onLoginSuccess = { userRole ->
                            usuarioLogado.value = userRole
                        }
                    )
                }
                composable("cadastro") {
                    RegisterScreen(
                        navController = authNavController
                    )
                }
            }
        } else {
            AppNavigation(
                userRole = usuarioLogado.value!!,
                onLogout = {
                    usuarioLogado.value = null
                }
            )
        }
    }
}