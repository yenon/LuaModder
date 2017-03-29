package lua.javafx

import javafx.scene.control.Control
import util.setFXProcedure

abstract class LuaControl<out T : Control> : LuaNode<T>() {

    init {
        setFXProcedure("setStyle", {
            e.style = it.checkjstring(1)
        })

    }
}