package com.example.autocare.medicamento

import com.google.firebase.firestore.Exclude
import java.util.UUID

data class Frequencia(
    val intervaloHoras: Int = 8,
    val primeiraHora: String = ""
)

data class Medicamento(

    val id: String = UUID.randomUUID().toString(),
    val nome: String = "",
    val descricaoCurta: String = "",
    val dosagem: String = "",
    val frequencia: Frequencia = Frequencia(),
    val imageUrl: String? = null,
    val audioUrl: String? = null,
    val favorito: Boolean = false,

    @get:Exclude
    val imageResId: Int? = null,

    @get:Exclude
    val audioResId: Int? = null
)
