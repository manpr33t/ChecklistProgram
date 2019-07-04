package test.kotlin

import net.manpreet_singh.checklist.Utility
import net.manpreet_singh.config.UCRParser
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.Reader

class Extractor {
    init {

        val parser = UCRParser()
        parser.readDataFromFile(File("UCR.xls"))
        val ucrData = parser.getDataMap()
        val checklistData: MutableMap<String, String> = HashMap()

        val checklistFile = FileReader("temp/output.csv")

        val br = BufferedReader(checklistFile as Reader?)

        val iterator = br.lineSequence().iterator()
        while (iterator.hasNext()) {
            val line = iterator.next()
            val buffer = line.split(",")
            checklistData[Utility.removeZipcodePrefix(buffer[2])] = buffer[1] // Map Sort Group to Location
        }
        val keyOutput = File("temp/Keys.txt").printWriter()
        for (key in ucrData!!.keys) {
            keyOutput.println(key)
            if (key.startsWith("D")) {
                val fileOutput = File("temp/$key-new.csv").printWriter()
                ucrData[key]!!.forEach {
                    val zipcode = Utility.removeZipcodePrefix(it)
                    try {
                        fileOutput.println("$key,$it,${checklistData[zipcode]}")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        println("[$key,$it]")
                    }
                }
                fileOutput.close()
            }
        }
        keyOutput.close()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Extractor()
        }
    }
}