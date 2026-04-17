package org.smartgym.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.smartgym.model.Adm.Maquina

@Composable
fun MaquinaCard(
    maquina: Maquina,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onStatusToggleClick: () -> Unit
) {
    val statusDaApi = maquina.status?.uppercase() ?: "LIVRE"

    val statusColor = when (statusDaApi) {
        "LIVRE" -> Color(0xFF4CAF50) // Verde
        "OCUPADA" -> Color(0xFFF44336) // Vermelho
        "MANUTENCAO" -> Color(0xFFFF9800) // Laranja
        else -> Color.Gray
    }

    val borderCol = when (statusDaApi) {
        "LIVRE" -> Color(0xFFC8E6C9)
        "OCUPADA" -> Color(0xFFFFCDD2)
        "MANUTENCAO" -> Color(0xFFFFE0B2)
        else -> Color.LightGray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, borderCol)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(statusColor, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = statusDaApi, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Row {
                    IconButton(onClick = onEditClick, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = maquina.nome ?: "Sem Nome", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Localização:", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                Text(text = maquina.localizacao ?: "N/A", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
            }

            Spacer(modifier = Modifier.height(20.dp))

            val textoDoBotao = if (statusDaApi == "LIVRE") "Marcar Ocupada" else "Liberar Máquina"

            OutlinedButton(
                onClick = onStatusToggleClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
            ) {
                Text(text = textoDoBotao, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}