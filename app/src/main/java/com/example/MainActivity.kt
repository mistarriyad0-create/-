package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.RongdhonuRepository
import com.example.model.ThemeMode
import com.example.ui.RongdhonuApp
import com.example.ui.theme.RongdhonuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by RongdhonuRepository.themeMode.collectAsStateWithLifecycle()
            val isDark = when (themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            RongdhonuTheme(darkTheme = isDark) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RongdhonuApp()
                }
            }
        }
    }
}
