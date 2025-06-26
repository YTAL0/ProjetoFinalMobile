package com.example.autocare.com.example.autocare.medicamento

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.autocare.AlarmScheduler
import com.example.autocare.Medicamento
import com.example.autocare.getSampleMedicamentos

class MedicamentoViewModel(application: Application) : AndroidViewModel(application) {


    private val scheduler = AlarmScheduler(application)

    private val _medicamentos = mutableStateListOf<Medicamento>().apply {
        addAll(getSampleMedicamentos())
    }
    val medicamentos: List<Medicamento> = _medicamentos

    private val _favoriteMedicamentos = mutableStateListOf<Medicamento>()
    val favoriteMedicamentos: List<Medicamento> = _favoriteMedicamentos

    var darkModeEnabled by mutableStateOf(false)
        private set

    var notificationsEnabled by mutableStateOf(true)
        private set

    init {
        _medicamentos.forEach { scheduler.schedule(it) }
    }

    fun addMedicamento(medicamento: Medicamento) {
        Log.d("AutoCareDebug", "ViewModel: DENTRO de addMedicamento. Tentando adicionar ${medicamento.nome}")

        _medicamentos.add(medicamento)
        Log.d("AutoCareDebug", "ViewModel: Checando se notificações estão ativadas. Valor: $notificationsEnabled")

        if (notificationsEnabled) {
            scheduler.schedule(medicamento)
        } else {
            Log.d("AutoCareDebug", "ViewModel: Notificações desativadas, agendamento PULADO.")
        }
    }

    fun updateMedicamento(updatedMedicamento: Medicamento) {
        val index = _medicamentos.indexOfFirst { it.id == updatedMedicamento.id }
        if (index != -1) {
            _medicamentos[index] = updatedMedicamento

            if (notificationsEnabled) {
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
        medicamento?.let {
            scheduler.cancel(it)
        }

        _medicamentos.removeIf { it.id == medicamentoId }
        _favoriteMedicamentos.removeIf { it.id == medicamentoId }
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

    fun toggleDarkMode() {
        darkModeEnabled = !darkModeEnabled
    }


    fun toggleNotifications() {
        notificationsEnabled = !notificationsEnabled
        if (notificationsEnabled) {

            _medicamentos.forEach { scheduler.schedule(it) }
        } else {

            _medicamentos.forEach { scheduler.cancel(it) }
        }
    }

    fun clearFavorites() {
        _favoriteMedicamentos.clear()
    }

    fun resetPreferences() {
        darkModeEnabled = false
        notificationsEnabled = true
    }
}