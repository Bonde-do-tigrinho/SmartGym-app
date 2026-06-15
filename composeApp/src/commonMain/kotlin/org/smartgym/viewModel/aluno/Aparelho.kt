package org.smartgym.viewModel.aluno

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.smartgym.model.Adm.MaquinaIot
import org.smartgym.repository.ApiMaquinasIotRepository

class AparelhosViewModel : ViewModel() {

    private val repository = ApiMaquinasIotRepository()

    private val _maquinasIot = MutableStateFlow<List<MaquinaIot>>(emptyList())
    val maquinasIot: StateFlow<List<MaquinaIot>> = _maquinasIot.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Todos")
    val selectedCategory: StateFlow<String> = _selectedCategory

    init {
        carregarMaquinasIot()
    }

    fun carregarMaquinasIot() {
        viewModelScope.launch {
            try {
                 val listaDoBanco = repository.getAll()
                 _maquinasIot.value = listaDoBanco.toMutableList()

                println("📱 [ALUNO SCREEN] Loop rodou! Total de máquinas na UI do Aluno: ${_maquinasIot.value.size}")
            } catch (e: Exception) {
                println("🚨 [ERRO API ALUNO]: Falha ao buscar aparelhos do banco: ${e.message}")
            }
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }
}