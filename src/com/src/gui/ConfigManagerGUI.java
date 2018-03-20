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
import com.src.config.ConfigParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Manpreet Singh (2854787)
 *         FedEx Smartport SEWA/5983
 */
public class ConfigManagerGUI {

    private final int WINDOW_HEIGHT = 215;
    private final int WINDOW_WIDTH = 325;

    private final String COLUMN_A_KEY = "property";
    private final String COLUMN_B_KEY = "value";

    private ComboBox<String>    mDestComboBox;

    private TableView   mDataTable;
    private TableColumn<Map, String> mConfigProperty;
    private TableColumn<Map, String> mConfigValue;

    ObservableList<Map> mAllData;

    private Button  mNewConfigButton;
    private Button  mLoadConfig;

    private Stage    mStage;
    private Scene    mScene;
    private GridPane mGridPane;

    private ConfigManager mConfigManager;

    public ConfigManagerGUI(ConfigManager configManager) throws Exception {
        mStage = new Stage();

        mConfigManager = configManager;

        mDestComboBox = new ComboBox<>();

        if (!mConfigManager.getKeys().isEmpty())
            mDestComboBox.getItems().addAll(mConfigManager.getKeys());

        mConfigProperty = new TableColumn<>("Property");
        mConfigValue = new TableColumn<>("Value");

        mConfigProperty.setCellValueFactory(new MapValueFactory<>(COLUMN_A_KEY));
        mConfigProperty.setMinWidth(130);
        mConfigValue.setCellValueFactory(new MapValueFactory<>(COLUMN_B_KEY));
        mConfigValue.setMinWidth(130);

        mAllData = FXCollections.observableArrayList();
        mDataTable = new TableView<>(mAllData);
        mDataTable.setEditable(false);
        mDataTable.getSelectionModel().setCellSelectionEnabled(true);
        mDataTable.getColumns().setAll(mConfigProperty, mConfigValue);

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
        mConfigProperty.setCellFactory(cellFactoryForMap);
        mConfigValue.setCellFactory(cellFactoryForMap);

        mNewConfigButton = new Button("Add New Config");
        mLoadConfig = new Button("Load");

        mGridPane = new GridPane();
        mGridPane.setAlignment(Pos.TOP_LEFT);
        mGridPane.setHgap(10);
        mGridPane.setVgap(10);
        mGridPane.setPadding(new Insets(5,5,5,5));

        mGridPane.add(mDestComboBox, 0, 0);
        mGridPane.add(mNewConfigButton, 2,0);
        mGridPane.add(mLoadConfig, 1, 0);

        final VBox vbox = new VBox();

        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(mDataTable);

        mGridPane.add(vbox, 0, 1,3,1);

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

        // When user clicks on choice
        mLoadConfig.setOnAction(event -> {
            try {
                if (mConfigManager.getKeys().contains(mDestComboBox.getValue()))
                    updateTableView(mDestComboBox.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private void updateTableView(String key) {
        mAllData.removeAll(mAllData);
        ConfigParser cp = mConfigManager.getValue(key);

        Map<String, String> dataRowOne = new HashMap<>();
        Map<String, String> dataRowTwo = new HashMap<>();
        Map<String, String> dataRowThree = new HashMap<>();

        dataRowOne.put(COLUMN_A_KEY, "InputFileName");
        dataRowOne.put(COLUMN_B_KEY, cp.getInputFileName());

        dataRowTwo.put(COLUMN_A_KEY, "OutputFileName");
        dataRowTwo.put(COLUMN_B_KEY, cp.getOutputFileName());

        dataRowThree.put(COLUMN_A_KEY, "Routes");
        dataRowThree.put(COLUMN_B_KEY, cp.isMultipleRoutes() ? Arrays.toString(cp.getMultipleRoute()) : cp.getTitle());

        mAllData.add(dataRowOne);
        mAllData.add(dataRowTwo);
        mAllData.add(dataRowThree);
    }

    public void parseUCR(File in, Desktop desktop) throws Exception {
        this.mConfigManager.parseUCR(in, desktop);
    }

    public void run() throws Exception {
        eventHandler();
        mStage.show();
    }
}
