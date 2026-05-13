package com.example.sicemultiplataform.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sicemultiplataform.data.MateriaCarga
import kotlinx.coroutines.delay
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.Instant


@Composable
fun CargaAcademicaScreen(viewModel: SNViewModel) {
    val uiState = viewModel.uiState
    val matricula = uiState.alumno?.matricula

    if (matricula != null) {
        LaunchedEffect(Unit) {
            viewModel.cargarCargaAcademica(matricula)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TimestampBanner(                                    // ← reemplaza el Surface de antes
            lastSyncTimestamp = uiState.fechaActualizacionCarga,
            isOnline          = uiState.isOnline
        )


        if (uiState.isLoading && uiState.cargaAcademica.isNullOrEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            CargaAcademicaSection(lista = uiState.cargaAcademica ?: emptyList())
        }
    }
}

@Composable
fun CargaAcademicaSection(lista: List<MateriaCarga>) {
    if (lista.isEmpty()) return

    val hoy = obtenerDiaActual()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            val hoyDisplay = hoy
                .replace("Miercoles", "Miércoles")
                .replace("Sabado", "Sábado")
            Text("Horario de Hoy ($hoyDisplay)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }

        val materiasHoy = lista
            .filter { it.obtenerHorarioPorDia(hoy).isNotBlank() }
            .sortedBy { it.obtenerHorarioPorDia(hoy) }

        if (materiasHoy.isEmpty()) {
            Text("No tienes clases programadas para hoy.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            materiasHoy.forEach { MateriaCard(materia = it, destacarDia = hoy) }
        }

        Spacer(Modifier.height(16.dp))
        Text("Carga Completa", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        lista.forEach { MateriaCard(materia = it) }
    }
}

@Composable
fun MateriaCard(materia: MateriaCarga, destacarDia: String? = null) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(materia.materia, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Docente: ${materia.docente}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (destacarDia != null) {
                    DiaRow(destacarDia.take(3), materia.obtenerHorarioPorDia(destacarDia), true)
                } else {
                    DiaRow("Lun", materia.lunes)
                    DiaRow("Mar", materia.martes)
                    DiaRow("Mié", materia.miercoles)
                    DiaRow("Jue", materia.jueves)
                    DiaRow("Vie", materia.viernes)
                    if (materia.sabado.isNotBlank()) DiaRow("Sáb", materia.sabado)
                }
            }
        }
    }
}

@Composable
fun DiaRow(nombreDia: String, horario: String, destacado: Boolean = false) {
    if (horario.isNotBlank()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(nombreDia, style = MaterialTheme.typography.bodyMedium, fontWeight = if (destacado) FontWeight.Bold else FontWeight.SemiBold, color = if (destacado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
            Text(horario, style = MaterialTheme.typography.bodyMedium, color = if (destacado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// kotlinx-datetime reemplaza java.util.Calendar
fun obtenerDiaActual(): String {
    val dow = java.time.LocalDate.now().dayOfWeek.value
    return when (dow) {
        1 -> "Lunes"
        2 -> "Martes"
        3 -> "Miercoles"
        4 -> "Jueves"
        5 -> "Viernes"
        6 -> "Sabado"
        else -> ""
    }
}
@Composable
fun TimestampBanner(lastSyncTimestamp: Long?, isOnline: Boolean) {

    var mostrarRestablecida by remember { mutableStateOf(false) }
    var anteriorOnline by remember { mutableStateOf(isOnline) }

    LaunchedEffect(isOnline) {
        if (isOnline && !anteriorOnline) {
            mostrarRestablecida = true
            delay(3000)
            mostrarRestablecida = false
        }
        anteriorOnline = isOnline
    }

    // No mostrar nada si hay conexión y ya pasaron los 3 segundos
    if (isOnline && !mostrarRestablecida) return

    val displayText = when {
        mostrarRestablecida          -> "Conexión restablecida"
        lastSyncTimestamp != null    -> "Sin conexión desde: ${formatTimestamp(lastSyncTimestamp)}"
        else                         -> "Sin conexión — sin datos guardados"
    }

    Surface(
        color = if (mostrarRestablecida)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.errorContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            displayText,
            style = MaterialTheme.typography.labelSmall,
            color = if (mostrarRestablecida)
                MaterialTheme.colorScheme.onPrimaryContainer
            else
                MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(16.dp, 8.dp)
        )
    }
}

// Función compartida para formatear timestamps
fun formatTimestamp(timestamp: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        .withZone(ZoneId.systemDefault())
    return formatter.format(Instant.ofEpochMilli(timestamp))
}