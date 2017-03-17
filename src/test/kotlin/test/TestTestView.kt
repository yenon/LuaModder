package test

import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import javafx.stage.StageStyle
import tornadofx.*

class TestTestMain : App() {
    override val primaryView = TestTestView::class
}

class TestTestView : View() {
    override val root: Parent = label {
        prefWidth = 600.0
        prefHeight = 400.0
        text = ""
        textFill = Color.RED
        background = Background(BackgroundFill(Color.rgb(192, 128, 64), CornerRadii.EMPTY, Insets.EMPTY))
        primaryStage.initStyle(StageStyle.TRANSPARENT)
        primaryStage.x = 0.0
        primaryStage.y = 0.0
    }
}