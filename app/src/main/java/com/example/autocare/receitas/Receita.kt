package com.example.autocare.receitas

import com.google.firebase.firestore.Exclude
import java.util.UUID

data class ReceitaMedica(
    val id: String = UUID.randomUUID().toString(),
    val medicamentoNome: String = "",
    val dataEmissao: String = "",
    val dataVencimento: String = "",
    val imageUrl: String? = null,

    @get:Exclude
    val imagemResId: Int? = null
)
