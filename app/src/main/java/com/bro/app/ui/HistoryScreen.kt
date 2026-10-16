package com.bro.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bro.app.CalculatorViewModel
import androidx.compose.runtime.collectAsStateWithLifecycle

@Composable
fun HistoryScreen(
    viewModel: CalculatorViewModel,
    onBack: () -> Unit
) {
    val history by viewModel.history.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        history.forEach { item ->
            Text(text = "${item.expression} = ${item.result}")
        }
        Button(onClick = onBack, modifier = Modifier.padding(top = 8.dp)) {
            Text("Back")
        }
    }
}
