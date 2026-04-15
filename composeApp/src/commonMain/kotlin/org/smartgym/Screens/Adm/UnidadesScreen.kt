package org.smartgym.Screens.Adm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.OutlinedFlag
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Estrutura de dados temporária (Mock)
data class Unidade(
    val nome: String,
    val endereco: String,
    val cidade: String,
    val alunos: Int,
    val instrutores: Int
)

val mockUnidades = listOf(
    Unidade("Unidade Centro", "Rua Principal, 123", "São Paulo - SP", 320, 12),
    Unidade("Unidade Zona Sul", "Av. Paulista, 456", "São Paulo - SP", 189, 8),
    Unidade("Unidade Zona Oeste", "Rua Secundária, 789", "São Paulo - SP", 80, 4)
)

@Composable
fun UnidadesScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Cabeçalho e Botão Corrigidos
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // O weight(1f) faz o texto ocupar o espaço correto e quebrar a linha
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
                    lineHeight = 18.sp, // Ajuda no espaçamento se quebrar a linha
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { /* Ação de nova unidade */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                modifier = Modifier.height(36.dp) // Altura forçada para deixar mais fino
            ) {
                // Reduzindo um pouquinho o ícone e a fonte para caber no botão mais fino
                Icon(Icons.Default.Add, contentDescription = "Adicionar", tint = Color.Black, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Nova Unidade", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Lista de Cards
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(mockUnidades) { unidade ->
                UnidadeCard(unidade)
            }
        }
    }
}

@Composable
fun UnidadeCard(unidade: Unidade) {
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
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.onSurfaceVariant)
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