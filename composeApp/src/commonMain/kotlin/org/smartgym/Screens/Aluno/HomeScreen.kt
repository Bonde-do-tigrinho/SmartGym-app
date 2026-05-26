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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.smartgym.components.InfoCard
import org.smartgym.viewModel.aluno.AlunoPerfilViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: AlunoPerfilViewModel) {
    val perfil by viewModel.perfil.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val colors = MaterialTheme.colorScheme

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Boa tarde,", color = colors.onSurfaceVariant, fontSize = 16.sp)
                Text(
                    nome.uppercase(),
                    color = colors.onBackground,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            IconButton(
                onClick = { /* TODO */ },
                modifier = Modifier.background(colors.surfaceVariant, CircleShape)
            ) {
                Icon(Icons.Default.Notifications, contentDescription = "Notificações", tint = colors.onSurface)
            }
        }

        Spacer(Modifier.height(24.dp))

        // Card de Treino do Dia
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clickable { /* TODO */ },
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(modifier = Modifier.padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "TREINO DE HOJE",
                        color = colors.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        if (professorNome != null) "Aguardando ficha" else "Sem professor",
                        color = colors.onSurface,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        if (professorNome != null) "Professor vinculado" else "Nenhum professor vinculado",
                        color = colors.onSurfaceVariant
                    )
                    Text("0 exercícios", color = colors.onSurfaceVariant)
                }
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(colors.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("?", color = colors.onPrimary, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Cards de Info
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).clickable { /* TODO */ }) {
                InfoCard(
                    icon = Icons.Default.Bolt,
                    value = "-",
                    label = "Aparelhos livres",
                    iconColor = colors.primary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).clickable { /* TODO */ }) {
                InfoCard(
                    icon = Icons.Default.Groups,
                    value = "-",
                    label = "Em uso agora",
                    iconColor = colors.primary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        // Seção Professor
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("SEU PROFESSOR", color = colors.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = { /* TODO */ }) {
                Text("Ver todos >", color = colors.primary, fontSize = 14.sp)
            }
        }

        // Card do Professor
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* TODO: navController.navigate("professor_detalhe/${perfil?.professorId}") */ },
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

        // Card do Plano
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* TODO */ },
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