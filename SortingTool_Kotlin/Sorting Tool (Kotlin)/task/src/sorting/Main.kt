package sorting
import java.io.File
import java.util.Scanner
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)

    inputStrategy(args)
}

fun inputStrategy(args: Array<String>){
    val checkedArgs = checkInput(args)
    var sortByCount = false
    var dataType = ""
    var inputFile = ""
    var outputFile = ""

    if (checkedArgs.isNotEmpty()){
        if (checkedArgs.contains("-sortingType")){
            val sortingTypeIndex = checkedArgs.indexOf("-sortingType") + 1
            if (checkedArgs[sortingTypeIndex] == "byCount")
                sortByCount = true
        }
        if (checkedArgs.contains("-dataType")){
            val dataTypeIndex = checkedArgs.indexOf("-dataType") + 1
            dataType = checkedArgs[dataTypeIndex]
        }
        if (checkedArgs.contains("-inputFile")){
            val inputFileIndex = checkedArgs.indexOf("-inputFile") + 1
            inputFile = checkedArgs[inputFileIndex]
        }
        if (checkedArgs.contains("-outputFile")){
            val outputFileIndex = checkedArgs.indexOf("-outputFile") + 1
            outputFile = checkedArgs[outputFileIndex]
        }
    }

    when(dataType){
        "word" -> inputWord(sortByCount, inputFile, outputFile)
        "long" -> inputLong(sortByCount, inputFile, outputFile)
        "line" -> inputLine(sortByCount, inputFile, outputFile)
        else -> inputWord(sortByCount, inputFile, outputFile)
    }
}
//-dataType long -sortingType byCount -inputFile out.txt
fun checkInput(args: Array<String>): Array<String>{
    if (args.contains("-sortingType") && !args.contains("byCount") && !args.contains("natural")){
        println("No sorting type defined!")
        exitProcess(1)
    }
    if (args.contains("-dataType") && !args.contains("word") && !args.contains("long") && !args.contains("line")){
        println("No data type defined!")
        exitProcess(1)
    }


    val checkedArgs = mutableListOf<String>()
    val allowedValues = listOf("-sortingType", "byCount", "natural", "-dataType", "word", "long", "line", "-inputFile", "-outputFile")
    for (argument in args){
        if (allowedValues.contains(argument))
            checkedArgs.add(argument)
        else
            checkedArgs.add(argument)
        // println("\"$argument\" isn not a valid parameter. It will be skipped.")
    }

    return checkedArgs.toTypedArray()
}

fun inputLong(sortByCount: Boolean = false, inputFile: String = "", outputFile: String = ""){
    val customComparator = compareBy<Map.Entry<Long, Int>> { it.value }.thenBy { it.key }
    val sb: StringBuilder = StringBuilder()
    val listOfLong = mutableListOf<Long>()

    val scanner = if (inputFile.isBlank()) {
        Scanner(System.`in`)
    } else {
        Scanner(File(inputFile))
    }
    while (scanner.hasNextLong()) {
        val number = scanner.nextLong()
        listOfLong.add(number)
    }
    scanner.close()

    if (sortByCount){
        val mapOfLong = mutableMapOf<Long, Int>()
        for (element in listOfLong){
            mapOfLong[element] = listOfLong.count { it == element }
        }
        val sortedMap = mapOfLong.entries.sortedWith(customComparator)
        sb.append("Total numbers: ${listOfLong.size}.\n")
        sortedMap.forEach {
            sb.append("${it.key}: ${it.value} time(s), ${(it.value * 100) / listOfLong.size}%\n")
        }
    } else{
        listOfLong.sort()
        sb.append("Total numbers: ${listOfLong.size}.\n" + "Sorted data: ")
        listOfLong.forEach { sb.append("$it ") }
    }

    if (outputFile.isBlank()){
        println(sb.toString())
    } else{
        val outFile = File(outputFile)
        outFile.writeText(sb.toString())
    }
}

fun inputLine(sortByCount: Boolean = false, inputFile: String = "", outputFile: String = ""){
    val customComparator = compareBy<Map.Entry<String, Int>> { it.value }.thenBy { it.key }
    val sb: StringBuilder = StringBuilder()
    val listOfStrings = mutableListOf<String>()

    val scanner = if (inputFile.isBlank()) {
        Scanner(System.`in`)
    } else {
        Scanner(File(inputFile))
    }
    while (scanner.hasNextLine()){
        val number = scanner.nextLine()
        listOfStrings.add(number)
    }
    scanner.close()

    if (sortByCount){
        val mapOfLong = mutableMapOf<String, Int>()
        for (element in listOfStrings){
            mapOfLong[element] = listOfStrings.count { it == element }
        }
        val sortedMap = mapOfLong.entries.sortedWith(customComparator)
        sb.append("Total numbers: ${listOfStrings.size}.\n")
        sortedMap.forEach {
            sb.append("${it.key}: ${it.value} time(s), ${(it.value * 100) / listOfStrings.size}%\n")
        }
    } else{
        listOfStrings.sort()
        sb.append("Total lines: ${listOfStrings.size}.\n" + "Sorted data:\n")
        listOfStrings.forEach { sb.append("$it\n") }
    }

    if (outputFile.isBlank()){
        println(sb.toString())
    } else{
        val outFile = File(outputFile)
        outFile.writeText(sb.toString())
    }
}

fun inputWord(sortByCount: Boolean = false, inputFile: String = "", outputFile: String = ""){
    val customComparator = compareBy<Map.Entry<String, Int>> { it.value }.thenBy { it.key }
    val sb: StringBuilder = StringBuilder()
    val whitespaceRegEx = "\\s+".toRegex()
    val listOfStrings = mutableListOf<String>()

    val scanner = if (inputFile.isBlank()) {
        Scanner(System.`in`)
    } else {
        Scanner(File(inputFile))
    }
    while (scanner.hasNextLine()){
        val numbersList = scanner.nextLine().replace(whitespaceRegEx, " ").split(" ")
        listOfStrings.addAll(numbersList)
    }
    scanner.close()

    if (sortByCount){
        val mapOfLong = mutableMapOf<String, Int>()
        for (element in listOfStrings){
            mapOfLong[element] = listOfStrings.count { it == element }
        }
        val sortedMap = mapOfLong.entries.sortedWith(customComparator)
        sb.append("Total numbers: ${listOfStrings.size}.\n")
        sortedMap.forEach {
            sb.append("${it.key}: ${it.value} time(s), ${(it.value * 100) / listOfStrings.size}%\n")
        }
    } else{
        listOfStrings.sort()
        sb.append("Total words: ${listOfStrings.size}.\n" + "Sorted data: ")
        listOfStrings.forEach { sb.append("$it ") }
    }

    if (outputFile.isBlank()){
        println(sb.toString())
    } else{
        val outFile = File(outputFile)
        outFile.writeText(sb.toString())
    }
}