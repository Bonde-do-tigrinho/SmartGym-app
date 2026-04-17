package org.smartgym.Screens.Adm

import MaquinaViewModel
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
import org.smartgym.components.MaquinaCard
import org.smartgym.model.Adm.Maquina

@Composable
fun MaquinasAdminScreen(
    modifier: Modifier = Modifier,
    viewModel: MaquinaViewModel = viewModel { MaquinaViewModel() }
) {
    var mostrandoFormulario by remember { mutableStateOf(false) }
    var maquinaParaEditar by remember { mutableStateOf<Maquina?>(null) }
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
            FormularioMaquinaScreen(
                viewModel = viewModel,
                maquinaInicial = maquinaParaEditar,
                modifier = modifier.padding(paddingValues),
                onVoltar = {
                    mostrandoFormulario = false
                    maquinaParaEditar = null
                }
            )
        } else {
            ListagemMaquinasContent(
                viewModel = viewModel,
                modifier = modifier.padding(paddingValues),
                onNovoClick = {
                    maquinaParaEditar = null
                    mostrandoFormulario = true
                },
                onEditClick = { maquinaSelecionada ->
                    maquinaParaEditar = maquinaSelecionada
                    mostrandoFormulario = true
                }
            )
        }
    }
}

@Composable
private fun ListagemMaquinasContent(
    viewModel: MaquinaViewModel,
    modifier: Modifier,
    onNovoClick: () -> Unit,
    onEditClick: (Maquina) -> Unit
) {
    val listaDeMaquinas by viewModel.maquinas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var maquinaIdParaApagar by remember { mutableStateOf<Long?>(null) }

    // Conta exatamente pelos status da API
    val maquinasLivres = listaDeMaquinas.count { it.status?.uppercase() == "LIVRE" }
    val maquinasOcupadas = listaDeMaquinas.count { it.status?.uppercase() != "LIVRE" } // Ocupada ou Manutencao

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
                Text("Máquinas", fontSize = 28.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground)
                Text("Gerencie as máquinas e o status de uso", fontSize = 14.sp, lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                Text("Nova Máquina", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                        Text(maquinasLivres.toString(), fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color(0xFF1B5E20))
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
                        Text(maquinasOcupadas.toString(), fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color(0xFFB71C1C))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading && listaDeMaquinas.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(listaDeMaquinas) { maquina ->
                    MaquinaCard(
                        maquina = maquina,
                        onEditClick = { onEditClick(maquina) },
                        onDeleteClick = { maquinaIdParaApagar = maquina.id?.toLong() ?: 0L },
                        onStatusToggleClick = {
                            val novoStatus = if (maquina.status?.uppercase() == "LIVRE") "OCUPADA" else "LIVRE"
                            val maquinaAtualizada = maquina.copy(status = novoStatus)
                            maquina.id?.let { id -> viewModel.atualizarMaquina(id.toLong(), maquinaAtualizada) }
                        }
                    )
                }
            }
        }
    }

    if (maquinaIdParaApagar != null) {
        AlertDialog(
            onDismissRequest = { maquinaIdParaApagar = null },
            title = { Text("Excluir máquina", fontWeight = FontWeight.Bold) },
            text = { Text("Tem certeza que deseja apagar esta máquina?", color = MaterialTheme.colorScheme.onBackground) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletarMaquina(maquinaIdParaApagar!!)
                        maquinaIdParaApagar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Sim, excluir", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { OutlinedButton(onClick = { maquinaIdParaApagar = null }) { Text("Cancelar") } }
        )
    }
}