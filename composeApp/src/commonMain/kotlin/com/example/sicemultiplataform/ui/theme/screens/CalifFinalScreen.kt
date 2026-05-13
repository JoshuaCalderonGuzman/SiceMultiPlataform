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
        uiState.fechaActualizacionFinales?.let { timestamp ->
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    formatTimestamp(timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(16.dp, 8.dp)
                )
            }
        }
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

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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