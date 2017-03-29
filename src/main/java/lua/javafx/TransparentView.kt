package lua.javafx

import com.sun.glass.ui.Window
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinUser.GWL_STYLE
import javafx.application.Platform
import javafx.scene.Cursor
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Background
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import tornadofx.*
import java.util.concurrent.FutureTask

class TransparentView : View() {

    private lateinit var stage: Stage

    companion object Transparent {
        fun open(x: Int, y: Int, width: Int, height: Int): TransparentView {
            val task: FutureTask<TransparentView> = FutureTask {
                val stage: Stage = Stage(StageStyle.TRANSPARENT)
                stage.initModality(Modality.NONE)
                //val view: TransparentView = tornadofx.find(TransparentView::class)
                val view: TransparentView = TransparentView()
                view.setStage(stage)
                val scene: Scene = Scene(view.root)
                scene.cursor = Cursor.NONE
                scene.fill = null

                stage.scene = scene

                stage.x = x.toDouble()
                stage.y = y.toDouble()
                stage.width = width.toDouble()
                stage.height = height.toDouble()
                stage.title = "KillMePls"
                stage.isAlwaysOnTop = true

                stage.show()

                Platform.runLater({
                    Window.getWindows().forEach {
                        if (it.title == "KillMePls") {
                            val lhwnd = it.nativeWindow
                            val lpVoid = Pointer(lhwnd)
                            val hwnd = HWND(lpVoid)
                            val user32 = User32.INSTANCE
                            val oldStyle = user32.GetWindowLong(hwnd, GWL_STYLE)
                            println(Integer.toBinaryString(oldStyle))
                            val newStyle: Int = 40
                            println(Integer.toBinaryString(newStyle))
                            user32.SetWindowLong(hwnd, GWL_STYLE, oldStyle)
                        }
                    }
                })

                return@FutureTask view
            }
            Platform.runLater(task)
            return task.get()
        }
    }

    override val root: Parent = anchorpane {
        background = Background.EMPTY
    }

    fun getRoot(): AnchorPane {
        return root as AnchorPane
    }

    fun hide() {
        stage.hide()
    }

    fun isShowing(): Boolean {
        return stage.isShowing
    }

    fun show() {
        stage.show()
    }

    fun setStage(stage: Stage) {
        this.stage = stage
    }
}