package com.bro.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    onNavigateToHistory: () -> Unit
) {
    val expression by viewModel.expression
    val result by viewModel.result
    var shake by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Background gradient animation (simple color cycle)
    val gradientColors = listOf(
        Color(0xFF1E1E2C),
        Color(0xFF2E2E3C),
        Color(0xFF1E1E2C)
    )
    val brush = Brush.verticalGradient(gradientColors)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Expression display with pop‑in animation
            val exprScale = remember { Animatable(1f) }
            LaunchedEffect(expression) {
                exprScale.snapTo(0.8f)
                exprScale.animateTo(1f, animationSpec = tween(200))
            }
            Text(
                text = expression.ifBlank { "0" },
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .align(Alignment.End)
                    .scale(exprScale.value)
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Result with slide‑up and glow when updated
            AnimatedVisibility(
                visible = result.isNotBlank(),
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
            ) {
                val glow = remember { Animatable(0f) }
                LaunchedEffect(result) {
                    glow.snapTo(0f)
                    glow.animateTo(1f, animationSpec = tween(500))
                }
                Text(
                    text = result,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f + 0.3f * glow.value),
                    modifier = Modifier.align(Alignment.End)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            // Buttons grid
            val buttonRows = listOf(
                listOf("C", "⌫", "%", "/"),
                listOf("7", "8", "9", "*"),
                listOf("4", "5", "6", "-"),
                listOf("1", "2", "3", "+"),
                listOf("0", ".", "=", "H")
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(buttonRows.flatten()) { label ->
                    CalculatorButton(
                        label = label,
                        onClick = {
                            when (label) {
                                "C" -> viewModel.onClear()
                                "⌫" -> viewModel.onBackspace()
                                "=" -> {
                                    viewModel.onEquals()
                                    if (viewModel.result.value == "Error") {
                                        shake = true
                                        coroutineScope.launch {
                                            // reset shake after animation
                                            kotlinx.coroutines.delay(500)
                                            shake = false
                                        }
                                    }
                                }
                                "H" -> onNavigateToHistory()
                                "+", "-", "*", "/", "%" -> viewModel.onOperator(label.first())
                                else -> viewModel.onInput(label.first())
                            }
                        },
                        isPrimary = label == "=",
                        isClear = label == "C",
                        shake = shake && label == "="
                    )
                }
            }
        }
    }
}

@Composable
private fun CalculatorButton(
    label: String,
    onClick: () -> Unit,
    isPrimary: Boolean = false,
    isClear: Boolean = false,
    shake: Boolean = false
) {
    var pressed by remember { mutableStateOf(false) }
    val scale = if (pressed) 0.9f else 1f
    val rotation = if (shake) 10f else 0f
    val background = when {
        isPrimary -> MaterialTheme.colorScheme.primary
        isClear -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(72.dp)
            .scale(scale)
            .rotate(rotation)
            .background(background, shape = CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    pressed = true
                    onClick()
                    // bounce back after short delay
                    kotlinx.coroutines.GlobalScope.launch {
                        kotlinx.coroutines.delay(100)
                        pressed = false
                    }
                }
            )
    ) {
        Text(
            text = label,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
