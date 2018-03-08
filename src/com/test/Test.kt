package com.test

import com.src.checklist.Utility
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem

import java.io.File
import java.io.PrintWriter

private var spreadSheetRows: ArrayList<HSSFRow>? = null
private var dataMap: MutableMap<String, MutableSet<String>>? = null

/**
 * Create a Map from a UCR Excel Spreadsheet
 *
 */
fun readDataFromFile(inputFile: File) {

    spreadSheetRows = ArrayList()
    dataMap = HashMap()

    val fileSystem: POIFSFileSystem = POIFSFileSystem(inputFile)
    val workBook: HSSFWorkbook = HSSFWorkbook(fileSystem)
    val spreadSheet: HSSFSheet = workBook.getSheetAt(0)

    val rowIterator: Iterator<Row> = spreadSheet.rowIterator()

    while (rowIterator.hasNext()) spreadSheetRows?.add(rowIterator.next() as HSSFRow)

        spreadSheetRows!!.forEach({
            if (checkForData(it)) {
                try {
                    if (!dataMap!!.containsKey(it.getCell(8).toString()))
                        dataMap!!.put(it.getCell(8).toString(), HashSet())
                    dataMap!!.get(it.getCell(8).toString())?.add(
                            Utility.removeZipcodePrefix(it.getCell(it.firstCellNum.toInt()).toString())
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    println(e.localizedMessage)
                }
            }
        })

}

fun checkForData(row: HSSFRow): Boolean {
    return row.getCell(row.firstCellNum.toInt()).toString().startsWith("D") ||
            row.getCell(row.firstCellNum.toInt()).toString().startsWith("P") ||
            !row.getCell(row.firstCellNum.toInt()).toString().startsWith("S")
}

fun getDataMap() : MutableMap<String, MutableSet<String>>? {
    return dataMap
}

fun outputMap() {
    val file: File = File("test.csv")
    val pw = PrintWriter(file)

    dataMap!!.keys.forEach {
        pw.write(it)
        dataMap?.get(it)?.forEach { i ->
            pw.write(i)
            pw.write(",")
        }
        pw.write("\n")
    }
    pw.close()
}

fun main(args: Array<String>) {
    readDataFromFile(File("UCR.xls"))
    outputMap()
}
