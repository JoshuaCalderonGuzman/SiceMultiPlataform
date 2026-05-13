package com.example.sicemultiplataform.ui.theme.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sicemultiplataform.data.Alumno
import com.example.sicemultiplataform.data.CalificacionFinal
import com.example.sicemultiplataform.data.KardexCompleto
import com.example.sicemultiplataform.data.MateriaCarga
import com.example.sicemultiplataform.data.MateriaUnidades
import com.example.sicemultiplataform.data.SNRepository
import com.example.sicemultiplataform.data.local.entity.AlumnoEntity
import com.example.sicemultiplataform.data.local.entity.CargaEntity
import com.example.sicemultiplataform.data.local.entity.KardexEntity
import com.example.sicemultiplataform.data.local.entity.CalifFinalEntity
import com.example.sicemultiplataform.data.local.entity.CalifUnidadesEntity
import com.example.sicemultiplataform.data.repository.LocalRepository
import com.example.sicemultiplataform.data.network.ConnectivityMonitor
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class SNUiState(
    val isLoading: Boolean = false,
    val isLogged: Boolean = false,
    val alumno: Alumno? = null,
    val kardex: KardexCompleto? = null,
    val califFinales: List<CalificacionFinal>? = null,
    val califUnidades: List<MateriaUnidades>? = null,
    val cargaAcademica: List<MateriaCarga>? = null,
    val fechaActualizacionCarga: Long? = null,
    val fechaActualizacionKardex: Long? = null,
    val fechaActualizacionFinales: Long? = null,
    val fechaActualizacionUnidades: Long? = null,
    val errorMessage: String? = null,
    val isOnline: Boolean = true
)

