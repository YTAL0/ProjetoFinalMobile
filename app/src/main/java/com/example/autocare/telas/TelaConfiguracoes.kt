package com.example.autocare

import android.annotation.SuppressLint
import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autocare.com.example.autocare.medicamento.MedicamentoViewModel
import com.example.autocare.ui.theme.AutoCareTheme

@Composable
fun TelaConfiguracoes(
    medicamentoViewModel: MedicamentoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Configurações do Aplicativo",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )


        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Preferências", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(16.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DarkMode, contentDescription = "Modo Escuro")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Modo Escuro", fontSize = 16.sp)
                    }
                    Switch(
                        checked = medicamentoViewModel.darkModeEnabled,
                        onCheckedChange = {
                            medicamentoViewModel.toggleDarkMode()
                            Toast.makeText(context, "Modo Escuro: ${if (medicamentoViewModel.darkModeEnabled) "Ativado" else "Desativado"}", Toast.LENGTH_SHORT).show()

                        }
                    )
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notificações")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Receber Notificações", fontSize = 16.sp)
                    }
                    Switch(
                        checked = medicamentoViewModel.notificationsEnabled,
                        onCheckedChange = {
                            medicamentoViewModel.toggleNotifications()
                            Toast.makeText(context, "Notificações: ${if (medicamentoViewModel.notificationsEnabled) "Ativadas" else "Desativadas"}", Toast.LENGTH_SHORT).show()

                        }
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Ações", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(16.dp))


                Button(
                    onClick = {
                        medicamentoViewModel.clearFavorites()
                        Toast.makeText(context, "Favoritos limpos!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = "Limpar Favoritos")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Limpar Favoritos")
                }
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        medicamentoViewModel.resetPreferences()
                        Toast.makeText(context, "Preferências redefinidas!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Redefinir Preferências")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Redefinir Preferências")
                }
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, widthDp = 360)
@Composable
fun TelaConfiguracoesPreview() {
    AutoCareTheme {
        TelaConfiguracoes(medicamentoViewModel = MedicamentoViewModel(LocalContext.current.applicationContext as Application))
    }
}
