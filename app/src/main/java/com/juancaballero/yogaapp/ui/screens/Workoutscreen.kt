package com.juancaballero.yogaapp.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

// Interfaz Gráfica de la pantalla de ejercicio
@Composable
fun ActiveWorkoutScreen(
    workoutName: String = "Morning Stretch",
    durationMinutes: Int = 5,
    onBack: () -> Unit // Función para volver al Home tras terminar
) {
    // Usamos los colores de tu tema
    val zenFlowBg = Color(0xFFFAFAFA)
    val zenFlowOrange = Color(0xFFFF8A65)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(zenFlowBg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título del Ejercicio
        Text(text = workoutName, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Focus on your breathing", fontSize = 16.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(60.dp))

        // Círculo gigante simulando el temporizador / estado
        Box(
            modifier = Modifier
                .size(250.dp)
                .shadow(8.dp, CircleShape)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = zenFlowOrange,
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "$durationMinutes:00",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = zenFlowOrange
                )
                Text(
                    text = "MINUTES",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))

        // Botón para terminar y guardar en Firebase
        Button(
            onClick = {
                // Llama a tu función de Firebase para sumar minutos y rutinas
                updateProgress(minutesToAdd = durationMinutes)
                // Vuelve a la pantalla anterior
                onBack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = zenFlowOrange)
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Finish Workout", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}