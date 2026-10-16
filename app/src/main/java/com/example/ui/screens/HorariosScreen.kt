package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Programacion
import com.example.model.TipoHorario
import com.example.ui.theme.*

@Composable
fun HorariosScreen(
    tiposHorario: List<TipoHorario>,
    selectedTipoId: String,
    onSelectTipo: (String) -> Unit,
    selectedDia: Int,
    onSelectDia: (Int) -> Unit,
    rutinaSemanal: Map<Int, String>,
    tempRutinaSemanal: Map<Int, String>,
    onActualizarTempRutinaDia: (Int, String) -> Unit,
    onGuardarRutinaSemanal: () -> Unit,
    programaciones: List<Programacion>,
    onToggleProgramacion: (String) -> Unit,
    onEditarProgramacion: (Programacion) -> Unit,
    onNuevaProgramacion: () -> Unit,
    onEliminarProgramacion: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val diasSemana = listOf(
        Pair(1, "Lunes"),
        Pair(2, "Martes"),
        Pair(3, "Miércoles"),
        Pair(4, "Jueves"),
        Pair(5, "Viernes")
    )

    // Filtrar los toques del día seleccionado y tipo de horario vigente para ese día
    val tipoDelDia = rutinaSemanal[selectedDia] ?: selectedTipoId
    val listaFiltrada = programaciones
        .filter { it.idTipoHorario == tipoDelDia && it.diaSemana == selectedDia }
        .sortedBy { it.hora }

    val tipoActual = tiposHorario.find { it.id == tipoDelDia }
    val totalSegundos = listaFiltrada.filter { it.activo }.sumOf { it.duracionSegundos }
    val toquesActivosCount = listaFiltrada.count { it.activo }
    val hayCambiosPendientes = tempRutinaSemanal != rutinaSemanal

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNuevaProgramacion,
                containerColor = NeonGreen,
                contentColor = BackgroundDark,
                icon = { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp)) },
                text = { Text("Nuevo Toque", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("fab_add_schedule")
            )
        },
        containerColor = BackgroundDark
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. PANEL DE CONFIGURACIÓN SEMANAL (Lunes a Viernes con selector desplegable y botón Guardar Horario)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, CardBorderDark, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = CardSurfaceDark)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = NeonGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Régimen Escolar Semanal",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                            }

                            if (hayCambiosPendientes) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = AmberWarning.copy(alpha = 0.15f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, AmberWarning.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = "Pendiente",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AmberWarning,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Asigna el tipo de horario (A, B o C) para cada día de la semana y pulsa 'Guardar Horario':",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        // Lista de días de la semana con dropdown por día
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            diasSemana.forEach { (diaNum, diaNombre) ->
                                val tipoSeleccionado = tempRutinaSemanal[diaNum] ?: "TIPO_A"
                                DiaHorarioSelectorRow(
                                    diaNombre = diaNombre,
                                    tipoActualId = tipoSeleccionado,
                                    tiposHorario = tiposHorario,
                                    onTipoSelected = { nuevoTipo ->
                                        onActualizarTempRutinaDia(diaNum, nuevoTipo)
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        // Botón GUARDAR HORARIO
                        Button(
                            onClick = onGuardarRutinaSemanal,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_guardar_horario_semanal"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonGreen,
                                contentColor = BackgroundDark
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Guardar Horario",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }

            // 2. SELECTOR RÁPIDO DE DÍA A VISUALIZAR
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Días de la Semana",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "Selecciona para ver toques",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(diasSemana) { (num, nombre) ->
                        val isSelected = num == selectedDia
                        val tipoAsignado = rutinaSemanal[num] ?: "TIPO_A"
                        val (badgeColor, badgeLetra) = when (tipoAsignado) {
                            "TIPO_A" -> Pair(NeonGreen, "A")
                            "TIPO_B" -> Pair(ElectricCyan, "B")
                            "TIPO_C" -> Pair(AmberWarning, "C")
                            else -> Pair(SlateGray, "-")
                        }

                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) ElectricCyan else CardBorderDark,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { onSelectDia(num) }
                                .testTag("day_chip_$num"),
                            color = if (isSelected) ElectricCyan.copy(alpha = 0.15f) else CardSurfaceDark
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = nombre,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) TextWhite else TextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                                Surface(
                                    shape = CircleShape,
                                    color = badgeColor.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.6f))
                                ) {
                                    Text(
                                        text = badgeLetra,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = badgeColor,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. TARJETA RESUMEN DE LOS TIMBRES DEL DÍA SELECCIONADO
            item {
                val diaActualNombre = diasSemana.find { it.first == selectedDia }?.second ?: "Día"
                val tipoNombre = tipoActual?.nombre ?: "Horario Asignado"

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurfaceDark),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, CardBorderDark)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(NeonGreen)
                                )
                                Text(
                                    text = "Resumen del $diaActualNombre",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = NeonGreen.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, NeonGreen.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "$toquesActivosCount toques activos",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = tipoNombre,
                                style = MaterialTheme.typography.bodySmall,
                                color = ElectricCyan,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Total sonido: ${totalSegundos}s",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // 4. LISTA COMPACTA DE TIMBRES (CUADROS MÁS PEQUEÑOS Y ESTILIZADOS)
            if (listaFiltrada.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSurfaceDark)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsOff,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = "Sin toques registrados para este día",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextWhite
                            )
                            Text(
                                text = "Pulsa '+ Nuevo Toque' para agregar un timbre",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            } else {
                items(listaFiltrada, key = { it.id }) { prog ->
                    CompactProgramacionCard(
                        programacion = prog,
                        onToggle = { onToggleProgramacion(prog.id) },
                        onEdit = { onEditarProgramacion(prog) },
                        onDelete = { onEliminarProgramacion(prog.id) }
                    )
                }
            }
        }
    }
}

