package com.example.preparcial3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.preparcial3.ui.PostApp
import com.example.preparcial3.ui.theme.Preparcial3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Preparcial3Theme {
                PostApp()
            }
        }
    }
}
