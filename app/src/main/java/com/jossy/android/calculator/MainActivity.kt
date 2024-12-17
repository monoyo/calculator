package com.jossy.android.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var display: TextView

    private var inputValue: String = ""
    private var operator: String = ""
    private var operand1: Double? = null
    private var resultDisplayed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        display = findViewById(R.id.resultDisplay)

        val buttonClear: Button = findViewById(R.id.buttonClear)
        val buttonEquals: Button = findViewById(R.id.buttonEquals)
        val buttonRoot: Button = findViewById(R.id.buttonRoot)
        val buttonPercent: Button = findViewById(R.id.buttonPercent)
        val buttonDot: Button = findViewById(R.id.buttonDot)

        val buttons = listOf(
            R.id.button0 to "0",
            R.id.button1 to "1",
            R.id.button2 to "2",
            R.id.button3 to "3",
            R.id.button4 to "4",
            R.id.button5 to "5",
            R.id.button6 to "6",
            R.id.button7 to "7",
            R.id.button8 to "8",
            R.id.button9 to "9",
            R.id.buttonAdd to "+",
            R.id.buttonSubtract to "-",
            R.id.buttonMultiply to "*",
            R.id.buttonDivide to "/"
        )

        buttons.forEach { (id, value) ->
            findViewById<Button>(id).setOnClickListener { handleInput(value) }
        }
        buttonClear.setOnClickListener { backspace() }
        buttonEquals.setOnClickListener { calculateResult() }
        buttonRoot.setOnClickListener { calculateRoot() }
        buttonPercent.setOnClickListener { calculatePercent() }
        buttonDot.setOnClickListener { handleInput(".") }
        buttonClear.setOnLongClickListener {
            clear()
            true
        }
    }

    private fun handleInput(value: String) {
        if (value in listOf("+", "-", "*", "/")) {
            if (resultDisplayed) {
                operator = value
                operand1 = display.text.toString().toDoubleOrNull()
                inputValue = ""
                resultDisplayed = false
            } else if (operator.isEmpty() && inputValue.isNotEmpty()) {
                operator = value
                operand1 = inputValue.toDoubleOrNull()
                inputValue = ""
            } else if (operand1 != null && inputValue.isNotEmpty()) {
                calculateResult()
                operator = value
                operand1 = display.text.toString().toDoubleOrNull()
                inputValue = ""
            }
        } else {
            if (resultDisplayed) {
                inputValue = value
                resultDisplayed = false
            } else {
                inputValue += value
                inputValue = inputValue.trimLeadingZeros()
            }
        }
        updateDisplay()
    }

    private fun calculateResult() {
        val operand2 = inputValue.toDoubleOrNull()
        if (operand1 != null && operand2 != null && operator.isNotEmpty()) {
            val result = when (operator) {
                "+" -> operand1!! + operand2
                "-" -> operand1!! - operand2
                "*" -> operand1!! * operand2
                "/" -> if (operand2 != 0.0) operand1!! / operand2 else "Error"
                else -> ""
            }
            display.text = formatResult(result)
            operand1 = null
            operator = ""
            inputValue = ""
            resultDisplayed = true
        }
    }

    private fun calculateRoot() {
        if (inputValue.isNotEmpty() || resultDisplayed) {
            val value = (if (resultDisplayed) display.text.toString() else inputValue).toDoubleOrNull()
            if (value != null && value >= 0) {
                val result = kotlin.math.sqrt(value)
                display.text = formatResult(result)
                operand1 = null
                operator = ""
                inputValue = ""
                resultDisplayed = true
            } else {
                display.text = "Error"
            }
        }
    }

    private fun calculatePercent() {
        if (inputValue.isNotEmpty() || resultDisplayed) {
            val value = (if (resultDisplayed) display.text.toString() else inputValue).toDoubleOrNull()
            if (value != null) {
                val result = value / 100
                display.text = formatResult(result)
                operand1 = null
                operator = ""
                inputValue = ""
                resultDisplayed = true
            }
        }
    }

    private fun backspace() {
        if (inputValue.isNotEmpty()) {
            inputValue = inputValue.dropLast(1)
        } else if (operator.isNotEmpty()) {
            operator = ""
        } else if (operand1 != null) {
            operand1 = null
        }
        updateDisplay()
    }

    private fun clear() {
        operand1 = null
        operator = ""
        inputValue = ""
        display.text = "0"
        resultDisplayed = false
    }

    private fun updateDisplay() {
        val formattedOperand1 = operand1?.let { formatResult(it) } ?: "0"
        display.text = inputValue.ifEmpty { formattedOperand1 }
    }

    private fun formatResult(value: Any): String {
        return if (value is Double && value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else {
            value.toString()
        }
    }

    private fun String.trimLeadingZeros(): String {
        return if (this.contains(".")) {
            this.replaceFirst("^0+(?=[1-9]|0\\.)".toRegex(), "")
        } else {
            this.replaceFirst("^0+(?=[1-9])".toRegex(), "")
        }
    }
}
