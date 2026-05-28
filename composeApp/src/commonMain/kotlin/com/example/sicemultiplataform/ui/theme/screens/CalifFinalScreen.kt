package com.example.sicemultiplataform.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sicemultiplataform.data.CalificacionFinal

@Composable
fun FinalesScreen(viewModel: SNViewModel) {
    val uiState = viewModel.uiState
    val matricula = uiState.alumno?.matricula ?: return
    val modEducativo = uiState.alumno.modEducativo.toString()

    LaunchedEffect(Unit) {
        viewModel.cargarFinales(matricula, modEducativo)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TimestampBanner(
            lastSyncTimestamp = uiState.fechaActualizacionFinales,
            isOnline          = uiState.isOnline
        )
        FinalesSection(calificaciones = uiState.califFinales ?: emptyList())
    }
}

@Composable
fun FinalesSection(calificaciones: List<CalificacionFinal>) {
    if (calificaciones.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.School,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp).alpha(0.3f)
                )
                Text(
                    "No hay calificaciones finales registradas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
        return
    }

    val califsValidas = calificaciones.filter { it.calif > 0 }
    val promedio = if (calificaciones.isNotEmpty())
        calificaciones.sumOf { it.calif }.toDouble() / calificaciones.size
    else 0.0

    val colorPromedio = when {
        promedio >= 90 -> MaterialTheme.colorScheme.primaryContainer
        promedio >= 70 -> MaterialTheme.colorScheme.secondaryContainer
        else           -> MaterialTheme.colorScheme.errorContainer
    }
    val colorTexto = when {
        promedio >= 90 -> MaterialTheme.colorScheme.onPrimaryContainer
        promedio >= 70 -> MaterialTheme.colorScheme.onSecondaryContainer
        else           -> MaterialTheme.colorScheme.onErrorContainer
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colorPromedio)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Promedio General",
                            style = MaterialTheme.typography.labelMedium,
                            color = colorTexto
                        )
                        Text(
                            "${califsValidas.size} materias acreditadas",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorTexto.copy(alpha = 0.7f)
                        )
                    }
                    Text(
                        "%.1f".format(promedio),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = colorTexto
                    )
                }
            }
        }

        item { }
        items(calificaciones) { calif ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(calif.materia, fontWeight = FontWeight.Bold)
                    Text("Calificación: ${calif.calif}")
                    Text("Acreditación: ${calif.acred}")
                    if (calif.observaciones.isNotBlank()) {
                        Text("Obs: ${calif.observaciones}")
                    }
                }
            }
        }
    }
}