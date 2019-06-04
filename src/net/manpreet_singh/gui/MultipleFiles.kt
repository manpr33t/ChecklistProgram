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


package net.manpreet_singh.gui

import javafx.application.Application
import javafx.stage.FileChooser
import javafx.stage.Stage
import net.manpreet_singh.config.UCRParser
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import java.io.File
import java.util.*
import kotlin.collections.HashMap
import kotlin.system.exitProcess

class MultipleFiles {

    var files: MutableList<File>? = null
    private var mFileChooser: FileChooser? = null
    private var mUCRParser: UCRParser? = null

    private var mDataMap: MutableMap<String, MutableSet<String>?>? = null

    init {
        mFileChooser = FileChooser()
        mUCRParser = UCRParser()
        mDataMap = HashMap()
    }

    /**
     * Ask the user for some Files.
     * @param stage Stage Object to use when opening the file chooser
     */
    private fun askForFiles(stage: Stage) : MutableList<File>? {
        val files = mFileChooser?.showOpenMultipleDialog(stage)
        var badFiles: Stack<File>? = null

        // Check each of the files to make sure they're all spreadsheets.
        if (files != null) files.forEach {
            if (it.extension != "xls" || !it.canExecute()) {
                if (badFiles == null)
                    badFiles = Stack()
                badFiles!!.add(it)
            }
        } else {
            throw Exception("No Files Inputted")
        }

        // If we had any bad files, throw an exception.
        if (badFiles != null && badFiles!!.size > 0) {
            val fileNames = StringBuilder()
            badFiles!!.forEach { it ->
                fileNames.append("\n${it.name} ")
            }
            throw Exception("Unsupported files: $fileNames")
        }

        return files
    }

    /**
     * @param s File name
     * @param stage Stage Object to use when running this method
     */
    fun mergeAllAndSave(s: String, stage: Stage)  {
        val files = askForFiles(stage) // Prompt user for files
        val workbooks: MutableSet<Workbook> = HashSet() // Prepare a set of workbooks
        val mergedWorkbook = HSSFWorkbook() // One workbook to merge all the other workbooks into

        // Go through all the files and add them to the set of workbooks
        files!!.forEach {
            val fs = POIFSFileSystem(it)
            workbooks.add(HSSFWorkbook(fs))
        }

        val sheet = mergedWorkbook.createSheet() // Start mew sheet
        var rowNum: Int = 0 // keep track of all the rows we add
        workbooks.forEach { it ->
            val rowIterator = it.getSheetAt(0).rowIterator()
            while (rowIterator.hasNext()) { // Iterate across the rows
                val newRow = sheet.createRow(rowNum) // Create a new row
                mergeRows(rowIterator.next(), newRow) // Merge into the new row
                rowNum++
            }
            it.close() // Close the sheet once we're done with it, no need to keep it around 💔
        }

        mergedWorkbook.write(File("$s.xls")) // Save to file
    }

    /**
     * Run this program
     * @param stage Stage Object to use when running this program.
     */
    fun run(stage: Stage) : MutableMap<String, MutableSet<String>?>? {
        try {
            files = askForFiles(stage)
        } catch (e: Exception) {
             throw e
        }

        // Pull data from all the files
        files!!.forEach {
            // Read the data
            mUCRParser!!.readDataFromFile(it)

            // Go through the data
            mUCRParser!!.getDataMap()!!.keys.forEach{ key ->
                if (!mDataMap!!.containsKey(key))
                    mDataMap!![key] = mUCRParser!!.getDataMap()!![key]
                else
                    mDataMap!![key]!!.addAll(mUCRParser!!.getDataMap()!![key]!!)
            }

            // Prep for next run
            mUCRParser!!.clearData()
        }

        return this.mDataMap
    }

    /**
     * Merge the the source row into the target row.
     * @param source The first row to merge.
     * @param target The row to merge the first row into.
     */
    private fun mergeRows(source: Row, target: Row) { // row, row, row you boat ... gently down the stream ...
        val cellIterator = source.cellIterator()
        var cellNum = 0 // keep track of the cells we copy over

        // go through the cells in the source row
        while (cellIterator.hasNext()) {
            val sourceCell = cellIterator.next()
            val sourceCellType  = sourceCell.cellTypeEnum

            val targetCell = target.createCell(cellNum)

            // Copy the cell values depending its type, we don't know care about cell styles or anything
            when (sourceCellType!!) { // Check the cell type to determine how to copy its data
                CellType._NONE -> targetCell.setCellValue("**")
                CellType.NUMERIC -> targetCell.setCellValue(sourceCell.numericCellValue)
                CellType.STRING -> targetCell.setCellValue(sourceCell.stringCellValue)
                CellType.FORMULA -> targetCell.setCellValue(sourceCell.richStringCellValue)
                CellType.BLANK -> targetCell.setCellValue("*")
                CellType.BOOLEAN -> targetCell.setCellValue(sourceCell.booleanCellValue)
                CellType.ERROR -> throw RuntimeException()
            }

            cellNum++
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

            Application.launch(Companion.Test::class.java, *args)
        }

        class Test : Application() {
            override fun start(primaryStage: Stage?) {
                val mf = MultipleFiles()
                mf.mergeAllAndSave("MergedUCR", primaryStage!!)
                print("Done")
                exitProcess(0)
            }
        }
    }

}
