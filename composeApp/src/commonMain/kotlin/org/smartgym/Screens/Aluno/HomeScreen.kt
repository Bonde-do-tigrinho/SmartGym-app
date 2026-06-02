package org.smartgym.Screens.Aluno

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.smartgym.components.InfoCard
import org.smartgym.model.Adm.StatusMaquinaIot
import org.smartgym.viewModel.aluno.AlunoPerfilViewModel
import org.smartgym.viewModel.aluno.AparelhosViewModel
import org.smartgym.viewModel.aluno.TreinoViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    treinoViewModel: TreinoViewModel,
    viewModel: AlunoPerfilViewModel,
    temNotificacaoNova: Boolean,
    aparelhosViewModel: AparelhosViewModel = viewModel { AparelhosViewModel() }
) {
    val colors = MaterialTheme.colorScheme

    val perfil by viewModel.perfil.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val fichaState by treinoViewModel.fichaAtiva.collectAsState()
    val letraSelecionada by treinoViewModel.letraSelecionada.collectAsState()
    val exerciciosDoDiaAtivo by treinoViewModel.exerciciosDoDiaAtivo.collectAsState()
    val focoDoDiaAtivo by treinoViewModel.focoDoDiaAtivo.collectAsState()
    val listaDeMaquinas by aparelhosViewModel.maquinasIot.collectAsState()

    LaunchedEffect(Unit) {
        while (true) {
            aparelhosViewModel.carregarMaquinasIot()
            kotlinx.coroutines.delay(3000L)
        }
    }

    LaunchedEffect(Unit) {
        treinoViewModel.carregarMeuTreino()
    }

    LaunchedEffect(fichaState) {
        val rotinaDias = fichaState?.rotinaDias ?: emptyList()
        if (rotinaDias.isNotEmpty()) {
            val agora = Clock.System.now()

            val hoje = agora.toLocalDateTime(TimeZone.currentSystemDefault())
            val diaDoAno = hoje.dayOfYear

            val indice = diaDoAno % rotinaDias.size
            val letraIdealDeHoje = rotinaDias[indice].letra

            treinoViewModel.selecionarDia(letraIdealDeHoje)
        }
    }

    LaunchedEffect(Unit){
        if (perfil == null || perfil?.plano == null) {
            viewModel.carregarPerfil()
        }
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = colors.primary)
        }
        return
    }

    val nome = perfil?.nome ?: "Usuário"
    val professorNome = perfil?.professorNome
    val planoNome = perfil?.plano?.nome
    val planoValor = perfil?.plano?.valor?.let { valor ->
        "R$ ${valor.toString().replace(".", ",")}${if (!valor.toString().contains(".")) ",00" else ""}"
    } ?: ""
    val planoVencimento = perfil?.planoVencimento ?: ""

    val aparelhosLivresCount = listaDeMaquinas.count { it.status == StatusMaquinaIot.LIVRE }
    val aparelhosEmUsoCount = listaDeMaquinas.count { it.status == StatusMaquinaIot.OCUPADA }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Ola,", color = colors.onSurfaceVariant, fontSize = 16.sp)
                Text(
                    nome.uppercase(),
                    color = colors.onBackground,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            IconButton(
                onClick = { navController.navigate("notificacoes") },
                modifier = Modifier.background(colors.surfaceVariant, CircleShape)
            ) {
                if (temNotificacaoNova) {
                    BadgedBox(
                        badge = { Badge(containerColor = Color(0xFFD9FF00)) }
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notificações", tint = colors.onSurface)
                    }
                } else {
                    Icon(Icons.Default.Notifications, contentDescription = "Notificações", tint = colors.onSurface)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clickable {
                    if (fichaState?.rotinaDias?.isNotEmpty() == true) {
                        navController.navigate("treino")
                    }
                },
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(modifier = Modifier.padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                    Text(
                        "TREINO DE HOJE",
                        color = colors.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (fichaState != null) "TREINO $letraSelecionada" else "SEM TREINO",
                        color = colors.onSurface,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = if (fichaState != null) "Foco: $focoDoDiaAtivo" else "Nenhuma ficha ativa",
                        color = colors.onSurfaceVariant,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "${exerciciosDoDiaAtivo.size} exercícios cadastrados",
                        color = colors.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(colors.primary, CircleShape)
                        .align(Alignment.CenterVertically),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (fichaState != null) letraSelecionada else "?",
                        color = colors.onPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .clickable { navController.navigate("aparelhos") }
            ) {
                InfoCard(
                    icon = Icons.Default.Bolt,
                    value = aparelhosLivresCount.toString(),
                    label = "Aparelhos livres",
                    iconColor = colors.primary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Box(modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .clickable { navController.navigate("aparelhos") }
            ) {
                InfoCard(
                    icon = Icons.Default.Groups,
                    value = aparelhosEmUsoCount.toString(),
                    label = "Em uso agora",
                    iconColor = colors.primary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("SEU PROFESSOR", color = colors.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* TODO */ },
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(colors.surfaceVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (professorNome != null)
                            professorNome.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
                        else "?",
                        color = colors.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        professorNome ?: "Nenhum professor vinculado",
                        color = if (professorNome != null) colors.onSurface else colors.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    if (professorNome != null) {
                        Text("Musculação", color = colors.onSurfaceVariant, fontSize = 14.sp)
                        Text("Seg-Sáb 08h-14h", color = colors.onSurfaceVariant, fontSize = 12.sp)
                    }
                }
                if (professorNome != null) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = colors.primary, modifier = Modifier.size(20.dp))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("pagamentos") },
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Shield,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        planoNome ?: "Sem plano ativo",
                        color = if (planoNome != null) colors.onSurface else colors.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    if (planoVencimento.isNotBlank()) {
                        Text("Vence em $planoVencimento", color = colors.onSurfaceVariant, fontSize = 12.sp)
                    }
                }
                if (planoValor.isNotBlank()) {
                    Text(
                        planoValor,
                        color = colors.primary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(120.dp))
    }
}