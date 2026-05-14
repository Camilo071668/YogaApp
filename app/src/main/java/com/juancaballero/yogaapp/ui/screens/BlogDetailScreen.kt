package com.juancaballero.yogaapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.juancaballero.yogaapp.ui.theme.ZenFlowBg
import com.juancaballero.yogaapp.ui.theme.ZenFlowOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogDetailScreen(title: String, onBack: () -> Unit) {

    // Contenido simulado dinámico (dependiendo de qué artículo toques)
    val articleContent = when (title) {
        "5 Benefits of Morning Yoga" -> "Waking up early and stepping onto your mat can completely transform your day. Morning yoga helps awaken your body, boosts your metabolism, and clears your mind before the daily chaos begins.\n\n1. Improves Flexibility\n2. Boosts Energy Levels\n3. Reduces Stress\n4. Enhances Focus\n5. Promotes Better Posture\n\nTry starting with just 5 minutes a day!"
        "How to breathe correctly" -> "Breathing is the core of Yoga (Pranayama). Most of us take shallow breaths into our chest. To breathe correctly, place a hand on your belly. As you inhale, feel your belly rise like a balloon. As you exhale, feel it fall. This deep diaphragmatic breathing signals your nervous system to relax and reduces anxiety instantly."
        "Yoga for back pain relief" -> "Sitting at a desk all day compresses the spine. Gentle yoga poses can decompress your vertebrae and stretch tight back muscles.\n\nPoses like Cat-Cow, Child’s Pose, and Downward Dog are excellent for relieving lower back tension. Remember to move slowly and never push into sharp pain. Healing takes time and consistency."
        else -> "Discover the amazing benefits of integrating this practice into your daily life. Taking a moment for yourself is the ultimate form of self-care. Breathe deeply, stay consistent, and watch your mind and body transform."
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { }, // Título vacío para un look más limpio
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = { /* Lógica de compartir en el futuro */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.Gray)
                    }
                    IconButton(onClick = { /* Lógica de guardar en favoritos */ }) {
                        Icon(Icons.Default.BookmarkBorder, contentDescription = "Save", tint = Color.Gray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ZenFlowBg)
            )
        },
        containerColor = ZenFlowBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Imagen/Banner simulado (Premium)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFFFF0EB)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ZenFlowOrange, modifier = Modifier.size(60.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Textos
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "LIFESTYLE & HEALTH",
                    color = ZenFlowOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 34.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Autor Simulado
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).background(Color.LightGray, CircleShape), contentAlignment = Alignment.Center) {
                        Text("Z", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("ZenFlow Editorial", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        Text("Published Today • 5 min read", color = Color.Gray, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Contenido principal del blog
                Text(
                    text = articleContent,
                    fontSize = 16.sp,
                    color = Color.DarkGray,
                    lineHeight = 26.sp
                )

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}