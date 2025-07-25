package com.example.autocare.receitas.telas

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.autocare.R
import com.example.autocare.receitas.ReceitaMedica
import com.example.autocare.ui.theme.AutoCareTheme

@Composable
fun TelaDetalhesReceita(
    receita: ReceitaMedica,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showFullScreenImage by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar Exclusão") },
            text = { Text("Tem certeza que deseja excluir a receita para '${receita.medicamentoNome}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteClick(receita.id)
                        showDeleteDialog = false
                        Toast.makeText(context, "Receita excluída.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (!receita.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = Uri.parse(receita.imageUrl),
                contentDescription = "Foto da receita para ${receita.medicamentoNome}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clickable { showFullScreenImage = true }
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Fit,
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
                        .height(250.dp)
                        .clickable { showFullScreenImage = true }
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Fit
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sem Imagem", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
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


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { onEditClick(receita.id) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Editar Receita")
                Spacer(Modifier.width(8.dp))
                Text("Editar")
            }
            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Excluir Receita")
                Spacer(Modifier.width(8.dp))
                Text("Excluir")
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
                    .clickable { showFullScreenImage = false },
                contentAlignment = Alignment.Center
            ) {
                if (!receita.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = Uri.parse(receita.imageUrl),
                        contentDescription = "Imagem em tela cheia",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    val localImageResId = receita.imagemResId
                    if (localImageResId != null) {
                        Image(
                            painter = painterResource(id = localImageResId),
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
}


@Preview(showBackground = true)
@Composable
fun TelaDetalhesReceitaPreview() {
    AutoCareTheme {
        TelaDetalhesReceita(
            receita = ReceitaMedica(
                id = "rec_preview",
                medicamentoNome = "Paracetamol 750mg",
                dataEmissao = "24/07/2025",
                dataVencimento = "24/08/2025",
                imagemResId = R.drawable.receitas_1
            ),
            onEditClick = {},
            onDeleteClick = {}
        )
    }
}
