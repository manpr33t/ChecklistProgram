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
import javafx.stage.FileChooser
import javafx.stage.Stage
import net.manpreet_singh.config.UCRParser
import java.io.File
import java.lang.Exception
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.HashMap
import kotlin.system.exitProcess

class MultipleFiles {

    private var mFiles: MutableList<File>? = null
    private var mFileChooser: FileChooser? = null
    private var mUCRParser: UCRParser? = null

    private var mDataMap: MutableMap<String, MutableSet<String>?>? = null

    init {
        mFileChooser = FileChooser()
        mUCRParser = UCRParser()
        mDataMap = HashMap()
    }

    /**
     * Ask the user for some Files.
     * @param stage Stage Object to use when opening the file chooser
     */
    private fun askForFiles(stage: Stage) : MutableList<File>? {
        val files = mFileChooser!!.showOpenMultipleDialog(stage)
        var badFiles: Stack<File>? = null

        // Check each of the files to make sure they're all spreadsheets.
        files.forEach{ it ->
            if (!it.extension.equals("xls") || !it.canExecute()) {
                if (badFiles == null)
                    badFiles = Stack()
                badFiles!!.add(it)
            }
        }

        // If we had any bad files, throw an exception.
        if (badFiles != null && badFiles!!.size > 0) {
            val fileNames = StringBuilder()
            badFiles!!.forEach { it ->
                fileNames.append("${it.name} ")
            }
            throw Exception("Unsupported files: $fileNames")
        }

        return files
    }

    /**
     * Run this program
     * @param stage Stage Object to use when running this program.
     */
    fun run(stage: Stage) : MutableMap<String, MutableSet<String>?>? {
        try {
            mFiles = askForFiles(stage)
            println("Success")
        } catch (e: Exception) {
            exception(e)
        }

        // Pull data from all the files
        mFiles!!.forEach { it ->
            mUCRParser!!.readDataFromFile(it)
            mUCRParser!!.getDataMap()!!.keys.forEach{ key ->
                if (!mDataMap!!.containsKey(key))
                    mDataMap!![key] = mUCRParser!!.getDataMap()!![key]
                else
                    mDataMap!![key]!!.addAll(mUCRParser!!.getDataMap()!![key]!!)
            }
            mUCRParser!!.clearData()
        }
        return this.mDataMap
    }
}

class Test : Application() {
    private val multipleFiles = MultipleFiles()
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