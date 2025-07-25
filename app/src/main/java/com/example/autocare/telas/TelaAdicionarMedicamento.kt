package com.example.autocare.telas

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.autocare.data.AuthRepository
import com.example.autocare.medicamento.Frequencia
import com.example.autocare.medicamento.Medicamento
import com.example.autocare.medicamento.MedicamentoViewModel
import com.example.autocare.ui.theme.AutoCareTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaAdicionarMedicamento(
    medicamentoViewModel: MedicamentoViewModel,
    onMedicamentoAdded: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val repository = remember { AuthRepository() }

    var nome by remember { mutableStateOf("") }
    var descricaoCurta by remember { mutableStateOf("") }
    var dosagem by remember { mutableStateOf("") }
    var primeiraHora by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedAudioUri by remember { mutableStateOf<Uri?>(null) }
    val intervaloOptions = (1..24).map { "A cada $it hora(s)" }
    var selectedIntervalText by remember { mutableStateOf("") }
    var intervaloHoras by remember { mutableStateOf(0) }
    var isIntervaloExpanded by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE),
        is24Hour = true
    )

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> selectedImageUri = uri }
    )
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            selectedAudioUri = uri
            if (uri != null) {
                val fileName = uri.lastPathSegment ?: "Áudio selecionado"
                Toast.makeText(context, "Áudio: $fileName", Toast.LENGTH_SHORT).show()
            }
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Adicionar Novo Medicamento",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome do Medicamento") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = descricaoCurta, onValueChange = { descricaoCurta = it }, label = { Text("Descrição Curta") }, modifier = Modifier.fillMaxWidth(), minLines = 3, maxLines = 5)
        OutlinedTextField(value = dosagem, onValueChange = { dosagem = it }, label = { Text("Dosagem (ex: 500mg, 10ml)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

        ExposedDropdownMenuBox(
            expanded = isIntervaloExpanded,
            onExpandedChange = { isIntervaloExpanded = it }
        ) {
            OutlinedTextField(
                value = selectedIntervalText,
                onValueChange = {},
                readOnly = true,
                label = { Text("Intervalo de tempo") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isIntervaloExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = isIntervaloExpanded,
                onDismissRequest = { isIntervaloExpanded = false }
            ) {
                intervaloOptions.forEachIndexed { index, text ->
                    DropdownMenuItem(
                        text = { Text(text) },
                        onClick = {
                            selectedIntervalText = text
                            intervaloHoras = (index + 1)
                            isIntervaloExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = primeiraHora,
            onValueChange = {},
            label = { Text("Primeira Hora (HH:MM)") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showTimePicker = true },
            singleLine = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Selecionar Hora"
                )
            }
        )

        if (showTimePicker) {
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                title = { Text("Selecione a Hora") },
                text = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        TimePicker(state = timePickerState)
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            primeiraHora = String.format(Locale.getDefault(), "%02d:%02d", timePickerState.hour, timePickerState.minute)
                            showTimePicker = false
                        }
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
                }
            )
        }


        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.AddAPhoto, contentDescription = "Selecionar Foto da Galeria")
            Spacer(Modifier.width(8.dp))
            Text("Selecionar Foto da Galeria")
        }

        selectedImageUri?.let { uri ->
            Spacer(modifier = Modifier.height(8.dp))
            AsyncImage(
                model = uri,
                contentDescription = "Foto selecionada",
                modifier = Modifier
                    .size(120.dp)
                    .border(1.dp, Color.Gray, MaterialTheme.shapes.small)
                    .padding(4.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = { audioPickerLauncher.launch("audio/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Audiotrack, contentDescription = "Selecionar Áudio do Dispositivo")
            Spacer(Modifier.width(8.dp))
            Text("Selecionar Áudio do Dispositivo")
        }

        selectedAudioUri?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Áudio selecionado: ${it.lastPathSegment ?: "Arquivo de áudio"}", color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (nome.isBlank() || descricaoCurta.isBlank() || dosagem.isBlank() ||
                    intervaloHoras <= 0 || primeiraHora.isBlank()
                ) {
                    Toast.makeText(context, "Por favor, preencha todos os campos.", Toast.LENGTH_LONG).show()
                } else {
                    isLoading = true
                    scope.launch {
                        val imageUrl = selectedImageUri?.let { repository.uploadFileAndGetUrl(it, "images") }
                        val audioUrl = selectedAudioUri?.let { repository.uploadFileAndGetUrl(it, "audio") }

                        val newMedicamento = Medicamento(
                            id = UUID.randomUUID().toString(),
                            nome = nome,
                            descricaoCurta = descricaoCurta,
                            dosagem = dosagem,
                            frequencia = Frequencia(intervaloHoras, primeiraHora),
                            imageUrl = imageUrl,
                            audioUrl = audioUrl
                        )

                        medicamentoViewModel.addMedicamento(newMedicamento)


                        launch(kotlinx.coroutines.Dispatchers.Main) {
                            isLoading = false
                            Toast.makeText(context, "Medicamento '${nome}' adicionado!", Toast.LENGTH_SHORT).show()
                            onMedicamentoAdded()
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
                Icon(Icons.Default.Save, contentDescription = "Salvar Medicamento")
                Spacer(Modifier.width(8.dp))
                Text("Salvar Medicamento")
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, widthDp = 360)
@Composable
fun TelaAdicionarMedicamentoPreview() {
    AutoCareTheme {
        TelaAdicionarMedicamento(
            medicamentoViewModel = MedicamentoViewModel(LocalContext.current.applicationContext as Application),
            onMedicamentoAdded = {}
        )
    }
}
