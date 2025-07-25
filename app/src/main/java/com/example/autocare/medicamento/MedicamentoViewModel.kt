package com.example.autocare.medicamento

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.autocare.AlarmScheduler
import com.example.autocare.DataStore
import com.example.autocare.data.FirebaseClient
import com.example.autocare.receitas.ReceitaMedica
import com.example.autocare.telas.Agendamento
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MedicamentoViewModel(application: Application) : AndroidViewModel(application) {

    private val scheduler = AlarmScheduler(application)
    private val dataStore = DataStore(application)
    private val db: FirebaseFirestore = FirebaseClient.firestore
    private val auth: FirebaseAuth = FirebaseClient.auth
    private val TAG = "AutoCareDebug"

    private var medicamentosCollection: CollectionReference? = null
    private var receitasCollection: CollectionReference? = null

    private val _medicamentos = MutableStateFlow<List<Medicamento>>(emptyList())
    val medicamentos: StateFlow<List<Medicamento>> = _medicamentos.asStateFlow()

    val favoriteMedicamentos: StateFlow<List<Medicamento>> = _medicamentos.map { meds ->
        meds.filter { it.favorito }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _receitas = MutableStateFlow<List<ReceitaMedica>>(emptyList())
    val receitas: StateFlow<List<ReceitaMedica>> = _receitas.asStateFlow()

    val darkModeEnabled: StateFlow<Boolean> = dataStore.darkModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val notificationsEnabled: StateFlow<Boolean> = dataStore.notificationsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)


    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                setupListenersForUser(user.uid)
            } else {
                _medicamentos.value = emptyList()
                _receitas.value = emptyList()
                medicamentosCollection = null
                receitasCollection = null
            }
        }
    }

    private fun setupListenersForUser(userId: String) {
        medicamentosCollection = db.collection("usuarios").document(userId).collection("medicamentos")
        receitasCollection = db.collection("usuarios").document(userId).collection("receitas")

        medicamentosCollection?.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e(TAG, "Listen de medicamentos falhou.", e)
                return@addSnapshotListener
            }
            snapshot?.let { querySnapshot ->
                val medList = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Medicamento::class.java)?.copy(id = document.id)
                }
                _medicamentos.value = medList
            }
        }

        receitasCollection?.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e(TAG, "Listen de receitas falhou.", e)
                return@addSnapshotListener
            }
            snapshot?.let { querySnapshot ->
                val receitaList = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(ReceitaMedica::class.java)?.copy(id = document.id)
                }
                _receitas.value = receitaList
            }
        }
    }


    fun getAgendamentosParaDia(dataSelecionada: LocalDate, todosMedicamentos: List<Medicamento>): List<Agendamento> {
        val agendamentos = mutableListOf<Agendamento>()
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        for (medicamento in todosMedicamentos) {

            if (medicamento.frequencia.primeiraHora.isBlank() || medicamento.frequencia.intervaloHoras <= 0) {
                continue
            }

            try {
                val primeiraHora = LocalTime.parse(medicamento.frequencia.primeiraHora, timeFormatter)
                val intervalo = medicamento.frequencia.intervaloHoras

                val dosesNoDia = 24 / intervalo

                for (i in 0 until dosesNoDia) {
                    val horaDaDose = primeiraHora.plusHours(intervalo.toLong() * i)
                    agendamentos.add(
                        Agendamento(
                            nomeMedicamento = medicamento.nome,
                            horario = horaDaDose.format(timeFormatter)
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao calcular agendamento para ${medicamento.nome}: ${e.message}")
            }
        }
        return agendamentos
    }

    fun addReceita(receita: ReceitaMedica) {
        receitasCollection?.document(receita.id)?.set(receita)
            ?.addOnFailureListener { e -> Log.e(TAG, "FALHA ao adicionar a receita.", e) }
    }

    fun updateReceita(updatedReceita: ReceitaMedica) {
        receitasCollection?.document(updatedReceita.id)?.set(updatedReceita)
            ?.addOnFailureListener { e -> Log.e(TAG, "FALHA ao atualizar a receita.", e) }
    }

    fun removeReceita(receitaId: String) {
        receitasCollection?.document(receitaId)?.delete()
            ?.addOnFailureListener { e -> Log.e(TAG, "FALHA ao remover a receita.", e) }
    }

    fun addMedicamento(medicamento: Medicamento) {
        medicamentosCollection?.document(medicamento.id)?.set(medicamento)
            ?.addOnSuccessListener {
                if (notificationsEnabled.value) scheduler.schedule(medicamento)
            }
    }

    fun updateMedicamento(updatedMedicamento: Medicamento) {
        medicamentosCollection?.document(updatedMedicamento.id)?.set(updatedMedicamento)
            ?.addOnSuccessListener {
                if (notificationsEnabled.value) scheduler.schedule(updatedMedicamento)
            }
    }

    fun removeMedicamento(medicamentoId: String) {
        val medicamento = _medicamentos.value.find { it.id == medicamentoId }
        medicamentosCollection?.document(medicamentoId)?.delete()
            ?.addOnSuccessListener {
                medicamento?.let { scheduler.cancel(it) }
            }
    }

    fun addFavorite(medicamento: Medicamento) {
        medicamentosCollection?.document(medicamento.id)?.update("favorito", true)
    }

    fun removeFavorite(medicamento: Medicamento) {
        medicamentosCollection?.document(medicamento.id)?.update("favorito", false)
    }

    fun isFavorite(medicamento: Medicamento): Boolean {
        return medicamento.favorito
    }

    fun getReceitaById(id: String): ReceitaMedica? {
        return _receitas.value.find { it.id == id }
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
                _medicamentos.value.forEach { scheduler.schedule(it) }
            } else {
                _medicamentos.value.forEach { scheduler.cancel(it) }
            }
        }
    }

    fun clearFavorites() {
        viewModelScope.launch {
            favoriteMedicamentos.value.forEach { med ->
                removeFavorite(med)
            }
        }
    }

    fun resetPreferences() {
        viewModelScope.launch {
            dataStore.toggleDarkMode(false)
            dataStore.toggleNotifications(true)
        }
    }
}
