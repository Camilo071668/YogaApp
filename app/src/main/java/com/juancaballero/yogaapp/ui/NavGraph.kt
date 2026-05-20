package com.juancaballero.yogaapp.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.juancaballero.yogaapp.ui.screens.*
import com.juancaballero.yogaapp.ui.utils.AIPoseScreen // Importación de la pantalla de IA

@Composable
fun ZenFlowNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login",
        enterTransition = { fadeIn(tween(400)) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(400)) },
        exitTransition = { fadeOut(tween(400)) },
        popEnterTransition = { fadeIn(tween(400)) },
        popExitTransition = { fadeOut(tween(400)) + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(400)) }
    ) {

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
                onLogout = { navController.navigate("login") { popUpTo("home") { inclusive = true } } },
                onProfileClick = { navController.navigate("profile") },
                onDiscoverClick = { navController.navigate("discover") },
                onWorkoutClick = { title, duration -> navController.navigate("workout_timer/$title/$duration") }
            )
        }

        composable(route = "discover") {
            DiscoverScreen(
                onHomeClick = { navController.navigate("home") { popUpTo("discover") { inclusive = true } } },
                onProfileClick = { navController.navigate("profile") },
                onWorkoutClick = { title, duration -> navController.navigate("workout_timer/$title/$duration") },
                onBlogClick = { blogTitle -> navController.navigate("blog_detail/$blogTitle") }
            )
        }

        composable(route = "profile") {
            ProfileScreen(
                onLogout = { navController.navigate("login") { popUpTo(0) } },
                onHomeClick = { navController.navigate("home") { popUpTo("profile") { inclusive = true } } },
                onDiscoverClick = { navController.navigate("discover") },
                onEditProfileClick = { navController.navigate("edit_profile") }
            )
        }

        composable(route = "edit_profile") {
            EditProfileScreen(
                onBack = { navController.popBackStack() },
                onAccountDeleted = { navController.navigate("login") { popUpTo(0) } }
            )
        }

        composable(
            route = "blog_detail/{title}",
            arguments = listOf(navArgument("title") { type = NavType.StringType })
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: "Article"
            BlogDetailScreen(
                title = title,
                onBack = { navController.popBackStack() }
            )
        }

        // ENRUTAMIENTO DINÁMICO: Abre la cámara con IA y envía la rutina seleccionada
        composable(
            route = "workout_timer/{title}/{duration}",
            arguments = listOf(
                navArgument("title") { type = NavType.StringType },
                navArgument("duration") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: "Routine"
            val durationStr = backStackEntry.arguments?.getString("duration") ?: "5 min"
            val minutes = durationStr.split(" ")[0].toIntOrNull() ?: 5

            AIPoseScreen(
                exerciseName = title,
                durationMinutes = minutes,
                onBack = { navController.popBackStack() }
            )
        }
    }
}