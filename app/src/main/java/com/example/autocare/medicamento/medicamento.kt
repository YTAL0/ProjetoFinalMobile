package com.example.autocare

data class Frequencia(
    val intervaloHoras: Int,
    val primeiraHora: String
)

data class Medicamento(
    val id: String,
    val nome: String,
    val descricaoCurta: String,
    val imageResId: Int = 0,
    val imageUrl: String? = null,
    val dosagem: String,
    val frequencia: Frequencia,
    val audioResId: Int? = null,
    val audioUrl: String? = null
)
fun getSampleMedicamentos(): List<Medicamento> {
    return listOf(
        Medicamento(
            id = "med_001",
            nome = "Paracetamol",
            descricaoCurta = "Alívio de dor e febre. Uso para dores leves e moderadas.",
            imageResId = R.drawable.paracetamol,
            dosagem = "500mg",
            frequencia = Frequencia(8, "09:00"),
            audioResId = R.raw.paracetamol,
            audioUrl = null
        ),
        Medicamento(
            id = "med_002",
            nome = "Amoxicilina",
            descricaoCurta = "Antibiótico para infecções bacterianas. Necessita de receita.",
            imageResId = R.drawable.amoxilina,
            dosagem = "250mg",
            frequencia = Frequencia(12, "07:00"),
            audioResId = R.raw.paracetamol,
            audioUrl = null
        )
    )
}
