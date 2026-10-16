package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.TimeSchoolRepository
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class ScreenTab(val titulo: String, val ruta: String) {
    DASHBOARD("Panel Central", "dashboard"),
    HORARIOS("Horarios A/B/C", "horarios"),
    CALENDARIO("Calendario Escolar", "calendario"),
    CONTROL_IOT("Timbre & Enlace IoT", "control_iot"),
    BITACORA("Bitácora de Eventos", "bitacora"),
    CONFIGURACION("Institución", "configuracion")
}

data class TimeSchoolUiState(
    val currentTab: ScreenTab = ScreenTab.DASHBOARD,
    val selectedTipoHorarioId: String = "TIPO_A",
    val selectedDiaSemana: Int = 1, // 1 = Lunes
    val rutinaSemanal: Map<Int, String> = mapOf(
        1 to "TIPO_A",
        2 to "TIPO_B",
        3 to "TIPO_A",
        4 to "TIPO_B",
        5 to "TIPO_C"
    ),
    val tempRutinaSemanal: Map<Int, String> = mapOf(
        1 to "TIPO_A",
        2 to "TIPO_B",
        3 to "TIPO_A",
        4 to "TIPO_B",
        5 to "TIPO_C"
    ),
    val mensajeFeedback: String? = null,
    val isErrorFeedback: Boolean = false,
    val horaActualReloj: String = "07:35:12",
    val proximoTimbreTexto: String = "07:55 - Cambio de Clase",
    val minutosParaProximoTimbre: String = "19 min 48 seg",
    val programacionEnEdicion: Programacion? = null,
    val showDialogEditarProgramacion: Boolean = false,
    val diaEnEdicion: CalendarioDia? = null,
    val showDialogEditarDia: Boolean = false,
    val confirmandoEvacuacion: Boolean = false,
    val filtroLogTexto: String = "",
    val filtroLogTipo: TipoEvento? = null,
    val sesionIniciada: Boolean = false,
    val usuariosDisponibles: List<Usuario> = emptyList()
)

