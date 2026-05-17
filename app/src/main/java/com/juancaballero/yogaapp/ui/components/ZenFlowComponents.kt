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

/** 1. CAMPO DE TEXTO (Login/Registro) **/
@Composable
fun ZenFlowTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = label, color = Color.Gray) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(2.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true
    )
}

/** 2. TARJETA DE EJERCICIO (Home) **/
@Composable
fun WorkoutCard(
    title: String,
    duration: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = ZenFlowOrange
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                Text(text = duration, color = Color.Gray, fontSize = 14.sp)
            }
            // Corregido: Uso de AutoMirrored para evitar el warning
            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

/** 3. BARRA DE BÚSQUEDA **/
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

/** 4. WATER TRACKER **/
@Composable
fun WaterTrackerWidget() {
    var count by remember { mutableIntStateOf(0) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Opacity, contentDescription = null, tint = Color(0xFF2196F3), modifier = Modifier.size(30.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Daily Hydration", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("$count / 8 glasses today", fontSize = 12.sp, color = Color.Gray)
            }
            IconButton(onClick = { if (count < 8) count++ }) {
                Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color(0xFF2196F3), modifier = Modifier.size(32.dp))
            }
        }
    }
}

/** 5. BANNER DE CITA DIARIA **/
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

/** 6. BARRA DE NIVEL / XP **/
@Composable
fun ZenLevelBar(level: Int, xp: Int, totalXp: Int) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Yogui Level $level", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = ZenFlowOrange)
            Text("$xp / $totalXp XP", color = Color.Gray, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { xp.toFloat() / totalXp.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(CircleShape),
            color = ZenFlowOrange,
            trackColor = Color.LightGray.copy(alpha = 0.2f)
        )
    }
}

/** 7. BARRA DE NAVEGACIÓN INFERIOR (Añadida para corregir errores de x0, x1) **/
@Composable
fun ZenFlowBottomBar(
    currentRoute: String,
    onHomeClick: () -> Unit,
    onDiscoverClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Home") },
            selected = currentRoute == "home",
            onClick = onHomeClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ZenFlowOrange,
                selectedTextColor = ZenFlowOrange,
                indicatorColor = Color.Transparent
            )
        )
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