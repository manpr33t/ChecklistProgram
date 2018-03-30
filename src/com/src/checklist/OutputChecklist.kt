package com.src.checklist

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.FileOutputStream

/**
 * Static Function to output a Checklist ArrayList to an excel file
 * @param data ArrayList containing Checklist data
 * @param header String header to print above all other data
 * @param fileName Output File Name
 */
fun outputList(data: MutableCollection<String>, header: String, fileName: String) {
    val workBook = HSSFWorkbook()
    val sheet = workBook.createSheet(fileName)

    val style = workBook.createCellStyle()
    val font = workBook.createFont()
    font.fontHeightInPoints = 11
    font.fontName = "Calibri"
    style.setFont(font)

    var rowNum = 0

    // Output file Header
    val headerRow = sheet.createRow(rowNum++)
    headerRow.setRowStyle(style)
    var headerCellNum = 0
    header.split(",").forEach { i ->
        val cell = headerRow.createCell(headerCellNum++)
        cell.setCellValue(i)
    }

    // Loop through the given data
    data.forEach { i ->
        val row = sheet.createRow(rowNum++)
        var colNum = 0
        i.split(",").forEach { t ->
            val cell = row.createCell(colNum++)
            cell.setCellValue(t)
            cell.setCellStyle(style)
        }
    }

    val footerRow = sheet.createRow(++rowNum)
    footerRow.setRowStyle(style)
    footerRow.createCell(0).setCellValue("Number of Containers:")
    footerRow.createCell(1).setCellValue(data.size.toString())


    sheet.autoSizeColumn(0)

    val outputStream = FileOutputStream(fileName)
    workBook.write(outputStream)
    workBook.close()
    outputStream.close()
}