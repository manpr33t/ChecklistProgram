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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * The Class to setup/generate and run the Checklist Classes for all the different Destinations.
 * @author Manpreet Singh (2854787)
 *         FedEx Smartport SEWA/5983
 */
public class Parser {

    private String mFileName;

    // Containers for later use.
    private Collection<String> mSherwoodList    = new ArrayList<>();
    private Collection<String> mSpokaneList     = new ArrayList<>();
    private Collection<String> mSpringfieldList = new ArrayList<>();
    private Collection<String> mKennewickList   = new ArrayList<>();
    private Collection<String> mLocalList       = new ArrayList<>();
    private Collection<String> mLVGBagsList     = new ArrayList<>();

    // Not used yet
    private Collection<String> mAlaskaList      = new ArrayList<>();

    // Checklist Objects
    private Checklist   mSherwoodDispatchList,
                        mSpokaneDispatchList,
                        mSpringfieldDispatchList,
                        mKennewickDispatchList,
                        mLocalDispatchList,
                        mLVGDispatchList;

    // Enumeration to Contain destination codes.
    public enum Destination {
        USHE01("USHE01"),
        USPO01("USPO01"),
        USPR01("USPR01"),
        UKEN01("UKEN01"),
        SASW10("SASW10"),
        SANE20("SANE20"),
        PORT01("PORT01"),
        YAKI01("YAKI01"),
        TACO01("TACO01"),
        ZANC01("ZANC01"),
        ALAS01("ALAS01");

        private final String value;

        Destination(String value) {this.value = value;}
        public String getValue() {return this.value;}
    }

    /**
     * This Class Goes through the file that is passed into this constructor and filters the data based on Checklists.
     * @param fileName The filepath of the file to be filtered.
     */
    public Parser(String fileName) throws ArrayIndexOutOfBoundsException{
        this.mFileName = fileName;

        this.mSherwoodDispatchList = new Checklist(
                "checklists/SherwoodDispatchChecklistMASTER.csv",
                "SherwoodDispatchList.csv", "Sherwood");

        this.mSpokaneDispatchList = new Checklist(
                "checklists/SpokaneDispatchCheckListMASTER.csv",
                "SpokaneDispatchList.csv", "Spokane");

        this.mSpringfieldDispatchList = new Checklist(
                "checklists/SpringfieldDispatchCheckListMASTER.csv",
                "SpringfieldDispatchCheckList.csv", "Springfield");

        this.mKennewickDispatchList = new Checklist(
                "checklists/KennewickDispatchCheckListMASTER.csv",
                "KennewickDispatchCheckList.csv", "Kennewick");

        this.mLocalDispatchList = new Checklist(
                "checklists/LocalsDispatchCheckListMASTER.csv",
                "LocalsDispatchCheckList.csv", "Locals");

        this.mLVGDispatchList = new Checklist(
                "checklists/LVDDispatchChecklistMASTER.csv",
                "LVGDispatchChecklist.csv", "LVG");
    }

    public void run() throws IOException{
        // Sort the lines in the file into different groups.
        parseUCR(this.mFileName);

        // Generate final Cut lists based on the sorted data.
        // And output the files
        mSherwoodDispatchList.generateDifference(mSherwoodList);
        mSpokaneDispatchList.generateDifference(mSpokaneList);
        mKennewickDispatchList.generateDifference(mKennewickList);
        mSpringfieldDispatchList.generateDifference(mSpringfieldList);
        mLocalDispatchList.generateDifference(mLocalList);
        mLVGDispatchList.generateDifference(mLVGBagsList);

        // Prep data structures for next run
        prepForNextRun();
    }

    /**
     * Clear all data pertaining to this current run of the Checklist Program
     */
    private void prepForNextRun() {
        this.mSherwoodList.clear();
        this.mSpokaneList.clear();
        this.mKennewickList.clear();
        this.mSpringfieldList.clear();
        this.mLocalList.clear();
    }

    /**
     * Function to go through the UCR file and filter each line into different Lists based on their Destination codes.
     * @param fileName Filename of the File to parse and filter.
     * @throws IOException File not found or Read/Write Error.
     */
    private void parseUCR(String fileName) throws IOException, IndexOutOfBoundsException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            // Go through the file line by line and copy the current line into the variable line
            for (String line; (line = br.readLine()) != null;) {
                // Split up the line by commas and put into an array
                String[] lineBuffer = line.split(",");

                // Sort only the zipcodes from the lines based on their destination codes
                int size = lineBuffer[8].length(); // This line is for debugging purposes.
                if(size > 5 && Character.isLetter(lineBuffer[8].charAt(0))) {
                    switch (Destination.valueOf(lineBuffer[8].substring(4))) {
                        default:
                            break;
                        case USHE01:
                            mSherwoodList.add(Utility.removeZipcodePrefix(lineBuffer[0]));
                            break;
                        case USPO01:
                            mSpokaneList.add(Utility.removeZipcodePrefix(lineBuffer[0]));
                            break;
                        case USPR01:
                            mSpringfieldList.add(Utility.removeZipcodePrefix(lineBuffer[0]));
                            break;
                        case UKEN01:
                            mKennewickList.add(Utility.removeZipcodePrefix(lineBuffer[0]));
                            break;
                        case SASW10:
                            mLocalList.add(Utility.removeZipcodePrefix(lineBuffer[0]));
                            break;
                        case SANE20:
                            mLocalList.add(Utility.removeZipcodePrefix(lineBuffer[0]));
                            break;
                        case PORT01:
                            mLVGBagsList.add(Utility.removeZipcodePrefix(lineBuffer[0]));
                            break;
                        case YAKI01:
                            mLVGBagsList.add(Utility.removeZipcodePrefix(lineBuffer[0]));
                            break;
                        case TACO01:
                            mLVGBagsList.add(Utility.removeZipcodePrefix(lineBuffer[0]));
                            break;
                        case ZANC01:
                            mAlaskaList.add(Utility.removeZipcodePrefix(lineBuffer[0]));
                            break;
                        case ALAS01:
                            mAlaskaList.add(Utility.removeZipcodePrefix(lineBuffer[0]));
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
