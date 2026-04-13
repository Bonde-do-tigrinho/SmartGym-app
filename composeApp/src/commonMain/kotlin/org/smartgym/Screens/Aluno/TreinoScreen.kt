package org.smartgym.Screens.Aluno

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.smartgym.components.Aluno.ExercicioCard

import org.smartgym.viewModel.aluno.TreinoViewModel

@Composable
fun TreinoScreen(
    navController: NavController,
    viewModel: TreinoViewModel
) {
    val colors = MaterialTheme.colorScheme
    val treinos by viewModel.treinos.collectAsState()
    val diaSelecionado by viewModel.diaSelecionado.collectAsState()
    val exercicios by viewModel.exerciciosDoDia.collectAsState()

    // Cálculo da Barra de Progresso
    val concluidos = exercicios.count { it.concluido }
    val total = exercicios.size
    val progresso = if (total > 0) concluidos.toFloat() / total else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        // Header e Botão de Voltar
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = colors.onBackground)
        }
        Text("TREINO.", color = colors.onBackground, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
        Text("Seu treino de hoje", color = colors.onSurfaceVariant, fontSize = 16.sp)

        Spacer(Modifier.height(24.dp))

        // Abas A, B, C (mantive na mesma tela pois são bem específicas deste contexto)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            treinos.forEach { treino ->
                val isSelected = diaSelecionado == treino.id
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .clickable { viewModel.selecionarDia(treino.id) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) colors.primary else colors.surfaceVariant
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = treino.id,
                            color = if (isSelected) colors.onPrimary else colors.onSurface,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = treino.titulo,
                            color = if (isSelected) colors.onPrimary.copy(alpha = 0.7f) else colors.onSurfaceVariant,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Barra de Progresso (Verde Neon)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Progresso", color = colors.onSurfaceVariant, fontSize = 14.sp)
            Text("$concluidos/$total", color = colors.primary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progresso },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)),
            color = colors.primary,
            trackColor = colors.surfaceVariant,
        )

        Spacer(Modifier.height(24.dp))

        // A Mágica Acontece Aqui: Lista usando o nosso ExercicioCard
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(exercicios) { exercicio ->
                // Chamando o componente externo e passando a função da ViewModel
                ExercicioCard(
                    exercicio = exercicio,
                    onClick = { viewModel.alternarConclusaoExercicio(exercicio.id) }
                )
            }
        }
    }
}