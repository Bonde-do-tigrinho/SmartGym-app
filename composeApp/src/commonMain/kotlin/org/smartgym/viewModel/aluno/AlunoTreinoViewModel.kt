package org.smartgym.viewModel.aluno

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.smartgym.model.professor.FichaTreino
import org.smartgym.repository.FichaTreinoRepository

class AlunoTreinoViewModel(
    private val fichaRepository: FichaTreinoRepository
) : ViewModel() {

    private val _fichaAtiva = MutableStateFlow<FichaTreino?>(null)
    val fichaAtiva: StateFlow<FichaTreino?> = _fichaAtiva.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun carregarMeuTreino() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _fichaAtiva.value = fichaRepository.getMinhaFicha()
            } catch (e: Exception) {
                _fichaAtiva.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}