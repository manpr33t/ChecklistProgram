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

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.Properties;

/**
 *
 * @author Manpreet Singh (2854787)
 *         FedEx Smartport SEWA/5983
 */
public class ConfigSetupGUI {

    private final int WINDOW_HEIGHT = 215;
    private final int WINDOW_WIDTH = 325;

    private Stage mStage;

    private GridPane mGridPane;
    private Button mSubmitButton;

    private Button mChooseInputFile;

    private FileChooser mFileChooser;

    private TextField mFileName;
    private TextField mOutputName;
    private TextField mDestinationTag;

    private Properties mProperties;
    private OutputStream mOutput;

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
        mDestinationTag.setPromptText("Destination name");

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

        mStage.setResizable(false);

        Scene scene = new Scene(mGridPane, WINDOW_WIDTH, WINDOW_HEIGHT);

        mStage.setScene(scene);

        mProperties = new Properties();
    }

    private void eventHandler() throws Exception {
        mChooseInputFile.setOnAction(event -> {
            File file = mFileChooser.showOpenDialog(this.mStage);
            if (file != null) {
                mFileName.setText(file.getPath());
            } else {
                throw new IllegalArgumentException("Illegal File Name");
            }
        });

        mSubmitButton.setOnAction(event -> {
            // Save all the information to a config file
            if (mOutputName.getText().isEmpty() || mDestinationTag.getText().isEmpty())
                throw new IllegalArgumentException("All Information not filled in");
            else {
                try {
                    mOutput = new FileOutputStream("dependencies/" + mDestinationTag.getText() + ".properties");

                    mProperties.setProperty("input_file", mFileName.getText());
                    mProperties.setProperty("output_file", mOutputName.getText() + ".csv");
                    mProperties.setProperty("destination_tag", mDestinationTag.getText());

                    mProperties.store(mOutput, null);
                } catch (IOException e) { e.printStackTrace(); }
                finally {
                    if (mOutput != null) {
                        try {
                            mOutput.close();
                        } catch (IOException e) { e.printStackTrace(); }
                    }
                }

            }
        });
    }

    public void run() throws Exception {
        eventHandler();
        mStage.show();
    }
}
