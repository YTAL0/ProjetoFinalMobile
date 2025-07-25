package com.example.autocare.receitas.telas

import android.app.Application
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.autocare.data.AuthRepository
import com.example.autocare.medicamento.MedicamentoViewModel
import com.example.autocare.receitas.ReceitaMedica
import com.example.autocare.ui.theme.AutoCareTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaAdicionarReceita(
    medicamentoViewModel: MedicamentoViewModel,
    onReceitaAdded: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val repository = remember { AuthRepository() }

    var medicamentoNome by remember { mutableStateOf("") }
    var dataEmissao by remember { mutableStateOf("") }
    var dataVencimento by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    var showDatePickerDialogForEmissao by remember { mutableStateOf(false) }
    var showDatePickerDialogForVencimento by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> selectedImageUri = uri }
    )

    if (showDatePickerDialogForEmissao) {
        val datePickerState = rememberDatePickerState()
        val confirmEnabled = remember { derivedStateOf { datePickerState.selectedDateMillis != null } }
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialogForEmissao = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePickerDialogForEmissao = false
                        datePickerState.selectedDateMillis?.let { millis ->
                            dataEmissao = millis.toFormattedDate()
                        }
                    },
                    enabled = confirmEnabled.value
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialogForEmissao = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showDatePickerDialogForVencimento) {
        val datePickerState = rememberDatePickerState()
        val confirmEnabled = remember { derivedStateOf { datePickerState.selectedDateMillis != null } }
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialogForVencimento = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePickerDialogForVencimento = false
                        datePickerState.selectedDateMillis?.let { millis ->
                            dataVencimento = millis.toFormattedDate()
                        }
                    },
                    enabled = confirmEnabled.value
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialogForVencimento = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Adicionar Nova Receita",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = medicamentoNome,
            onValueChange = { medicamentoNome = it },
            label = { Text("Nome do Medicamento na Receita") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = dataEmissao,
            onValueChange = {},
            label = { Text("Data de Emissão") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePickerDialogForEmissao = true },
            trailingIcon = { Icon(Icons.Default.CalendarMonth, "Selecionar Data") }
        )

        OutlinedTextField(
            value = dataVencimento,
            onValueChange = {},
            label = { Text("Data de Vencimento") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePickerDialogForVencimento = true },
            trailingIcon = { Icon(Icons.Default.CalendarMonth, "Selecionar Data") }
        )

        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.AddAPhoto, contentDescription = "Selecionar Imagem da Receita")
            Spacer(Modifier.width(8.dp))
            Text("Selecionar Imagem da Receita")
        }

        selectedImageUri?.let { uri ->
            Spacer(modifier = Modifier.height(8.dp))
            AsyncImage(
                model = uri,
                contentDescription = "Imagem da receita selecionada",
                modifier = Modifier
                    .size(150.dp)
                    .border(1.dp, Color.Gray, MaterialTheme.shapes.small)
                    .padding(4.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (medicamentoNome.isBlank() || dataEmissao.isBlank() || dataVencimento.isBlank()) {
                    Toast.makeText(context, "Por favor, preencha todos os campos.", Toast.LENGTH_LONG).show()
                } else {
                    isLoading = true
                    scope.launch {
                        val imageUrl = selectedImageUri?.let { repository.uploadFileAndGetUrl(it, "receitas") }

                        val novaReceita = ReceitaMedica(
                            id = UUID.randomUUID().toString(),
                            medicamentoNome = medicamentoNome,
                            dataEmissao = dataEmissao,
                            dataVencimento = dataVencimento,
                            imageUrl = imageUrl
                        )

                        medicamentoViewModel.addReceita(novaReceita)

                        launch(Dispatchers.Main) {
                            isLoading = false
                            Toast.makeText(context, "Receita para '${medicamentoNome}' adicionada!", Toast.LENGTH_SHORT).show()
                            onReceitaAdded()
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Icon(Icons.Default.Save, contentDescription = "Salvar Receita")
                Spacer(Modifier.width(8.dp))
                Text("Salvar Receita")
            }
        }
    }
}

private fun Long.toFormattedDate(): String {
    val date = Date(this)
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    format.timeZone = TimeZone.getTimeZone("UTC")
    return format.format(date)
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun TelaAdicionarReceitaPreview() {
    AutoCareTheme {
        TelaAdicionarReceita(
            medicamentoViewModel = MedicamentoViewModel(LocalContext.current.applicationContext as Application),
            onReceitaAdded = {}
        )
    }
}
