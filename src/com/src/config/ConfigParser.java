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

    private Collection<String> mSortGroupsList;

    private String mMasterCheckListFileName;
    private String mOutputFileName;
    private String mDestinationTitle;

    private Checklist mDispatchCheckList;

//    private Properties mProp;
//    public ConfigParser() {
//        mProp = new Properties();
//        try {
//            oi = new FileOutputStream("dependencies/main_config.properties");
//            mProp.setProperty("route_config_filenames","sherwood.properties,spokane.properties,kennewick.properties,springfield.properties,locals.properties");
//
//            mProp.store(oi, null);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (oi != null) {
//                try {
//                    oi.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    public static void main(String[] args) {
//        ConfigParser cp = new ConfigParser();
//        Properties rProp = new Properties();
//        try {
//            InputStream in = new FileInputStream("dependencies/main_config.properties");
//            rProp.load(in);
//            for (String s : rProp.getProperty("route_config_filenames").split(","))
//                System.out.println(s);
//        } catch (Exception e ) {
//            e.printStackTrace();
//        }
//    }

    public ConfigParser(String fileName) throws Exception{
        mSortGroupsList = new TreeSet<>();
        mConfigReader.load(new FileInputStream(fileName));
    }


}
