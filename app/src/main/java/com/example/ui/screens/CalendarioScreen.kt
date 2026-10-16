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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.CalendarioDia
import com.example.model.Programacion
import com.example.model.TipoHorario
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalendarioScreen(
    calendarioDias: List<CalendarioDia>,
    tiposHorario: List<TipoHorario>,
    rutinaSemanal: Map<Int, String>,
    programaciones: List<Programacion>,
    onEditarDia: (CalendarioDia) -> Unit,
    modifier: Modifier = Modifier
) {
    val hoy = remember { Calendar.getInstance() }
    var currentYear by remember { mutableIntStateOf(hoy.get(Calendar.YEAR)) }
    var currentMonth by remember { mutableIntStateOf(hoy.get(Calendar.MONTH)) } // 0..11
    var selectedDayNumber by remember { mutableIntStateOf(hoy.get(Calendar.DAY_OF_MONTH)) }
    var showMonthPicker by remember { mutableStateOf(false) }

    val monthNames = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )
    val dayHeaders = listOf("LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM")

    // Calcular días del mes actual
    val calendarCalc = remember(currentYear, currentMonth) {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, currentMonth)
            set(Calendar.DAY_OF_MONTH, 1)
        }
    }

    val maxDaysInMonth = calendarCalc.getActualMaximum(Calendar.DAY_OF_MONTH)
    // Calendar.DAY_OF_WEEK: 1=DOM, 2=LUN, 3=MAR, 4=MIÉ, 5=JUE, 6=VIE, 7=SÁB
    val firstDayOfWeek = calendarCalc.get(Calendar.DAY_OF_WEEK)
    // Queremos LUN = 0, MAR = 1, ..., DOM = 6
    val leadingEmptySlots = (firstDayOfWeek + 5) % 7

    // Asegurar que selectedDayNumber esté dentro del rango del mes seleccionado
    val safeSelectedDay = selectedDayNumber.coerceIn(1, maxDaysInMonth)
    val selectedFechaIso = String.format(Locale.US, "%04d-%02d-%02d", currentYear, currentMonth + 1, safeSelectedDay)

    // Resolver la configuración del día seleccionado
    val diaSeleccionadoResuelto = remember(selectedFechaIso, calendarioDias, rutinaSemanal) {
        resolverDiaCalendario(
            fechaIso = selectedFechaIso,
            year = currentYear,
            month = currentMonth,
            day = safeSelectedDay,
            calendarioDias = calendarioDias,
            rutinaSemanal = rutinaSemanal
        )
    }

    val diaSemanaSeleccionado = getDiaSemanaIndex(currentYear, currentMonth, safeSelectedDay)
    val esEventoPersonalizado = calendarioDias.any { it.fecha == selectedFechaIso }

    // Timbres asociados al día seleccionado
    val timbresDelDia = remember(diaSeleccionadoResuelto, programaciones, diaSemanaSeleccionado) {
        if (!diaSeleccionadoResuelto.hayClases || diaSeleccionadoResuelto.idTipoHorario == "SIN_CLASES") {
            emptyList()
        } else {
            programaciones
                .filter { it.idTipoHorario == diaSeleccionadoResuelto.idTipoHorario && it.diaSemana == diaSemanaSeleccionado }
                .sortedBy { it.hora }
        }
    }

    val fechaFormateadaHumana = remember(selectedFechaIso) {
        try {
            val dfInput = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val dfOutput = SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", Locale("es", "CO"))
            val parsed = dfInput.parse(selectedFechaIso)
            parsed?.let { dfOutput.format(it).replaceFirstChar { c -> c.uppercase() } } ?: selectedFechaIso
        } catch (e: Exception) {
            selectedFechaIso
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. BANNER INFORMATIVO
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardSurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderDark),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = ElectricCyan.copy(alpha = 0.15f),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Calendario Escolar Mensual",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "Consulta y planifica los eventos, jornadas y régimen de timbres de cada fecha.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // 2. CUADRÍCULA MENSUAL ESTILO SMARTPHONE
        item {
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
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Barra de navegación entre meses
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Botón Mes Anterior
                        IconButton(
                            onClick = {
                                if (currentMonth == 0) {
                                    currentMonth = 11
                                    currentYear -= 1
                                } else {
                                    currentMonth -= 1
                                }
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceDark)
                                .border(1.dp, CardBorderDark, RoundedCornerShape(8.dp))
                                .testTag("btn_prev_month")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Mes anterior",
                                tint = TextWhite
                            )
                        }

                        // Selector de Mes y Año
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SurfaceDark,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { showMonthPicker = true }
                                .testTag("btn_select_month_year")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "${monthNames[currentMonth]} $currentYear".uppercase(),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricCyan
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Cambiar mes o año",
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Botón "Hoy" y Mes Siguiente
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Botón para volver a hoy
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SurfaceDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderDark),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        currentYear = hoy.get(Calendar.YEAR)
                                        currentMonth = hoy.get(Calendar.MONTH)
                                        selectedDayNumber = hoy.get(Calendar.DAY_OF_MONTH)
                                    }
                                    .testTag("btn_calendar_today")
                            ) {
                                Text(
                                    text = "Hoy",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Botón Mes Siguiente
                            IconButton(
                                onClick = {
                                    if (currentMonth == 11) {
                                        currentMonth = 0
                                        currentYear += 1
                                    } else {
                                        currentMonth += 1
                                    }
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceDark)
                                    .border(1.dp, CardBorderDark, RoundedCornerShape(8.dp))
                                    .testTag("btn_next_month")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Mes siguiente",
                                    tint = TextWhite
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = CardBorderDark.copy(alpha = 0.6f))

                    // Encabezados de días de la semana (LUN a DOM)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        dayHeaders.forEachIndexed { idx, header ->
                            val isWeekend = idx >= 5
                            Text(
                                text = header,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isWeekend) SlateGray else TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Grilla de días (7 columnas)
                    val totalSlots = leadingEmptySlots + maxDaysInMonth
                    val totalRows = (totalSlots + 6) / 7

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (row in 0 until totalRows) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                for (col in 0 until 7) {
                                    val slotIndex = row * 7 + col
                                    val dayNum = slotIndex - leadingEmptySlots + 1

                                    if (dayNum in 1..maxDaysInMonth) {
                                        val fechaIso = String.format(Locale.US, "%04d-%02d-%02d", currentYear, currentMonth + 1, dayNum)
                                        val isSelected = (dayNum == safeSelectedDay)
                                        val isToday = (dayNum == hoy.get(Calendar.DAY_OF_MONTH) &&
                                                currentMonth == hoy.get(Calendar.MONTH) &&
                                                currentYear == hoy.get(Calendar.YEAR))

                                        val diaData = resolverDiaCalendario(
                                            fechaIso = fechaIso,
                                            year = currentYear,
                                            month = currentMonth,
                                            day = dayNum,
                                            calendarioDias = calendarioDias,
                                            rutinaSemanal = rutinaSemanal
                                        )

                                        val hasSpecialEvent = calendarioDias.any { it.fecha == fechaIso }
                                        val dayOfWeekCol = col + 1 // 1=LUN, ..., 7=DOM
                                        val isWeekendDay = dayOfWeekCol >= 6

                                        val dotColor = when {
                                            !diaData.hayClases || diaData.idTipoHorario == "SIN_CLASES" -> {
                                                if (isWeekendDay) SlateGray else CrimsonRed
                                            }
                                            diaData.idTipoHorario == "TIPO_A" -> NeonGreen
                                            diaData.idTipoHorario == "TIPO_B" -> ElectricCyan
                                            diaData.idTipoHorario == "TIPO_C" -> AmberWarning
                                            else -> ElectricCyan
                                        }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .padding(2.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    when {
                                                        isSelected -> ElectricCyan.copy(alpha = 0.25f)
                                                        isToday -> SurfaceDark
                                                        hasSpecialEvent -> dotColor.copy(alpha = 0.12f)
                                                        else -> Color.Transparent
                                                    }
                                                )
                                                .border(
                                                    width = if (isSelected) 1.5.dp else if (isToday) 1.dp else if (hasSpecialEvent) 1.dp else 0.5.dp,
                                                    color = when {
                                                        isSelected -> ElectricCyan
                                                        isToday -> NeonGreen
                                                        hasSpecialEvent -> dotColor.copy(alpha = 0.7f)
                                                        else -> CardBorderDark.copy(alpha = 0.4f)
                                                    },
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .clickable {
                                                    selectedDayNumber = dayNum
                                                }
                                                .testTag("calendar_day_$dayNum"),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = dayNum.toString(),
                                                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                                                    fontWeight = if (isSelected || isToday || hasSpecialEvent) FontWeight.Bold else FontWeight.Medium,
                                                    color = when {
                                                        isSelected -> ElectricCyan
                                                        isToday -> NeonGreen
                                                        isWeekendDay -> SlateGray
                                                        else -> TextWhite
                                                    },
                                                    fontSize = 13.sp
                                                )

                                                Spacer(modifier = Modifier.height(2.dp))

                                                // Punto de color debajo del número según tipo de evento
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(if (hasSpecialEvent) 6.dp else 5.dp)
                                                            .clip(CircleShape)
                                                            .background(dotColor)
                                                    )
                                                    if (hasSpecialEvent) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(4.dp)
                                                                .clip(CircleShape)
                                                                .background(ElectricCyan)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        // Espacio vacío para rellenar la grilla
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .padding(2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Leyenda de colores
                    HorizontalDivider(color = CardBorderDark.copy(alpha = 0.5f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LegendItem(color = NeonGreen, label = "Tipo A")
                        LegendItem(color = ElectricCyan, label = "Tipo B")
                        LegendItem(color = AmberWarning, label = "Tipo C")
                        LegendItem(color = CrimsonRed, label = "Sin Clases")
                    }
                }
            }
        }

        // 3. TARJETA DETALLE DEL DÍA SELECCIONADO Y ACCIÓN DE EVENTO
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, CardBorderDark, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = CardSurfaceDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Encabezado del día seleccionado
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = fechaFormateadaHumana,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "Configuración activa para esta fecha",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }

                        // Distintivo de evento personalizado
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (esEventoPersonalizado) AmberWarning.copy(alpha = 0.15f) else SurfaceDark,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (esEventoPersonalizado) AmberWarning else CardBorderDark
                            )
                        ) {
                            Text(
                                text = if (esEventoPersonalizado) "★ Evento Registrado" else "Régimen Habitual",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (esEventoPersonalizado) AmberWarning else TextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    HorizontalDivider(color = CardBorderDark.copy(alpha = 0.5f))

                    // Etiquetas de Estado: Clases, Tipo de Horario y Jornada
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Badge Clases
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (diaSeleccionadoResuelto.hayClases) NeonGreen.copy(alpha = 0.15f) else CrimsonRed.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (diaSeleccionadoResuelto.hayClases) NeonGreen.copy(alpha = 0.5f) else CrimsonRed.copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (diaSeleccionadoResuelto.hayClases) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                    contentDescription = null,
                                    tint = if (diaSeleccionadoResuelto.hayClases) NeonGreen else CrimsonRed,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (diaSeleccionadoResuelto.hayClases) "Con Clases" else "Sin Clases",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (diaSeleccionadoResuelto.hayClases) NeonGreen else CrimsonRed
                                )
                            }
                        }

                        // Badge Horario
                        val tipoNombreCorto = when (diaSeleccionadoResuelto.idTipoHorario) {
                            "TIPO_A" -> "Horario A (Ordinario)"
                            "TIPO_B" -> "Horario B (Especial)"
                            "TIPO_C" -> "Horario C (Reducido)"
                            else -> "Sin Clases / Receso"
                        }
                        val tipoColor = when (diaSeleccionadoResuelto.idTipoHorario) {
                            "TIPO_A" -> NeonGreen
                            "TIPO_B" -> ElectricCyan
                            "TIPO_C" -> AmberWarning
                            else -> SlateGray
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = tipoColor.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, tipoColor.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = tipoNombreCorto,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = tipoColor
                            )
                        }

                        // Badge Jornada
                        if (diaSeleccionadoResuelto.hayClases) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SurfaceDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderDark)
                            ) {
                                Text(
                                    text = "Jornada ${diaSeleccionadoResuelto.jornada}",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Motivo / Observación
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderDark),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Motivo u Observación:",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                            Text(
                                text = diaSeleccionadoResuelto.observacion.ifBlank { "Sin observación registrada." },
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextWhite
                            )
                        }
                    }

                    // Coherencia con Horarios: Resumen de toques de campana del día
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Resumen de Timbres Programados:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "${timbresDelDia.count { it.activo }} toques activos",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricCyan
                            )
                        }

                        if (timbresDelDia.isEmpty()) {
                            Text(
                                text = if (!diaSeleccionadoResuelto.hayClases) {
                                    "🔔 No sonarán timbres automáticos durante esta jornada."
                                } else {
                                    "No hay toques configurados para este tipo de horario en este día de la semana."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        } else {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                items(timbresDelDia, key = { it.id }) { prog ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = SurfaceDark,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderDark)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = prog.hora,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                                                fontWeight = FontWeight.Bold,
                                                color = ElectricCyan
                                            )
                                            Text(
                                                text = prog.etiqueta,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextSecondary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Botón de acción para abrir la ventana modal de evento
                    Button(
                        onClick = { onEditarDia(diaSeleccionadoResuelto) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricCyan,
                            contentColor = BackgroundDark
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_configurar_dia_seleccionado")
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditCalendar,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (esEventoPersonalizado) "Modificar Evento de este Día" else "Registrar Evento / Horario Especial",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 4. COHERENCIA CON HORARIOS: RESUMEN DE LA SEMANA DE LOS TIMBRES
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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = NeonGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Régimen Semanal Vigente (Horarios)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        }
                        Text(
                            text = "Lunes a Viernes",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }

                    Text(
                        text = "El calendario aplica automáticamente esta rutina semanal a cada fecha, excepto cuando configuras un evento especial en un día determinado.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    // Tabla semanal resumen
                    val diasSemanaNombres = listOf(
                        Pair(1, "Lunes"),
                        Pair(2, "Martes"),
                        Pair(3, "Miércoles"),
                        Pair(4, "Jueves"),
                        Pair(5, "Viernes")
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        diasSemanaNombres.forEach { (diaNum, nombre) ->
                            val tipoId = rutinaSemanal[diaNum] ?: "TIPO_A"
                            val tipoObj = tiposHorario.find { it.id == tipoId }
                            val (badgeColor, badgeNombre) = when (tipoId) {
                                "TIPO_A" -> Pair(NeonGreen, "Horario A (Ordinario)")
                                "TIPO_B" -> Pair(ElectricCyan, "Horario B (Especial)")
                                "TIPO_C" -> Pair(AmberWarning, "Horario C (Reducido)")
                                else -> Pair(SlateGray, "Sin Clases")
                            }
                            val cantTimbres = programaciones.count { it.idTipoHorario == tipoId && it.diaSemana == diaNum && it.activo }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceDark)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = nombre,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextWhite
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = badgeColor.copy(alpha = 0.15f),
                                        border = androidx.compose.foundation.BorderStroke(0.5.dp, badgeColor.copy(alpha = 0.5f))
                                    ) {
                                        Text(
                                            text = tipoObj?.etiquetaCorta ?: badgeNombre,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = badgeColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }

                                    Text(
                                        text = "$cantTimbres toques",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal para saltar directamente a cualquier mes y año
    if (showMonthPicker) {
        Dialog(onDismissRequest = { showMonthPicker = false }) {
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
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Seleccionar Mes y Año",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )

                    // Selector de Año
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { currentYear -= 1 },
                            modifier = Modifier.testTag("btn_picker_prev_year")
                        ) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Año anterior", tint = TextWhite)
                        }
                        Text(
                            text = currentYear.toString(),
                            style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Monospace),
                            fontWeight = FontWeight.Bold,
                            color = ElectricCyan
                        )
                        IconButton(
                            onClick = { currentYear += 1 },
                            modifier = Modifier.testTag("btn_picker_next_year")
                        ) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Año siguiente", tint = TextWhite)
                        }
                    }

                    // Grilla de los 12 meses
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (r in 0 until 4) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                for (c in 0 until 3) {
                                    val mIdx = r * 3 + c
                                    val isSelectedM = (mIdx == currentMonth)
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                currentMonth = mIdx
                                                showMonthPicker = false
                                            }
                                            .testTag("btn_picker_month_$mIdx"),
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelectedM) ElectricCyan.copy(alpha = 0.2f) else SurfaceDark,
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isSelectedM) ElectricCyan else CardBorderDark
                                        )
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(vertical = 10.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = monthNames[mIdx],
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = if (isSelectedM) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelectedM) ElectricCyan else TextWhite,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showMonthPicker = false }) {
                            Text("Cerrar", color = ElectricCyan)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontSize = 11.sp
        )
    }
}

