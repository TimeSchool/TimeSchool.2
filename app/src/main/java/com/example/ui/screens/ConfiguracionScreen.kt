package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.model.InstitucionConfig
import com.example.model.RolUsuario
import com.example.model.Usuario
import com.example.ui.components.RolBadge
import com.example.ui.theme.*

@Composable
fun ConfiguracionScreen(
    institucion: InstitucionConfig,
    usuarioActual: Usuario,
    onGuardarConfig: (nombre: String, sede: String, ano: Int) -> Unit,
    onAlternarRol: () -> Unit,
    onCerrarSesion: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isAdmin = usuarioActual.rol == RolUsuario.ADMINISTRADOR

    var nombreColegio by remember(institucion.nombreColegio) { mutableStateOf(institucion.nombreColegio) }
    var sede by remember(institucion.sede) { mutableStateOf(institucion.sede) }
    var anoLectivoStr by remember(institucion.anoLectivo) { mutableStateOf(institucion.anoLectivo.toString()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner de Control de Roles
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(
                        1.dp,
                        if (isAdmin) NeonGreen.copy(alpha = 0.5f) else AmberWarning.copy(alpha = 0.5f),
                        RoundedCornerShape(14.dp)
                    ),
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
                        Text(
                            text = "Sesión Activa",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextSecondary
                        )
                        RolBadge(rol = usuarioActual.rol)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = if (isAdmin) NeonGreen else ElectricCyan,
                            modifier = Modifier.size(36.dp)
                        )
                        Column {
                            Text(
                                text = usuarioActual.nombreCompleto,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = usuarioActual.correo,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    if (!isAdmin) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AmberWarning.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AmberWarning.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = AmberWarning,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Modo Directivo: Acceso de solo lectura a la configuración institucional según las reglas de Firestore.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AmberWarning
                                )
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = onAlternarRol,
                        modifier = Modifier.fillMaxWidth().testTag("btn_switch_role_config"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isAdmin) ElectricCyan else NeonGreen
                        )
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAdmin) "Cambiar a Rol Directivo (Prueba de Permisos)" else "Cambiar a Rol Administrador"
                        )
                    }

                    Button(
                        onClick = onCerrarSesion,
                        modifier = Modifier.fillMaxWidth().testTag("btn_logout_config"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CrimsonNeon.copy(alpha = 0.15f),
                            contentColor = CrimsonNeon
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonNeon.copy(alpha = 0.4f))
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Cerrar Sesión Activa", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Formulario de Datos Institucionales
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, CardBorderDark, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = CardSurfaceDark)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Configuración Institucional",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )

                    OutlinedTextField(
                        value = nombreColegio,
                        onValueChange = { if (isAdmin) nombreColegio = it },
                        label = { Text("Nombre del Colegio / Institución") },
                        enabled = isAdmin,
                        leadingIcon = { Icon(Icons.Default.School, contentDescription = null, tint = NeonGreen) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGreen,
                            unfocusedBorderColor = CardBorderDark,
                            disabledBorderColor = CardBorderDark.copy(alpha = 0.5f),
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            disabledTextColor = TextSecondary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("input_school_name")
                    )

                    OutlinedTextField(
                        value = sede,
                        onValueChange = { if (isAdmin) sede = it },
                        label = { Text("Sede / Campus") },
                        enabled = isAdmin,
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = ElectricCyan) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = CardBorderDark,
                            disabledBorderColor = CardBorderDark.copy(alpha = 0.5f),
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            disabledTextColor = TextSecondary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("input_school_sede")
                    )

                    OutlinedTextField(
                        value = anoLectivoStr,
                        onValueChange = { if (isAdmin) anoLectivoStr = it.filter { c -> c.isDigit() } },
                        label = { Text("Año Lectivo") },
                        enabled = isAdmin,
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = AmberWarning) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberWarning,
                            unfocusedBorderColor = CardBorderDark,
                            disabledBorderColor = CardBorderDark.copy(alpha = 0.5f),
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            disabledTextColor = TextSecondary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("input_school_year")
                    )

                    // Metadatos de auditoría
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceDark,
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, CardBorderDark)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Última modificación: ${institucion.fechaActualizacion}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                            Text(
                                text = "Por: ${institucion.actualizadoPor}",
                                style = MaterialTheme.typography.bodySmall,
                                color = ElectricCyan
                            )
                        }
                    }

                    if (isAdmin) {
                        Button(
                            onClick = {
                                val anoInt = anoLectivoStr.toIntOrNull() ?: 2026
                                onGuardarConfig(nombreColegio, sede, anoInt)
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_save_school_config"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonGreen,
                                contentColor = BackgroundDark
                            )
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Guardar Cambios Institucionales", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Resumen de Arquitectura NoSQL Cloud Firestore
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, CardBorderDark, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = CardSurfaceDark)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Colecciones Cloud Firestore (Etapa 1)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    val colecciones = listOf(
                        Pair("configuracion/institucion", "Configuración central y año lectivo"),
                        Pair("usuarios/{uid}", "Perfiles de Administrador y Directivo"),
                        Pair("tipos_horario/{tipoId}", "Tipos A, B y C"),
                        Pair("programaciones/{progId}", "Matriz de toques por día y hora"),
                        Pair("calendario_dias/{YYYY-MM-DD}", "Asignación diaria de regímenes"),
                        Pair("registros/{registroId}", "Bitácora inmutable de auditoría")
                    )

                    colecciones.forEach { (path, desc) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = path,
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                color = NeonGreen,
                                fontSize = 11.sp
                            )
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
