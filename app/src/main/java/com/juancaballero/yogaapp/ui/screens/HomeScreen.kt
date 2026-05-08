package com.juancaballero.yogaapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import kotlinx.coroutines.delay
// --- IMPORTS NUEVOS PARA LAS ANIMACIONES Y FORMAS ---
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import kotlinx.coroutines.delay
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.draw.shadow
// ----------------------------------------------------
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.juancaballero.yogaapp.R
import com.juancaballero.yogaapp.ui.components.WorkoutCard
import com.juancaballero.yogaapp.ui.theme.ZenFlowOrange
import com.juancaballero.yogaapp.ui.theme.ZenFlowBg
import com.google.firebase.firestore.SetOptions

@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onWorkoutClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    var userName by remember { mutableStateOf("Loading...") }
    val db = Firebase.firestore
    val auth = Firebase.auth

    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    // Si el documento existe, traemos el nombre
                    userName = document.getString("fullName") ?: "User"
                }
                .addOnFailureListener {
                    userName = "Zen User"
                }
        }
    }

    val workouts = listOf(
        WorkoutItem("Morning Stretch", "5 min", R.drawable.ic_sun),
        WorkoutItem("Office Pause", "10 min", R.drawable.ic_office),
        WorkoutItem("Night Relaxation", "8 min", R.drawable.ic_moon)
    )

    Scaffold(
        bottomBar = {
            ZenFlowBottomBar(onProfileClick = onProfileClick)
        },
        containerColor = ZenFlowBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // 1. Tus textos de bienvenida originales
            Text(text = "Hello, $userName", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = "What do you need today?", fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp)) // Reduje un poco este espacio a 16.dp para que quepa todo

            // ---------------------------------------------------------
            // ¡AQUÍ ESTAMOS LLAMANDO A LA NUEVA TARJETA DE RESPIRACIÓN!
            // ---------------------------------------------------------
            BreathingCard()

            // Agregamos un espacio para separar la tarjeta animada de la lista de ejercicios
            Spacer(modifier = Modifier.height(24.dp))

            // 2. Tu lista de ejercicios original (LazyColumn)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(workouts) { workout ->
                    WorkoutCard(
                        title = workout.title,
                        duration = workout.duration,
                        iconRes = workout.iconRes,
                        onClick = onWorkoutClick
                    )
                }
            }
        }
    }
}

@Composable
fun ZenFlowBottomBar(onProfileClick: () -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home") },
            selected = true,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ZenFlowOrange,
                selectedTextColor = ZenFlowOrange,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
            label = { Text("Workout") },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Profile") },
            selected = false,
            onClick = onProfileClick,
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray)
        )
    }
}

data class WorkoutItem(val title: String, val duration: String, val iconRes: Int)

@Composable
fun DailyQuoteCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)), // Un naranja muy clarito
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFFF8A65), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Daily Zen", color = Color(0xFFFF8A65), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "\"Yoga is the journey of the self, through the self, to the self.\"",
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = Color.DarkGray,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun WaterTracker() {
    var glasses by remember { mutableStateOf(0) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).background(Color(0xFFE3F2FD), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Si no tienes un icono de gota de agua, usamos Add o Info por ahora
                Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF42A5F5))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Daily Water", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("$glasses / 8 glasses", color = Color.Gray, fontSize = 12.sp)
            }
        }

        Button(
            onClick = { if (glasses < 8) glasses++ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42A5F5)),
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(36.dp)
        ) {
            Text("+", fontSize = 20.sp, color = Color.White)
        }
    }
}

@Composable
fun BreathingCard() {
    var isBreathingIn by remember { mutableStateOf(true) }

    // Animación del tamaño del círculo
    val scale by animateFloatAsState(
        targetValue = if (isBreathingIn) 1.5f else 1f,
        animationSpec = tween(durationMillis = 4000, easing = LinearEasing),
        label = "breatheAnimation"
    )

    // Bucle infinito que cambia el estado cada 4 segundos
    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            isBreathingIn = !isBreathingIn
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), // Verde muy suave y zen
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Take a moment to breathe",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Círculo animado
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .scale(scale)
                    .background(Color(0xFF81C784), CircleShape),
                contentAlignment = Alignment.Center
            ) {}

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = if (isBreathingIn) "Inhale..." else "Exhale...",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray
            )
        }
    }
}