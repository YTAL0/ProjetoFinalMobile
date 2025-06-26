package com.example.autocare.medicamento

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocare.AlarmScheduler
import com.example.autocare.DataStore
import com.example.autocare.receitas.ReceitaMedica
import com.example.autocare.receitas.getSampleReceitasMedicas
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Corrigido o nome da classe para corresponder ao que usamos nos outros arquivos
class MedicamentoViewModel(application: Application) : AndroidViewModel(application) {

    private val scheduler = AlarmScheduler(application)
    // CORRIGIDO: O nome da classe é SettingsDataStore
    private val dataStore = DataStore(application)

    // --- Seção de Medicamentos ---
    private val _medicamentos = mutableStateListOf<Medicamento>().apply {
        addAll(getSampleMedicamentos())
    }
    val medicamentos: List<Medicamento> = _medicamentos

    private val _favoriteMedicamentos = mutableStateListOf<Medicamento>()
    val favoriteMedicamentos: List<Medicamento> = _favoriteMedicamentos

    // --- NOVO: Seção de Receitas ---
    private val _receitas = mutableStateListOf<ReceitaMedica>().apply {
        addAll(getSampleReceitasMedicas())
    }
    val receitas: List<ReceitaMedica> = _receitas
    // --- FIM DA SEÇÃO DE RECEITAS ---

    fun getReceitaById(id: String): ReceitaMedica? {
        return _receitas.find { it.id == id }
    }
    // --- Seção de Preferências ---
    val darkModeEnabled: StateFlow<Boolean> = dataStore.darkModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val notificationsEnabled: StateFlow<Boolean> = dataStore.notificationsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)


    init {
        if (notificationsEnabled.value) {
            _medicamentos.forEach { scheduler.schedule(it) }
        }
    }

    // O resto do seu arquivo continua igual, pois as funções de receita (add, remove)
    // ainda não são necessárias para esta primeira versão.

    fun addMedicamento(medicamento: Medicamento) {
        _medicamentos.add(medicamento)
        if (notificationsEnabled.value) {
            scheduler.schedule(medicamento)
        }
    }

    fun updateMedicamento(updatedMedicamento: Medicamento) {
        val index = _medicamentos.indexOfFirst { it.id == updatedMedicamento.id }
        if (index != -1) {
            _medicamentos[index] = updatedMedicamento
            if (notificationsEnabled.value) {
                scheduler.schedule(updatedMedicamento)
            }
            val favoriteIndex = _favoriteMedicamentos.indexOfFirst { it.id == updatedMedicamento.id }
            if (favoriteIndex != -1) {
                _favoriteMedicamentos[favoriteIndex] = updatedMedicamento
            }
        }
    }

    fun removeMedicamento(medicamentoId: String) {
        val medicamento = _medicamentos.find { it.id == medicamentoId }
        medicamento?.let { scheduler.cancel(it) }
        _medicamentos.removeIf { it.id == medicamentoId }
        _favoriteMedicamentos.removeIf { it.id == medicamentoId }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            dataStore.toggleDarkMode(!darkModeEnabled.value)
        }
    }

    fun toggleNotifications() {
        viewModelScope.launch {
            val newState = !notificationsEnabled.value
            dataStore.toggleNotifications(newState)

            if (newState) {
                _medicamentos.forEach { scheduler.schedule(it) }
            } else {
                _medicamentos.forEach { scheduler.cancel(it) }
            }
        }
    }

    fun resetPreferences() {
        viewModelScope.launch {
            dataStore.toggleDarkMode(false)
            dataStore.toggleNotifications(true)
        }
    }

    fun addFavorite(medicamento: Medicamento) {
        if (!_favoriteMedicamentos.any { it.id == medicamento.id }) {
            _favoriteMedicamentos.add(medicamento)
        }
    }
    fun removeFavorite(medicamento: Medicamento) {
        _favoriteMedicamentos.removeIf { it.id == medicamento.id }
    }
    fun isFavorite(medicamento: Medicamento): Boolean {
        return _favoriteMedicamentos.any { it.id == medicamento.id }
    }
    fun clearFavorites() {
        _favoriteMedicamentos.clear()
    }
}