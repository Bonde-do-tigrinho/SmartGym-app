package org.smartgym

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Payment
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import org.smartgym.Screens.Adm.AlunosAdminScreen
import org.smartgym.Screens.Adm.EditarAlunoScreen
import org.smartgym.Screens.Adm.HomeAdminScreen
import org.smartgym.Screens.Adm.NovoAlunoScreen
import org.smartgym.Screens.Adm.UnidadesScreen
import org.smartgym.Screens.Aluno.AparelhosScreen
import org.smartgym.Screens.Aluno.HomeScreen
import org.smartgym.Screens.Aluno.PagamentosScreen
import org.smartgym.Screens.Aluno.PerfilAlunoScreen
import org.smartgym.Screens.Aluno.TreinoScreen
import org.smartgym.Screens.Professor.AvaliacoesScreen
import org.smartgym.Screens.Professor.CriarExercicioScreen
import org.smartgym.Screens.Professor.ExerciciosScreen
import org.smartgym.Screens.Professor.FichasScreen
import org.smartgym.Screens.Professor.HomeProfessorScreen
import org.smartgym.viewModel.aluno.AparelhosViewModel
import org.smartgym.viewModel.aluno.TreinoViewModel
import org.smartgym.theme.TextGray
import org.smartgym.viewModel.Adm.AlunosViewModel
import org.smartgym.viewModel.Professor.ExerciciosViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType.Application.Json
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(userRole: UserRole, onLogout: () -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val rotasComBottomNav = listOf(
        Screen.HomeAluno.route,
        Screen.Aparelhos.route,
        Screen.Treino.route,
        Screen.Pagamentos.route,
        Screen.PerfilAluno.route
    )

    val mostrarBottomNav = currentRoute in rotasComBottomNav

    val items = listOf(
        Screen.HomeAluno,
        Screen.Aparelhos,
        Screen.Treino,
        Screen.Pagamentos,
        Screen.PerfilAluno
    )

    val labels = mapOf(
        Screen.HomeAluno.route to "Home",
        Screen.Aparelhos.route to "Aparelhos",
        Screen.Treino.route to "Treino",
        Screen.Pagamentos.route to "Pagamento",
        Screen.PerfilAluno.route to "Perfil"
    )

    val icons = mapOf(
        Screen.HomeAluno.route to Icons.Rounded.Home,
        Screen.Aparelhos.route to Icons.Rounded.FitnessCenter,
        Screen.Treino.route to Icons.Rounded.Assignment,
        Screen.Pagamentos.route to Icons.Rounded.Payment,
        Screen.PerfilAluno.route to Icons.Rounded.Person
    )

    when (userRole) {
        UserRole.ALUNO -> {
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
            ) { padding ->
                NavContent(
                    navController = navController,
                    userRole = userRole,
                    onLogout = onLogout,
                    modifier = Modifier.padding(padding)
                )
            }
        }

        UserRole.PROFESSOR -> {
            NavContent(
                navController = navController,
                userRole = userRole,
                onLogout = onLogout,
            )
        }

        UserRole.ADMIN -> {
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            val adminItems = listOf(
                Screen.HomeAdmin,
                Screen.AlunosAdmin,
                Screen.UnidadesAdmin
            )

            val adminLabels = mapOf(
                Screen.HomeAdmin.route to "Dashboard",
                Screen.AlunosAdmin.route to "Alunos",
                Screen.UnidadesAdmin.route to "Unidades",
            )

            val adminIcons = mapOf(
                Screen.HomeAdmin.route to Icons.Outlined.Home,
                Screen.AlunosAdmin.route to Icons.Outlined.People,
                Screen.UnidadesAdmin.route to Icons.Outlined.Apartment
            )
            val alunosViewModel: AlunosViewModel = viewModel()

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.surface) {
                        Spacer(Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("GYM", modifier = Modifier.padding(1.dp), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSecondary)
                            Text(".", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                        }
                        Text("Área do Gerente", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
                        Spacer(Modifier.height(8.dp))

                        adminItems.forEach { screen ->
                            val selected = currentRoute == screen.route
                            NavigationDrawerItem(
                                shape = RoundedCornerShape(15.dp),
                                label = { Text(adminLabels[screen.route] ?: "", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)) },
                                icon = { Icon(adminIcons[screen.route] ?: Icons.Default.Home, contentDescription = null) },
                                selected = selected,
                                onClick = {
                                    navController.navigate(screen.route) { launchSingleTop = true }
                                    scope.launch { drawerState.close() }
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
            ) {
                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(modifier = Modifier.padding(horizontal = 0.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text("GYM", modifier = Modifier.padding(1.dp), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSecondary)
                                    Text(".", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                                }
                            },
                            navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, contentDescription = "Menu") } },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.shadow(elevation = 5.dp)
                        )
                    }
                ) { padding ->
                    NavContent(navController = navController, userRole = userRole, onLogout = onLogout, modifier = Modifier.padding(padding))
                }
            }
        }
    }
}

