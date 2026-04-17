package org.smartgym.Screens.Adm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.smartgym.model.Adm.Unidade
import org.smartgym.viewModel.Adm.UnidadesViewModel

@Composable
fun UnidadesScreen(
    modifier: Modifier = Modifier,
    viewModel: UnidadesViewModel = viewModel { UnidadesViewModel() }
) {
    val mostrandoFormulario by viewModel.mostrandoFormulario.collectAsState()

    if (mostrandoFormulario) {
        FormularioUnidade(viewModel, modifier)
    } else {
        ListagemUnidades(viewModel, modifier)
    }
}

// ----------------------------------------------------
// TELA DE LISTAGEM
// ----------------------------------------------------
@Composable
fun ListagemUnidades(viewModel: UnidadesViewModel, modifier: Modifier) {
    val listaDeUnidades by viewModel.listaUnidades.collectAsState()

    var unidadeIdParaApagar by remember { mutableStateOf<String?>(null) }

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
                Text(
                    text = "Unidades",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Gerencia as unidades da academia",
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = {
                    viewModel.limparCampos()
                    viewModel.mostrandoFormulario.value = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar", tint = Color.Black, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Nova Unidade", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(listaDeUnidades) { unidade ->
                UnidadeCard(
                    unidade = unidade,
                    onEditClick = { viewModel.prepararEdicao(unidade) },
                    onDeleteClick = { unidadeIdParaApagar = unidade.id }
                )
            }
        }
    }

    if (unidadeIdParaApagar != null) {
        AlertDialog(
            onDismissRequest = { unidadeIdParaApagar = null },
            title = {
                Text(text = "Excluir unidade", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Tem certeza que deseja apagar esta unidade? Esta ação não poderá ser desfeita e afetará alunos e professores vinculados." , color = MaterialTheme.colorScheme.onBackground)
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.apagar(unidadeIdParaApagar!!)
                        unidadeIdParaApagar = null
                    },
                    // Alterado para a cor primária (Verde)
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Sim, excluir", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { unidadeIdParaApagar = null }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// ----------------------------------------------------
// TELA DE FORMULÁRIO
// ----------------------------------------------------
@Composable
fun FormularioUnidade(viewModel: UnidadesViewModel, modifier: Modifier) {
    val nome by viewModel.nomeAtual.collectAsState()
    val endereco by viewModel.enderecoAtual.collectAsState()
    val cidade by viewModel.cidadeAtual.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Cadastrar nova Unidade", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)

        OutlinedTextField(
            value = nome,
            onValueChange = { viewModel.nomeAtual.value = it },
            label = { Text("Nome da Unidade") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = endereco,
            onValueChange = { viewModel.enderecoAtual.value = it },
            label = { Text("Endereço") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = cidade,
            onValueChange = { viewModel.cidadeAtual.value = it },
            label = { Text("Cidade") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.gravar() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Salvar")
            }

            OutlinedButton(
                onClick = { viewModel.limparCampos() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Limpar")
            }
        }

        TextButton(onClick = { viewModel.mostrandoFormulario.value = false }) {
            Text("Cancelar")
        }
    }
}

// ----------------------------------------------------
// DESIGN DO CARD
// ----------------------------------------------------
@Composable
fun UnidadeCard(unidade: Unidade, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                val iconGradient = Brush.linearGradient(
                    colors = listOf(Color(0xFFAD46FF), Color(0xFF9810FA))
                )

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(brush = iconGradient, shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White)
                }

                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = unidade.nome, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = unidade.endereco, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
            Text(text = unidade.cidade, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Column {
                    Text(text = "Alunos", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    Text(text = unidade.alunos.toString(), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
                }
                Column {
                    Text(text = "Instrutores", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    Text(text = unidade.instrutores.toString(), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}