package search
import java.io.File

fun main(args: Array<String>) {
    if (args.size != 2 || args[0] != "--data") {
        println("Usage: java SearchProgram --data <filename>")
        return
    }

    val fileName = args[1]
    val invertedIndex = buildInvertedIndex(fileName)

    while (true) {
        printMenu()
        when (val choice = readLine()!!.toInt()) {
            1 -> searchPerson(invertedIndex, fileName)
            2 -> printAllPeople(fileName)
            0 -> {
                println("Bye!")
                break
            }
            else -> println("Incorrect option! Try again.")
        }
    }
}

fun buildInvertedIndex(fileName: String): Map<String, MutableList<Int>> {
    val invertedIndex = mutableMapOf<String, MutableList<Int>>()

    File(fileName).readLines().forEachIndexed { index, line ->
        line.split(" ").forEach { word ->
            val normalizedWord = word.toLowerCase()
            if (invertedIndex.containsKey(normalizedWord)) {
                invertedIndex[normalizedWord]!!.add(index)
            } else {
                invertedIndex[normalizedWord] = mutableListOf(index)
            }
        }
    }

    return invertedIndex
}

fun printMenu() {
    println("\n=== Menu ===")
    println("1. Find a person")
    println("2. Print all people")
    println("0. Exit")
    print("> ")
}

fun searchPerson(invertedIndex: Map<String, MutableList<Int>>, fileName: String) {
    println("Select a matching strategy: ALL, ANY, NONE")
    val strategy = readLine()!!.toUpperCase()
    println("Enter a name or email to search all matching people:")
    val query = readLine()!!.trim().toLowerCase().split(" ")

    val results = when (strategy) {
        "ALL" -> searchAll(invertedIndex, query)
        "ANY" -> searchAny(invertedIndex, query)
        "NONE" -> searchNone(invertedIndex, query)
        else -> {
            println("Invalid strategy.")
            return
        }
    }

    println("${results.size} persons found:")
    val dataLines = File(fileName).readLines()
    results.forEach { index ->
        println(dataLines[index])
    }
}

fun searchAll(invertedIndex: Map<String, MutableList<Int>>, query: List<String>): List<Int> {
    val allResults = mutableListOf<Int>()
    val firstWord = query[0]
    val initialResults = invertedIndex[firstWord] ?: return emptyList()

    for (index in initialResults) {
        var foundAll = true
        for (word in query) {
            if (word !in invertedIndex || index !in invertedIndex[word]!!) {
                foundAll = false
                break
            }
        }
        if (foundAll) allResults.add(index)
    }

    return allResults
}

fun searchAny(invertedIndex: Map<String, MutableList<Int>>, query: List<String>): List<Int> {
    val anyResults = mutableSetOf<Int>()
    for (word in query) {
        if (word in invertedIndex) {
            anyResults.addAll(invertedIndex[word]!!)
        }
    }
    return anyResults.toList()
}

fun searchNone(invertedIndex: Map<String, MutableList<Int>>, query: List<String>): List<Int> {
    val allIndices = invertedIndex.values.flatten().toSet()
    val noneResults = allIndices.toMutableList()

    for (word in query) {
        if (word in invertedIndex) {
            noneResults.removeAll(invertedIndex[word]!!)
        }
    }

    return noneResults
}

fun printAllPeople(fileName: String) {
    println("\n=== List of people ===")
    File(fileName).forEachLine { println(it) }
}

