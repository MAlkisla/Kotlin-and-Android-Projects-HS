package org.hyperskill.calculator

import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import org.hyperskill.calculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var input: String = ""
    private var arg1: Double? = null
    private var operation: Char? = null
    private var savedArg: Double? = null
    private var savedOperation: Char? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.displayEditText.inputType = InputType.TYPE_NULL

        binding.button0.setOnClickListener { addChar('0') }
        binding.button1.setOnClickListener { addChar('1') }
        binding.button2.setOnClickListener { addChar('2') }
        binding.button3.setOnClickListener { addChar('3') }
        binding.button4.setOnClickListener { addChar('4') }
        binding.button5.setOnClickListener { addChar('5') }
        binding.button6.setOnClickListener { addChar('6') }
        binding.button7.setOnClickListener { addChar('7') }
        binding.button8.setOnClickListener { addChar('8') }
        binding.button9.setOnClickListener { addChar('9') }
        binding.dotButton.setOnClickListener { addChar('.') }
        binding.subtractButton.setOnClickListener {
            if ((arg1 == null || operation != null) && input.isEmpty()) addChar('-')
            else addOperation('-')
        }
        binding.addButton.setOnClickListener { addOperation('+') }
        binding.divideButton.setOnClickListener { addOperation('/') }
        binding.multiplyButton.setOnClickListener { addOperation('*') }
        binding.equalButton.setOnClickListener { calculateEqual() }
        binding.clearButton.setOnClickListener { clear() }
    }

    private fun clear() {
        arg1 = null
        operation = null
        savedOperation = null
        savedArg = null
        binding.displayEditText.hint = "0"
        setText("")
    }

    private fun calculateEqual() {
        if (input.isEmpty()) {
            if (operation == null) {
                if (savedOperation != null && savedArg != null) {
                    operation = savedOperation
                    arg1 = calculate(savedArg ?: 0.0)
                }
            } else {
                savedArg = arg1
                savedOperation = operation
                arg1 = calculate(savedArg ?: 0.0)
            }
        } else if (operation != null) {
            if (savedArg == null) {
                savedArg = input.toDouble()
                savedOperation = operation
                arg1 = calculate(savedArg ?: 0.0)
            } else {
                val arg2 = input.toDouble()
                arg1 = calculate(arg2)
                savedArg = arg2
            }
        } else arg1 = input.toDouble()

        setText("")
        binding.displayEditText.hint = showResult(arg1 ?: 0.0)
        operation = null
    }

    private fun addOperation(c: Char) {
        savedOperation = null
        savedArg = null
        val arg = try {
            (if (input.isEmpty()) binding.displayEditText.hint.toString()
            else input).toDouble()
        } catch (e: Exception) {
            0.0
        }
        if (operation != null) {
            arg1 = calculate(arg)
        } else arg1 = arg
        setText("")
        binding.displayEditText.hint = showResult(arg1 ?: 0.0)
        operation = c
    }

    private fun calculate(arg: Double): Double {
        val result = when (operation) {
            '-' -> arg1!! - arg
            '+' -> arg1!! + arg
            '*' -> arg1!! * arg
            '/' -> arg1!! / arg
            else -> 0.0
        }
        operation = null
        return result
    }

    private fun showResult(arg: Double): String {
        val result = arg.toString()
        return if (result.endsWith(".0")) result.dropLast(2)
        else result
    }

    private fun addChar(char: Char) {
        if (char == '.') {
            if (input.isEmpty()) input = "0."
            else if (input.equals("-")) input = "-0."
            else if (!input.contains(char)) input += char
        } else if (input.equals("0")) input = char.toString()
        else if (input.equals("-0")) input = "-" + char
        else input += char

        setText(input)
    }

    private fun setText(str: String) {
        input = str
        binding.displayEditText.setText(input)
    }
}
