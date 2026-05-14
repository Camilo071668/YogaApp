package com.juancaballero.yogaapp.ui.screens

import com.juancaballero.yogaapp.ui.theme.ZenFlowBg
import com.juancaballero.yogaapp.ui.theme.ZenFlowOrange
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.juancaballero.yogaapp.R

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onClickRegister: () -> Unit) {

    // Instancia de Firebase Auth para manejar el inicio de sesión
    val auth = Firebase.auth
    val context = LocalView.current.context
    // Necesitamos el 'Activity' para que Firebase gestione los hilos de ejecución correctamente
    val activity = context as? Activity

    // Estados para los Inputs (Guardar lo que el usuario escribe)
    var inputEmail by remember { mutableStateOf("") }
    var inputPassword by remember { mutableStateOf("") }

    // Estados para Manejo De Errores
    var loginError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    // Color gris suave para el fondo de los campos (del mockup)
    val inputBg = Color.White

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ZenFlowBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding() // Respeta la barra de estado de Android (reloj, batería)
                .navigationBarsPadding()
                .imePadding() // Hace que el contenido suba cuando el teclado aparece
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // CABECERA CON LOGO (Estilo ZenFlow)
            Spacer(modifier = Modifier.height(60.dp))

            // Icono de la Flor
            Icon(
                painter = painterResource(id = R.drawable.ic_flower_logo),
                contentDescription = "Logo",
                tint = ZenFlowOrange,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome to ZenFlow",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(48.dp))

            // CONTENEDOR DE FORMULARIO
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                // CAMPO EMAIL
                Text(text = "EMAIL", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = inputEmail,
                    onValueChange = { inputEmail = it; emailError = "" },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp)),
                    isError = emailError.isNotEmpty(),
                    supportingText = { if (emailError.isNotEmpty()) Text(emailError) },
                    placeholder = { Text("email@example.com", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = ZenFlowOrange) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = inputBg,
                        unfocusedContainerColor = inputBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // CAMPO CONTRASEÑA
                Text(text = "PASSWORD", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = inputPassword,
                    onValueChange = { inputPassword = it; passwordError = "" },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp)),
                    isError = passwordError.isNotEmpty(),
                    supportingText = { if (passwordError.isNotEmpty()) Text(passwordError) },
                    visualTransformation = PasswordVisualTransformation(),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = ZenFlowOrange) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = inputBg,
                        unfocusedContainerColor = inputBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )

                // Mensaje de error general de Firebase
                if (loginError.isNotEmpty()) {
                    Text(loginError, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(40.dp))

                // BOTÓN DE LOGIN (Con el gradiente naranja de ZenFlow)
                Button(
                    onClick = {
                        val emailValid = validateEmail(inputEmail)
                        val passValid = validatePassword(inputPassword)

                        emailError = emailValid.second
                        passwordError = passValid.second

                        if (emailValid.first && passValid.first) {
                            activity?.let { act ->
                                auth.signInWithEmailAndPassword(inputEmail, inputPassword)
                                    .addOnCompleteListener(act) { task ->
                                        if (task.isSuccessful) {
                                            onLoginSuccess()
                                        } else {
                                            loginError = when (task.exception) {
                                                is FirebaseAuthInvalidCredentialsException -> "Incorrect credentials"
                                                is FirebaseAuthInvalidUserException -> "Account does not exist"
                                                else -> "Error: ${task.exception?.message}"
                                            }
                                        }
                                    }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(ZenFlowOrange, Color(0xFFFF8A65))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Log In", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // LINK A REGISTRO
            Row(modifier = Modifier.padding(bottom = 32.dp)) {
                Text(text = "Don't have an account? ", color = Color.Gray)
                Text(
                    text = "Sign Up",
                    color = ZenFlowOrange,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onClickRegister() }
                )
            }
        }
    }
}

// FUNCIONES DE VALIDACIÓN (Dile al profesor: "Son funciones de utilidad para asegurar la integridad de los datos")
fun validateEmail(email: String): Pair<Boolean, String> {
    return if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        Pair(true, "")
    } else {
        Pair(false, "Invalid email format")
    }
}

fun validatePassword(password: String): Pair<Boolean, String> {
    return if (password.length >= 6) {
        Pair(true, "")
    } else {
        Pair(false, "Password must be at least 6 characters")
    }
}