package chess

import kotlin.math.abs

class Pawn(private val color: String = "", private var firstMove: Boolean = true) {
    override fun toString() = color

    fun isFirstMove() = firstMove

    fun moved() {
        firstMove = false
    }
}

class Chess {
    private val separator = "  +---+---+---+---+---+---+---+---+"
    private val files = mapOf("a" to 0, "b" to 1, "c" to 2, "d" to 3, "e" to 4, "f" to 5, "g" to 6, "h" to 7)
    private val blackPawns = mutableListOf<Pair<Int, Int>>()
    private val whitePawns = mutableListOf<Pair<Int, Int>>()
    private val illegalMoves = mutableListOf<String>()
    private var nextMove = ""

    private val chessBoard = MutableList(8) { i ->
        MutableList(8) {
            when (i) {
                1 -> Pawn("B")
                6 -> Pawn("W")
                else -> " "
            }
        }
    }

    private lateinit var player1: Triple<String, String, String>
    private lateinit var player2: Triple<String, String, String>
    private lateinit var currentPlayer: Triple<String, String, String>

    private val validMoves = Regex("""[a-h][1-8][a-h][1-8]""")
    private var exit = true

    init {
        for (j in chessBoard[1].indices) blackPawns.add(Pair(1, j))
        for (j in chessBoard[6].indices) whitePawns.add(Pair(6, j))
    }

    fun play() {
        println("Pawns-Only Chess")
        askPlayerName()
        showBoard()

        var input: String
        while (exit) {
            println("${currentPlayer.first}'s turn:")
            input = readln()
            if (input == "exit") break
            checkMove(input)
            checkEndGame()
        }
        println("Bye!")
    }

    private fun askPlayerName() {
        println("First Player's name:")
        player1 = Triple(readln(), "W", "white")
        println("Second Player's name:")
        player2 = Triple(readln(), "B", "black")
        currentPlayer = player1
    }

    private fun checkMove(move: String) {
        if (!validMoves.matches(move)) {
            println("Invalid Input")
            return
        }
        checkPositions(move)
    }

    private fun checkPositions(move: String) {
        val (startPosition, destinationPosition) = getPositions(move)
        val nbRanks = abs(startPosition.first - destinationPosition.first)
        val pos = chessBoard[startPosition.first][startPosition.second]
        val pawn = if (pos.toString() != " ") pos as Pawn else Pawn()

        when {
            pos.toString() != currentPlayer.second -> println("No ${currentPlayer.third} pawn at ${move.substring(0, 2)}")

            currentPlayer.second == "W" && destinationPosition.first > startPosition.first -> println("Invalid input")
            currentPlayer.second == "B" && destinationPosition.first < startPosition.first -> println("Invalid input")
            nbRanks > 2 || !pawn.isFirstMove() && nbRanks == 2 || illegalMoves.contains(move) -> println("Invalid input")

            abs(startPosition.second - destinationPosition.second) == 1 && nbRanks == 1 -> capture(
                startPosition,
                destinationPosition
            )

            startPosition.second != destinationPosition.second
                    || chessBoard[destinationPosition.first][destinationPosition.second].toString() != " " -> println("Invalid input")

            else -> {
                if (nextMove == move) nextMove = "" else illegalMoves.add(nextMove)
                makeMove(startPosition, destinationPosition)
                checkNeighbor(destinationPosition, move)
            }
        }
    }

