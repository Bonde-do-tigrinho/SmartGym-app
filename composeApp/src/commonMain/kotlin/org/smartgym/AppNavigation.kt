package org.smartgym

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Payment
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.smartgym.Screens.*
import org.smartgym.Screens.HomeScreen
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.smartgym.theme.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val items = listOf(
        Screen.Home,
        Screen.Aparelhos,
        Screen.Treino,
        Screen.Pagamentos,
    )

    val labels = mapOf(
        Screen.Home.route to "Home",
        Screen.Aparelhos.route to "Aparelhos",
        Screen.Treino.route to "Treino",
        Screen.Pagamentos.route to "Pagamento",
    )

    val icons = mapOf(
        Screen.Home.route to Icons.Rounded.Home,
        Screen.Aparelhos.route to Icons.Rounded.FitnessCenter,
        Screen.Treino.route to Icons.Rounded.Assignment,
        Screen.Pagamentos.route to Icons.Rounded. Payment,
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                items.forEach { screen ->
                    val selected = currentRoute == screen.route
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route){
                                popUpTo(navController.graph.startDestinationRoute ?: Screen.Home.route) {                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(icons[screen.route] ?:Icons.Default.Home, contentDescription = null,
                                tint = if(selected) MaterialTheme.colorScheme.primary else TextGray)
                        },
                        label = {
                            Text(labels[screen.route] ?: "",
                                color = if(selected) MaterialTheme.colorScheme.primary else TextGray) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ){innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route
        ){
            composable(Screen.Home.route) {
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

                HomeScreen(
                    navController = navController,
                    userData = usuarioLogado
                )
            }
            composable(Screen.Aparelhos.route) {
                AparelhosScreen(navController)
            }
            composable(Screen.Treino.route) {
                TreinoScreen(navController)
            }
            composable(Screen.Pagamentos.route) {
                PagamentosScreen(navController)
            }
        }
    }

}