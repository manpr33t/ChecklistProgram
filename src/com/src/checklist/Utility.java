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

package com.src.checklist;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This Class holds static Utility methods that get used in other files.
 * @author Manpreet Singh (2854787)
 *         FedEx Smartport SEWA/5983
 */
public class Utility {
    // Data structure to hold data on Cells inside of Excel file
    private static List<List<HSSFCell>> cellGrid;

    /**
     * Convert the Input file into a CSV file and save the output
     * @param inputFile Name or Path of the file you want to conver to CSV
     * @throws IOException File not found or File is open in another program
     */
    public static void convertExcelToCsv(File inputFile, String outputName) throws IOException {

        try {
            cellGrid = new ArrayList<>();
            POIFSFileSystem myFileSystem = new POIFSFileSystem(inputFile);
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);
            Iterator<?> rowIter = mySheet.rowIterator();
            // Go through the file
            while (rowIter.hasNext()) {
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator<?> cellIter = myRow.cellIterator();
                List<HSSFCell> cellRowList = new ArrayList<>();
                // Go cell by cell of the file
                while (cellIter.hasNext()) {
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    cellRowList.add(myCell);
                }
                // Add it to the cellGrid buffer
                cellGrid.add(cellRowList);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new IOException("File Not Found or Unable to open file");
        }

        // Save file for future reference
        File file = new File(outputName);
        PrintStream stream = new PrintStream(file);
        for (List<HSSFCell> cellRowList : cellGrid) {
            cellRowList.stream().map(HSSFCell::toString).map(stringCellValue -> stringCellValue + ",").forEach(stream::print);
            stream.println("");
        }
        stream.close();
    }

    /**
     * Remove the DDU and P5D prefixes from Sort Group identifications
     * @param s String to change
     * @return A string without the DDU or P5D prefix
     */
    public static String removeZipcodePrefix(String s) {
        if (s.charAt(0) != 'P' && s.charAt(0) != 'D')
            return s;
        return s.substring(4);
    }

    public static void makeFile(String fileName) throws IOException{
        File f = new File(fileName);
        f.getParentFile().mkdirs();
        f.createNewFile();
    }
}
