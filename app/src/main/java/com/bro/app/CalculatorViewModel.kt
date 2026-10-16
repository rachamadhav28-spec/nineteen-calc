package com.bro.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant

class CalculatorViewModel : ViewModel() {
    private val _expression = mutableStateOf("")
    val expression: State<String> get() = _expression

    private val _result = mutableStateOf("")
    val result: State<String> get() = _result

    // Database initialization (singleton per process)
    private val db by lazy {
        Room.databaseBuilder(
            getApplicationContext(),
            AppDatabase::class.java,
            "calc_history.db"
        ).build()
    }
    private val dao = db.historyDao()

    val history: StateFlow<List<HistoryItem>> = dao.getAll()
        .map { list ->
            list.map { HistoryItem(it.expression, it.result, it.timestamp) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onInput(char: Char) {
        _expression.value += char
    }

    fun onOperator(op: Char) {
        if (_expression.value.isNotEmpty() && _expression.value.last().isDigit()) {
            _expression.value += op
        }
    }

    fun onClear() {
        _expression.value = ""
        _result.value = ""
    }

    fun onBackspace() {
        if (_expression.value.isNotEmpty()) {
            _expression.value = _expression.value.dropLast(1)
        }
    }

    fun onEquals() {
        val expr = _expression.value
        val eval = CalculatorLogic.evaluate(expr)
        if (eval == null) {
            // error – show shake via UI flag (handled in UI)
            _result.value = "Error"
        } else {
            val formatted = if (eval % 1.0 == 0.0) eval.toLong().toString() else eval.toString()
            _result.value = formatted
            // Save to DB
            viewModelScope.launch {
                dao.insert(
                    HistoryEntity(
                        expression = expr,
                        result = formatted,
                        timestamp = Instant.now().toEpochMilli()
                    )
                )
            }
        }
    }
}

data class HistoryItem(
    val expression: String,
    val result: String,
    val timestamp: Long
)
