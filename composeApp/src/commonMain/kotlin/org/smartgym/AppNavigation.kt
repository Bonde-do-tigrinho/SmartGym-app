package org.smartgym

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.smartgym.Screens.Adm.HomeAdminScreen
import org.smartgym.Screens.Aluno.*
import org.smartgym.Screens.Auth.LoginScreen
import org.smartgym.Screens.Auth.RegisterScreen
import org.smartgym.Screens.Professor.HomeProfessorScreen
import org.smartgym.theme.*

// Rotas de autenticação — não mostram a bottom nav
private val rotasAuth = listOf("login", "cadastro")

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val treinoViewModel = remember { org.smartgym.viewModel.aluno.TreinoViewModel() }
    val aparelhosViewModel = remember { org.smartgym.viewModel.aluno.AparelhosViewModel() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Só mostra bottom nav fora das telas de auth e nas telas de aluno
    val mostrarBottomNav = currentRoute !in rotasAuth &&
            currentRoute in listOf(
        Screen.HomeAluno.route,
        Screen.Aparelhos.route,
        Screen.Treino.route,
        Screen.Pagamentos.route
    )

    val items = listOf(
        Screen.HomeAluno,
        Screen.Aparelhos,
        Screen.Treino,
        Screen.Pagamentos,
    )

    val icons = mapOf(
        Screen.HomeAluno.route to Icons.Rounded.Home,
        Screen.Aparelhos.route to Icons.Rounded.FitnessCenter,
        Screen.Treino.route to Icons.Rounded.Assignment,
        Screen.Pagamentos.route to Icons.Rounded.Payment,
    )

    val labels = mapOf(
        Screen.HomeAluno.route to "Home",
        Screen.Aparelhos.route to "Aparelhos",
        Screen.Treino.route to "Treino",
        Screen.Pagamentos.route to "Pagamento",
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (mostrarBottomNav) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    items.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(Screen.HomeAluno.route) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    icons[screen.route] ?: Icons.Default.Home,
                                    contentDescription = null,
                                    tint = if (selected) MaterialTheme.colorScheme.primary else TextGray
                                )
                            },
                            label = {
                                Text(
                                    labels[screen.route] ?: "",
                                    color = if (selected) MaterialTheme.colorScheme.primary else TextGray
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login", // ← Login é a primeira tela
            modifier = Modifier.padding(innerPadding)
        ) {

            // ── Auth ──────────────────────────────────────────
            composable("login") {
                LoginScreen(navController)
            }
            composable("cadastro") {
                RegisterScreen(navController)
            }

            // ── Aluno ─────────────────────────────────────────
            composable(Screen.HomeAluno.route) {
                val usuarioLogado = UserHomeData(
                    userName = "Leandro",
                    treinoAtual = "TREINO A",
                    focoTreino = "Peito e Tríceps",
                    qtdExercicios = 5,
                    aparelhosLivres = 12,
                    pessoasEmUso = 4,
                    professorNome = "Rafael Silva",
                    professorNota = 4.9,
                    planoVencimento = "15/04/2026",
                    planoValor = "R$ 149,90"
                )
                HomeScreen(navController = navController, userData = usuarioLogado)
            }
            composable(Screen.Aparelhos.route) { AparelhosScreen(navController = navController, viewModel = aparelhosViewModel)}
            composable(Screen.Treino.route) { TreinoScreen(navController = navController, viewModel = treinoViewModel) }
            composable(Screen.Pagamentos.route) { PagamentosScreen(navController) }

            // ── Professor ─────────────────────────────────────
            composable(Screen.HomeProfessor.route) {
                HomeProfessorScreen(navController)
            }

            // ── Admin ─────────────────────────────────────────
            composable(Screen.HomeAdmin.route) {
                HomeAdminScreen(navController)
            }
        }
    }
}