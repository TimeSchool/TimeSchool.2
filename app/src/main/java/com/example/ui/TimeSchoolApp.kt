package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TimeSchoolTheme
import com.example.viewmodel.ScreenTab
import com.example.viewmodel.TimeSchoolViewModel

@Composable
fun TimeSchoolApp(
    viewModel: TimeSchoolViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val institucion by viewModel.institucion.collectAsStateWithLifecycle()
    val usuarioActual by viewModel.usuarioActual.collectAsStateWithLifecycle()
    val tiposHorario by viewModel.tiposHorario.collectAsStateWithLifecycle()
    val programaciones by viewModel.programaciones.collectAsStateWithLifecycle()
    val calendarioDias by viewModel.calendarioDias.collectAsStateWithLifecycle()
    val registros by viewModel.registros.collectAsStateWithLifecycle()
    val hardwareStatus by viewModel.hardwareStatus.collectAsStateWithLifecycle()
    val correoGuardado by viewModel.correoGuardado.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar feedback en Snackbar
    LaunchedEffect(uiState.mensajeFeedback) {
        uiState.mensajeFeedback?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.limpiarFeedback()
        }
    }

    // Manejo de botón Atrás nativo
    BackHandler(enabled = uiState.currentTab != ScreenTab.DASHBOARD) {
        viewModel.selectTab(ScreenTab.DASHBOARD)
    }

    TimeSchoolTheme {
        if (!uiState.sesionIniciada) {
            RegistroSesionScreen(
                institucion = institucion,
                usuariosDisponibles = uiState.usuariosDisponibles,
                horaActual = uiState.horaActualReloj,
                correoGuardado = correoGuardado,
                onLimpiarCorreo = { viewModel.limpiarCorreoGuardado() },
                onAutenticar = { user, pass, recordar, terminos ->
                    viewModel.autenticarUsuario(user, pass, recordar, terminos)
                },
                onRegistrarNuevoUsuario = { nom, ape, rol, mail, pass ->
                    viewModel.registrarNuevoUsuarioYSesion(nom, ape, rol, mail, pass)
                }
            )
        } else {
            Scaffold(
                topBar = {
                    TimeSchoolTopBar(
                        institucion = institucion,
                        usuarioActual = usuarioActual,
                        bluetoothEstado = hardwareStatus.estado,
                        onAlternarRol = { viewModel.alternarUsuarioRol() },
                        onBluetoothClick = { viewModel.toggleBluetoothConnection() },
                        onCerrarSesion = { viewModel.cerrarSesion() }
                    )
                },
            bottomBar = {
                TimeSchoolBottomBar(
                    currentTab = uiState.currentTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.navigationBarsPadding()
                ) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = if (uiState.isErrorFeedback) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        actionColor = NeonGreen
                    )
                }
            },
            containerColor = BackgroundDark,
            contentWindowInsets = WindowInsets.safeDrawing
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (uiState.currentTab) {
                    ScreenTab.DASHBOARD -> {
                        DashboardScreen(
                            institucion = institucion,
                            usuarioActual = usuarioActual,
                            hardwareStatus = hardwareStatus,
                            programaciones = programaciones,
                            registros = registros,
                            horaActual = uiState.horaActualReloj,
                            proximoTimbreTexto = uiState.proximoTimbreTexto,
                            tiempoParaProximo = uiState.minutosParaProximoTimbre,
                            onNavigateTab = { viewModel.selectTab(it) },
                            onManualRing = { viewModel.presionarTimbreManual(3) },
                            onSyncEsp32 = { viewModel.sincronizarHorariosConEsp32() }
                        )
                    }

                    ScreenTab.HORARIOS -> {
                        HorariosScreen(
                            tiposHorario = tiposHorario,
                            selectedTipoId = uiState.selectedTipoHorarioId,
                            onSelectTipo = { viewModel.selectTipoHorario(it) },
                            selectedDia = uiState.selectedDiaSemana,
                            onSelectDia = { viewModel.selectDiaSemana(it) },
                            rutinaSemanal = uiState.rutinaSemanal,
                            tempRutinaSemanal = uiState.tempRutinaSemanal,
                            onActualizarTempRutinaDia = { dia, tipo -> viewModel.actualizarTempRutinaDia(dia, tipo) },
                            onGuardarRutinaSemanal = { viewModel.guardarRutinaSemanal() },
                            programaciones = programaciones,
                            onToggleProgramacion = { viewModel.toggleProgramacion(it) },
                            onEditarProgramacion = { viewModel.abrirDialogoEditarProgramacion(it) },
                            onNuevaProgramacion = { viewModel.abrirDialogoNuevaProgramacion() },
                            onEliminarProgramacion = { viewModel.eliminarProgramacion(it) }
                        )
                    }

                    ScreenTab.CALENDARIO -> {
                        CalendarioScreen(
                            calendarioDias = calendarioDias,
                            tiposHorario = tiposHorario,
                            rutinaSemanal = uiState.rutinaSemanal,
                            programaciones = programaciones,
                            onEditarDia = { viewModel.abrirDialogoEditarDia(it) }
                        )
                    }

                    ScreenTab.CONTROL_IOT -> {
                        ControlIotScreen(
                            hardwareStatus = hardwareStatus,
                            onToggleBluetooth = { viewModel.toggleBluetoothConnection() },
                            onSyncEsp32 = { viewModel.sincronizarHorariosConEsp32() },
                            onPresionarTimbreManual = { viewModel.presionarTimbreManual(it) },
                            onSoltarTimbreManual = { viewModel.soltarTimbreManual() },
                            onActivarEvacuacion = { viewModel.activarAlarmaEvacuacion() },
                            onDesactivarEvacuacion = { viewModel.desactivarAlarmaEvacuacion() }
                        )
                    }

                    ScreenTab.BITACORA -> {
                        BitacoraScreen(
                            registros = registros,
                            filtroTexto = uiState.filtroLogTexto,
                            filtroTipo = uiState.filtroLogTipo,
                            onSetFiltro = { texto, tipo -> viewModel.setFiltroLog(texto, tipo) }
                        )
                    }

                    ScreenTab.CONFIGURACION -> {
                        ConfiguracionScreen(
                            institucion = institucion,
                            usuarioActual = usuarioActual,
                            onGuardarConfig = { nombre, sede, ano ->
                                viewModel.actualizarInstitucion(nombre, sede, ano)
                            },
                            onAlternarRol = { viewModel.alternarUsuarioRol() },
                            onCerrarSesion = { viewModel.cerrarSesion() }
                        )
                    }
                }

                // Diálogos modales
                if (uiState.showDialogEditarProgramacion && uiState.programacionEnEdicion != null) {
                    EditProgramacionDialog(
                        programacion = uiState.programacionEnEdicion!!,
                        onDismiss = { viewModel.cerrarDialogoProgramacion() },
                        onSave = { viewModel.guardarProgramacion(it) }
                    )
                }

                if (uiState.showDialogEditarDia && uiState.diaEnEdicion != null) {
                    EditDiaDialog(
                        dia = uiState.diaEnEdicion!!,
                        onDismiss = { viewModel.cerrarDialogoEditarDia() },
                        onSave = { fecha, tipoId, obs, jornada, hayClases ->
                            viewModel.guardarAsignacionDia(fecha, tipoId, obs, jornada, hayClases)
                        },
                        onEliminar = { fecha ->
                            viewModel.eliminarEventoDia(fecha)
                        }
                    )
                }
            }
        }
    }
}
}
