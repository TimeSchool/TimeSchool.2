package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BluetoothEstado
import com.example.model.RolUsuario
import com.example.model.TipoEvento
import com.example.ui.theme.*

@Composable
fun StatusBadge(
    texto: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(6.dp)),
        color = color.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = texto,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RolBadge(
    rol: RolUsuario,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val color = when (rol) {
        RolUsuario.ADMINISTRADOR -> NeonGreen
        RolUsuario.DIRECTIVO -> ElectricCyan
    }
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
        color = color.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = if (compact) 6.dp else 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (compact) 4.dp else 6.dp)
        ) {
            Icon(
                imageVector = if (rol == RolUsuario.ADMINISTRADOR) Icons.Default.AdminPanelSettings else Icons.Default.School,
                contentDescription = rol.etiqueta,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = if (compact) {
                    if (rol == RolUsuario.ADMINISTRADOR) "ADMIN" else "DIR"
                } else {
                    rol.etiqueta.uppercase()
                },
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold,
                letterSpacing = if (compact) 0.5.sp else 0.8.sp,
                fontSize = if (compact) 10.sp else 11.sp
            )
        }
    }
}

@Composable
fun BluetoothPill(
    estado: BluetoothEstado,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val color = when (estado) {
        BluetoothEstado.CONECTADO -> NeonGreen
        BluetoothEstado.CONECTANDO, BluetoothEstado.SINCRONIZANDO -> ElectricCyan
        BluetoothEstado.DESCONECTADO -> SlateGray
        BluetoothEstado.ERROR -> CrimsonNeon
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag("bluetooth_pill_btn"),
        color = CardSurfaceDark
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (compact) 7.dp else 10.dp,
                vertical = if (compact) 4.dp else 6.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (compact) 4.dp else 6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Bluetooth,
                contentDescription = "Estado Bluetooth: ${estado.etiqueta}",
                tint = color,
                modifier = Modifier.size(15.dp)
            )
            if (!compact) {
                Text(
                    text = estado.etiqueta,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextWhite,
                    fontSize = 11.sp
                )
            } else {
                Text(
                    text = when (estado) {
                        BluetoothEstado.CONECTADO -> "CON"
                        BluetoothEstado.CONECTANDO -> "SYNC"
                        BluetoothEstado.SINCRONIZANDO -> "SYNC"
                        BluetoothEstado.DESCONECTADO -> "OFF"
                        BluetoothEstado.ERROR -> "ERR"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
            Box(
                modifier = Modifier
                    .size(if (compact) 6.dp else 8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
fun EventoBadge(
    tipo: TipoEvento,
    modifier: Modifier = Modifier
) {
    val (color, icon) = when (tipo) {
        TipoEvento.PROGRAMADO -> Pair(ElectricCyan, Icons.Default.Schedule)
        TipoEvento.MANUAL -> Pair(NeonGreen, Icons.Default.NotificationsActive)
        TipoEvento.EVACUACION -> Pair(CrimsonNeon, Icons.Default.Warning)
        TipoEvento.SINCRONIZACION -> Pair(AmberWarning, Icons.Default.Sync)
        TipoEvento.SESION -> Pair(Color(0xFFB388FF), Icons.Default.AccountCircle)
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(6.dp)),
        color = color.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = tipo.etiqueta,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun InfoMetricCard(
    titulo: String,
    valor: String,
    subtitulo: String,
    icon: ImageVector,
    colorAcento: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, CardBorderDark, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = CardSurfaceDark)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colorAcento,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = valor,
                style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Monospace),
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitulo,
                style = MaterialTheme.typography.labelSmall,
                color = colorAcento
            )
        }
    }
}
