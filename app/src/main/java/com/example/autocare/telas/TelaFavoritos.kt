package com.example.autocare

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.autocare.ui.theme.AutoCareTheme

@Composable
fun TelaFavoritos(
    medicamentosFavoritos: List<Medicamento>,
    onRemoveFavoriteClick: (Medicamento) -> Unit,
    onMedicamentoClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (medicamentosFavoritos.isEmpty()) {
        Column(
            modifier = modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Nenhum medicamento favorito adicionado ainda.",
                fontSize = 18.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(medicamentosFavoritos) { medicamento ->
                FavoriteMedicamentoCard(
                    medicamento = medicamento,
                    onRemoveClick = { onRemoveFavoriteClick(it) },
                    onItemClick = { onMedicamentoClick(it) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteMedicamentoCard(
    medicamento: Medicamento,
    onRemoveClick: (Medicamento) -> Unit,
    onItemClick: (String) -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = { onItemClick(medicamento.id) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {

                if (!medicamento.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = Uri.parse(medicamento.imageUrl),
                        contentDescription = "Imagem de ${medicamento.nome}",
                        modifier = Modifier
                            .size(64.dp)
                            .padding(end = 16.dp),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.ic_launcher_foreground),
                        placeholder = painterResource(id = R.drawable.ic_launcher_foreground)
                    )
                } else if (medicamento.imageResId != 0) {
                    Image(
                        painter = painterResource(id = medicamento.imageResId),
                        contentDescription = "Imagem de ${medicamento.nome}",
                        modifier = Modifier
                            .size(64.dp)
                            .padding(end = 16.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .padding(end = 16.dp)
                            .background(Color.LightGray, shape = MaterialTheme.shapes.small),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Sem Imagem", fontSize = 10.sp, textAlign = TextAlign.Center)
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = medicamento.nome, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = medicamento.dosagem, fontSize = 14.sp, color = Color.Gray)
                }
            }
            IconButton(
                onClick = {
                    onRemoveClick(medicamento)
                    Toast.makeText(context, "${medicamento.nome} removido!", Toast.LENGTH_SHORT).show()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remover dos favoritos",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TelaFavoritosPreview() {
    AutoCareTheme {
        FavoriteMedicamentoCard(
            medicamento = Medicamento(
                id = "prev_001",
                nome = "Vitamina C",
                descricaoCurta = "Suplemento para imunidade.",
                imageResId = R.drawable.paracetamol,
                dosagem = "1000mg",
                frequencia = Frequencia(24, "09:00")
            ),
            onItemClick = {},
            onRemoveClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TelaFavoritosEmptyPreview() {
    AutoCareTheme {
        TelaFavoritos(
            medicamentosFavoritos = emptyList(),
            onRemoveFavoriteClick = {  },
            onMedicamentoClick = {  }
        )
    }
}