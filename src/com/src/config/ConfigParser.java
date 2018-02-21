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

package com.src.config;

import com.src.checklist.Checklist;

import java.io.*;
import java.util.Collection;
import java.util.Properties;
import java.util.TreeSet;

/**
 * @author Manpreet Singh (2854787)
 *         FedEx Smartport SEWA/5983
 */
public class ConfigParser {
    private Properties mConfigReader;

    private static Collection<String> kSortGroupsList;

    private String mInputFileName;
    private String      mOutputFileName;
    private String      mDestinationTitle;

    private Checklist   mDispatchCheckList;

    private boolean     mValuesUpdated = false;

    public ConfigParser(String fileName) throws Exception{
        if (kSortGroupsList == null)
            kSortGroupsList = new TreeSet<>();

        mConfigReader.load(new FileInputStream(fileName));

        this.mInputFileName = this.mConfigReader.getProperty("input_file");
        this.mOutputFileName = this.mConfigReader.getProperty("output_file");
        this.mDestinationTitle = this.mConfigReader.getProperty("destination_tag");

        kSortGroupsList.add(this.mDestinationTitle);
    }

    public String getInputFileName() {
        return mInputFileName;
    }

    public String getOutputFileName() {
        return mOutputFileName;
    }

    public String getTitle() {
        return mDestinationTitle;
    }
}
