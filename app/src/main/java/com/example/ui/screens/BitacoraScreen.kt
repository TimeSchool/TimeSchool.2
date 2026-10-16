package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.model.RegistroLog
import com.example.model.TipoEvento
import com.example.ui.components.EventoBadge
import com.example.ui.theme.*

@Composable
fun BitacoraScreen(
    registros: List<RegistroLog>,
    filtroTexto: String,
    filtroTipo: TipoEvento?,
    onSetFiltro: (String, TipoEvento?) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchInput by remember { mutableStateOf(filtroTexto) }

    val registrosFiltrados = registros.filter { reg ->
        val coincideTipo = filtroTipo == null || reg.tipoEvento == filtroTipo
        val coincideTexto = searchInput.isBlank() ||
                reg.observacion.contains(searchInput, ignoreCase = true) ||
                reg.nombreUsuario.contains(searchInput, ignoreCase = true) ||
                reg.hora.contains(searchInput, ignoreCase = true)
        coincideTipo && coincideTexto
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Banner de Seguridad Firestore
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardSurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderDark)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = NeonGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Bitácora Inmutable (Cloud Firestore)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "Reglas de seguridad activas: Inserción y lectura habilitadas; edición y borrado estrictamente prohibidos.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Buscador
        item {
            OutlinedTextField(
                value = searchInput,
                onValueChange = {
                    searchInput = it
                    onSetFiltro(it, filtroTipo)
                },
                placeholder = { Text("Buscar en la bitácora (ej. Carlos, Toque, 07:00)...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                trailingIcon = {
                    if (searchInput.isNotEmpty()) {
                        IconButton(onClick = {
                            searchInput = ""
                            onSetFiltro("", filtroTipo)
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = TextSecondary)
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonGreen,
                    unfocusedBorderColor = CardBorderDark,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedContainerColor = CardSurfaceDark,
                    unfocusedContainerColor = CardSurfaceDark
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("input_search_logs")
            )
        }

        // Chips de Filtro por Tipo de Evento
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = filtroTipo == null,
                        onClick = { onSetFiltro(searchInput, null) },
                        label = { Text("Todos (${registros.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonGreen,
                            selectedLabelColor = BackgroundDark,
                            containerColor = CardSurfaceDark,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = filtroTipo == null,
                            borderColor = if (filtroTipo == null) NeonGreen else CardBorderDark
                        ),
                        modifier = Modifier.testTag("filter_chip_all")
                    )
                }

                items(TipoEvento.entries.toTypedArray()) { tipo ->
                    val isSelected = filtroTipo == tipo
                    val count = registros.count { it.tipoEvento == tipo }
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSetFiltro(searchInput, if (isSelected) null else tipo) },
                        label = { Text("${tipo.etiqueta} ($count)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricCyan,
                            selectedLabelColor = BackgroundDark,
                            containerColor = CardSurfaceDark,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) ElectricCyan else CardBorderDark
                        ),
                        modifier = Modifier.testTag("filter_chip_${tipo.name}")
                    )
                }
            }
        }

        // Conteo de Registros
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Eventos (${registrosFiltrados.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Text(
                    text = "Orden cronológico inverso",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }

        // Items de la Bitácora
        if (registrosFiltrados.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurfaceDark)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterListOff,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "No se encontraron eventos con este criterio",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextWhite
                        )
                    }
                }
            }
        } else {
            items(registrosFiltrados, key = { it.id }) { reg ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, CardBorderDark, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = CardSurfaceDark)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            EventoBadge(tipo = reg.tipoEvento)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "${reg.fecha} ${reg.hora}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                    color = TextSecondary
                                )
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Registro inmutable",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }

                        Text(
                            text = reg.observacion,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextWhite,
                            fontWeight = FontWeight.Medium
                        )

                        Divider(color = CardBorderDark.copy(alpha = 0.5f), thickness = 0.5.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = reg.nombreUsuario,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ElectricCyan
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = NeonGreen.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = if (reg.ejecutado) "Ejecutado" else "Pendiente",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonGreen,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
