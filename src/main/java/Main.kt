import javafx.application.Application
import javafx.stage.Stage
import tornadofx.*
import util.HookThread
import windows.WindowMain
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

class Main : App() {

    companion object Main {
        val path: Path = Paths.get(System.getProperty("user.home") + "/yenon/LuaModder")
        lateinit var launchParameters: Application.Parameters
    }

    override val primaryView = WindowMain::class

    override fun start(stage: Stage) {
        HookThread.start()
        Main.launchParameters = parameters
        super.start(stage)
    }
}

fun main(args: Array<String>) {
    System.setProperty("javafx.allowTransparentStage", "true")
    println(File(".").absolutePath)
    Application.launch(Main::class.java, *args)
}