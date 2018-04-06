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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

    /**
     * Properties file parser Object to pull data from a correctly formatted Properties file
     * @param fileName Properties File to pull data from
     * @throws Exception Properties File doesn't contain the information
     */
    public ConfigParser(String fileName) throws Exception{

        mConfigReader = new Properties();

        mConfigReader.load(new FileInputStream("dependencies/" + fileName));

        this.mInputFileName = this.mConfigReader.getProperty("input_file");
        this.mOutputFileName = this.mConfigReader.getProperty("output_file");
        this.mDestinationTitle = this.mConfigReader.getProperty("destination_tag");

        if (this.mDestinationTitle.split("-").length > 1) {
            this.mMultipleRoutes = true;
            this.mMultipleRouteCodes = this.mDestinationTitle.split("-");
        }

        this.mDispatchCheckList = new Checklist(this.mInputFileName, this.mOutputFileName, this.mDestinationTitle);
        this.mConfigReader.clear();
    }

    /**
     * Get the Input file name
     * @return Input file name
     */
    public String getInputFileName() {
        return mInputFileName;
    }

    /**
     * Get the Output file name of this config
     * @return Output file name
     */
    public String getOutputFileName() {
        return mOutputFileName;
    }

    /**
     * Get the Destination title of this Config
     * @return Destination title
     */
    public String getTitle() {
        return mDestinationTitle;
    }

    /**
     * Change the Input file name
     * @param s Input file name
     */
    public void setInputFileName(String s) {
        this.mInputFileName = s;
    }

    /**
     * Set the Output file name
     * @param s Output file name
     */
    public void setOutputFileName(String s) {
        this.mOutputFileName = s;
    }

    /**
     * Set the Destination Title
     * @param s Title to change the Destination Title to
     */
    public void setTitle(String s) {
        this.mDestinationTitle = s;
    }

    /**
     * Check if this Config has multiple route codes in its title or not
     * @return True if it does, false otherwise
     */
    public boolean isMultipleRoutes() {
        return this.mMultipleRoutes;
    }

    /**
     * Get the multiple routes if this title if they exist
     * @return String Array with different Route codes
     */
    public String[] getMultipleRoute() {
        return this.mMultipleRouteCodes;
    }

    /**
     * Run the checklist with the given input Collection
     * @param collection Collection of Strings containing Sort Destination Codes
     * @throws Exception
     */
    public void run(Collection<String> collection) throws Exception {
        mDispatchCheckList.generateDifference(collection);
    }

    public void deleteConfigFile(String key) throws Exception{
        File f = new File("dependencies/" + key + ".properties");
        if (!f.delete())
            throw new Exception("Unable to update");
    }

    /**
     * Save the Properties file
     */
    public void save() throws Exception{
        FileOutputStream fileOutput;
        try {
            fileOutput = new FileOutputStream("dependencies/" + this.mDestinationTitle + ".properties");
        } catch (Exception e) {
            new File("dependencies/" + this.mDestinationTitle + ".properties").delete();
        } finally {
            fileOutput = new FileOutputStream("dependencies/" + this.mDestinationTitle + ".properties");
        }

        Properties p = new Properties();

        p.setProperty("input_file", this.mInputFileName);
        p.setProperty("output_file", this.mOutputFileName);
        p.setProperty("destination_tag", this.mDestinationTitle);

        p.store(fileOutput,null);
        fileOutput.close();
    }
}
