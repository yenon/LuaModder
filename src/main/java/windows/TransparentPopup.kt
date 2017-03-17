package windows

import javafx.scene.Scene
import javafx.stage.Popup
import tornadofx.*

/**
 * Created by basti on 09.03.2017.
 */
object TransparentPopup : Popup() {
    init {
        this.scene = Scene(label {
            text = "hi"
        })
    }
}