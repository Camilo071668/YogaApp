package com.juancaballero.yogaapp.ui.screens

import com.juancaballero.yogaapp.ui.theme.ZenFlowBg
import com.juancaballero.yogaapp.ui.theme.ZenFlowOrange
import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.juancaballero.yogaapp.R
import com.juancaballero.yogaapp.ui.theme.ZenFlowOrange
import com.juancaballero.yogaapp.ui.theme.ZenFlowBg

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onBackClick: () -> Unit
) {
    // Estados para los inputs
    var inputName by remember { mutableStateOf("") }
    var inputEmail by remember { mutableStateOf("") }
    var inputPassword by remember { mutableStateOf("") }
    var inputConfirmPassword by remember { mutableStateOf("") }
    var acceptedTerms by remember { mutableStateOf(false) }

    val auth = Firebase.auth
    val activity = LocalView.current.context as Activity

    // Manejo de errores
    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }
    var registerError by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ZenFlowBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botón Atrás
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = ZenFlowOrange)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Logo y Título
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.align(Alignment.Start)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_flower_logo),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = ZenFlowOrange
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "ZenFlow", color = ZenFlowOrange, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Create Account",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Start)
            )
            Text(
                text = "Start your journey to inner peace today.",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp).align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campos del formulario (Usando tu lógica de RegisterField)
            Column(modifier = Modifier.fillMaxWidth()) {
                RegisterField(
                    label = "FULL NAME",
                    value = inputName,
                    onValueChange = { inputName = it; nameError = "" },
                    placeholder = "Your Name",
                    leadingIcon = Icons.Default.Person,
                    isError = nameError.isNotEmpty(),
                    errorMessage = nameError
                )

                Spacer(modifier = Modifier.height(20.dp))

                RegisterField(
                    label = "EMAIL ADDRESS",
                    value = inputEmail,
                    onValueChange = { inputEmail = it; emailError = "" },
                    placeholder = "hello@example.com",
                    leadingIcon = Icons.Default.Email,
                    isError = emailError.isNotEmpty(),
                    errorMessage = emailError
                )

                Spacer(modifier = Modifier.height(20.dp))

                RegisterField(
                    label = "PASSWORD",
                    value = inputPassword,
                    onValueChange = { inputPassword = it; passwordError = "" },
                    placeholder = "........",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    isError = passwordError.isNotEmpty(),
                    errorMessage = passwordError
                )

                Spacer(modifier = Modifier.height(20.dp))

                RegisterField(
                    label = "CONFIRM PASSWORD",
                    value = inputConfirmPassword,
                    onValueChange = { inputConfirmPassword = it; confirmPasswordError = "" },
                    placeholder = "........",
                    leadingIcon = Icons.Default.Refresh,
                    isPassword = true,
                    isError = confirmPasswordError.isNotEmpty(),
                    errorMessage = confirmPasswordError
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Checkbox de Términos
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Checkbox(
                    checked = acceptedTerms,
                    onCheckedChange = { acceptedTerms = it },
                    colors = CheckboxDefaults.colors(checkedColor = ZenFlowOrange)
                )
                Text(
                    text = buildAnnotatedString {
                        append("I agree to the ")
                        withStyle(style = SpanStyle(color = ZenFlowOrange, fontWeight = FontWeight.Bold)) {
                            append("Terms & Conditions")
                        }
                    },
                    fontSize = 12.sp, color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // BOTÓN REGISTRARSE
            Button(
                onClick = {
                    val vName = validateName(inputName)
                    val vEmail = validateEmail(inputEmail)
                    val vPass = validatePassword(inputPassword)
                    val vConf = validatePasswordConfirm(inputPassword, inputConfirmPassword)

                    nameError = vName.second
                    emailError = vEmail.second
                    passwordError = vPass.second
                    confirmPasswordError = vConf.second

                    if (vName.first && vEmail.first && vPass.first && vConf.first && acceptedTerms) {
                        auth.createUserWithEmailAndPassword(inputEmail, inputPassword)
                            .addOnCompleteListener(activity) { task ->
                                if (task.isSuccessful) {
                                    onRegisterSuccess()
                                } else {
                                    registerError = when (task.exception) {
                                        is FirebaseAuthUserCollisionException -> "Email already registered"
                                        else -> "Registration failed: ${task.exception?.message}"
                                    }
                                }
                            }
                    } else if (!acceptedTerms) {
                        registerError = "Please accept terms"
                    }
                },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(
                        brush = Brush.horizontalGradient(colors = listOf(ZenFlowOrange, Color(0xFFFF8A65)))
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Sign Up", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            if (registerError.isNotEmpty()) {
                Text(registerError, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.padding(bottom = 32.dp)) {
                Text(text = "Already have an account? ", color = Color.Gray)
                Text(
                    text = "Log In",
                    color = ZenFlowOrange,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}

@Composable
fun RegisterField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    isPassword: Boolean = false,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    Column {
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(28.dp)).clip(RoundedCornerShape(28.dp)),
            placeholder = { Text(placeholder, color = Color.Gray) },
            leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = ZenFlowOrange) },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            isError = isError,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )
        if (isError) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }
    }
}

// FUNCIONES DE VALIDACIÓN ADICIONALES
fun validateName(name: String): Pair<Boolean, String> =
    if (name.isNotBlank()) Pair(true, "") else Pair(false, "Name cannot be empty")

fun validatePasswordConfirm(pass: String, conf: String): Pair<Boolean, String> =
    if (pass == conf) Pair(true, "") else Pair(false, "Passwords do not match")