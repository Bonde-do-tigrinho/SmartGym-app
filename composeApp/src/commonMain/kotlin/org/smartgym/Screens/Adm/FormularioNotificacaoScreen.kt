package org.smartgym.Screens.Adm

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.smartgym.model.Adm.Notificacao
import org.smartgym.repository.ApiNotificacaoRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioNotificacaoScreen(
    notificacaoId: Int?,
    onNavigateBack: () -> Unit
) {
    val repository = remember { ApiNotificacaoRepository() }
    val coroutineScope = rememberCoroutineScope()

    var titulo by remember { mutableStateOf("") }
    var mensagem by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("GERAL") }
    val categoriasDisponiveis = listOf("GERAL", "MANUTENCAO", "EVENTO", "OUTRO")
    var isLoading by remember { mutableStateOf(false) }

    // Se tiver um ID, busca os dados da notificação para preencher a tela
    LaunchedEffect(notificacaoId) {
        if (notificacaoId != null) {
            isLoading = true
            try {
                val existente = repository.buscarTodas().find { it.id == notificacaoId }
                if (existente != null) {
                    titulo = existente.titulo
                    mensagem = existente.mensagem
                    categoria = existente.categoria
                }
            } catch (e: Exception) {
                println("Erro ao carregar para edição: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (notificacaoId == null) "Novo Aviso" else "Editar Aviso", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E1E1E)),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFD9FF00))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título do Aviso (mín. 5 caracteres)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = mensagem,
                onValueChange = { mensagem = it },
                label = { Text("Descrição do aviso (mín. 5 caracteres)") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5
            )

            Text("Categoria do Aviso:", style = MaterialTheme.typography.titleMedium)

            // Quebra as 4 categorias em 2 linhas para caber certinho na tela do celular
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                categoriasDisponiveis.chunked(2).forEach { rowCategorias ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        rowCategorias.forEach { cat ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = (categoria == cat),
                                    onClick = { categoria = cat },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFD9FF00))
                                )
                                Text(cat, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f)) // Empurra o botão salvar para o final da tela

            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            val novaNotificacao = Notificacao(
                                id = notificacaoId,
                                titulo = titulo,
                                mensagem = mensagem,
                                categoria = categoria
                            )
                            repository.salvar(novaNotificacao)
                            onNavigateBack() // Volta pra lista
                        } catch (e: Exception) {
                            println("Erro ao salvar: ${e.message}")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9FF00)),
                enabled = titulo.length >= 5 && mensagem.length >= 5 // 5 caracteres minimos
            ) {
                Text(
                    text = if (notificacaoId == null) "PUBLICAR AVISO" else "SALVAR ALTERAÇÕES",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}