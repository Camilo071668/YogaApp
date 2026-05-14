package com.juancaballero.yogaapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.juancaballero.yogaapp.ui.theme.ZenFlowOrange
import kotlinx.coroutines.delay

fun updateProgress(minutesToAdd: Int) {
    val uid = Firebase.auth.currentUser?.uid
    if (uid != null) {
        Firebase.firestore.collection("users").document(uid).update(
            "totalMinutes", FieldValue.increment(minutesToAdd.toLong()),
            "routinesCompleted", FieldValue.increment(1)
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun WorkoutScreen(totalMinutes: Int, routineTitle: String, onFinish: () -> Unit) {
    val totalSeconds = totalMinutes * 60
    var secondsLeft by remember { mutableIntStateOf(totalSeconds) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning, secondsLeft) {
        if (isRunning && secondsLeft > 0) {
            delay(1000L)
            secondsLeft -= 1
        }
    }

    // ANIMACIÓN DEL CÍRCULO (Calcula qué porcentaje del círculo dibujar)
    val progressPercent = if (totalSeconds > 0) secondsLeft.toFloat() / totalSeconds.toFloat() else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progressPercent,
        animationSpec = tween(1000, easing = LinearEasing), label = ""
    )

    val displayMinutes = secondsLeft / 60
    val displaySeconds = secondsLeft % 60
    val timeFormatted = String.format("%d:%02d", displayMinutes, displaySeconds)

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 40.dp)) {
            Text(text = routineTitle, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Focus on your breathing", fontSize = 16.sp, color = Color.Gray)
        }

        // RELOJ ANIMADO PREMIUM
        Box(
            modifier = Modifier.size(300.dp),
            contentAlignment = Alignment.Center
        ) {
            // El anillo de progreso circular
            CircularProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier.fillMaxSize(),
                color = ZenFlowOrange,
                strokeWidth = 16.dp, // Grosor del anillo
                trackColor = Color(0xFFFFF0EB) // Color de fondo del anillo
            )

            // El botón central
            Box(
                modifier = Modifier.size(240.dp).shadow(12.dp, CircleShape).background(Color.White, CircleShape)
                    .clickable { isRunning = !isRunning },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null, tint = ZenFlowOrange, modifier = Modifier.size(48.dp)
                    )
                    Text(timeFormatted, fontSize = 64.sp, fontWeight = FontWeight.Bold, color = ZenFlowOrange)
                    Text("MINUTES", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }
        }

        Button(
            onClick = { updateProgress(totalMinutes); onFinish() },
            modifier = Modifier.fillMaxWidth().height(60.dp).padding(bottom = 8.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ZenFlowOrange)
        ) {
            Text("✓ Finish Workout", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}