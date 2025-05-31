package com.example.autocare

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autocare.ui.theme.AutoCareTheme

@Composable
fun TelaFavoritos(
    medicamentosFavoritos: List<Medicamento>,
    onRemoveFavoriteClick: (Medicamento) -> Unit,
    onMedicamentoClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    if (medicamentosFavoritos.isEmpty()) {
        Column(
            modifier = modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Nenhum medicamento favorito adicionado ainda.",
                fontSize = 18.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Adicione seus medicamentos favoritos na tela de detalhes!",
                fontSize = 14.sp,
                color = Color.LightGray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
                Image(
                    painter = painterResource(id = medicamento.imageResId),
                    contentDescription = "Imagem de ${medicamento.nome}",
                    modifier = Modifier
                        .size(64.dp)
                        .padding(end = 16.dp),
                    contentScale = ContentScale.Crop
                )
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
        val sampleFavorites = listOf(
            Medicamento(
                id = "prev_fav_1",
                nome = "Paracetamol",
                descricaoCurta = "Alívio de dor e febre.",
                imageResId = R.drawable.paracetamol,
                dosagem = "500mg",
                frequencia = "8 em 8 horas"
            ),
            Medicamento(
                id = "prev_fav_2",
                nome = "Omeprazol",
                descricaoCurta = "Reduz a produção de ácido no estômago.",
                imageResId = R.drawable.omeprazol,
                dosagem = "20mg",
                frequencia = "1 vez ao dia"
            )
        )
        TelaFavoritos(
            medicamentosFavoritos = sampleFavorites,
            onRemoveFavoriteClick = {  },
            onMedicamentoClick = {  }
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
