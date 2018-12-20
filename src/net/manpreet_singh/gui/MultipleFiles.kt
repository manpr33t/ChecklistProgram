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


package net.manpreet_singh.gui

import javafx.application.Application
import javafx.scene.control.Button
import javafx.scene.layout.GridPane
import javafx.stage.FileChooser
import javafx.stage.Stage
import net.manpreet_singh.config.ConfigManager
import java.io.File

class MultipleFiles {


    private var mFileSet: MutableSet<File>? = null
    private var mFileChooser: FileChooser? = null

    init {
        mFileSet = HashSet()
        mFileChooser = FileChooser()
    }

    private fun askForFiles(stage: Stage) : MutableSet<File> {
        val files = mFileChooser!!.showOpenMultipleDialog(stage)
        return HashSet()
    }

    fun run(stage: Stage) {
        mFileSet!!.addAll(mFileChooser!!.showOpenMultipleDialog(stage))
        mFileSet!!.forEach { it ->
            if (it.extension.equals("xls"))
                println("we got one boys.")
            else
                println("oh no, we got trouble!")
        }
    }
}

class Test : Application() {
    val multipleFiles = MultipleFiles()
    override fun start(primaryStage: Stage?) {
        multipleFiles.run(primaryStage!!)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Test::class.java)
        }
    }
}