package lua.javafx

import javafx.scene.control.Label
import javafx.scene.paint.Color
import javafx.scene.text.Font
import util.setFXProcedure

class LuaLabel : LuaControl<Label>() {
    override val e: Label = Label()

    init {
        setFXProcedure("setText", {
            e.text = it.checkjstring(1)
        })
        setFXProcedure("setTextColor", {
            e.textFill = Color.rgb(it.checkint(1), it.checkint(2), it.checkint(3))
        })
        setFXProcedure("setFont", {
            e.font = Font.font(it.checkjstring(1), e.font.size)
        })
        setFXProcedure("setFontSize", {
            e.font = Font.font(e.font.family, it.checkdouble(1))
        })
    }
}
