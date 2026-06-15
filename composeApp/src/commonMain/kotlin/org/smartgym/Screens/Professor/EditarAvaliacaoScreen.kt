package org.smartgym.Screens.Professor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import org.smartgym.theme.SmartGymGreen
import org.smartgym.viewModel.Professor.AvaliacoesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarAvaliacaoScreen(
    navController: NavController,
    viewModel: AvaliacoesViewModel,
    avaliacaoId: Int
) {
    val nomeAluno by viewModel.nomeAluno.collectAsState()
    val dataAvaliacao by viewModel.dataAvaliacao.collectAsState()
    val peso by viewModel.peso.collectAsState()
    val percentualGordura by viewModel.percentualGordura.collectAsState()
    val imc by viewModel.imc.collectAsState()
    val nota by viewModel.nota.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(avaliacaoId) {
        viewModel.loadById(avaliacaoId)

        viewModel.navigationEvent.collectLatest {
            viewModel.loadAll()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Avaliação", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator(color = SmartGymGreen)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = nomeAluno,
                    onValueChange = {},
                    label = { Text("Aluno") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                )

                OutlinedTextField(
                    value = dataAvaliacao,
                    onValueChange = { viewModel.updateDataAvaliacao(it) },
                    label = { Text("Data da Avaliação (DD/MM/AAAA)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = peso,
                    onValueChange = { viewModel.updatePeso(it) },
                    label = { Text("Peso (kg)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = percentualGordura,
                    onValueChange = { viewModel.updatePercentualGordura(it) },
                    label = { Text("% Gordura") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = imc,
                    onValueChange = { viewModel.updateImc(it) },
                    label = { Text("IMC") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = nota,
                    onValueChange = { viewModel.updateNota(it) },
                    label = { Text("Notas/Observações do Professor") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.save() }, // Executa o save que fará o UPDATE
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SmartGymGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Salvar Alterações", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}