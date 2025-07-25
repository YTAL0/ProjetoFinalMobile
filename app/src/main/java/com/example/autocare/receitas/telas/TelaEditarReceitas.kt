package com.example.autocare.telas

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
import com.example.autocare.medicamento.MedicamentoViewModel
import com.example.autocare.receitas.ReceitaMedica
import com.example.autocare.ui.theme.AutoCareTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaEditarReceita(
    receitaId: String,
    medicamentoViewModel: MedicamentoViewModel,
    onReceitaUpdated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val receitas by medicamentoViewModel.receitas.collectAsState()
    val originalReceita = remember(receitaId, receitas) {
        receitas.find { it.id == receitaId }
    }

    if (originalReceita == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Carregando receita...")
        }
        return
    }

    var medicamentoNome by remember { mutableStateOf(originalReceita.medicamentoNome) }
    var dataEmissao by remember { mutableStateOf(originalReceita.dataEmissao) }
    var dataVencimento by remember { mutableStateOf(originalReceita.dataVencimento) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(originalReceita.imageUrl?.let { Uri.parse(it) }) }

    var showDatePickerDialogForEmissao by remember { mutableStateOf(false) }
    var showDatePickerDialogForVencimento by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> selectedImageUri = uri }
    )

    if (showDatePickerDialogForEmissao) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialogForEmissao = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePickerDialogForEmissao = false
                        datePickerState.selectedDateMillis?.let { dataEmissao = it.toFormattedDate() }
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialogForEmissao = false }) { Text("Cancelar") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    if (showDatePickerDialogForVencimento) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialogForVencimento = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePickerDialogForVencimento = false
                        datePickerState.selectedDateMillis?.let { dataVencimento = it.toFormattedDate() }
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialogForVencimento = false }) { Text("Cancelar") }
            }
        ) { DatePicker(state = datePickerState) }
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
            text = "Editar Receita",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(value = medicamentoNome, onValueChange = { medicamentoNome = it }, label = { Text("Nome do Medicamento") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = dataEmissao, onValueChange = {}, label = { Text("Data de Emissão") }, readOnly = true, modifier = Modifier.fillMaxWidth().clickable { showDatePickerDialogForEmissao = true }, trailingIcon = { Icon(Icons.Default.CalendarMonth, "Selecionar Data") })
        OutlinedTextField(value = dataVencimento, onValueChange = {}, label = { Text("Data de Vencimento") }, readOnly = true, modifier = Modifier.fillMaxWidth().clickable { showDatePickerDialogForVencimento = true }, trailingIcon = { Icon(Icons.Default.CalendarMonth, "Selecionar Data") })

        Button(onClick = { imagePickerLauncher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.AddAPhoto, contentDescription = "Alterar Imagem da Receita")
            Spacer(Modifier.width(8.dp))
            Text("Alterar Imagem")
        }

        selectedImageUri?.let { uri ->
            Spacer(modifier = Modifier.height(8.dp))
            AsyncImage(model = uri, contentDescription = "Imagem da receita", modifier = Modifier.size(150.dp).border(1.dp, Color.Gray, MaterialTheme.shapes.small).padding(4.dp), contentScale = ContentScale.Fit)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (medicamentoNome.isBlank() || dataEmissao.isBlank() || dataVencimento.isBlank()) {
                    Toast.makeText(context, "Por favor, preencha todos os campos.", Toast.LENGTH_LONG).show()
                } else {
                    val updatedReceita = originalReceita.copy(
                        medicamentoNome = medicamentoNome,
                        dataEmissao = dataEmissao,
                        dataVencimento = dataVencimento,
                        imageUrl = selectedImageUri?.toString()
                    )
                    medicamentoViewModel.updateReceita(updatedReceita)
                    Toast.makeText(context, "Receita atualizada!", Toast.LENGTH_SHORT).show()
                    onReceitaUpdated()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = "Salvar Alterações")
            Spacer(Modifier.width(8.dp))
            Text("Salvar Alterações")
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
fun TelaEditarReceitaPreview() {
    AutoCareTheme {
        Text("Tela de Edição de Receita")
    }
}
