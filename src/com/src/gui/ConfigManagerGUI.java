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
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.awt.*;
import java.io.File;
import java.util.Map;


/**
 * @author Manpreet Singh (2854787)
 *         FedEx Smartport SEWA/5983
 */
public class ConfigManagerGUI {

    private final int WINDOW_HEIGHT = 325;
    private final int WINDOW_WIDTH = 465;

    private static final String[] COLUMN_KEYS = {"output", "title", "input" };

    private TableView   mDataTable;
    private TableColumn<Map, String> mOutputColumn;
    private TableColumn<Map, String> mTitleColumn;
    private TableColumn<Map, String> mInputColumn;

    private ObservableList<Map> mAllData;

    private Button  mNewConfigButton;

    private Stage    mStage;
    private Scene    mScene;
    private GridPane mGridPane;

    private ConfigManager mConfigManager;

    public ConfigManagerGUI(ConfigManager configManager) throws Exception {
        mStage = new Stage();
        mStage.initModality(Modality.WINDOW_MODAL);

        mConfigManager = configManager;

        mOutputColumn = new TableColumn<>("Output");
        mTitleColumn = new TableColumn<>("Sort Groups");
        mInputColumn = new TableColumn<>("Input");

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

        Callback<TableColumn<Map, String>, TableCell<Map, String>>
                cellFactoryForMap = p -> new TextFieldTableCell(new StringConverter() {
            @Override
            public String toString(Object t) {
                return t.toString();
            }
            @Override
            public Object fromString(String string) {
                return string;
            }
        });

        mOutputColumn.setCellFactory(cellFactoryForMap);
        mTitleColumn.setCellFactory(cellFactoryForMap);
        mInputColumn.setCellFactory(cellFactoryForMap);

        mNewConfigButton = new Button("Add New Config");

        mGridPane = new GridPane();
        mGridPane.setAlignment(Pos.TOP_LEFT);
        mGridPane.setHgap(10);
        mGridPane.setVgap(10);
        mGridPane.setPadding(new Insets(10,0,0,10));

        mGridPane.setGridLinesVisible(true);
        mGridPane.add(mNewConfigButton, 1,1);
        GridPane.setHalignment(mNewConfigButton, HPos.RIGHT);
        mGridPane.add(mDataTable, 0, 0,2,1);

        mScene = new Scene(mGridPane, WINDOW_WIDTH, WINDOW_HEIGHT);

        mStage.setScene(mScene);
        mStage.setResizable(false);
        mStage.setTitle("Config Manager");
    }

    private void eventHandler() throws Exception {
        mNewConfigButton.setOnAction(event -> {
            ConfigSetupGUI newConfig = new ConfigSetupGUI();
            try {
                newConfig.run(mConfigManager);
            } catch (Exception e) {
                try {
                    throw e;
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        mOutputColumn.setOnEditCommit(event -> {
            // TODO Update config manager accordingly to the changes
            event.getRowValue().put(COLUMN_KEYS[0], event.getNewValue());
        });

        mTitleColumn.setOnEditCommit(event -> {
            // Update Row Data.
            event.getRowValue().put(COLUMN_KEYS[1], event.getNewValue());
            // Update the ConfigManager map and have it delete the old Config File.
            mConfigManager.updateKey(event.getOldValue().replaceAll(",", "-"),
                    event.getNewValue().replaceAll(",", "-"));
        });

        mStage.setOnCloseRequest(event -> {
            for (Map m : mAllData)
                System.out.println(m);
        });

        mInputColumn.setOnEditStart(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose new Input File...");
            File file = fileChooser.showOpenDialog(mStage);
            if (file != null)
                event.getRowValue().put(COLUMN_KEYS[2], file.getAbsolutePath());
            System.out.println(event.getRowValue());
            System.out.println("Stuff happened here.");
        });
    }

    public void parseUCR(File in, Desktop desktop) throws Exception {
        this.mConfigManager.parseUCR(in, desktop);
    }

    public void run(Stage parentStage) throws Exception {
        eventHandler();
        if (mStage.getOwner() == null)
            mStage.initOwner(parentStage);
        mStage.show();
    }
}