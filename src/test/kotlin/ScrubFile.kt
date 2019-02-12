package test.kotlin

import java.io.BufferedReader
import java.io.File
import java.io.FileReader

fun scrubFile() {
    val bfr = BufferedReader(FileReader("temp/reptPRfinalContainer_02112019_1100145.txt"))
    val iterator = bfr.lineSequence().iterator()
    val output = File("temp/output.txt").printWriter()

    iterator.forEach {
        if (it.startsWith("PP"))
            output.println(it)
    }
}

fun parseLocations() {
    val bfr = BufferedReader(FileReader("temp/output.txt"))
    val iterator = bfr.lineSequence().iterator()
    val output = File("temp/output.csv").printWriter()

    iterator.forEach {
        val line = it.split(Regex("  *"))
        if (line.size > 10) {
            output.println("*,${line[1]},${line[2]+ " " + line[3].dropLast(1)}")
        }
    }
    output.close()
}

fun main(args: Array<String>) {
//    scrubFile()
    parseLocations()
}