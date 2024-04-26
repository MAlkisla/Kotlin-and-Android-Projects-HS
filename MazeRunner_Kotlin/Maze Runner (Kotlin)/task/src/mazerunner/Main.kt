package mazerunner

import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.pow

const val EXIT = "Exit"
const val GENERATE = "Generate a new maze"
const val LOAD = "Load a maze"
const val SAVE = "Save the maze"
const val DISPLAY = "Display the maze"
const val ESCAPE = "Find the escape"
val allOptions = listOf(EXIT, GENERATE, LOAD, SAVE, DISPLAY, ESCAPE)
var mMaze: Maze? = null

fun main() {
    while (true) {
        val mOptions = allOptions.filter { it in listOf(EXIT, GENERATE, LOAD) || mMaze != null }
        println("=== Menu ===")
        mOptions.forEachIndexed { index, s -> run { if (index != 0) println("$index. $s") } }
        println("0. ${mOptions[0]}")
        when (mOptions[readln().toInt()]) {
            EXIT -> {
                println("Bye!")
                break
            }
            GENERATE -> {
                println("Enter the size of a new maze")
                val size = readln().toInt()
                mMaze = Maze(size, size)
                println(mMaze.toString().replace(Maze.ESCAPE_STR, Maze.PASSAGE_STR))
            }
            LOAD -> {
                println("Enter file name")
                val file = File(readln())
                if (file.exists()) {
                    mMaze = Maze.toMaze(file.readText())
                } else {
                    println("The file $file does not exist")
                }
            }
            SAVE -> {
                println("Enter file name")
                val file = File(readln())
                file.writeText(mMaze.toString())
                println()
            }
            DISPLAY -> println(mMaze.toString().replace(Maze.ESCAPE_STR, Maze.PASSAGE_STR))
            ESCAPE -> {
                mMaze?.findEscape()
                println(mMaze)
            }
            else -> println("Incorrect option. Please try again")
        }
    }
}

class Maze(val height: Int, val width: Int,
           val passage: MutableSet<Pos> = mutableSetOf(),
           val escape: MutableList<Pos> = mutableListOf(),
           val distance: Double = 2.0, val bound: Int = 1) {

    val visited = mutableSetOf<Pos>()

    init {
        if (passage.isEmpty()) {
            var next: Pos = (bound until height - bound).random() to (bound until width - bound).random()
            passage.add(next)
            val frontier = frontierCells(next)
            while (frontier.isNotEmpty()) {
                next = frontier.random()
                frontier.remove(next)
                if (isValid(next)) passage.add(next)
                val closest = passage.filter { it.distance(next) == distance }.random()
                val inBetween = ((next.row() + closest.row()) / 2) to ((next.col() + closest.col()) / 2)
                if (isValid(inBetween)) passage.add(inBetween)
                if (isValid(next)) frontier.addAll(frontierCells(next))
            }
        }
    }

    private fun isValid(pos: Pos): Boolean {
        val hasEntrance = passage.any { isEntrance(it) }
        val hasExit = passage.any { isEscape(it) }
        return when {
            isEntrance(pos) && hasEntrance && pos.row() != passage.first { isEntrance(it) }.row() -> false
            isEscape(pos) && hasExit && pos.row() != passage.first { isEscape(it) }.row() -> false
            pos.col() !in 0 until width || pos.row() !in bound until height - bound -> false
            else -> true
        }
    }

    private fun frontierCells(currentPos: Pos, distance: Int = 2 ): MutableSet<Pos> {
        val (row, col) = currentPos
        return mutableSetOf(
            row - distance to col,
            row + distance to col,
            row to col - distance,
            row to col + distance
        ).filter { it !in passage }.toMutableSet()
    }

    fun findEscape() {
        visited.clear()
        escape.clear()
        val start = passage.first { isEntrance(it) }
        findEscapeHelper(start)
    }

    private fun findEscapeHelper(pos: Pos) {
        if (isEscape(passage.last())) return
        escape.add(pos)
        visited.add(pos)
        if (isEscape(escape.last())) return
        if (getUnknownNeighborhood(pos).isNotEmpty()) {
            for (neighbor in getUnknownNeighborhood(pos)) {
                if (isEscape(escape.last())) return
                findEscapeHelper(neighbor)
                if (isEscape(escape.last())) return
            }
        } else {
            while (getUnknownNeighborhood(escape.last()).isEmpty()) {
                escape.remove(escape.last())
            }
        }
    }

    private fun isEntrance(pos: Pos): Boolean = pos.col() == 0
    private fun isEscape(pos: Pos): Boolean = pos.col() == width - 1
    private fun getUnknownNeighborhood(pos: Pos): Set<Pos> =
        passage.filter { it.distance(pos) == 1.0 }.filter { it !in visited  }.toSet()

    override fun toString(): String {
        return buildString {
            for (row in 0 until height) {
                for (col in 0 until width) {
                    append(
                        when (Pos(row, col)) {
                            in escape -> ESCAPE_STR
                            in passage -> PASSAGE_STR
                            else -> WALL_STR
                        }
                    )
                }
                append("\n")
            }
        }
    }

    companion object {
        const val WALL_STR = "\u2588\u2588"
        const val PASSAGE_STR = "  "
        const val ESCAPE_STR = "//"

        fun toMaze(string: String): Maze {
            val lines = string.lines().filter { it.isNotBlank() }.map { it.filterIndexed { index, _ -> index % 2 == 0 } }
            val height = lines.size
            val width = lines.getOrNull(0)?.length ?: 0
            val passage = mutableSetOf<Pos>()
            lines.forEachIndexed { row, line ->
                line.forEachIndexed { col, char ->
                    if (char == PASSAGE_STR[0]) passage.add(row to col)
                }
            }
            return Maze(height, width, passage)
        }
    }
}

typealias Pos = Pair<Int, Int>
fun Pos.row() = this.first
fun Pos.col() = this.second
fun Pos.distance(other: Pos, l: Double = 2.0) = (
        (this.row() - other.row()).toDouble().absoluteValue.pow(l) +
                (this.col() - other.col()).toDouble().absoluteValue.pow(l)
        ).pow(1 / l )
