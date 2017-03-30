package windows

import Main
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Parent
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.text.Font
import org.jnativehook.keyboard.NativeKeyEvent
import tornadofx.*
import util.DisabledSelectionModel
import util.HookThread
import java.nio.file.Files
import java.nio.file.Paths


class WindowMain : View() {

    val fileList: ObservableList<Script> = FXCollections.observableArrayList()

    private fun refreshList() {
        fileList.forEach(Script::stop)
        fileList.clear()
        Files.list(Main.path.resolve("scripts")).forEach {
            if (Files.isDirectory(it)) {
                fileList.add(Script(it))
            }
        }
    }

    override val root: Parent = vbox {
        Font.loadFont(WindowMain::class.java.getResource("/awesome.ttf").toExternalForm(), 12.0)
        menubar {
            menu("About") {
                menuitem("API documentation")
                menuitem("About this program")
            }
        }
        listview(fileList) {
            selectionModel = DisabledSelectionModel<Script>()
            cellCache {
                gridpane {
                    row {
                        imageview {
                            image = it.image
                            fitHeight = 64.0
                            prefHeight = 64.0
                        }
                        label {
                            gridpaneConstraints {
                                vhGrow = Priority.ALWAYS
                            }
                            text = it.name
                        }
                        togglebutton {
                            font = Font.font("FontAwesome", 16.0)
                            text = "\uf04b"
                            textFill = Color.GREEN
                            selectedProperty().addListener({ _, _, newValue ->
                                if (newValue) {
                                    text = "\uf04d"
                                    textFill = Color.RED
                                    it.execute()
                                } else {
                                    text = "\uf04b"
                                    textFill = Color.GREEN
                                    it.stop()
                                }
                            })
                        }
                    }
                }
            }
        }
    }

    init {
        Files.createDirectories(Main.path.resolve("scripts"))
        Files.createDirectories(Main.path.resolve("plugins"))

        if (Main.launchParameters.named["debug"] == "true") {
            find(WindowDebug::class).openWindow()
            if (Files.isRegularFile(Paths.get("ScreenCap.jar"))) {
                HookThread.keyDownEventProperty.addListener({ _, _, newValue ->
                    if (newValue.keyCode == NativeKeyEvent.VC_PRINTSCREEN) {
                        Runtime.getRuntime().exec("java -jar ScreenCap.jar")
                    }
                })
            }
        }

        Platform.runLater({
            refreshList()
        })

        primaryStage.setOnCloseRequest {
            System.exit(1)
        }
        //TransparentView.openWindow()
    }
}