package com.example.model

enum class RolUsuario(val valor: String, val etiqueta: String) {
    ADMINISTRADOR("administrador", "Administrador"),
    DIRECTIVO("directivo", "Directivo");

    companion object {
        fun fromValor(valor: String): RolUsuario =
            entries.firstOrNull { it.valor.equals(valor, ignoreCase = true) } ?: DIRECTIVO
    }
}

enum class TipoEvento(val etiqueta: String) {
    PROGRAMADO("Programado"),
    MANUAL("Toque Manual"),
    EVACUACION("Alarma Evacuación"),
    SINCRONIZACION("Sincronización IoT"),
    SESION("Registro de Sesión")
}

enum class BluetoothEstado(val etiqueta: String) {
    DESCONECTADO("Desconectado"),
    CONECTANDO("Conectando..."),
    CONECTADO("Conectado (SPP)"),
    SINCRONIZANDO("Sincronizando..."),
    ERROR("Error de Enlace")
}

data class InstitucionConfig(
    val nombreColegio: String = "Institución Educativa Técnico Industrial San José",
    val sede: String = "Sede Principal - Campus Central",
    val anoLectivo: Int = 2026,
    val actualizadoPor: String = "admin_master",
    val fechaActualizacion: String = "01/10/2026 08:30"
)

data class Usuario(
    val uid: String,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val rol: RolUsuario,
    val estado: Boolean = true,
    val fechaCreacion: String = "01/01/2026"
) {
    val nombreCompleto: String get() = "$nombre $apellido"
}

data class TipoHorario(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val etiquetaCorta: String
)

data class Programacion(
    val id: String,
    val idTipoHorario: String,
    val diaSemana: Int, // 1 = Lunes, 2 = Martes, 3 = Miércoles, 4 = Jueves, 5 = Viernes, 6 = Sábado, 7 = Domingo
    val hora: String,   // HH:mm
    val duracionSegundos: Int,
    val idTimbre: String = "CAMPANA_CENTRAL",
    val activo: Boolean = true,
    val etiqueta: String = "Toque Escolar"
) {
    val diaSemanaNombre: String
        get() = when (diaSemana) {
            1 -> "Lunes"
            2 -> "Martes"
            3 -> "Miércoles"
            4 -> "Jueves"
            5 -> "Viernes"
            6 -> "Sábado"
            7 -> "Domingo"
            else -> "Día $diaSemana"
        }
}

data class CalendarioDia(
    val fecha: String, // YYYY-MM-DD
    val idTipoHorario: String,
    val observacion: String,
    val jornada: String = "Completa", // "Mañana", "Tarde", "Completa"
    val hayClases: Boolean = true
)

data class RegistroLog(
    val id: String,
    val fecha: String,
    val hora: String,
    val tipoEvento: TipoEvento,
    val usuarioUid: String,
    val nombreUsuario: String,
    val observacion: String,
    val ejecutado: Boolean = true
)

data class Esp32HardwareStatus(
    val nombre: String = "ESP32_TIMESCHOOL_BELL",
    val mac: String = "24:6F:28:B2:7A:1C",
    val uuidSpp: String = "00001101-0000-1000-8000-00805F9B34FB",
    val estado: BluetoothEstado = BluetoothEstado.DESCONECTADO,
    val ultimoSync: String = "Pendiente",
    val horariosCargadosEnFlash: Int = 0,
    val releActivo: Boolean = false,
    val modoEvacuacionActivo: Boolean = false
)
