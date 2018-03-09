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

import com.src.checklist.Utility;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

/**
 * @author Manpreet Singh (2854787)
 *         FedEx Smartport SEWA/5983
 */
public class ConfigManager {

    private Map<String, ConfigParser> mConfigMap;
    private UCRParser mUCRData;
    private Set<String> mConfigFileNames;
    private Properties mInputProperties;
    private Properties mOutputProperties;
    private FileOutputStream mFileOutput;

    private String mInputFileName;

    public ConfigManager(String inputFileName) throws Exception {
        mInputFileName = inputFileName;

        mUCRData = new UCRParser();

        mConfigMap = new TreeMap<>();
        mConfigFileNames = new TreeSet<>();

        mInputProperties = new Properties();
        mOutputProperties = new Properties();

        InputStream fileInput;

        try {
            fileInput = new FileInputStream(inputFileName);
            mInputProperties.load(fileInput);
        } catch (FileNotFoundException e) {
            Utility.makeFile(inputFileName);
            saveCurrentConfig();
            throw new FileNotFoundException(e.getLocalizedMessage());
        }

        try {
            mConfigFileNames.addAll(Arrays.asList(mInputProperties.getProperty("config_filenames").split(",")));
        } catch (NullPointerException e) {
            saveCurrentConfig();
            throw new NullPointerException("Config File Empty");
        }

        for (String s : mConfigFileNames) {
            if (!s.isEmpty()) {
                try {
                    mConfigMap.put(s.split("\\.")[0], new ConfigParser(s));
                }
                catch (Exception e) {
                    e.printStackTrace();
                    throw new Exception("Unable to open config file: " + s);
                }
            }
        }
        fileInput.close();
    }

    public Set<String> getKeys() throws Exception {
        if (!this.mConfigMap.isEmpty())
            return this.mConfigMap.keySet();
        throw new Exception("No destination config files found");
    }

    public ConfigParser getValue(String key) {
        return this.mConfigMap.get(key);
    }

    public void addNewConfig(String configFileName) {
        mConfigFileNames.add(configFileName);
    }

    public void saveCurrentConfig() throws Exception {
        mFileOutput = new FileOutputStream(this.mInputFileName);

        System.out.println("Saving current Config");

        StringBuilder sb = new StringBuilder();

        for (String s : mConfigFileNames) {
            if (!s.isEmpty())
                sb.append(s).append(",");
        }

        this.mOutputProperties.setProperty("config_filenames",
                sb.toString().length() < 2 ? "" : sb.toString());
        this.mOutputProperties.store(this.mFileOutput,null);

        mFileOutput.close();
    }
}
