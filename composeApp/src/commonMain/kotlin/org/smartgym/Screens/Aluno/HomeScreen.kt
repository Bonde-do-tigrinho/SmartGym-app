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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.smartgym.UserHomeData
import org.smartgym.components.InfoCard

@Composable
fun HomeScreen(navController: NavController, userData: UserHomeData) {
    val colors = MaterialTheme.colorScheme

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
                Text("Boa tarde,", color = colors.onSurfaceVariant, fontSize = 16.sp)
                Text(
                    userData.userName.uppercase(),
                    color = colors.onBackground,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            IconButton(
                onClick = { /* TODO: navController.navigate("notificacoes") */ },
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
                .clickable { /* TODO: navController.navigate("treino_detalhe") */ },
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
                        userData.treinoAtual,
                        color = colors.onSurface,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(userData.focoTreino, color = colors.onSurfaceVariant)
                    Text("${userData.qtdExercicios} exercícios", color = colors.onSurfaceVariant)
                }
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(colors.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        userData.treinoAtual.last().toString(),
                        color = colors.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Cards de Aparelhos e Pessoas
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .clickable { /* TODO: navController.navigate("aparelhos") */ }
            ) {
                InfoCard(
                    icon = Icons.Default.Bolt,
                    value = userData.aparelhosLivres.toString(),
                    label = "Aparelhos livres",
                    iconColor = colors.primary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Box(modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .clickable { /* TODO: navController.navigate("ocupacao") */ }
            ) {
                InfoCard(
                    icon = Icons.Default.Groups,
                    value = userData.pessoasEmUso.toString(),
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
            TextButton(onClick = { /* TODO: navController.navigate("professores") */ }) {
                Text("Ver todos >", color = colors.primary, fontSize = 14.sp)
            }
        }

        // Card do Professor
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* TODO: navController.navigate("professor_detalhe/${userData.professorId}") */ },
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
                        userData.professorNome.split(" ").map { it.first() }.joinToString(""),
                        color = colors.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(userData.professorNome, color = colors.onSurface, fontWeight = FontWeight.Bold)
                    Text("Musculação", color = colors.onSurfaceVariant, fontSize = 14.sp)
                    Text("Seg-Sáb 08h-14h", color = colors.onSurfaceVariant, fontSize = 12.sp)
                }
                Icon(Icons.Default.Star, contentDescription = null, tint = colors.primary, modifier = Modifier.size(20.dp))
                Text(" ${userData.professorNota}", color = colors.onSurface, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Card do Plano
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* TODO: navController.navigate("pagamentos") */ },
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Shield,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Plano Premium", color = colors.onSurface, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Vence em ${userData.planoVencimento}", color = colors.onSurfaceVariant, fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.weight(1f))
                Text(
                    userData.planoValor,
                    color = colors.primary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
            }
        }

        Spacer(Modifier.height(120.dp))
    }
}