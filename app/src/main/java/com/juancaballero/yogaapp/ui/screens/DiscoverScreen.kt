package com.juancaballero.yogaapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.juancaballero.yogaapp.ui.theme.ZenFlowBg

@Composable
fun DiscoverScreen(
    onHomeClick: () -> Unit,
    onProfileClick: () -> Unit,
    onWorkoutClick: (String, String) -> Unit,
    onBlogClick: (String) -> Unit // Esta función la agregamos para que al tocar un blog nos lleve al detalle
) {
    // Acá armo la lista de categorías que vamos a mostrar.
    // Cada una tiene su título, tiempo, un icono y un color de fondo pastel para que se vea Zen.
    val categories = listOf(
        CategoryItem("Flexibility", "15 min", Icons.Default.AccessibilityNew, Color(0xFFFFE0B2)),
        CategoryItem("Meditation", "10 min", Icons.Default.SelfImprovement, Color(0xFFC8E6C9)),
        CategoryItem("Strength", "20 min", Icons.Default.FitnessCenter, Color(0xFFFFCDD2)),
        CategoryItem("Relaxation", "8 min", Icons.Default.Spa, Color(0xFFBBDEFB))
    )

    // Uso el Scaffold porque es la base de Material Design.
    // Me permite clavar la barrita de abajo (BottomBar) y que no se mueva.
    Scaffold(
        bottomBar = {
            ZenFlowBottomBar(
                currentRoute = "discover",
                onHomeClick = onHomeClick,
                onDiscoverClick = {}, // Vacío porque ya estamos acá
                onProfileClick = onProfileClick
            )
        },
        containerColor = ZenFlowBg // Color de fondo gris clarito del tema
    ) { innerPadding ->
        // Uso una Column con scroll porque la pantalla tiene bastante contenido y si no, no cabe en teléfonos chicos.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()) // Esto activa el scroll manual
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Títulos principales con diferentes grosores para jerarquía visual
            Text("Discover", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text("Find the perfect routine for today", fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))
            Text("Categories", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(16.dp))

            // Acá armé una cuadrícula manual usando Rows.
            // Uso weight(1f) en las tarjetas para que se repartan el ancho de la pantalla a la mitad (50/50).
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CategoryCard(cat = categories[0], modifier = Modifier.weight(1f)) { onWorkoutClick(it.title, it.duration) }
                CategoryCard(cat = categories[1], modifier = Modifier.weight(1f)) { onWorkoutClick(it.title, it.duration) }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                CategoryCard(cat = categories[2], modifier = Modifier.weight(1f)) { onWorkoutClick(it.title, it.duration) }
                CategoryCard(cat = categories[3], modifier = Modifier.weight(1f)) { onWorkoutClick(it.title, it.duration) }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Read & Learn (Blogs)", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(16.dp))

            // Sección de artículos. Cuando clickeas, le pasamos el título al NavGraph para que sepa qué artículo abrir.
            BlogCard(title = "5 Benefits of Morning Yoga", readTime = "3 min read", icon = Icons.Default.WbTwilight) {
                onBlogClick("5 Benefits of Morning Yoga")
            }
            BlogCard(title = "How to breathe correctly", readTime = "5 min read", icon = Icons.Default.Air) {
                onBlogClick("How to breathe correctly")
            }
            BlogCard(title = "Yoga for back pain relief", readTime = "4 min read", icon = Icons.Default.Healing) {
                onBlogClick("Yoga for back pain relief")
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * Este es el componente de las tarjetas de arriba.
 * Recibe un objeto CategoryItem y lo dibuja.
 */
@Composable
fun CategoryCard(cat: CategoryItem, modifier: Modifier = Modifier, onClick: (CategoryItem) -> Unit) {
    Card(
        modifier = modifier
            .height(140.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clickable { onClick(cat) }, // Hacer que toda la caja reaccione al toque
        colors = CardDefaults.cardColors(containerColor = cat.color),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Icon(cat.icon, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(32.dp))
            Column {
                Text(cat.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray)
                Text(cat.duration, color = Color.DarkGray, fontSize = 12.sp)
            }
        }
    }
}

/**
 * Las tarjetas de los blogs.
 * Son más chatas y alargadas para diferenciarlas de las categorías.
 */
@Composable
fun BlogCard(title: String, readTime: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            // Un cuadradito gris de fondo para el icono para que resalte
            Box(modifier = Modifier.size(60.dp).background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = Color(0xFFFF8A65), modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(readTime, color = Color.Gray, fontSize = 12.sp)
            }
            // Flechita para indicar que se puede entrar al artículo
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

// Modelo de datos simple para no repetir código arriba.
data class CategoryItem(val title: String, val duration: String, val icon: ImageVector, val color: Color)