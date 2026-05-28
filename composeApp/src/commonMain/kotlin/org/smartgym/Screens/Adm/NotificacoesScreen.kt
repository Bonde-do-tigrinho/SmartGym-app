package org.smartgym.Screens.Adm

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.smartgym.viewModel.Adm.NotificacoesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacoesScreen(
    viewModel: NotificacoesViewModel = viewModel(),
    isAdmin: Boolean = true,
    onNavigateToCriar: () -> Unit = {},
    onNavigateToEditar: (Int) -> Unit = {}
) {
    val notificacoes by viewModel.notificacoes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.carregarNotificacoes()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // CABEÇALHO DO APP
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Notificações",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (isAdmin) "Gerencie o Mural de Avisos" else "Aqui fica o Mural de Avisos",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                // Botão "Criar Aviso"
                if (isAdmin) {
                    Button(
                        onClick = onNavigateToCriar,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9FF00)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Novo", tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Criar Aviso", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // LISTA DE AVISOS
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFD9FF00))
                }
            } else if (notificacoes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Sem avisos no mural.", color = Color.Gray, style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(notificacoes) { notificacao ->
                        val isNova = viewModel.ehNotificacaoNova(notificacao)

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = if (isNova) BorderStroke(1.dp, Color(0xFFD9FF00)) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {

                                // TOPO DO CARD: Ellipse da Categoria e Botões
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Ellipse com a Categoria
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFD9FF00), shape = RoundedCornerShape(50))
                                            .padding(horizontal = 12.dp, vertical = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = notificacao.categoria,
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }

                                    // Botões de Editar e Deletar alinhados na mesma altura
                                    if (isAdmin) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(
                                                onClick = { onNavigateToEditar(notificacao.id!!) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Outlined.Edit, contentDescription = "Editar", tint = Color.Gray, modifier = Modifier.size(22.dp))
                                            }
                                            IconButton(
                                                onClick = { viewModel.apagarNotificacao(notificacao.id!!) },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(
                                                    Icons.Outlined.Delete,
                                                    contentDescription = "Deletar",
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = notificacao.titulo,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = notificacao.mensagem,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = viewModel.formatarData(notificacao.dataPostagem),
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}