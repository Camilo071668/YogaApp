package com.juancaballero.yogaapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.juancaballero.yogaapp.ui.theme.ZenFlowOrange

/**
 * 1. ZenFlowTextField: Componente personalizado para la entrada de texto.
 * Se utiliza en Login y Registro para estandarizar el diseño.
 */
@Composable
fun ZenFlowTextField(
    value: String,                   // El texto actual almacenado en el estado.
    onValueChange: (String) -> Unit, // Callback para actualizar el estado hacia arriba (State Hoisting).
    label: String,                   // Texto de ayuda o placeholder.
    isPassword: Boolean = false      // Booleano para activar la máscara de caracteres ocultos.
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = label, color = Color.Gray) },
        modifier = Modifier
            .fillMaxWidth()           // Ocupa el ancho máximo disponible.
            .padding(vertical = 8.dp) // Separación vertical externa.
            .shadow(2.dp, RoundedCornerShape(20.dp)), // Elevación visual suave.
        shape = RoundedCornerShape(20.dp), // Bordes redondeados definidos en el diseño.
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,   // Color de fondo cuando está activo.
            unfocusedContainerColor = Color.White, // Color de fondo cuando está inactivo.
            disabledContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,  // Oculta la línea inferior de Material Design.
            unfocusedIndicatorColor = Color.Transparent,
        ),
        // Lógica de transformación: si es password, oculta los caracteres con puntos.
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true // Evita que el campo crezca verticalmente si el usuario presiona Enter.
    )
}

/**
 * 2. WorkoutCard: Representa un ítem de ejercicio en la lista principal.
 */
@Composable
fun WorkoutCard(
    title: String,      // Título de la rutina.
    duration: String,   // Duración (ej: "5 min").
    iconRes: Int,       // ID del recurso gráfico (Drawable).
    onClick: () -> Unit // Evento de navegación al ser presionado.
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp))
            .clickable { onClick() }, // Hace que toda la superficie sea interactiva.
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically // Centra el icono y el texto verticalmente.
        ) {
            // Muestra el icono de la rutina pintado con el color corporativo.
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = ZenFlowOrange
            )
            Spacer(modifier = Modifier.width(16.dp)) // Espaciador horizontal entre elementos.
            Column(modifier = Modifier.weight(1f)) { // Weight permite que la columna tome el espacio restante.
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                Text(text = duration, color = Color.Gray, fontSize = 14.sp)
            }
            // Icono de navegación AutoMirrored para soporte de idiomas derecha-a-izquierda.
            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

/**
 * 3. ZenSearchBar: Barra de búsqueda dinámica.
 */
@Composable
fun ZenSearchBar(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Search routine...", color = Color.Gray) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ZenFlowOrange) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = ZenFlowOrange
        )
    )
}

/**
 * 4. WaterTrackerWidget: Componente de gamificación para registro de hidratación.
 */
@Composable
fun WaterTrackerWidget() {
    // remember conserva el valor del estado aunque la pantalla se reconfigure.
    var count by remember { mutableIntStateOf(0) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)), // Color azul pastel.
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Opacity, contentDescription = null, tint = Color(0xFF2196F3), modifier = Modifier.size(30.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Daily Hydration", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("$count / 8 glasses today", fontSize = 12.sp, color = Color.Gray)
            }
            // Botón incremental: suma 1 al contador si es menor a 8.
            IconButton(onClick = { if (count < 8) count++ }) {
                Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color(0xFF2196F3), modifier = Modifier.size(32.dp))
            }
        }
    }
}

/**
 * 5. DailyZenQuote: Banner motivacional con frases inspiradoras.
 */
@Composable
fun DailyZenQuote(quote: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.FormatQuote, contentDescription = null, tint = ZenFlowOrange)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = quote, fontSize = 14.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, color = Color.DarkGray)
        }
    }
}

/**
 * 6. ZenLevelBar: Visualización de experiencia (XP) del usuario.
 */
@Composable
fun ZenLevelBar(level: Int, xp: Int, totalXp: Int) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Yogui Level $level", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = ZenFlowOrange)
            Text("$xp / $totalXp XP", color = Color.Gray, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Indicador de progreso lineal: calcula el porcentaje dividiendo XP actual entre total.
        LinearProgressIndicator(
            progress = { xp.toFloat() / totalXp.toFloat() },
            modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
            color = ZenFlowOrange,
            trackColor = Color.LightGray.copy(alpha = 0.2f)
        )
    }
}

/**
 * 7. ZenFlowBottomBar: Componente global de navegación inferior.
 */
@Composable
fun ZenFlowBottomBar(
    currentRoute: String,     // Identifica en qué pantalla está el usuario para resaltar el icono.
    onHomeClick: () -> Unit,
    onDiscoverClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        // ÍTEM HOME
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Home") },
            selected = currentRoute == "home", // Se activa si la ruta coincide.
            onClick = onHomeClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ZenFlowOrange,
                selectedTextColor = ZenFlowOrange,
                indicatorColor = Color.Transparent
            )
        )
        // ÍTEM DISCOVER
        NavigationBarItem(
            icon = { Icon(Icons.Default.Explore, null) },
            label = { Text("Discover") },
            selected = currentRoute == "discover",
            onClick = onDiscoverClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ZenFlowOrange,
                selectedTextColor = ZenFlowOrange,
                indicatorColor = Color.Transparent
            )
        )
        // ÍTEM PROFILE
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, null) },
            label = { Text("Profile") },
            selected = currentRoute == "profile",
            onClick = onProfileClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ZenFlowOrange,
                selectedTextColor = ZenFlowOrange,
                indicatorColor = Color.Transparent
            )
        )
    }
}