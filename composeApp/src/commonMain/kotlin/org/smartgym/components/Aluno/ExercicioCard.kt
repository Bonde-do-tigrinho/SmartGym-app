package org.smartgym.components.Aluno

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.smartgym.model.aluno.Exercicio

@Composable
fun ExercicioCard(
    exercicio: Exercicio,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    // Lógica de Cores baseada no status (Concluído vs Pendente)
    // Se concluído, usa um fundo mais escuro/esverdeado (aproximando do seu design)
    val bgColor = if (exercicio.concluido) Color(0xFF1B261A) else colors.surface

    // O círculo do check fica verde neon se concluído, ou cinza escuro se pendente
    val iconBgColor = if (exercicio.concluido) colors.primary else colors.surfaceVariant.copy(alpha = 0.5f)
    val iconColor = if (exercicio.concluido) colors.onPrimary else Color.Transparent

    // Risco no texto se a tarefa foi feita
    val textDecoration = if (exercicio.concluido) TextDecoration.LineThrough else TextDecoration.None

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Círculo com Checkmark
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconBgColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (exercicio.concluido) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Concluído",
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            // Informações do Exercício
            Column {
                Text(
                    text = exercicio.nome,
                    color = colors.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = textDecoration
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${exercicio.series} séries × ${exercicio.repeticoes} reps",
                    color = colors.onSurfaceVariant,
                    fontSize = 14.sp,
                    textDecoration = textDecoration
                )
                Spacer(Modifier.height(8.dp))

                // Badge do Grupo Muscular (Pílula vermelha)
                Box(
                    modifier = Modifier
                        .background(Color(0xFF4A1C1C), RoundedCornerShape(50.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = exercicio.grupoMuscular,
                        color = Color(0xFFFF8888),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}