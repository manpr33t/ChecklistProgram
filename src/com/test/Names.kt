package com.test

import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.FileInputStream
import java.io.FileOutputStream

class Names {

    fun run() {
        val lvdChecklist = HSSFWorkbook(FileInputStream("C:\\Users\\2854787\\IdeaProjects\\CSVReader\\checklists\\LVDDispatchChecklistMASTER.xls"))
        val lfReport = HSSFWorkbook(FileInputStream("C:\\Users\\2854787\\IdeaProjects\\CSVReader\\LF Report.xls"))

        val lfReportSheet = lfReport.getSheet("LVD")
        val lvdSheet = lvdChecklist.createSheet("LC")

        val dataMap: MutableMap<String, String> = HashMap<String, String>()

        lfReportSheet.forEach({it ->
            if (it.getCell(1) != null && checkForData(it as HSSFRow))
                dataMap.put(it.getCell(1).toString(), it.getCell(2).toString())
            else
                println(it.toList().toString())
        })

        lvdSheet.forEach({ it ->
            if (checkForData(it as HSSFRow)) {
                if (dataMap.containsKey(it.getCell(1).toString()))
                    it.getCell(0).setCellValue(dataMap[it.getCell(1).toString()])
            }
        })

        val outputStream = FileOutputStream("C:\\Users\\2854787\\IdeaProjects\\CSVReader\\checklists\\LVDTest.xls")
        with(lvdChecklist) {
            write(outputStream)
//            write(File("test.xls"))
            close()
        }
        outputStream.close()
    }

    companion object {


        private fun checkForData(row: HSSFRow): Boolean {
            return (!row.getCell(1).toString().startsWith("Z") ||
                    !row.getCell(1).toString().startsWith("S")) &&
                            row.getCell(1) != null
        }
    }
}

fun main(args: Array<String>) {
    val names = Names()
    names.run()
}
