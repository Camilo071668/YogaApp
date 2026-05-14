package com.juancaballero.yogaapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.juancaballero.yogaapp.ui.theme.ZenFlowBg
import com.juancaballero.yogaapp.ui.theme.ZenFlowOrange

@Composable
fun ProfileScreen(onLogout: () -> Unit) { // Limpié el parámetro 'item' que causaba conflicto
    var totalMinutes by remember { mutableIntStateOf(0) }
    var routinesCount by remember { mutableIntStateOf(0) }
    val db = Firebase.firestore
    val auth = Firebase.auth
    val uid = auth.currentUser?.uid

    // Cargar datos de Firebase
    LaunchedEffect(Unit) {
        uid?.let {
            db.collection("users").document(it).get().addOnSuccessListener { doc ->
                totalMinutes = doc.getLong("totalMinutes")?.toInt() ?: 0
                routinesCount = doc.getLong("routinesCompleted")?.toInt() ?: 0
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ZenFlowBg)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text("Your Progress", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        // RASTREADOR DE ÁNIMO
        MoodTrackerCard()

        Spacer(modifier = Modifier.height(32.dp))

        // Imagen de perfil original
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(ZenFlowOrange, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(60.dp))
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Tarjetas de progreso
        ProgressCard(title = "Total Minutes", value = "$totalMinutes", icon = Icons.Default.Timer)
        ProgressCard(title = "Routines Completed", value = "$routinesCount", icon = Icons.Default.CheckCircle)

        Spacer(modifier = Modifier.height(16.dp))

        // --- FUNCIONALIDAD: BARRA DE NIVEL (XP) ---
        // (Quitamos el 'item' y dejamos el contenido directo)
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Level 1 (Novice)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("350 / 500 XP", color = Color.Gray, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { 0.7f },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = ZenFlowOrange,
                trackColor = Color.LightGray.copy(alpha = 0.3f)
            )
        }

        // --- FUNCIONALIDAD: LOGROS (BADGES) ---
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Achievements", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                BadgeIcon("🔥", "7 Day Streak", unlocked = true)
                BadgeIcon("🧘", "First Session", unlocked = true)
                BadgeIcon("🏆", "Zen Master", unlocked = false)
                BadgeIcon("🌑", "Night Owl", unlocked = false)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // INTERRUPTOR DE RECORDATORIOS
        ReminderSwitchCard()

        Spacer(modifier = Modifier.height(40.dp))

        // BOTÓN DE CERRAR SESIÓN
        Button(
            onClick = {
                auth.signOut()
                onLogout()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
            shape = RoundedCornerShape(25.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log Out", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

// --- COMPONENTE DE MEDALLAS ---
@Composable
fun BadgeIcon(emoji: String, title: String, unlocked: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(if (unlocked) Color(0xFFFFF3E0) else Color(0xFFF5F5F5), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 24.sp, modifier = Modifier.alpha(if (unlocked) 1f else 0.3f))
        }
        Text(title, fontSize = 10.sp, color = if (unlocked) Color.Black else Color.Gray)
    }
}

@Composable
fun ProgressCard(title: String, value: String, icon: ImageVector) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = ZenFlowOrange)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, color = Color.Gray, fontSize = 14.sp)
                Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

@Composable
fun MoodTrackerCard() {
    var selectedMood by remember { mutableStateOf<String?>(null) }
    val moods = listOf(
        "😢" to "Sad",
        "😐" to "Okay",
        "🙂" to "Good",
        "🤩" to "Great"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("How are you feeling today?", fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                moods.forEach { (emoji, label) ->
                    val isSelected = selectedMood == label
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { selectedMood = label }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(
                                    if (isSelected) Color(0xFFFFCC80) else Color(0xFFF5F5F5), // Naranja si está seleccionado
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = label, fontSize = 12.sp, color = if (isSelected) Color(0xFFFF8A65) else Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun ReminderSwitchCard() {
    // Esto guarda si el interruptor está prendido (true) o apagado (false)
    var isReminderOn by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Separa el texto a la izquierda y el switch a la derecha
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFFFF8A65))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Daily Reminders", fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Text("Remind me to stretch", fontSize = 12.sp, color = Color.Gray)
                }
            }
            // El interruptor interactivo
            Switch(
                checked = isReminderOn,
                onCheckedChange = { isReminderOn = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFFFF8A65) // Naranja ZenFlow
                )
            )
        }
    }
}