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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import org.smartgym.viewModel.Adm.AlunosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovoAlunoScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: AlunosViewModel
) {
    val scrollState = rememberScrollState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var planoSelecionado by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(true) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val planos = listOf("Basic", "Premium", "Black")

    var cpfRaw by remember { mutableStateOf("") }
    var telefoneRaw by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novo Aluno", fontWeight = FontWeight.Bold) },
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
                value = cpfRaw,
                onValueChange = {
                    if (it.length <= 11) cpfRaw = it.filter { c -> c.isDigit() }
                },
                keyboardType = KeyboardType.Number,
                visualTransformation = CpfVisualTransformation()
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
                value = telefoneRaw,
                onValueChange = {
                    if (it.length <= 11) telefoneRaw = it.filter { c -> c.isDigit() }
                },
                keyboardType = KeyboardType.Phone,
                visualTransformation = TelefoneVisualTransformation()
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
                            if (status) "Aluno terá acesso à academia" else "Aluno sem acesso à academia",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = status,
                        onCheckedChange = { status = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            if (isLoading) {
                CircularProgressIndicator()
            }

            errorMessage?.let {
                Text(it, color = Color.Red)
            }
            Button(
                onClick = {
                    viewModel.adicionarAluno(
                        nome = nome,
                        email = email,
                        cpf = cpfRaw,
                        telefone = telefoneRaw,
                        plano = planoSelecionado,
                        status = status,
                    )

                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                enabled = nome.isNotBlank() && email.isNotBlank() && planoSelecionado.isNotBlank()
            ) {
                Text("Salvar Aluno", color = Color.Black, fontWeight = FontWeight.Bold)
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

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun CampoTexto(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        visualTransformation = visualTransformation
    )
}