class TimeSchoolViewModel(
    private val repository: TimeSchoolRepository = TimeSchoolRepository.getInstance()
) : ViewModel() {

    val institucion: StateFlow<InstitucionConfig> = repository.institucion
    val usuarioActual: StateFlow<Usuario> = repository.usuarioActual
    val tiposHorario: StateFlow<List<TipoHorario>> = repository.tiposHorario
    val programaciones: StateFlow<List<Programacion>> = repository.programaciones
    val calendarioDias: StateFlow<List<CalendarioDia>> = repository.calendarioDias
    val registros: StateFlow<List<RegistroLog>> = repository.registros
    val hardwareStatus: StateFlow<Esp32HardwareStatus> = repository.hardwareStatus
    val rutinaSemanal: StateFlow<Map<Int, String>> = repository.rutinaSemanal
    val correoGuardado: StateFlow<String> = repository.correoGuardado

    private val _uiState = MutableStateFlow(TimeSchoolUiState())
    val uiState: StateFlow<TimeSchoolUiState> = _uiState.asStateFlow()

    private var clockJob: Job? = null
    private var manualRingTimerJob: Job? = null

    init {
        iniciarReloj()
        observarRutinaSemanal()
        _uiState.update { it.copy(usuariosDisponibles = repository.usuariosInstitucionales) }
    }

    private fun observarRutinaSemanal() {
        viewModelScope.launch {
            repository.rutinaSemanal.collect { rutina ->
                _uiState.update { current ->
                    val tipoActualParaDia = rutina[current.selectedDiaSemana] ?: current.selectedTipoHorarioId
                    current.copy(
                        rutinaSemanal = rutina,
                        tempRutinaSemanal = rutina,
                        selectedTipoHorarioId = tipoActualParaDia
                    )
                }
            }
        }
    }

    private fun iniciarReloj() {
        clockJob?.cancel()
        clockJob = viewModelScope.launch {
            val timeFmt = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            while (true) {
                val now = Date()
                val horaStr = timeFmt.format(now)
                
                // Calcular próximo timbre estimado
                val proximo = calcularProximoTimbre(horaStr)

                _uiState.update {
                    it.copy(
                        horaActualReloj = horaStr,
                        proximoTimbreTexto = proximo.first,
                        minutosParaProximoTimbre = proximo.second
                    )
                }
                delay(1000)
            }
        }
    }

    private fun calcularProximoTimbre(horaActual: String): Pair<String, String> {
        val hoyProgs = programaciones.value
            .filter { it.idTipoHorario == _uiState.value.selectedTipoHorarioId && it.activo }
            .sortedBy { it.hora }

        val actualHhMm = horaActual.take(5)
        val proximo = hoyProgs.firstOrNull { it.hora > actualHhMm }
            ?: hoyProgs.firstOrNull() // cicla a la primera mañana

        return if (proximo != null) {
            val etiqueta = "${proximo.hora} - ${proximo.etiqueta}"
            Pair(etiqueta, "Programado (${proximo.duracionSegundos}s)")
        } else {
            Pair("Sin toques pendientes hoy", "--")
        }
    }

    fun selectTab(tab: ScreenTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun selectTipoHorario(tipoId: String) {
        _uiState.update { it.copy(selectedTipoHorarioId = tipoId) }
    }

    fun selectDiaSemana(dia: Int) {
        _uiState.update { current ->
            val tipoAsignado = current.rutinaSemanal[dia] ?: current.selectedTipoHorarioId
            current.copy(
                selectedDiaSemana = dia,
                selectedTipoHorarioId = tipoAsignado
            )
        }
    }

    fun actualizarTempRutinaDia(dia: Int, tipoId: String) {
        _uiState.update { current ->
            val nuevaTemp = current.tempRutinaSemanal.toMutableMap().apply {
                put(dia, tipoId)
            }
            current.copy(tempRutinaSemanal = nuevaTemp)
        }
    }

    fun guardarRutinaSemanal() {
        val nuevaRutina = _uiState.value.tempRutinaSemanal
        repository.actualizarRutinaSemanal(nuevaRutina)
        _uiState.update { current ->
            val tipoActualizadoParaDia = nuevaRutina[current.selectedDiaSemana] ?: current.selectedTipoHorarioId
            current.copy(
                rutinaSemanal = nuevaRutina,
                selectedTipoHorarioId = tipoActualizadoParaDia
            )
        }
        mostrarFeedback("Horario semanal guardado exitosamente")
    }

    fun toggleProgramacion(id: String) {
        repository.toggleProgramacionActiva(id)
    }

    fun abrirDialogoNuevaProgramacion() {
        val nueva = Programacion(
            id = "prog_${UUID.randomUUID().toString().take(8)}",
            idTipoHorario = _uiState.value.selectedTipoHorarioId,
            diaSemana = _uiState.value.selectedDiaSemana,
            hora = "08:00",
            duracionSegundos = 4,
            idTimbre = "CAMPANA_CENTRAL",
            activo = true,
            etiqueta = "Nuevo Timbre"
        )
        _uiState.update {
            it.copy(
                programacionEnEdicion = nueva,
                showDialogEditarProgramacion = true
            )
        }
    }

    fun abrirDialogoEditarProgramacion(programacion: Programacion) {
        _uiState.update {
            it.copy(
                programacionEnEdicion = programacion,
                showDialogEditarProgramacion = true
            )
        }
    }

    fun cerrarDialogoProgramacion() {
        _uiState.update {
            it.copy(
                programacionEnEdicion = null,
                showDialogEditarProgramacion = false
            )
        }
    }

    fun guardarProgramacion(prog: Programacion) {
        repository.guardarProgramacion(prog)
        cerrarDialogoProgramacion()
        mostrarFeedback("Horario guardado correctamente: ${prog.hora} (${prog.etiqueta})")
    }

    fun eliminarProgramacion(id: String) {
        repository.eliminarProgramacion(id)
        cerrarDialogoProgramacion()
        mostrarFeedback("Programación eliminada")
    }

    fun abrirDialogoEditarDia(dia: CalendarioDia) {
        _uiState.update {
            it.copy(
                diaEnEdicion = dia,
                showDialogEditarDia = true
            )
        }
    }

    fun cerrarDialogoEditarDia() {
        _uiState.update {
            it.copy(
                diaEnEdicion = null,
                showDialogEditarDia = false
            )
        }
    }

    fun guardarAsignacionDia(
        fechaIso: String,
        idTipoHorario: String,
        obs: String,
        jornada: String = "Completa",
        hayClases: Boolean = true
    ) {
        repository.asignarTipoHorarioADia(fechaIso, idTipoHorario, obs, jornada, hayClases)
        cerrarDialogoEditarDia()
        mostrarFeedback("Evento registrado para $fechaIso")
    }

    fun eliminarEventoDia(fechaIso: String) {
        repository.eliminarEventoDia(fechaIso)
        cerrarDialogoEditarDia()
        mostrarFeedback("Evento eliminado. Día restablecido a horario habitual.")
    }

    // Bluetooth y Enlace ESP32
    fun toggleBluetoothConnection() {
        val actual = hardwareStatus.value.estado
        if (actual == BluetoothEstado.CONECTADO) {
            repository.cambiarEstadoBluetooth(BluetoothEstado.DESCONECTADO)
            mostrarFeedback("Bluetooth ESP32 desconectado")
        } else {
            viewModelScope.launch {
                repository.cambiarEstadoBluetooth(BluetoothEstado.CONECTANDO)
                delay(1200)
                repository.cambiarEstadoBluetooth(BluetoothEstado.CONECTADO)
                mostrarFeedback("Conectado con éxito a ESP32_TIMESCHOOL_BELL (SPP)")
            }
        }
    }

    fun sincronizarHorariosConEsp32() {
        viewModelScope.launch {
            val resultado = repository.sincronizarConEsp32()
            mostrarFeedback(resultado)
        }
    }

    // Timbre Manual
    fun presionarTimbreManual(duracionSegundos: Int = 3) {
        repository.activarTimbreManual(duracionSegundos)
        manualRingTimerJob?.cancel()
        manualRingTimerJob = viewModelScope.launch {
            delay(duracionSegundos * 1000L)
            repository.detenerTimbreManual()
        }
    }

    fun soltarTimbreManual() {
        manualRingTimerJob?.cancel()
        repository.detenerTimbreManual()
    }

    // Evacuación
    fun activarAlarmaEvacuacion() {
        repository.toggleAlarmaEvacuacion(true)
        mostrarFeedback("¡ALARMA DE EVACUACIÓN ACTIVADA EN CAMPANA CENTRAL!", isError = true)
    }

    fun desactivarAlarmaEvacuacion() {
        repository.toggleAlarmaEvacuacion(false)
        mostrarFeedback("Alarma de evacuación detenida y normalizada.")
    }

    // Manejo de Sesión y Control de Acceso
    fun autenticarUsuario(
        identificador: String,
        passwordIngresada: String,
        recordarSesion: Boolean,
        terminosAceptados: Boolean
    ): Pair<Boolean, String?> {
        if (!terminosAceptados) {
            return Pair(false, "Debes aceptar los Términos y Condiciones para poder acceder")
        }
        if (identificador.isBlank()) {
            return Pair(false, "El nombre de usuario o correo institucional es obligatorio")
        }
        if (passwordIngresada.isBlank()) {
            return Pair(false, "La contraseña es obligatoria para acceder a la aplicación")
        }

        val idLimpio = identificador.trim().lowercase()
        val passLimpia = passwordIngresada.trim()

        val usuarioEncontrado = when {
            idLimpio == "admin" || idLimpio == "carlos" || idLimpio == repository.usuarioAdmin.correo.lowercase() -> {
                if (passLimpia == "admin123" || passLimpia == "123456" || passLimpia == "admin" || passLimpia == "1234") {
                    repository.usuarioAdmin
                } else {
                    return Pair(false, "Contraseña incorrecta para el usuario Administrador")
                }
            }
            idLimpio == "directivo" || idLimpio == "marta" || idLimpio == repository.usuarioDirectivo.correo.lowercase() -> {
                if (passLimpia == "directivo123" || passLimpia == "123456" || passLimpia == "directivo" || passLimpia == "1234") {
                    repository.usuarioDirectivo
                } else {
                    return Pair(false, "Contraseña incorrecta para el usuario Directivo")
                }
            }
            else -> {
                val coincidente = repository.usuariosInstitucionales.find {
                    it.correo.equals(idLimpio, ignoreCase = true) ||
                    it.nombre.equals(idLimpio, ignoreCase = true)
                }
                if (coincidente != null) {
                    if (passLimpia.length >= 4) {
                        coincidente
                    } else {
                        return Pair(false, "La contraseña debe tener al menos 4 caracteres")
                    }
                } else {
                    if (passLimpia.length >= 4) {
                        Usuario(
                            uid = "usr_${UUID.randomUUID().toString().take(8)}",
                            nombre = identificador.trim().replaceFirstChar { it.uppercase() },
                            apellido = "Institucional",
                            correo = if (identificador.contains("@")) identificador.trim() else "$idLimpio@timeschool.edu.co",
                            rol = if (idLimpio.contains("admin")) RolUsuario.ADMINISTRADOR else RolUsuario.DIRECTIVO,
                            estado = true,
                            fechaCreacion = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                        )
                    } else {
                        return Pair(false, "La contraseña es obligatoria (mínimo 4 caracteres)")
                    }
                }
            }
        }

        repository.guardarCorreoAccesoRapido(identificador.trim())
        iniciarSesion(usuarioEncontrado, if (recordarSesion) "Acceso con sesión recordada" else "Acceso estándar")
        return Pair(true, null)
    }

    fun limpiarCorreoGuardado() {
        repository.limpiarCorreoAccesoRapido()
    }

    fun iniciarSesion(usuario: Usuario, metodo: String = "Acceso a la Aplicación") {
        repository.registrarInicioSesion(usuario, metodo)
        _uiState.update {
            it.copy(
                sesionIniciada = true,
                currentTab = ScreenTab.DASHBOARD
            )
        }
        mostrarFeedback("Sesión iniciada: Bienvenido(a) ${usuario.nombreCompleto}")
    }

    fun cerrarSesion() {
        repository.registrarCierreSesion()
        _uiState.update {
            it.copy(
                sesionIniciada = false,
                currentTab = ScreenTab.DASHBOARD
            )
        }
        mostrarFeedback("Sesión cerrada. Correo guardado para tu próximo acceso rápido.")
    }

    fun registrarNuevoUsuarioYSesion(nombre: String, apellido: String, rol: RolUsuario, correo: String, passwordIngresada: String): Pair<Boolean, String?> {
        if (passwordIngresada.isBlank()) {
            return Pair(false, "La contraseña es obligatoria para registrar la cuenta")
        }
        if (passwordIngresada.length < 4) {
            return Pair(false, "La contraseña debe tener al menos 4 caracteres")
        }
        val nuevo = Usuario(
            uid = "usr_${UUID.randomUUID().toString().take(8)}",
            nombre = nombre.trim().ifBlank { "Funcionario" },
            apellido = apellido.trim(),
            correo = correo.trim().ifBlank { "usuario@timeschool.edu.co" },
            rol = rol,
            estado = true,
            fechaCreacion = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        )
        repository.guardarCorreoAccesoRapido(nuevo.correo)
        iniciarSesion(nuevo, "Ingreso con Nueva Cuenta Registrada")
        return Pair(true, null)
    }

    // Cambio de rol rápido para auditoría de permisos
    fun alternarUsuarioRol() {
        val actual = usuarioActual.value
        val siguiente = if (actual.rol == RolUsuario.ADMINISTRADOR) {
            repository.usuarioDirectivo
        } else {
            repository.usuarioAdmin
        }
        repository.cambiarUsuario(siguiente)
        mostrarFeedback("Sesión cambiada a: ${siguiente.nombreCompleto} (${siguiente.rol.etiqueta})")
    }

    // Configuración Institucional
    fun actualizarInstitucion(nombre: String, sede: String, ano: Int) {
        val exito = repository.actualizarInstitucion(nombre, sede, ano)
        if (exito) {
            mostrarFeedback("Datos institucionales actualizados correctamente.")
        } else {
            mostrarFeedback("Acceso Denegado: Solo el Administrador puede modificar estos datos.", isError = true)
        }
    }

    fun setFiltroLog(texto: String, tipo: TipoEvento?) {
        _uiState.update {
            it.copy(
                filtroLogTexto = texto,
                filtroLogTipo = tipo
            )
        }
    }

    fun mostrarFeedback(mensaje: String, isError: Boolean = false) {
        _uiState.update {
            it.copy(
                mensajeFeedback = mensaje,
                isErrorFeedback = isError
            )
        }
    }

    fun limpiarFeedback() {
        _uiState.update { it.copy(mensajeFeedback = null) }
    }
}
