package com.bro.app

object CalculatorLogic {
    /**
     * Very simple expression evaluator supporting + - * / % and decimal numbers.
     * It respects operator precedence (* / % before + -) and evaluates left‑to‑right.
     * Returns null if the expression cannot be parsed or division by zero occurs.
     */
    fun evaluate(expression: String): Double? {
        if (expression.isBlank()) return null
        // Tokenize numbers and operators
        val tokens = mutableListOf<String>()
        var current = StringBuilder()
        for (c in expression) {
            when {
                c.isDigit() || c == '.' -> current.append(c)
                c == '+' || c == '-' || c == '*' || c == '/' || c == '%' -> {
                    if (current.isNotEmpty()) {
                        tokens.add(current.toString())
                        current = StringBuilder()
                    }
                    tokens.add(c.toString())
                }
                else -> return null // invalid character
            }
        }
        if (current.isNotEmpty()) tokens.add(current.toString())
        // First pass: * / %
        val firstPass = mutableListOf<String>()
        var i = 0
        while (i < tokens.size) {
            val token = tokens[i]
            if (token == "*" || token == "/" || token == "%") {
                val left = firstPass.removeLast().toDoubleOrNull() ?: return null
                val right = tokens.getOrNull(i + 1)?.toDoubleOrNull() ?: return null
                val res = when (token) {
                    "*" -> left * right
                    "/" -> if (right == 0.0) return null else left / right
                    "%" -> left % right
                    else -> 0.0
                }
                firstPass.add(res.toString())
                i += 2
            } else {
                firstPass.add(token)
                i++
            }
        }
        // Second pass: + -
        var result = firstPass[0].toDoubleOrNull() ?: return null
        i = 1
        while (i < firstPass.size) {
            val op = firstPass[i]
            val next = firstPass.getOrNull(i + 1)?.toDoubleOrNull() ?: return null
            result = when (op) {
                "+" -> result + next
                "-" -> result - next
                else -> return null
            }
            i += 2
        }
        return result
    }
}
