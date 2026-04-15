package org.smartgym.Screens.Adm

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import org.smartgym.Screen
import org.smartgym.model.Adm.Aluno
import org.smartgym.viewModel.Adm.AlunosViewModel


@Composable
fun AlunosAdminScreen(navController: NavController, modifier: Modifier = Modifier, viewModel: AlunosViewModel = viewModel()) {
    val alunos by viewModel.alunosFiltrados.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val horizontalScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Alunos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Gerencie os alunos da academia",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate(Screen.NovoAluno.route) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9FF00)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
            Spacer(Modifier.width(8.dp))
            Text("Novo Aluno", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = searchQuery,
            onValueChange = {viewModel.onSearchQueryChange(it)},
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar alunos...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder()
        ) {
                if (alunos.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Nenhum aluno cadastrado.", color = Color.Gray)
                    }
                }else {
                    Column(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Text("Nome", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.width(150.dp))
                            Text("Contato", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.width(180.dp))
                            Text("CPF", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.width(120.dp))
                            Text("Plano", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.width(80.dp))
                            Text("Status", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.width(80.dp))
                            Text("Ações", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, modifier = Modifier.width(90.dp))
                        }
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)


                    LazyColumn(modifier = Modifier.heightIn(max = 500.dp)) {
                        items(alunos) { aluno ->
                            AlunoRow(
                                aluno = aluno,
                                onEditClick = {navController.navigate(Screen.EditarAluno.route + "/${aluno.id}")},
                                onDeleteClick = {viewModel.deletarAluno(aluno.id)})
                            Divider(
                                color = MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlunoRow(
    aluno: Aluno,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Deletar Aluno") },
            text = { Text("Tem certeza que deseja deletar ${aluno.nome}? Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteClick()
                    showDeleteDialog = false
                }) {
                    Text("Deletar", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Row(
        modifier = Modifier.width(800.dp).padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Nome
        Text(aluno.nome, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, modifier = Modifier.width(150.dp))

        // Contato
        Column(modifier = Modifier.width(180.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Email, null, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(4.dp))
                Text(aluno.email, fontSize = 11.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, null, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(4.dp))
                Text(aluno.telefone, fontSize = 11.sp)
            }
        }

        // CPF
        Text(aluno.cpf, fontSize = 12.sp, modifier = Modifier.width(120.dp))

        // Plano
        Box(
            modifier = Modifier
                .width(80.dp)
                .clip(CircleShape)
                .background(Color(0xFFD9FF00).copy(alpha = 0.3f))
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(aluno.plano, fontSize = 11.sp, color = Color(0xFFD9FF00), fontWeight = FontWeight.ExtraBold)
        }

        // Status
        val statusTexto = if (aluno.status) "Ativo" else "Inativo"
        val statusCor = if (aluno.status) Color.Green else Color.Red
        Box(
            modifier = Modifier
                .width(80.dp)
                .clip(CircleShape)
                .background(statusCor.copy(alpha = 0.3f))
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(statusTexto, fontSize = 11.sp, color = statusCor, fontWeight = FontWeight.ExtraBold)
        }

        // Botões de ação
        Row(modifier = Modifier.width(90.dp)) {
            IconButton(onClick = onEditClick, modifier = Modifier.size(36.dp)) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Deletar",
                    tint = Color.Red,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}