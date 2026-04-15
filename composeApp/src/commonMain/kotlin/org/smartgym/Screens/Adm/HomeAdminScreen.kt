package org.smartgym.Screens.Adm

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import org.smartgym.viewModel.Adm.HomeAdminViewModel

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
fun HomeAdminScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: HomeAdminViewModel = viewModel()
) {
    val kpiItems by viewModel.kpiItems.collectAsState()
    val chartData by viewModel.chartData.collectAsState()
    val gradientItems by viewModel.gradientItems.collectAsState()
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

        kpiItems.forEach { item ->
            KpiCard(
                title = item.title,
                subtitle = item.subtitle,
                value = item.value,
                badgeValue = item.badgeValue,
                icon = item.icon,
                iconColor = item.iconColor,
                isPositive = item.isPositive
            )
        }

        RevenueBarChart(data = chartData)

        gradientItems.forEach { item ->
            GradientCard(
                title = item.title,
                value = item.value,
                icon = item.icon,
                gradient = item.gradient
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}
