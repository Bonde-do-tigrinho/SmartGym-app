package org.smartgym

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.navigation.NavHostController
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
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
import androidx.compose.material3.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.smartgym.Screens.Adm.AlunosAdminScreen
import org.smartgym.Screens.Aluno.AparelhosScreen
import org.smartgym.Screens.Aluno.HomeScreen
import org.smartgym.Screens.Aluno.PagamentosScreen
import org.smartgym.Screens.Aluno.TreinoScreen
import org.smartgym.theme.*
import org.smartgym.Screens.Professor.HomeProfessorScreen
import org.smartgym.Screens.Professor.ExerciciosScreen
import org.smartgym.Screens.Professor.FichasScreen
import org.smartgym.Screens.Adm.HomeAdminScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    userRole: UserRole,
) {
    val navController = rememberNavController()

    val items = listOf(
        Screen.HomeAluno,
        Screen.Aparelhos,
        Screen.Treino,
        Screen.Pagamentos,
    )

    val labels = mapOf(
        Screen.HomeAluno.route to "Home",
        Screen.Aparelhos.route to "Aparelhos",
        Screen.Treino.route to "Treino",
        Screen.Pagamentos.route to "Pagamento",
    )

    val icons = mapOf(
        Screen.HomeAluno.route to Icons.Rounded.Home,
        Screen.Aparelhos.route to Icons.Rounded.FitnessCenter,
        Screen.Treino.route to Icons.Rounded.Assignment,
        Screen.Pagamentos.route to Icons.Rounded. Payment,
    )
    if(userRole == UserRole.ALUNO){
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
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
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
        ){ padding ->
            NavContent(navController, userRole, Modifier.padding(padding))
        }
    }else if(userRole == UserRole.PROFESSOR){
        NavContent(navController, userRole, Modifier.padding(16.dp))
    }else{
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val adminItems = listOf(
            Screen.HomeAdmin,
            Screen.AlunosAdmin
        )

        val adminLabels = mapOf(
            Screen.HomeAdmin.route to "Dashboard",
            Screen.AlunosAdmin.route to "Alunos",
        )

        val adminIcons = mapOf(
            Screen.HomeAdmin.route to Icons.Outlined.Home,
            Screen.AlunosAdmin.route to Icons.Outlined.People,

            )

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.surface) {
                    Spacer(Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text("GYM",
                            modifier = Modifier.padding(1.dp),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                        Text(".",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text("Área do Gerente",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
                    Spacer(Modifier.height(8.dp))
                    adminItems.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationDrawerItem(
                            shape = RoundedCornerShape(15.dp),
                            label = {Text(adminLabels[screen.route] ?: "",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)) },
                            icon = {
                                Icon(adminIcons[screen.route] ?:Icons.Default.Home, contentDescription = null,)
                            },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    launchSingleTop = true
                                }
                                scope.launch {drawerState.close()}
                            },
                            modifier = Modifier.padding(horizontal = 25.dp, vertical = 2.dp),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = Color.Black,
                                selectedIconColor = Color.Black,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        ){
            Scaffold (
                containerColor = MaterialTheme.colorScheme.background,
                topBar = {
                    TopAppBar(
                        title = {
                            Row(
                                modifier = Modifier.padding(horizontal = 0.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Text("GYM",
                                    modifier = Modifier.padding(1.dp),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                                Text(".",
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }},
                        navigationIcon = {
                            IconButton(onClick = {scope.launch{drawerState.open()}}) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.shadow(elevation = 5.dp)
                    )
                }
            ) {padding ->
                NavContent(navController, userRole, Modifier.padding(padding))
            }
        }
    }

}

@Composable
fun NavContent(navController: NavHostController, userRole: UserRole, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = when(userRole) {
        UserRole.ALUNO -> Screen.HomeAluno.route
        UserRole.ADMIN -> Screen.HomeAdmin.route
        UserRole.PROFESSOR -> Screen.HomeProfessor.route
    }) {
        // Aluno
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
        composable(Screen.Aparelhos.route) { AparelhosScreen(navController) }
        composable(Screen.Treino.route) { TreinoScreen(navController) }
        composable(Screen.Pagamentos.route) { PagamentosScreen(navController) }

        // Professor
        composable(Screen.HomeProfessor.route) { HomeProfessorScreen(navController) }
        composable(Screen.Exercicios.route) { ExerciciosScreen(navController) }
        composable(Screen.Fichas.route) { FichasScreen(navController) }
        // TODO: Adicionar as screens de Fichas e Avaliações quando forem criadas

        // composable(Screen.Avaliacoes.route) { AvaliacoesScreen(navController) }

        // Admin
        composable(Screen.HomeAdmin.route) { HomeAdminScreen(navController, modifier) }
        composable (Screen.AlunosAdmin.route ) { AlunosAdminScreen(navController, modifier) }
    }
}