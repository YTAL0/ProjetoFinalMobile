package com.example.autocare

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MedicamentoViewModel : ViewModel() {


    private val _favoriteMedicamentos = mutableStateListOf<Medicamento>()
    val favoriteMedicamentos: List<Medicamento> = _favoriteMedicamentos


    var darkModeEnabled by mutableStateOf(false)
        private set

    var notificationsEnabled by mutableStateOf(true)
        private set


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
    }


    fun clearFavorites() {
        _favoriteMedicamentos.clear()
    }


    fun resetPreferences() {
        darkModeEnabled = false
        notificationsEnabled = true

    }
}
