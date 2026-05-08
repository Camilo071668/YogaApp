package com.juancaballero.yogaapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
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

            Text(text = "Hello, $userName", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = "What do you need today?", fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(32.dp))

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