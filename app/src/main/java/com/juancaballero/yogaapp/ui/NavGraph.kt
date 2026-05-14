package com.juancaballero.yogaapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.juancaballero.yogaapp.ui.screens.*

@Composable
fun ZenFlowNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        // 1. LOGIN
        composable(route = "login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("home") },
                onClickRegister = { navController.navigate("register") }
            )
        }

        // 2. REGISTER
        composable(route = "register") {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate("details") },
                onNavigateToLogin = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        // 3. DETAILS (Let's get to know you)
        composable(route = "details") {
            RegisterDetailsScreen(onStartJourney = {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }

        // 4. HOME
        composable(route = "home") {
            HomeScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onProfileClick = { navController.navigate("profile") },
                // Recibir los dos datos (title y duration) y se pasa a la ruta
                onWorkoutClick = { title, duration ->
                    navController.navigate("workout_timer/$title/$duration")
                }
            )
        }

        // 5. PROFILE
        composable(route = "profile") {
            ProfileScreen {
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            }
        }

        // 6. Timer Dinamico (funciona para 5, 8 y 10 min)
        composable(
            route = "workout_timer/{title}/{duration}",
            arguments = listOf(
                navArgument("title") { type = NavType.StringType },
                navArgument("duration") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: "Routine"
            val durationStr = backStackEntry.arguments?.getString("duration") ?: "5 min"

            // Convertir "10 min" en el número 10
            val minutes = durationStr.split(" ")[0].toIntOrNull() ?: 5

            WorkoutScreen(
                routineTitle = title,
                totalMinutes = minutes,
                onFinish = { navController.popBackStack() }
            )
        }
    }
}


