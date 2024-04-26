package bullscows

import java.util.Random

const val SYMBOLS = "0123456789abcdefghijklmnopqrstuvxywz"

fun main() {
    println("Input the length of the secret code:")
    val inputLength = readln()

    val length = inputLength.toIntOrNull()

    if (length == null || length < 1) {
        print("Error: \"$inputLength\" isn't a valid number.\n")
        return
    }

    println("Input the number of possible symbols in the code:")
    val symbolsLength = readln().toInt()

    if (length > symbolsLength){
        print("Error: it's not possible to generate a code with a length of $length with $symbolsLength unique symbols.")
        return
    }

    if (symbolsLength > SYMBOLS.length) {
        println("Error: maximum number of possible symbols in the code is ${SYMBOLS.length} ${getSymbolsRange()}.")
        return
    }

    val secret = getRandomSecret(length, symbolsLength)
    val symbolsRange = getSymbolsRange(symbolsLength)

    println("The secret is prepared: ${"*".repeat(length)} $symbolsRange.")

    println("Okay, let's start a game!")

    var turn = 0

    do {
        turn++

        println("Turn $turn:")
        val guess = readln()

        var bulls = 0
        var cows = 0

        for (i in secret.indices) {
            if (secret[i] == guess[i]) {
                bulls++
            } else if (guess[i] in secret) {
                cows++
            }
        }

        print("Grade: ")

        println(
            when {
                bulls > 0 && cows > 0 -> "$bulls bull(s) and $cows cow(s). "
                bulls > 0 -> "$bulls bull(s). "
                cows > 0 -> "$cows cow(s). "
                else -> "None. "
            }
        )
    } while (secret != guess)

    println("Congratulations! You guessed the secret code.")
}

fun getSymbolsRange(symbolsLength: Int = SYMBOLS.length): String {
    return when {
        symbolsLength <= 10 -> "(0-${SYMBOLS[symbolsLength - 1]})"
        else -> "(0-9, a-${SYMBOLS[symbolsLength - 1]})"
    }
}

fun getRandomSecret(length: Int, symbolsLength: Int): String {
    val random = Random()

    var secret = ""

    while (secret.length < length) {
        val number = random.nextInt(symbolsLength)
        val symbol = SYMBOLS[number]

        if (symbol !in secret) {
            secret += symbol
        }
    }

    return secret
}