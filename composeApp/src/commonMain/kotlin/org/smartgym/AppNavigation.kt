package org.smartgym

import org.smartgym.viewModel.Adm.MaquinaIotViewModel
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material.icons.outlined.SupervisorAccount
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Payment
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Notifications
import org.smartgym.viewModel.aluno.AlunoPerfilViewModel
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import org.smartgym.Screens.Aluno.AulasAlunoScreen
import org.smartgym.Screens.Professor.AvaliacoesScreen
import org.smartgym.Screens.Professor.CriarAvaliacaoScreen
import org.smartgym.Screens.Professor.CriarExercicioScreen
import org.smartgym.Screens.Professor.CriarFichaScreen
import org.smartgym.Screens.Professor.ExerciciosScreen
import org.smartgym.Screens.Professor.FichasScreenReal
import org.smartgym.Screens.Professor.HomeProfessorScreen
import org.smartgym.Screens.Professor.AulasProfessorScreen
import org.smartgym.Screens.Professor.UpsertAulaScreen
import org.smartgym.viewModel.aluno.AparelhosViewModel
import org.smartgym.viewModel.aluno.TreinoViewModel
import org.smartgym.theme.TextGray
import org.smartgym.viewModel.Adm.AlunosViewModel
import org.smartgym.viewModel.Professor.ExerciciosViewModel
import org.smartgym.viewModel.Professor.FichasViewModel
import org.smartgym.Screens.Adm.EditarProfessorScreen
import org.smartgym.Screens.Adm.MaquinasIotAdminScreen
import org.smartgym.Screens.Adm.NovoProfessorScreen
import org.smartgym.Screens.Adm.ProfessoresAdminScreen
import org.smartgym.viewModel.Professor.AvaliacoesViewModel
import org.smartgym.viewModel.Professor.CriarFichaViewModel
import org.smartgym.repository.ApiFichaTreinoRepository
import org.smartgym.viewModel.Adm.PlanoViewModel
import org.smartgym.viewModel.Adm.ProfessoresViewModel
import org.smartgym.Screens.Adm.NotificacoesScreen
import org.smartgym.Screens.Adm.FormularioNotificacaoScreen
import org.smartgym.Screens.Adm.PlanosScreen
import org.smartgym.Screens.Aluno.CompletarPerfilScreen
import org.smartgym.Screens.Professor.EditarAvaliacaoScreen
import org.smartgym.Screens.Professor.EditarFichaScreen
import org.smartgym.Screens.Professor.VisualizarFichaScreen
import org.smartgym.repository.ApiAlunoRepository
import org.smartgym.repository.ApiAvaliacaoRepository
import org.smartgym.repository.ApiExercicioRepository
import org.smartgym.repository.ApiProfessorRepository
import org.smartgym.viewModel.Adm.NotificacoesViewModel
import org.smartgym.viewModel.AulasColetivasViewModel
import org.smartgym.viewModel.aluno.CompletarPerfilViewModel

