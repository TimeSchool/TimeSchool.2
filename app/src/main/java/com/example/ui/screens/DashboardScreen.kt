package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.EventoBadge
import com.example.ui.components.InfoMetricCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.viewmodel.ScreenTab

@Composable
fun DashboardScreen(
    institucion: InstitucionConfig,
    usuarioActual: Usuario,
    hardwareStatus: Esp32HardwareStatus,
    programaciones: List<Programacion>,
    registros: List<RegistroLog>,
    horaActual: String,
    proximoTimbreTexto: String,
    tiempoParaProximo: String,
    onNavigateTab: (ScreenTab) -> Unit,
    onManualRing: () -> Unit,
    onSyncEsp32: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activasCount = programaciones.count { it.activo }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Card: Reloj en Tiempo Real y Próximo Toque
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        1.dp,
                        Brush.horizontalGradient(listOf(NeonGreen.copy(alpha = 0.6f), ElectricCyan.copy(alpha = 0.3f))),
                        RoundedCornerShape(16.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = CardSurfaceDark)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusBadge(
                            texto = "SISTEMA AUTÓNOMO ACTIVO",
                            color = NeonGreen
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (hardwareStatus.estado == BluetoothEstado.CONECTADO) NeonGreen else SlateGray)
                            )
                            Text(
                                text = "ESP32: ${hardwareStatus.estado.etiqueta}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }

                    // Reloj Digital Principal
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = horaActual,
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 42.sp,
                                letterSpacing = 2.sp
                            ),
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Hora sincronizada con RTC y Servidor",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Divider(color = CardBorderDark)

                    // Próximo Toque Programado
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(NeonGreen.copy(alpha = 0.15f))
                                    .border(1.dp, NeonGreen, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = NeonGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Próximo Timbre:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                                Text(
                                    text = proximoTimbreTexto,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextWhite,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ElectricCyan.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = tiempoParaProximo,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 2. Acciones Rápidas
        item {
            Text(
                text = "Acciones Inmediatas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Toque Manual Rápido (3s)
                Button(
                    onClick = onManualRing,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("quick_ring_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGreen,
                        contentColor = BackgroundDark
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Toque 3s",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Sincronizar Horarios
                Button(
                    onClick = onSyncEsp32,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("quick_sync_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CardSurfaceDark,
                        contentColor = ElectricCyan
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Sincronizar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Evacuación directa
                Button(
                    onClick = { onNavigateTab(ScreenTab.CONTROL_IOT) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("quick_evac_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CrimsonNeon.copy(alpha = 0.15f),
                        contentColor = CrimsonNeon
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonNeon)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Alarma",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // 3. Grid de Métricas y Estado
        item {
            Text(
                text = "Telemetría y Estado Operativo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoMetricCard(
                    titulo = "Toques Activos",
                    valor = "$activasCount",
                    subtitulo = "Programados hoy",
                    icon = Icons.Default.Alarm,
                    colorAcento = NeonGreen,
                    modifier = Modifier.weight(1f)
                )
                InfoMetricCard(
                    titulo = "Memoria Flash ESP32",
                    valor = "${hardwareStatus.horariosCargadosEnFlash}",
                    subtitulo = "Tablas grabadas",
                    icon = Icons.Default.Memory,
                    colorAcento = ElectricCyan,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoMetricCard(
                    titulo = "Último Enlace IoT",
                    valor = hardwareStatus.ultimoSync,
                    subtitulo = "Bluetooth SPP Classic",
                    icon = Icons.Default.BluetoothConnected,
                    colorAcento = AmberWarning,
                    modifier = Modifier.weight(1f)
                )
                InfoMetricCard(
                    titulo = "Bitácora de Eventos",
                    valor = "${registros.size}",
                    subtitulo = "Registros inmutables",
                    icon = Icons.Default.History,
                    colorAcento = PurpleAccent,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 4. Últimos Registros Inmutables de la Bitácora
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Últimos Eventos Auditados",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                TextButton(
                    onClick = { onNavigateTab(ScreenTab.BITACORA) },
                    modifier = Modifier.testTag("see_all_logs_button")
                ) {
                    Text("Ver todos", color = ElectricCyan, fontSize = 13.sp)
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                registros.take(3).forEach { reg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(0.5.dp, CardBorderDark, RoundedCornerShape(10.dp)),
                        colors = CardDefaults.cardColors(containerColor = CardSurfaceDark)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            EventoBadge(tipo = reg.tipoEvento)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = reg.observacion,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextWhite,
                                    maxLines = 1
                                )
                                Text(
                                    text = "${reg.hora} - ${reg.nombreUsuario}",
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
