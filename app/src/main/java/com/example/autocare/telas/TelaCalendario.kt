package com.example.autocare.telas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.autocare.medicamento.Frequencia
import com.example.autocare.medicamento.Medicamento
import com.example.autocare.ui.theme.AutoCareTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

data class Agendamento(
    val nomeMedicamento: String,
    val horario: String
)

@Composable
fun TelaCalendario(
    medicamentos: List<Medicamento>,
    getAgendamentosParaDia: (LocalDate, List<Medicamento>) -> List<Agendamento>
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val agendamentosDoDia by remember(selectedDate, medicamentos) {
        derivedStateOf { getAgendamentosParaDia(selectedDate, medicamentos) }
    }

    val diasComAgendamento by remember(currentMonth, medicamentos) {
        derivedStateOf {
            val dias = mutableSetOf<LocalDate>()
            val diasNoMes = currentMonth.lengthOfMonth()
            for (i in 1..diasNoMes) {
                val data = currentMonth.atDay(i)
                if (getAgendamentosParaDia(data, medicamentos).isNotEmpty()) {
                    dias.add(data)
                }
            }
            dias
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CalendarioHeader(
            yearMonth = currentMonth,
            onPrevMonth = { currentMonth = currentMonth.minusMonths(1) },
            onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        CalendarioGrid(
            yearMonth = currentMonth,
            selectedDate = selectedDate,
            diasComAgendamento = diasComAgendamento,
            onDateSelected = { selectedDate = it }
        )
        Spacer(modifier = Modifier.height(16.dp))
        AgendaDoDia(
            selectedDate = selectedDate,
            agendamentos = agendamentosDoDia
        )
    }
}

@Composable
fun CalendarioHeader(
    yearMonth: YearMonth,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("pt", "BR"))
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPrevMonth) {
            Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Mês Anterior")
        }
        Text(
            text = yearMonth.format(formatter).replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onNextMonth) {
            Icon(Icons.Default.ArrowForwardIos, contentDescription = "Próximo Mês")
        }
    }
}

@Composable
fun CalendarioGrid(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    diasComAgendamento: Set<LocalDate>,
    onDateSelected: (LocalDate) -> Unit
) {
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfMonth = yearMonth.atDay(1).dayOfWeek
    val emptyDays = (firstDayOfMonth.value % 7)

    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            val diasDaSemana = DayOfWeek.values().map { it.getDisplayName(TextStyle.SHORT, Locale("pt", "BR")) }
            diasDaSemana.forEach { dia ->
                Text(
                    text = dia.uppercase(),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Grid dos dias
        var dayCounter = 1
        repeat(6) { // Para no máximo 6 semanas
            if (dayCounter > daysInMonth) return@repeat
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) { dayIndex ->
                    if (it == 0 && dayIndex < emptyDays) {
                        Box(modifier = Modifier.weight(1f)) // Dias vazios
                    } else if (dayCounter <= daysInMonth) {
                        val date = yearMonth.atDay(dayCounter)
                        DiaDoCalendario(
                            date = date,
                            isSelected = date == selectedDate,
                            temAgendamento = date in diasComAgendamento,
                            onClick = { onDateSelected(date) }
                        )
                        dayCounter++
                    } else {
                        Box(modifier = Modifier.weight(1f)) // Espaços vazios no final
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.DiaDoCalendario(
    date: LocalDate,
    isSelected: Boolean,
    temAgendamento: Boolean,
    onClick: () -> Unit
) {
    val isToday = date == LocalDate.now()
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.secondaryContainer
        else -> Color.Transparent
    }
    val contentColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isToday -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(
                width = if (isToday && !isSelected) 1.dp else 0.dp,
                color = if (isToday && !isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = date.dayOfMonth.toString(),
                color = contentColor,
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
            )
            if (temAgendamento) {
                Icon(
                    imageVector = Icons.Default.Circle,
                    contentDescription = "Tem agendamento",
                    modifier = Modifier.size(6.dp),
                    tint = if (isSelected) contentColor else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun AgendaDoDia(
    selectedDate: LocalDate,
    agendamentos: List<Agendamento>
) {
    val formatter = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM", Locale("pt", "BR"))
    Column {
        Text(
            text = selectedDate.format(formatter).replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        if (agendamentos.isEmpty()) {
            Text(
                text = "Nenhum medicamento agendado para este dia.",
                modifier = Modifier.padding(top = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn {
                items(agendamentos.sortedBy { it.horario }) { agendamento ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = agendamento.horario,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(60.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = agendamento.nomeMedicamento,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TelaCalendarioPreview() {
    AutoCareTheme {
        TelaCalendario(
            medicamentos = listOf(
                Medicamento(
                    nome = "Paracetamol",
                    frequencia = Frequencia(8, "09:00")
                ),
                Medicamento(
                    nome = "Amoxicilina",
                    frequencia = Frequencia(12, "07:00")
                )
            ),
            getAgendamentosParaDia = { _, _ ->
                listOf(
                    Agendamento("Paracetamol", "09:00"),
                    Agendamento("Paracetamol", "17:00")
                )
            }
        )
    }
}
