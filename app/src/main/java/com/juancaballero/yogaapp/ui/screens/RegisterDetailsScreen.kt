package com.juancaballero.yogaapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import com.google.firebase.firestore.SetOptions
import com.juancaballero.yogaapp.ui.components.ZenFlowTextField
import com.juancaballero.yogaapp.ui.theme.ZenFlowOrange
import com.juancaballero.yogaapp.ui.theme.ZenFlowBg

@Composable
fun RegisterDetailsScreen(onStartJourney: () -> Unit) {
    var age by remember { mutableStateOf("") }
    var experienceLevel by remember { mutableStateOf("Beginner") }
    var hasInjuries by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val auth = Firebase.auth
    val db = Firebase.firestore

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ZenFlowBg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "Let's get to know you",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(40.dp))

        ZenFlowTextField(value = age, onValueChange = { age = it }, label = "Age")

        Spacer(modifier = Modifier.height(16.dp))

        ZenFlowTextField(value = experienceLevel, onValueChange = { experienceLevel = it }, label = "Experience Level")

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = hasInjuries,
                onCheckedChange = { hasInjuries = it },
                colors = CheckboxDefaults.colors(ZenFlowOrange)
            )
            Text(text = "I have previous injuries", color = Color.Black)
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    isLoading = true

                    val userDetails = hashMapOf(
                        "age" to age,
                        "experience" to experienceLevel,
                        "hasInjuries" to hasInjuries,
                        "totalMinutes" to 0,
                        "routinesCompleted" to 0
                    )

                    // Se agregó SetOptions.merge() para fusionar los datos
                    db.collection("users").document(userId)
                        .set(userDetails, SetOptions.merge())
                        .addOnSuccessListener {
                            isLoading = false
                            onStartJourney()
                        }
                        .addOnFailureListener {
                            isLoading = false
                        }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isLoading,
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ZenFlowOrange)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(text = "Start my journey", color = Color.White, fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}