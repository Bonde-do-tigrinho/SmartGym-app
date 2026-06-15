package org.smartgym.Screens.Aluno

import AparelhoCard
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.StateFlow
import org.smartgym.Screen
import org.smartgym.viewModel.aluno.AparelhosViewModel
import org.smartgym.viewModel.aluno.TreinoViewModel

@Composable
fun AparelhosScreen(
    navController: NavController,
    viewModel: AparelhosViewModel = viewModel { AparelhosViewModel() }
) {
    val colors = MaterialTheme.colorScheme
    val machines by viewModel.maquinasIot.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val categories = listOf("Todos", "Peito", "Costas", "Pernas")

    LaunchedEffect(Unit) {
        while (true) {
            viewModel.carregarMaquinasIot()
            kotlinx.coroutines.delay(3000L)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = colors.onBackground)
        }

        Text("APARELHOS.", color = colors.onBackground, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
        Text("Status em tempo real", color = colors.onSurfaceVariant, fontSize = 16.sp)

        Spacer(Modifier.height(24.dp))

        // Filtros (Chips)
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                val isSelected = selectedCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectCategory(category) },
                    label = { Text(category) },
                    shape = RoundedCornerShape(50.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colors.primary,
                        selectedLabelColor = colors.onPrimary,
                        containerColor = colors.surfaceVariant,
                        labelColor = colors.onSurface
                    ),
                    border = null
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            val filteredMachines = if (selectedCategory == "Todos") {
                machines
            } else {
                machines.filter { it.localizacao.contains(selectedCategory, ignoreCase = true) }
            }

            items(filteredMachines) { machine ->
                AparelhoCard(maquinaIot = machine)
            }
        }
    }
}
