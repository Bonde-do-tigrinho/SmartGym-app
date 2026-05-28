package org.smartgym.Screens.Professor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.smartgym.LocalSnackbar
import org.smartgym.model.professor.AulaColetiva
import org.smartgym.theme.SmartGymGreen
import org.smartgym.viewModel.AulasColetivasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpsertAulaScreen(
    navController: NavController,
    viewModel: AulasColetivasViewModel = viewModel { AulasColetivasViewModel() }
) {
    val colors = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()
    val isLoading by viewModel.isLoading.collectAsState()
    val globalSnackbar = LocalSnackbar.current // Puxa a tomada do Snackbar

    val aulaIdParaEditar = navController.previousBackStackEntry?.savedStateHandle?.get<Long>("aulaIdParaEditar")
    val isEdicao = aulaIdParaEditar != null

    var nome by remember { mutableStateOf("") }
    var capacidadeMaxima by remember { mutableStateOf("") }
    var dataAula by remember { mutableStateOf("") }
    var horaInicio by remember { mutableStateOf("") }
    var horaFim by remember { mutableStateOf("") }

    // Escuta os eventos da ViewModel e mostra na tela!
    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collectLatest { message ->
            globalSnackbar.showSnackbar(message)
        }
    }

    LaunchedEffect(aulaIdParaEditar) {
        if (isEdicao) {
            viewModel.buscarAulaPorId(aulaIdParaEditar!!) { aula ->
                nome = aula.nome
                capacidadeMaxima = aula.capacidadeMaxima.toString()
                val partesInicio = aula.dataHoraInicio.split("T")
                if (partesInicio.size == 2) {
                    val partesData = partesInicio[0].split("-")
                    if (partesData.size == 3) dataAula = "${partesData[2]}/${partesData[1]}/${partesData[0]}"
                    horaInicio = partesInicio[1].take(5)
                }
                val partesFim = aula.dataHoraFim.split("T")
                if (partesFim.size == 2) horaFim = partesFim[1].take(5)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEdicao) "Editar Aula" else "Nova Aula", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background)
            )
        },
        containerColor = colors.background
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp).verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(16.dp))
            Text("Detalhes da Aula", color = colors.onBackground, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Preencha as informações para a grade de horários.", color = colors.onSurfaceVariant, fontSize = 14.sp)
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome da Aula") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), enabled = !isLoading)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = capacidadeMaxima, onValueChange = { capacidadeMaxima = it }, label = { Text("Capacidade Máxima (Vagas)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), enabled = !isLoading)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = dataAula, onValueChange = { dataAula = it }, label = { Text("Data da Aula (DD/MM/AAAA)") }, placeholder = { Text("Ex: 28/05/2026") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), enabled = !isLoading)
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = horaInicio, onValueChange = { horaInicio = it }, label = { Text("Início (HH:MM)") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), enabled = !isLoading, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = horaFim, onValueChange = { horaFim = it }, label = { Text("Fim (HH:MM)") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), enabled = !isLoading, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    val partesDaData = dataAula.split("/")
                    if (nome.isBlank() || partesDaData.size != 3) {
                        scope.launch { globalSnackbar.showSnackbar("Preencha todos os dados corretamente.") }
                        return@Button
                    }
                    val dataFormatadaIso = "${partesDaData[2]}-${partesDaData[1]}-${partesDaData[0]}"
                    val novaAula = AulaColetiva(
                        id = if (isEdicao) aulaIdParaEditar else null,
                        nome = nome,
                        capacidadeMaxima = capacidadeMaxima.toIntOrNull() ?: 0,
                        dataHoraInicio = "${dataFormatadaIso}T${horaInicio}:00",
                        dataHoraFim = "${dataFormatadaIso}T${horaFim}:00",
                        professorId = 3L
                    )

                    val onSuccessCallback: () -> Unit = {
                        navController.currentBackStackEntry?.savedStateHandle?.remove<Long>("aulaIdParaEditar")
                        navController.popBackStack()
                    }

                    if (isEdicao) {
                        viewModel.atualizarAula(aulaIdParaEditar!!, novaAula, onSuccessCallback)
                    } else {
                        viewModel.criarAula(novaAula, onSuccessCallback)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = SmartGymGreen, contentColor = Color.Black), enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                else Text(if (isEdicao) "Atualizar Aula" else "Salvar Aula", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}