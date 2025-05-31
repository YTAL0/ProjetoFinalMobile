package com.example.autocare

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autocare.ui.theme.AutoCareTheme

data class FAQItem(
    val id: String,
    val question: String,
    val answer: String
)

// Lista de FAQs de exemplo
fun getSampleFAQs(): List<FAQItem> {
    return listOf(
        FAQItem(
            id = "faq_1",
            question = "Como faço para adicionar um novo medicamento?",
            answer = "Para adicionar um novo medicamento, vá para a tela inicial, clique no botão '+' (futuramente) ou em um item para ver detalhes, e lá você poderá registrar um novo medicamento ou editar um existente."
        ),
        FAQItem(
            id = "faq_2",
            question = "Como configuro lembretes para meus remédios?",
            answer = "Na tela de detalhes de cada medicamento, você encontrará opções para definir horários e frequências. O aplicativo enviará notificações baseadas nessas configurações."
        ),
        FAQItem(
            id = "faq_3",
            question = "Posso usar o aplicativo em modo offline?",
            answer = "Sim, as informações dos seus medicamentos e lembretes são armazenadas localmente e podem ser acessadas offline. A sincronização com a nuvem (futuramente) exigirá conexão."
        ),
        FAQItem(
            id = "faq_4",
            question = "Como faço para limpar meus favoritos?",
            answer = "Você pode limpar sua lista de medicamentos favoritos acessando a Tela de Configurações, onde encontrará um botão específico para essa ação."
        ),
        FAQItem(
            id = "faq_5",
            question = "Onde posso encontrar informações sobre efeitos colaterais?",
            answer = "Na tela de detalhes de cada medicamento, haverá uma seção dedicada a informações adicionais, incluindo possíveis efeitos colaterais e interações (funcionalidade futura)."
        )
    )
}

@Composable
fun TelaAjudaSuporte(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val faqs = getSampleFAQs()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ajuda e Suporte",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )


        Text(
            text = "Perguntas Frequentes",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(faqs) { faq ->
                FAQCard(faq = faq)
            }
        }

        // Botão para Enviar Mensagem para Suporte
        Button(
            onClick = {
                Toast.makeText(context, "TOC TOC " +
                        "-QUEM É?" +
                        "É O SUPORTE", Toast.LENGTH_LONG).show()

            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(Icons.Default.MailOutline, contentDescription = "Enviar mensagem de suporte")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Enviar Mensagem para Suporte")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAQCard(faq: FAQItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(durationMillis = 300))
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.question,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Recolher" else "Expandir"
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = faq.answer,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun TelaAjudaSuportePreview() {
    AutoCareTheme {
        TelaAjudaSuporte()
    }
}
