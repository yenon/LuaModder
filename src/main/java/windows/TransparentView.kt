package windows

import javafx.application.Platform
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Background
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import tornadofx.*
import util.HookThread
import java.util.concurrent.FutureTask

class TransparentView : View() {

    private lateinit var stage: Stage

    companion object Transparent {
        fun open(x: Int, y: Int, width: Int, height: Int): TransparentView {
            val task: FutureTask<TransparentView> = FutureTask<TransparentView> {
                val stage: Stage = Stage(StageStyle.TRANSPARENT)
                stage.initModality(Modality.NONE)
                val view: TransparentView = tornadofx.find(TransparentView::class)
                view.setStage(stage)
                val scene: Scene = Scene(view.root)
                scene.fill = null
                stage.scene = scene
                stage.isAlwaysOnTop = true
                stage.x = x.toDouble()
                stage.y = y.toDouble()
                stage.width = width.toDouble()
                stage.height = height.toDouble()
                stage.show()
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

    fun show() {
        stage.show()
    }

    fun setStage(stage: Stage) {
        this.stage = stage
        HookThread.mouseUpEventProperty.addListener({ _, _, _ ->
            if (!stage.isShowing) {
                Platform.runLater({
                    stage.show()
                })
            }
        })
    }
}