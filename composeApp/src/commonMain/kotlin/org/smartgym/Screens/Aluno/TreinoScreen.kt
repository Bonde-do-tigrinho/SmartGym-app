package org.smartgym.Screens.Aluno

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.jetbrains.compose.resources.Font
import org.smartgym.theme.SmartGymGreen
import org.smartgym.viewModel.aluno.TreinoViewModel
import smartgym.composeapp.generated.resources.Res
import smartgym.composeapp.generated.resources.inter_bold
import smartgym.composeapp.generated.resources.inter_regular
import smartgym.composeapp.generated.resources.inter_semibold

private val InterFont @Composable get() = FontFamily(
    Font(Res.font.inter_regular, FontWeight.Normal),
    Font(Res.font.inter_semibold, FontWeight.SemiBold),
    Font(Res.font.inter_bold, FontWeight.Bold)
)

@Composable
fun TreinoScreen(
    navController: NavController,
    viewModel: TreinoViewModel
) {
    // Cores fiéis à estética Dark Mode da marca
    val fundoPretoFosco = Color(0xFF0A0A0A)
    val cinzaCardEscuro = Color(0xFF1A1A1A)

    val fichaAtiva by viewModel.fichaAtiva.collectAsState()
    val letraSelecionada by viewModel.letraSelecionada.collectAsState()
    val exerciciosDoDia by viewModel.exerciciosDoDiaAtivo.collectAsState()

    val exerciciosConcluidosIds by viewModel.exerciciosConcluidosIds.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val total = exerciciosDoDia.size
    val concluidos = exerciciosDoDia.count { exerciciosConcluidosIds.contains(it.exercicioId) }
    val progresso = if (total > 0) concluidos.toFloat() / total else 0f

    LaunchedEffect(Unit) {
        viewModel.carregarMeuTreino()
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize().background(fundoPretoFosco), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = SmartGymGreen)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fundoPretoFosco)
            .padding(horizontal = 24.dp)
            .statusBarsPadding()
    ) {
        Spacer(Modifier.height(8.dp))

        IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(24.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "TREINO.",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = InterFont
        )
        Text(
            text = "Seu treino de hoje",
            color = Color.Gray,
            fontSize = 14.sp,
            fontFamily = InterFont
        )

        Spacer(Modifier.height(24.dp))

        // --- ROW DE SELEÇÃO: LETRAS A, B, C (GIGANTES E QUADRADAS) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf("A", "B", "C").forEach { letra ->
                val isSelected = letraSelecionada.equals(letra, ignoreCase = true)
                val rotinaCadastrada = fichaAtiva?.rotinaDias?.find { it.letra.equals(letra, ignoreCase = true) }

                // Formata o subtítulo dividindo o texto se houver conjunções, mantendo limpo
                val focoTreinoTexto = rotinaCadastrada?.focoTreino?.replace(" e ", "\ne\n") ?: "Vazio"

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(110.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) SmartGymGreen else cinzaCardEscuro)
                        .clickable { viewModel.selecionarDia(letra) }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = letra,
                            color = if (isSelected) Color.Black else Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = InterFont
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = focoTreinoTexto,
                            color = if (isSelected) Color.Black.copy(alpha = 0.8f) else Color.Gray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            lineHeight = 13.sp,
                            fontFamily = InterFont
                        )
                    }
                }
            }
        }

        // 💡 POSIÇÃO CORRETA: Fora do loop, criando o espaçamento perfeito para a lista
        Spacer(modifier = Modifier.height(28.dp))

        // Carrega o foco do dia selecionado em tempo real
        val rotinaAtual = fichaAtiva?.rotinaDias?.find { it.letra.equals(letraSelecionada, ignoreCase = true) }
        val nomeDoTreinoFocado = rotinaAtual?.focoTreino ?: "Sem foco definido"

        Text(
            text = "Treino $letraSelecionada — $nomeDoTreinoFocado",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = InterFont
        )

        Spacer(modifier = Modifier.height(12.dp))

        // --- SEÇÃO DE PROGRESSO ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Progresso das séries", color = Color.Gray, fontSize = 14.sp, fontFamily = InterFont)
            Text("$concluidos/$total", color = SmartGymGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = InterFont)
        }

        Spacer(Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { progresso },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)),
            color = SmartGymGreen,
            trackColor = cinzaCardEscuro,
        )

        Spacer(Modifier.height(24.dp))

        // --- CONTEÚDO DINÂMICO (LISTA DE EXERCÍCIOS) ---
        if (exerciciosDoDia.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = "Nenhum exercício registrado para o Treino $letraSelecionada.",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = InterFont
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 32.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(exerciciosDoDia, key = { it.exercicioId }) { config ->
                    val isConcluido = exerciciosConcluidosIds.contains(config.exercicioId)
                    val nomeReal = viewModel.obterNomeExercicio(config.exercicioId)
                    val grupoReal = viewModel.obterGrupoMuscular(config.exercicioId)

                    ExercicioTreinoItem(
                        nome = nomeReal,
                        subtitulo = "${config.series} séries × ${config.repeticoes} reps",
                        grupoMuscular = grupoReal.ifBlank { "Geral" },
                        isConcluido = isConcluido,
                        onClick = { viewModel.alternarConclusaoExercicio(config.exercicioId) }
                    )
                }
            }
        }
    }
}

@Composable
fun ExercicioTreinoItem(
    nome: String,
    subtitulo: String,
    grupoMuscular: String,
    isConcluido: Boolean,
    onClick: () -> Unit
) {
    val fundoCard = if (isConcluido) Color(0xFF142416) else Color(0xFF1A1A1A)
    val bordaCard = if (isConcluido) SmartGymGreen.copy(alpha = 0.3f) else Color.Transparent

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(fundoCard)
            .border(1.dp, bordaCard, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Indicador de Check lateral
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isConcluido) SmartGymGreen else Color(0xFF262626)),
                contentAlignment = Alignment.Center
            ) {
                if (isConcluido) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Concluído",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Textos informativos
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nome,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isConcluido) Color.Gray else Color.White,
                    textDecoration = if (isConcluido) TextDecoration.LineThrough else TextDecoration.None,
                    fontFamily = InterFont
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitulo,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontFamily = InterFont
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .background(color = Color(0xFF3A1F1F), shape = RoundedCornerShape(50.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = grupoMuscular,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEF4444),
                        fontFamily = InterFont
                    )
                }
            }
        }
    }
}