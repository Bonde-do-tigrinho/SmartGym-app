package org.smartgym

import MaquinaIotViewModel
import MaquinaViewModel
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material.icons.outlined.SupervisorAccount
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Payment
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.collectLatest
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
import org.smartgym.Screens.Professor.CriarAvaliacaoScreen
import org.smartgym.Screens.Professor.CriarExercicioScreen
import org.smartgym.Screens.Professor.CriarFichaScreen
import org.smartgym.Screens.Professor.ExerciciosScreen
import org.smartgym.Screens.Professor.FichasScreenReal
import org.smartgym.Screens.Professor.HomeProfessorScreen
import org.smartgym.viewModel.aluno.AparelhosViewModel
import org.smartgym.viewModel.aluno.TreinoViewModel
import org.smartgym.theme.TextGray
import org.smartgym.viewModel.Adm.AlunosViewModel
import org.smartgym.viewModel.Professor.ExerciciosViewModel
import org.smartgym.viewModel.Professor.FichasViewModel
import org.smartgym.Screens.Adm.EditarProfessorScreen
import org.smartgym.Screens.Adm.MaquinasAdminScreen
import org.smartgym.Screens.Adm.MaquinasIotAdminScreen
import org.smartgym.Screens.Professor.FichasScreen
import org.smartgym.Screens.Adm.NovoProfessorScreen
import org.smartgym.Screens.Adm.ProfessoresAdminScreen
import org.smartgym.viewModel.Professor.AvaliacoesViewModel
import org.smartgym.viewModel.Professor.CriarFichaViewModel
import org.smartgym.repository.ApiFichaTreinoRepository
import org.smartgym.viewModel.Adm.PlanoViewModel
import org.smartgym.viewModel.Adm.ProfessoresViewModel
import org.smartgym.viewModel.aluno.AlunoPerfilViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(userRole: UserRole, onLogout: () -> Unit, perfilCompleto: Boolean) { // 👈 Recebendo o booleano aqui
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

    val snackbarHostState = remember { SnackbarHostState() }

    when (userRole) {
        UserRole.ALUNO -> {
            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState) { data ->
                        Snackbar(
                            snackbarData = data,
                            containerColor = Color(0xFF1A1A1A),
                            contentColor = Color(0xFFD9FF00),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                },
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
                    perfilCompleto = perfilCompleto, // 👈 Passando o booleano adiante para o NavContent
                    modifier = Modifier.padding(padding),
                    snackbarHostState = snackbarHostState
                )
            }
        }

        UserRole.PROFESSOR -> {
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val professorItems = listOf(
                Screen.HomeProfessor,
                Screen.Exercicios,
                Screen.Fichas,
                Screen.Avaliacoes
            )
            val professorRotasSemHeader = setOf(
                Screen.NovoExercicio.route,
                Screen.NovaAvaliacao.route,
                Screen.NovaFicha.route,
                Screen.EditarFicha.route
            )
            val mostrarHeaderProfessor = currentRoute !in professorRotasSemHeader
            val professorLabels = mapOf(
                Screen.HomeProfessor.route to "Dashboard",
                Screen.Exercicios.route to "Exercícios",
                Screen.Fichas.route to "Fichas",
                Screen.Avaliacoes.route to "Avaliações"
            )

            val professorIcons = mapOf(
                Screen.HomeProfessor.route to Icons.Outlined.Home,
                Screen.Exercicios.route to Icons.Rounded.FitnessCenter,
                Screen.Fichas.route to Icons.Rounded.Assignment,
                Screen.Avaliacoes.route to Icons.Outlined.People
            )

            ModalNavigationDrawer(
                gesturesEnabled = mostrarHeaderProfessor,
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
                        Text("Área do Instrutor", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
                        Spacer(Modifier.height(8.dp))

                        professorItems.forEach { screen ->
                            val selected = currentRoute == screen.route
                            NavigationDrawerItem(
                                shape = RoundedCornerShape(15.dp),
                                label = { Text(professorLabels[screen.route] ?: "", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)) },
                                icon = { Icon(professorIcons[screen.route] ?: Icons.Default.Home, contentDescription = null) },
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

                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
                        Spacer(Modifier.height(8.dp))

                        NavigationDrawerItem(
                            shape = RoundedCornerShape(15.dp),
                            label = { Text("Sair", fontWeight = FontWeight.SemiBold) },
                            icon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onLogout()
                            },
                            modifier = Modifier.padding(horizontal = 25.dp, vertical = 2.dp),
                            colors = NavigationDrawerItemDefaults.colors(
                                unselectedTextColor = MaterialTheme.colorScheme.error,
                                unselectedIconColor = MaterialTheme.colorScheme.error
                            )
                        )
                    }
                }
            ) {
                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState) { data ->
                            Snackbar(
                                snackbarData = data,
                                containerColor = Color(0xFF1A1A1A),
                                contentColor = Color(0xFFD9FF00),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    },
                    topBar = {
                        if (mostrarHeaderProfessor) {
                            TopAppBar(
                                title = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("GYM", modifier = Modifier.padding(1.dp), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSecondary)
                                        Text(".", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                                    }
                                },
                                navigationIcon = {
                                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.shadow(elevation = 5.dp)
                            )
                        }
                    }
                ) { padding ->
                    NavContent(
                        navController = navController,
                        userRole = userRole,
                        onLogout = onLogout,
                        perfilCompleto = true, // Professor não precisa de completar perfil
                        modifier = Modifier.padding(padding),
                        snackbarHostState = snackbarHostState
                    )
                }
            }
        }

        UserRole.ADMIN -> {
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            val adminItems = listOf(
                Screen.HomeAdmin.route,
                Screen.AlunosAdmin.route,
                Screen.ProfessoresAdmin.route,
                Screen.UnidadesAdmin.route,
                "telaPlanos",
                Screen.MaquinasAdmin.route,
                Screen.MaquinasIotAdmin.route,
            )

            val adminLabels = mapOf(
                Screen.HomeAdmin.route to "Dashboard",
                Screen.AlunosAdmin.route to "Alunos",
                Screen.UnidadesAdmin.route to "Unidades",
                "telaPlanos" to "Planos",
                Screen.MaquinasAdmin.route to "Máquinas",
                Screen.MaquinasIotAdmin.route to "Máquinas IOTs",
                Screen.ProfessoresAdmin.route to "Professores",
            )

            val adminIcons = mapOf(
                Screen.HomeAdmin.route to Icons.Outlined.Home,
                Screen.AlunosAdmin.route to Icons.Outlined.People,
                Screen.UnidadesAdmin.route to Icons.Outlined.Apartment,
                "telaPlanos" to Icons.Rounded.Assignment,
                Screen.MaquinasAdmin.route to Icons.Outlined.FitnessCenter,
                Screen.MaquinasIotAdmin.route to Icons.Outlined.Sensors,
                Screen.ProfessoresAdmin.route to Icons.Outlined.SupervisorAccount,
            )

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

                        adminItems.forEach { rota ->
                            val selected = currentRoute == rota
                            NavigationDrawerItem(
                                shape = RoundedCornerShape(15.dp),
                                label = { Text(adminLabels[rota] ?: "", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)) },
                                icon = { Icon(adminIcons[rota] ?: Icons.Default.Home, contentDescription = null) },
                                selected = selected,
                                onClick = {
                                    navController.navigate(rota) { launchSingleTop = true }
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
                        Spacer(Modifier.height(16.dp))

                        HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))

                        Spacer(Modifier.height(8.dp))

                        NavigationDrawerItem(
                            label = { Text("Sair", fontWeight = FontWeight.SemiBold) },
                            icon = { Icon(Icons.Default.Logout, contentDescription = null) },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onLogout()
                            },
                            modifier = Modifier.padding(horizontal = 25.dp, vertical = 2.dp),
                            colors = NavigationDrawerItemDefaults.colors(
                                unselectedTextColor = MaterialTheme.colorScheme.error,
                                unselectedIconColor = MaterialTheme.colorScheme.error
                            )
                        )
                    }
                }
            ) {
                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState) { data ->
                            Snackbar(
                                snackbarData = data,
                                containerColor = Color(0xFF1A1A1A),
                                contentColor = Color(0xFFD9FF00),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    },
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
                    NavContent(
                        navController = navController,
                        userRole = userRole,
                        onLogout = onLogout,
                        perfilCompleto = true, // Admin não precisa de completar perfil
                        modifier = Modifier.padding(padding),
                        snackbarHostState = snackbarHostState
                    )
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
    perfilCompleto: Boolean,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState
) {
    val planosViewModel = remember { PlanoViewModel() }
    val treinoViewModel = remember { TreinoViewModel() }
    val aparelhosViewModel = remember { AparelhosViewModel() }
    val alunosViewModel = remember { AlunosViewModel() }
    val exerciciosViewModel = remember { ExerciciosViewModel() }
    val maquinaViewModel = remember { MaquinaViewModel() }
    val maquinaIotViewModel = remember { MaquinaIotViewModel() }
    val avaliacaoRepository = remember { org.smartgym.repository.ApiAvaliacaoRepository() }
    val alunoRepository = remember { org.smartgym.repository.ApiAlunoRepository() }
    val exercicioRepository = remember { org.smartgym.repository.ApiExercicioRepository() }
    val fichaRepository = remember { ApiFichaTreinoRepository() }
    val avaliacoesViewModel = remember { AvaliacoesViewModel(avaliacaoRepository, alunoRepository) }
    val fichasViewModel = remember { FichasViewModel(fichaRepository, alunoRepository) }
    val criarFichaViewModel = remember { CriarFichaViewModel(alunoRepository, exercicioRepository, fichaRepository) }
    val professoresViewModel = remember { ProfessoresViewModel()}
    val alunoPerfilViewModel = remember { AlunoPerfilViewModel() }

    LaunchedEffect(Unit) {
        alunosViewModel.snackbarEvent.collectLatest { message ->
            println("SNACKBAR: $message")
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(Unit) {
        criarFichaViewModel.snackbarEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    LaunchedEffect(Unit) {
        fichasViewModel.snackbarEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val startDest = when (userRole) {
        UserRole.ALUNO -> if (perfilCompleto) Screen.HomeAluno.route else "completar-perfil"
        UserRole.PROFESSOR -> Screen.HomeProfessor.route
        UserRole.ADMIN -> Screen.HomeAdmin.route
    }

    NavHost(
        navController = navController,
        startDestination = startDest,
        modifier = modifier
    ) {
        // ────────────────────────────────────────────────────
        // ROTA DO CADASTRO COMPLEMENTAR DO ALUNO
        // ────────────────────────────────────────────────────
        composable("completar-perfil") {
            val completarViewModel = remember { org.smartgym.viewModel.aluno.CompletarPerfilViewModel() }
            org.smartgym.Screens.Aluno.CompletarPerfilScreen(
                navController = navController,
                viewModel = completarViewModel
            )
        }

        composable(Screen.HomeAluno.route) {
            HomeScreen(
                navController = navController,
                viewModel = alunoPerfilViewModel
            )
        }
        composable(Screen.Aparelhos.route) {
            AparelhosScreen(
                navController = navController,
                viewModel = aparelhosViewModel
            )
        }
        composable(Screen.Treino.route) { TreinoScreen(navController = navController, viewModel = treinoViewModel) }
        composable(Screen.Pagamentos.route) { PagamentosScreen(navController) }

        composable(Screen.PerfilAluno.route) {
            PerfilAlunoScreen(navController = navController, viewModel = alunoPerfilViewModel, onLogout = onLogout)
        }

        composable(Screen.HomeProfessor.route) { HomeProfessorScreen(navController) }

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

        composable(Screen.Fichas.route) {
            FichasScreenReal(
                navController = navController,
                viewModel = fichasViewModel,
                criarFichaViewModel = criarFichaViewModel
            )
        }
        composable(Screen.NovaFicha.route) {
            CriarFichaScreen(navController = navController, viewModel = criarFichaViewModel)
        }
        composable(Screen.EditarFicha.route) {
            CriarFichaScreen(navController = navController, viewModel = criarFichaViewModel)
        }
        composable(Screen.Avaliacoes.route) {
            AvaliacoesScreen(navController = navController, viewModel = avaliacoesViewModel)
        }
        composable(Screen.NovaAvaliacao.route) {
            CriarAvaliacaoScreen(navController = navController, viewModel = avaliacoesViewModel)
        }

        // ────────────────────────────────────────────────────
        // ADMIN
        // ────────────────────────────────────────────────────
        composable(Screen.HomeAdmin.route) { HomeAdminScreen(navController) }
        composable(Screen.AlunosAdmin.route) { AlunosAdminScreen(navController, viewModel = alunosViewModel) }
        composable(Screen.UnidadesAdmin.route) { UnidadesScreen() }

        composable("telaPlanos") {
            org.smartgym.Screens.Adm.PlanosScreen(viewModel = planosViewModel)
        }

        composable(Screen.MaquinasAdmin.route) {
            MaquinasAdminScreen(viewModel = maquinaViewModel)
        }

        composable(Screen.MaquinasIotAdmin.route) {
            MaquinasIotAdminScreen(viewModel = maquinaIotViewModel)
        }

        composable(Screen.NovoAluno.route) { NovoAlunoScreen(navController, viewModel = alunosViewModel) }

        composable(
            route = "${Screen.EditarAluno.route}/{alunoId}"
        ) { backStackEntry ->
            val alunoId = backStackEntry.savedStateHandle
                .get<String>("alunoId")
                ?.toIntOrNull()

            if (alunoId != null) {
                EditarAlunoScreen(
                    alunoId = alunoId,
                    navController = navController,
                    viewModel = alunosViewModel
                )
            } else {
                LaunchedEffect(Unit) { navController.popBackStack() }
            }
        }

        composable(
            route = "${Screen.EditarProfessor.route}/{professorId}"
        ) { backStackEntry ->
            val professorId = backStackEntry.savedStateHandle
                .get<String>("professorId")
                ?.toIntOrNull()

            if (professorId != null) {
                EditarProfessorScreen(
                    professorId = professorId,
                    navController = navController,
                    viewModel = professoresViewModel
                )
            } else {
                println("ERRO: ID do professor não encontrado")
                LaunchedEffect(Unit) { navController.popBackStack() }
            }
        }

        composable(Screen.ProfessoresAdmin.route) {
            ProfessoresAdminScreen(navController, viewModel = professoresViewModel)
        }

        composable(Screen.NovoProfessor.route) {
            NovoProfessorScreen(navController, viewModel = professoresViewModel)
        }
    }
}