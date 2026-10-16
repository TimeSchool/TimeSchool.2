package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BluetoothEstado
import com.example.model.Esp32HardwareStatus
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun ControlIotScreen(
    hardwareStatus: Esp32HardwareStatus,
    onToggleBluetooth: () -> Unit,
    onSyncEsp32: () -> Unit,
    onPresionarTimbreManual: (Int) -> Unit,
    onSoltarTimbreManual: () -> Unit,
    onActivarEvacuacion: () -> Unit,
    onDesactivarEvacuacion: () -> Unit,
    modifier: Modifier = Modifier
) {
    var duracionManualSeleccionada by remember { mutableStateOf(3) }
    var seguroEvacuacionDesbloqueado by remember { mutableStateOf(false) }

    // Animación de pulso para relé o alarma
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Tarjeta de Enlace Bluetooth Classic SPP con ESP32
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = CardSurfaceDark)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
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
                                imageVector = Icons.Default.Bluetooth,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "Enlace Hardware (ESP32)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        }

                        StatusBadge(
                            texto = hardwareStatus.estado.etiqueta,
                            color = when (hardwareStatus.estado) {
                                BluetoothEstado.CONECTADO -> NeonGreen
                                BluetoothEstado.CONECTANDO, BluetoothEstado.SINCRONIZANDO -> ElectricCyan
                                else -> SlateGray
                            }
                        )
                    }

                    // Datos Técnicos del Hardware
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceDark,
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, CardBorderDark)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Dispositivo:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text(hardwareStatus.nombre, style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), color = TextWhite)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Dirección MAC:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text(hardwareStatus.mac, style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), color = TextSecondary)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("UUID SPP:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text("00001101-0000-1000-8000-00805F9B34FB", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp), color = ElectricCyan)
                            }
                        }
                    }

                    // Botones de Conexión y Sincronización Flash
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onToggleBluetooth,
                            modifier = Modifier.weight(1f).testTag("btn_toggle_bt"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (hardwareStatus.estado == BluetoothEstado.CONECTADO) CrimsonNeon else ElectricCyan
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (hardwareStatus.estado == BluetoothEstado.CONECTADO) CrimsonNeon.copy(alpha = 0.5f) else ElectricCyan
                            )
                        ) {
                            Icon(
                                imageVector = if (hardwareStatus.estado == BluetoothEstado.CONECTADO) Icons.Default.BluetoothDisabled else Icons.Default.BluetoothSearching,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (hardwareStatus.estado == BluetoothEstado.CONECTADO) "Desconectar" else "Conectar")
                        }

                        Button(
                            onClick = onSyncEsp32,
                            modifier = Modifier.weight(1f).testTag("btn_sync_schedules"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricCyan,
                                contentColor = BackgroundDark
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sincronizar", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 2. Módulo de Timbre Manual Táctil
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CardBorderDark, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = CardSurfaceDark)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Toque Manual de Campana",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (hardwareStatus.releActivo && !hardwareStatus.modoEvacuacionActivo) NeonGreen.copy(alpha = 0.2f) else SlateGray.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (hardwareStatus.releActivo && !hardwareStatus.modoEvacuacionActivo) "RELÉ: ACTIVO" else "RELÉ: REPOSO",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (hardwareStatus.releActivo && !hardwareStatus.modoEvacuacionActivo) NeonGreen else TextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Selector de duración rápida
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Duración toque:",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        listOf(2, 3, 5, 8).forEach { seg ->
                            val isSelected = duracionManualSeleccionada == seg
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) NeonGreen else SurfaceDark)
                                    .border(1.dp, if (isSelected) NeonGreen else CardBorderDark, RoundedCornerShape(6.dp))
                                    .clickable { duracionManualSeleccionada = seg }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${seg}s",
                                    color = if (isSelected) BackgroundDark else TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // Botón Central Táctil con Onda de Glow
                    val isRinging = hardwareStatus.releActivo && !hardwareStatus.modoEvacuacionActivo
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(
                                if (isRinging) NeonGreen.copy(alpha = pulseAlpha) else NeonGreen.copy(alpha = 0.15f)
                            )
                            .border(
                                3.dp,
                                if (isRinging) NeonGreen else NeonGreen.copy(alpha = 0.6f),
                                CircleShape
                            )
                            .pointerInput(duracionManualSeleccionada) {
                                detectTapGestures(
                                    onPress = {
                                        onPresionarTimbreManual(duracionManualSeleccionada)
                                        tryAwaitRelease()
                                        onSoltarTimbreManual()
                                    }
                                )
                            }
                            .testTag("btn_manual_bell_circle"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Activar timbre",
                                tint = if (isRinging) BackgroundDark else NeonGreen,
                                modifier = Modifier.size(46.dp)
                            )
                            Text(
                                text = if (isRinging) "¡TIMBRANDO!" else "TOCAR",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isRinging) BackgroundDark else TextWhite
                            )
                            Text(
                                text = "Toca o mantén",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isRinging) BackgroundDark else TextSecondary
                            )
                        }
                    }

                    Text(
                        text = "Envía la trama CMD:RING_START y CMD:RING_STOP por Bluetooth al ESP32",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // 3. Módulo de Alarma de Evacuación (Seguridad Crítica)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        1.5.dp,
                        if (hardwareStatus.modoEvacuacionActivo) CrimsonNeon else CrimsonNeon.copy(alpha = 0.4f),
                        RoundedCornerShape(16.dp)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (hardwareStatus.modoEvacuacionActivo) CrimsonNeon.copy(alpha = 0.15f) else CardSurfaceDark
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
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
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = CrimsonNeon,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = "Alarma de Evacuación",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = CrimsonNeon
                                )
                                Text(
                                    text = "Ciclo intermitente: 2s activo / 1s silencio",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    // Interruptor de Seguridad Antidisparos
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderDark)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Seguro Antidisparo Accidental",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextWhite
                                )
                                Text(
                                    text = if (seguroEvacuacionDesbloqueado) "Desbloqueado: Botón de alarma habilitado" else "Bloqueado para prevenir toques involuntarios",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (seguroEvacuacionDesbloqueado) CrimsonNeon else TextSecondary
                                )
                            }
                            Switch(
                                checked = seguroEvacuacionDesbloqueado,
                                onCheckedChange = { seguroEvacuacionDesbloqueado = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = BackgroundDark,
                                    checkedTrackColor = CrimsonNeon,
                                    uncheckedThumbColor = TextSecondary,
                                    uncheckedTrackColor = SurfaceDark
                                ),
                                modifier = Modifier.testTag("switch_safety_evacuation")
                            )
                        }
                    }

                    // Botón de Disparo de Evacuación
                    if (hardwareStatus.modoEvacuacionActivo) {
                        Button(
                            onClick = onDesactivarEvacuacion,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("btn_stop_evacuation"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SurfaceDark,
                                contentColor = NeonGreen
                            ),
                            border = androidx.compose.foundation.BorderStroke(2.dp, NeonGreen)
                        ) {
                            Icon(Icons.Default.StopCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DETENER ALARMA DE EVACUACIÓN",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    } else {
                        Button(
                            onClick = onActivarEvacuacion,
                            enabled = seguroEvacuacionDesbloqueado,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("btn_trigger_evacuation"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CrimsonNeon,
                                contentColor = TextWhite,
                                disabledContainerColor = CardBorderDark,
                                disabledContentColor = TextSecondary
                            )
                        ) {
                            Icon(Icons.Default.Campaign, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ACTIVAR EVACUACIÓN GENERAL",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}
