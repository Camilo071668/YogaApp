package com.juancaballero.yogaapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.juancaballero.yogaapp.R
import com.juancaballero.yogaapp.ui.theme.ZenFlowBg
import com.juancaballero.yogaapp.ui.theme.ZenFlowOrange
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onWorkoutClick: (String, String) -> Unit,
    onProfileClick: () -> Unit,
    onDiscoverClick: () -> Unit,
    onLogout: () -> Unit
) {
    var userName by remember { mutableStateOf("Loading...") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    // Lógica para traer el nombre
    LaunchedEffect(Unit) {
        Firebase.auth.currentUser?.uid?.let { uid ->
            Firebase.firestore.collection("users").document(uid).get()
                .addOnSuccessListener { doc -> userName = doc.getString("fullName") ?: "Zen User" }
        }
    }

    val allWorkouts = listOf(
        WorkoutItem("Morning Stretch", "5 min", Icons.Default.WbSunny, "Stretching"),
        WorkoutItem("Office Pause", "10 min", Icons.Default.Chair, "Focus"),
        WorkoutItem("Night Relaxation", "8 min", Icons.Default.Bedtime, "Meditation"),
        WorkoutItem("Core Power", "15 min", Icons.Default.FitnessCenter, "Power"),
        WorkoutItem("Deep Breath", "5 min", Icons.Default.Air, "Meditation"),
        WorkoutItem("Flexibility", "20 min", Icons.Default.AccessibilityNew, "Stretching"),
        WorkoutItem("Energy Boost", "10 min", Icons.Default.Bolt, "Power")
    )

    // Filtrado por texto y categoría
    val filteredWorkouts = allWorkouts.filter {
        it.title.contains(searchQuery, ignoreCase = true) &&
                (selectedCategory == "All" || it.category == selectedCategory)
    }

    Scaffold(
        bottomBar = { // <-- AQUÍ VOLVEMOS A AGREGAR LA BARRA INFERIOR
            ZenFlowBottomBar(
                currentRoute = "home",
                onHomeClick = {},
                onDiscoverClick = onDiscoverClick,
                onProfileClick = onProfileClick
            )
        },
        containerColor = ZenFlowBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // CABECERA CON NOTIFICACIÓN
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = "Hello, $userName", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                    Text(text = "How are you feeling today?", fontSize = 16.sp, color = Color.Gray)
                }
                IconButton(onClick = { /* Notificaciones */ }, modifier = Modifier.background(Color.White, CircleShape).shadow(2.dp, CircleShape)) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = ZenFlowOrange)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // MOOD SELECTOR
            MoodSelector()

            Spacer(modifier = Modifier.height(24.dp))

            // BUSCADOR
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search routines...", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                leadingIcon = { Icon(Icons.Default.Search, null, tint = ZenFlowOrange) },
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // CATEGORÍAS
            Text("Categories", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            LazyRow(modifier = Modifier.padding(vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val categories = listOf("All", "Stretching", "Meditation", "Focus", "Power")
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat) },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ZenFlowOrange, selectedLabelColor = Color.White)
                    )
                }
            }

            // CARRUSEL DE RUTINAS
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(filteredWorkouts) { workout ->
                    PremiumRoutineCard(
                        title = workout.title,
                        duration = workout.duration,
                        icon = workout.icon
                    ) {
                        onWorkoutClick(workout.title, workout.duration)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // RESUMEN DE SALUD
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.weight(1f)) { WaterTrackerWidget() }
                Box(modifier = Modifier.weight(1f)) { DailyStepsWidget() }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // TARJETA DE RESPIRACIÓN
            BreathingCard()

            Spacer(modifier = Modifier.height(24.dp))

            // CITA DIARIA
            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)), shape = RoundedCornerShape(20.dp)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFFFB74D))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("\"Yoga is the journey of the self, through the self, to the self.\"", fontSize = 14.sp, fontStyle = FontStyle.Italic, color = Color.DarkGray)
                }
            }
        }
    }
}

@Composable
fun PremiumRoutineCard(title: String, duration: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp)
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Box(modifier = Modifier.size(48.dp).background(Color(0xFFFFF0EB), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = ZenFlowOrange)
            }
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
fun BreathingCard() {
    var isBreathingIn by remember { mutableStateOf(true) }
    val scale by animateFloatAsState(
        targetValue = if (isBreathingIn) 1.5f else 1f,
        animationSpec = tween(4000, easing = LinearEasing),
        label = ""
    )
    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            isBreathingIn = !isBreathingIn
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Take a moment to breathe", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
            Spacer(modifier = Modifier.height(32.dp))
            Box(modifier = Modifier.size(60.dp).scale(scale).background(Color(0xFF81C784), CircleShape), contentAlignment = Alignment.Center) {}
            Spacer(modifier = Modifier.height(32.dp))
            Text(if (isBreathingIn) "Inhale..." else "Exhale...", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
        }
    }
}

@Composable
fun ZenFlowBottomBar(currentRoute: String, onHomeClick: () -> Unit, onDiscoverClick: () -> Unit, onProfileClick: () -> Unit) {
    NavigationBar(containerColor = Color.White, tonalElevation = 16.dp) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) }, label = { Text("Home") },
            selected = currentRoute == "home", onClick = onHomeClick,
            colors = NavigationBarItemDefaults.colors(selectedIconColor = ZenFlowOrange, selectedTextColor = ZenFlowOrange, indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Explore, contentDescription = null) }, label = { Text("Discover") },
            selected = currentRoute == "discover", onClick = onDiscoverClick,
            colors = NavigationBarItemDefaults.colors(selectedIconColor = ZenFlowOrange, selectedTextColor = ZenFlowOrange, indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null) }, label = { Text("Profile") },
            selected = currentRoute == "profile", onClick = onProfileClick,
            colors = NavigationBarItemDefaults.colors(selectedIconColor = ZenFlowOrange, selectedTextColor = ZenFlowOrange, indicatorColor = Color.Transparent)
        )
    }
}

@Composable
fun MoodSelector() {
    val moods = listOf("😔" to "Low", "😐" to "Okay", "😊" to "Good", "🤩" to "Great")
    var selected by remember { mutableStateOf("Good") }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        moods.forEach { (emoji, label) ->
            val isSelected = selected == label
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { selected = label }
                    .background(if (isSelected) ZenFlowOrange.copy(alpha = 0.1f) else Color.Transparent)
                    .padding(8.dp)
            ) {
                Text(emoji, fontSize = 28.sp)
                Text(label, fontSize = 10.sp, color = if (isSelected) ZenFlowOrange else Color.Gray, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun WaterTrackerWidget() {
    var water by remember { mutableIntStateOf(3) }
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF2196F3))
            Text("Water", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("$water/8 cups", fontSize = 12.sp, color = Color.Gray)
            LinearProgressIndicator(progress = { water/8f }, modifier = Modifier.padding(top = 8.dp).fillMaxWidth().height(4.dp).clip(CircleShape), color = Color(0xFF2196F3))
        }
    }
}

@Composable
fun DailyStepsWidget() {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(Icons.AutoMirrored.Filled.DirectionsRun, contentDescription = null, tint = Color(0xFF4CAF50))
            Text("Steps", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("4,230 pts", fontSize = 12.sp, color = Color.Gray)
            LinearProgressIndicator(progress = { 0.4f }, modifier = Modifier.padding(top = 8.dp).fillMaxWidth().height(4.dp).clip(CircleShape), color = Color(0xFF4CAF50))
        }
    }
}

data class WorkoutItem(val title: String, val duration: String, val icon: ImageVector, val category: String)