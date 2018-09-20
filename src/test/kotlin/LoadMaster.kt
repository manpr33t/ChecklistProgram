package test.kotlin

import net.manpreet_singh.config.UCRParser
import java.io.File

class LoadMaster {
    private var tripData: MutableMap<String, MutableSet<String>>? = null
    init {
        val ucrParser = UCRParser()
        ucrParser.readDataFromFile(File("UCR.xls"))
        tripData = ucrParser.getTrips()

        println("Available Trips: ")
        var num = 1
        tripData!!.keys.forEach { it ->
            println("$num) $it")
            num++
        }

        println("\nYou decide: ")
    }


    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val lm = LoadMaster()
        }
    }
}