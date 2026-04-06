package org.smartgym.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
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

        // Linha de Info Cards
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            InfoCard(Icons.Default.Bolt, userData.aparelhosLivres.toString(), "Aparelhos livres", Color.Green, Modifier.weight(1f))
            InfoCard(Icons.Default.Groups, userData.pessoasEmUso.toString(), "Em uso agora", Color.Yellow, Modifier.weight(1f))
        }

        // Espaço para a Bottom Bar (que já deve estar no AppNavigation)
        Spacer(Modifier.height(80.dp))
    }
}