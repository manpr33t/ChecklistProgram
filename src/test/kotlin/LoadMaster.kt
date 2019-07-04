package test.kotlin

import net.manpreet_singh.checklist.Utility
import net.manpreet_singh.config.UCRParser
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.PrintWriter

class LoadMaster {
    private var tripData: MutableMap<String, MutableSet<String>>? = null
    init {
        val ucrParser = UCRParser()
        ucrParser.readDataFromFile(File("UCR.xls"))
        tripData = ucrParser.getTrips()

        val printWriter = PrintWriter(File("Trips.csv"))
        printWriter.println("Here's what's Loaded")

        println("Available Trips: ")
        tripData!!.keys.forEach { it ->
            println(it)
        }

        println("\nYou decide: ")
        val userChoice = readLine()!!.toInt()
        val choice = userChoice.toString()
        if (tripData!!.containsKey(choice)) {
            val checklistFile = FileReader("checklists/" + askForChecklistFile())
            val br = BufferedReader(checklistFile)
            val iterator = br.lineSequence().iterator()
            while (iterator.hasNext()) {
                val line = iterator.next()
                val buffer = line.split(",")
                if (tripData!![choice]?.contains(Utility.removeZipcodePrefix(buffer[1]))!!)
                    printWriter.println("${buffer[0]},${buffer[1]},${buffer[2]}")
            }
        }
        else
            printWriter.println("Nothing at all!")
        printWriter.close()
    }

    fun askForChecklistFile(): String {
        val f = File("checklists")
        val subDir: Array<out File>
        subDir = f.listFiles()
        subDir.forEachIndexed {index, file ->
            println(index.toString() + ") " + file.name)
        }
        print("\nMake your choice: ")
        val index = readLine()
        return subDir[index!!.toInt()].name
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val lm = LoadMaster()
        }
    }
}