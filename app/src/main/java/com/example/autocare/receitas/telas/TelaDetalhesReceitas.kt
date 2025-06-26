package com.example.autocare.receitas.telas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.autocare.receitas.ReceitaMedica
import com.example.autocare.receitas.getSampleReceitasMedicas
import com.example.autocare.ui.theme.AutoCareTheme

@Composable
fun TelaDetalhesReceita(
    receita: ReceitaMedica,
    modifier: Modifier = Modifier
) {

    var showFullScreenImage by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        receita.imagemResId?.let { imageRes ->
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Foto da receita para ${receita.medicamentoNome}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clickable { showFullScreenImage = true }
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Fit
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Medicamento Referente",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = receita.medicamentoNome,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "Data de Emissão: ${receita.dataEmissao}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Data de Vencimento: ${receita.dataVencimento}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }

    if (showFullScreenImage) {
        Dialog(
            onDismissRequest = { showFullScreenImage = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable { showFullScreenImage = false }
            ) {
                receita.imagemResId?.let {
                    Image(
                        painter = painterResource(id = it),
                        contentDescription = "Imagem em tela cheia",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TelaDetalhesReceitaPreview() {
    AutoCareTheme {
        TelaDetalhesReceita(
            receita = getSampleReceitasMedicas().first()
        )
    }
}