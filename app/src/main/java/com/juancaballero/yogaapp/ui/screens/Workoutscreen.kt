package com.juancaballero.yogaapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.juancaballero.yogaapp.ui.theme.ZenFlowOrange
import kotlinx.coroutines.delay

// Función para guardar el progreso en Firebase
fun updateProgress(minutesToAdd: Int) {
    val db = Firebase.firestore
    val uid = Firebase.auth.currentUser?.uid

    if (uid != null) {
        val userRef = db.collection("users").document(uid)
        // Usamos increment para que Firebase sume automáticamente
        userRef.update(
            "totalMinutes", FieldValue.increment(minutesToAdd.toLong()),
            "routinesCompleted", FieldValue.increment(1)
        )
    }
}

@Composable
fun WorkoutScreen(totalMinutes: Int, routineTitle: String, onFinish: () -> Unit) {
    // ESTADOS: Segundos restantes y si el reloj está activo
    var secondsLeft by remember { mutableIntStateOf(totalMinutes * 60) }
    var isRunning by remember { mutableStateOf(false) }

    // EL MOTOR DEL RELOJ
    LaunchedEffect(isRunning, secondsLeft) {
        if (isRunning && secondsLeft > 0) {
            delay(1000L) // Espera un segundo
            secondsLeft -= 1
        }
    }

    // Formateo del tiempo (ej: 05:00)
    val displayMinutes = secondsLeft / 60
    val displaySeconds = secondsLeft % 60
    val timeFormatted = String.format("%d:%02d", displayMinutes, displaySeconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Títulos
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 40.dp)
        ) {
            Text(text = routineTitle, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Focus on your breathing", fontSize = 16.sp, color = Color.Gray)
        }

        // CÍRCULO CENTRAL CON CLICK PARA INICIAR/PAUSAR
        Box(
            modifier = Modifier
                .size(280.dp)
                .shadow(12.dp, CircleShape)
                .background(Color.White, CircleShape)
                .clickable { isRunning = !isRunning }, // <-- Toca para empezar!
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = ZenFlowOrange,
                    modifier = Modifier.size(48.dp)
                )

                Text(
                    text = timeFormatted,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZenFlowOrange
                )
                Text(
                    text = "MINUTES",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // BOTÓN FINALIZAR (Guarda en Firebase y vuelve al Home)
        Button(
            onClick = {
                // LLAMAMOS A LA FUNCIÓN DE FIREBASE ANTES DE SALIR
                updateProgress(minutesToAdd = totalMinutes)
                onFinish()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ZenFlowOrange)
        ) {
            Text(text = "✓ Finish Workout", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}