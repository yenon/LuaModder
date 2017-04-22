package windows

import Main
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.FileChooser
import org.jnativehook.keyboard.NativeKeyEvent
import tornadofx.*
import util.DisabledSelectionModel
import util.HookThread
import java.awt.Desktop
import java.io.File
import java.net.URI
import java.nio.file.FileVisitOption
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Comparator
import java.util.function.Consumer
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.collections.ArrayList


class WindowMain : View() {

    val fileList: ObservableList<Script> = FXCollections.observableArrayList()

    private fun refreshList() {
        val remove: ArrayList<Script> = ArrayList()
        fileList.forEach {
            if (!it.isAvailable()) {
                remove.add(it)
            } else {
                it.refreshImage()
            }
        }
        remove.forEach({
            fileList.remove(it)
        })
        Files.list(Main.path.resolve("scripts")).forEach { file ->
            var found = false
            fileList.forEach {
                if (Files.isSameFile(it.path, file)) {
                    found = true
                }
            }

            if (Files.isDirectory(file) && !found) {
                fileList.add(Script(file))
            }
        }
    }

    fun deleteDir(path: Path) {
        if (Files.isDirectory(path)) {
            Files.walk(path, FileVisitOption.FOLLOW_LINKS)
                    .sorted(Comparator.reverseOrder<Path>())
                    .map<File>(Path::toFile)
                    .peek(Consumer<File>(::println))
                    .forEach({ it.delete() })
        }
    }

    override val root: Parent = vbox {
        Font.loadFont(WindowMain::class.java.getResource("/awesome.ttf").toExternalForm(), 12.0)
        menubar {
            menu("File") {
                menuitem("Add Mod .zip") {
                    println("Add Mod")
                    val fileChooser = FileChooser()
                    fileChooser.title = "Open Mod"
                    fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("Mod", "*.zip"))
                    val zipFile: File? = fileChooser.showOpenDialog(primaryStage)
                    if (zipFile != null) {
                        val modName: String = zipFile.name.replace(".zip", "")
                        val modPath: Path = Main.path.resolve("scripts/$modName")
                        if (Files.isDirectory(modPath)) {
                            var script: Script? = null
                            fileList.forEach {
                                if (it.path == modPath) {
                                    script = it
                                }
                            }
                            if (script != null) {
                                script!!.stop()
                                fileList.remove(script)
                                System.gc()
                                Thread.sleep(500)
                                deleteDir(script!!.path)
                                System.gc()
                            } else {
                                val alert: Alert = Alert(Alert.AlertType.ERROR)
                                alert.title = "Error removing mod"
                                alert.headerText = "Couldn't remove mod directory"
                                alert.contentText = "Please remove the directory with the same name manually."
                                alert.showAndWait()
                                Desktop.getDesktop().browse(URI(modPath.toString()))
                                return@menuitem
                            }
                        }
                        Files.createDirectories(modPath)
                        val zipInput: ZipInputStream = ZipInputStream(zipFile.inputStream())
                        var zipEntry: ZipEntry? = zipInput.nextEntry

                        while (zipEntry != null) {
                            val filePath: Path = modPath.resolve(zipEntry.name)
                            Files.createDirectories(modPath.parent)
                            zipInput.copyTo(Files.newOutputStream(filePath))
                            zipEntry = zipInput.nextEntry
                        }
                        refreshList()
                    }
                }
                menuitem("Reload") {
                    refreshList()
                }
            }
            menu("About") {
                menuitem("API documentation") {
                    Desktop.getDesktop().browse(URI("https://github.com/yenon/luamodder/wiki"))
                }
                menuitem("About this program") {
                    Desktop.getDesktop().browse(URI("https://github.com/yenon/luamodder"))
                }
            }
        }
        listview(fileList) {
            selectionModel = DisabledSelectionModel<Script>()
            cellCache {
                gridpane {
                    row {
                        imageview {
                            imageProperty().bind(it.image)
                            fitHeight = 64.0
                            fitWidth = 64.0
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