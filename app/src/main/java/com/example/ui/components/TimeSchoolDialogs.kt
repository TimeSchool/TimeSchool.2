package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Label
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.CalendarioDia
import com.example.model.Programacion
import com.example.ui.theme.*

@Composable
fun EditProgramacionDialog(
    programacion: Programacion,
    onDismiss: () -> Unit,
    onSave: (Programacion) -> Unit
) {
    var hora by remember { mutableStateOf(programacion.hora) }
    var duracion by remember { mutableStateOf(programacion.duracionSegundos.toFloat()) }
    var etiqueta by remember { mutableStateOf(programacion.etiqueta) }
    var diaSemana by remember { mutableStateOf(programacion.diaSemana) }

    val dias = listOf(
        Pair(1, "L"),
        Pair(2, "M"),
        Pair(3, "X"),
        Pair(4, "J"),
        Pair(5, "V")
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = CardSurfaceDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Configurar Toque de Timbre",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )

                // Hora Input
                OutlinedTextField(
                    value = hora,
                    onValueChange = { hora = it },
                    label = { Text("Hora (formato HH:mm)") },
                    leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null, tint = NeonGreen) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonGreen,
                        unfocusedBorderColor = CardBorderDark,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("dialog_input_hora")
                )

                // Etiqueta Input
                OutlinedTextField(
                    value = etiqueta,
                    onValueChange = { etiqueta = it },
                    label = { Text("Descripción / Evento") },
                    leadingIcon = { Icon(Icons.Default.Label, contentDescription = null, tint = ElectricCyan) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = CardBorderDark,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("dialog_input_etiqueta")
                )

                // Selector de Día
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Día de la semana",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        dias.forEach { (num, letra) ->
                            val isSelected = diaSemana == num
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) NeonGreen else SurfaceDark)
                                    .border(1.dp, if (isSelected) NeonGreen else CardBorderDark, RoundedCornerShape(8.dp))
                                    .clickable { diaSemana = num },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = letra,
                                    color = if (isSelected) BackgroundDark else TextWhite,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Slider Duración del Timbre
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Duración de activación del relé:",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Text(
                            text = "${duracion.toInt()} segundos",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            color = NeonGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = duracion,
                        onValueChange = { duracion = it },
                        valueRange = 1f..15f,
                        steps = 13,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonGreen,
                            activeTrackColor = NeonGreen,
                            inactiveTrackColor = SurfaceDark
                        ),
                        modifier = Modifier.testTag("dialog_slider_duracion")
                    )
                }

                // Botones Cancelar y Guardar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val actualizado = programacion.copy(
                                hora = hora.trim(),
                                etiqueta = etiqueta.trim(),
                                duracionSegundos = duracion.toInt(),
                                diaSemana = diaSemana
                            )
                            onSave(actualizado)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonGreen,
                            contentColor = BackgroundDark
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("dialog_btn_guardar_programacion")
                    ) {
                        Text("Guardar Toque", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun EditDiaDialog(
    dia: CalendarioDia,
    onDismiss: () -> Unit,
    onSave: (fecha: String, tipoId: String, obs: String, jornada: String, hayClases: Boolean) -> Unit,
    onEliminar: ((fecha: String) -> Unit)? = null
) {
    var hayClases by remember { mutableStateOf(dia.hayClases && dia.idTipoHorario != "SIN_CLASES") }
    var selectedTipo by remember {
        mutableStateOf(if (dia.idTipoHorario == "SIN_CLASES") "TIPO_A" else dia.idTipoHorario)
    }
    var selectedJornada by remember { mutableStateOf(dia.jornada.ifBlank { "Completa" }) }
    var observacion by remember { mutableStateOf(dia.observacion) }

    val tipos = listOf(
        Pair("TIPO_A", "Horario Ordinario (Tipo A)"),
        Pair("TIPO_B", "Horario Especial / Actos (Tipo B)"),
        Pair("TIPO_C", "Jornada Reducida (Tipo C)")
    )

    val jornadas = listOf("Mañana", "Tarde", "Completa")

    val motivosSugeridos = listOf(
        "Acto Cívico",
        "Jornada Pedagógica",
        "Evaluaciones Trimestrales",
        "Feriado / Sin Clases",
        "Encuentro Deportivo"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = CardSurfaceDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Cabecera
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Configuración del Día",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "Asigna horario, jornada y evento escolar",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderDark)
                    ) {
                        Text(
                            text = dia.fecha,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
                            color = ElectricCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                HorizontalDivider(color = CardBorderDark)

                // 1. ¿Habrá clases o suspensión?
                Text(
                    text = "¿Habrá clases en esta fecha?",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Opción Sí Hay Clases
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                hayClases = true
                                if (selectedTipo == "SIN_CLASES") selectedTipo = "TIPO_A"
                            },
                        shape = RoundedCornerShape(8.dp),
                        color = if (hayClases) NeonGreen.copy(alpha = 0.15f) else SurfaceDark,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (hayClases) NeonGreen else CardBorderDark
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            RadioButton(
                                selected = hayClases,
                                onClick = {
                                    hayClases = true
                                    if (selectedTipo == "SIN_CLASES") selectedTipo = "TIPO_A"
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = NeonGreen, unselectedColor = TextSecondary),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Sí, Hay Clases",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (hayClases) FontWeight.Bold else FontWeight.Normal,
                                color = if (hayClases) NeonGreen else TextSecondary
                            )
                        }
                    }

                    // Opción No Hay Clases
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                hayClases = false
                                if (observacion.isBlank() || observacion == "Jornada escolar ordinaria") {
                                    observacion = "Suspensión de clases / Feriado"
                                }
                            },
                        shape = RoundedCornerShape(8.dp),
                        color = if (!hayClases) CrimsonRed.copy(alpha = 0.15f) else SurfaceDark,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (!hayClases) CrimsonRed else CardBorderDark
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            RadioButton(
                                selected = !hayClases,
                                onClick = {
                                    hayClases = false
                                    if (observacion.isBlank() || observacion == "Jornada escolar ordinaria") {
                                        observacion = "Suspensión de clases / Feriado"
                                    }
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = CrimsonRed, unselectedColor = TextSecondary),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Sin Clases / Receso",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (!hayClases) FontWeight.Bold else FontWeight.Normal,
                                color = if (!hayClases) CrimsonRed else TextSecondary
                            )
                        }
                    }
                }

                if (hayClases) {
                    // 2. Tipo de Horario
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Tipo de Horario Escolar:",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        tipos.forEach { (tipoId, nombre) ->
                            val isSelected = selectedTipo == tipoId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) ElectricCyan.copy(alpha = 0.15f) else SurfaceDark)
                                    .border(1.dp, if (isSelected) ElectricCyan else CardBorderDark, RoundedCornerShape(8.dp))
                                    .clickable { selectedTipo = tipoId }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedTipo = tipoId },
                                    colors = RadioButtonDefaults.colors(selectedColor = ElectricCyan, unselectedColor = TextSecondary),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = nombre,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isSelected) TextWhite else TextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    // 3. Selector de Jornada
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Jornada Escolar:",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            jornadas.forEach { jor ->
                                val isJorSelected = selectedJornada == jor
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedJornada = jor },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isJorSelected) AmberWarning.copy(alpha = 0.18f) else SurfaceDark,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isJorSelected) AmberWarning else CardBorderDark
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = jor,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = if (isJorSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isJorSelected) AmberWarning else TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CrimsonRed.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonRed.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "ℹ️ En los días sin clases los timbres automáticos quedan deshabilitados en el calendario escolar.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextWhite,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                // 4. Motivo / Nombre del Evento
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = observacion,
                        onValueChange = { observacion = it },
                        label = { Text("Motivo / Evento de este día") },
                        placeholder = { Text("ej. Acto cívico de la Independencia") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = CardBorderDark,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedLabelColor = ElectricCyan,
                            unfocusedLabelColor = TextSecondary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("dialog_input_obs_dia")
                    )

                    // Sugerencias rápidas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        motivosSugeridos.take(3).forEach { sugerencia ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SurfaceDark,
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, CardBorderDark),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { observacion = sugerencia }
                            ) {
                                Text(
                                    text = sugerencia,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                // Botones de Acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onEliminar != null) {
                        TextButton(
                            onClick = { onEliminar(dia.fecha) },
                            modifier = Modifier.testTag("dialog_btn_eliminar_evento_dia")
                        ) {
                            Text("Restablecer", color = AmberWarning, style = MaterialTheme.typography.bodySmall)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancelar", color = TextSecondary)
                        }
                        Button(
                            onClick = {
                                val tipoFinal = if (!hayClases) "SIN_CLASES" else selectedTipo
                                val motivoFinal = if (observacion.isBlank()) {
                                    if (!hayClases) "Sin Clases / Receso" else "Jornada habitual"
                                } else observacion.trim()
                                onSave(dia.fecha, tipoFinal, motivoFinal, selectedJornada, hayClases)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (hayClases) ElectricCyan else CrimsonRed,
                                contentColor = BackgroundDark
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("dialog_btn_guardar_dia")
                        ) {
                            Text("Guardar Asignación", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
