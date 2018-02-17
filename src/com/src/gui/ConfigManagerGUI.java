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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.applet.Applet;

/**
 * @author Manpreet Singh (2854787)
 *         FedEx Smartport SEWA/5983
 */
public class ConfigManagerGUI {

    private final int WINDOW_HEIGHT = 215;
    private final int WINDOW_WIDTH = 325;

    private ComboBox<String> mDestComboBox;

    private TableView<String> mDataTable;

    private Stage mStage;

    private Scene mScene;

    private GridPane mGridPane;

    public ConfigManagerGUI(ConfigManager configManager) throws Exception {
        mDestComboBox = new ComboBox<>();
        mDestComboBox.getItems().addAll(configManager.getKeys());

        mDataTable = new TableView<>();

        mGridPane = new GridPane();
        mGridPane.setAlignment(Pos.TOP_LEFT);
        mGridPane.setHgap(10);
        mGridPane.setVgap(10);
        mGridPane.setPadding(new Insets(5,5,5,5));

        mGridPane.add(mDestComboBox, 0, 0);

    }

    public void eventHandler() {

    }

    public void run() {
        eventHandler();
        mStage.show();
    }
}
