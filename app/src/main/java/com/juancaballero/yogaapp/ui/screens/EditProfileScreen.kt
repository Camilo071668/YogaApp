package com.juancaballero.yogaapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.juancaballero.yogaapp.ui.theme.ZenFlowBg
import com.juancaballero.yogaapp.ui.theme.ZenFlowOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    onAccountDeleted: () -> Unit
) {
    val auth = Firebase.auth
    val db = Firebase.firestore
    val context = LocalContext.current

    var newName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Cargar nombre actual
    LaunchedEffect(Unit) {
        auth.currentUser?.uid?.let { uid ->
            db.collection("users").document(uid).get().addOnSuccessListener {
                newName = it.getString("fullName") ?: ""
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ZenFlowBg)
            )
        },
        containerColor = ZenFlowBg
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Update your Information", color = Color.Gray, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(24.dp))

            // Campo Nombre
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = ZenFlowOrange) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ZenFlowOrange, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón Guardar
            Button(
                onClick = {
                    if (newName.isNotBlank()) {
                        isLoading = true
                        auth.currentUser?.uid?.let { uid ->
                            db.collection("users").document(uid).update("fullName", newName)
                                .addOnSuccessListener {
                                    isLoading = false
                                    Toast.makeText(context, "Profile updated!", Toast.LENGTH_SHORT).show()
                                    onBack()
                                }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ZenFlowOrange)
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Changes", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón Peligroso (Eliminar Cuenta)
            OutlinedButton(
                onClick = {
                    auth.currentUser?.delete()?.addOnSuccessListener {
                        Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show()
                        onAccountDeleted()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
            ) {
                Icon(Icons.Default.DeleteForever, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete Account", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}