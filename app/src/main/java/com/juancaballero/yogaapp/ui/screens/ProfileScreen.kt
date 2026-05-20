package com.juancaballero.yogaapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.juancaballero.yogaapp.ui.theme.ZenFlowBg
import com.juancaballero.yogaapp.ui.theme.ZenFlowOrange

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onHomeClick: () -> Unit,
    onDiscoverClick: () -> Unit,
    onEditProfileClick: () -> Unit
) {
    var totalMinutes by remember { mutableIntStateOf(0) }
    var routinesCount by remember { mutableIntStateOf(0) }
    val db = Firebase.firestore
    val auth = Firebase.auth

    LaunchedEffect(Unit) {
        auth.currentUser?.uid?.let { uid ->
            db.collection("users").document(uid).get().addOnSuccessListener { doc ->
                totalMinutes = doc.getLong("totalMinutes")?.toInt() ?: 0
                routinesCount = doc.getLong("routinesCompleted")?.toInt() ?: 0
            }
        }
    }

    Scaffold(
        bottomBar = { // INTEGRACIÓN DE LA BARRA DE NAVEGACIÓN INFERIOR
            ZenFlowBottomBar(
                currentRoute = "profile",
                onHomeClick = onHomeClick,
                onDiscoverClick = onDiscoverClick,
                onProfileClick = {}
            )
        },
        containerColor = ZenFlowBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cabecera con botón para Editar Perfil
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Your Progress", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = onEditProfileClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = ZenFlowOrange)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Imagen/Avatar de perfil
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(ZenFlowOrange, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(60.dp))
            }
            Spacer(modifier = Modifier.height(32.dp))

            // Tarjetas de Progreso
            ProgressCard(title = "Total Minutes", value = "$totalMinutes", icon = Icons.Default.Timer)
            ProgressCard(title = "Routines Completed", value = "$routinesCount", icon = Icons.Default.CheckCircle)
            Spacer(modifier = Modifier.height(24.dp))

            // Barra de Experiencia y Nivel
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Level 1 (Novice)", fontWeight = FontWeight.Bold)
                Text("350 / 500 XP", color = Color.Gray, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { 0.7f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = ZenFlowOrange,
                trackColor = Color(0xFFFFF0EB)
            )

            Spacer(modifier = Modifier.height(32.dp))
            ReminderSwitchCard()

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de Cerrar Sesión
            Button(
                onClick = { auth.signOut(); onLogout() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
                shape = RoundedCornerShape(25.dp)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProgressCard(title: String, value: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).shadow(2.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = ZenFlowOrange)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, color = Color.Gray, fontSize = 14.sp)
                Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ReminderSwitchCard() {
    var isReminderOn by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = ZenFlowOrange)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Daily Reminders", fontWeight = FontWeight.Bold)
                    Text("Remind me to stretch", fontSize = 12.sp, color = Color.Gray)
                }
            }
            Switch(checked = isReminderOn, onCheckedChange = { isReminderOn = it }, colors = SwitchDefaults.colors(checkedTrackColor = ZenFlowOrange))
        }
    }
}