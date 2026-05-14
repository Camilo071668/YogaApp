@file:OptIn(ExperimentalMaterial3Api::class) // Necesario para los menús desplegables de Material 3

package com.juancaballero.yogaapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore

@Composable
fun RegisterDetailsScreen(
    onStartJourney: () -> Unit
) {
    val context = LocalContext.current

    // Variables de Estado
    var isLoading by remember { mutableStateOf(false) }
    var hasInjuries by remember { mutableStateOf(false) }

    // Listas para los menús desplegables
    val ageOptions = listOf("Under 18", "18 - 25", "26 - 35", "36 - 45", "46 - 55", "56+")
    val experienceOptions = listOf("Beginner", "Intermediate", "Advanced")

    var selectedAge by remember { mutableStateOf(ageOptions[0]) }
    var selectedExperience by remember { mutableStateOf(experienceOptions[0]) }

    // Controlar si los menús están abiertos o cerrados
    var ageExpanded by remember { mutableStateOf(false) }
    var experienceExpanded by remember { mutableStateOf(false) }

    // Colores de tu diseño
    val zenFlowBg = Color(0xFFFAFAFA)
    val zenFlowOrange = Color(0xFFFF8A65)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(zenFlowBg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "Let's get to know you",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(40.dp))

        // ----------------------------------------------------
        // MENÚ DESPLEGABLE: EDAD (AGE)
        // ----------------------------------------------------
        ExposedDropdownMenuBox(
            expanded = ageExpanded,
            onExpandedChange = { ageExpanded = !ageExpanded }
        ) {
            OutlinedTextField(
                value = selectedAge,
                onValueChange = {},
                readOnly = true, // Evita que se escriba con el teclado
                label = { Text("Select your age") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = ageExpanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedBorderColor = zenFlowOrange,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = ageExpanded,
                onDismissRequest = { ageExpanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                ageOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            selectedAge = selectionOption
                            ageExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ----------------------------------------------------
        // MENÚ DESPLEGABLE: EXPERIENCIA
        // ----------------------------------------------------
        ExposedDropdownMenuBox(
            expanded = experienceExpanded,
            onExpandedChange = { experienceExpanded = !experienceExpanded }
        ) {
            OutlinedTextField(
                value = selectedExperience,
                onValueChange = {},
                readOnly = true,
                label = { Text("Experience level") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = experienceExpanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedBorderColor = zenFlowOrange,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = experienceExpanded,
                onDismissRequest = { experienceExpanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                experienceOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            selectedExperience = selectionOption
                            experienceExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // CHECKBOX DE LESIONES
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = hasInjuries,
                onCheckedChange = { hasInjuries = it },
                colors = CheckboxDefaults.colors(checkedColor = zenFlowOrange)
            )
            Text(text = "I have previous injuries", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                isLoading = true // Enciende la ruedita de carga

                val uid = Firebase.auth.currentUser?.uid
                if (uid != null) {
                    val db = Firebase.firestore

                    // Preparamos los datos a guardar
                    val userData = hashMapOf(
                        "age" to selectedAge,
                        "experience" to selectedExperience,
                        "hasInjuries" to hasInjuries,
                        "totalMinutes" to 0,       // Inicializamos los minutos en 0
                        "routinesCompleted" to 0   // Inicializamos rutinas en 0
                    )

                    // Usamos SetOptions.merge() para no borrar el nombre del usuario si ya existía
                    db.collection("users").document(uid).set(userData, SetOptions.merge())
                        .addOnSuccessListener {
                            isLoading = false // Apaga la ruedita
                            onStartJourney()  // Navega al Home
                        }
                        .addOnFailureListener { e ->
                            isLoading = false // Apaga la ruedita si hay error
                            Toast.makeText(context, "Error saving data: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                } else {
                    isLoading = false
                    Toast.makeText(context, "Error: User not logged in", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = zenFlowOrange),
            enabled = !isLoading // Deshabilita el botón mientras carga para evitar dobles clics
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Start Journey", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}