    private fun getPositions(move: String): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        val startPosition = Pair(getLine(move[1].toString().toInt()), files[move[0].toString()]!!)
        val destinationPosition = Pair(getLine(move[3].toString().toInt()), files[move[2].toString()]!!)
        return startPosition to destinationPosition
    }

    private fun capture(startPosition: Pair<Int, Int>, destinationPosition: Pair<Int, Int>) {
        val destinationPawn = chessBoard[destinationPosition.first][destinationPosition.second]
        val destinationIsOtherPawn = destinationPawn.toString() != " "&& destinationPawn.toString() != currentPlayer.second

        val direction = if (currentPlayer.second == "B") -1 else 1
        val destinationPawnBehind = chessBoard[destinationPosition.first + direction][destinationPosition.second]
        val enAvant = destinationPawnBehind.toString() != " "&& destinationPawnBehind.toString() != currentPlayer.second

        when {
            destinationIsOtherPawn -> {
                if (currentPlayer.second == "B") whitePawns.remove(destinationPosition) else blackPawns.remove(destinationPosition)
                makeMove(startPosition, destinationPosition)
            }
            enAvant -> {
                chessBoard[destinationPosition.first + direction][destinationPosition.second] = " "
                if (currentPlayer.second == "B") whitePawns.remove(destinationPosition.copy(destinationPosition.first + direction)) else blackPawns.remove((destinationPosition.copy(destinationPosition.first + direction)) )
                makeMove(startPosition, destinationPosition)
            }
            else -> println("Invalid input")
        }
    }

    private fun makeMove(startPosition: Pair<Int, Int>, destinationPosition: Pair<Int, Int>) {
        val pawn = chessBoard[startPosition.first][startPosition.second] as Pawn
        chessBoard[destinationPosition.first][destinationPosition.second] = Pawn(currentPlayer.second, false)
        chessBoard[startPosition.first][startPosition.second] = " "
        pawn.moved()

        if (currentPlayer.second == "B") {
            blackPawns.remove(startPosition)
            blackPawns.add(destinationPosition)
        } else {
            whitePawns.remove(startPosition)
            whitePawns.add(destinationPosition)
        }

        currentPlayer = if (currentPlayer == player1) player2 else player1
        showBoard()
    }

    private fun checkNeighbor(position: Pair<Int, Int>, move: String) {
        nextMove = ""
        val column = move[2]
        val line = move[3]
        val leftNeighbor = position.second > 0 && chessBoard[position.first][position.second - 1] != " "
                && chessBoard[position.first][position.second - 1] != currentPlayer.second

        val rightNeighbor = position.second < 7 && chessBoard[position.first][position.second + 1] != " "
                && chessBoard[position.first][position.second + 1] != currentPlayer.second

        val direction = if (currentPlayer.second == "B") -1 else 1

        when {
            leftNeighbor -> {
                nextMove = (column - 1).toString() + line + column + (line.toString().toInt() + direction)
            }

            rightNeighbor -> {
                nextMove = (column + 1).toString() + line + column + (line.toString().toInt() + direction)
            }
        }
    }

    private fun checkEndGame() {
        val blackWin = chessBoard.last().any{it != " "} || whitePawns.isEmpty()
        val whiteWin = chessBoard.first().any{it != " "} || blackPawns.isEmpty()

        val noMovesWhite = currentPlayer.second == "W" && whitePawns.size == 1 && !checkStalemateMovement(whitePawns.first())
        val noMovesBlack  = currentPlayer.second == "B" && blackPawns.size == 1 && !checkStalemateMovement(blackPawns.first())
        val stalemate = noMovesWhite || noMovesBlack

        when {
            blackWin -> {
                println("Black wins!")
                exit = false
            }
            whiteWin -> {
                println("White wins!")
                exit = false
            }
            stalemate -> {
                println("Stalemate!")
                exit = false
            }
        }
    }

    private fun checkStalemateMovement(pawn: Pair<Int, Int>): Boolean {
        val direction = if (currentPlayer.second == "B") 1 else -1
        val moveForward =  chessBoard[pawn.first + direction][pawn.second] == " "
        val moveLeft = pawn.second > 0 && chessBoard[pawn.first][pawn.second - 1] != " "
        val moveRight = pawn.second < 7 && chessBoard[pawn.first][pawn.second + 1] != " "
        return moveForward || moveLeft || moveRight
    }

    private fun showBoard() {
        println(separator)
        for (i in 8 downTo 1) {
            println("$i | ${chessBoard[abs(i - 8)].joinToString(" | ")} |")
            println(separator)
        }
        println("    a   b   c   d   e   f   g   h\n")
    }

    private fun getLine(num: Int) = abs(num - chessBoard.size)
}

fun main() {
    val game = Chess()
    game.play()
}
