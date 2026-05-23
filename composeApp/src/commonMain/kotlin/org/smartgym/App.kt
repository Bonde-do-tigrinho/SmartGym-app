package org.smartgym

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.smartgym.Auth.TokenManager
import org.smartgym.Screens.Auth.ForgotPasswordScreen
import org.smartgym.Screens.Auth.LoginScreen
import org.smartgym.Screens.Auth.RegisterScreen
import org.smartgym.Screens.Auth.ResetPasswordScreen
import org.smartgym.theme.AppTheme
import org.smartgym.viewModel.AuthViewModel

@Composable
fun App(resetToken: String? = null) {
    AppTheme {
        val usuarioLogado = remember { mutableStateOf<UserRole?>(null) }
        val authViewModel = remember { AuthViewModel() }
        val authNavController = rememberNavController()

        val startDestination = if (resetToken != null) "resetar-senha/$resetToken" else "login"

        if (usuarioLogado.value == null) {
            val authNavController = rememberNavController()

            NavHost(navController = authNavController, startDestination = "login") {
                composable("login") {
                    LoginScreen(
                        navController = authNavController,
                        onLoginSuccess = { userRole -> usuarioLogado.value = userRole },
                        viewModel = authViewModel
                    )
                }
                composable("cadastro") {
                    RegisterScreen(
                        navController = authNavController,
                        viewModel = authViewModel
                    )
                }
                composable("recuperar-senha") {
                    ForgotPasswordScreen(
                        navController = authNavController,
                        viewModel = authViewModel
                    )
                }
                composable("resetar-senha/{token}") { backStackEntry ->
                    val token = backStackEntry.savedStateHandle.get<String>("token") ?: ""
                    ResetPasswordScreen(
                        navController = authNavController,
                        viewModel = authViewModel,
                        token = token
                    )
                }
            }
        } else {
            AppNavigation(
                userRole = usuarioLogado.value!!,
                onLogout = {
                    TokenManager.clearToken()
                    usuarioLogado.value = null
                }
            )
        }
    }
}