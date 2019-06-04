package test.kotlin

import java.io.BufferedReader
import java.io.File
import java.io.FileReader

fun scrubFile() {
    val bfr = BufferedReader(FileReader("temp/reptPRfinalContainer_05152019_3124621.txt"))
    val iterator = bfr.lineSequence().iterator()
    val output = File("temp/output.txt").printWriter()

    iterator.forEach {
        if (it.startsWith("PP"))
            output.println(it)
    }
    output.close()
    bfr.close()
}

fun parseLocations() {
    val bfr = BufferedReader(FileReader("temp/output.txt"))
    val iterator = bfr.lineSequence().iterator()
    val output = File("temp/output.csv").printWriter()
    val dataMap: MutableMap<String, String> = HashMap() // Have to put everything into a hash map to remove duplicates :(

    iterator.forEach {
        val line = it.split(Regex("  *"))
        if (line.size > 10) {
            dataMap.put(line[1], line[2]+ " " + line[3].dropLast(1))
//            output.println("*,${line[1]},${line[2]+ " " + line[3].dropLast(1)}")
        }
    }

    dataMap.keys.forEach {
        output.println("*,$it,${dataMap[it]}")
    }

    dataMap.clear() // dump all data into abyss

    output.close()
    bfr.close()
}

fun main(args: Array<String>) {
    scrubFile()
    parseLocations()
}