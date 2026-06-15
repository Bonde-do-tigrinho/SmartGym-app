package org.smartgym.Screens.Professor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.smartgym.LocalSnackbar
import org.smartgym.Screen
import org.smartgym.model.professor.AulaColetiva
import org.smartgym.theme.SmartGymGreen
import org.smartgym.viewModel.AgendamentosViewModel
import org.smartgym.viewModel.AulasColetivasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AulasProfessorScreen(
    navController: NavController,
    aulasViewModel: AulasColetivasViewModel = viewModel { AulasColetivasViewModel() },
    agendamentosViewModel: AgendamentosViewModel = viewModel { AgendamentosViewModel() }
) {
    val colors = MaterialTheme.colorScheme
    val aulasDaSemana by aulasViewModel.aulasDaSemana.collectAsState()
    val globalSnackbar = LocalSnackbar.current

    var aulaSelecionada by remember { mutableStateOf<AulaColetiva?>(null) }
    val listaDeChamada by agendamentosViewModel.listaDeChamadaComNomes.collectAsState()
    val isCarregandoChamada by agendamentosViewModel.isLoading.collectAsState()
    var diaSelecionadoIndex by remember { mutableStateOf(0) }
    var exibirDialogoDelecao by remember { mutableStateOf(false) }

    var mostrarCalendario by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(Unit) {
        aulasViewModel.carregarVisaoSemanal() // Carrega dinamicamente na ViewModel
        launch { aulasViewModel.snackbarEvent.collectLatest { globalSnackbar.showSnackbar(it) } }
        launch { agendamentosViewModel.snackbarEvent.collectLatest { globalSnackbar.showSnackbar(it) } }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.currentBackStackEntry?.savedStateHandle?.remove<Long>("aulaIdParaEditar")
                    navController.navigate(Screen.UpsertAulaProfessor.route)
                },
                containerColor = SmartGymGreen,
                contentColor = Color.Black
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nova Aula")
            }
        },
        containerColor = colors.background
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("SUAS AULAS.", color = colors.onBackground, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Gerencie sua grade e turmas", color = colors.onSurfaceVariant, fontSize = 16.sp)
                }
                IconButton(onClick = { mostrarCalendario = true }) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = "Escolher Semana", tint = SmartGymGreen)
                }
            }
            Spacer(Modifier.height(24.dp))

            if (aulasDaSemana.isNotEmpty()) {
                LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(aulasDaSemana.indices.toList()) { index ->
                        val dia = aulasDaSemana[index]
                        val isSelected = diaSelecionadoIndex == index
                        Card(
                            modifier = Modifier.size(60.dp, 80.dp).clickable { diaSelecionadoIndex = index },
                            colors = CardDefaults.cardColors(containerColor = if (isSelected) SmartGymGreen else colors.surfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(dia.data.takeLast(2), fontSize = 24.sp, fontWeight = FontWeight.Black, color = if (isSelected) Color.Black else colors.onSurfaceVariant)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                val aulasDoDia = aulasDaSemana.getOrNull(diaSelecionadoIndex)?.aulas ?: emptyList()

                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (aulasDoDia.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(top = 64.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Outlined.EventBusy, contentDescription = null, modifier = Modifier.size(64.dp), tint = colors.onSurfaceVariant.copy(alpha = 0.5f))
                                Spacer(Modifier.height(16.dp))
                                Text("Dia livre!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colors.onBackground)
                                Text("Você não tem aulas programadas para este dia.", fontSize = 14.sp, color = colors.onSurfaceVariant, textAlign = TextAlign.Center)
                            }
                        }
                    }
                    items(aulasDoDia) { aula ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable {
                                aulaSelecionada = aula
                                agendamentosViewModel.carregarAgendamentosDaAula(aula.id!!)
                            },
                            colors = CardDefaults.cardColors(containerColor = colors.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text(aula.nome, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.onBackground)
                                    val horaLimpa = if (aula.dataHoraInicio.contains("T")) aula.dataHoraInicio.substringAfter("T").take(5) else aula.dataHoraInicio.takeLast(8)
                                    Text("Início: $horaLimpa", fontSize = 14.sp, color = colors.onSurfaceVariant)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Icon(Icons.Default.Group, contentDescription = null, tint = SmartGymGreen)
                                    Text("Ver Turma", color = SmartGymGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    mostrarCalendario = false
                    datePickerState.selectedDateMillis?.let { millis ->
                        val dataFormatada = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                            .toString()

                        aulasViewModel.carregarVisaoSemanal(dataFormatada)
                        diaSelecionadoIndex = 0
                    }
                }) { Text("Confirmar", color = SmartGymGreen) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) { Text("Cancelar", color = colors.onSurfaceVariant) }
            },
            colors = DatePickerDefaults.colors(containerColor = colors.surface)
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (aulaSelecionada != null) {
        AlertDialog(
            onDismissRequest = { aulaSelecionada = null; agendamentosViewModel.limparListaDeChamada() },
            containerColor = colors.surface,
            title = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Turma", fontWeight = FontWeight.Bold)
                    Row {
                        IconButton(onClick = {
                            navController.currentBackStackEntry?.savedStateHandle?.set("aulaIdParaEditar", aulaSelecionada!!.id)
                            aulaSelecionada = null
                            navController.navigate(Screen.UpsertAulaProfessor.route)
                        }) { Icon(Icons.Default.Edit, contentDescription = "Editar", tint = colors.primary) }
                        IconButton(onClick = { exibirDialogoDelecao = true }) { Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = colors.error) }
                    }
                }
            },
            text = {
                Column {
                    Text(aulaSelecionada?.nome ?: "", color = SmartGymGreen, fontWeight = FontWeight.Bold)
                    Text("Total agendado: ${listaDeChamada.size} / ${aulaSelecionada?.capacidadeMaxima}", color = colors.onSurfaceVariant, fontSize = 14.sp)
                    Spacer(Modifier.height(16.dp))

                    if (isCarregandoChamada) {
                        CircularProgressIndicator(color = SmartGymGreen)
                    } else if (listaDeChamada.isEmpty()) {
                        Text("Nenhum aluno agendado ainda.", color = colors.onSurfaceVariant)
                    } else {
                        LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                            items(listaDeChamada) { agendamento ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(10.dp).background(SmartGymGreen, shape = RoundedCornerShape(50)))
                                    Spacer(Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = "Aluno Matriculado",
                                            color = colors.onSurface,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = "Inscrição: #${agendamento.alunoId}",
                                            color = colors.onSurfaceVariant,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                                HorizontalDivider(color = colors.onSurfaceVariant.copy(alpha = 0.15f))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { aulaSelecionada = null; agendamentosViewModel.limparListaDeChamada() }) { Text("Fechar", color = SmartGymGreen) }
            }
        )
    }

    if (exibirDialogoDelecao) {
        AlertDialog(
            onDismissRequest = { exibirDialogoDelecao = false },
            containerColor = colors.surface,
            title = { Text("Excluir Aula", color = colors.error, fontWeight = FontWeight.Bold) },
            text = { Text("Tem certeza que deseja excluir esta aula? Os alunos matriculados perderão suas vagas.", color = colors.onSurfaceVariant) },
            confirmButton = {
                Button(
                    onClick = {
                        aulasViewModel.deletarAula(aulaSelecionada!!.id!!)
                        exibirDialogoDelecao = false
                        aulaSelecionada = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.error)
                ) { Text("Excluir", color = Color.White) }
            },
            dismissButton = { TextButton(onClick = { exibirDialogoDelecao = false }) { Text("Cancelar", color = colors.onSurfaceVariant) } }
        )
    }
}