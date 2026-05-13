package com.example.sicemultiplataform.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AddChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sicemultiplataform.data.KardexCompleto

@Composable
fun KardexScreen(viewModel: SNViewModel) {
    val uiState = viewModel.uiState
    val matricula = uiState.alumno?.matricula ?: return
    val modEducativo = uiState.alumno.modEducativo.toString()

    LaunchedEffect(Unit) {
        viewModel.cargarKardex(matricula, modEducativo)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        uiState.fechaActualizacionKardex?.let { timestamp ->
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
        KardexSection(kardex = uiState.kardex)
    }
}

@Composable
fun KardexSection(kardex: KardexCompleto?) {
    if (kardex == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Text("Cargando historial académico...", modifier = Modifier.padding(top = 8.dp))
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ResumenItem("Promedio", "${kardex.resumen.promedioGral}", Icons.Default.Star)
                    ResumenItem("Créditos", "${kardex.resumen.cdtsAcumulados}", Icons.Default.AddChart)
                    ResumenItem("Avance", "${kardex.resumen.avance}", Icons.AutoMirrored.Filled.TrendingUp)
                }
            }
        }

        items(kardex.materias) { materia ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(materia.materia, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Surface(
                            shape = CircleShape,
                            color = if (materia.calif >= 70) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                "${materia.calif}",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (materia.calif >= 70) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                    HorizontalDivider(Modifier.padding(vertical = 8.dp).alpha(0.3f))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(materia.clvMat, style = MaterialTheme.typography.bodySmall)
                            Text("${materia.periodo} ${materia.anio} Semestre: ${materia.semestre}", style = MaterialTheme.typography.bodySmall)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(materia.acred, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            Text("${materia.cdts} Créditos", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResumenItem(label: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}