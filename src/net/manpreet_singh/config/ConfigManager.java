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

package net.manpreet_singh.config;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.stage.Stage;
import net.manpreet_singh.checklist.Utility;
import net.manpreet_singh.gui.ErrorMessagesKt;
import net.manpreet_singh.gui.MultipleFiles;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

import static javafx.collections.FXCollections.observableMap;

/**
 * @author Manpreet Singh (2854787)
 *         FedEx Smartport SEWA/5983
 */
public class ConfigManager {

    private ObservableMap<String, ConfigParser> mConfigMap;

    private UCRParser   mUCRData;
    private Set<String> mConfigFileNames;
    private Properties  mOutputProperties;
    private String      mInputFileName;

    /**
     * Configuration Manager
     * This Class manages multiple config files and keeps track of them with a master config file
     * @param inputFileName Master config file to run this ConfigManager with
     * @throws Exception Unable to use the specified config file
     */
    public ConfigManager(String inputFileName) throws Exception {
        mInputFileName = inputFileName;

        mUCRData = new UCRParser();

        mConfigMap = observableMap(new TreeMap<>());
        mConfigFileNames = new TreeSet<>();

        loadConfigFiles();


    }

    /**
     * Get the ket set of this data map
     * @return Set of Keys for the data map
     */
    public Set<String> getKeys() {
            return this.mConfigMap.keySet();
    }

    /**
     * Get ConfigParse Object associated with a key
     * @param key The key to get the value
     * @return Key not in data map key set
     */
    public ConfigParser getValue(String key) {
        return this.mConfigMap.get(key);
    }

    /**
     * Replace a key in the data map
     * @param prevKey The key to replace
     * @param newKey The new key
     */
    public void updateKey(String prevKey, String newKey) throws Exception {
        if (!prevKey.equals(newKey)) {
            this.mConfigMap.put(newKey, this.mConfigMap.get(prevKey));
            this.mConfigMap.get(prevKey).deleteConfigFile(prevKey);
            this.mConfigMap.remove(prevKey);

            this.mConfigFileNames.remove(prevKey + ".properties");
            this.mConfigFileNames.add(newKey + ".properties");

            this.mConfigMap.get(newKey).setTitle(newKey);
        } else  {
            System.err.println("ERROR");
            throw new IllegalStateException("The Previous Key is the same and the New Key");
        }
    }

    /**
     * Add a new config file to the list of config files
     * @param configFileName Name of the new Config File
     */
    public void addNewConfig(String configFileName) {
        mConfigFileNames.add(configFileName);
    }

    /**
     * Remove a config file from the list of available configs
     * @param configName Name of the Config File to remove
     */
    public void removeConfig(String configName) throws Exception{
        String filePath = String.format("dependencies/%s.properties", configName);
        mConfigFileNames.remove(configName+".properties");
        if (!Utility.deleteFile(filePath))
            throw new Exception("Unable to delete selected item");
        mConfigMap.remove(configName);
        System.out.println("removed " + configName);
    }

    /**
     * Save the current master config file
     * @throws Exception Unable to save file
     */
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

    /**
     * Parse the UCR and pull all mapped data that is going to be used
     * @param inputFile Input UCR Excel file
     * @param desktop Desktop to open the files once this operation is done
     * @throws Exception Error with Input File or Desktop
     */
    public void parseUCR(File inputFile, Desktop desktop) throws Exception {
        mUCRData.readDataFromFile(inputFile);
        assert mUCRData.getDataMap() != null;
        for (String s : mConfigMap.keySet()) {
            if (mConfigMap.get(s).isMultipleRoutes()) {
                Set<String> temp = new HashSet<>();
                for (String i : mConfigMap.get(s).getMultipleRoute()) {
                    if (mUCRData.getDataMap().containsKey(i))
                        temp.addAll(mUCRData.getDataMap().get(i));
                }
                mConfigMap.get(s).run(temp);
            } else {
                mConfigMap.get(s).run(mUCRData.getDataMap().get(s));
            }
        }
        openOutputFiles(desktop);
    }