/**
 * Devuelve el índice del día de la semana (1=Lunes, 2=Martes, ..., 7=Domingo)
 */
private fun getDiaSemanaIndex(year: Int, month: Int, day: Int): Int {
    val cal = Calendar.getInstance().apply {
        set(year, month, day)
    }
    return when (cal.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> 1
        Calendar.TUESDAY -> 2
        Calendar.WEDNESDAY -> 3
        Calendar.THURSDAY -> 4
        Calendar.FRIDAY -> 5
        Calendar.SATURDAY -> 6
        Calendar.SUNDAY -> 7
        else -> 1
    }
}

/**
 * Resuelve la configuración de un día del calendario:
 * Si el usuario asignó un evento o cambio específico a esa fecha (CalendarioDia), lo retorna.
 * Si no, usa la rutina semanal configurada en Horarios (Lunes -> Tipo A, etc.) o Sin Clases para fines de semana.
 */
private fun resolverDiaCalendario(
    fechaIso: String,
    year: Int,
    month: Int,
    day: Int,
    calendarioDias: List<CalendarioDia>,
    rutinaSemanal: Map<Int, String>
): CalendarioDia {
    val explicit = calendarioDias.find { it.fecha == fechaIso }
    if (explicit != null) {
        return explicit
    }

    val diaSemana = getDiaSemanaIndex(year, month, day)
    val esFinDeSemana = (diaSemana == 6 || diaSemana == 7)

    return if (esFinDeSemana) {
        CalendarioDia(
            fecha = fechaIso,
            idTipoHorario = "SIN_CLASES",
            observacion = "Fin de semana institucional",
            jornada = "Completa",
            hayClases = false
        )
    } else {
        val tipoDefault = rutinaSemanal[diaSemana] ?: "TIPO_A"
        CalendarioDia(
            fecha = fechaIso,
            idTipoHorario = tipoDefault,
            observacion = "Régimen escolar habitual",
            jornada = "Completa",
            hayClases = true
        )
    }
}
