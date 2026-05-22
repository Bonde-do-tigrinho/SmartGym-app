package org.smartgym.Screens.Adm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.smartgym.model.Adm.Plano
import org.smartgym.viewModel.Adm.PlanoViewModel

@Composable
fun PlanosScreen(viewModel: PlanoViewModel) {
    var mostrandoFormulario by remember { mutableStateOf(false) }
    var planoParaEditar by remember { mutableStateOf<Plano?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (mostrandoFormulario) {
            FormularioPlanoScreen(
                viewModel = viewModel,
                planoInicial = planoParaEditar,
                snackbarHostState = snackbarHostState,
                modifier = Modifier.padding(paddingValues),
                onVoltar = {
                    mostrandoFormulario = false
                    planoParaEditar = null
                }
            )
        } else {
            ListagemPlanosContent(
                viewModel = viewModel,
                modifier = Modifier.padding(paddingValues),
                onNovoClick = {
                    planoParaEditar = null
                    mostrandoFormulario = true
                },
                onEditClick = { plano ->
                    planoParaEditar = plano
                    mostrandoFormulario = true
                }
            )
        }
    }
}

@Composable
private fun ListagemPlanosContent(
    viewModel: PlanoViewModel,
    modifier: Modifier,
    onNovoClick: () -> Unit,
    onEditClick: (Plano) -> Unit
) {
    val planos by viewModel.planos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var planoParaDeletar by remember { mutableStateOf<Plano?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Planos", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Button(
                onClick = onNovoClick,
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Novo Plano")
                Spacer(Modifier.width(4.dp))
                Text("Novo Plano")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading && planos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(planos) { plano ->
                    PlanoCard(
                        plano = plano,
                        onEditClick = { onEditClick(plano) },
                        onDeleteClick = { planoParaDeletar = plano }
                    )
                }
            }
        }
    }

    if (planoParaDeletar != null) {
        AlertDialog(
            onDismissRequest = { planoParaDeletar = null },
            title = { Text("Excluir Plano") },
            text = { Text("Tem certeza que deseja excluir o plano '${planoParaDeletar?.nome}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        planoParaDeletar?.id?.let { viewModel.deletarPlano(it) }
                        planoParaDeletar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { planoParaDeletar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun PlanoCard(
    plano: Plano,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val corStatus = if (plano.ativo) Color(0xFF4CAF50) else Color.Red
    val textoStatus = if (plano.ativo) "Ativo" else "Pausado"

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Linha de Status e Botões
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.size(10.dp).background(corStatus, CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = textoStatus, color = corStatus, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = onEditClick, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Deletar", tint = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Nome e Valor
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = plano.nome,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "R$ ${formatarMoeda(plano.valor)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(text = "/mês", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 2.dp))
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Duração do Plano
            val textoDuracao = when(plano.duracaoMeses) {
                1 -> "Plano Mensal"
                3 -> "Plano Trimestral"
                6 -> "Plano Semestral"
                12 -> "Plano Anual"
                else -> "${plano.duracaoMeses} Meses"
            }
            Text(
                text = textoDuracao,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            // Descrição
            Text(
                text = plano.descricao,
                fontSize = 14.sp,
                color = Color.DarkGray,
                maxLines = 2
            )
        }
    }
}
fun formatarMoeda(valor: Double): String {
    val partes = valor.toString().split(".")
    val inteira = partes[0]
    val decimal = if (partes.size > 1) partes[1].padEnd(2, '0').take(2) else "00"
    return "$inteira,$decimal"
}