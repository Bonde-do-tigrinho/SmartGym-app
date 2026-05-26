package org.smartgym.viewModel.Adm

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.smartgym.Screens.Adm.ChartItem
import org.smartgym.model.Adm.GradientItem
import org.smartgym.model.Adm.KpiItem
import org.smartgym.repository.ApiDashboardRepository

class HomeAdminViewModel : ViewModel() {

    private val repository = ApiDashboardRepository()

    private val _kpiItems = MutableStateFlow(emptyList<KpiItem>())
    val kpiItems: StateFlow<List<KpiItem>> = _kpiItems.asStateFlow()

    private val _chartData = MutableStateFlow(loadChartData())
    val chartData: StateFlow<List<ChartItem>> = _chartData.asStateFlow()

    private val _gradientItems = MutableStateFlow(emptyList<GradientItem>())
    val gradientItems: StateFlow<List<GradientItem>> = _gradientItems.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro.asStateFlow()

    init {
        carregarDashboard()
    }

    fun carregarDashboard() {
        viewModelScope.launch {
            _isLoading.value = true
            _erro.value = null

            val data = repository.getDashboard()

            if (data != null) {
                _kpiItems.value = listOf(
                    KpiItem(
                        "Total de Alunos", "Ativos: ${data.alunosAtivos} | Inativos: ${data.alunosInativos}",
                        "${data.totalAlunos}", "0", Icons.Rounded.People, Color(0xFF2196F3)
                    ),
                    KpiItem(
                        "Professores", "Ativos no sistema",
                        "${data.totalProfessores}", "0", Icons.Rounded.Person, Color(0xFF4CAF50)
                    ),
                    KpiItem(
                        "Unidades", "Em operação",
                        "${data.totalUnidades}", "0", Icons.Rounded.Business, Color(0xFF9C27B0)
                    )
                )

                _gradientItems.value = listOf(
                    GradientItem(
                        "Alunos Ativos", "${data.alunosAtivos}", Icons.Rounded.People,
                        Brush.linearGradient(listOf(Color(0xFF2196F3), Color(0xFF1976D2)))
                    ),
                    GradientItem(
                        "Alunos Inativos", "${data.alunosInativos}", Icons.Rounded.PersonOff,
                        Brush.linearGradient(listOf(Color(0xFFFF5722), Color(0xFFE64A19)))
                    ),
                    GradientItem(
                        "Total de Unidades", "${data.totalUnidades}", Icons.Rounded.Business,
                        Brush.linearGradient(listOf(Color(0xFF9C27B0), Color(0xFF7B1FA2)))
                    )
                )
            } else {
                _erro.value = "Erro ao carregar dados do dashboard"
            }

            _isLoading.value = false
        }
    }

    private fun loadChartData() = listOf(
        ChartItem("Jan", 42000f),
        ChartItem("Fev", 52000f),
        ChartItem("Mar", 47000f),
        ChartItem("Abr", 61000f),
        ChartItem("Mai", 56000f),
        ChartItem("Jun", 67000f)
    )

}