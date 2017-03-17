import javafx.application.Application
import javafx.stage.Stage
import tornadofx.*
import util.HookThread
import windows.WindowMain
import java.io.File

/**
 * Created by yenon on 2/28/17.
 */

class Main : App() {
    companion object Main {
        lateinit var launchParameters: Application.Parameters
    }

    override val primaryView = WindowMain::class

    override fun start(stage: Stage) {
        Main.launchParameters = parameters
        super.start(stage)
    }
}

fun main(args: Array<String>) {
    System.setProperty("javafx.allowTransparentStage", "true")
    println(File(".").absolutePath)
    HookThread.start()
    Application.launch(Main::class.java, *args)
}