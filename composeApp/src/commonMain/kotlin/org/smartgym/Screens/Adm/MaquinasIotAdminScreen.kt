package org.smartgym.Screens.Adm

import MaquinaIotViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.SignalCellularAlt
import androidx.compose.material.icons.rounded.SignalCellularConnectedNoInternet0Bar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest
import org.smartgym.components.MaquinaIotCard
import org.smartgym.model.Adm.MaquinaIot

@Composable
fun MaquinasIotAdminScreen(
    modifier: Modifier = Modifier,
    viewModel: MaquinaIotViewModel = viewModel { MaquinaIotViewModel() }
) {
    var mostrandoFormulario by remember { mutableStateOf(false) }
    var maquinaIotParaEditar by remember { mutableStateOf<MaquinaIot?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collectLatest { mensagem ->
            snackbarHostState.showSnackbar(mensagem)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        if (mostrandoFormulario) {
            FormularioMaquinaIotScreen(
                viewModel = viewModel,
                maquinaIotInicial = maquinaIotParaEditar,
                modifier = modifier.padding(paddingValues),
                onVoltar = {
                    mostrandoFormulario = false
                    maquinaIotParaEditar = null
                }
            )
        } else {
            ListagemMaquinasIotContent(
                viewModel = viewModel,
                modifier = modifier.padding(paddingValues),
                onNovoClick = {
                    maquinaIotParaEditar = null
                    mostrandoFormulario = true
                },
                onEditClick = { maquinaIotSelecionada ->
                    maquinaIotParaEditar = maquinaIotSelecionada
                    mostrandoFormulario = true
                }
            )
        }
    }
}

@Composable
private fun ListagemMaquinasIotContent(
    viewModel: MaquinaIotViewModel,
    modifier: Modifier,
    onNovoClick: () -> Unit,
    onEditClick: (MaquinaIot) -> Unit
) {
    val listaDeMaquinasIot by viewModel.maquinasIot.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var maquinaIotIdParaApagar by remember { mutableStateOf<String?>(null) }

    // Conta exatamente pelos status da API
    val maquinasIotLivres = listaDeMaquinasIot.count { it.status?.uppercase() == "LIVRE" }
    val maquinasIotOcupadas = listaDeMaquinasIot.count { it.status?.uppercase() != "LIVRE" } // Ocupada ou Manutencao

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                Text("Máquinas IOTs", fontSize = 28.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground)
                Text("Gerencie as máquinas IOTs e o status de uso", fontSize = 14.sp, lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(
                onClick = onNovoClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar", tint = Color.Black, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Nova Máquina IOT", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                border = BorderStroke(1.dp, Color(0xFFC8E6C9)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).background(Color(0xFF4CAF50), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.SignalCellularAlt, contentDescription = null, tint = Color.White)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Livres", fontSize = 12.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                        Text(maquinasIotLivres.toString(), fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color(0xFF1B5E20))
                    }
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                border = BorderStroke(1.dp, Color(0xFFFFCDD2)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).background(Color(0xFFF44336), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.SignalCellularConnectedNoInternet0Bar, contentDescription = null, tint = Color.White)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Ocupadas/Manut.", fontSize = 12.sp, color = Color(0xFFF44336), fontWeight = FontWeight.Bold)
                        Text(maquinasIotOcupadas.toString(), fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color(0xFFB71C1C))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading && listaDeMaquinasIot.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(listaDeMaquinasIot) { maquinaIot ->
                    MaquinaIotCard(
                        maquinaIot = maquinaIot,
                        onEditClick = { onEditClick(maquinaIot) },
                        onDeleteClick = { maquinaIotIdParaApagar = maquinaIot.id },
                        onStatusToggleClick = {
                            val novoStatus = if (maquinaIot.status?.uppercase() == "LIVRE") "OCUPADA" else "LIVRE"
                            val maquinaIotAtualizada = maquinaIot.copy(status = novoStatus)
                            maquinaIot.id?.let { id -> viewModel.atualizarMaquinaIot(id, maquinaIotAtualizada) }
                        }
                    )
                }
            }
        }
    }

    if (maquinaIotIdParaApagar != null) {
        AlertDialog(
            onDismissRequest = { maquinaIotIdParaApagar = null },
            title = { Text("Excluir máquina IOT", fontWeight = FontWeight.Bold) },
            text = { Text("Tem certeza que deseja apagar esta máquina IOT?", color = MaterialTheme.colorScheme.onBackground) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletarMaquinaIot(maquinaIotIdParaApagar!!)
                        maquinaIotIdParaApagar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Sim, excluir", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { OutlinedButton(onClick = { maquinaIotIdParaApagar = null }) { Text("Cancelar") } }
        )
    }
}