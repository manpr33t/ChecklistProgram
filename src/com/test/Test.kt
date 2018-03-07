package com.test

import com.src.checklist.Utility
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem

import java.io.File

private var spreadSheetRows: ArrayList<HSSFRow>? = null
private var dataMap: MutableMap<String, String>? = null

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
        dataMap!!.put (
            it.getCell(8).toString(),
            Utility.removeZipcodePrefix(it.getCell(it.firstCellNum.toInt()).toString())
    )})

}

fun getDataMap() : MutableMap<String, String>? {
    return dataMap
}

fun main(args: Array<String>) {
}
