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

import com.src.checklist.Utility;
import com.src.checklist.Parser;
import com.src.config.ConfigManager;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Graphical User Interface for the Checklist Generator Program
 * @author Manpreet Singh (2854787)
 *         FedEx Smartport SEWA/5983
 */
public class MainGUI extends Application{

    // Window Dimensions
    private final int WINDOW_HEIGHT = 300;
    private final int WINDOW_WIDTH = 350;

    private final String mLicence = "Copyright 2018 Manpreet Singh\n\n" +
            "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
            "you may not use this file except in compliance with the License.\n" +
            "You may obtain a copy of the License at\n\n" +

            "http://www.apache.org/licenses/LICENSE-2.0\n\n" +

            "Unless required by applicable law or agreed to in writing, software\n" +
            "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
            "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
            "See the License for the specific language governing permissions and\n" +
            "limitations under the License.";

    // GUI elements
    private TextArea    mLog;

    private FileChooser mFileChooser;

    private Button      mOpenFile;
    private Button      mGenerateFiles;
    private Button      mConfigButton;

    private Image       mConfigIcon;

    private GridPane    mGridPane;
    private Desktop     mDesktop = Desktop.getDesktop();
    private Parser      mParser;

    private Hyperlink   mName;
    private File        mDescription;

    private ConfigManager mConfig;
    private ConfigSetupGUI configSetupGUI;

    /**
     * GUI Built using JavaFX
     */
    public MainGUI() {
        // Set up log area
        mLog = new TextArea();

        mLog.setMinWidth(290);
        mLog.setMaxWidth(300);

        mLog.setMinHeight(50);
        mLog.setMaxHeight(100);

        mLog.setEditable(false);

        ScrollPane mLogScrollPane = new ScrollPane(mLog);
        mLogScrollPane.setStyle("-fx-background-color:transparent;");

        // Set up buttons
        mFileChooser = new FileChooser();
        mFileChooser.setTitle("Open UCR File ...");

        mOpenFile = new Button("Open Excel File");
        mOpenFile.setStyle("-fx-font: 14 arial; -fx-base: #AfffB9;");

        mGenerateFiles = new Button("Generate From File");
        mGenerateFiles.setStyle("-fx-font:14 arial; -fx-base: #C3FAFF;");

        mConfigButton = new Button();
        mConfigButton.setStyle("-fx-font:14 arial; -fx-base: #FF5100;");

        try {
            mConfigIcon = new Image(new FileInputStream("dependencies/gear.png"));
            mConfigButton.setGraphic(new ImageView(mConfigIcon));
        } catch (Exception e) {
            mConfigButton.setText("Config");
            e.printStackTrace();
        }

        mName = new Hyperlink("Manpreet Singh 2017-2018");
        mName.setBorder(Border.EMPTY);
        Tooltip.install(mName, new Tooltip(mLicence));

        Text mLocation = new Text("SEWA/5983");
        mLocation.setFill(Paint.valueOf("#A9A9A9"));

        mDescription = new File("dependencies/description.html");

        // Set up layout of program
        mGridPane = new GridPane();

        mGridPane = new GridPane();
        mGridPane.setAlignment(Pos.TOP_LEFT);

        mGridPane.setHgap(25);
        mGridPane.setVgap(25);
        mGridPane.setPadding(new Insets(10,10,10,10));

        mGridPane.add(mOpenFile, 0, 0);
        mGridPane.add(mGenerateFiles, 1,0);
        mGridPane.add(mLogScrollPane, 0, 1,3,1);
        mGridPane.add(mName, 1, 2);
        mGridPane.add(mLocation, 0, 2);
        mGridPane.add(mConfigButton, 2, 0);

        mParser = new Parser("UCR.csv");
        configSetupGUI = new ConfigSetupGUI();

        try {
            mConfig = new ConfigManager(new FileInputStream("dependencies/main_config.properties"));
        } catch (Exception e) {
            mLog.appendText(e.getLocalizedMessage());
            mLog.appendText("Generating empty config file\n");

        }
    }

    /**
     * Event handler to handel Buttons events
     * @param stage Stage object to read file information from
     */
    private void eventHandler(Stage stage) {
        mOpenFile.setOnAction(event -> {
            File file = mFileChooser.showOpenDialog(stage);
            if (file != null) {
                mLog.appendText("Opening file: " + file.getName() + "\n");
                try {
                    Utility.convertExcelToCsv(file);
                    mParser.run();
                    openChecklists();
                } catch (IOException e) {
                    mLog.appendText(e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        });

        mGenerateFiles.setOnAction(event -> {
            mLog.appendText("Generating checklists from saved UCR file\n");
            try {
                mParser.run();
                openChecklists();
            } catch (IOException e) {
                mLog.appendText(e.getMessage());
                e.printStackTrace();
            }
        });

        mName.setOnAction(event -> {
            mLog.appendText("Opening Licence\n");
            try {
                openFile(mDescription);
            } catch (IOException e) {
                mLog.appendText(e.getMessage() + "\n\n");
            }
        });

        mConfigButton.setOnAction(event -> {
            try {
                configSetupGUI.run();
            } catch (Exception e) {
                mLog.appendText(e.getLocalizedMessage());
            }
        });
    }

    /**
     * Open the specified file with the Default program on the current Desktop
     * @param file File to open
     * @throws IOException Either a file Read/Write error or file not found
     */
    private void openFile(File file) throws IOException {
            mDesktop.open(file);
    }

    /**
     * Open the generated checklists once they are generated
     * @throws IOException Read/Write error or Files not found
     */
    private void openChecklists() throws IOException{
        openFile(new File("SherwoodDispatchList.csv"));
        openFile(new File("SpokaneDispatchList.csv"));
        openFile(new File("SpringfieldDispatchCheckList.csv"));
        openFile(new File("KennewickDispatchCheckList.csv"));
        openFile(new File("LocalsDispatchCheckList.csv"));
        openFile(new File("LVGDispatchChecklist.csv"));
    }

    /**
     * Main loop of the program
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Checklist Generator");
        primaryStage.getIcons().add(new Image("file:dependencies/img.png"));
        primaryStage.setResizable(false);
        Scene scene = new Scene(mGridPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        eventHandler(primaryStage);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
