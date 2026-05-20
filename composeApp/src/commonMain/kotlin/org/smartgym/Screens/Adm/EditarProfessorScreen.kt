package org.smartgym.Screens.Adm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import org.smartgym.viewModel.Adm.ProfessoresViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarProfessorScreen(
    navController: NavController,
    professorId: Int,
    modifier: Modifier = Modifier,
    viewModel: ProfessoresViewModel
) {
    val professores by viewModel.professores.collectAsState()
    val professor = professores.find { it.id == professorId }

    if (professor == null) {
        LaunchedEffect(Unit) { navController.popBackStack() }
        return
    }

    val scrollState = rememberScrollState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var nome by remember { mutableStateOf(professor.nome) }
    var email by remember { mutableStateOf(professor.email) }
    var cpfRaw by remember { mutableStateOf(professor.cpf.filter { it.isDigit() }) }
    var telefoneRaw by remember { mutableStateOf(professor.telefone.filter { it.isDigit() }) }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Professor", fontWeight = FontWeight.Bold) },
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

            Spacer(Modifier.height(8.dp))

            if (isLoading) CircularProgressIndicator()

            errorMessage?.let {
                Text(it, color = Color.Red)
            }

            Button(
                onClick = {
                    viewModel.editarProfessor(
                        professor.copy(
                            nome = nome,
                            email = email,
                            cpf = cpfRaw,
                            telefone = telefoneRaw
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                enabled = nome.isNotBlank() && email.isNotBlank() && cpfRaw.isNotBlank() && telefoneRaw.isNotBlank()
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