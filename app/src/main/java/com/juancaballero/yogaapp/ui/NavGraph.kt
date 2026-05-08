package com.juancaballero.yogaapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.juancaballero.yogaapp.ui.screens.HomeScreen
import com.juancaballero.yogaapp.ui.screens.LoginScreen
import com.juancaballero.yogaapp.ui.screens.ProfileScreen
import com.juancaballero.yogaapp.ui.screens.RegisterScreen
import com.juancaballero.yogaapp.ui.screens.RegisterDetailsScreen
import com.juancaballero.yogaapp.ui.screens.ActiveWorkoutScreen // <-- ¡Aquí está el import que faltaba!

@Composable
fun ZenFlowNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable(route = "login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("home") },
                onClickRegister = { navController.navigate("register") }
            )
        }
        composable(route = "register") {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate("details") },
                onNavigateToLogin = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(route = "details") {
            RegisterDetailsScreen(onStartJourney = {
                navController.navigate("home") { popUpTo("login") { inclusive = true } }
            })
        }
        composable(route = "home") {
            HomeScreen(
                onLogout = { navController.navigate("login") },
                onWorkoutClick = { navController.navigate("workout_timer") },
                onProfileClick = { navController.navigate("profile") }
            )
        }
        composable(route = "profile") {
            // <-- ¡AQUÍ ARREGLAMOS EL ERROR DEL PROFILE! -->
            // Ahora le pasamos onLogout para que cuando toquen "Log Out", los mande al Login.
            ProfileScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true } // Esto borra el historial para que no puedan volver con el botón físico
                    }
                }
            )
        }

        // -------------------------------------------------------------------
        // PANTALLA DE EJERCICIO
        // -------------------------------------------------------------------
        composable(route = "workout_timer") {
            ActiveWorkoutScreen(
                workoutName = "Zen Flow Routine",
                durationMinutes = 5,
                onBack = { navController.popBackStack() } // Regresa al Home al terminar
            )
        }
    }
}