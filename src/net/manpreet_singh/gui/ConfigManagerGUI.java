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

package net.manpreet_singh.gui;

import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import net.manpreet_singh.config.ConfigManager;

import java.awt.*;
import java.io.File;
import java.util.Map;


/**
 * @author Manpreet Singh (2854787)
 *         FedEx Smartport SEWA/5983
 */
public class ConfigManagerGUI {

    private final int WINDOW_HEIGHT = 325;
    private final int WINDOW_WIDTH  = 465;

    private static final String[] COLUMN_KEYS = {"output", "title", "input" };

    private TableView                mDataTable;
    private TableColumn<Map, String> mOutputColumn;
    private TableColumn<Map, String> mTitleColumn;
    private TableColumn<Map, String> mInputColumn;

    private ObservableList<Map>      mAllData;

    private Button   mNewConfigButton;
    private Button   mDeleteSelected;

    private Stage    mStage;
    private GridPane mGridPane;

    private ConfigManager mConfigManager;

    /**
     * Configuration Manager GUI to edit and push data to the ConfigManager Object
     * @param configManager ConfigManager Object to push data to
     */
    public ConfigManagerGUI(ConfigManager configManager) {
        mStage = new Stage();
        mStage.initModality(Modality.WINDOW_MODAL);

        mConfigManager = configManager;

        mOutputColumn = new TableColumn<>("Output");
        mTitleColumn  = new TableColumn<>("Sort Groups");
        mInputColumn  = new TableColumn<>("Input");

        mOutputColumn.setCellValueFactory(new MapValueFactory<>(COLUMN_KEYS[0]));
        mTitleColumn.setCellValueFactory(new MapValueFactory<>(COLUMN_KEYS[1]));
        mInputColumn.setCellValueFactory(new MapValueFactory<>(COLUMN_KEYS[2]));

        mOutputColumn.setMinWidth(150);
        mTitleColumn.setMinWidth(150);
        mInputColumn.setMinWidth(150);

        mAllData = mConfigManager.getObservableMap(COLUMN_KEYS);
        mDataTable = new TableView<>(mAllData);
        mDataTable.setEditable(true);
        mDataTable.setMaxWidth(500);
        mDataTable.setMaxHeight(280);
        mDataTable.getSelectionModel().setCellSelectionEnabled(true);
        mDataTable.getColumns().setAll(mOutputColumn, mTitleColumn, mInputColumn);

        Callback<TableColumn<Map, String>, TableCell<Map, String>> cellFactoryForMap = TextFieldTableCell.forTableColumn(new StringConverter<String>() {
            @Override
            public String toString(String object) {
                return object;
            }

            @Override
            public String fromString(String string) {
                return string;
            }
        });

        mOutputColumn.setCellFactory(cellFactoryForMap);
        mTitleColumn.setCellFactory(cellFactoryForMap);
        mInputColumn.setCellFactory(cellFactoryForMap);

        mNewConfigButton = new Button("Add New Config");
        mDeleteSelected = new Button("Delete Seclected");

        mGridPane = new GridPane();
        mGridPane.setAlignment(Pos.TOP_LEFT);
        mGridPane.setHgap(10);
        mGridPane.setVgap(10);
        mGridPane.setPadding(new Insets(10,0,0,10));

//        mGridPane.setGridLinesVisible(true);
        mGridPane.add(mNewConfigButton, 1,1);
        mGridPane.add(mDeleteSelected, 0, 1);
        GridPane.setHalignment(mNewConfigButton, HPos.RIGHT);
        mGridPane.add(mDataTable, 0, 0,2,1);

        Scene scene = new Scene(mGridPane, WINDOW_WIDTH, WINDOW_HEIGHT);

        mStage.setScene(scene);
        mStage.setResizable(false);
        mStage.setTitle("Config Manager");
    }

    /**
     * Assign Actions to buttons
     */
    private void eventHandler() {
        /*
        Create a new config at the click of a button
         */
        mNewConfigButton.setOnAction(event -> {
            ConfigSetupGUI newConfig = new ConfigSetupGUI();
            try {
                newConfig.run(mConfigManager, mStage);
            } catch (Exception e) {
                try {
                    throw e;
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            redisplayData();
        });

        mDeleteSelected.setOnAction(event -> {
            System.out.println(mDataTable.getSelectionModel().getSelectedItem().toString());
//            mAllData.remove(mDataTable.getSelectionModel().getSelectedItem());
            if (mDataTable.getSelectionModel().getSelectedItem() instanceof Map) {
                System.out.println(((Map) mDataTable.getSelectionModel().getSelectedItem()).get(COLUMN_KEYS[1]));
                String filename = ((Map) mDataTable.getSelectionModel().getSelectedItem()).get(COLUMN_KEYS[1]).toString();
                filename.replaceAll(",", "-");
                mConfigManager.removeConfig(filename);
            }
            redisplayData();
        });

        /*
        When user edits and commits a change to the Output Column
         */
        mOutputColumn.setOnEditCommit(event -> {
            event.getRowValue().put(COLUMN_KEYS[0], event.getNewValue());
        });

        /*
        When user edits and commits a change to the Title Column
         */
        mTitleColumn.setOnEditCommit(event -> {
            String old = event.getRowValue().get(COLUMN_KEYS[1]).toString();
            // Update Row Data.
            if (event.getNewValue().contains("-"))
                event.getRowValue().put(COLUMN_KEYS[1], event.getNewValue().replaceAll("-",","));
            else
                event.getRowValue().put(COLUMN_KEYS[1], event.getNewValue());
            // Update the ConfigManager map and have it delete the old Config File.
            event.getTableView().refresh();
            try {
                mConfigManager.updateKey(old.replaceAll(",", "-"),
                        event.getNewValue().replaceAll(",", "-"));
            } catch (Exception e) {
                ErrorMessagesKt.exception(e);
            }
        });

        /*
        When user wants to change the file Input
         */
        mInputColumn.setOnEditStart(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose new Input File...");
            File file = fileChooser.showOpenDialog(mStage);
            if (file != null)
                event.getRowValue().put(COLUMN_KEYS[2], file.getAbsolutePath());

        });

        /*
        When the user closes this window
         */
        mStage.setOnCloseRequest(event -> {
            try {
                mConfigManager.updateData(mAllData, COLUMN_KEYS);
            } catch (Exception e) {
                ErrorMessagesKt.exception(e);
            }
            try {
                mConfigManager.reloadConfig();
            } catch (Exception e) { ErrorMessagesKt.exception(e); }
        });
    }

    /**
     * Redisplay data in the table view
     */
     private void redisplayData() {
        try {
            mConfigManager.reloadConfig();
        } catch (Exception e) {
            ErrorMessagesKt.exception(e);
        }
        mAllData.clear();
        mAllData.addAll(mConfigManager.getObservableMap(COLUMN_KEYS));
    }

    /**
     * Parse the UCR Excel File
     * @param in Excel file to be parsed
     * @param desktop The Desktop to open the file on
     * @throws Exception File was null or File was incorrect format
     */
    public void parseUCR(File in, Desktop desktop) throws Exception {
        this.mConfigManager.parseUCR(in, desktop);
    }

    /**
     * Run this Stage of the program
     * @param parentStage Assign this stage a parent stage
     * @throws Exception Either Config files are missing or there was some mishap with data edits
     */
    public void run(Stage parentStage) throws Exception {
        eventHandler();
        mConfigManager.reloadConfig();
        if (mStage.getOwner() == null)
            mStage.initOwner(parentStage);
        mStage.getIcons().add(new Image("file:dependencies/img.png"));
        mStage.show();
    }
}