package com.juancaballero.yogaapp.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import com.juancaballero.yogaapp.ui.theme.ZenFlowOrange
import com.juancaballero.yogaapp.ui.theme.ZenFlowBg

@Composable
fun HomeScreen(
    onWorkoutClick: (String, String) -> Unit,
    onProfileClick: () -> Unit,
    onDiscoverClick: () -> Unit,
    onLogout: () -> Unit
) {
    var userName by remember { mutableStateOf("Loading...") }
    val db = Firebase.firestore
    val auth = Firebase.auth

    // VARIABLE PARA LA BARRA DE BÚSQUEDA
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { doc -> userName = doc.getString("fullName") ?: "User" }
                .addOnFailureListener { userName = "Zen User" }
        }
    }

    val allWorkouts = listOf(
        WorkoutItem("Morning Stretch", "5 min", Icons.Default.WbSunny),
        WorkoutItem("Office Pause", "10 min", Icons.Default.Chair),
        WorkoutItem("Night Relaxation", "8 min", Icons.Default.Bedtime),
        WorkoutItem("Core Power", "15 min", Icons.Default.FitnessCenter),
        WorkoutItem("Deep Breath", "5 min", Icons.Default.Air),
        WorkoutItem("Flexibility", "20 min", Icons.Default.AccessibilityNew),
        WorkoutItem("Stress Relief", "12 min", Icons.Default.SelfImprovement),
        WorkoutItem("Energy Boost", "10 min", Icons.Default.Bolt)
    )

    // ESTE CÓDIGO FILTRA LAS RUTINAS SEGÚN LO QUE ESCRIBAS
    val filteredWorkouts = allWorkouts.filter {
        it.title.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        bottomBar = { ZenFlowBottomBar(currentRoute = "home", onHomeClick = {}, onDiscoverClick = onDiscoverClick, onProfileClick = onProfileClick) },
        containerColor = ZenFlowBg
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(text = "Hello, $userName", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = "Ready to find your center?", fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))

            // BARRA DE BÚSQUEDA ARREGLADA
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it }, // Ahora sí guarda lo que escribes
                placeholder = { Text("Search routines...", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ZenFlowOrange) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) { // Botón "X" para borrar
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent)
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(if (searchQuery.isEmpty()) "Recommended for you" else "Search Results", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(12.dp))

            // CARRUSEL (Ahora muestra solo las rutinas filtradas)
            if (filteredWorkouts.isEmpty()) {
                Text("No routines found matching '$searchQuery'", color = Color.Gray, modifier = Modifier.padding(vertical = 16.dp))
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(filteredWorkouts) { workout ->
                        PremiumRoutineCard(
                            title = workout.title,
                            duration = workout.duration,
                            icon = workout.icon,
                            onClick = { onWorkoutClick(workout.title, workout.duration) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            BreathingCard()
            Spacer(modifier = Modifier.height(24.dp))

            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)), shape = RoundedCornerShape(20.dp)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB74D))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("\"Yoga is not about touching your toes, it's about what you learn on the way down.\"", fontSize = 14.sp, fontStyle = FontStyle.Italic, color = Color.DarkGray)
                }
            }
        }
    }
}

@Composable
fun PremiumRoutineCard(title: String, duration: String, icon: ImageVector, onClick: () -> Unit) {
    Card(modifier = Modifier.width(160.dp).height(200.dp).shadow(8.dp, RoundedCornerShape(24.dp)).clickable { onClick() }, shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Box(modifier = Modifier.size(48.dp).background(Color(0xFFFFF0EB), CircleShape), contentAlignment = Alignment.Center) { Icon(icon, contentDescription = null, tint = ZenFlowOrange) }
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(duration, color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ZenFlowBottomBar(currentRoute: String, onHomeClick: () -> Unit, onDiscoverClick: () -> Unit, onProfileClick: () -> Unit) {
    NavigationBar(containerColor = Color.White, tonalElevation = 16.dp) {
        NavigationBarItem(icon = { Icon(Icons.Default.Home, contentDescription = null) }, label = { Text("Home") }, selected = currentRoute == "home", onClick = onHomeClick, colors = NavigationBarItemDefaults.colors(selectedIconColor = ZenFlowOrange, selectedTextColor = ZenFlowOrange, indicatorColor = Color.Transparent))
        NavigationBarItem(icon = { Icon(Icons.Default.Explore, contentDescription = null) }, label = { Text("Discover") }, selected = currentRoute == "discover", onClick = onDiscoverClick, colors = NavigationBarItemDefaults.colors(selectedIconColor = ZenFlowOrange, selectedTextColor = ZenFlowOrange, indicatorColor = Color.Transparent))
        NavigationBarItem(icon = { Icon(Icons.Default.Person, contentDescription = null) }, label = { Text("Profile") }, selected = currentRoute == "profile", onClick = onProfileClick, colors = NavigationBarItemDefaults.colors(selectedIconColor = ZenFlowOrange, selectedTextColor = ZenFlowOrange, indicatorColor = Color.Transparent))
    }
}

data class WorkoutItem(val title: String, val duration: String, val icon: ImageVector)

@Composable
fun BreathingCard() {
    var isBreathingIn by remember { mutableStateOf(true) }
    val scale by animateFloatAsState(targetValue = if (isBreathingIn) 1.5f else 1f, animationSpec = tween(4000, easing = LinearEasing), label = "")
    LaunchedEffect(Unit) { while (true) { delay(4000); isBreathingIn = !isBreathingIn } }

    Card(modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp)), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), shape = RoundedCornerShape(20.dp)) {
        Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Take a moment to breathe", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
            Spacer(modifier = Modifier.height(32.dp))
            Box(modifier = Modifier.size(60.dp).scale(scale).background(Color(0xFF81C784), CircleShape), contentAlignment = Alignment.Center) {}
            Spacer(modifier = Modifier.height(32.dp))
            Text(if (isBreathingIn) "Inhale..." else "Exhale...", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
        }
    }
}