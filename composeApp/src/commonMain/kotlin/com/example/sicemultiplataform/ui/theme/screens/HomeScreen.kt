package com.example.sicemultiplataform.ui.theme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sicemultiplataform.data.Alumno
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    padding: PaddingValues,
    viewModel: SNViewModel = viewModel(factory = snViewModelFactory())
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedSection by remember { mutableStateOf("Inicio") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        modifier = Modifier.padding(padding),
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "SICE Móvil",
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                DrawerItem("Inicio", Icons.Default.Home, selectedSection) {
                    selectedSection = "Inicio"; scope.launch { drawerState.close() }
                }
                DrawerItem("Kardex", Icons.Default.Description, selectedSection) {
                    selectedSection = "Kardex"; scope.launch { drawerState.close() }
                }
                DrawerItem("Calificaciones Finales", Icons.Default.Star, selectedSection) {
                    selectedSection = "Finales"; scope.launch { drawerState.close() }
                }
                DrawerItem("Calificaciones Unidad", Icons.AutoMirrored.Filled.ListAlt, selectedSection) {
                    selectedSection = "Unidades"; scope.launch { drawerState.close() }
                }
                DrawerItem("Carga Académica", Icons.Default.DateRange, selectedSection) {
                    selectedSection = "Carga"; scope.launch { drawerState.close() }
                }

                Spacer(modifier = Modifier.weight(1f))

                NavigationDrawerItem(
                    label = { Text("Cerrar Sesión") },
                    selected = false,
                    onClick = onLogout,
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(unselectedIconColor = MaterialTheme.colorScheme.error)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(selectedSection) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        scrolledContainerColor = Color.Unspecified,
                        navigationIconContentColor = Color.Unspecified,
                        titleContentColor = Color.Unspecified,
                        actionIconContentColor = Color.Unspecified
                    )
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (selectedSection) {
                    "Inicio"   -> HomeContent(viewModel)
                    "Finales"  -> FinalesScreen(viewModel)
                    "Unidades" -> UnidadesScreen(viewModel)
                    "Kardex"   -> KardexScreen(viewModel)
                    "Carga"    -> CargaAcademicaScreen(viewModel)
                    else       -> PlaceholderSection(selectedSection)
                }
            }
        }
    }
}

@Composable
fun HomeContent(viewModel: SNViewModel) {
    val uiState = viewModel.uiState
    val alumno = uiState.alumno
    if (alumno == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    Column(modifier = Modifier.fillMaxSize()) {
        val ultimaSync = listOfNotNull(
            uiState.fechaActualizacionKardex,
            uiState.fechaActualizacionFinales,
            uiState.fechaActualizacionUnidades,
            uiState.fechaActualizacionCarga
        ).maxOrNull()

        TimestampBanner(
            lastSyncTimestamp = ultimaSync,
            isOnline          = uiState.isOnline
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(64.dp).clip(CircleShape),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(alumno.nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Matrícula: ${alumno.matricula}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoCard("Estatus", alumno.estatus, Icons.Default.Info, Modifier.weight(1f))
                InfoCard("Semestre", "${alumno.semActual}°", Icons.Default.School, Modifier.weight(1f))
            }

            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Información de Carrera", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    HorizontalDivider()
                    DetailRow("Carrera", alumno.carrera)
                    DetailRow("Especialidad", alumno.especialidad)
                    DetailRow("Créditos Totales", "${alumno.cdtosAcumulados}")
                    DetailRow("modEducativo", "${alumno.modEducativo}")
                }
            }

            ProximaClaseCard(viewModel)

            if (alumno.adeudo) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Adeudo detectado: ${alumno.adeudoDescriptivo}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerItem(label: String, icon: ImageVector, current: String, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(label) },
        selected = current == label,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = null) },
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}

@Composable
fun InfoCard(title: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(title, style = MaterialTheme.typography.labelSmall)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        Text(value, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun PlaceholderSection(name: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                modifier = Modifier.size(48.dp).alpha(0.5f),
                tint = MaterialTheme.colorScheme.outline
            )
            Text("Sección $name en desarrollo", color = MaterialTheme.colorScheme.outline)
        }
    }
}
data class ClaseInfo(
    val containerColor: Color,
    val label: String,
    val materia: String,
    val aula: String,
    val horaTexto: String
)

@Composable
fun ProximaClaseCard(viewModel: SNViewModel) {
    val uiState = viewModel.uiState
    if (uiState.isLoading || (!uiState.isLoading && uiState.cargaAcademica == null && uiState.isLogged)) {
        ProximaClaseSkeleton()
        return
    }
    val carga = uiState.cargaAcademica ?: return

    val hoy = obtenerDiaActual()
    if (hoy.isEmpty()) return

    val ahoraMinutos = java.time.LocalTime.now(java.time.ZoneId.systemDefault())
        .let { it.hour * 60 + it.minute }

    val proxima = carga
        .mapNotNull { materia ->
            val horario = materia.obtenerHorarioPorDia(hoy)
            if (horario.isBlank()) return@mapNotNull null
            val rango = horario.trim().substringBefore(" ")
            val partes = rango.split("-")
            if (partes.size < 2) return@mapNotNull null
            val inicioMin = runCatching {
                partes[0].split(":").let { it[0].toInt() * 60 + it[1].toInt() }
            }.getOrNull() ?: return@mapNotNull null
            val aula = horario.substringAfter("Aula:", "").trim()
            Triple(materia, inicioMin, aula)
        }
        .filter { (_, inicio, _) -> inicio > ahoraMinutos }
        .minByOrNull { (_, inicio, _) -> inicio }
        ?: return

    val minutosRestantes = proxima.second - ahoraMinutos
    val tiempoTexto = when {
        minutosRestantes < 60 -> "en $minutosRestantes min"
        else -> "en ${minutosRestantes / 60}h ${minutosRestantes % 60}min"
    }
    val horaInicio = "%02d:%02d".format(proxima.second / 60, proxima.second % 60)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Label con punto azul
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    modifier = Modifier.size(6.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary
                ) {}
                Text(
                    "PRÓXIMA CLASE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(8.dp))

            // Nombre de la materia
            Text(
                proxima.first.materia,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                lineHeight = MaterialTheme.typography.titleMedium.lineHeight
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Footer: aula + hora
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pill de aula
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.MeetingRoom,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            "Aula ${proxima.third}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Hora + tiempo restante
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        horaInicio,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        tiempoTexto,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ProximaClaseSkeleton() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(10.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(18.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(26.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(32.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        }
    }
}