@Composable
fun NavContent(
    navController: NavHostController,
    userRole: UserRole,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val treinoViewModel = remember { TreinoViewModel() }
    val aparelhosViewModel = remember { AparelhosViewModel() }
    val alunosViewModel = remember { AlunosViewModel() }

    val httpClient = remember {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
        }
    }

    val exercicioRepository = remember { org.smartgym.repository.ApiExercicioRepository(httpClient) }

    val exerciciosViewModel = remember { ExerciciosViewModel(exercicioRepository) }

    // ----------------------------------------------------------------

    NavHost(
        navController = navController,
        startDestination = when (userRole) {
            UserRole.ALUNO -> Screen.HomeAluno.route
            UserRole.PROFESSOR -> Screen.HomeProfessor.route
            UserRole.ADMIN -> Screen.HomeAdmin.route
        },
        modifier = modifier
    ) {
        composable(Screen.HomeAluno.route) {
            val usuarioLogado = UserHomeData("Leandro", "TREINO A", "Peito e Tríceps", 5, 12, 4, "Rafael Silva", 4.9, "15/04/2026", "R$ 149,90")
            HomeScreen(navController = navController, userData = usuarioLogado)
        }
        composable(Screen.Aparelhos.route) { AparelhosScreen(navController = navController, viewModel = aparelhosViewModel) }
        composable(Screen.Treino.route) { TreinoScreen(navController = navController, viewModel = treinoViewModel) }
        composable(Screen.Pagamentos.route) { PagamentosScreen(navController) }

        composable(Screen.PerfilAluno.route) {
            PerfilAlunoScreen(navController = navController, onLogout = onLogout)
        }

        composable(Screen.HomeProfessor.route) { HomeProfessorScreen(navController) }

        // --- MUDANÇA AQUI: Passando o viewModel para a ExerciciosScreen ---
        composable(Screen.Exercicios.route) {
            ExerciciosScreen(
                navController = navController,
                viewModel = exerciciosViewModel
            )
        }
        composable(Screen.NovoExercicio.route) {
            CriarExercicioScreen(
                navController = navController,
                viewModel = exerciciosViewModel
            )
        }
        // ------------------------------------------------------------------

        composable(Screen.Fichas.route) { FichasScreen(navController) }
        composable(Screen.Avaliacoes.route) { AvaliacoesScreen(navController) }

        // ────────────────────────────────────────────────────
        // ADMIN
        // ────────────────────────────────────────────────────
        composable(Screen.HomeAdmin.route) { HomeAdminScreen(navController) }
        composable(Screen.AlunosAdmin.route) { AlunosAdminScreen(navController, viewModel = alunosViewModel) }
        composable(Screen.UnidadesAdmin.route) { UnidadesScreen() }
        composable(Screen.NovoAluno.route) { NovoAlunoScreen(navController, viewModel = alunosViewModel) }
        composable(
            route = Screen.EditarAluno.route + "/{alunoId}",
            arguments = listOf(navArgument("alunoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val alunoId = backStackEntry.arguments?.getInt("alunoId") ?: return@composable
            EditarAlunoScreen(navController, alunoId, viewModel = alunosViewModel)
        }
    }
}