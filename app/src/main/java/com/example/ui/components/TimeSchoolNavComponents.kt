package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.BluetoothEstado
import com.example.model.InstitucionConfig
import com.example.model.Usuario
import com.example.ui.theme.*
import com.example.viewmodel.ScreenTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSchoolTopBar(
    institucion: InstitucionConfig,
    usuarioActual: Usuario,
    bluetoothEstado: BluetoothEstado,
    onAlternarRol: () -> Unit,
    onBluetoothClick: () -> Unit,
    onCerrarSesion: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 0.5.dp, color = CardBorderDark),
        color = SurfaceDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Fila 1: Logo, Nombre TimeSchool horizontal completo en una sola línea y Nombre de Institución
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Bell Icon / Logo TimeSchool
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CardSurfaceDark)
                        .border(1.5.dp, NeonGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.timeschool_logo),
                        contentDescription = "TimeSchool Logo",
                        modifier = Modifier.size(26.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "TimeSchool",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 19.sp,
                                letterSpacing = 0.5.sp
                            ),
                            fontWeight = FontWeight.Black,
                            color = NeonGreen,
                            maxLines = 1,
                            softWrap = false
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = ElectricCyan.copy(alpha = 0.15f),
                            border = BorderStroke(0.5.dp, ElectricCyan.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "IoT Timbre",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp,
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = institucion.nombreColegio,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = TextSecondary,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Fila 2: Los dos botones colocados abajo junto con el acceso para cerrar/cambiar sesión
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón 1: Estado y Conexión Bluetooth
                BluetoothPill(
                    estado = bluetoothEstado,
                    onClick = onBluetoothClick,
                    compact = false
                )

                // Botón 2: Selector interactivo para alternar rol
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onAlternarRol)
                        .testTag("switch_role_button"),
                    color = CardSurfaceDark
                ) {
                    RolBadge(
                        rol = usuarioActual.rol,
                        compact = false,
                        modifier = Modifier.padding(2.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Botón 3: Cerrar Sesión / Cambiar de Sesión
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onCerrarSesion)
                        .testTag("btn_cerrar_sesion"),
                    color = CardSurfaceDark,
                    border = BorderStroke(0.5.dp, CardBorderDark)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Cerrar Sesión",
                            tint = CrimsonNeon,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Salir",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimeSchoolBottomBar(
    currentTab: ScreenTab,
    onTabSelected: (ScreenTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .border(width = 0.5.dp, color = CardBorderDark),
        containerColor = SurfaceDark,
        tonalElevation = 6.dp
    ) {
        val items = listOf(
            Triple(ScreenTab.DASHBOARD, Icons.Default.Dashboard, "Panel"),
            Triple(ScreenTab.HORARIOS, Icons.Default.Schedule, "Horarios"),
            Triple(ScreenTab.CALENDARIO, Icons.Default.CalendarMonth, "Calendario"),
            Triple(ScreenTab.CONTROL_IOT, Icons.Default.NotificationsActive, "IoT Timbre"),
            Triple(ScreenTab.BITACORA, Icons.Default.History, "Bitácora"),
            Triple(ScreenTab.CONFIGURACION, Icons.Default.Tune, "Sede")
        )

        items.forEach { (tab, icon, label) ->
            val isSelected = currentTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BackgroundDark,
                    selectedTextColor = NeonGreen,
                    indicatorColor = NeonGreen,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                ),
                modifier = Modifier.testTag("nav_tab_${tab.ruta}")
            )
        }
    }
}
