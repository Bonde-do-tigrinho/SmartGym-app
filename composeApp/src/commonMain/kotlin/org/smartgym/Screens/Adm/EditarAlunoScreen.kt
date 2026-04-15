package org.smartgym.Screens.Adm

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import org.smartgym.viewModel.Adm.AlunosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarAlunoScreen(
    navController: NavController,
    alunoId: Int,
    modifier: Modifier = Modifier,
    viewModel: AlunosViewModel = viewModel()
) {
    val alunos by viewModel.alunos.collectAsState()
    val aluno = alunos.find { it.id == alunoId }

    // Se o aluno não for encontrado, volta para a tela anterior
    if (aluno == null) {
        LaunchedEffect(Unit) { navController.popBackStack() }
        return
    }

    val scrollState = rememberScrollState()

    // Estados dos campos preenchidos com os dados do aluno
    var nome by remember { mutableStateOf(aluno.nome) }
    var email by remember { mutableStateOf(aluno.email) }
    var telefone by remember { mutableStateOf(aluno.telefone) }
    var cpf by remember { mutableStateOf(aluno.cpf) }
    var planoSelecionado by remember { mutableStateOf(aluno.plano) }
    var ativo by remember { mutableStateOf(aluno.status) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val planos = listOf("Basic", "Premium", "Black")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Aluno", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionTitle("Dados Pessoais")

            CampoTexto(
                label = "Nome completo",
                value = nome,
                onValueChange = { nome = it }
            )
            CampoTexto(
                label = "CPF",
                value = cpf,
                onValueChange = { novoValor ->
                    val apenasDigitos = novoValor.filter { it.isDigit() }.take(11)
                    cpf = mascaraCpf(apenasDigitos)
                },
                keyboardType = KeyboardType.Number
            )

            SectionTitle("Contato")

            CampoTexto(
                label = "E-mail",
                value = email,
                onValueChange = { email = it },
                keyboardType = KeyboardType.Email
            )
            CampoTexto(
                label = "Telefone",
                value = telefone,
                onValueChange = { novoValor ->
                    val apenasDigitos = novoValor.filter { it.isDigit() }.take(11)
                    telefone = mascaraTelefone(apenasDigitos)
                },
                keyboardType = KeyboardType.Phone
            )

            SectionTitle("Plano")

            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = planoSelecionado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Selecione o plano") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    planos.forEach { plano ->
                        DropdownMenuItem(
                            text = { Text(plano) },
                            onClick = {
                                planoSelecionado = plano
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            SectionTitle("Status")

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Aluno Ativo", fontWeight = FontWeight.SemiBold)
                        Text(
                            if (ativo) "Aluno terá acesso à academia" else "Aluno sem acesso à academia",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = ativo,
                        onCheckedChange = { ativo = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Botão Salvar
            Button(
                onClick = {
                    viewModel.editarAluno(
                        aluno.copy(
                            nome = nome,
                            email = email,
                            telefone = telefone,
                            cpf = cpf,
                            plano = planoSelecionado,
                            status = ativo
                        )
                    )
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                enabled = nome.isNotBlank() && email.isNotBlank() && planoSelecionado.isNotBlank()
            ) {
                Text("Salvar Alterações", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancelar")
            }
        }
    }
}