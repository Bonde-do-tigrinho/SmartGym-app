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
import org.smartgym.model.professor.ExercicioFichaTreino

@Composable
fun ExercicioCard(
    exercicio: ExercicioFichaTreino,
    concluido: Boolean,
    nomeExercicio: String,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    val bgColor = if (concluido) Color(0xFF1B261A) else colors.surface
    val iconBgColor = if (concluido) colors.primary else colors.surfaceVariant.copy(alpha = 0.5f)
    val iconColor = if (concluido) colors.onPrimary else Color.Transparent
    val textDecoration = if (concluido) TextDecoration.LineThrough else TextDecoration.None

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
                if (concluido) {
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
                    text = nomeExercicio,
                    color = colors.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = textDecoration
                )
                Spacer(Modifier.height(4.dp))

                Text(
                    text = "${exercicio.series} séries × ${exercicio.repeticoes} reps (${exercicio.descansoSegundos}s desc)",
                    color = colors.onSurfaceVariant,
                    fontSize = 14.sp,
                    textDecoration = textDecoration
                )
                Spacer(Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .background(Color(0xFF4A1C1C), RoundedCornerShape(50.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "SmartGym",
                        color = Color(0xFFFF8888),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}