package net.manpreet_singh.updater

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.FileReader

class Updater {
    class Version(fileName: String) {
        val version = (JSONParser().parse(FileReader(fileName)) as JSONObject).get("version").toString()
        val version_major = version.split(".")[0]
        val version_minor = version.split(".")[1]
        val version_min = version.split(".")[2]
    }
    init {
        val v = Version("dependencies/version.json")
    }
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Updater()
//            Runtime.getRuntime().exec("cmd /c start \"\" Launch.vbs");
        }
    }
}