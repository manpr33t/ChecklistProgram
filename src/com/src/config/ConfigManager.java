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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.awt.*;
import java.io.*;
import java.util.*;

import static javafx.collections.FXCollections.observableMap;

/**
 * @author Manpreet Singh (2854787)
 *         FedEx Smartport SEWA/5983
 */
public class ConfigManager {

    private ObservableMap<String, ConfigParser> mConfigMap;
    private UCRParser mUCRData;
    private Set<String> mConfigFileNames;
    private Properties mOutputProperties;

    private String mInputFileName;

    public ConfigManager(String inputFileName) throws Exception {
        mInputFileName = inputFileName;

        mUCRData = new UCRParser();

        mConfigMap = observableMap(new TreeMap<>());
        mConfigFileNames = new TreeSet<>();

        loadConfigFiles();

        for (String s : mConfigFileNames) {
            if (!s.isEmpty()) {
                try {
                    mConfigMap.put(s.split("\\.")[0].split("-")[0], new ConfigParser(s));
                }
                catch (Exception e) {
                    e.printStackTrace();
                    throw new Exception("Unable to open config file: " + s);
                }
            }
        }
    }

    public Set<String> getKeys() throws Exception {
            return this.mConfigMap.keySet();
    }

    public ConfigParser getValue(String key) {
        return this.mConfigMap.get(key);
    }

    public void updateKey(String prevKey, String newKey) {
        this.mConfigMap.put(newKey, this.mConfigMap.get(prevKey));
        this.mConfigMap.remove(prevKey);
    }

    public void addNewConfig(String configFileName) {
        mConfigFileNames.add(configFileName);
    }

    public void saveCurrentConfig() throws Exception {
        FileOutputStream fileOutputStream = new FileOutputStream(this.mInputFileName);

        System.out.println("Saving current Config");

        StringBuilder sb = new StringBuilder();

        for (String s : mConfigFileNames) {
            if (!s.isEmpty())
                sb.append(s).append(",");
        }

        this.mOutputProperties.setProperty("config_filenames",
                sb.toString().length() < 2 ? "" : sb.toString());
        this.mOutputProperties.store(fileOutputStream,null);

        fileOutputStream.close();
    }

    public void parseUCR(File inputFile, Desktop desktop) throws Exception {
        mUCRData.readDataFromFile(inputFile);
        for (String s : mUCRData.getDataMap().keySet())
            try {
                if (mConfigMap.containsKey(s)) {
                    if (mConfigMap.get(s).isMultipleRoutes())
                        for (int i = 0; i < mConfigMap.get(s).getMultipleRoute().length; i++) {
                            Set<String> temp = mUCRData.getDataMap().get(mConfigMap.get(s).getMultipleRoute()[i]);
                            if (temp != null)
                                mUCRData.getDataMap().get(s).addAll(temp);
                        }
                    mConfigMap.get(s).run(mUCRData.getDataMap().get(s));
                }
            } catch (Exception e) {
                throw e;
            }

        openOutputFiles(desktop);
    }

    public ObservableList<Map> getObservableMap(String[] columnKeys) {
        ObservableList<Map> mapData = FXCollections.observableArrayList();
        if (columnKeys.length == 3) {
            for (String s : mConfigMap.keySet()) {
                Map<String, String> dataRow = new HashMap<>();
                String temp = "";
                if (mConfigMap.get(s).isMultipleRoutes()) {
                    for (String k : mConfigMap.get(s).getMultipleRoute()) {
                        temp += k;
                        temp += ",";
                    }
                    temp = temp.substring(0, temp.length()-1);
                } else {
                    temp = mConfigMap.get(s).getTitle();
                }
                dataRow.put(columnKeys[0], mConfigMap.get(s).getOutputFileName());
                dataRow.put(columnKeys[1], temp);
                dataRow.put(columnKeys[2], mConfigMap.get(s).getInputFileName());
                mapData.add(dataRow);
            }
        }
        return  mapData;
    }

    private void openOutputFiles(Desktop desktop) throws IOException {
        for (String s : mConfigMap.keySet()) {
            desktop.open(new File(mConfigMap.get(s).getOutputFileName()));
        }
    }

    private void loadConfigFiles() throws Exception {
        Properties properties = new Properties();
        mOutputProperties = new Properties();

        InputStream fileInput;

        try {
            fileInput = new FileInputStream(mInputFileName);
            properties.load(fileInput);
        } catch (FileNotFoundException e) {
            Utility.makeFile(mInputFileName);
            saveCurrentConfig();
            throw new FileNotFoundException(e.getLocalizedMessage());
        }

        try {
            mConfigFileNames.addAll(Arrays.asList(properties.getProperty("config_filenames").split(",")));
        } catch (NullPointerException e) {
            saveCurrentConfig();
            throw new NullPointerException("Config File Empty");
        }

        fileInput.close();
    }
}
