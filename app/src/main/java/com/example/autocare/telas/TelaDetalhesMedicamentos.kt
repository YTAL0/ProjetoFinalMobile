package com.example.autocare.telas

import android.annotation.SuppressLint
import android.app.Application
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.autocare.R
import com.example.autocare.medicamento.Frequencia
import com.example.autocare.medicamento.Medicamento
import com.example.autocare.medicamento.MedicamentoViewModel
import com.example.autocare.ui.theme.AutoCareTheme

@Composable
fun TelaDetalhesMedicamento(
    medicamento: Medicamento,
    navController: NavController,
    medicamentoViewModel: MedicamentoViewModel,
    todosMedicamentos: List<Medicamento>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val isFavorite = medicamento.favorito

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var isAudioReady by remember { mutableStateOf(false) }

    DisposableEffect(medicamento.audioUrl) {

        if (!medicamento.audioUrl.isNullOrBlank()) {
            val player = MediaPlayer().apply {
                try {
                    setDataSource(medicamento.audioUrl)

                    prepareAsync()
                    setOnPreparedListener {
                        isAudioReady = true
                        Log.d("AudioPlayer", "Áudio pronto para tocar.")
                    }
                    setOnCompletionListener {
                        isPlaying = false
                    }
                    setOnErrorListener { mp, what, extra ->
                        Log.e("AudioPlayer", "Erro no MediaPlayer: what=$what, extra=$extra")
                        isAudioReady = false
                        true
                    }
                } catch (e: Exception) {
                    Log.e("AudioPlayer", "Erro ao configurar o DataSource", e)
                }
            }
            mediaPlayer = player
        }

        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!medicamento.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = Uri.parse(medicamento.imageUrl),
                contentDescription = "Imagem de ${medicamento.nome}",
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Fit,
                error = painterResource(id = R.drawable.ic_launcher_foreground),
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground)
            )
        } else {
            val localImageResId = medicamento.imageResId
            if (localImageResId != null && localImageResId != 0) {
                Image(
                    painter = painterResource(id = localImageResId),
                    contentDescription = "Imagem de ${medicamento.nome}",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .padding(bottom = 16.dp)
                        .background(Color.LightGray, shape = MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sem Imagem", color = Color.DarkGray)
                }
            }
        }

        Text(
            text = medicamento.nome,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = medicamento.descricaoCurta,
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Dosagem:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = medicamento.dosagem, fontSize = 16.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Frequência:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "${medicamento.frequencia.intervaloHoras} em ${medicamento.frequencia.intervaloHoras} horas", fontSize = 16.sp)
                Text(text = "Primeira: ${medicamento.frequencia.primeiraHora}", fontSize = 14.sp, color = Color.Gray)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    if (isFavorite) {
                        medicamentoViewModel.removeFavorite(medicamento)
                        Toast.makeText(context, "${medicamento.nome} removido dos favoritos!", Toast.LENGTH_SHORT).show()
                    } else {
                        medicamentoViewModel.addFavorite(medicamento)
                        Toast.makeText(context, "${medicamento.nome} adicionado aos favoritos!", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remover dos favoritos" else "Adicionar aos favoritos"
                )
                Spacer(Modifier.width(8.dp))
                Text(if (isFavorite) "Desfavoritar" else "Favoritar")
            }

            Button(
                onClick = {
                    mediaPlayer?.let { player ->
                        if (player.isPlaying) {
                            player.pause()
                            isPlaying = false
                        } else {
                            player.start()
                            isPlaying = true
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = isAudioReady
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pausar áudio" else "Reproduzir áudio"
                )
                Spacer(Modifier.width(8.dp))
                Text(if (isPlaying) "Pausar" else "Ouvir Explicação")
            }
        }

        Button(
            onClick = { navController.navigate("edit_medicamento_route/${medicamento.id}") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Editar Medicamento")
            Spacer(Modifier.width(8.dp))
            Text("Editar Medicamento")
        }


        Text(
            text = "Outros Medicamentos",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 16.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            val relatedMedicamentos = todosMedicamentos.filter { it.id != medicamento.id }.take(5)
            items(relatedMedicamentos) { relatedItem ->
                RelatedMedicamentoItem(
                    medicamento = relatedItem,
                    onItemClick = { clickedMedicamentoId ->
                        navController.navigate("details/$clickedMedicamentoId") {
                            popUpTo("details/{medicamentoId}") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelatedMedicamentoItem(medicamento: Medicamento, onItemClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(180.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onItemClick(medicamento.id) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!medicamento.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = Uri.parse(medicamento.imageUrl),
                    contentDescription = "Imagem de ${medicamento.nome}",
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Fit,
                    error = painterResource(id = R.drawable.ic_launcher_foreground),
                    placeholder = painterResource(id = R.drawable.ic_launcher_foreground)
                )
            } else {
                val localImageResId = medicamento.imageResId
                if (localImageResId != null && localImageResId != 0) {
                    Image(
                        painter = painterResource(id = localImageResId),
                        contentDescription = "Imagem de ${medicamento.nome}",
                        modifier = Modifier.size(80.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.LightGray, shape = MaterialTheme.shapes.small),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Sem Imagem", fontSize = 10.sp, textAlign = TextAlign.Center)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = medicamento.nome,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = medicamento.dosagem,
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, widthDp = 360)
@Composable
fun TelaDetalhesMedicamentoPreview() {
    AutoCareTheme {
        val sampleMedicamento = Medicamento(
            id = "prev_detail",
            nome = "Ibuprofeno 400mg",
            descricaoCurta = "Analgésico e anti-inflamatório.",
            imageResId = R.drawable.paracetamol,
            dosagem = "400mg",
            frequencia = Frequencia(6, "08:00"),
            favorito = true
        )
        val sampleList = listOf(
            sampleMedicamento,
            Medicamento(id = "prev_2", nome = "Dipirona", dosagem = "500mg"),
            Medicamento(id = "prev_3", nome = "Vitamina C", dosagem = "1g")
        )

        TelaDetalhesMedicamento(
            medicamento = sampleMedicamento,
            navController = rememberNavController(),
            medicamentoViewModel = MedicamentoViewModel(LocalContext.current.applicationContext as Application),
            todosMedicamentos = sampleList
        )
    }
}
