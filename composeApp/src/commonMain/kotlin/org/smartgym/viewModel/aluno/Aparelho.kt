package org.smartgym.viewModel.aluno

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.smartgym.model.aluno.Aparelho

class AparelhosViewModel : ViewModel() {
    private val _machines = MutableStateFlow<List<Aparelho>>(emptyList())
    val machines: StateFlow<List<Aparelho>> = _machines

    private val _selectedCategory = MutableStateFlow("Todos")
    val selectedCategory: StateFlow<String> = _selectedCategory

    init {
        // Dados mockados baseados na sua imagem
        _machines.value = listOf(
            Aparelho(1, "Supino Reto", "Peito", 12, true),
            Aparelho(2, "Supino Inclinado", "Peito", null, true),
            Aparelho(3, "Crossover", "Peito", null, true),
            Aparelho(4, "Puxada Frontal", "Costas", 8, true)
        )
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }
}