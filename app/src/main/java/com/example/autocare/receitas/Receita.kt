package com.example.autocare.receitas
import com.example.autocare.R

data class ReceitaMedica(
    val id: String,
    val medicamentoNome: String,
    val dataEmissao: String,
    val dataVencimento: String,
    @DrawableRes val imagemResId: Int? = null
)

annotation class DrawableRes

fun getSampleReceitasMedicas(): List<ReceitaMedica> {
    return listOf(
        ReceitaMedica(
            id = "rec001",
            medicamentoNome = "Amoxicilina 500mg",
            dataEmissao = "15/05/2025",
            dataVencimento = "15/06/2025",
            imagemResId = R.drawable.receitas_2
        ),
        ReceitaMedica(
            id = "rec002",
            medicamentoNome = "Loratadina 10mg",
            dataEmissao = "20/06/2025",
            dataVencimento = "20/12/2025",
            imagemResId = R.drawable.receitas_1
        )
    )
}