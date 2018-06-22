package net.manpreet_singh.updater

import javafx.application.Application
import net.manpreet_singh.gui.MainGUI

class Updater {
    init {
    }
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Application.launch(MainGUI::class.java)
        }
    }
}