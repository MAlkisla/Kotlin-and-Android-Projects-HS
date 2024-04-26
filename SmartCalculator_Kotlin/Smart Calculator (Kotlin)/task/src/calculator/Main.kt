package calculator

import java.math.BigInteger

class Calculator {

    private var mapList: MutableMap<String, String> = mutableMapOf()
    private val aZRegex = "[a-zA-Z]+".toRegex()
    private val digRegex = "-?\\d+".toRegex()

    fun runCalculator() {
        println("Enter expressions containing numbers, '+', '-', '*', '/', '=' operators or type '/help' for assistance or '/exit' to quit:")
        while (true) {
            when (val input = readLine()?.trim()) {
                "/exit" -> {
                    println("Bye!")
                    break
                }
                "/help" -> println("The calculator supports addition (+), subtraction (-), multiplication (*), division (/) operations, and variable assignments.")
                "" -> continue
                else -> processInput(input ?: "")
            }
        }
    }

    private fun processInput(input: String) {
        if ("^/.*".toRegex().matches(input)) errorMessage(5)
        else checkExpression(trimPlus(input))
    }

    private fun trimPlus(string: String) = string
        .replace(Regex("\\s{2,}"), " ")
        .replace(Regex("--"), "+")
        .replace(Regex("[+]{2,}"), "+")
        .replace(Regex("\\+- "), " - ")
        .replace(" + -", " - ")

    private fun addSpace(string: String) = string
        .replace(Regex("\\d+"), "\$0 ")
        .replace(Regex("[-+*/)(]"), "\$0 ")
        .replace(Regex("[a-zA-Z]+"), "\$0 ")
        .replace("  ", " ")

    private fun checkExpression(input: String) {
        var stop = false
        if (".*[)(].*".toRegex().matches(input)) stop = countBracket(input)
        if (!stop) {
            if(".*=.*".toRegex().matches(input)) varEqualsVal(input)
            else if ("^-?\\d+$".toRegex().matches(input)) println(input)
            else if (".*[-+*/].*".toRegex().matches(input)) getValueOfVar(input)
            else if (input !in mapList.keys) errorMessage(3)
            else println(mapList[input])
        }
    }

    private fun countBracket(input: String): Boolean {
        var stop = false
        if (input.split("(").lastIndex != input.split(")").lastIndex) {
            stop = true
            errorMessage(4)
        }
        return stop
    }

    private fun varEqualsVal(input: String) {
        val (variable, value) = input.split("=")
        if(!aZRegex.matches(variable.trim())) errorMessage(1)
        else if(!aZRegex.matches(value.trim()) && !digRegex.matches(value.trim())) errorMessage(2)
        else if (mapList.containsKey(value.trim())) mapList[variable.trim()] = mapList[value.trim()].toString()
        else if (digRegex.matches(value.trim())) mapList[variable.trim()] = value.trim()
        else  errorMessage(3)
    }

    private fun getValueOfVar(input: String) {
        var setFlag = listOf<Boolean>()
        var newStr = ""
        input.split(" ").toSet().map {
            if (aZRegex.matches(it)) setFlag = listOf(it in mapList.keys)
        }
        if (false !in setFlag) {
            input.split(" ").map {
                newStr += if (aZRegex.matches(it))  "${mapList[it]} " else "$it "
            }
            calculateExpression(newStr)
        } else errorMessage(3)
    }

    private fun calculateExpression(input: String) {
        try {
            val spacedString = trimPlus(input)
            val postfix = Postfix()
            postfix.parseExpression(addSpace(spacedString))
        } catch (e: Exception) {
            errorMessage(4)
        }
    }

    private fun errorMessage(num: Int) {
        when(num) {
            1 -> println("Invalid identifier")
            2 -> println("Invalid assignment")
            3 -> println("Unknown variable")
            4 -> println("Invalid expression")
            5 -> println("Unknown command")
        }
    }
}

class Postfix  {

    private var stack = mutableListOf<String>()
    private var queue = mutableListOf<String>()
    private var expressionList = mutableListOf<String>()

    fun parseExpression(string: String) {
        expressionList = string.split(" ").toMutableList()
        getPostFixEx()
    }

    private fun getPostFixEx() {
        expressionList.forEach {
            when {
                it == "(" -> push(it)
                it == ")" -> if (expressionList.contains("(")) pop()
                it == "^" -> push(it)
                Regex("-?\\d+").containsMatchIn(it) -> addQueue(it)
                Regex("[+-]").containsMatchIn(it) ->
                    if (stack.isEmpty() || stack.last() == "(") push(it)
                    else if (stack.last().contains(Regex("[/*]"))) {
                        pop()
                        push(it)
                    } else {
                        addQueue(stack.last())
                        stack[stack.lastIndex] = it
                    }
                Regex("[*/]").containsMatchIn(it) -> {
                    if (stack.isNotEmpty() && (stack.last() == "*" || stack.last() == "/")) pop()
                    push(it)
                }
            }
        }
        if (stack.isNotEmpty()) {
            for (i in stack.lastIndex downTo 0) {
                if (stack[i] != "(") addQueue(stack[i])
            }
        }
        calcPostFix()
    }

    private fun pop() {
        for (i in stack.lastIndex downTo 0) {
            if (stack[i] == "(") {
                stack[i] = " "
                break
            }
            addQueue(stack[i])
            stack[i] = " "
        }
        stack.removeIf { it == " " }
    }

    private fun addQueue(item: String) {
        queue.add(item)
    }

    private fun push(item: String) {
        stack.add(item)
    }

    private fun calcPostFix() {
        val stack = mutableListOf<BigInteger>()
        for (item in queue) {
            if (Regex("-?\\d+").matches(item)) stack.add(item.toBigInteger())
            when (item) {
                "+" -> {
                    stack[stack.lastIndex - 1] = stack[stack.lastIndex - 1] + stack.last()
                    stack.removeAt(stack.lastIndex)
                }
                "*" -> {
                    stack[stack.lastIndex - 1] = stack[stack.lastIndex - 1] * stack.last()
                    stack.removeAt(stack.lastIndex)
                }
                "/" -> {
                    stack[stack.lastIndex - 1] = stack[stack.lastIndex - 1] / stack.last()
                    stack.removeAt(stack.lastIndex)
                }
                "-" -> {
                    stack[stack.lastIndex - 1] = stack[stack.lastIndex - 1] - stack.last()
                    stack.removeAt(stack.lastIndex)
                }
            }
        }
        println(stack.first())
    }
}

fun main() {
    Calculator().runCalculator()
}
