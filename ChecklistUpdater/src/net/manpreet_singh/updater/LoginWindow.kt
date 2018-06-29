package net.manpreet_singh.updater

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

class LoginWindow() : Application() {
    init {

    }
    override fun start(primaryStage: Stage?) {
        val root = FXMLLoader.load(javaClass.getResource("LoginWindow.fxml")) as Parent
        val scene = Scene(root, 300.0,275.0)
        primaryStage!!.title = "Checklist Program Log In"
        primaryStage.scene = scene
        primaryStage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(LoginWindow::class.java)
        }
    }
}