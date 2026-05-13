package com.example.sicemultiplataform.ui.theme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sicemultiplataform.data.MateriaUnidades

@Composable
fun UnidadesScreen(viewModel: SNViewModel) {
    val uiState = viewModel.uiState
    val matricula = uiState.alumno?.matricula ?: return

    LaunchedEffect(Unit) {
        viewModel.cargarUnidades(matricula)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TimestampBanner(
            lastSyncTimestamp = uiState.fechaActualizacionUnidades,
            isOnline          = uiState.isOnline
        )
        UnidadesSection(materias = uiState.califUnidades ?: emptyList())
    }
}

@Composable
fun UnidadesSection(materias: List<MateriaUnidades>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(materias) { materia -> UnidadesCard(materia) }
    }
}

@Composable
fun UnidadesCard(materia: MateriaUnidades) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(materia.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Grupo: ${materia.grupo}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.height(12.dp))

            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                materia.calificaciones.forEachIndexed { index, calif ->
                    if (calif != null) {
                        UnidadBadge(numero = index + 1, nota = calif)
                    }
                }
            }
        }
    }
}

@Composable
fun UnidadBadge(numero: Int, nota: Int) {
    val bagcolor = when {
        nota >= 70 -> MaterialTheme.colorScheme.primaryContainer
        nota in 1..69 -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("U$numero", style = MaterialTheme.typography.labelSmall)
        Surface(
            modifier = Modifier.size(38.dp),
            shape = CircleShape,
            color = bagcolor,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(nota.toString(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}