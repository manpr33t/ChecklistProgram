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

package com.src.checklist

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Static Function to output a Checklist ArrayList to an excel file
 * @param data ArrayList containing Checklist data
 * @param header String header to print above all other data
 * @param fileName Output File Name
 */
fun outputList(data: MutableCollection<String>, fileName: String, header: String) {
    val workBook = HSSFWorkbook()
    val sheet = workBook.createSheet(fileName)

    // Set the cell styles
    val style = workBook.createCellStyle()
    val font = workBook.createFont()
    font.fontHeightInPoints = 11
    font.fontName = "Calibri"
    style.setFont(font)

    var rowNum = 0

    // Output file header
    val headerRow = sheet.createRow(rowNum++)
    var headerCellNum = 0
    header.split(",").forEach { i ->
        val cell = headerRow.createCell(headerCellNum++)
        cell.setCellValue(i)
        cell.setCellStyle(style)
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

    // Output file footer
    val time = LocalDateTime.now()
    val template = DateTimeFormatter.ofPattern("HH:mm:ss")

    for (i in 0..1) {
        if (i == 0) {
            val footerRow = sheet.createRow(++rowNum)
            footerRow.createCell(0).setCellValue("Number of Containers:")
            footerRow.createCell(1).setCellValue(data.size.toString())
            footerRow.forEach { it.cellStyle = style }
        } else {
            val footerRow = sheet.createRow(++rowNum)
            footerRow.setRowStyle(style)
            footerRow.createCell(0).setCellValue("Generated at:")
            footerRow.createCell(1).setCellValue(time.format(template))
            footerRow.forEach { it.cellStyle = style }
        }
    }

    // Resize column 0
    sheet.autoSizeColumn(0)

    // Save the file
    val outputStream = FileOutputStream(fileName)
    workBook.write(outputStream)
    workBook.close()
    outputStream.close()
}