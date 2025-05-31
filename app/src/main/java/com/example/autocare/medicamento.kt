package com.example.autocare

data class Medicamento(
    val id: String,
    val nome: String,
    val descricaoCurta: String,
    val imageResId: Int,
    val dosagem: String,
    val frequencia: String,
    val audioResId: Int? = null
)


fun getSampleMedicamentos(): List<Medicamento> {
    return listOf(
        Medicamento(
            id = "med_001",
            nome = "Paracetamol",
            descricaoCurta = "Alívio de dor e febre. Uso para dores leves e moderadas.",
            imageResId = R.drawable.paracetamol,
            dosagem = "500mg",
            frequencia = "8 em 8 horas",
            audioResId = R.raw.paracetamol
        ),
        Medicamento(
            id = "med_002",
            nome = "Amoxicilina",
            descricaoCurta = "Antibiótico para infecções bacterianas. Necessita de receita.",
            imageResId = R.drawable.amoxilina,
            dosagem = "250mg",
            frequencia = "12 em 12 horas",
            audioResId = R.raw.paracetamol
        ),
        Medicamento(
            id = "med_003",
            nome = "Omeprazol",
            descricaoCurta = "Reduz a produção de ácido no estômago. Tomar antes das refeições.",
            imageResId = R.drawable.omeprazol,
            dosagem = "20mg",
            frequencia = "1 vez ao dia",
            audioResId = R.raw.paracetamol
        ),
        Medicamento(
            id = "med_004",
            nome = "Dipirona",
            descricaoCurta = "Analgésico e antipirético. Cuidado com reações alérgicas.",
            imageResId = R.drawable.dipirona,
            dosagem = "500mg",
            frequencia = "6 em 6 horas",
            audioResId = R.raw.paracetamol
        ),
        Medicamento(
            id = "med_005",
            nome = "Sinvastatina",
            descricaoCurta = "Controle do colesterol alto. Geralmente tomado à noite.",
            imageResId = R.drawable.sivastatina,
            dosagem = "40mg",
            frequencia = "1 vez ao dia (à noite)",
            audioResId = R.raw.paracetamol
        )
    )
}
