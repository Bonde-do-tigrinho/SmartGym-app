package org.smartgym.Screens.Aluno

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.smartgym.theme.SmartGymGreen
import org.smartgym.viewModel.AgendamentosViewModel
import org.smartgym.viewModel.AulasColetivasViewModel
import org.smartgym.viewModel.aluno.AlunoPerfilViewModel // 👈 Garanta esse import

@Composable
fun AulasAlunoScreen(
    navController: NavController,
    alunoIdLogado: Int,
    viewModel: AlunoPerfilViewModel,
    aulasViewModel: AulasColetivasViewModel = viewModel { AulasColetivasViewModel() },
    agendamentosViewModel: AgendamentosViewModel = viewModel { AgendamentosViewModel() }
) {
    val colors = MaterialTheme.colorScheme
    val aulasDaSemana by aulasViewModel.aulasDaSemana.collectAsState()
    val isAgendando by agendamentosViewModel.isLoading.collectAsState()
    val aulasAgendadasIds by agendamentosViewModel.aulasAgendadasIds.collectAsState()

    var diaSelecionadoIndex by remember { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        aulasViewModel.carregarVisaoSemanal("2026-05-26")
        agendamentosViewModel.carregarAgendamentosDoAluno(alunoIdLogado)

        launch { aulasViewModel.snackbarEvent.collectLatest { snackbarHostState.showSnackbar(it) } }
        launch { agendamentosViewModel.snackbarEvent.collectLatest { snackbarHostState.showSnackbar(it) } }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = colors.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            Text("AULAS.", color = colors.onBackground, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            Text("Garanta sua vaga", color = colors.onSurfaceVariant, fontSize = 16.sp)

            Spacer(Modifier.height(24.dp))

            if (aulasDaSemana.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(aulasDaSemana.indices.toList()) { index ->
                        val dia = aulasDaSemana[index]
                        val isSelected = diaSelecionadoIndex == index
                        val diaTexto = dia.data.takeLast(2)

                        Card(
                            modifier = Modifier
                                .size(60.dp, 80.dp)
                                .clickable { diaSelecionadoIndex = index },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) SmartGymGreen else colors.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(diaTexto, fontSize = 24.sp, fontWeight = FontWeight.Black, color = if (isSelected) Color.Black else colors.onSurfaceVariant)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                val aulasDoDia = aulasDaSemana[diaSelecionadoIndex].aulas

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    if (aulasDoDia.isEmpty()) {
                        item {
                            Text("Nenhuma aula programada para este dia.", color = colors.onSurfaceVariant, modifier = Modifier.padding(top = 16.dp))
                        }
                    }

                    items(aulasDoDia) { aula ->
                        val jaEstaAgendado = aulasAgendadasIds.contains(aula.id)

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = colors.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(aula.nome, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.onBackground)
                                    Text("Início: ${aula.dataHoraInicio.takeLast(8)}", fontSize = 14.sp, color = colors.onSurfaceVariant)
                                    Text("Vagas: ${aula.capacidadeMaxima}", fontSize = 12.sp, color = SmartGymGreen)
                                }

                                Button(
                                    onClick = { agendamentosViewModel.realizarAgendamento(alunoIdLogado,
                                        aula.id!!.toInt()
                                    ) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (jaEstaAgendado) Color.DarkGray else SmartGymGreen,
                                        contentColor = if (jaEstaAgendado) Color.LightGray else Color.Black
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    enabled = !isAgendando && !jaEstaAgendado
                                ) {
                                    if (isAgendando) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black, strokeWidth = 2.dp)
                                    } else {
                                        Text(if (jaEstaAgendado) "Agendado" else "Agendar", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}