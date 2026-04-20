package com.task.testtask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.task.navigation.RootNavHost
import com.task.core.presentation.ui.theme.TestTaskTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestTaskTheme {
                UpdateSystemBars(isSystemInDarkTheme())
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                ) { paddingValues ->

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        RootNavHost()
                    }
                }
            }
        }
    }


    @Composable
    fun UpdateSystemBars(
        darkMode: Boolean,
    ) {
        val activity = (LocalActivity.current as ComponentActivity)
        DisposableEffect(darkMode) {
            val barStyle = when (darkMode) {
                true -> SystemBarStyle.dark(Color.Transparent.toArgb())
                false -> SystemBarStyle.light(
                    Color.Transparent.toArgb(),
                    Color.Transparent.toArgb()
                )
            }
            activity.enableEdgeToEdge(
                statusBarStyle = barStyle,
                navigationBarStyle = barStyle
            )
            onDispose { }
        }
    }
}
