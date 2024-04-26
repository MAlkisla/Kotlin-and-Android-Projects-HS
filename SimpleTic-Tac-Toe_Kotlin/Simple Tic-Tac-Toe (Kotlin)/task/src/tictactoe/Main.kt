package tictactoe

fun printGrid(gameState: String) {
    println("---------")
    for (i in 0 until 3) {
        print("| ")
        for (j in 0 until 3) {
            print("${gameState[i * 3 + j]} ")
        }
        println("|")
    }
    println("---------")
}

fun checkWinner(gameState: String): Char? {
    val lines = listOf(
            listOf(0, 1, 2),
            listOf(3, 4, 5),
            listOf(6, 7, 8),
            listOf(0, 3, 6),
            listOf(1, 4, 7),
            listOf(2, 5, 8),
            listOf(0, 4, 8),
            listOf(2, 4, 6)
    )

    for (line in lines) {
        val symbols = line.map { gameState[it] }
        if (symbols.all { it == 'X' }) return 'X'
        if (symbols.all { it == 'O' }) return 'O'
    }

    return null
}

fun main() {
    var gameState = "_________"
    var currentPlayer = 'X'

    printGrid(gameState)

    var gameFinished = false
    while (!gameFinished) {
        var validInput = false
        while (!validInput) {
            print("Enter the coordinates: ")
            val coordinates = readLine()!!.trim().split(" ")
            if (coordinates.size != 2 || !coordinates.all { it.all { c -> c.isDigit() } }) {
                println("You should enter numbers!")
                continue
            }
            val row = coordinates[0].toInt() - 1
            val col = coordinates[1].toInt() - 1
            if (row !in 0..2 || col !in 0..2) {
                println("Coordinates should be from 1 to 3!")
                continue
            }
            if (gameState[row * 3 + col] != '_') {
                println("This cell is occupied! Choose another one!")
                continue
            }
            gameState = gameState.substring(0, row * 3 + col) + currentPlayer + gameState.substring(row * 3 + col + 1)
            validInput = true
        }

        printGrid(gameState)

        val winner = checkWinner(gameState)
        if (winner != null) {
            println("$winner wins")
            gameFinished = true
        } else if (!gameState.contains('_')) {
            println("Draw")
            gameFinished = true
        } else {
            currentPlayer = if (currentPlayer == 'X') 'O' else 'X'
        }
    }
}
