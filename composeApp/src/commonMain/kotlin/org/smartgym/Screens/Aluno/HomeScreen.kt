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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.smartgym.UserHomeData
import org.smartgym.components.InfoCard

@Composable
fun HomeScreen(navController: NavController, userData: UserHomeData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Boa tarde,", color = Color.Gray, fontSize = 16.sp)
                Text(userData.userName.uppercase(), color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            }
            IconButton(onClick = {}, modifier = Modifier.background(Color(0xFF1C1C1E), CircleShape)) {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
            }
        }

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth().height(180.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2E)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(modifier = Modifier.padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("TREINO DE HOJE", color = Color(0xFFD4FF00), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(userData.treinoAtual, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text(userData.focoTreino, color = Color.Gray)
                    Text("${userData.qtdExercicios} exercícios", color = Color.Gray)
                }
                Box(
                    modifier = Modifier.size(60.dp).background(Color(0xFFD4FF00), CircleShape),
                    contentAlignment = Alignment.Center
                ) {

                    Text(userData.treinoAtual.last().toString(), fontWeight = FontWeight.Bold, fontSize = 24.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            InfoCard(Icons.Default.Bolt, userData.aparelhosLivres.toString(), "Aparelhos livres", Color.Green, Modifier.weight(1f))
            InfoCard(Icons.Default.Groups, userData.pessoasEmUso.toString(), "Em uso agora", Color.Yellow, Modifier.weight(1f))
        }

        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("SEU PROFESSOR", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = { /* Ver todos */ }) {
                Text("Ver todos >", color = Color(0xFFD4FF00), fontSize = 14.sp)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(56.dp).background(Color(0xFF2C2C2E), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        userData.professorNome.split(" ").map { it.first() }.joinToString(""),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(userData.professorNome, color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Musculação", color = Color.Gray, fontSize = 14.sp)
                    Text("Seg-Sáb 08h-14h", color = Color.DarkGray, fontSize = 12.sp)
                }
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFD4FF00), modifier = Modifier.size(20.dp))
                Text(" ${userData.professorNota}", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
            border = BorderStroke(1.dp, Color(0xFFD4FF00).copy(alpha = 0.3f)),
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
                        tint = Color(0xFFD4FF00),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Plano Premium", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Vence em ${userData.planoVencimento}", color = Color.Gray, fontSize = 12.sp)
                    }
                }


                Spacer(Modifier.weight(1f))

                Text(
                    userData.planoValor,
                    color = Color(0xFFD4FF00),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
            }
        }

        Spacer(Modifier.height(120.dp))
    }
}