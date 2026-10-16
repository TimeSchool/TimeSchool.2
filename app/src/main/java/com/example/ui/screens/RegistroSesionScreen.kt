package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.InstitucionConfig
import com.example.model.RolUsuario
import com.example.model.Usuario
import com.example.ui.theme.*

@Composable
fun RegistroSesionScreen(
    institucion: InstitucionConfig,
    usuariosDisponibles: List<Usuario>,
    horaActual: String,
    correoGuardado: String = "",
    onLimpiarCorreo: () -> Unit = {},
    onAutenticar: (username: String, password: String, recordar: Boolean, terminos: Boolean) -> Pair<Boolean, String?>,
    onRegistrarNuevoUsuario: (nombre: String, apellido: String, rol: RolUsuario, correo: String, password: String) -> Pair<Boolean, String?>,
    modifier: Modifier = Modifier
) {
    var usernameInput by remember(correoGuardado) {
        mutableStateOf(correoGuardado.ifBlank { "admin@timeschool.edu.co" })
    }
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var terminosAceptados by remember { mutableStateOf(true) }

    // Validación de errores
    var errorGeneral by remember { mutableStateOf<String?>(null) }
    var errorPassword by remember { mutableStateOf<String?>(null) }
    var errorUsername by remember { mutableStateOf<String?>(null) }
    var errorTerminos by remember { mutableStateOf<String?>(null) }

    // Diálogos modales
    var showTerminosDialog by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var showRegisterDialog by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    fun ejecutarLogin() {
        focusManager.clearFocus()
        errorGeneral = null
        errorPassword = null
        errorUsername = null
        errorTerminos = null

        var hasError = false
        if (usernameInput.isBlank()) {
            errorUsername = "El nombre de usuario o correo es obligatorio"
            hasError = true
        }
        if (passwordInput.isBlank()) {
            errorPassword = "La contraseña es obligatoria"
            hasError = true
        }
        if (!terminosAceptados) {
            errorTerminos = "Debes aceptar los Términos y Condiciones"
            hasError = true
        }

        if (hasError) return

        val resultado = onAutenticar(usernameInput, passwordInput, rememberMe, terminosAceptados)
        if (!resultado.first) {
            errorGeneral = resultado.second ?: "Error en la autenticación"
            if (errorGeneral?.contains("contraseña", ignoreCase = true) == true) {
                errorPassword = errorGeneral
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header institucional TimeSchool
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(CardSurfaceDark)
                        .border(1.5.dp, NeonGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.timeschool_logo),
                        contentDescription = "TimeSchool Logo",
                        modifier = Modifier.size(40.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "TimeSchool",
                        style = MaterialTheme.typography.titleLarge.copy(
                            letterSpacing = 0.5.sp
                        ),
                        fontWeight = FontWeight.Black,
                        color = NeonGreen
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
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = institucion.nombreColegio,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            // Título de la estructura (de la imagen de referencia) con tipografía neón TimeSchool
            Text(
                text = "Formulario de inicio de sesión",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 22.sp,
                    letterSpacing = 0.3.sp
                ),
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Tarjeta con la ESTRUCTURA de la imagen de referencia pero con el DISEÑO original TimeSchool
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_form_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurfaceDark),
                border = BorderStroke(1.dp, CardBorderDark)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Contenedor principal de campos
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. Campo Username *
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Username *",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )

                            OutlinedTextField(
                                value = usernameInput,
                                onValueChange = {
                                    usernameInput = it
                                    errorUsername = null
                                    errorGeneral = null
                                },
                                placeholder = {
                                    Text("Enter your Username", color = TextMuted)
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = if (errorUsername != null) CrimsonNeon else ElectricCyan
                                    )
                                },
                                trailingIcon = {
                                    if (usernameInput.isNotEmpty()) {
                                        IconButton(onClick = {
                                            usernameInput = ""
                                            onLimpiarCorreo()
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Limpiar usuario",
                                                tint = TextSecondary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                },
                                singleLine = true,
                                isError = errorUsername != null,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = SurfaceDark,
                                    unfocusedContainerColor = SurfaceDark,
                                    focusedBorderColor = NeonGreen,
                                    unfocusedBorderColor = CardBorderDark,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    cursorColor = NeonGreen
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_username")
                            )

                            if (correoGuardado.isNotBlank() && usernameInput.equals(correoGuardado, ignoreCase = true)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BookmarkAdded,
                                        contentDescription = null,
                                        tint = NeonGreen,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "Correo recordado • Solo ingresa tu contraseña",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NeonGreen,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            if (errorUsername != null) {
                                Text(
                                    text = errorUsername!!,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CrimsonNeon,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }

                        // 2. Campo Password * (Obligatoria)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Password *",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )

                            OutlinedTextField(
                                value = passwordInput,
                                onValueChange = {
                                    passwordInput = it
                                    errorPassword = null
                                    errorGeneral = null
                                },
                                placeholder = {
                                    Text("Enter your Password (solo tu contraseña)", color = TextMuted)
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = if (errorPassword != null) CrimsonNeon else ElectricCyan
                                    )
                                },
                                singleLine = true,
                                isError = errorPassword != null,
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { ejecutarLogin() }
                                ),
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = if (passwordVisible) "Ocultar" else "Mostrar",
                                            tint = TextSecondary
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = SurfaceDark,
                                    unfocusedContainerColor = SurfaceDark,
                                    focusedBorderColor = NeonGreen,
                                    unfocusedBorderColor = CardBorderDark,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    cursorColor = NeonGreen
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_password")
                            )

                            if (errorPassword != null) {
                                Text(
                                    text = errorPassword!!,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CrimsonNeon,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }

                        // 3. Casilla Remember me
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { rememberMe = !rememberMe }
                                .padding(vertical = 2.dp)
                        ) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = NeonGreen,
                                    uncheckedColor = TextSecondary,
                                    checkmarkColor = BackgroundDark
                                ),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Remember me",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextWhite,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // 4. Casilla Obligatoria: Términos y Condiciones
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable {
                                        terminosAceptados = !terminosAceptados
                                        if (terminosAceptados) errorTerminos = null
                                    }
                                    .padding(vertical = 2.dp)
                            ) {
                                Checkbox(
                                    checked = terminosAceptados,
                                    onCheckedChange = {
                                        terminosAceptados = it
                                        if (it) errorTerminos = null
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = NeonGreen,
                                        uncheckedColor = if (errorTerminos != null) CrimsonNeon else TextSecondary,
                                        checkmarkColor = BackgroundDark
                                    ),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Acepto los ",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = "Términos y Condiciones *",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            textDecoration = TextDecoration.Underline,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = ElectricCyan,
                                        modifier = Modifier.clickable { showTerminosDialog = true }
                                    )
                                }
                            }

                            if (errorTerminos != null) {
                                Text(
                                    text = errorTerminos!!,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CrimsonNeon,
                                    modifier = Modifier.padding(start = 32.dp)
                                )
                            }
                        }

                        // Mensaje de error general si aplica
                        if (errorGeneral != null && errorPassword == null && errorUsername == null && errorTerminos == null) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CrimsonNeon.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, CrimsonNeon.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = errorGeneral!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CrimsonNeon,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        // 5. Botón ACCESO con el estilo y color principal TimeSchool (NeonGreen)
                        Button(
                            onClick = { ejecutarLogin() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("btn_acceso"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonGreen,
                                contentColor = BackgroundDark
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Login,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = BackgroundDark
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ACCESO",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Divider(color = CardBorderDark, thickness = 0.5.dp)

                    // Franja inferior con los dos enlaces estructurados tal como en la imagen
                    Surface(
                        color = SurfaceDark,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "¿No tienes una cuenta?",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = ElectricCyan,
                                modifier = Modifier
                                    .clickable { showRegisterDialog = true }
                                    .testTag("link_register")
                            )

                            Text(
                                text = "¿Se te olvidó tu contraseña?",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = ElectricCyan,
                                modifier = Modifier
                                    .clickable { showForgotPasswordDialog = true }
                                    .testTag("link_forgot_password")
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chips de credenciales de prueba con el diseño original de la app
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark.copy(alpha = 0.6f)),
                border = BorderStroke(0.5.dp, CardBorderDark)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = NeonGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Credenciales de Acceso Rápido (Pruebas):",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = usernameInput == "admin",
                            onClick = {
                                usernameInput = "admin"
                                passwordInput = "admin123"
                                errorGeneral = null
                                errorPassword = null
                                errorUsername = null
                            },
                            label = { Text("Admin: admin / admin123", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonGreen.copy(alpha = 0.2f),
                                selectedLabelColor = NeonGreen
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = usernameInput == "admin",
                                borderColor = CardBorderDark,
                                selectedBorderColor = NeonGreen
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        FilterChip(
                            selected = usernameInput == "directivo",
                            onClick = {
                                usernameInput = "directivo"
                                passwordInput = "directivo123"
                                errorGeneral = null
                                errorPassword = null
                                errorUsername = null
                            },
                            label = { Text("Directivo: directivo / directivo123", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricCyan.copy(alpha = 0.2f),
                                selectedLabelColor = ElectricCyan
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = usernameInput == "directivo",
                                borderColor = CardBorderDark,
                                selectedBorderColor = ElectricCyan
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    // Modal 1: Términos y Condiciones
    if (showTerminosDialog) {
        AlertDialog(
            onDismissRequest = { showTerminosDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Gavel, contentDescription = null, tint = NeonGreen)
                    Text("Términos y Condiciones TimeSchool", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "1. Uso del Sistema de Timbre Escolar:",
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "TimeSchool es una plataforma de automatización de toques de campana escolar y sincronización IoT vía Bluetooth con el dispositivo ESP32. Su uso está reservado exclusivamente al personal docente y directivo de ${institucion.nombreColegio}.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Text(
                        text = "2. Auditoría Inmutable de Sesiones y Acciones:",
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Todo inicio de sesión, cambio de horario (Tipos A, B, C) y activación manual de campana se registra de manera permanente en la bitácora escolar para control de trazabilidad institucional.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Text(
                        text = "3. Protocolo de Seguridad y Alarma:",
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "El sistema de alarma de evacuación solo debe accionarse en situaciones de emergencia verificada o simulacros autorizados.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        terminosAceptados = true
                        errorTerminos = null
                        showTerminosDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = BackgroundDark)
                ) {
                    Text("Aceptar Términos", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTerminosDialog = false }) {
                    Text("Cerrar", color = TextSecondary)
                }
            },
            containerColor = CardSurfaceDark
        )
    }

    // Modal 2: ¿Se te olvidó tu contraseña?
    if (showForgotPasswordDialog) {
        var emailRecuperacion by remember { mutableStateOf(usernameInput) }
        var mensajeEnviado by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.LockReset, contentDescription = null, tint = ElectricCyan)
                    Text("Recuperar Contraseña", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (!mensajeEnviado) {
                        Text(
                            text = "Ingresa tu correo institucional o nombre de usuario para coordinar el restablecimiento:",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        OutlinedTextField(
                            value = emailRecuperacion,
                            onValueChange = { emailRecuperacion = it },
                            label = { Text("Correo o Usuario") },
                            placeholder = { Text("ejemplo@timeschool.edu.co") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SurfaceDark,
                                unfocusedContainerColor = SurfaceDark,
                                focusedBorderColor = ElectricCyan,
                                unfocusedBorderColor = CardBorderDark
                            )
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SurfaceDark,
                            border = BorderStroke(0.5.dp, CardBorderDark)
                        ) {
                            Text(
                                text = "Credenciales predeterminadas para pruebas:\n• Administrador: admin / admin123\n• Directivo: directivo / directivo123",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricCyan,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NeonGreen.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, NeonGreen)
                        ) {
                            Text(
                                text = "Solicitud enviada al Administrador. Puedes utilizar temporalmente las credenciales de prueba predeterminadas.",
                                style = MaterialTheme.typography.bodySmall,
                                color = NeonGreen,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                if (!mensajeEnviado) {
                    Button(
                        onClick = { mensajeEnviado = true },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = BackgroundDark)
                    ) {
                        Text("Enviar Solicitud", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { showForgotPasswordDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = BackgroundDark)
                    ) {
                        Text("Entendido", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("Volver", color = TextSecondary)
                }
            },
            containerColor = CardSurfaceDark
        )
    }

    // Modal 3: ¿No tienes una cuenta? (Registro con contraseña obligatoria)
    if (showRegisterDialog) {
        var regNombre by remember { mutableStateOf("") }
        var regApellido by remember { mutableStateOf("") }
        var regCorreo by remember { mutableStateOf("") }
        var regPassword by remember { mutableStateOf("") }
        var regRol by remember { mutableStateOf(RolUsuario.ADMINISTRADOR) }
        var regError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showRegisterDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = NeonGreen)
                    Text("Crear Cuenta de Funcionario", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = regNombre,
                        onValueChange = { regNombre = it },
                        label = { Text("Nombre *") },
                        placeholder = { Text("Ej. Claudia") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = regApellido,
                        onValueChange = { regApellido = it },
                        label = { Text("Apellido") },
                        placeholder = { Text("Ej. Morales") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = regCorreo,
                        onValueChange = { regCorreo = it },
                        label = { Text("Usuario o Correo *") },
                        placeholder = { Text("claudia@timeschool.edu.co") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = regPassword,
                        onValueChange = {
                            regPassword = it
                            regError = null
                        },
                        label = { Text("Contraseña Obligatoria *") },
                        placeholder = { Text("Mínimo 4 caracteres") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Rol institucional asignado:",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextWhite
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = regRol == RolUsuario.ADMINISTRADOR,
                            onClick = { regRol = RolUsuario.ADMINISTRADOR },
                            label = { Text("Administrador") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonGreen.copy(alpha = 0.2f),
                                selectedLabelColor = NeonGreen
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = regRol == RolUsuario.ADMINISTRADOR,
                                borderColor = CardBorderDark,
                                selectedBorderColor = NeonGreen
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        FilterChip(
                            selected = regRol == RolUsuario.DIRECTIVO,
                            onClick = { regRol = RolUsuario.DIRECTIVO },
                            label = { Text("Directivo") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricCyan.copy(alpha = 0.2f),
                                selectedLabelColor = ElectricCyan
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = regRol == RolUsuario.DIRECTIVO,
                                borderColor = CardBorderDark,
                                selectedBorderColor = ElectricCyan
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (regError != null) {
                        Text(
                            text = regError!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = CrimsonNeon,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (regNombre.isBlank() || regCorreo.isBlank()) {
                            regError = "Nombre y usuario/correo son obligatorios"
                            return@Button
                        }
                        if (regPassword.isBlank() || regPassword.length < 4) {
                            regError = "La contraseña es obligatoria (mínimo 4 caracteres)"
                            return@Button
                        }
                        val res = onRegistrarNuevoUsuario(regNombre, regApellido, regRol, regCorreo, regPassword)
                        if (res.first) {
                            showRegisterDialog = false
                        } else {
                            regError = res.second
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = BackgroundDark)
                ) {
                    Text("Crear y Entrar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRegisterDialog = false }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            containerColor = CardSurfaceDark
        )
    }
}
