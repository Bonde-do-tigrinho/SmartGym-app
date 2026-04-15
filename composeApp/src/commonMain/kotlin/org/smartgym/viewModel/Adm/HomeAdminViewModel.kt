package org.smartgym.viewModel.Adm

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.smartgym.Screens.Adm.ChartItem
import org.smartgym.model.Adm.GradientItem
import org.smartgym.model.Adm.KpiItem

class HomeAdminViewModel : ViewModel() {

    private val _kpiItems = MutableStateFlow(loadKpiItems())
    val kpiItems: StateFlow<List<KpiItem>> = _kpiItems.asStateFlow()

    private val _chartData = MutableStateFlow(loadChartData())
    val chartData: StateFlow<List<ChartItem>> = _chartData.asStateFlow()

    private val _gradientItems = MutableStateFlow(loadGradientItems())
    val gradientItems: StateFlow<List<GradientItem>> = _gradientItems.asStateFlow()

    private fun loadKpiItems() = listOf(
        KpiItem("Total de Alunos", "Crescimento este mês", "589", "12", Icons.Rounded.People, Color.Blue),
        KpiItem("Instrutores", "Ativos no sistema", "24", "2", Icons.Rounded.Person, Color.Green),
        KpiItem("Unidades", "Em operação", "3", "0", Icons.Rounded.Business, Color.Magenta),
        KpiItem("Receita Mensal", "Previsão de fechamento", "R$ 67k", "14", Icons.Rounded.ShowChart, Color.Yellow)
    )

    private fun loadChartData() = listOf(
        ChartItem("Jan", 42000f),
        ChartItem("Fev", 52000f),
        ChartItem("Mar", 47000f),
        ChartItem("Abr", 61000f),
        ChartItem("Mai", 56000f),
        ChartItem("Jun", 67000f)
    )

    private fun loadGradientItems() = listOf(
        GradientItem(
            "Taxa de Frequência", "78%", Icons.Rounded.Timeline,
            Brush.linearGradient(listOf(Color(0xFF2196F3), Color(0xFF1976D2)))
        ),
        GradientItem(
            "Taxa de Renovação", "92%", Icons.Rounded.CreditCard,
            Brush.linearGradient(listOf(Color(0xFF4CAF50), Color(0xFF388E3C)))
        ),
        GradientItem(
            "Crescimento Anual", "24%", Icons.Rounded.TrendingUp,
            Brush.linearGradient(listOf(Color(0xFF9C27B0), Color(0xFF7B1FA2)))
        )
    )
}