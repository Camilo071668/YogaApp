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

@Composable
fun ZenFlowNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("home") },
                onClickRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate("details") },
                onNavigateToLogin = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("details") {
            RegisterDetailsScreen(onStartJourney = {
                navController.navigate("home") { popUpTo("login") { inclusive = true } }
            })
        }
        composable("home") {
            HomeScreen(
                onLogout = { navController.navigate("login") },
                onWorkoutClick = { navController.navigate("workout_timer") },
                onProfileClick = { navController.navigate("profile") }
            )
        }
        composable("profile") {
            ProfileScreen(onBack = { navController.popBackStack() })
        }
    }
}