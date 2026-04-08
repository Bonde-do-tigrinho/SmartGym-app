package org.smartgym.Screens.Aluno

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
                onClick = {},
                modifier = Modifier.background(colors.surfaceVariant, CircleShape)
            ) {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = colors.onSurface)
            }
        }

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth().height(180.dp),
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
                    modifier = Modifier.size(60.dp).background(colors.primary, CircleShape),
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

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            InfoCard(Icons.Default.Bolt, userData.aparelhosLivres.toString(), "Aparelhos livres", colors.primary, Modifier.weight(1f))
            InfoCard(Icons.Default.Groups, userData.pessoasEmUso.toString(), "Em uso agora", colors.primary, Modifier.weight(1f))
        }

        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("SEU PROFESSOR", color = colors.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = { }) {
                Text("Ver todos >", color = colors.primary, fontSize = 14.sp)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(56.dp).background(colors.surfaceVariant, CircleShape),
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

        Card(
            modifier = Modifier.fillMaxWidth(),
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