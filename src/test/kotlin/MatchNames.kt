package test.kotlin

import javafx.application.Application
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import kotlin.system.exitProcess

class MatchNames : Application() {

    private var mFileChooser: FileChooser? = null
    private var dataMap: MutableMap<String, Array<String>>? = null
    private var mLayoutFile: FileReader?  = null

    init {
        dataMap = HashMap()
        mFileChooser = FileChooser()
    }

    override fun start(primaryStage: Stage?) {
        mLayoutFile = FileReader("temp/ListWithNamesShogun.csv")
        val bf = BufferedReader(mLayoutFile)
        val iterator = bf.lineSequence().iterator()

        iterator.forEach { it ->
            val s = it.split(",")
            dataMap!![s[1]] = arrayOf(s[0], s[2])
        }
        println(dataMap)

        readChecklistFiles(primaryStage)
        primaryStage!!.close()
        exitProcess(0)
    }

    fun readChecklistFiles(stage: Stage?) {
        val checklistFiles = mFileChooser!!.showOpenMultipleDialog(stage)
        checklistFiles.forEach{
            val output = File("temp/${it.name}-Master.csv").printWriter()
            val iterator = BufferedReader(FileReader(it.absoluteFile)).lineSequence().iterator()
            iterator.forEach { s ->
                val line  = s.split(",")
                if (dataMap!!.keys.contains(line[1])) {
                    val loc = if (dataMap!![line[1]]!![1].length > 3) dataMap!![line[1]]!![1].substring(2,5) else dataMap!![line[1]]!![1]
                    output.println("${dataMap!![line[1]]!![0]},${line[1]},$loc")
                } else {
                    output.println(s)
                }
            }
            output.close()
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(MatchNames::class.java, *args)
        }
    }
}

