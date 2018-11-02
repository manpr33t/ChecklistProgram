/*
   Copyright 2018 Manpreet Singh

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package net.manpreet_singh.config

import net.manpreet_singh.checklist.Utility
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.Row
import java.io.File
import java.io.PrintWriter

class UCRParser {

    private var spreadSheetRows: ArrayList<HSSFRow>? = null
    private var dataMap: MutableMap<String, MutableSet<String>>? = null

    /**
     * Create a Map from a UCR Excel Spreadsheet
     */
    fun readDataFromFile(inputFile: File) {

        if (inputFile.name.split('.')[1] != "xls")
            throw IllegalArgumentException("Unsupported File type")

        // Re-establish objects every time this method is run
        spreadSheetRows = ArrayList()
        dataMap = HashMap()

        val fileSystem = POIFSFileSystem(inputFile)
        val workBook = HSSFWorkbook(fileSystem)
        val spreadSheet: HSSFSheet = workBook.getSheetAt(0)

        val rowIterator: Iterator<Row> = spreadSheet.rowIterator()

        // Go through row by row and add it to the rows array list
        while (rowIterator.hasNext()) spreadSheetRows?.add(rowIterator.next() as HSSFRow)

        // For each of the rows, parse its data and add to the appropriate data structure
        spreadSheetRows!!.forEach { it ->
            if (checkForData(it)) {
                try {
                    if (it.getCell(8) != null) {
                        if (!dataMap!!.containsKey(it.getCell(8).toString()))
                            dataMap!![it.getCell(8).toString()] = HashSet()

                        dataMap!![it.getCell(8).toString()]!!.add(
                                Utility.removeZipcodePrefix(it.getCell(it.firstCellNum.toInt()).toString())
                        )
                    }
                } catch (e: Exception) {
                    println(e.localizedMessage)
                    println("Row:${it.rowNum + 1}")
                }
            }
        }
        workBook.close()
        fileSystem.close()
    }

    /**
     * Check if the row contains actual usable data
     */
    private fun checkForData(row: HSSFRow): Boolean {
        val str = row.getCell(row.firstCellNum.toInt()).toString()
        return "DDU|P5D|\\d".toRegex().containsMatchIn(str)
    }

    /**
     * Get the mapped data
     */
    fun getDataMap(): MutableMap<String, MutableSet<String>>? {
        return dataMap
    }

    fun getTrips(): MutableMap<String, MutableSet<String>>? {
        val tripMap: MutableMap<String, MutableSet<String>>? = HashMap()
        if (!spreadSheetRows!!.isEmpty()) {
            spreadSheetRows!!.forEach { it ->
                if (checkForData(it)) {
                    try {
                        if (it.getCell(9) != null) {
                            if (!tripMap!!.containsKey(it.getCell(9).toString()))
                                tripMap[it.getCell(9).toString()] = HashSet()

                            tripMap[it.getCell(9).toString()]!!.add(
                                    Utility.removeZipcodePrefix(it.getCell(it.firstCellNum.toInt()).toString())
                            )
                        }
                    } catch (e: Exception) {
                        println(e.localizedMessage)
                        println("Row:${it.rowNum + 1}")
                    }
                }
            }
        }
        return tripMap
    }

    /**
     * Output the map into a CSV file for debugging purposes
     */
    fun outputMap() {
        val file = File("DataMap.csv")
        val pw = PrintWriter(file)

        getDataMap()!!.keys.forEach {
            pw.write(it)
            pw.write(",")
            getDataMap()?.get(it)?.forEach { i ->
                pw.write(i)
                pw.write(",")
            }
            pw.write("\n")
        }
        pw.close()
    }


}