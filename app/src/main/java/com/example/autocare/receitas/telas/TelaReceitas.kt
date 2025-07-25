package com.example.autocare.receitas.telas

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.autocare.R
import com.example.autocare.receitas.ReceitaMedica
import com.example.autocare.ui.theme.AutoCareTheme

@Composable
fun TelaReceitas(
    receitas: List<ReceitaMedica>,
    onReceitaClick: (String) -> Unit,
    onAddReceitaClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddReceitaClick,
                shape = CircleShape,
                containerColor = Color(0xFFFF5555)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Receita")
            }
        }
    ) { paddingValues ->
        var searchQuery by remember { mutableStateOf("") }

        val filteredReceitas = remember(receitas, searchQuery) {
            if (searchQuery.isBlank()) {
                receitas
            } else {
                receitas.filter {
                    it.medicamentoNome.contains(searchQuery, ignoreCase = true)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar receitas...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Ícone de Busca") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (filteredReceitas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isBlank()) "Nenhuma receita adicionada ainda.\nClique no '+' para adicionar uma!" else "Nenhuma receita encontrada para \"$searchQuery\".",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredReceitas) { receita ->
                        ReceitaCard(
                            receita = receita,
                            onClick = { onReceitaClick(receita.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceitaCard(
    receita: ReceitaMedica,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            if (!receita.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = Uri.parse(receita.imageUrl),
                    contentDescription = "Foto da receita para ${receita.medicamentoNome}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.ic_launcher_foreground),
                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground)
                )
            } else {
                val localImageResId = receita.imagemResId
                if (localImageResId != null && localImageResId != 0) {
                    Image(
                        painter = painterResource(id = localImageResId),
                        contentDescription = "Foto da receita para ${receita.medicamentoNome}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Sem Imagem", color = Color.DarkGray)
                    }
                }
            }

            Column(Modifier.padding(16.dp)) {
                Text(
                    text = receita.medicamentoNome,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Emitida em: ${receita.dataEmissao}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Válida até: ${receita.dataVencimento}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TelaReceitasPreview() {
    AutoCareTheme {
        TelaReceitas(
            receitas = listOf(
                ReceitaMedica(
                    id = "rec001",
                    medicamentoNome = "Amoxicilina 500mg",
                    dataEmissao = "15/05/2025",
                    dataVencimento = "15/06/2025",
                    imagemResId = R.drawable.receitas_2
                )
            ),
            onReceitaClick = {},
            onAddReceitaClick = {}
        )
    }
}
