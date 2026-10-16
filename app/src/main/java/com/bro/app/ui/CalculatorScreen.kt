package com.bro.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bro.app.CalculatorViewModel

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    onNavigateToHistory: () -> Unit
) {
    val expression by viewModel.expression
    val result by viewModel.result

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = expression,
            onValueChange = { /* read‑only */ },
            label = { Text("Expression") },
            readOnly = true,
            modifier = Modifier.fillMaxSize()
        )
        Text(text = "Result: $result", modifier = Modifier.padding(top = 8.dp))
        Button(onClick = { viewModel.onEquals() }, modifier = Modifier.padding(top = 8.dp)) {
            Text("=")
        }
        Button(onClick = onNavigateToHistory, modifier = Modifier.padding(top = 8.dp)) {
            Text("History")
        }
    }
}
