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

package com.src.gui;

import com.src.config.ConfigManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 *
 * @author Manpreet Singh (2854787)
 *         FedEx Smartport SEWA/5983
 */
public class ConfigSetupGUI {

    private final int   WINDOW_HEIGHT = 150;
    private final int   WINDOW_WIDTH  = 250;

    private String      mNewFileName;

    private Stage       mStage;

    private GridPane    mGridPane;
    private Button      mSubmitButton;

    private Button      mChooseInputFile;

    private FileChooser mFileChooser;

    private TextField   mFileName;
    private TextField   mOutputName;
    private TextField   mDestinationTag;

    private Properties  mProperties;

    private OutputStream mOutput;

    /**
     * Configuration Setup GUI
     * This Window allows the user to add a now configuration to the existing configuration
     */
    public ConfigSetupGUI() {
        mStage = new Stage();

        mFileChooser = new FileChooser();
        mFileChooser.setTitle("Input file ...");

        mSubmitButton = new Button("Submit");
        mChooseInputFile = new Button("Choose file...");

        mFileName = new TextField();
        mFileName.setEditable(false);
        mFileName.setPromptText("Input File Path");

        mOutputName = new TextField();
        mOutputName.setEditable(true);
        mOutputName.setPromptText("Enter output file name");

        mDestinationTag = new TextField();
        mDestinationTag.setEditable(true);
        mDestinationTag.setPromptText("Sort Group name");

        mGridPane = new GridPane();
        mGridPane.setAlignment(Pos.TOP_LEFT);
        mGridPane.setHgap(10);
        mGridPane.setVgap(10);
        mGridPane.setPadding(new Insets(5,5,5,5));

        mGridPane.add(mFileName, 0, 0);
        mGridPane.add(mChooseInputFile, 1, 0);
        mGridPane.add(mOutputName, 0, 1);
        mGridPane.add(mDestinationTag, 0, 2);

        mGridPane.add(mSubmitButton, 1, 4);

        Scene scene = new Scene(mGridPane, WINDOW_WIDTH, WINDOW_HEIGHT);

        mStage.setScene(scene);
        mStage.setResizable(false);
        mStage.setTitle("Add new config");

        mProperties = new Properties();
    }

    /**
     * Get the name of the new Properties file
     * @return String containing the name of the file including the file extention
     * @throws Exception There was no file name specified
     */
    public String getNewFileName() throws Exception{
        if (!this.mFileName.getText().isEmpty())
            return mNewFileName + ".properties";
        throw new Exception("No File name specified");
    }

    /**
     * Assign action to different GUI elements
     * @param manager Configuration Manager being used to track and store changes
     * @throws Exception
     */
    private void eventHandler(ConfigManager manager) throws Exception {

        /*
        Allow the user to choose an input file via a File Chooser
         */
        mChooseInputFile.setOnAction(event -> {
            File file = mFileChooser.showOpenDialog(this.mStage);
            if (file != null) {
                mFileName.setText(file.getPath());
            } else {
                throw new IllegalArgumentException("Illegal File Name");
            }
        });

        /*
        Allow the user to submit the entered properties to a properties file if all of the data is filled out
         */
        mSubmitButton.setOnAction(event -> {
            // Save all the information to a config file if it isn't empty
            if (mOutputName.getText().isEmpty() || mDestinationTag.getText().isEmpty())
                throw new IllegalArgumentException("All Information not filled in");
            else {
                // Try to parse and save data
                try {
                    mNewFileName = mDestinationTag.getText();
                    if (mNewFileName.contains(","))
                        mNewFileName = mNewFileName.replaceAll(",", "-");

                    mOutput = new FileOutputStream("dependencies/" + mNewFileName + ".properties");

                    mProperties.setProperty("input_file", mFileName.getText());
                    mProperties.setProperty("output_file", mOutputName.getText() + ".csv");

                    mProperties.setProperty("destination_tag", mNewFileName);

                    mProperties.store(mOutput, null);
                } catch (IOException e) {
                    try {
                        // We Catch it and We throw it ... let someone else deal with it
                        e.printStackTrace();
                        throw e;
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                finally { // if we haven't crashed, do all of this
                    if (mOutput != null) {
                        try {
                            mOutput.close();
                            // if everything checks out commit this new config into the Configuration Manager
                            manager.addNewConfig(this.getNewFileName());
                            manager.saveCurrentConfig();

                            // Clear everything once it's been saved
                            mDestinationTag.clear();
                            mFileName.clear();
                            mOutputName.clear();
                        } catch (Exception e) {
                            try {
                                throw new Exception(e.getMessage());
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                        mStage.close(); // Close it up
                    }
                }

            }
        });
    }

    /**
     * Run this Stage
     * @param manager Config Manager to be used during execution
     * @throws Exception New config Data was not entered properly
     */
    public void run(ConfigManager manager) throws Exception {
        eventHandler(manager);
        mStage.show();
    }
}
