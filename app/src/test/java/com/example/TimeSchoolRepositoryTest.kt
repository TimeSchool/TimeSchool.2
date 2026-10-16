package com.example

import com.example.data.TimeSchoolRepository
import com.example.model.RolUsuario
import com.example.model.TipoEvento
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TimeSchoolRepositoryTest {

    private lateinit var repository: TimeSchoolRepository

    @Before
    fun setUp() {
        repository = TimeSchoolRepository()
    }

    @Test
    fun testInitialConfiguration() {
        val config = repository.institucion.value
        assertEquals("Institución Educativa Técnico Industrial San José", config.nombreColegio)
        assertEquals(2026, config.anoLectivo)
    }

    @Test
    fun testAdminCanUpdateConfiguration() {
        repository.cambiarUsuario(repository.usuarioAdmin)
        val success = repository.actualizarInstitucion("Colegio Central San José", "Sede Norte", 2026)
        assertTrue(success)
        assertEquals("Colegio Central San José", repository.institucion.value.nombreColegio)
    }

    @Test
    fun testDirectivoCannotUpdateConfigurationDueToSecurityRule() {
        repository.cambiarUsuario(repository.usuarioDirectivo)
        val success = repository.actualizarInstitucion("Nombre No Autorizado", "Sede", 2026)
        assertFalse(success)
        assertNotEquals("Nombre No Autorizado", repository.institucion.value.nombreColegio)
    }

    @Test
    fun testToggleScheduleActiveState() {
        val firstProg = repository.programaciones.value.first()
        val originalState = firstProg.activo
        repository.toggleProgramacionActiva(firstProg.id)

        val updatedProg = repository.programaciones.value.first { it.id == firstProg.id }
        assertEquals(!originalState, updatedProg.activo)
    }

    @Test
    fun testManualBellGeneratesImmutableLog() {
        val logsBefore = repository.registros.value.size
        repository.activarTimbreManual(3)
        val logsAfter = repository.registros.value.size

        assertEquals(logsBefore + 1, logsAfter)
        val latestLog = repository.registros.value.first()
        assertEquals(TipoEvento.MANUAL, latestLog.tipoEvento)
        assertTrue(latestLog.ejecutado)
    }

    @Test
    fun testActualizarRutinaSemanal() {
        val nuevaRutina = mapOf(
            1 to "TIPO_A",
            2 to "TIPO_B",
            3 to "TIPO_A",
            4 to "TIPO_B",
            5 to "TIPO_C"
        )
        repository.actualizarRutinaSemanal(nuevaRutina)
        assertEquals(nuevaRutina, repository.rutinaSemanal.value)
        assertEquals("TIPO_B", repository.rutinaSemanal.value[2])
        assertEquals("TIPO_C", repository.rutinaSemanal.value[5])

        val latestLog = repository.registros.value.first()
        assertEquals(TipoEvento.PROGRAMADO, latestLog.tipoEvento)
        assertTrue(latestLog.observacion.contains("Rutina semanal actualizada"))
    }

    @Test
    fun testAsignarTipoHorarioADiaConJornadaYClases() {
        val testFecha = "2026-11-20"
        repository.asignarTipoHorarioADia(
            fechaIso = testFecha,
            idTipoHorario = "TIPO_B",
            observacion = "Festival de Ciencias y Danzas",
            jornada = "Mañana",
            hayClases = true
        )

        val diaGuardado = repository.calendarioDias.value.find { it.fecha == testFecha }
        assertNotNull(diaGuardado)
        assertEquals("TIPO_B", diaGuardado?.idTipoHorario)
        assertEquals("Mañana", diaGuardado?.jornada)
        assertTrue(diaGuardado?.hayClases == true)
        assertEquals("Festival de Ciencias y Danzas", diaGuardado?.observacion)

        // Test eliminar y restablecer a rutina
        repository.eliminarEventoDia(testFecha)
        val diaEliminado = repository.calendarioDias.value.find { it.fecha == testFecha }
        assertNull(diaEliminado)
    }
}
