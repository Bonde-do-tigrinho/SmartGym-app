package org.smartgym.Screens.Adm

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun KpiCard(
    title: String,
    subtitle: String,
    value: String,
    badgeValue: String,
    icon: ImageVector,
    iconColor: Color,
    isPositive: Boolean = true
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier.size(40.dp).background(iconColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = iconColor)
                }
                Text(
                    text = if (isPositive) "+$badgeValue%" else "-$badgeValue%",
                    color = if (isPositive) Color(0xFF4CAF50) else Color.Red,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun GradientCard(
    title: String,
    value: String,
    icon: ImageVector,
    gradient: Brush
) {
    Card(
        modifier = Modifier.fillMaxWidth().height(120.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(gradient).padding(16.dp)) {
            Column(verticalArrangement = Arrangement.SpaceBetween) {
                Icon(icon, null, tint = Color.White)
                Spacer(Modifier.weight(1f))
                Text(title, color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
                Text(value, color = Color.White, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}
@Composable
fun HomeAdminScreen(navController: NavController, modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                "Dashboard",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Visão geral do negócio",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        KpiCard("Total de Alunos", "Crescimento este mês", "589", "12", Icons.Rounded.People, Color.Blue)
        KpiCard("Instrutores", "Ativos no sistema", "24", "2", Icons.Rounded.Person, Color.Green)
        KpiCard("Unidades", "Em operação", "3", "0", Icons.Rounded.Business, Color.Magenta, isPositive = true)
        KpiCard("Receita Mensal", "Previsão de fechamento", "R$ 67k", "14", Icons.Rounded.ShowChart, Color.Yellow)

        // Espaço para o Gráfico da Receita (Usando KoalaPlot futuramente)
        Card(
            modifier = Modifier.fillMaxWidth().height(300.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Receita Mensal", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                // Aqui entrará o componente do KoalaPlot
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Gráfico de Barras aqui", color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        GradientCard(
            "Taxa de Frequência", "78%", Icons.Rounded.Timeline,
            Brush.linearGradient(listOf(Color(0xFF2196F3), Color(0xFF1976D2)))
        )
        GradientCard(
            "Taxa de Renovação", "92%", Icons.Rounded.CreditCard,
            Brush.linearGradient(listOf(Color(0xFF4CAF50), Color(0xFF388E3C)))
        )
        GradientCard(
            "Crescimento Anual", "24%", Icons.Rounded.TrendingUp,
            Brush.linearGradient(listOf(Color(0xFF9C27B0), Color(0xFF7B1FA2)))
        )

        Spacer(Modifier.height(32.dp)) // Espaço extra no final do scroll
    }
}