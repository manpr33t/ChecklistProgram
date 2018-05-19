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

package com.src.gui

import javafx.scene.control.Alert
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.stage.Screen
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Display an Error popup window with the specified string as the content
 * @param s Content of the Error popup window
 */
fun error(s: String) {
    val window = Alert(Alert.AlertType.ERROR)
    window.contentText = s
    window.showAndWait()
}

fun message(s: String) {
    val window = Alert(Alert.AlertType.INFORMATION)
}

/**
 * Display a Warning popup window with the specified string as the content
 * @param s Content of the Warning popup window
 */
fun warning(s: String) {
    val window = Alert(Alert.AlertType.WARNING)
    window.contentText = s
    window.showAndWait()
}

/**
 * Display the stacktrace and localized message of an exception in an Error window
 * @param e Exception to display data from
 */
fun exception(e: Exception) {
    val window = Alert(Alert.AlertType.ERROR)
    window.title = e.message
    window.headerText = e.localizedMessage
    window.contentText = e.toString()

    val sw = StringWriter()
    val pw = PrintWriter(sw)
    e.printStackTrace(pw)
    val label = Label("Exception Stacktrace")

    val textArea = TextArea(sw.toString())
    textArea.isEditable = false
    textArea.isWrapText = true

    textArea.maxWidth = Screen.getPrimary().visualBounds.width
    textArea.maxHeight = Screen.getPrimary().visualBounds.height
    GridPane.setVgrow(textArea, Priority.ALWAYS)
    GridPane.setHgrow(textArea, Priority.ALWAYS)

    val pane = GridPane()
    pane.maxWidth = Double.MAX_VALUE
    pane.add(label, 0,0)
    pane.add(textArea, 0, 1)

    window.dialogPane.expandableContent = pane

    window.showAndWait()
}
