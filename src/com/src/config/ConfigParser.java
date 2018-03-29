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
import javafx.beans.property.SimpleStringProperty;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.Properties;

/**
 * @author Manpreet Singh (2854787)
 *         FedEx Smartport SEWA/5983
 */
public class ConfigParser {
    private Properties  mConfigReader;
    private boolean     mMultipleRoutes;
    private String[]    mMultipleRouteCodes;
    private Checklist   mDispatchCheckList;

    private String mInputFileName;
    private String mOutputFileName;
    private String mDestinationTitle;

    public ConfigParser(String fileName) throws Exception{

        mConfigReader = new Properties();

        mConfigReader.load(new FileInputStream("dependencies/" + fileName));

        this.mInputFileName = this.mConfigReader.getProperty("input_file");
        this.mOutputFileName = this.mConfigReader.getProperty("output_file");
        this.mDestinationTitle = this.mConfigReader.getProperty("destination_tag");

        if (this.mDestinationTitle.split(",").length > 0) {
            this.mMultipleRoutes = true;
            this.mMultipleRouteCodes = this.mDestinationTitle.split("-");
        }

        this.mDispatchCheckList = new Checklist(this.mInputFileName, this.mOutputFileName, this.mDestinationTitle);
        this.mConfigReader.clear();
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

    public void setInputFileName(String s) {
        this.mInputFileName = s;
    }

    public void setOutputFileName(String s) {
        this.mOutputFileName = s;
    }

    public void setTitle(String s) {
        this.mDestinationTitle = s;
    }

    public boolean isMultipleRoutes() {
        return this.mMultipleRoutes;
    }

    public String[] getMultipleRoute() {
        return this.mMultipleRouteCodes;
    }

    public void run(Collection<String> collection) throws Exception {
        mDispatchCheckList.generateDifference(collection);
    }

    public void save() {
        // TODO Write to a new Properties File
    }
}