val LocalSnackbar = compositionLocalOf<SnackbarHostState> {
    error("Nenhum SnackbarHostState fornecido")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(userRole: UserRole, onLogout: () -> Unit, perfilCompleto: Boolean) {
    val notificacoesViewModel = remember { NotificacoesViewModel() }

    val listaNotificacoes by notificacoesViewModel.notificacoes.collectAsState()

    LaunchedEffect(Unit) {
        notificacoesViewModel.carregarNotificacoes()
    }

    val temAvisoNovo = listaNotificacoes.any { notificacoesViewModel.ehNotificacaoNova(it) }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val rotasComBottomNav = listOf(
        Screen.HomeAluno.route,
        Screen.Aparelhos.route,
        Screen.Treino.route,
        Screen.AulasAluno.route,
        Screen.Pagamentos.route,
        Screen.PerfilAluno.route
    )

    val mostrarBottomNav = currentRoute in rotasComBottomNav

    val items = listOf(
        Screen.HomeAluno,
        Screen.Aparelhos,
        Screen.Treino,
        Screen.AulasAluno,
        Screen.Pagamentos,
        Screen.PerfilAluno
    )

    val labels = mapOf(
        Screen.HomeAluno.route to "Home",
        Screen.Aparelhos.route to "Aparelhos",
        Screen.Treino.route to "Treino",
        Screen.AulasAluno.route to "Aulas",
        Screen.Pagamentos.route to "Pagamento",
        Screen.PerfilAluno.route to "Perfil"
    )

    val icons = mapOf(
        Screen.HomeAluno.route to Icons.Rounded.Home,
        Screen.Aparelhos.route to Icons.Rounded.FitnessCenter,
        Screen.Treino.route to Icons.AutoMirrored.Rounded.Assignment,
        Screen.AulasAluno.route to Icons.Rounded.DateRange,
        Screen.Pagamentos.route to Icons.Rounded.Payment,
        Screen.PerfilAluno.route to Icons.Rounded.Person
    )

    val snackbarHostState = remember { SnackbarHostState() }

    CompositionLocalProvider(LocalSnackbar provides snackbarHostState) {
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
                                                popUpTo(Screen.HomeAluno.route) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = {
                                            val iconModifier = Modifier.size(15.dp)
                                            val iconVector = icons[screen.route] ?: Icons.Default.Home
                                            val iconTint = if (selected) MaterialTheme.colorScheme.primary else TextGray

                                            if (screen.route == Screen.Notificacoes.route && temAvisoNovo) {
                                                BadgedBox(badge = { Badge(containerColor = Color(0xFFD9FF00), modifier = Modifier.size(6.dp)) }) {
                                                    Icon(
                                                        iconVector,
                                                        contentDescription = null,
                                                        tint = iconTint,
                                                        modifier = iconModifier
                                                    )
                                                }
                                            } else {
                                                Icon(
                                                    iconVector,
                                                    contentDescription = null,
                                                    tint = if (selected) MaterialTheme.colorScheme.primary else TextGray
                                                )
                                            }
                                        },
                                        label = {
                                            Text(
                                                labels[screen.route] ?: "",
                                                fontSize = 10.sp,
                                                color = if (selected) MaterialTheme.colorScheme.primary else TextGray
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
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
                        modifier = Modifier.padding(padding),
                        perfilCompleto = perfilCompleto,
                        snackbarHostState = snackbarHostState,
                        notificacoesViewModel = notificacoesViewModel,
                        temNotificacaoNova = temAvisoNovo
                    )
                }
            }

            UserRole.PROFESSOR -> {
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val professorItems = listOf(
                    Screen.HomeProfessor,
                    Screen.Notificacoes,
                    Screen.Exercicios,
                    Screen.Fichas,
                    Screen.Avaliacoes,
                    Screen.AulasProfessor
                )
                val professorRotasSemHeader = setOf(
                    Screen.NovoExercicio.route,
                    Screen.NovaAvaliacao.route,
                    Screen.NovaFicha.route,
                    Screen.EditarFicha.route,
                    Screen.UpsertAulaProfessor.route
                )
                val mostrarHeaderProfessor = currentRoute !in professorRotasSemHeader
                val professorLabels = mapOf(
                    Screen.HomeProfessor.route to "Dashboard",
                    Screen.Notificacoes.route to "Notificações",
                    Screen.Exercicios.route to "Exercícios",
                    Screen.Fichas.route to "Fichas",
                    Screen.Avaliacoes.route to "Avaliações",
                    Screen.AulasProfessor.route to "Aulas"
                )
                val professorIcons = mapOf(
                    Screen.HomeProfessor.route to Icons.Outlined.Home,
                    Screen.Notificacoes.route to Icons.Outlined.Notifications,
                    Screen.Exercicios.route to Icons.Rounded.FitnessCenter,
                    Screen.Fichas.route to Icons.AutoMirrored.Rounded.Assignment,
                    Screen.Avaliacoes.route to Icons.Outlined.People,
                    Screen.AulasProfessor.route to Icons.Outlined.Event
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
                                Text(
                                    "GYM",
                                    modifier = Modifier.padding(1.dp),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                                Text(
                                    ".",
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                "Área do Instrutor",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
                            Spacer(Modifier.height(8.dp))

                            professorItems.forEach { screen ->
                                val selected = currentRoute == screen.route
                                NavigationDrawerItem(
                                    shape = RoundedCornerShape(15.dp),
                                    label = {
                                        Text(
                                            professorLabels[screen.route] ?: "",
                                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                                        )
                                    },
                                    icon = {
                                        if (screen.route == Screen.Notificacoes.route && temAvisoNovo) {
                                            BadgedBox(badge = { Badge(containerColor = Color(0xFFD9FF00)) }) {
                                                Icon(
                                                    professorIcons[screen.route] ?: Icons.Default.Home,
                                                    contentDescription = null
                                                )
                                            }
                                        } else {
                                            Icon(
                                                professorIcons[screen.route] ?: Icons.Default.Home,
                                                contentDescription = null
                                            )
                                        }
                                    },
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
                                icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
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
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            "GYM",
                                            modifier = Modifier.padding(1.dp),
                                            style = MaterialTheme.typography.titleLarge,
                                            color = MaterialTheme.colorScheme.onSecondary
                                        )
                                        Text(
                                            ".",
                                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                },
                                navigationIcon = {
                                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                        Icon(
                                            Icons.Default.Menu,
                                            contentDescription = "Menu"
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.shadow(elevation = 5.dp)
                            )
                        }
                    ) { padding ->
                        NavContent(
                            navController = navController,
                            userRole = userRole,
                            onLogout = onLogout,
                            modifier = Modifier.padding(padding),
                            perfilCompleto = true,
                            snackbarHostState = snackbarHostState,
                            notificacoesViewModel = notificacoesViewModel,
                            temNotificacaoNova = temAvisoNovo
                        )
                    }
                }
            }

            UserRole.ADMIN -> {
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                val adminItems = listOf(
                    Screen.HomeAdmin.route,
                    Screen.Notificacoes.route,
                    Screen.AlunosAdmin.route,
                    Screen.ProfessoresAdmin.route,
                    Screen.UnidadesAdmin.route,
                    "telaPlanos",
                    Screen.MaquinasIotAdmin.route,
                )

                val adminLabels = mapOf(
                    Screen.HomeAdmin.route to "Dashboard",
                    Screen.Notificacoes.route to "Notificações",
                    Screen.AlunosAdmin.route to "Alunos",
                    Screen.UnidadesAdmin.route to "Unidades",
                    "telaPlanos" to "Planos",
                    Screen.MaquinasIotAdmin.route to "Máquinas",
                    Screen.ProfessoresAdmin.route to "Professores",
                )

                val adminIcons = mapOf(
                    Screen.HomeAdmin.route to Icons.Outlined.Home,
                    Screen.Notificacoes.route to Icons.Outlined.Notifications,
                    Screen.AlunosAdmin.route to Icons.Outlined.People,
                    Screen.UnidadesAdmin.route to Icons.Outlined.Apartment,
                    "telaPlanos" to Icons.AutoMirrored.Rounded.Assignment,
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
                                Text(
                                    "GYM",
                                    modifier = Modifier.padding(1.dp),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                                Text(
                                    ".",
                                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                "Área do Gerente",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
                            Spacer(Modifier.height(8.dp))

                            adminItems.forEach { rota ->
                                val selected = currentRoute == rota
                                NavigationDrawerItem(
                                    shape = RoundedCornerShape(15.dp),
                                    label = {
                                        Text(
                                            adminLabels[rota] ?: "",
                                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                                        )
                                    },
                                    icon = {
                                        if (rota == Screen.Notificacoes.route && temAvisoNovo) {
                                            BadgedBox(badge = { Badge(containerColor = Color(0xFFD9FF00)) }) {
                                                Icon(adminIcons[rota] ?: Icons.Default.Home, contentDescription = null)
                                            }
                                        } else {
                                            Icon(adminIcons[rota] ?: Icons.Default.Home, contentDescription = null)
                                        }
                                    },
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
                                    Row(
                                        modifier = Modifier.padding(horizontal = 0.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "GYM",
                                            modifier = Modifier.padding(1.dp),
                                            style = MaterialTheme.typography.titleLarge,
                                            color = MaterialTheme.colorScheme.onSecondary
                                        )
                                        Text(
                                            ".",
                                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                },
                                navigationIcon = {
                                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                        Icon(
                                            Icons.Default.Menu,
                                            contentDescription = "Menu"
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.shadow(elevation = 5.dp)
                            )
                        }
                    ) { padding ->
                        NavContent(
                            navController = navController,
                            userRole = userRole,
                            onLogout = onLogout,
                            perfilCompleto = true,
                            modifier = Modifier.padding(padding),
                            snackbarHostState = snackbarHostState,
                            notificacoesViewModel = notificacoesViewModel,
                            temNotificacaoNova = temAvisoNovo
                        )
                    }
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
    modifier: Modifier = Modifier,
    perfilCompleto: Boolean,
    snackbarHostState: SnackbarHostState,
    notificacoesViewModel: NotificacoesViewModel,
    temNotificacaoNova: Boolean
) {

    val fichaTreinoRepository = remember { ApiFichaTreinoRepository() }
    val apiExercicioRepository = remember { ApiExercicioRepository() }
    val planosViewModel = remember { PlanoViewModel() }
    val treinoViewModel = remember { TreinoViewModel(fichaTreinoRepository, apiExercicioRepository) }
    val aparelhosViewModel = remember { AparelhosViewModel() }
    val alunosViewModel = remember { AlunosViewModel() }
    val exerciciosViewModel = remember { ExerciciosViewModel() }
    val maquinaIotViewModel = remember { MaquinaIotViewModel() }
    val avaliacaoRepository = remember { ApiAvaliacaoRepository() }
    val alunoRepository = remember { ApiAlunoRepository() }
    val exercicioRepository = remember { ApiExercicioRepository() }
    val fichaRepository = remember { ApiFichaTreinoRepository() }
    val avaliacoesViewModel = remember {AvaliacoesViewModel(avaliacaoRepository, alunoRepository)}
    val fichasViewModel = remember { FichasViewModel(fichaRepository, alunoRepository) }
    val criarFichaViewModel = remember { CriarFichaViewModel(alunoRepository, exercicioRepository, fichaRepository) }
    val professorRepository = remember { ApiProfessorRepository() }
    val professoresViewModel = remember { ProfessoresViewModel(professorRepository) }
    val alunoPerfilViewModel = remember { AlunoPerfilViewModel() }
    var notificacaoEmEdicaoId by remember { mutableStateOf<Int?>(null) }
    val perfilUsuario by alunoPerfilViewModel.perfil.collectAsState()
    val alunoIdReal = perfilUsuario?.id ?: 0

    LaunchedEffect(Unit) {
        alunosViewModel.snackbarEvent.collectLatest { message ->
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
        composable("completar-perfil") {
            val completarViewModel = remember { CompletarPerfilViewModel() }
            CompletarPerfilScreen(
                navController = navController,
                viewModel = completarViewModel
            )
        }

        composable(Screen.HomeAluno.route) {
            HomeScreen(
                navController = navController,
                treinoViewModel = treinoViewModel,
                viewModel = alunoPerfilViewModel,
                temNotificacaoNova = temNotificacaoNova
            )
        }
        composable(Screen.Aparelhos.route) {
            AparelhosScreen(
                navController = navController,
                viewModel = aparelhosViewModel
            )
        }

        composable(Screen.Treino.route) {
            TreinoScreen(
                navController = navController,
                viewModel = treinoViewModel
            )
        }
        composable(Screen.Pagamentos.route) { PagamentosScreen(navController) }
        composable(Screen.PerfilAluno.route) {
            PerfilAlunoScreen(
                navController = navController,
                onLogout = onLogout,
                viewModel = alunoPerfilViewModel
            )
        }

        composable(Screen.AulasAluno.route) {
            AulasAlunoScreen(
                navController = navController,
                viewModel = alunoPerfilViewModel,
                alunoIdLogado = alunoIdReal
            )
        }

        composable(Screen.HomeProfessor.route) {
            HomeProfessorScreen(
                navController = navController,
                alunosViewModel = alunosViewModel,
                fichasViewModel = fichasViewModel,
                avaliacoesViewModel = avaliacoesViewModel,
                exerciciosViewModel = exerciciosViewModel
            )
        }
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
            CriarFichaScreen(
                navController = navController,
                viewModel = criarFichaViewModel
            )
        }
        composable(
            route = "${Screen.EditarFicha.route}/{fichaId}"
        ) { backStackEntry ->
            val fichaId = backStackEntry.savedStateHandle
                .get<String>("fichaId")
                ?.toIntOrNull()

            if (fichaId != null) {
                EditarFichaScreen(
                    navController = navController,
                    viewModel = criarFichaViewModel, // Seu ViewModel compartilhado de fichas
                    fichaId = fichaId
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }

        composable(
            route = "${Screen.VisualizarFicha.route}/{fichaId}"
        ) { backStackEntry ->
            val fichaId = backStackEntry.savedStateHandle
                .get<String>("fichaId")
                ?.toIntOrNull()

            if (fichaId != null) {
                VisualizarFichaScreen(
                    navController = navController,
                    viewModel = criarFichaViewModel,
                    fichaId = fichaId
                )
            } else {
                LaunchedEffect(Unit) { navController.popBackStack() }
            }
        }

        composable(Screen.Avaliacoes.route) {
            AvaliacoesScreen(
                navController = navController,
                viewModel = avaliacoesViewModel
            )
        }
        composable(Screen.NovaAvaliacao.route) {
            CriarAvaliacaoScreen(
                navController = navController,
                viewModel = avaliacoesViewModel
            )
        }
        composable(
            route = "${Screen.EditarAvaliacao.route}/{avaliacaoId}"
        ) { backStackEntry ->
            val avaliacaoId = backStackEntry.savedStateHandle
                .get<String>("avaliacaoId")
                ?.toIntOrNull()

            if (avaliacaoId != null) {
                EditarAvaliacaoScreen(
                    navController = navController,
                    viewModel = avaliacoesViewModel,
                    avaliacaoId = avaliacaoId
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }

        composable(Screen.AulasProfessor.route) { AulasProfessorScreen(navController = navController) }
        composable(Screen.UpsertAulaProfessor.route) { UpsertAulaScreen(
            navController = navController,
            perfilViewModel = AlunoPerfilViewModel(),
            viewModel = AulasColetivasViewModel()
        ) }
        composable(Screen.HomeAdmin.route) { HomeAdminScreen(navController) }
        composable(Screen.AlunosAdmin.route) { AlunosAdminScreen(navController, viewModel = alunosViewModel) }
        composable(Screen.UnidadesAdmin.route) { UnidadesScreen() }
        composable("telaPlanos") { PlanosScreen(viewModel = planosViewModel) }
        composable(Screen.MaquinasIotAdmin.route) { MaquinasIotAdminScreen(viewModel = maquinaIotViewModel) }
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
            ProfessoresAdminScreen(
                navController,
                viewModel = professoresViewModel
            )
        }
        composable(Screen.NovoProfessor.route) {
            NovoProfessorScreen(
                navController,
                viewModel = professoresViewModel
            )
        }

        composable(Screen.Notificacoes.route) {
            NotificacoesScreen(
                navController = navController,
                viewModel = notificacoesViewModel,
                isAdmin = userRole == UserRole.ADMIN,
                onNavigateToCriar = {
                    notificacaoEmEdicaoId = null
                    navController.navigate(Screen.NovaNotificacao.route)
                },
                onNavigateToEditar = { id ->
                    notificacaoEmEdicaoId = id
                    navController.navigate(Screen.EditarNotificacao.createRoute(id))
                }
            )
        }

        composable(Screen.NovaNotificacao.route) {
            FormularioNotificacaoScreen(
                notificacaoId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.EditarNotificacao.route) {
            if (notificacaoEmEdicaoId != null) {
                FormularioNotificacaoScreen(
                    notificacaoId = notificacaoEmEdicaoId,
                    onNavigateBack = { navController.popBackStack() }
                )
            } else {
                LaunchedEffect(Unit) { navController.popBackStack() }
            }
        }
    }
}