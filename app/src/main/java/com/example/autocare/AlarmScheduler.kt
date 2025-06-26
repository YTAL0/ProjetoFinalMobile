package com.example.autocare

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun schedule(medicamento: Medicamento) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Log.d("AutoCareDebug", "Verificando a permissão CAN_SCHEDULE_EXACT_ALARMS.")
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.e("AutoCareDebug", "ERRO: A permissão para agendar alarmes exatos foi NEGADA pelo sistema. O agendamento foi abortado.")
                return
            }
        }
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("EXTRA_MEDICAMENTO_NOME", medicamento.nome)
            putExtra("EXTRA_MEDICAMENTO_ID", medicamento.id.hashCode())
        }

        val proximoHorario = calcularProximoHorario(medicamento.frequencia.primeiraHora, medicamento.frequencia.intervaloHoras)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            proximoHorario,
            PendingIntent.getBroadcast(
                context,
                medicamento.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
        Log.d("AutoCareDebug", "Alarme AGENDADO para ${medicamento.nome} no horário ${Date(proximoHorario)}")
    }

    fun cancel(medicamento: Medicamento) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                medicamento.id.hashCode(),
                Intent(context, NotificationReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    private fun calcularProximoHorario(primeiraHora: String, intervaloHoras: Int): Long {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val horaInicial = LocalTime.parse(primeiraHora, formatter)

        val agora = Calendar.getInstance()

        val proximoHorario = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, horaInicial.hour)
            set(Calendar.MINUTE, horaInicial.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        while (proximoHorario.before(agora)) {
            proximoHorario.add(Calendar.HOUR_OF_DAY, intervaloHoras)
        }

        return proximoHorario.timeInMillis
    }
}