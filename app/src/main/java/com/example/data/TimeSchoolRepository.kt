package com.example.data

import android.content.Context
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class TimeSchoolRepository(context: Context? = null) {

    private val preferences: TimeSchoolPreferences? = context?.let { TimeSchoolPreferences(it) }

    companion object {
        @Volatile
        private var instance: TimeSchoolRepository? = null

        fun initialize(context: Context): TimeSchoolRepository {
            return instance ?: synchronized(this) {
                instance ?: TimeSchoolRepository(context.applicationContext).also { instance = it }
            }
        }

        fun getInstance(context: Context? = null): TimeSchoolRepository {
            return instance ?: synchronized(this) {
                instance ?: TimeSchoolRepository(context?.applicationContext).also { instance = it }
            }
        }
    }

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val timestampFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    // Usuarios de prueba para verificar control de roles (Administrador vs Directivo)
    val usuarioAdmin = Usuario(
        uid = "admin_uid_timeschool_01",
        nombre = "Carlos",
        apellido = "Mendoza",
        correo = "admin@timeschool.edu.co",
        rol = RolUsuario.ADMINISTRADOR,
        estado = true,
        fechaCreacion = "15/01/2026"
    )

    val usuarioDirectivo = Usuario(
        uid = "directivo_uid_timeschool_02",
        nombre = "Marta",
        apellido = "Rojas",
        correo = "coordinacion@timeschool.edu.co",
        rol = RolUsuario.DIRECTIVO,
        estado = true,
        fechaCreacion = "01/02/2026"
    )

    val usuariosInstitucionales: List<Usuario> = listOf(usuarioAdmin, usuarioDirectivo)

    private val _institucion = MutableStateFlow(
        preferences?.loadInstitucion() ?: InstitucionConfig(
            nombreColegio = "Institución Educativa Técnico Industrial San José",
            sede = "Sede Principal - Campus Central",
            anoLectivo = 2026,
            actualizadoPor = "Carlos Mendoza (Administrador)",
            fechaActualizacion = timestampFormat.format(Date())
        )
    )
    val institucion: StateFlow<InstitucionConfig> = _institucion.asStateFlow()

    private val _usuarioActual = MutableStateFlow<Usuario>(usuarioAdmin)
    val usuarioActual: StateFlow<Usuario> = _usuarioActual.asStateFlow()

    private val _correoGuardado = MutableStateFlow(preferences?.loadCorreoGuardado() ?: "admin@timeschool.edu.co")
    val correoGuardado: StateFlow<String> = _correoGuardado.asStateFlow()

    fun guardarCorreoAccesoRapido(correo: String) {
        _correoGuardado.value = correo
        preferences?.saveCorreoGuardado(correo)
    }

    fun limpiarCorreoAccesoRapido() {
        _correoGuardado.value = ""
        preferences?.clearCorreoGuardado()
    }

    private val _tiposHorario = MutableStateFlow(
        listOf(
            TipoHorario(
                id = "TIPO_A",
                nombre = "Horario Ordinario (Tipo A)",
                descripcion = "Jornada escolar estándar con 7 bloques lectivos y 2 recesos de 20 min.",
                etiquetaCorta = "Tipo A"
            ),
            TipoHorario(
                id = "TIPO_B",
                nombre = "Horario Especial / Actos (Tipo B)",
                descripcion = "Bloques recortados a 45 minutos para actos cívicos y reuniones pedagógicas.",
                etiquetaCorta = "Tipo B"
            ),
            TipoHorario(
                id = "TIPO_C",
                nombre = "Jornada Reducida (Tipo C)",
                descripcion = "Jornada intensiva hasta las 12:30 pm sin descanso general extendido.",
                etiquetaCorta = "Tipo C"
            )
        )
    )
    val tiposHorario: StateFlow<List<TipoHorario>> = _tiposHorario.asStateFlow()

    private val _rutinaSemanal = MutableStateFlow<Map<Int, String>>(
        preferences?.loadRutinaSemanal() ?: mapOf(
            1 to "TIPO_A",
            2 to "TIPO_B",
            3 to "TIPO_A",
            4 to "TIPO_B",
            5 to "TIPO_C"
        )
    )
    val rutinaSemanal: StateFlow<Map<Int, String>> = _rutinaSemanal.asStateFlow()

    private val _programaciones = MutableStateFlow(
        preferences?.loadProgramaciones() ?: generarProgramacionesIniciales()
    )
    val programaciones: StateFlow<List<Programacion>> = _programaciones.asStateFlow()

    private val _calendarioDias = MutableStateFlow(
        preferences?.loadCalendario() ?: generarCalendarioInicial()
    )
    val calendarioDias: StateFlow<List<CalendarioDia>> = _calendarioDias.asStateFlow()

    private val _registros = MutableStateFlow(
        preferences?.loadRegistros() ?: generarRegistrosIniciales()
    )
    val registros: StateFlow<List<RegistroLog>> = _registros.asStateFlow()

    private val _hardwareStatus = MutableStateFlow(
        Esp32HardwareStatus(
            nombre = "ESP32_TIMESCHOOL_BELL",
            mac = "24:6F:28:B2:7A:1C",
            uuidSpp = "00001101-0000-1000-8000-00805F9B34FB",
            estado = BluetoothEstado.CONECTADO,
            ultimoSync = "Hoy 07:05 AM",
            horariosCargadosEnFlash = _programaciones.value.count { it.activo },
            releActivo = false,
            modoEvacuacionActivo = false
        )
    )
    val hardwareStatus: StateFlow<Esp32HardwareStatus> = _hardwareStatus.asStateFlow()

    init {
        // Si no existían datos en el almacenamiento persistente, guardar los iniciales
        if (preferences?.loadRutinaSemanal() == null) {
            preferences?.saveRutinaSemanal(_rutinaSemanal.value)
        }
        if (preferences?.loadProgramaciones() == null) {
            preferences?.saveProgramaciones(_programaciones.value)
        }
        if (preferences?.loadInstitucion() == null) {
            preferences?.saveInstitucion(_institucion.value)
        }
        if (preferences?.loadCalendario() == null) {
            preferences?.saveCalendario(_calendarioDias.value)
        }
    }

    fun cambiarUsuario(nuevoUsuario: Usuario) {
        _usuarioActual.value = nuevoUsuario
        agregarRegistro(
            tipo = TipoEvento.SINCRONIZACION,
            obs = "Sesión activa cambiada a ${nuevoUsuario.nombreCompleto} (${nuevoUsuario.rol.etiqueta})"
        )
    }

    fun registrarInicioSesion(usuario: Usuario, metodo: String = "Acceso a la Aplicación") {
        _usuarioActual.value = usuario
        agregarRegistro(
            tipo = TipoEvento.SESION,
            obs = "Inicio de sesión registrado: ${usuario.nombreCompleto} (${usuario.rol.etiqueta}) - $metodo"
        )
    }

    fun registrarCierreSesion() {
        val u = _usuarioActual.value
        agregarRegistro(
            tipo = TipoEvento.SESION,
            obs = "Cierre de sesión registrado: ${u.nombreCompleto} (${u.rol.etiqueta})"
        )
    }

    fun actualizarInstitucion(nombre: String, sede: String, ano: Int): Boolean {
        if (_usuarioActual.value.rol != RolUsuario.ADMINISTRADOR) {
            return false // Denegado por reglas de seguridad
        }

        val adminNombre = "${_usuarioActual.value.nombre} (${_usuarioActual.value.rol.etiqueta})"
        val now = timestampFormat.format(Date())
        val updated = InstitucionConfig(
            nombreColegio = nombre.trim(),
            sede = sede.trim(),
            anoLectivo = ano,
            actualizadoPor = adminNombre,
            fechaActualizacion = now
        )
        _institucion.value = updated
        preferences?.saveInstitucion(updated)

        agregarRegistro(
            tipo = TipoEvento.SINCRONIZACION,
            obs = "Actualización institucional: $nombre | $sede | Año $ano"
        )
        return true
    }

    fun toggleProgramacionActiva(id: String) {
        _programaciones.update { lista ->
            val updatedList = lista.map { prog ->
                if (prog.id == id) {
                    val nuevoEstado = !prog.activo
                    val estadoTexto = if (nuevoEstado) "habilitado" else "deshabilitado"
                    agregarRegistro(
                        tipo = TipoEvento.PROGRAMADO,
                        obs = "Timbre de las ${prog.hora} (${prog.etiqueta}) $estadoTexto por ${_usuarioActual.value.nombreCompleto}"
                    )
                    prog.copy(activo = nuevoEstado)
                } else prog
            }
            preferences?.saveProgramaciones(updatedList)
            updatedList
        }
    }

    fun guardarProgramacion(programacion: Programacion) {
        _programaciones.update { lista ->
            val index = lista.indexOfFirst { it.id == programacion.id }
            val updatedList = if (index >= 0) {
                lista.toMutableList().apply { set(index, programacion) }
            } else {
                lista + programacion
            }
            preferences?.saveProgramaciones(updatedList)
            updatedList
        }
        agregarRegistro(
            tipo = TipoEvento.PROGRAMADO,
            obs = "Programación guardada: ${programacion.hora} (${programacion.duracionSegundos}s) para ${programacion.diaSemanaNombre}"
        )
    }

    fun eliminarProgramacion(id: String) {
        val prog = _programaciones.value.find { it.id == id }
        _programaciones.update { lista ->
            val updatedList = lista.filterNot { it.id == id }
            preferences?.saveProgramaciones(updatedList)
            updatedList
        }
        if (prog != null) {
            agregarRegistro(
                tipo = TipoEvento.PROGRAMADO,
                obs = "Eliminada programación: ${prog.hora} (${prog.etiqueta}) de ${prog.diaSemanaNombre}"
            )
        }
    }

    fun asignarTipoHorarioADia(
        fechaIso: String,
        idTipoHorario: String,
        observacion: String,
        jornada: String = "Completa",
        hayClases: Boolean = true
    ) {
        _calendarioDias.update { lista ->
            val index = lista.indexOfFirst { it.fecha == fechaIso }
            val nuevo = CalendarioDia(
                fecha = fechaIso,
                idTipoHorario = idTipoHorario,
                observacion = observacion,
                jornada = jornada,
                hayClases = hayClases
            )
            val updatedList = if (index >= 0) {
                lista.toMutableList().apply { set(index, nuevo) }
            } else {
                lista + nuevo
            }
            preferences?.saveCalendario(updatedList)
            updatedList
        }
        val detalleClases = if (!hayClases) "Sin Clases" else "Con Clases ($idTipoHorario, $jornada)"
        agregarRegistro(
            tipo = TipoEvento.SINCRONIZACION,
            obs = "Asignado a $fechaIso: $detalleClases - $observacion"
        )
    }

    fun eliminarEventoDia(fechaIso: String) {
        _calendarioDias.update { lista ->
            val updatedList = lista.filterNot { it.fecha == fechaIso }
            preferences?.saveCalendario(updatedList)
            updatedList
        }
        agregarRegistro(
            tipo = TipoEvento.SINCRONIZACION,
            obs = "Restablecido día $fechaIso a la rutina semanal ordinaria"
        )
    }

    fun actualizarRutinaSemanal(nuevaRutina: Map<Int, String>) {
        _rutinaSemanal.value = nuevaRutina
        preferences?.saveRutinaSemanal(nuevaRutina)

        val resumen = nuevaRutina.entries.sortedBy { it.key }.joinToString(", ") { (dia, tipo) ->
            val diaNom = when (dia) {
                1 -> "Lun"
                2 -> "Mar"
                3 -> "Mié"
                4 -> "Jue"
                5 -> "Vie"
                else -> "D$dia"
            }
            val tipoNom = when (tipo) {
                "TIPO_A" -> "Horario A"
                "TIPO_B" -> "Horario B"
                "TIPO_C" -> "Horario C"
                else -> tipo
            }
            "$diaNom: $tipoNom"
        }
        agregarRegistro(
            tipo = TipoEvento.PROGRAMADO,
            obs = "Rutina semanal actualizada y guardada: $resumen por ${_usuarioActual.value.nombreCompleto}"
        )
    }

    fun cambiarEstadoBluetooth(nuevoEstado: BluetoothEstado) {
        _hardwareStatus.update { it.copy(estado = nuevoEstado) }
        agregarRegistro(
            tipo = TipoEvento.SINCRONIZACION,
            obs = "Estado Bluetooth ESP32: ${nuevoEstado.etiqueta}"
        )
    }

    fun sincronizarConEsp32(): String {
        _hardwareStatus.update { it.copy(estado = BluetoothEstado.SINCRONIZANDO) }
        val activas = _programaciones.value.filter { it.activo }
        val horaActualStr = SimpleDateFormat("HH:mm:ss dd/MM", Locale.getDefault()).format(Date())

        _hardwareStatus.update {
            it.copy(
                estado = BluetoothEstado.CONECTADO,
                ultimoSync = "Hoy ${horaActualStr.take(5)}",
                horariosCargadosEnFlash = activas.size
            )
        }

        agregarRegistro(
            tipo = TipoEvento.SINCRONIZACION,
            obs = "Sincronizados ${activas.size} toques y reloj RTC con ESP32 vía Bluetooth SPP"
        )
        return "Sincronización exitosa: ${activas.size} horarios transmitidos al ESP32."
    }

    fun activarTimbreManual(duracionSegundos: Int = 3) {
        _hardwareStatus.update { it.copy(releActivo = true) }
        agregarRegistro(
            tipo = TipoEvento.MANUAL,
            obs = "Toque manual activado por ${_usuarioActual.value.nombreCompleto} (Duración: ${duracionSegundos}s)"
        )
    }

    fun detenerTimbreManual() {
        _hardwareStatus.update { it.copy(releActivo = false) }
    }

    fun toggleAlarmaEvacuacion(activar: Boolean) {
        _hardwareStatus.update {
            it.copy(
                modoEvacuacionActivo = activar,
                releActivo = activar
            )
        }

        val accion = if (activar) "INICIADA ALARMA DE EVACUACIÓN (Ciclo continuo 2s timbre / 1s silencio)" else "DETENIDA ALARMA DE EVACUACIÓN"
        agregarRegistro(
            tipo = TipoEvento.EVACUACION,
            obs = "$accion por orden de ${_usuarioActual.value.nombreCompleto}"
        )
    }

    fun agregarRegistro(tipo: TipoEvento, obs: String) {
        val now = Date()
        val nuevoLog = RegistroLog(
            id = "reg_${UUID.randomUUID().toString().take(8)}",
            fecha = dateFormat.format(now),
            hora = timeFormat.format(now),
            tipoEvento = tipo,
            usuarioUid = _usuarioActual.value.uid,
            nombreUsuario = "${_usuarioActual.value.nombreCompleto} (${_usuarioActual.value.rol.etiqueta})",
            observacion = obs,
            ejecutado = true
        )
        _registros.update { current ->
            val updated = listOf(nuevoLog) + current
            preferences?.saveRegistros(updated)
            updated
        }
    }

    private fun generarProgramacionesIniciales(): List<Programacion> {
        val lista = mutableListOf<Programacion>()
        val bloquesTipoA = listOf(
            Triple("06:45", 5, "Entrada General y Formación"),
            Triple("07:00", 4, "Inicio Hora 1"),
            Triple("07:55", 3, "Cambio de Clase - Hora 2"),
            Triple("08:50", 3, "Cambio de Clase - Hora 3"),
            Triple("09:45", 5, "Inicio Primer Descanso"),
            Triple("10:15", 4, "Fin Descanso / Inicio Hora 4"),
            Triple("11:10", 3, "Cambio de Clase - Hora 5"),
            Triple("12:05", 5, "Inicio Segundo Descanso / Almuerzo"),
            Triple("12:45", 4, "Fin Descanso / Inicio Hora 6"),
            Triple("13:40", 3, "Cambio de Clase - Hora 7"),
            Triple("14:30", 6, "Salida General de Estudiantes")
        )

        for (dia in 1..5) {
            bloquesTipoA.forEachIndexed { i, bloque ->
                lista.add(
                    Programacion(
                        id = "prog_a_d${dia}_$i",
                        idTipoHorario = "TIPO_A",
                        diaSemana = dia,
                        hora = bloque.first,
                        duracionSegundos = bloque.second,
                        idTimbre = "CAMPANA_CENTRAL",
                        activo = true,
                        etiqueta = bloque.third
                    )
                )
            }
        }

        val bloquesTipoB = listOf(
            Triple("07:00", 4, "Inicio Jornada Especial"),
            Triple("07:45", 3, "Cambio Clase 1-2"),
            Triple("08:30", 3, "Cambio Clase 2-3"),
            Triple("09:15", 5, "Acto Cívico / Receso"),
            Triple("10:00", 4, "Reanudación Bloque 4"),
            Triple("10:45", 3, "Cambio Clase 4-5"),
            Triple("11:30", 3, "Cambio Clase 5-6"),
            Triple("12:15", 5, "Salida Jornada Pedagógica")
        )
        for (dia in 1..5) {
            bloquesTipoB.forEachIndexed { i, bloque ->
                lista.add(
                    Programacion(
                        id = "prog_b_d${dia}_$i",
                        idTipoHorario = "TIPO_B",
                        diaSemana = dia,
                        hora = bloque.first,
                        duracionSegundos = bloque.second,
                        idTimbre = "CAMPANA_CENTRAL",
                        activo = true,
                        etiqueta = bloque.third
                    )
                )
            }
        }

        val bloquesTipoC = listOf(
            Triple("07:00", 4, "Entrada Jornada Reducida"),
            Triple("07:50", 3, "Cambio de Bloque 1"),
            Triple("08:40", 3, "Cambio de Bloque 2"),
            Triple("09:30", 4, "Receso Breve 15 min"),
            Triple("09:45", 3, "Inicio Bloque 4"),
            Triple("10:35", 3, "Cambio de Bloque 5"),
            Triple("11:25", 3, "Cambio de Bloque 6"),
            Triple("12:15", 5, "Salida Jornada Continua")
        )
        for (dia in 1..5) {
            bloquesTipoC.forEachIndexed { i, bloque ->
                lista.add(
                    Programacion(
                        id = "prog_c_d${dia}_$i",
                        idTipoHorario = "TIPO_C",
                        diaSemana = dia,
                        hora = bloque.first,
                        duracionSegundos = bloque.second,
                        idTimbre = "CAMPANA_CENTRAL",
                        activo = true,
                        etiqueta = bloque.third
                    )
                )
            }
        }

        return lista
    }

    private fun generarCalendarioInicial(): List<CalendarioDia> {
        val lista = mutableListOf<CalendarioDia>()
        val cal = java.util.Calendar.getInstance()
        for (i in 0..20) {
            val dateStr = dateFormat.format(cal.time)
            val dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK)
            val isWeekend = dayOfWeek == java.util.Calendar.SATURDAY || dayOfWeek == java.util.Calendar.SUNDAY
            val tipo = if (isWeekend) "SIN_CLASES" else when (i) {
                4 -> "TIPO_B"
                8 -> "TIPO_C"
                12 -> "SIN_CLASES"
                16 -> "TIPO_B"
                else -> "TIPO_A"
            }
            val hayClases = !isWeekend && tipo != "SIN_CLASES"
            val jornada = if (hayClases) {
                when (tipo) {
                    "TIPO_B" -> "Mañana"
                    "TIPO_C" -> "Tarde"
                    else -> "Completa"
                }
            } else "Completa"
            val obs = when {
                isWeekend -> "Fin de semana institucional"
                i == 4 -> "Viernes de acto cívico y jornada pedagógica"
                i == 8 -> "Jornada de evaluaciones trimestrales reducida"
                i == 12 -> "Día cívico institucional - Suspensión de clases"
                i == 16 -> "Encuentro deportivo intercolegial"
                else -> "Jornada escolar ordinaria"
            }
            lista.add(CalendarioDia(dateStr, tipo, obs, jornada, hayClases))
            cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }
        return lista
    }

    private fun generarRegistrosIniciales(): List<RegistroLog> {
        val todayStr = dateFormat.format(Date())
        return listOf(
            RegistroLog(
                id = "reg_init_01",
                fecha = todayStr,
                hora = "07:00:02",
                tipoEvento = TipoEvento.PROGRAMADO,
                usuarioUid = "sistema_autonomo_esp32",
                nombreUsuario = "ESP32 Timer Autónomo",
                observacion = "Disparo automático: Timbre 07:00 (Inicio Hora 1) - Duración: 4s",
                ejecutado = true
            ),
            RegistroLog(
                id = "reg_init_02",
                fecha = todayStr,
                hora = "06:45:00",
                tipoEvento = TipoEvento.PROGRAMADO,
                usuarioUid = "sistema_autonomo_esp32",
                nombreUsuario = "ESP32 Timer Autónomo",
                observacion = "Disparo automático: Timbre 06:45 (Entrada General) - Duración: 5s",
                ejecutado = true
            ),
            RegistroLog(
                id = "reg_init_03",
                fecha = todayStr,
                hora = "06:30:15",
                tipoEvento = TipoEvento.SINCRONIZACION,
                usuarioUid = usuarioAdmin.uid,
                nombreUsuario = "${usuarioAdmin.nombreCompleto} (Administrador)",
                observacion = "Sincronización Bluetooth con ESP32: Verificación de RTC y tabla horaria Tipo A",
                ejecutado = true
            )
        )
    }
}