    public List<File> parseUCRData(Stage stage, Desktop desktop) throws Exception {
        MultipleFiles mpf = new MultipleFiles();
        Map<String, Set<String>> tempDataMap = mpf.run(stage);
        if (tempDataMap == null) {
            ErrorMessagesKt.message("No Files Provided");
            return null;
        }
        for (String s : mConfigMap.keySet()) {
            if (mConfigMap.get(s).isMultipleRoutes()) {
                Set<String> tempSet = new HashSet<>();
                for (String i : mConfigMap.get(s).getMultipleRoute()) {
                    if (tempDataMap.containsKey(i))
                        tempSet.addAll(tempDataMap.get(i));
                }
                mConfigMap.get(s).run(tempSet);
            } else {

                mConfigMap.get(s).run(tempDataMap.get(s));
            }
        }
        openOutputFiles(desktop);
        return mpf.getFiles();
    }

    /**
     * Get an Observable List of the data map to the GUI
     * @param columnKeys Column keys to map the data to
     * @return Observable List containing mapped data
     */
    public ObservableList<Map> getObservableMap(String[] columnKeys) {
        ObservableList<Map> mapData = FXCollections.observableArrayList();
        if (columnKeys.length == 3) {
            for (String s : mConfigMap.keySet()) {
                Map<String, String> dataRow = new HashMap<>();
                StringBuilder temp = new StringBuilder();
                if (mConfigMap.get(s).isMultipleRoutes()) {
                    for (String k : mConfigMap.get(s).getMultipleRoute()) {
                        temp.append(k);
                        temp.append(",");
                    }
                    temp = new StringBuilder(temp.substring(0, temp.length() - 1));
                } else {
                    temp = Optional.ofNullable(mConfigMap.get(s).getTitle()).map(StringBuilder::new).orElse(null);
                }
                dataRow.put(columnKeys[0], mConfigMap.get(s).getOutputFileName());
                dataRow.put(columnKeys[1], temp == null ? null : temp.toString());
                dataRow.put(columnKeys[2], mConfigMap.get(s).getInputFileName());
                mapData.add(dataRow);
            }
        }
        return  mapData;
    }

    public void updateData(ObservableList<Map> map, String[] columnKeys) throws Exception {
        for (Map m : map) {
            if (m.get(columnKeys[1]).toString().contains(",")) {
                mConfigMap.get(m.get(columnKeys[1]).toString().replaceAll(",", "-")).setOutputFileName(m.get(columnKeys[0]).toString());
                mConfigMap.get(m.get(columnKeys[1]).toString().replaceAll(",", "-")).setInputFileName(m.get(columnKeys[2]).toString());
            }
            else {
                mConfigMap.get(m.get(columnKeys[1])).setOutputFileName(m.get(columnKeys[0]).toString());
                mConfigMap.get(m.get(columnKeys[1])).setInputFileName(m.get(columnKeys[2]).toString());
            }
        }
        System.out.println("\n" + mConfigMap);
        for (String s : mConfigMap.keySet()) {
            mConfigMap.get(s).save();
        }
    }

    /**
     * Open the output files
     * @param desktop Desktop to open the files on
     * @throws IOException Unable to open file
     */
    private void openOutputFiles(Desktop desktop) throws IOException {
        for (String s : mConfigMap.keySet()) {
            desktop.open(new File(mConfigMap.get(s).getOutputFileName()+".xls"));
        }
    }

    /**
     * Load Config files from the list of Config files
     * @throws Exception Unable to load Config Files
     */
    public void loadConfigFiles() throws Exception {
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
        System.out.println("Finished loading Config Files");
    }

    public void reloadConfig() throws Exception{
        saveCurrentConfig();
        loadConfigFiles();
    }
}
