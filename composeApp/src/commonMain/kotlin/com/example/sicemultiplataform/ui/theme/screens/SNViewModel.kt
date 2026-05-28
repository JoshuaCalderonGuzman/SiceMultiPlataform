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
import com.example.sicemultiplataform.data.segurity.SecureSessionManager
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class SNUiState(
    val isLoading: Boolean = false,
    val isLogged: Boolean = false,
    val sesionServidor: Boolean = false,
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
    private val connectivityMonitor: ConnectivityMonitor,
    private val sessionManager: SecureSessionManager
) : ViewModel() {

    private val jsonParser = Json { ignoreUnknownKeys = true }

    var uiState by mutableStateOf(SNUiState())
        private set

    init {
        observarConectividad()
        iniciarSyncPeriodico()
        autoLogin()
    }

    fun autoLogin() {
        viewModelScope.launch {
            val matricula = sessionManager.obtenerMatricula() ?: return@launch
            val password  = sessionManager.obtenerPassword()  ?: return@launch

            uiState = uiState.copy(isLoading = true)

            if (uiState.isOnline) {
                // Con internet: login normal
                login(matricula, password)
            } else {
                // Sin internet: cargar desde BD local directamente
                cargarDesdeLocal(matricula)
            }
        }
    }



    private fun iniciarSyncPeriodico() {
        viewModelScope.launch {
            while (true) {
                delay(5 * 60 * 1000L) // esperar 5 minutos

                if (uiState.isLogged && uiState.isOnline) {
                    val matricula    = uiState.alumno?.matricula ?: continue
                    val modEducativo = uiState.alumno?.modEducativo ?: continue

                    sincronizarTodo(matricula, modEducativo)
                }
            }
        }
    }

    private fun observarConectividad() {
        viewModelScope.launch {
            connectivityMonitor.isConnected.collect { conectado ->
                uiState = uiState.copy(isOnline = conectado)
                if (conectado && uiState.isLogged) {
                    // Siempre re-autenticar al reconectar,
                    // no confiar en sesionServidor que puede haber expirado
                    val matricula = sessionManager.obtenerMatricula() ?: return@collect
                    val password  = sessionManager.obtenerPassword()  ?: return@collect
                    login(matricula, password)
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

                    sessionManager.guardarSesion(m, p)

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
                        sesionServidor = true,
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

            }  catch (e: Exception) {
                println("SYNC_ERROR: ${e.message}")
                // Intentar re-autenticar antes de caer a local
                val matricula = uiState.alumno?.matricula ?: return@launch
                val password  = sessionManager.obtenerPassword()
                if (password != null && uiState.isOnline) {
                    uiState = uiState.copy(sesionServidor = false)
                    login(matricula, password)
                } else {
                    cargarDesdeLocal(matricula)
                }
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
            sesionServidor = false,
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
            sessionManager.cerrarSesion()
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