class SNViewModel(
    private val snRepository: SNRepository,
    private val localRepository: LocalRepository,
    private val connectivityMonitor: ConnectivityMonitor
) : ViewModel() {

    private val jsonParser = Json { ignoreUnknownKeys = true }

    var uiState by mutableStateOf(SNUiState())
        private set

    init {
        observarConectividad()
    }

    private fun observarConectividad() {
        viewModelScope.launch {
            connectivityMonitor.isConnected.collect { conectado ->
                uiState = uiState.copy(isOnline = conectado)  // ← siempre actualizar
                if (conectado && uiState.isLogged) {
                    val matricula    = uiState.alumno?.matricula ?: return@collect
                    val modEducativo = uiState.alumno?.modEducativo ?: return@collect
                    sincronizarTodo(matricula, modEducativo)
                }
            }
        }
    }
    fun login(m: String, p: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                snRepository.logoutSession()
                val result = snRepository.acceso(m, p)

                if (result.success) {
                    val alumnoParsed = snRepository.alumnoDatos()

                    if (alumnoParsed.matricula.trim().uppercase() != m.trim().uppercase()) {
                        uiState = uiState.copy(isLoading = false, errorMessage = "Sesión incorrecta, intenta nuevamente")
                        return@launch
                    }

                    // Guardar alumno en BD local
                    localRepository.saveAlumno(
                        AlumnoEntity(
                            control             = m,
                            jsonData            = jsonParser.encodeToString(alumnoParsed),
                            ultimaActualizacion = System.currentTimeMillis()
                        )
                    )

                    // Cargar UI inmediatamente con datos de red
                    uiState = uiState.copy(
                        isLogged  = true,
                        isLoading = false,
                        alumno    = alumnoParsed
                    )

                    // Sincronizar todo en paralelo y guardar en BD
                    sincronizarTodo(m, alumnoParsed.modEducativo)

                } else {
                    uiState = uiState.copy(isLoading = false, errorMessage = "Credenciales incorrectas")
                }

            } catch (e: Exception) {
                // Sin internet — cargar desde BD local
                cargarDesdeLocal(m)
            }
        }
    }

    // Descarga todo en paralelo y guarda en BD
    private fun sincronizarTodo(matricula: String, modEducativo: Int) {
        viewModelScope.launch {
            try {
                val kardexDeferred    = async { snRepository.kardex(modEducativo) }
                val califDeferred     = async { snRepository.califFinal(modEducativo) }
                val unidadesDeferred  = async { snRepository.califUnidades() }
                val cargaDeferred     = async { snRepository.cargaAcademica() }

                val kardex    = kardexDeferred.await()
                val finales   = califDeferred.await()
                val unidades  = unidadesDeferred.await()
                val carga     = cargaDeferred.await()

                val ahora = System.currentTimeMillis()

                // Guardar todo en BD local
                localRepository.insertKardex(
                    KardexEntity(matricula, jsonParser.encodeToString(kardex), ahora)
                )
                localRepository.insertCalifFinal(
                    CalifFinalEntity(matricula, jsonParser.encodeToString(finales), ahora)
                )
                localRepository.insertCalifUnidades(
                    CalifUnidadesEntity(matricula, jsonParser.encodeToString(unidades), ahora)
                )
                localRepository.insertCarga(
                    CargaEntity(matricula, jsonParser.encodeToString(carga), ahora)
                )

                // Actualizar UI con datos frescos
                uiState = uiState.copy(
                    kardex                      = kardex,
                    califFinales                = finales,
                    califUnidades               = unidades,
                    cargaAcademica              = carga,
                    fechaActualizacionKardex    = ahora,
                    fechaActualizacionFinales   = ahora,
                    fechaActualizacionUnidades  = ahora,
                    fechaActualizacionCarga     = ahora
                )

            } catch (e: Exception) {
                // Si falla la sincronización, cargar desde BD
                println("SYNC_ERROR: ${e.message}")
                cargarDesdeLocal(uiState.alumno?.matricula ?: return@launch)
            }
        }
    }

    // Carga todos los datos desde BD local
    private suspend fun cargarDesdeLocal(matricula: String) {
        val alumnoLocal = localRepository.getAlumno(matricula)

        if (alumnoLocal == null) {
            uiState = uiState.copy(isLoading = false, errorMessage = "Sin conexión a internet")
            return
        }

        val alumno = jsonParser.decodeFromString<Alumno>(alumnoLocal.jsonData)

        val kardexLocal    = localRepository.getKardexByControl(matricula)
        val finalesLocal   = localRepository.getCalifFinalByControl(matricula)
        val unidadesLocal  = localRepository.getCalifUnidadesByControl(matricula)
        val cargaLocal     = localRepository.getCargaByControl(matricula)

        uiState = uiState.copy(
            isLogged  = true,
            isLoading = false,
            alumno    = alumno,
            errorMessage = "Modo sin conexión",

            kardex = kardexLocal?.let {
                jsonParser.decodeFromString(it.jsonData)
            },
            fechaActualizacionKardex = kardexLocal?.ultimaActualizacion,

            califFinales = finalesLocal?.let {
                jsonParser.decodeFromString(it.jsonData)
            },
            fechaActualizacionFinales = finalesLocal?.ultimaActualizacion,

            califUnidades = unidadesLocal?.let {
                jsonParser.decodeFromString(it.jsonData)
            },
            fechaActualizacionUnidades = unidadesLocal?.ultimaActualizacion,

            cargaAcademica = cargaLocal?.let {
                jsonParser.decodeFromString(it.jsonData)
            },
            fechaActualizacionCarga = cargaLocal?.ultimaActualizacion
        )
    }

    fun logout() {
        viewModelScope.launch {
            snRepository.logoutSession()
            uiState = SNUiState()
        }
    }

    // Las funciones ahora solo refrescan desde BD
    fun cargarCargaAcademica(matricula: String) {
        viewModelScope.launch {
            val local = localRepository.getCargaByControl(matricula)
            if (local != null) {
                uiState = uiState.copy(
                    cargaAcademica          = jsonParser.decodeFromString(local.jsonData),
                    fechaActualizacionCarga = local.ultimaActualizacion
                )
            }
        }
    }

    fun cargarKardex(matricula: String, modEducativo: String) {
        viewModelScope.launch {
            val local = localRepository.getKardexByControl(matricula)
            if (local != null) {
                uiState = uiState.copy(
                    kardex                   = jsonParser.decodeFromString(local.jsonData),
                    fechaActualizacionKardex = local.ultimaActualizacion
                )
            }
        }
    }

    fun cargarFinales(matricula: String, modEducativo: String) {
        viewModelScope.launch {
            val local = localRepository.getCalifFinalByControl(matricula)
            if (local != null) {
                uiState = uiState.copy(
                    califFinales              = jsonParser.decodeFromString(local.jsonData),
                    fechaActualizacionFinales = local.ultimaActualizacion
                )
            }
        }
    }

    fun cargarUnidades(matricula: String) {
        viewModelScope.launch {
            val local = localRepository.getCalifUnidadesByControl(matricula)
            if (local != null) {
                uiState = uiState.copy(
                    califUnidades              = jsonParser.decodeFromString(local.jsonData),
                    fechaActualizacionUnidades = local.ultimaActualizacion
                )
            }
        }
    }
}