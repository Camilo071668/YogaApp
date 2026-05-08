package com.juancaballero.yogaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.juancaballero.yogaapp.ui.ZenFlowNavGraph
import com.juancaballero.yogaapp.ui.theme.YogaAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Llamamos a nuestro NavGraph dentro del Theme
            YogaAppTheme() {
                ZenFlowNavGraph()
            }
        }
    }
}