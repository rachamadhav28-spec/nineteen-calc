package com.bro.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bro.app.ui.CalculatorScreen
import com.bro.app.ui.HistoryScreen
import com.bro.app.ui.theme.AppTheme

sealed class Screen {
    object Calculator : Screen()
    object History : Screen()
}

class MainActivity : ComponentActivity() {
    private val viewModel: CalculatorViewModel by viewModels()

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Calculator) }
                AnimatedContent(targetState = currentScreen) { target ->
                    when (target) {
                        is Screen.Calculator -> CalculatorScreen(
                            viewModel = viewModel,
                            onNavigateToHistory = { currentScreen = Screen.History }
                        )
                        is Screen.History -> HistoryScreen(
                            viewModel = viewModel,
                            onBack = { currentScreen = Screen.Calculator }
                        )
                    }
                }
            }
        }
    }
}
