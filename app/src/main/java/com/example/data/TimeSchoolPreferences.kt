package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.*
import org.json.JSONArray
import org.json.JSONObject

class TimeSchoolPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("timeschool_persistent_storage", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_RUTINA_SEMANAL = "key_rutina_semanal"
        private const val KEY_PROGRAMACIONES = "key_programaciones"
        private const val KEY_INSTITUCION = "key_institucion"
        private const val KEY_CALENDARIO = "key_calendario"
        private const val KEY_REGISTROS = "key_registros"
        private const val KEY_USUARIO_ACTUAL = "key_usuario_actual"
        private const val KEY_CORREO_GUARDADO = "key_correo_guardado"
    }

    // --- RUTINA SEMANAL ---
    fun saveRutinaSemanal(rutina: Map<Int, String>) {
        val json = JSONObject()
        rutina.forEach { (dia, tipo) ->
            json.put(dia.toString(), tipo)
        }
        prefs.edit().putString(KEY_RUTINA_SEMANAL, json.toString()).apply()
    }

    fun loadRutinaSemanal(): Map<Int, String>? {
        val raw = prefs.getString(KEY_RUTINA_SEMANAL, null) ?: return null
        return try {
            val json = JSONObject(raw)
            val map = mutableMapOf<Int, String>()
            val keys = json.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                k.toIntOrNull()?.let { diaInt ->
                    map[diaInt] = json.getString(k)
                }
            }
            if (map.isNotEmpty()) map else null
        } catch (e: Exception) {
            null
        }
    }

    // --- PROGRAMACIONES ---
    fun saveProgramaciones(lista: List<Programacion>) {
        val array = JSONArray()
        lista.forEach { prog ->
            val obj = JSONObject().apply {
                put("id", prog.id)
                put("idTipoHorario", prog.idTipoHorario)
                put("diaSemana", prog.diaSemana)
                put("hora", prog.hora)
                put("duracionSegundos", prog.duracionSegundos)
                put("idTimbre", prog.idTimbre)
                put("activo", prog.activo)
                put("etiqueta", prog.etiqueta)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_PROGRAMACIONES, array.toString()).apply()
    }

    fun loadProgramaciones(): List<Programacion>? {
        val raw = prefs.getString(KEY_PROGRAMACIONES, null) ?: return null
        return try {
            val array = JSONArray(raw)
            val lista = mutableListOf<Programacion>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                lista.add(
                    Programacion(
                        id = obj.getString("id"),
                        idTipoHorario = obj.getString("idTipoHorario"),
                        diaSemana = obj.getInt("diaSemana"),
                        hora = obj.getString("hora"),
                        duracionSegundos = obj.getInt("duracionSegundos"),
                        idTimbre = obj.optString("idTimbre", "CAMPANA_CENTRAL"),
                        activo = obj.getBoolean("activo"),
                        etiqueta = obj.optString("etiqueta", "Toque Escolar")
                    )
                )
            }
            if (lista.isNotEmpty()) lista else null
        } catch (e: Exception) {
            null
        }
    }

    // --- INSTITUCION ---
    fun saveInstitucion(config: InstitucionConfig) {
        val obj = JSONObject().apply {
            put("nombreColegio", config.nombreColegio)
            put("sede", config.sede)
            put("anoLectivo", config.anoLectivo)
            put("actualizadoPor", config.actualizadoPor)
            put("fechaActualizacion", config.fechaActualizacion)
        }
        prefs.edit().putString(KEY_INSTITUCION, obj.toString()).apply()
    }

    fun loadInstitucion(): InstitucionConfig? {
        val raw = prefs.getString(KEY_INSTITUCION, null) ?: return null
        return try {
            val obj = JSONObject(raw)
            InstitucionConfig(
                nombreColegio = obj.getString("nombreColegio"),
                sede = obj.getString("sede"),
                anoLectivo = obj.getInt("anoLectivo"),
                actualizadoPor = obj.getString("actualizadoPor"),
                fechaActualizacion = obj.getString("fechaActualizacion")
            )
        } catch (e: Exception) {
            null
        }
    }

    // --- CALENDARIO DIAS ---
    fun saveCalendario(lista: List<CalendarioDia>) {
        val array = JSONArray()
        lista.forEach { dia ->
            val obj = JSONObject().apply {
                put("fecha", dia.fecha)
                put("idTipoHorario", dia.idTipoHorario)
                put("observacion", dia.observacion)
                put("jornada", dia.jornada)
                put("hayClases", dia.hayClases)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_CALENDARIO, array.toString()).apply()
    }

    fun loadCalendario(): List<CalendarioDia>? {
        val raw = prefs.getString(KEY_CALENDARIO, null) ?: return null
        return try {
            val array = JSONArray(raw)
            val lista = mutableListOf<CalendarioDia>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                lista.add(
                    CalendarioDia(
                        fecha = obj.getString("fecha"),
                        idTipoHorario = obj.getString("idTipoHorario"),
                        observacion = obj.getString("observacion"),
                        jornada = obj.optString("jornada", "Completa"),
                        hayClases = obj.optBoolean("hayClases", obj.getString("idTipoHorario") != "SIN_CLASES")
                    )
                )
            }
            if (lista.isNotEmpty()) lista else null
        } catch (e: Exception) {
            null
        }
    }

    // --- REGISTROS BITACORA ---
    fun saveRegistros(lista: List<RegistroLog>) {
        val array = JSONArray()
        lista.take(50).forEach { reg -> // Guardar los 50 más recientes para rendimiento óptimo
            val obj = JSONObject().apply {
                put("id", reg.id)
                put("fecha", reg.fecha)
                put("hora", reg.hora)
                put("tipoEvento", reg.tipoEvento.name)
                put("usuarioUid", reg.usuarioUid)
                put("nombreUsuario", reg.nombreUsuario)
                put("observacion", reg.observacion)
                put("ejecutado", reg.ejecutado)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_REGISTROS, array.toString()).apply()
    }

    fun loadRegistros(): List<RegistroLog>? {
        val raw = prefs.getString(KEY_REGISTROS, null) ?: return null
        return try {
            val array = JSONArray(raw)
            val lista = mutableListOf<RegistroLog>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val tipoNombre = obj.getString("tipoEvento")
                val tipoEvento = try {
                    TipoEvento.valueOf(tipoNombre)
                } catch (e: Exception) {
                    TipoEvento.PROGRAMADO
                }
                lista.add(
                    RegistroLog(
                        id = obj.getString("id"),
                        fecha = obj.getString("fecha"),
                        hora = obj.getString("hora"),
                        tipoEvento = tipoEvento,
                        usuarioUid = obj.getString("usuarioUid"),
                        nombreUsuario = obj.getString("nombreUsuario"),
                        observacion = obj.getString("observacion"),
                        ejecutado = obj.optBoolean("ejecutado", true)
                    )
                )
            }
            if (lista.isNotEmpty()) lista else null
        } catch (e: Exception) {
            null
        }
    }

    // --- CORREO O USUARIO GUARDADO PARA ACCESO RÁPIDO ---
    fun saveCorreoGuardado(correoOUser: String) {
        prefs.edit().putString(KEY_CORREO_GUARDADO, correoOUser).apply()
    }

    fun loadCorreoGuardado(): String? {
        return prefs.getString(KEY_CORREO_GUARDADO, null)
    }

    fun clearCorreoGuardado() {
        prefs.edit().remove(KEY_CORREO_GUARDADO).apply()
    }
}
