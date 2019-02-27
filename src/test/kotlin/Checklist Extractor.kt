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

        val checklistFile = FileReader("checklists/AllPalletsListShogun.csv")

        val br = BufferedReader(checklistFile as Reader?)

        val iterator = br.lineSequence().iterator()
        while (iterator.hasNext()) {
            val line = iterator.next()
            val buffer = line.split(",")
            checklistData[Utility.removeZipcodePrefix(buffer[1])] = buffer[2]
//            try {
//                if (ucrData!![sortGroup]?.contains(Utility.removeZipcodePrefix(buffer[1]))!!)
//                    pw.println("$sortGroup,${buffer[1]}, ${buffer[2]}")
//            } catch (e: Exception) {
//                e.printStackTrace()
//                println(buffer)
//            }
        }

        for (key in ucrData!!.keys) {
            if (key.startsWith("S")) {
                val printW = File("temp/$key-new.csv").printWriter()
                ucrData[key]!!.forEach {
                    val zipcode = Utility.removeZipcodePrefix(it)
                    try {
                        printW.println("$key,$it,${checklistData[zipcode]}")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        println("[$key,$it]")
                    }
                }
                printW.close()
            }
        }

    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Extractor()
        }
    }
}