/**
 * Fila con selector desplegable de tipo de horario para un día específico
 */
@Composable
fun DiaHorarioSelectorRow(
    diaNombre: String,
    tipoActualId: String,
    tiposHorario: List<TipoHorario>,
    onTipoSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val tipoObj = tiposHorario.find { it.id == tipoActualId }
    val etiqueta = tipoObj?.etiquetaCorta ?: "Tipo A"

    val (colorBadge, letra) = when (tipoActualId) {
        "TIPO_A" -> Pair(NeonGreen, "Horario A")
        "TIPO_B" -> Pair(ElectricCyan, "Horario B")
        "TIPO_C" -> Pair(AmberWarning, "Horario C")
        else -> Pair(SlateGray, tipoActualId)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceDark)
            .border(0.5.dp, CardBorderDark, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = diaNombre,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextWhite
            )
        }

        // Selector desplegable compacto
        Box {
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .border(1.dp, colorBadge.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                    .clickable { expanded = true }
                    .testTag("dropdown_selector_${diaNombre.lowercase()}"),
                color = colorBadge.copy(alpha = 0.12f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = letra,
                        style = MaterialTheme.typography.labelSmall,
                        color = colorBadge,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Cambiar tipo",
                        tint = colorBadge,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(CardSurfaceDark)
                    .border(1.dp, CardBorderDark)
            ) {
                tiposHorario.forEach { tipo ->
                    val isCurrent = tipo.id == tipoActualId
                    val itemColor = when (tipo.id) {
                        "TIPO_A" -> NeonGreen
                        "TIPO_B" -> ElectricCyan
                        "TIPO_C" -> AmberWarning
                        else -> SlateGray
                    }

                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(itemColor)
                                )
                                Column {
                                    Text(
                                        text = tipo.nombre,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isCurrent) itemColor else TextWhite,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Text(
                                        text = tipo.descripcion,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary,
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        },
                        onClick = {
                            onTipoSelected(tipo.id)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

/**
 * Tarjeta compacta para cada toque de timbre (diseño estilizado de baja altura)
 */
@Composable
fun CompactProgramacionCard(
    programacion: Programacion,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = if (programacion.activo) 1.dp else 0.5.dp,
                color = if (programacion.activo) CardBorderDark else SlateGray.copy(alpha = 0.25f),
                shape = RoundedCornerShape(10.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (programacion.activo) CardSurfaceDark else CardSurfaceDark.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Badge compacto de hora (Monospace)
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (programacion.activo) NeonGreen.copy(alpha = 0.15f) else SlateGray.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(
                    0.8.dp,
                    if (programacion.activo) NeonGreen.copy(alpha = 0.5f) else SlateGray.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = programacion.hora,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    fontWeight = FontWeight.Bold,
                    color = if (programacion.activo) NeonGreen else TextSecondary
                )
            }

            // Etiqueta y pastilla de duración
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = programacion.etiqueta,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (programacion.activo) TextWhite else TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = ElectricCyan.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "${programacion.duracionSegundos}s",
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Acciones: Editar, Eliminar y Switch rápido
            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(28.dp).testTag("edit_schedule_${programacion.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = TextSecondary,
                    modifier = Modifier.size(15.dp)
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(28.dp).testTag("delete_schedule_${programacion.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = CrimsonNeon.copy(alpha = 0.7f),
                    modifier = Modifier.size(15.dp)
                )
            }

            Switch(
                checked = programacion.activo,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = BackgroundDark,
                    checkedTrackColor = NeonGreen,
                    uncheckedThumbColor = TextSecondary,
                    uncheckedTrackColor = SurfaceDark
                ),
                modifier = Modifier
                    .scale(0.8f)
                    .testTag("switch_schedule_${programacion.id}")
            )
        }
    }
}
