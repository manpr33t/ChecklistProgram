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

    private SimpleStringProperty mInputFileName;
    private SimpleStringProperty mOutputFileName;
    private SimpleStringProperty mDestinationTitle;

    public ConfigParser(String fileName) throws Exception{

        mConfigReader = new Properties();

        mConfigReader.load(new FileInputStream("dependencies/" + fileName));

        this.mInputFileName = new SimpleStringProperty(this.mConfigReader.getProperty("input_file"));
        this.mOutputFileName = new SimpleStringProperty(this.mConfigReader.getProperty("output_file"));
        this.mDestinationTitle = new SimpleStringProperty(this.mConfigReader.getProperty("destination_tag"));

        if (this.mDestinationTitle.get().split(",").length > 0) {
            this.mMultipleRoutes = true;
            this.mMultipleRouteCodes = this.mDestinationTitle.get().split("-");
        }

        this.mDispatchCheckList = new Checklist(this.mInputFileName.get(), this.mOutputFileName.get(), this.mDestinationTitle.get());
        this.mConfigReader.clear();
    }

    public String getInputFileName() {
        return mInputFileName.get();
    }

    public String getOutputFileName() {
        return mOutputFileName.get();
    }

    public String getTitle() {
        return mDestinationTitle.get();
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
}
