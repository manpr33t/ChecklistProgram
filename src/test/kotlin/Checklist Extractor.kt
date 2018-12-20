package test.kotlin

import net.manpreet_singh.checklist.Utility
import net.manpreet_singh.config.UCRParser
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.PrintWriter

class Extractor {
    init {
        val outputFile = File("Output.csv")
        val pw = PrintWriter(outputFile)

        val parser = UCRParser()
        parser.readDataFromFile(File("UCR.xls"))
        val ucrData = parser.getDataMap()

        val sortGroup = "SELGTACO01"

        val checklistFile = FileReader("checklists/LVDDispatchChecklistMASTER.csv")

        val br = BufferedReader(checklistFile)

        val iterator = br.lineSequence().iterator()
        while (iterator.hasNext()) {
            val line = iterator.next()
            val buffer = line.split(",")
            if (ucrData!![sortGroup]?.contains(Utility.removeZipcodePrefix(buffer[1]))!!) {
                pw.println("${sortGroup},${buffer[1]}, ${buffer[2]}")
            }
        }
        pw.close()

        var keyNumbers = 0
        println("Available Sort Groups: ")
        ucrData!!.keys.forEach {
            print(keyNumbers++)
            println(") $it")
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Extractor()
        }
    }
}